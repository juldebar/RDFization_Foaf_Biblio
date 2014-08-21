package com.ird.enrichissement_eco;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.DC;
import static com.ird.enrichissement_eco.Utilitaires.identifier_model;
import static com.ird.enrichissement_eco.Utilitaires.normalize;
import static com.ird.enrichissement_eco.Utilitaires.remplissage_endpoint;
import static com.ird.enrichissement_eco.Utilitaires.retirer_maj;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

/**
 * Utilise Lucene pour tag les articles avec des mots clés. Les annontations se
 * font sur deux modèles RDF : ajout de DC:subject pour indiquer un mot clé dans
 * un article et ajout de DC:isPrimaryTopicOf pour ajouter un article à un mot
 * clé. Les mots clés à chercher sont les skos:altLabel et skos:prefLabel du
 * SPARQL Endpoint Dès qu'un mot clé est vu, il est tag, même si une notion de
 * score pourra être utilisée plus tard (le score est tout de même calculé)
 *
 * @author jimmy
 */
public class enrichissement_lucene {

    /**
     * fonction principale, permet d'être appelé par le gui
     *
     * @param refBiblio adresse du fichier de biblio
     * @param agents adresse fichier agents (peut etre vide)
     * @param ecosys adresse du fichier ecosystems (peut etre vide)
     * @param ecodef adresse du fichier ecosystems_def (peut etre vide)
     * @param bl adresse de la liste de mot à ne pas annoter (peut etre vide)
     * @param service service endpoint
     * @throws IOException
     */
    public static void enrichissement(String refBiblio, String agents, String ecosys, String ecodef, String bl, String service) throws IOException {
        String path = System.getProperty("user.dir");
        // Les modèles dans lesquels on importe les triplets RDF des fichiers du dessus
        //MIX LECTURE / ECRITURE
        Model ModelBiblio_ext = ModelFactory.createDefaultModel();
        Model ModelAgents_ext = ModelFactory.createDefaultModel();
        Model ModelEcosys_ext = ModelFactory.createDefaultModel();
        Model ModelEcodef_ext = ModelFactory.createDefaultModel();

        //On alimente les modèles créés. Load bibliographic ref into the model ModelBiblio
        ModelBiblio_ext.read("file:"+refBiblio);
        if (!agents.equals("")) {
            ModelAgents_ext.read("file:"+agents);
        }
        if (!ecosys.equals("")) {
            ModelEcosys_ext.read("file:"+ecosys);
        }
        if (!ecodef.equals("")) {
            ModelEcodef_ext.read("file:"+ecodef);
        }
        //Set out
        PrintStream originalOut = new PrintStream(System.out);
        File result_query = new File(path + "/outputs/trace_enrichissement.txt");
        PrintStream printStream = new PrintStream(result_query);
        System.setOut(printStream);

        //Rempli les maps faisant les correspondaces ressources <-> labels
        HashMap<String, Resource> map_StoR = new HashMap<>();
        HashMap<Resource, LinkedList<String>> map_RtoS = new HashMap<>();
        if (bl.equals("")) {
            remplissage_endpoint(map_RtoS, map_StoR, service);
        } else {
            remplissage_endpoint(map_RtoS, map_StoR, bl, service);
        }
        //Set Lucene
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_9);
        // Store the index in memory:
        Directory directory = new RAMDirectory();
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_9, analyzer);
        IndexWriter iwriter = new IndexWriter(directory, config);

        Resource subject;
        RDFNode object;
        ResIterator iter_subject;
        NodeIterator iter_object;
        String titre = "";
        String description = "";
        String txt;
        String base_uri = "http://www.ecoscope.org/ontologies/resources/biblioUMRpublication/"; //Base de l'uri utilisés pour chaque publication
        String uri = "";
        LinkedList<String> list_uri = new LinkedList<>();
        iter_subject = ModelBiblio_ext.listSubjects(); //Parcours de tous les sujets d'Ecosys
        while (iter_subject.hasNext()) {
            subject = iter_subject.nextResource();
            iter_object = ModelBiblio_ext.listObjectsOfProperty(subject, DC.title); //On récupère le titre de la ressource
            while (iter_object.hasNext()) {
                object = iter_object.nextNode();
                titre = object.toString();
                uri = base_uri + normalize(titre); //On en profite pour générer son URI
                list_uri.add(uri);
            }
            iter_object = ModelBiblio_ext.listObjectsOfProperty(subject, DC.description); //Même travail avec l'abstract
            while (iter_object.hasNext()) {
                object = iter_object.nextNode();
                description = object.toString();
            }
            txt = titre + "\n" + description;
            Document doc = new Document();
            doc.add(new Field(uri, txt, TextField.TYPE_STORED)); //La subtilité : on crée autant de document qu'il existe d'article
            //On utilise l'URI de l'article comme nom de champs et on indexe son titre et sa description
            iwriter.addDocument(doc);
        }
        iwriter.close();

        // Now search the index:
        DirectoryReader ireader = DirectoryReader.open(directory);
        IndexSearcher isearcher = new IndexSearcher(ireader);

        String mot;
        Resource r;
        int cpt_mot = 0;
        int cpt_tag;
        int nb_tags = 0;
        String model;
        Resource article_eco;
        Resource article_biblio;
        String[] tokens;
        for (Map.Entry<String, Resource> entry : map_StoR.entrySet()) { //Pour chaque label du Endpoint
            cpt_tag = 0;
            mot = entry.getKey(); //On récupère le label
            r = entry.getValue(); //Et la ressource correspondante
            model = identifier_model(r.getURI());  //On identifie le model dont il provient
            ++cpt_mot;
            System.out.println(cpt_mot + ") Recherche du mot : " + mot + " (uri : " + r + ")");

            //Sensible à la casse alors on met tout en lowercase (pour coller avec l'index)
            mot = retirer_maj(mot);

            //Transforme mot en token afin de faire une PhraseQuery => Permet de chercher une phrase complète
            tokens = mot.split(" ");
            for (String s : list_uri) {
                PhraseQuery query = new PhraseQuery();
                query.setSlop(1); //Les mots se suivent 
                for (String token : tokens) {
                    query.add(new Term(s, token));
                }
                ScoreDoc[] hits = isearcher.search(query, null, 1000).scoreDocs; //On récupère les résultats, ie. les articles qui ont ce label
                for (ScoreDoc hit : hits) {
                    ++cpt_tag;
                    ++nb_tags;
                    Document hitDoc = isearcher.doc(hit.doc);
                    System.out.println(cpt_tag + "/" + nb_tags + ": <" + s + "> : SCORE = " + hit.score);
                    System.out.println("Doc : " + hitDoc.get(s) + "\n");

                    article_biblio = ModelBiblio_ext.createResource(s);
                    if (model.equals("ecosystems")) {
                        article_eco = ModelEcosys_ext.createResource(r.getURI());
                        article_biblio.addProperty(DC.subject, article_eco);
                        article_eco.addProperty(FOAF.isPrimaryTopicOf, article_biblio);
                    }
                    if (model.equals("ecosystems_def")) {
                        article_eco = ModelEcodef_ext.createResource(r.getURI());
                        article_biblio.addProperty(DC.subject, article_eco);
                        article_eco.addProperty(FOAF.isPrimaryTopicOf, article_biblio);
                    }
                    if (model.equals("agents")) {
                        article_eco = ModelAgents_ext.createResource(r.getURI());
                        article_biblio.addProperty(DC.subject, article_eco);
                        article_eco.addProperty(FOAF.isPrimaryTopicOf, article_biblio);
                    }
                }
            }
            System.out.println("\n");
        }
        System.out.println(nb_tags);
        ireader.close();
        directory.close();

        PrintWriter fluxSortie_bib = new PrintWriter(new FileOutputStream(refBiblio));
        PrintWriter fluxSortie_eco;
        PrintWriter fluxSortie_ecodef;
        PrintWriter fluxSortie_agents;
        if (ecosys.equals("")) {
            fluxSortie_eco = new PrintWriter(new FileOutputStream(path + "/outputs/Ecosystems_ext.rdf"));
        } else {
            fluxSortie_eco = new PrintWriter(new FileOutputStream(ecodef));
        }
        if (ecodef.equals("")) {
            fluxSortie_ecodef = new PrintWriter(new FileOutputStream(path + "/outputs/Ecosystems_def_ext.rdf"));
        } else {
            fluxSortie_ecodef = new PrintWriter(new FileOutputStream(ecodef));
        }
        if (agents.equals("")) {
            fluxSortie_agents = new PrintWriter(new FileOutputStream(path + "/outputs/Agents_ext.rdf"));
        } else {
            fluxSortie_agents = new PrintWriter(new FileOutputStream(agents));
        }
        ModelBiblio_ext.write(fluxSortie_bib);
        ModelEcosys_ext.write(fluxSortie_eco);
        ModelEcodef_ext.write(fluxSortie_ecodef);
        ModelAgents_ext.write(fluxSortie_agents);
        System.setOut(originalOut);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String path = System.getProperty("user.dir");
        //location of models : si on veut ajouter dedans on met le chemin. Sinon un Nom_ext.rdf est créé.
        String refBiblio = path + "/inputs/Biblio_ext.rdf"; //Model biblio
        String agents = path + "/inputs/Agents_ext.rdf"; //Model Agents
        String ecosys = "";
        String ecodef = "";
        String ne_pas_annoter = path + "/inputs/ne_pas_annoter.txt";
        String service = "http://ecoscopebc.mpl.ird.fr:8080/joseki/ecoscope";
        try {
            enrichissement(refBiblio, agents, ecosys, ecodef, ne_pas_annoter, service);
        } catch (IOException ex) {
            Logger.getLogger(enrichissement_lucene.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
