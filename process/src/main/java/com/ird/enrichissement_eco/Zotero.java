package com.ird.enrichissement_eco;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.DCTerms;
import static com.ird.enrichissement_eco.Utilitaires.createURI_agent;
import static com.ird.enrichissement_eco.Utilitaires.createURI_biblio;
import java.util.HashMap;

/**
 * Fichier permettant l'amélioration du bibliographie produit par Zotero. -
 * Supprime les doublons - Ajoute les prénoms lorsqu'ils étaient manquant (là où
 * c'est possible) - Nommage des noeuds anonymes (leur donne les URIs Ecoscopes
 * lorsque c'est possible, une arbitraire sinon)
 *
 * @author jimmy
 */
public class Zotero {

    /**
     * Traitement d'un modèle généré à partir d'un fichier RDF produit par
     * Zotero.
     *
     * @param ModelBiblio Modèle issu d'un fichier RDF exporté de Zotero
     * @param ModelZotero Modèle vide, est rempli en supprimant les doublons
     * de ModelBiblio. Peut être exporté dans Zotero
     */
    public static void export_to_zotero(Model ModelBiblio, Model ModelZotero) {
        Resource subject;
        Property predicat;
        RDFNode object;
        Resource article;
        Statement state;

        ResIterator iter_subject;
        NodeIterator iter_object;
        StmtIterator iter_stmt;

        String base_uri_bnode = "http://www.ecoscope.org/ontologies/bnode#";
        int cpt_bnode = 0;
        String uri = "";
        String surname;
        String givenname;
        char c;
        int size_givenname;
        int size_current_givenname;
        boolean is_longer;
        RDFNode object_givenname;

        HashMap<Resource, Resource> map = new HashMap<>(); //Table associant à une ressource une URI

        iter_subject = ModelBiblio.listSubjects(); //Parcours de tous les sujets afin de leur associer une URI
        while (iter_subject.hasNext()) {
            subject = iter_subject.nextResource();
            iter_object = ModelBiblio.listObjectsOfProperty(subject, DCTerms.title); //Si ils ont un titre : c'est une référence biblio
            if (iter_object.hasNext()) {
                object = iter_object.nextNode();
                uri = createURI_biblio(object.toString());
            } else {
                iter_object = ModelBiblio.listObjectsOfProperty(subject, FOAF.surname); // Si ils ont un nom / prénom, c'est un agent
                if (iter_object.hasNext()) {
                    object = iter_object.nextNode();
                    surname = object.toString();
                    iter_object = ModelBiblio.listObjectsOfProperty(subject, FOAF.givenname);
                    if (iter_object.hasNext()) {
                        object = iter_object.nextNode();
                        givenname = object.toString();
                        uri = createURI_agent(surname, givenname.charAt(0));
                    }
                } else { //Sinon, c'est une ressource quelconque. Identifiée par un n° (ordre d'apparition)
                    uri = base_uri_bnode + cpt_bnode; //Attention, cette URI n'est pas recalculable. Elle est néanmoins constante d'un fichier à un autre
                    ++cpt_bnode;
                }
            }
            article = ModelZotero.createResource(uri);
            map.put(subject, article); //On place le couple dans la table
        }

        iter_stmt = ModelBiblio.listStatements(); //Parcours de tous les triplets
        while (iter_stmt.hasNext()) {
            state = iter_stmt.nextStatement();
            subject = state.getSubject();
            predicat = state.getPredicate();
            object = state.getObject();
            if (map.containsKey(subject)) { //Si l'URI du sujet a été changé : on récupère la nouvelle
                subject = map.get(subject);
            }
            article = ModelZotero.createResource(subject.getURI()); //Création de la ressource
            if ((object.isResource()) && (map.containsKey(object.asResource()))) { //Si l'URI de l'objet a été changé : on récupère la nouvelle
                object = map.get(object.asResource());
            }

            //On n'ajoute pas un foaf:givenname si : On en a déjà un pour cet agent ET il est plus long
            if (predicat.equals(FOAF.givenname)) {
                size_givenname = object.toString().length();
                is_longer = true;
                iter_object = ModelZotero.listObjectsOfProperty(subject, predicat);
                while (is_longer && iter_object.hasNext()) {
                    object_givenname = iter_object.nextNode();
                    size_current_givenname = object_givenname.toString().length();
                    if (size_current_givenname < size_givenname) { //Si on les prénoms déjà trouvé sont plus petits : on les supprime
                        ModelZotero.remove(subject, predicat, object_givenname);
                    } else {
                        is_longer = false; //Si on trouve sur un prénom plus long : on ne l'ajoute
                    }
                }
                if (is_longer) {
                    article.addProperty(predicat, object); //-> On a trouvé un prénom plus long : on l'ajoute
                }
            } else {
                article.addProperty(predicat, object); //Sinon on ajoute directement l'objet (il n'y avait pas de prénom pour cet agent avant
            }
        }
    }
}
