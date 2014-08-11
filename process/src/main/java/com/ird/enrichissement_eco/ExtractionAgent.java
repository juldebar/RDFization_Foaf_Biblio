package com.ird.enrichissement_eco;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.RDF;
import static com.ird.enrichissement_eco.Utilitaires.createURI_agent;
import static com.ird.enrichissement_eco.Utilitaires.normalize;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Fichier permettant de remplir un modèle RDF à partir d'un listing d'agents
 * sous format csv. Correspond au csv généré par l'export du tableur. Avec (1)-
 * nom  (2) - prénom    (3) - Pas utilisé    (4) - titre  (5) - Organisme
 *
 * @author jimmy
 */
public class ExtractionAgent {

    /**
     * Remplissage du modèle des agents à partir d'un fichier csv
     *
     * @param ModelAgent Modèle vide rempli lors de l'exécution
     * @param filePath adresse du fichier csv
     */
    static void extraction_agent(Model ModelAgent, String filePath) {

        String line;
        String[] values;
        LinkedList<String> our_values = new LinkedList<>();
        boolean found;
        int i;
        int size;
        Resource newArticle;
        String name;
        String surname;
        String title;
        String boss;
        String uri;
        String tmp;
        String tmp2;
        Resource rdfType_agent = ModelAgent.createResource("http://xmlns.com/foaf/0.1/Person");
        Resource IRD = ModelAgent.createResource("http://www.ecoscope.org/ontologies/resources/agents/organizationIRD");
        Resource IFREMER = ModelAgent.createResource("http://www.ecoscope.org/ontologies/resources/agents/organizationIFREMER");

        try {
            BufferedReader buff = new BufferedReader(new FileReader(filePath));

            try {
                line = buff.readLine(); //Saut de la première ligne (définition des colonnes)
                while ((line = buff.readLine()) != null) { //Lecture ligne à ligne
                    our_values.removeAll(our_values);
                    values = line.split(",");   //Tokenizer par le caractère ','
                    size = values.length;
                    i = 0;
                    while (i < size) {  //Parfois certain champs possède le caractère ','. Ces champs sont 
                        tmp = values[i]; //Caractérisé par l'ajout de ' " ' en début et fin. 
                        ++i;
                        if ((tmp.length() > 0) && (tmp.charAt(0) == '\"')) { //Ici lorsqu'un token commence par ' " '
                            found = false;                                 //On le groupe tant qu'on ne rencontre pas un token
                            while (!found && i < size) {                   //finissant par ' " '
                                tmp2 = values[i];
                                ++i;
                                found = tmp2.charAt(tmp2.length() - 1) == '\"';
                                tmp += "," + tmp2;
                            }
                        }
                        our_values.add(tmp);    //les vrais tokens sont donc stockés ici
                    }

                    boss = our_values.get(4);  //D'après les normes sur les positions des champs, on récupère
                    name = our_values.get(0);  //Les informations qui nous intérèsse
                    surname = our_values.get(1);
                    uri = createURI_agent(name, surname);

                    title = our_values.get(3);

                    newArticle = ModelAgent.createResource(uri);

                    newArticle.addProperty(RDF.type, rdfType_agent);
                    newArticle.addProperty(FOAF.family_name, name);
                    newArticle.addProperty(FOAF.firstName, surname);
                    newArticle.addProperty(FOAF.title, title);
                    if (boss.equals("IRD")) {
                        newArticle.addProperty(FOAF.fundedBy, IRD);  //Doit être sous forme d'URI => Attention, c'est très relatif au labo !
                    }
                    if (boss.equals("Ifremer")) {
                        newArticle.addProperty(FOAF.fundedBy, IFREMER);
                    }
                }
            } finally {
                buff.close();
            }
        } catch (IOException ioe) {
            System.out.println("Erreur --" + ioe.toString());
        }
    }

    /**
     * Création d'un fichier rdf des agents extrait d'un fichier csv. La
     * différence avec l'autre est que celui-là permet de récupérer plus
     * d'information. Les règles concernant les fichiers csv se trouve dans la
     * classe CsvToRdf du package gui. Le fichier csv doit être de la forme :
     * (1)- nom/name/family name/nom de famille {obligatoire} (2)- prenom/given
     * name /surname {obligatoire} (3)- date de naissance/birthdate (4)-
     * genre/gender (5)- titre/title (6)- numéro de téléphone / téléphone /
     * phone / phone number. Tous les champs doivent être présent mais les
     * champs notés {obligatoire} doivent absolument avoir une valeur
     *
     * @see gui.CsvToRdf#item_help
     *
     * @param pathIn chemin du fichier csv
     * @param pathOut chemin de sortie du fichier (peut être null)
     */
    public static void extraction_agents_gui(File pathIn, String pathOut) {

        Model ModelAgent = ModelFactory.createDefaultModel();

        String line;
        String[] values;
        LinkedList<String> our_values = new LinkedList<>();
        boolean found;
        int i;
        int size;
        Resource newArticle;
        String nom;
        String prenom;
        String dateNaissance;
        String genre;
        String titre;
        String telephone;
        String uri;
        String tmp;
        String tmp2;
        Resource rdfType_agent = ModelAgent.createResource("http://xmlns.com/foaf/0.1/Person");

        try {
            BufferedReader buff = new BufferedReader(new FileReader(pathIn));

            try {
                line = buff.readLine(); //Saut de la première ligne (définition des colonnes)
                if (line.split(",").length == 6) {
                    while ((line = buff.readLine()) != null) { //Lecture ligne à ligne
                        our_values.removeAll(our_values);
                        values = line.split(",");   //Tokenizer par le caractère ','
                        size = values.length;
                        i = 0;
                        while (i < size) {  //Parfois certain champs possède le caractère ','. Ces champs sont 
                            tmp = values[i]; //Caractérisé par l'ajout de ' " ' en début et fin. 
                            ++i;
                            if ((tmp.length() > 0) && (tmp.charAt(0) == '\"')) { //Ici lorsqu'un token commence par ' " '
                                found = false;                                 //On le groupe tant qu'on ne rencontre pas un token
                                while (!found && i < size) {                   //finissant par ' " '
                                    tmp2 = values[i];
                                    ++i;
                                    found = tmp2.charAt(tmp2.length() - 1) == '\"';
                                    tmp += "," + tmp2;
                                }
                            }
                            our_values.add(tmp);    //les vrais tokens sont donc stockés ici
                        }

                        nom = our_values.get(0);
                        prenom = our_values.get(1);
                        dateNaissance = our_values.get(2);
                        genre = our_values.get(3);
                        titre = our_values.get(4);
                        telephone = our_values.get(5);

                        uri = createURI_agent(nom, prenom);

                        newArticle = ModelAgent.createResource(uri);

                        newArticle.addProperty(RDF.type, rdfType_agent);
                        newArticle.addProperty(FOAF.family_name, nom);
                        newArticle.addProperty(FOAF.firstName, prenom);
                        newArticle.addProperty(FOAF.title, titre);
                        newArticle.addProperty(FOAF.birthday, dateNaissance);
                        newArticle.addProperty(FOAF.gender, genre);
                        newArticle.addProperty(FOAF.phone, telephone);
                    }
                }
            } finally {
                buff.close();
            }
        } catch (IOException ioe) {
            System.out.println("Erreur --" + ioe.toString());
        }
        PrintWriter fluxMes_agents;
        try {
            fluxMes_agents = new PrintWriter(new FileOutputStream(pathOut));
            ModelAgent.write(fluxMes_agents);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ExtractionAgent.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Permet de tester si le fichier csv en entrée répond aux critères
     *
     * @param pathIn fichier en entrée
     * @return 0 si le fichier ne correspond pas, un code erreur sinon
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static int test_extraction_agents(File pathIn) throws FileNotFoundException, IOException {
        int result = 0;
        LinkedList<String> our_values = new LinkedList<>();
        String[] values;
        BufferedReader buff = new BufferedReader(new FileReader(pathIn));
        String line = buff.readLine(); //Saut de la première ligne (définition des colonnes)
        values = line.split(",");   //Tokenizer par le caractère ','
        int size = values.length;
        int i = 0;
        String tmp;
        String tmp2;
        boolean found;
        while (i < size) {  //Parfois certain champs possède le caractère ','. Ces champs sont 
            tmp = values[i]; //Caractérisé par l'ajout de ' " ' en début et fin. 
            ++i;
            if ((tmp.length() > 0) && (tmp.charAt(0) == '\"')) { //Ici lorsqu'un token commence par ' " '
                found = false;                                 //On le groupe tant qu'on ne rencontre pas un token
                while (!found && i < size) {                   //finissant par ' " '
                    tmp2 = values[i];
                    ++i;
                    found = tmp2.charAt(tmp2.length() - 1) == '\"';
                    tmp += "," + tmp2;
                }
            }
            our_values.add(tmp);    //les vrais tokens sont donc stockés ici
        }

        if (our_values.size() < 6) {
            return 7;
        } else {
            String c1 = normalize(our_values.get(0));
            String c2 = normalize(our_values.get(1));
            String c3 = normalize(our_values.get(2));
            String c4 = normalize(our_values.get(3));
            String c5 = normalize(our_values.get(4));
            String c6 = normalize(our_values.get(5));
            if ((c1 == null) || ((!c1.equals("nom")) && (!c1.equals("name")) && (!c1.equals("familyname")) && (!c1.equals("nomdefamille")))) {
                return 1;
            }
            if ((c2 == null) || ((!c2.equals("prenom")) && (!c2.equals("givenname")) && (!c2.equals("surname")))) {
                return 2;
            }
            if ((!c3.equals("datedenaissance")) && (!c3.equals("birthdate"))) {
                return 3;
            }
            if ((!c4.equals("genre")) && (!c4.equals("gender"))) {
                return 4;
            }
            if ((!c5.equals("titre")) && (!c5.equals("title"))) {
                return 5;
            }
            if ((!c6.equals("numerodetelephone")) && (!c6.equals("telephone")) && (!c6.equals("phonenumber")) && (!c6.equals("phone"))) {
                return 6;
            }

        }
        return result;
    }

}
