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
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import static com.ird.enrichissement_eco.Utilitaires.normalize;
import java.util.HashMap;

/**
 * Fichier permettant l'enrichissement d'un fichier de bibliographique issu de
 * Zotero (format RDF) et d'un fichier d'agents (format RDF).
 * A partir de ces deux modèles, deux autres sont remplis : 
 * ModelBiblio_out.rdf contient l'ensemble des références bibliographiques
 * contenus dans le fichier Zotero avec pour chacune (lorsque c'est possible) :
 * - Son titre 
 * - Ses auteurs (URI du fichier ModelAgent_out.rdf) 
 * - Sa date de publication 
 * - Un résumé 
 * - Un lien vers la ressource en ligne
 *
 * ModelAgent_out.rdf contient, pour chaque agent, l'ensemble de ses
 * publications (URI du fichier ModelBiblio_out.rdf)
 *
 * @author jimmy
 */
public class EnrichissementBiblio {



    /**
     * Recherche pour une ressource son titre, son résumé, sa date et son
     * identifier dans un modèle
     *
     * @param subject Ressource sur laquelle va porter l'identification
     * @param ModelBiblio Modèle dans lequel se trouve les informations à
     * identifier
     * @param title Stock la valeur du titre (peut être vide en fin de fonction)
     * @param description Stock la valeur du résumé (peut être vide en fin de
     * fonction)
     * @param date Stock la valeur de la date (peut être vide en fin de
     * fonction)
     * @param identifier Stock la valeur de l'identifiant (peut être vide en fin
     * de fonction)
     */
    static void identification(Resource subject, Model ModelBiblio, StringBuilder title, StringBuilder description, StringBuilder date, StringBuilder identifier) {

        if (title.length() == 0) { //Si le titre n'est pas encore trouvée
            NodeIterator iter_title;
            RDFNode object_title;
            iter_title = ModelBiblio.listObjectsOfProperty(subject, DCTerms.title); //On recherche une occurence d'un triplet <subject> <title> <Titre>
            while ((title.length() == 0) && (iter_title.hasNext())) { //Techniquement qu'un passage dans la boucle
                object_title = iter_title.next();
                title.append(object_title.toString()); //On stock la valeur du titre
            }
        }
        if (description.length() == 0) {    //Même comportement que le titre
            NodeIterator iter_resume;
            RDFNode object_resume;
            iter_resume = ModelBiblio.listObjectsOfProperty(subject, DCTerms.abstract_);
            while ((description.length() == 0) && (iter_resume.hasNext())) {
                object_resume = iter_resume.next();
                description.append(object_resume.toString());
            }
        }
        if (date.length() == 0) {   //Même comportement que le titre
            NodeIterator iter_date;
            RDFNode object_date;
            iter_date = ModelBiblio.listObjectsOfProperty(subject, DCTerms.date);
            while ((date.length() == 0) && (iter_date.hasNext())) {
                object_date = iter_date.next();
                date.append(object_date.toString());

            }
        }
        if (identifier.length() == 0) { //Si l'identifiant n'est pas encore trouvé
            StmtIterator iter = ModelBiblio.listStatements(); //Itérateur sur tous les triplets
            Statement state;
            RDFNode object;
            Resource o_as_r;
            Resource s;
            while ((identifier.length() == 0) && (iter.hasNext())) { //Parcours des triplets tant que l'identifiant n'est pas trouvé
                state = iter.nextStatement();                        // On cherche un triplet tel que son objet soit le sujet passé en paramètre
                object = state.getObject();
                if (object.isResource()) {                          // L'objet recherché doit être un sujet, donc une ressource
                    o_as_r = object.asResource();
                    if (o_as_r.equals(subject)) {                   // Si on a égalité alors le sujet de ce triplet est un père de notre point
                        s = state.getSubject();
                        if ( (s.getURI() != null) && (!s.getURI().startsWith("http://www.ecoscope.org/ontologies/bnode#"))) { // Si il n'est pas un noeud anonyme, on récupère son URI
                            identifier.append(s.toString());
                        }
                    }
                }
            }
        }
    }

    /**
     * Recherche, par les pères, pour une ressource de son titre, son résumé, sa
     * date et de son identifieur.
     *
     * @param b_node Ressource sur laquelle va porter l'identification
     * @param ModelBiblio Modèle dans lequel se trouve les informations à
     * identifier
     * @param title Stock la valeur du titre (peut être vide en fin de fonction)
     * @param description Stock la valeur du résumé (peut être vide en fin de
     * fonction)
     * @param date Stock la valeur de la date (peut être vide en fin de
     * fonction)
     * @param identifier Stock la valeur de l'identifiant (peut être vide en fin
     * de fonction)
     * @see com.ird.enrichissement_eco.EnrichissementBiblio#identification(com.hp.hpl.jena.rdf.model.Resource, com.hp.hpl.jena.rdf.model.Model, java.lang.StringBuilder, java.lang.StringBuilder, java.lang.StringBuilder, java.lang.StringBuilder)
     */
    static void identification_up(Resource b_node, Model ModelBiblio, StringBuilder title, StringBuilder description, StringBuilder date, StringBuilder identifier) {
        identification(b_node, ModelBiblio, title, description, date, identifier); //Tentative d'identification au même niveau
        if ((title.length() == 0) || (description.length() == 0) || (date.length() == 0) || (identifier.length() == 0)) { //Si l'on n'a pas pu tout identifier
            if( (b_node.getURI() == null) || (b_node.getURI().startsWith("http://www.ecoscope.org/ontologies/bnode#"))) {  //Et que le noeud courant est anonyme (on ne remonte pas plus haut que le premier noeud non anonyme)
                StmtIterator iter = ModelBiblio.listStatements(); //Parcours de tous les triplets afin de trouver tous ses noeuds pères
                Statement state;
                RDFNode object;
                Resource o_as_r;
                Resource s;
                while (((title.length() == 0) || (description.length() == 0) || (date.length() == 0) || (identifier.length() == 0)) && iter.hasNext()) { //Arrêt si toutes les informations sont trouvée
                    state = iter.nextStatement();
                    object = state.getObject();
                    if (object.isResource()) {   // On cherche un triplet tel que son objet soit le sujet passé en paramètre. C'est donc une ressource
                        o_as_r = object.asResource();
                        if (o_as_r.equals(b_node)) {   // Si on a égalité alors le sujet de ce triplet est un père de notre point
                            s = state.getSubject();
                            identification_up(s, ModelBiblio, title, description, date, identifier); //Rappel récursif de la fonction sur le sujet.
                        }
                    }
                }
            }
        }
    }

    /**
     * Recherche, par les fils, pour une ressource son titre, son résumé, sa
     * date et son identifier dans un modèle
     *
     * @param b_node Ressource sur laquelle va porter l'identification
     * @param ModelBiblio Modèle dans lequel se trouve les informations à
     * identifier
     * @param title Stock la valeur du titre (peut être vide en fin de fonction)
     * @param description Stock la valeur du résumé (peut être vide en fin de
     * fonction)
     * @param date Stock la valeur de la date (peut être vide en fin de
     * fonction)
     * @param identifier Stock la valeur de l'identifiant (peut être vide en fin
     * de fonction)
     * @see com.ird.enrichissement_eco.EnrichissementBiblio#identification(com.hp.hpl.jena.rdf.model.Resource, com.hp.hpl.jena.rdf.model.Model, java.lang.StringBuilder, java.lang.StringBuilder, java.lang.StringBuilder, java.lang.StringBuilder)
     */
    static void identification_down(Resource subject, Model ModelBiblio, StringBuilder title, StringBuilder description, StringBuilder date, StringBuilder identifier) {
        identification(subject, ModelBiblio, title, description, date, identifier); //Tentative d'identification au même niveau
        if ((title.length() == 0) || (description.length() == 0) || (date.length() == 0) || (identifier.length() == 0)) { //Si l'on n'a pas pu tout identifier
            StmtIterator iter = ModelBiblio.listStatements(); //Parcours de tous les triplets afin de trouver tous ses noeuds fils
            Statement state;
            RDFNode object;
            Property p;
            Resource s;
            while (((title.length() == 0) || (description.length() == 0) || (date.length() == 0) || (identifier.length() == 0)) && iter.hasNext()) { //Arrêt si toutes les informations sont trouvée
                state = iter.nextStatement();
                p = state.getPredicate();
                s = state.getSubject();
                if (s.equals(subject)) { // On cherche un triplet tel que son sujet soit le sujet passé en paramètre
                    if (!p.equals(DCTerms.creator)) { // On ne descend pas si le prédicat est creator (on sait que c'est pas ce que l'on recherche
                        object = state.getObject();
                        if (object.isResource()) { // Pas d'intérêt à apeller la fonction sur un litéral
                            if( (object.asResource().getURI() == null  || (object.asResource().getURI().startsWith("http://www.ecoscope.org/ontologies/bnode#")))) { //Rappel uniquement sur les noeuds anonymes. Pour ne pas sortir du cadre de la ressource qui nous intéresse
                                identification_down(object.asResource(), ModelBiblio, title, description, date, identifier);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Fais le lien entre ModelBiblio et ModelAgent. Création dans
     * ModelBiblio_out d'une ressource pour chaque article où sont récupérées
     * les informations suivantes : - Titre - Auteurs (sous leur forme d'URI de
     * ModelAgent) - Résumé - date - liens URL vers la ressource Création dans
     * ModelAgent_out d'une ressource par agent où est récupérée la liste de ses
     * publications (sous leur forme d'URI de ModelBiblio_out)
     *
     * @param ModelBiblio Modèle issu d'un Fichier RDF exporté de Zotero
     * @param ModelAgent Modèle issu du fichier RDF crée par la fonction
     * "extraction_agent"
     * @param ModelBiblio_out Modèle vide, rempli pendant l'appel
     * @param ModelAgent_out Modèle vide, rempli pendant l'appel
     */
    public static void enrichissement(Model ModelBiblio, Model ModelAgent, Model ModelBiblio_out, Model ModelAgent_out) {
        
        RDFNode object_biblio;
        String name_biblio = "";
        Resource subject_biblio;
        Resource newArticle_biblio;

        boolean found_name_biblio = false;
        String givenname_biblio = "";
        boolean found_givenname_biblio = false;

        Resource subject_agent;
        Resource newArticle_agent;

        ResIterator iter_subject_creator;
        NodeIterator iter_object_creator;
        NodeIterator iter_name_biblio;
        NodeIterator iter_givenname_biblio;
        RDFNode object_name_biblio;
        RDFNode object_givenname_biblio;

        ResIterator iter_Sname_agent;
        NodeIterator iter_Oname_agent;
        NodeIterator iter_Ogivenname_agent;
        RDFNode object_name_agent;
        boolean match_name;
        boolean match_givenname;
        RDFNode object_givenname_agent;
        String name_agent;
        String givenname_agent;

        Resource rdfType_biblio = ModelBiblio.createResource("http://www.ecoscope.org/ontologies/resources_def/publication"); //Type des ressources à ajouter au fichier ModelBiblio_out
        Resource rdfType_agent = ModelAgent.createResource("http://xmlns.com/foaf/0.1/Person"); //Type des ressources à ajouter au fichier ModelAgent_out

        String base_uri_biblio = "http://www.ecoscope.org/ontologies/resources/biblioUMRpublication/"; //Base de l'uri utilisés pour chaque publication
        String uri_biblio;

        HashMap<String, String> map_title = new HashMap<>();
        HashMap<String, String> map_description = new HashMap<>();
        HashMap<String, String> map_date = new HashMap<>();

        StringBuilder title = new StringBuilder();
        StringBuilder date = new StringBuilder();
        StringBuilder resume = new StringBuilder();
        StringBuilder identifier = new StringBuilder();

        iter_subject_creator = ModelBiblio.listSubjectsWithProperty(DCTerms.creator); //Parcours de tous les sujets qui soient dans un triplet possèdant le prédicat "creator"
        while (iter_subject_creator.hasNext()) {

            title.delete(0, title.length()); //Remise à zéro des informations à charger
            date.delete(0, date.length());
            resume.delete(0, resume.length());
            identifier.delete(0, identifier.length());

            subject_biblio = iter_subject_creator.next(); //On récupère un sujet. On va chercher à savoir à quelle ressource bibliographique il appartient (attaché à quel titre ?)

            if( (subject_biblio.getURI() == null  || (subject_biblio.getURI().startsWith("http://www.ecoscope.org/ontologies/bnode#")))) { //Si le sujet est un noeud anonyme, la recherche peut potentiellement être vers le haut et vers le bas
                identification(subject_biblio, ModelBiblio, title, resume, date, identifier); //On cherche tout d'abord dans tous les triplets dont il est le sujet

                if ((title.length() == 0) || (resume.length() == 0) || (date.length() == 0) || (identifier.length() == 0)) { //Si toutes les informations n'ont pas pu être récupérer
                    identification_up(subject_biblio, ModelBiblio, title, resume, date, identifier); //On cherche en haut d'abord, puis en bas
                    if ((title.length() == 0) || (resume.length() == 0) || (date.length() == 0) || (identifier.length() == 0)) {
                        identification_down(subject_biblio, ModelBiblio, title, resume, date, identifier);
                    }
                }
            } else { //Si le sujet n'est pas une ressource anonyme alors l'identification vers le haut est inutile
                identifier.append(subject_biblio.toString()); //De plus son identifiant correspond à l'URI du sujet (attribuée par Zotero)
                identification(subject_biblio, ModelBiblio, title, resume, date, identifier);
                if ((title.length() == 0) || (resume.length() == 0) || (date.length() == 0) || (identifier.length() == 0)) {
                    identification_down(subject_biblio, ModelBiblio, title, resume, date, identifier);
                }

            }

            uri_biblio = base_uri_biblio + normalize((title.toString())); //Création de l'URI pour cette publication

            iter_object_creator = ModelBiblio.listObjectsOfProperty(subject_biblio, DCTerms.creator); //Parcours de tous les triplets S_creator_O de biblio
            while (iter_object_creator.hasNext()) {
                object_biblio = iter_object_creator.next(); //Identification du nom / prénom de l'auteur dans le fichier biblio
                if (object_biblio.isResource()) { //Simple vérification, dans les faits Zotero crée 1 ressource par agent
                    iter_name_biblio = ModelBiblio.listObjectsOfProperty(object_biblio.asResource(), FOAF.surname);
                    found_name_biblio = false;
                    while ((!found_name_biblio) && iter_name_biblio.hasNext()) { //On récupère le nom associé à la ressource
                        object_name_biblio = iter_name_biblio.next();
                        name_biblio = object_name_biblio.toString();
                        found_name_biblio = true;
                    }
                    iter_givenname_biblio = ModelBiblio.listObjectsOfProperty(object_biblio.asResource(), FOAF.givenname);
                    found_givenname_biblio = false;
                    while ((!found_givenname_biblio) && iter_givenname_biblio.hasNext()) { //On récupère le prénom associé à la ressource
                        object_givenname_biblio = iter_givenname_biblio.next();
                        givenname_biblio = object_givenname_biblio.toString();
                        found_givenname_biblio = true;
                    }
                }

                if (found_name_biblio && found_givenname_biblio) { //Si l'on bien été en mesure de récupérer un nom et un prénom pour l'auteur on tente l'identification avec une ressource du fichier des agents
                    match_name = false;
                    match_givenname = false;
                    iter_Sname_agent = ModelAgent.listSubjectsWithProperty(FOAF.family_name); 
                    while ((!match_givenname) && iter_Sname_agent.hasNext()) {
                        subject_agent = iter_Sname_agent.next();
                        iter_Oname_agent = ModelAgent.listObjectsOfProperty(subject_agent, FOAF.family_name);
                        while (((!match_name) && (iter_Oname_agent.hasNext()))) {
                            object_name_agent = iter_Oname_agent.next(); //Ici on a un couple S_familyname_O, on veut identifier O avec name_biblio
                            name_agent = object_name_agent.toString();
                            if (normalize(name_biblio).equals(normalize(name_agent))) //On a un match
                            {
                                match_name = true;
                                iter_Ogivenname_agent = ModelAgent.listObjectsOfProperty(subject_agent, FOAF.firstName);
                                while (((!match_givenname) && (iter_Ogivenname_agent.hasNext()))) {
                                    object_givenname_agent = iter_Ogivenname_agent.next(); //Ici on a un couple S_givenname_O, on veut identifier O avec givenname_biblio
                                    givenname_agent = object_givenname_agent.toString();
                                    if (normalize(givenname_biblio.charAt(0)) == (normalize(givenname_agent.charAt(0)))) //On a un match
                                    {
                                        match_givenname = true;

                                        newArticle_biblio = ModelBiblio_out.createResource(uri_biblio);
                                        newArticle_agent = ModelAgent_out.createResource(subject_agent.getURI());

                                        newArticle_biblio.addProperty(DC.creator, newArticle_agent);
                                        newArticle_agent.addProperty(FOAF.publications, newArticle_biblio);

                                        newArticle_biblio.addProperty(RDF.type, rdfType_biblio);
                                        newArticle_agent.addProperty(RDF.type, rdfType_agent);

                                        /*
                                         Série de test empéchant 
                                         - D'avoir plusieurs titre / date / description (on peut avoir plusieurs identifier)
                                         - D'entrer une valeur nulle
                                         */
                                        if ((!map_title.containsKey(uri_biblio)) || (map_title.get(uri_biblio).equals("")) && (!title.toString().equals(""))) {
                                            map_title.put(uri_biblio, title.toString());
                                            newArticle_biblio.addProperty(DC.title, title.toString());
                                        }
                                        if ((!map_date.containsKey(uri_biblio)) || (map_date.get(uri_biblio).equals("")) && (!date.toString().equals(""))) {
                                            map_date.put(uri_biblio, date.toString());
                                            newArticle_biblio.addProperty(DC.date, date.toString());
                                        }

                                        if ((!map_description.containsKey(uri_biblio)) || (map_description.get(uri_biblio).equals("")) && (!resume.toString().equals(""))) {
                                            map_description.put(uri_biblio, resume.toString());
                                            newArticle_biblio.addProperty(DC.description, resume.toString());
                                        }
                                        if (!identifier.toString().equals("")) {
                                            newArticle_biblio.addProperty(DC.identifier, identifier.toString());
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
    }
}
