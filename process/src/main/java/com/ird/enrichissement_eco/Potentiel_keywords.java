package com.ird.enrichissement_eco;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import static com.ird.enrichissement_eco.Utilitaires.filtrer_apostrophe;
import static com.ird.enrichissement_eco.Utilitaires.get_labels;
import static com.ird.enrichissement_eco.Utilitaires.get_resumes;
import static com.ird.enrichissement_eco.Utilitaires.get_titres;
import static com.ird.enrichissement_eco.Utilitaires.normalize;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.shingle.ShingleAnalyzerWrapper;
import org.apache.lucene.analysis.standard.ClassicTokenizer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

/**
 * Programme permettant la génération de la liste de mots clés potentielles. Il
 * utilise les outils fournis par Lucene afin de mener une analyse statisque des
 * mots présents dans les abstracts et les titres. Les StopWords (les mots vide
 * comme "la", "the", etc.) sont ceux utilisés par Lucene par défault. Il est
 * possible d'en ajouter à travers un fichier texte (1 mot par ligne). Les
 * n-grams (expressions à n mots) de taille deux et trois sont aussi repérés.
 *
 * @author jimmy
 */
public class Potentiel_keywords {

    /**
     * Extraction des mots les plus courants dans un fichier rdf précis Version
     * sans labels existants
     *
     * @param fileBiblio fichier rdf normé (idéalement généré par le programme)
     * @param path_blacklist Fichier contenant des mots vides de sens
     * @param out nom du fichier résultat
     * @throws IOException
     */
    public static void extraire_mots(String fileBiblio, String path_blacklist, String out) throws IOException {

        LinkedList<String> blacklist = construct_blacklist();
        Model ModelBiblio = ModelFactory.createDefaultModel();
        ModelBiblio.read(fileBiblio);

        if (!path_blacklist.equals("")) {
            enhance_blacklist(blacklist, path_blacklist); //A executer si l'on souhaite ajouter de nouveaux termes à la blacklist
        }

        //Configuration de l'indexation lucene
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_9);

        // Store the index in memory:
        Directory directory = new RAMDirectory();

        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_9, analyzer);
        IndexWriter iwriter = new IndexWriter(directory, config);

        //Indexation des titres
        Document doc_titre = new Document();
        String titre = get_titres(ModelBiblio); //Récupération de tous les titres du ModelBiblio
        doc_titre.add(new Field("resource", titre, TextField.TYPE_STORED));
        iwriter.addDocument(doc_titre);

        //Indexation des résumé
        Document doc_resume = new Document();
        String resume = get_resumes(ModelBiblio); //Récupération de tous les résumés
        doc_resume.add(new Field("resource", resume, TextField.TYPE_STORED));
        iwriter.addDocument(doc_resume);

        //Fin indexation
        iwriter.close();

        //Configuration de l'output
        File result_query = new File(out);
        PrintStream originalOut = new PrintStream(System.out);
        PrintStream printStream = new PrintStream(result_query);
        System.setOut(printStream);

        // Now search the index:
        DirectoryReader ireader = DirectoryReader.open(directory);
        Fields fields = MultiFields.getFields(ireader);
        Terms terms = fields.terms("resource");
        TermsEnum iterator = terms.iterator(null);
        BytesRef byteRef;
        //table faisant la correspondance : Racine de mots avec la table donnant pour chacune des extension de cette racine son nombre d'occurrences
        HashMap<String, HashMap<String, Integer>> map_root = new HashMap<>();
        //Table <Racine, nombres occurrences totales>
        HashMap<String, Integer> map_cpt = new HashMap<>();
        String root;
        int cpt;
        while ((byteRef = iterator.next()) != null) {
            String term = new String(byteRef.bytes, byteRef.offset, byteRef.length); //On récupère les entrées de l'index
            term = filtrer_apostrophe(term); //Si 2e char = ' alors on retire 2premieres lettres
            cpt = (int) iterator.totalTermFreq(); //Compte le nombre d'occurrence
            if ((!blacklist.contains(term)) && (!normalize(term).equals("")) && (term.length() != 1)) //Tri une partie des mots : contenant que des chiffres / taille 1
            {
                root = stemmize(term); //Retourne la racine d'un mot (ou le mot si il n'a pas réussi)
                if (!map_root.containsKey(root)) { //Si c'est la première fois que cette racine apparaît : on initialise les tables
                    map_root.put(root, new HashMap<String, Integer>());
                    map_cpt.put(root, 0);
                }
                map_root.get(root).put(term, cpt); //On ajoute le couple <mot, occurrences> à la premiere table
                map_cpt.put(root, map_cpt.get(root) + cpt); //on ajoute le nombre d'occurrence du mot au nombre d'occurrences totales de la racine
            }
        }
        ireader.close();
        directory.close();

        HashMap<String, Integer> map_ngrams = nGrams(ModelBiblio, blacklist); //Construction de la table <n-grams, nbre occurrences>

        affiche_map(map_root, map_cpt, map_ngrams); //affichage  des résultats
        System.setOut(originalOut);
    }

    /**
     * Extraction des mots les plus courants dans un fichier rdf précis Version
     * avec labels existants. Crée un fichier log.txt où sont écrits les mots
     * non-retenus car déjà labels sur le endpoint.
     *
     * @param fileBiblio fichier rdf normé (idéalement généré par le programme)
     * @param path_blacklist fichier contenant des mots vides de sens
     * @param service Adresse du service endpoint
     * @param out nom du fichier résultat
     * @throws IOException
     */
    public static void extraire_mots(String fileBiblio, String path_blacklist, String service, String out) throws IOException {

        LinkedList<String> blacklist = construct_blacklist();
        Model ModelBiblio = ModelFactory.createDefaultModel();
        ModelBiblio.read(fileBiblio);

        if (!path_blacklist.equals("")) {
            enhance_blacklist(blacklist, path_blacklist); //A executer si l'on souhaite ajouter de nouveaux termes à la blacklist
        }
        LinkedList<String> already_keywords = get_labels(service); //Requete sur le SPARQL Endpoint des notions déjà présente (les entrées sont normalizées)

        LinkedList<String> doublons = new LinkedList<>();

        //Configuration de l'indexation lucene
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_9);

        // Store the index in memory:
        Directory directory = new RAMDirectory();

        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_9, analyzer);
        IndexWriter iwriter = new IndexWriter(directory, config);

        //Indexation des titres
        Document doc_titre = new Document();
        String titre = get_titres(ModelBiblio); //Récupération de tous les titres du ModelBiblio
        doc_titre.add(new Field("resource", titre, TextField.TYPE_STORED));
        iwriter.addDocument(doc_titre);

        //Indexation des résumé
        Document doc_resume = new Document();
        String resume = get_resumes(ModelBiblio); //Récupération de tous les résumés
        doc_resume.add(new Field("resource", resume, TextField.TYPE_STORED));
        iwriter.addDocument(doc_resume);

        //Fin indexation
        iwriter.close();

        //Configuration de l'output
        File result_query = new File(out);
        PrintStream originalOut = new PrintStream(System.out);
        PrintStream printStream = new PrintStream(result_query);
        System.setOut(printStream);

        // Now search the index:
        DirectoryReader ireader = DirectoryReader.open(directory);
        Fields fields = MultiFields.getFields(ireader);
        Terms terms = fields.terms("resource");
        TermsEnum iterator = terms.iterator(null);
        BytesRef byteRef;
//table faisant la correspondance : Racine de mots avec la table donnant pour chacune des extension de cette racine son nombre d'occurrences
        HashMap<String, HashMap<String, Integer>> map_root = new HashMap<>();
        //Table <Racine, nombres occurrences totales>
        HashMap<String, Integer> map_cpt = new HashMap<>();
        String root;
        int cpt;
        while ((byteRef = iterator.next()) != null) {
            String term = new String(byteRef.bytes, byteRef.offset, byteRef.length); //On récupère les entrées de l'index
            term = filtrer_apostrophe(term); //Si 2e char = ' alors on retire 2premieres lettres
            cpt = (int) iterator.totalTermFreq(); //Compte le nombre d'occurrence
            if ((!blacklist.contains(term)) && (!already_keywords.contains(normalize(term))) && (!normalize(term).equals("")) && (term.length() != 1)) //Tri une partie des mots : contenant que des chiffres / taille 1
            {
                root = stemmize(term); //Retourne la racine d'un mot (ou le mot si il n'a pas réussi)
                if (!map_root.containsKey(root)) { //Si c'est la première fois que cette racine apparaît : on initialise les tables
                    map_root.put(root, new HashMap<String, Integer>());
                    map_cpt.put(root, 0);
                }
                map_root.get(root).put(term, cpt); //On ajoute le couple <mot, occurrences> à la premiere table
                map_cpt.put(root, map_cpt.get(root) + cpt); //on ajoute le nombre d'occurrence du mot au nombre d'occurrences totales de la racine
            }
            if ((!normalize(term).equals("")) && (already_keywords.contains(normalize(term))) && (!doublons.contains(term))) { //Si c'était déjà un mot clé dans le model, on l'ajoute à la liste doublons
                doublons.add(term);
            }
        }
        ireader.close();
        directory.close();

        HashMap<String, Integer> map_ngrams = nGrams(ModelBiblio, blacklist, doublons, service); //Construction de la table <n-grams, nbre occurrences>

        affiche_map(map_root, map_cpt, map_ngrams); //affichage  des résultats
        try {
            BufferedWriter buff = new BufferedWriter(new FileWriter("doublons.txt"));

            try {
                for (String s : doublons) {
                    buff.write(s); //Ecriture des doublons (attentions, pas encore les n-grams)
                    buff.newLine();
                }
            } finally {
                buff.close();
            }
        } catch (IOException ioe) {
            System.out.println("Erreur --" + ioe.toString());
        }
        System.setOut(originalOut);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        String path = System.getProperty("user.dir");
        String addsPath = path + "/inputs/adds.txt"; //Adresse des mots à y ajouter
        String service = "http://ecoscopebc.mpl.ird.fr:8080/joseki/ecoscope";
        String refBiblio = path + "/inputs/Biblio_ext.rdf"; //Chargement du model
        String out = path + "/outputs/potential_keywords.txt";
        Model ModelBiblio = ModelFactory.createDefaultModel();
        ModelBiblio.read("file:" + refBiblio);
        try {
            extraire_mots(refBiblio, addsPath, service, out);
        } catch (IOException ex) {
            Logger.getLogger(Potentiel_keywords.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Génère une table "String n-gram, Integer compteur d'occurrences" avec les
     * n-grams de taille 2 et 3 contenus dans les Résumés et les titres de
     * ModelBiblio. Un n-gram n'est retenu que s'il est nouveau et s'il ne
     * contient aucun mot de la blacklist
     *
     * @param ModelBiblio Modèle contenant les références bibliographiques
     * @param blacklist Liste des StopWords (mots vide de sens)
     * @return Une table <n-grams, nbre occurrences>
     * @throws IOException
     */
    static HashMap<String, Integer> nGrams(Model ModelBiblio, LinkedList<String> blacklist) throws IOException {
        HashMap<String, Integer> map = new HashMap<>();
        String token;
        String titres = get_titres(ModelBiblio); //Récupération des titres 
        String resumes = get_resumes(ModelBiblio); //Récupération des abstracts 
        StringReader reader_t = new StringReader(titres);
        StringReader reader_r = new StringReader(resumes);

        // Parse the file into n-gram tokens
        SimpleAnalyzer simpleAnalyzer = new SimpleAnalyzer(Version.LUCENE_4_9);
        ShingleAnalyzerWrapper shingleAnalyzer = new ShingleAnalyzerWrapper(simpleAnalyzer, 2, 3); //Tokenize sur une fenêtre de taille 2 et 3

        TokenStream stream = shingleAnalyzer.tokenStream("contents", reader_t); //Analyse des titres
        CharTermAttribute charTermAttribute = stream.getAttribute(CharTermAttribute.class);

        stream.reset();
        while (stream.incrementToken()) { //Récupération de chaque tokens
            token = charTermAttribute.toString();
            if (token.split(" ").length > 1) {
                //Si aucune mots du token n'appartient à la blaclist et la notion est nouvelle (pas dans le endpoint) on ajoute une occurrence
                if (!is_blacklisted(token, blacklist)) {
                    if (!map.containsKey(token)) {
                        map.put(token, 1);
                    } else {
                        map.put(token, map.get(token) + 1);
                    }
                }
            }
        }
        stream.close();

        stream = shingleAnalyzer.tokenStream("contents", reader_r); //Même chose sur les abstracts
        charTermAttribute = stream.getAttribute(CharTermAttribute.class);
        stream.reset();
        while (stream.incrementToken()) {
            token = charTermAttribute.toString();
            if (token.split(" ").length > 1) {
                if (!is_blacklisted(token, blacklist)) {
                    if (!map.containsKey(token)) {
                        map.put(token, 1);
                    } else {
                        map.put(token, map.get(token) + 1);
                    }
                }
            }
        }
        stream.close();
        return map;
    }

    /**
     * Génère une table "String n-gram, Integer compteur d'occurrences" avec les
     * n-grams de taille 2 et 3 contenus dans les Résumés et les titres de
     * ModelBiblio. Un n-gram n'est retenu que s'il est nouveau et s'il ne
     * contient aucun mot de la blacklist
     *
     * @param ModelBiblio Modèle contenant les références bibliographiques
     * @param blacklist Liste des StopWords (mots vide de sens)
     * @param doublons Liste qui étaient déjà présente dans le endpoint et qui
     * donc ne sont pas rajoutées
     * @param service adresse du service endpoint
     * @return Une table <n-grams, nbre occurrences>
     * @throws IOException
     */
    static HashMap<String, Integer> nGrams(Model ModelBiblio, LinkedList<String> blacklist, LinkedList<String> doublons, String service) throws IOException {
        HashMap<String, Integer> map = new HashMap<>();
        String token;
        String titres = get_titres(ModelBiblio); //Récupération des titres 
        String resumes = get_resumes(ModelBiblio); //Récupération des abstracts 
        LinkedList<String> already_keywords = get_labels(service); //Requete sur le SPARQL Endpoint des notions déjà présente (les entrées sont normalisées
        StringReader reader_t = new StringReader(titres);
        StringReader reader_r = new StringReader(resumes);

        // Parse the file into n-gram tokens
        SimpleAnalyzer simpleAnalyzer = new SimpleAnalyzer(Version.LUCENE_4_9);
        ShingleAnalyzerWrapper shingleAnalyzer = new ShingleAnalyzerWrapper(simpleAnalyzer, 2, 3); //Tokenize sur une fenêtre de taille 2 et 3

        TokenStream stream = shingleAnalyzer.tokenStream("contents", reader_t); //Analyse des titres
        CharTermAttribute charTermAttribute = stream.getAttribute(CharTermAttribute.class);

        stream.reset();
        while (stream.incrementToken()) { //Récupération de chaque tokens
            token = charTermAttribute.toString();
            if (token.split(" ").length > 1) {
                //Si aucune mots du token n'appartient à la blaclist et la notion est nouvelle (pas dans le endpoint) on ajoute une occurrence
                if (!is_blacklisted(token, blacklist) && (!already_keywords.contains(normalize(token)))) {
                    if (!map.containsKey(token)) {
                        map.put(token, 1);
                    } else {
                        map.put(token, map.get(token) + 1);
                    }
                } else {
                    if ((!normalize(token).equals("")) && (already_keywords.contains(normalize(token))) && (!doublons.contains(token))) { //Si c'était déjà un mot clé dans le model, on l'ajoute à la liste doublons
                        doublons.add(token);
                    }
                }
            }
        }
        stream.close();

        stream = shingleAnalyzer.tokenStream("contents", reader_r); //Même chose sur les abstracts
        charTermAttribute = stream.getAttribute(CharTermAttribute.class);
        stream.reset();
        while (stream.incrementToken()) {
            token = charTermAttribute.toString();
            if (token.split(" ").length > 1) {
                if (!is_blacklisted(token, blacklist) && (!already_keywords.contains(normalize(token)))) {
                    if (!map.containsKey(token)) {
                        map.put(token, 1);
                    } else {
                        map.put(token, map.get(token) + 1);
                    }
                }
            } else {
                if ((!normalize(token).equals("")) && (already_keywords.contains(normalize(token))) && (!doublons.contains(token))) { //Si c'était déjà un mot clé dans le model, on l'ajoute à la liste doublons
                    doublons.add(token);
                }
            }
        }
        stream.close();
        return map;
    }

    /**
     * Retrouve la racine d'un mot, si aucune (ou plusieurs) retourne le mot
     *
     * @param term Le mot dont on cherche la racine
     * @return La racine si c'est possible, sinon le mot
     * @throws IOException
     */
    public static String stemmize(String term) throws IOException {

        // tokenize term
        TokenStream tokenStream = new ClassicTokenizer(Version.LUCENE_4_9, new StringReader(term));
        // stemmize
        tokenStream = new PorterStemFilter(tokenStream);

        Set<String> stems = new HashSet<>();
        CharTermAttribute token = tokenStream.getAttribute(CharTermAttribute.class);
        // for each token
        tokenStream.reset();
        while (tokenStream.incrementToken()) {
            // add it in the dedicated set (to keep unicity)
            stems.add(token.toString());
        }
        tokenStream.close();
        // if no stem or 2+ stems have been found, return null----> return the word
        if (stems.size() != 1) {
            return term;
        }

        String stem = stems.iterator().next();

        // if the stem has non-alphanumerical chars, return ----> return the word
        if (!stem.matches("[\\w-]+")) {
            return term;
        }
        return stem;
    }

    /**
     * Converti un tableau de char en une String
     *
     * @param in tableau de char
     * @return String
     */
    public static String charsToString(char[] in) {
        String out = "";
        for (char c : in) {
            out += c;
        }
        return out;
    }

    /**
     * Construit la liste de StopWords basique de lucene pour l'Anglais, le
     * Français et l'Espagnol
     *
     * @return Une liste de StopWords
     */
    public static LinkedList<String> construct_blacklist() {
        LinkedList<String> res = new LinkedList<>();
        char[] mot;
        CharArraySet stopSet = EnglishAnalyzer.getDefaultStopSet(); //StopWords par défault de lucene pour l'Anglais
        Iterator iter = stopSet.iterator();
        while (iter.hasNext()) {
            mot = (char[]) iter.next();
            res.add(charsToString(mot));
        }
        stopSet = FrenchAnalyzer.getDefaultStopSet(); //StopWords par défault de lucene pour le Français
        iter = stopSet.iterator();
        while (iter.hasNext()) {
            mot = (char[]) iter.next();
            res.add(charsToString(mot));
        }
        stopSet = SpanishAnalyzer.getDefaultStopSet(); //StopWords par défault de lucene pour l'Espagnol
        iter = stopSet.iterator();
        while (iter.hasNext()) {
            mot = (char[]) iter.next();
            res.add(charsToString(mot));
        }
        return res;
    }

    /**
     * Rempli un fichier des mots à ne pas retenir à partir des mots par défault
     * de lucene
     *
     * @param filePath fichier à remplir
     */
    public static void construct_blacklist(String filePath) {

        try {
            BufferedWriter buff = new BufferedWriter(new FileWriter(filePath));

            try {
                char[] mot;
                CharArraySet stopSet = EnglishAnalyzer.getDefaultStopSet(); //StopWords par défault de lucene pour l'Anglais
                Iterator iter = stopSet.iterator();
                while (iter.hasNext()) {
                    mot = (char[]) iter.next();
                    buff.write(mot);
                    buff.newLine();
                }
                stopSet = FrenchAnalyzer.getDefaultStopSet(); //StopWords par défault de lucene pour le Français
                iter = stopSet.iterator();
                while (iter.hasNext()) {
                    mot = (char[]) iter.next();
                    buff.write(mot);
                    buff.newLine();
                }
                stopSet = SpanishAnalyzer.getDefaultStopSet(); //StopWords par défault de lucene pour l'Espagnol
                iter = stopSet.iterator();
                while (iter.hasNext()) {
                    mot = (char[]) iter.next();
                    buff.write(mot);
                    buff.newLine();
                }

            } finally {
                buff.close();
            }
        } catch (IOException ioe) {
            System.out.println("Erreur --" + ioe.toString());
        }

    }

    /**
     * Lit un fichier afin d'augmenter la liste des mots à ne pas retenir du
     * fichier path_current
     *
     * @param path_current Chemin vers la blacklist actuelle
     * @param path_adds Chemin vers la liste des mots à ajouter
     */
    public static void enhance_blacklist(String path_current, String path_adds) {
        LinkedList<String> current = get_blacklist(path_current);
        LinkedList<String> adds = get_blacklist(path_adds);
        try {
            BufferedWriter buff = new BufferedWriter(new FileWriter(path_current, true));
            try {
                for (String s : adds) { //Pour chacun des mots à ajouter
                    if (!current.contains(s)) { //Si ils n'existent pas déjà dans la blacklist, on les ajoute
                        buff.write(s);
                        buff.newLine();
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
     * Lit un fichier afin d'augmenter la liste des mots à ne pas retenir de la
     * liste current
     *
     * @param current Liste des mots à ne pas retenir déjà présents
     * @param path_adds Chemin vers la liste des mots à ajouter
     */
    public static void enhance_blacklist(LinkedList<String> current, String path_adds) {
        LinkedList<String> adds = get_blacklist(path_adds);
        for (String s : adds) { //Pour chacun des mots à ajouter
            if (!current.contains(s)) { //Si ils n'existent pas déjà dans la blacklist, on les ajoute
                current.add(s);
            }
        }
    }

    /**
     * Ouvre un fichier afin de récupérer la liste des mots à ne pas retenir
     *
     * @param filePath chemin du fichier
     * @return Une liste des mots à filtrer
     */
    public static LinkedList<String> get_blacklist(String filePath) {
        String line;
        LinkedList<String> result = new LinkedList<>();
        try {
            BufferedReader buff = new BufferedReader(new FileReader(filePath));
            try {
                while ((line = buff.readLine()) != null) { //Lecture ligne à ligne
                    result.add(line);
                }
            } finally {
                buff.close();
            }
        } catch (IOException ioe) {
            System.out.println("Erreur --" + ioe.toString());
        }
        return result;
    }

    /**
     * Permet de savoir si un N-Gram contient un mot appartenant à la blacklist
     *
     * @param token N-Gram à analyser
     * @param blacklist Liste des termes vides de sens
     * @return True si un des termes du N-Gram appartient à la blacklist
     */
    static boolean is_blacklisted(String token, LinkedList<String> blacklist) {
        boolean result = false;
        String[] tokens = token.split(" ");
        for (String t : tokens) { //Pour chaque mot contenu dans le n-gram
            if (blacklist.contains(t)) { //Si il en existe un qui appartient à la blacklist, on retour vraie
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Affichage des maps par ordre décroissant du nombre d'occurrences
     *
     * @param map_root Table faisant le lien entre une racine et chacun de ses
     * mots avec leur nombre d'occurrences
     * @param map_cpt Table faisant le lien entre une racine et son nombre total
     * d'occurrences
     * @param map_ngrams Table faisant le lien entre un n-gram et son nombre
     * d'occurrences
     */
    static void affiche_map(HashMap<String, HashMap<String, Integer>> map_root, HashMap<String, Integer> map_cpt, HashMap<String, Integer> map_ngrams) {
        int cpt_max = 0;
        //Récupération du mot clé (ou n-grams) le plus fréquent
        for (Map.Entry<String, Integer> entry : map_cpt.entrySet()) {
            if (entry.getValue() > cpt_max) {
                cpt_max = entry.getValue();
            }
        }
        for (Map.Entry<String, Integer> entry : map_ngrams.entrySet()) {
            if (entry.getValue() > cpt_max) {
                cpt_max = entry.getValue();
            }
        }
        String root;
        String word;
        int cpt;
        int i = cpt_max;
        HashMap<String, Integer> m;
        //Parcours en ordre décroissant du nombre d'occurrences
        while (i > 0) {
            //Si une entrée possède le nombre d'occurrences recherché, on l'affiche
            for (Map.Entry<String, Integer> entry : map_ngrams.entrySet()) {
                if (entry.getValue() == i) {
                    System.out.println(entry.getKey() + " : " + entry.getValue() + "\n");
                }
            }
            //Si ce n'est pas un n-gram il faut affichier de la forme : 
            // Root : nombre d'occurrences de la racine
            //        Mot1 : nombre d'occurences du mot1
            //        Mot2 : nombre d'occurrences du mot2
            for (Map.Entry<String, Integer> entry : map_cpt.entrySet()) {
                cpt = entry.getValue();
                if (cpt == i) {
                    root = entry.getKey();
                    System.out.println(root + " : " + cpt);
                    m = map_root.get(root);
                    for (Map.Entry<String, Integer> entry3 : m.entrySet()) {
                        word = entry3.getKey();
                        cpt = entry3.getValue();
                        System.out.println("\t" + word + " : " + cpt);
                    }
                    System.out.print("\n");
                }
            }
            --i;
        }
    }

}
