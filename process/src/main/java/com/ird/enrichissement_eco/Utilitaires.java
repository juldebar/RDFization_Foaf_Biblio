package com.ird.enrichissement_eco;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.DC;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Fichier comportant la majorité des fonctions "utiles" aux autres fichiers
 *
 * @author jimmy
 */
public class Utilitaires {

    /**
     * Transforme une chaine de caractère en une chaîne équivalente composée
     * uniquement des caractères [a-z]
     *
     * @param in
     *
     * @return Une chaîne composée uniquement des caractères [a-z]
     * @see com.ird.enrichissement_eco.Utilitaires#normalize(char)
     */
    public static String normalize(String in) {
        String out = "";
        char c;
        int i = 0;
        while (i < in.length()) {
            c = in.charAt(i);
            c = normalize(c);
            if ((c >= 'a') && (c <= 'z')) {     //test de validité du caractère à ajouter
                out += c;
            }
            ++i;
        }
        return out;
    }

    /**
     * Transforme un cacactère en son équivalent dans [a-z]
     *
     * @param in
     * @return Un caractère dans [a-z]
     */
    static char normalize(char in) {
        char out = in;
        if ((in == 'ç') || (in == 'Ç')) {
            out = 'c';
        }
        if ((in == 'é') || (in == 'ê') || (in == 'è') || (in == 'ë') || (in == 'È') || (in == 'É') || (in == 'Ê') || (in == 'Ë')) {
            out = 'e';
        }
        if ((in == 'â') || (in == 'à') || (in == 'ä') || (in == 'á') || (in == 'ã') || (in == 'æ') || (in == 'Á') || (in == 'À') || (in == 'Â') || (in == 'Ã') || (in == 'Ä') || (in == 'Æ')) {
            out = 'a';
        }
        if ((in == 'ù') || (in == 'û') || (in == 'ü') || (in == 'Ù') || (in == 'Ú') || (in == 'Û') || (in == 'Ü')) {
            out = 'u';
        }
        if ((in == 'î') || (in == 'í') || (in == 'ï') || (in == 'ì') || (in == 'Ì') || (in == 'Í') || (in == 'Î') || (in == 'Ï')) {
            out = 'i';
        }
        if ((in == 'Ñ') || (in == 'ñ')) {
            out = 'n';
        }
        if ((in == 'ó') || (in == 'ô') || (in == 'õ') || (in == 'ö') || (in == 'ò') || (in == 'Ò') || (in == 'Ó') || (in == 'Ô') || (in == 'Õ') || (in == 'Ö')) {
            out = 'o';
        }
        if ((in == 'ù') || (in == 'ú') || (in == 'û') || (in == 'ü') || (in == 'Ù') || (in == 'Ú') || (in == 'Û') || (in == 'Ü')) {
            out = 'u';
        }
        if ((in == 'Ý') || (in == 'ý') || (in == 'ÿ')) {
            out = 'y';
        }
        if ((in >= 'A') && (in <= 'Z')) {
            out = (char) (in + 32);
        } 
        return out;
    }

    /**
     * Crée une URI pour un agent à partir de son nom et prénom
     *
     * @param nom nom de l'agent
     * @param prenom prénom de l'agent
     * @return base de l'URI suivi du prénom (normalisé) et du nom (normalisé
     * excepté la 1ere lettre en majuscule)
     */
    static String createURI_agent(String nom, String prenom) {
        String uri;
        String base_uri = "http://www.ecoscope.org/ontologies/agents/";
        String tmp = normalize(nom);
        char c = tmp.charAt(0);
        c -= 32;
        tmp = tmp.substring(1, tmp.length());
        uri = base_uri + normalize(prenom) + c + tmp; //L'uri des agents est l'uri de base, suivie du prénom (filtré) et du nom filtré mais dont la 1ere lettre est en majuscule

        return uri;
    }

    /**
     * Crée une URI pour un agent à partir de son nom et prénom (1ere lettre).
     * Utilisé pour produire le fichier Zotero : en effet parfois on ne possède
     * pas le prénom entier
     *
     * @param nom nom de l'agent
     * @param prenom première lettre du prénom de l'agent
     * @return base de l'URI suivi du prénom (normalisé) et du nom (normalisé
     * excepté la 1ere lettre en majuscule)
     */
    static String createURI_agent(String nom, char prenom) {
        String uri;
        String base_uri = "http://www.ecoscope.org/ontologies/agents/";
        String tmp = normalize(nom);
        char c = tmp.charAt(0);
        c -= 32;
        tmp = tmp.substring(1, tmp.length());
        uri = base_uri + normalize(prenom) + c + tmp; //L'uri des agents est l'uri de base, suivie du prénom (filtré) et du nom filtré mais dont la 1ere lettre est en majuscule

        return uri;
    }

    /**
     * Crée une URI pour une référence bibliographique à partir de son titre
     *
     * @param title titre de la référence
     * @return base de l'URI suivi du titre (normalisé)
     */
    static String createURI_biblio(String title) {
        return "http://www.ecoscope.org/ontologies/resources/biblioUMRpublication/" + normalize(title);
    }

    /**
     * Crée une URI pour une organisation
     *
     * @param nom nom de l'organisation
     * @return base de l'URI suivi du nom (normalisé)
     */
    static String createURI_organization(String nom) {
        return "http://www.ecoscope.org/ontologies/resources/agents/organization" + nom;
    }

    /**
     * Retourne le mot supposé correspondre au nom du model correspondant à une
     * URI (le mot avant le dernier '/')
     *
     * @param word Une URI
     * @return Le mot avant le dernier '/'
     */
    static String identifier_model(String word) {
        String[] tokens = word.split("/");
        return tokens[tokens.length - 2];
    }

    /**
     * Test si le model est un model accepté
     *
     * @param model Nom du model d'où est tirée la ressource
     * @return True si le model correspond à "ecosystems", "ecosystems_def" ou
     * "agents", False sinon
     */
    static boolean is_valid_model(String model) {
        boolean valid = false;
        if ((model.equals("ecosystems")) || (model.equals("ecosystems_def")) || (model.equals("agents"))) {
            valid = true;
        }
        return valid;
    }

    /**
     * Fonction d'affichage de la map particulière <Resource,
     * LinkedList<String>> @param map
     *
     * @param map Table faisant le lien entre une ressource et ses labels
     */
    static void affiche_RtoS(HashMap<Resource, LinkedList<String>> map) {
        for (Map.Entry<Resource, LinkedList<String>> entry : map.entrySet()) {
            System.out.println("Ressource : " + entry.getKey());
            for (String value : entry.getValue()) {
                System.out.println(value);
            }
            System.out.println("\n");
        }
    }

    /**
     * Fonction d'affichage de la map particulière <String, Resource>
     *
     * @param map Table faisant le lien entre un label et sa ressource
     */
    static void affiche_StoR(HashMap<String, Resource> map) {
        for (Map.Entry<String, Resource> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " <-> " + entry.getValue());
            System.out.println("\n");
        }
    }

    /**
     * Retire la présence d'une lettre suivi d'un apostrophe en début de mot
     *
     * @param token
     * @return token sans sa première lettre et l'apostrophe si cela est
     * nécessaire
     */
    static String filtrer_apostrophe(String token) {
        if (token.length() > 2) {
            if (token.charAt(1) == '\'') {
                token = token.substring(2, token.length());
            }
        }
        return token;
    }

    /**
     * Traitement divers pour rendre un String comparable à son indexation
     *
     * @param mot Le mot à traiter
     * @return mot sans majuscule
     */
    static String retirer_maj(String mot) {
        String result = "";
        char in;
        for (int i = 0; i < mot.length(); ++i) {
            in = mot.charAt(i);
            if ((in >= 'A') && (in <= 'Z')) {
                in = (char) (in + 32);
            }
            result += in;
        }
        return result;
    }

    /**
     * Stock dans une string l'ensemble des titres contenus dans ModelBiblio
     *
     * @param ModelBiblio Model RDF contenant article - titre - auteur - résumé
     * @return L'ensemble des titres du model
     */
    static String get_titres(Model ModelBiblio) {

        Property predicat;
        Statement state;
        StmtIterator iter_stmt;
        String titre = "";

        iter_stmt = ModelBiblio.listStatements(); //Parcours de tous les triplets du modèle
        while (iter_stmt.hasNext()) {
            state = iter_stmt.nextStatement();
            predicat = state.getPredicate();
            if (predicat.equals(DC.title)) { //Si le prédicat est un titre
                titre += state.getObject().toString() + " "; //On ajoute l'objet à la chaîne. On sépare avec la prochaine occurrence par un espace
            }
        }
        return titre;
    }

    /**
     * Stock dans une string l'ensemble des titres obtenus à partir d'une
     * requête Sparql sur un serveur endpoind
     *
     * @param endpoint adresse du service
     * @return Une chaine de caractère contenant tous les titres
     */
    static String get_titres(String endpoint) {
        String titres = "";
        String query
                = "PREFIX dc:  <http://purl.org/dc/elements/1.1/>\n"
                + "SELECT ?subject ?object WHERE {\n"
                + "     ?subject dc:title ?object.\n "
                + "}";

        QueryExecution qe = QueryExecutionFactory.sparqlService(endpoint, query);
        ResultSet rs = qe.execSelect();
        QuerySolution s;
        String object;
        while (rs.hasNext()) {
            s = rs.nextSolution();
            object = s.getLiteral("?object").toString();
            titres += object + " ";
        }
        return titres;
    }

    /**
     * Stock dans une string l'ensemble des résumés contenus dans ModelBiblio
     *
     * @param ModelBiblio Model RDF contenant article - titre - auteur - résumé
     * @return L'ensemble des résumés du model
     */
    static String get_resumes(Model ModelBiblio) {

        Property predicat;
        Statement state;
        StmtIterator iter_stmt;
        String resume = "";

        iter_stmt = ModelBiblio.listStatements();//Parcours de tous les triplets du modèle
        while (iter_stmt.hasNext()) {
            state = iter_stmt.nextStatement();
            predicat = state.getPredicate();
            if (predicat.equals(DC.description)) {  //Si le prédicat est un description (abstract)
                resume += state.getObject().toString() + " "; //On ajoute l'objet à la chaîne. On sépare avec la prochaine occurrence par un espace
            }
        }
        return resume;
    }

    /**
     * Stock dans une string l'ensemble des resumes obtenus à partir d'une
     * requête Sparql sur un serveur endpoind
     *
     * @param endpoint adresse du service
     * @return Une chaine de caractère contenant tous les resumes
     */
    static String get_resumes(String endpoint) {
        String resumes = "";
        String query
                = "PREFIX dc:  <http://purl.org/dc/elements/1.1/>\n"
                + "SELECT ?subject ?object WHERE {\n"
                + "     ?subject dc:description ?object.\n "
                + "}";

        QueryExecution qe = QueryExecutionFactory.sparqlService(endpoint, query);
        ResultSet rs = qe.execSelect();
        QuerySolution s;
        String object;
        while (rs.hasNext()) {
            s = rs.nextSolution();
            object = s.getLiteral("?object").toString();
            resumes += object + " ";
        }
        return resumes;
    }

    /**
     * Remplissage des maps avec le model présent sur le endpoint Attention on
     * n'utilise que les modèles suivants : ecosystems, ecosystems_def et
     * agents.
     *
     * @param map_RtoS Map faisant le lien entre une ressource (URI) et tous ses
     * alt et preflabels (String)
     * @param map_StoR Map faisant le lien entre un label (String) et une
     * ressource (URI)
     * @param service Adresse du service
     */
    static void remplissage_endpoint(HashMap<Resource, LinkedList<String>> map_RtoS, HashMap<String, Resource> map_StoR, String service) {

        //Requête sur prefLabel
        String query
                = "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n"
                + "SELECT ?subject ?object WHERE {\n"
                + "     ?subject skos:prefLabel ?object.\n "
                + "}";

        QueryExecution qe = QueryExecutionFactory.sparqlService(service, query);
        ResultSet rs = qe.execSelect();
        QuerySolution s;
        Resource subject;
        String object;
        String model;
        while (rs.hasNext()) {
            s = rs.nextSolution();
            subject = s.getResource("?subject");
            model = identifier_model(subject.getURI());
            if (is_valid_model(model)) {  //Si la ressource appartient bien à l'un des trois models étudiés, on l'ajoute à la liste 
                object = s.getLiteral("?object").toString();
                if ((object.length() > 3) && (object.charAt(object.length() - 3) == '@')) { //Si le mot termine par "@en" ou "@fr" ou "@es", on retire ces trois caractères
                    object = object.substring(0, object.length() - 3);
                }
                if (!map_RtoS.containsKey(subject)) {
                    map_RtoS.put(subject, new LinkedList<String>());
                }
                if (!map_StoR.containsKey(object)) {
                    map_StoR.put(object, subject);
                }
                map_RtoS.get(subject).add(object);
            }
        }
        //Requête sur altLabel, même traitement que prefLabel
        query
                = "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n"
                + "SELECT ?subject ?object WHERE {\n"
                + "     ?subject skos:altLabel ?object.\n "
                + "}";
        qe = QueryExecutionFactory.sparqlService(service, query);
        rs = qe.execSelect();
        while (rs.hasNext()) {
            s = rs.nextSolution();
            subject = s.getResource("?subject");
            model = identifier_model(subject.getURI());
            if (is_valid_model(model)) {
                object = s.getLiteral("?object").toString();
                if ((object.length() > 3) && (object.charAt(object.length() - 3) == '@')) { //Si le mot termine par "@en" ou "@fr" ou "@es", on retire ces trois caractères
                    object = object.substring(0, object.length() - 3);
                }
                if (!map_RtoS.containsKey(subject)) {
                    map_RtoS.put(subject, new LinkedList<String>());
                }
                if (!map_StoR.containsKey(object)) {
                    map_StoR.put(object, subject);
                }
                map_RtoS.get(subject).add(object);
            }
        }
    }

    /**
     * Remplissage des maps avec le model présent sur le endpoint Attention on
     * n'utilise que les modèles suivants : ecosystems, ecosystems_def et agents
     * et sans les labels contenus dans le fichier présent dans filePath
     *
     * @param map_RtoS Map faisant le lien entre une ressource (URI) et tous ses
     * alt et preflabels (String)
     * @param map_StoR Map faisant le lien entre un label (String) et une
     * ressource (URI)
     * @param filePath label à ne pas récupérer
     * @param service adresse du service endpoind
     *
     * @see
     * com.ird.enrichissement_eco.Utilitaires#remplissage_endpoint(java.util.HashMap,
     * java.util.HashMap)
     */
    static void remplissage_endpoint(HashMap<Resource, LinkedList<String>> map_RtoS, HashMap<String, Resource> map_StoR, String filePath, String service) throws IOException {

        //Construction de la liste des labels à ne pas traiter à partir d'un fichier texte
        LinkedList<String> ne_pas_traiter = not_treated(filePath);

        //Requête sur prefLabel
        String query
                = "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n"
                + "SELECT ?subject ?object WHERE {\n"
                + "     ?subject skos:prefLabel ?object.\n "
                + "}";

        QueryExecution qe = QueryExecutionFactory.sparqlService(service, query);
        ResultSet rs = qe.execSelect();
        QuerySolution s;
        Resource subject;
        String object;
        String model;
        while (rs.hasNext()) {
            s = rs.nextSolution();
            subject = s.getResource("?subject");
            model = identifier_model(subject.getURI());
            if (is_valid_model(model)) {
                object = s.getLiteral("?object").toString();
                if ((object.length() > 3) && (object.charAt(object.length() - 3) == '@')) { //Si le mot termine par "@en" ou "@fr" ou "@es", on retire ces trois caractères
                    object = object.substring(0, object.length() - 3);
                }
                if (!ne_pas_traiter.contains(object)) { //Ajout seulement si le mot ne fait pas partie de la liste à ne pas traiter
                    if (!map_RtoS.containsKey(subject)) {
                        map_RtoS.put(subject, new LinkedList<String>());
                    }
                    if (!map_StoR.containsKey(object)) {
                        map_StoR.put(object, subject);
                    }
                    map_RtoS.get(subject).add(object);
                }
            }
        }

        //Requête sur altLabel, similaire à prefLabel
        query
                = "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n"
                + "SELECT ?subject ?object WHERE {\n"
                + "     ?subject skos:altLabel ?object.\n "
                + "}";
        qe = QueryExecutionFactory.sparqlService(service, query);
        rs = qe.execSelect();
        while (rs.hasNext()) {
            s = rs.nextSolution();
            subject = s.getResource("?subject");
            model = identifier_model(subject.getURI());
            if (is_valid_model(model)) {
                object = s.getLiteral("?object").toString();
                if ((object.length() > 3) && (object.charAt(object.length() - 3) == '@')) { //Si le mot termine par "@en" ou "@fr" ou "@es", on retire ces trois caractères
                    object = object.substring(0, object.length() - 3);
                }
                if (!ne_pas_traiter.contains(object)) {
                    if (!map_RtoS.containsKey(subject)) {
                        map_RtoS.put(subject, new LinkedList<String>());
                    }
                    if (!map_StoR.containsKey(object)) {
                        map_StoR.put(object, subject);
                    }
                    map_RtoS.get(subject).add(object);
                }
            }
        }
    }

    /**
     * Retourne la liste de tous les labels (skos:altLabel et skos:prefLabel)
     * présents sur le endpoint
     *
     * @param service Adresse du service endpoint
     * @return La liste, sans ordre, des alt et prefLabel présents sur le
     * endpoint
     */
    public static LinkedList<String> get_labels(String service) {
        LinkedList<String> result = new LinkedList<>();
        HashMap<Resource, LinkedList<String>> map_RtoS = new HashMap<>(); //Map mettant en relation une ressource avec la liste de ses acceptations
        HashMap<String, Resource> map_StoR = new HashMap<>(); //Map mettant en relation un mot avec son URI
        remplissage_endpoint(map_RtoS, map_StoR, service); //Remplissage des maps de correspondance depuis le SPARQL Endpoint

        for (Map.Entry<String, Resource> map : map_StoR.entrySet()) { //Passage de la map en liste
            if (!result.contains(map.getKey())) {
                result.add(normalize(map.getKey())); //On normalize pour pouvoir comparer sans raté à cause de majuscule / accent
            }
        }

        return result;
    }

    /**
     * Récupère une liste de label dans un fichier texte et les places dans une
     * liste
     *
     * @param filePath chemin vers le fichier texte contenant les labels à
     * ignorer
     * @return une liste de label
     * @throws FileNotFoundException
     * @throws IOException
     */
    static LinkedList<String> not_treated(String filePath) throws FileNotFoundException, IOException {
        LinkedList<String> result = new LinkedList<>();
        String line;
        try (BufferedReader buff = new BufferedReader(new FileReader(filePath))) {
            while ((line = buff.readLine()) != null) {
                result.add(line);
            }
        }
        return result;
    }
}
