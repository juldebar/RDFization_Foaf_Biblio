package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * Deuxième étape dans le traitement des mots clés, permet de choisir quels mots
 * mérite d'être ajoutés aux models
 *
 * Voir l'action du bouton "Aide" pour plus de détails sur cette fenêtre.
 * @author jimmy
 */
public class Adds extends Basic_Frame implements Observable, ActionListener {

    /**
     * Creates new form adds
     *
     * @param file
     */
    public Adds(File file) {
        try {
            initComponents(file);
        } catch (IOException ex) {
            Logger.getLogger(Adds.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Initalisation des composants de la fenêtre
     *
     * @param param fichier passé lors de l'étape précédente
     * @throws IOException
     */
    private void initComponents(File param) throws IOException {

        this.setTitle("Manipulation des mots clés");

        //Lorsque l'on ferme => on appelle la fonction exit()
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });

        file = param;
        zoneRacine = new javax.swing.JTextPane();
        CheckBox = new javax.swing.JCheckBox();
        Terminer = new javax.swing.JButton();
        Retour = new javax.swing.JButton();
        Precedent = new javax.swing.JButton();
        Suivant = new javax.swing.JButton();
        LabelCpt = new javax.swing.JLabel();
        ScrollPane = new javax.swing.JScrollPane();
        zoneMots = new javax.swing.JTextPane();
        mot_courant = 0;
        list = init_list(file); //initialisation de la liste des mots à partir du fichier
        saved_list = new LinkedList<>(); //On sauvegarde la liste
        for (LinkedList<String> entry : list) {
            saved_list.add(entry);
        }
        deja_choisi = new LinkedList<>();
        //Une entrée par racine, donc size de list donne le nombre de racine
        total_mots = list.size();

        zoneRacine.setEditable(false);
        zoneMots.setEditable(false);

        map_cpt = new HashMap<>();
        map_mots = new HashMap<>();

        setLabels(mot_courant);

        CheckBox.setText("Choisir ce mot");

        Terminer.setText("Terminer");
        Terminer.addActionListener(this);

        Retour.setText("Retour");
        Retour.addActionListener(this);

        Precedent.setText("Mot précédent");
        Precedent.addActionListener(this);

        Suivant.setText("Mot suivant");
        Suivant.addActionListener(this);

        zoneMots.setText("Liste des mots sélectionnés jusqu'à présent :");
        ScrollPane.setViewportView(zoneMots);

        item_reinit.addActionListener(this);
        item_help.addActionListener(this);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());

        getContentPane()
                .setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(Retour, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 78, Short.MAX_VALUE)
                        .addComponent(Precedent)
                        .addGap(52, 52, 52)
                        .addComponent(LabelCpt)
                        .addGap(50, 50, 50)
                        .addComponent(Suivant)
                        .addGap(102, 102, 102)
                        .addComponent(Terminer, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(zoneRacine, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(CheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(zoneRacine, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(ScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE))
                                        .addGap(18, 18, 18))
                                .addGroup(layout.createSequentialGroup()
                                        .addGap(106, 106, 106)
                                        .addComponent(CheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(LabelCpt, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(Precedent)
                                .addComponent(Retour)
                                .addComponent(Terminer)
                                .addComponent(Suivant)))
        );

        pack();

        this.setVisible(true);
    }

    // Variables declaration - do not modify 
    private boolean is_editor = false;  //Permet de savoir si l'on est passé à l'editeur ou non
    private javax.swing.JCheckBox CheckBox; //Sélection ou non du mot
    private javax.swing.JLabel LabelCpt; //Label indiquant le n° du mot actuel / le nombre de mot total
    private javax.swing.JTextPane zoneMots; //Liste les mots déjà sélectionnés
    private javax.swing.JButton Precedent; //Retourne au mot précédent (le dernier si l'on est au premier)
    private javax.swing.JButton Retour;   //Retourne à l'étape de sélection des fichiers
    private javax.swing.JScrollPane ScrollPane; //Permet à zoneMots d'être "scrollable"
    private javax.swing.JButton Suivant; //Passe au mot suivant
    private javax.swing.JButton Terminer; //Passe à l'étape suivante
    private File file; //Fichier entré à l'étape précédente
    private javax.swing.JTextPane zoneRacine; //indique le mot courant et ses occurrences
    private int mot_courant; //Indice du mot courant
    private int total_mots; // Nombre de mots au total
    private LinkedList<LinkedList<String>> list; //Liste des mots clés sélectionnés (les mots clés sont stockés dans une liste particulière :
    //1ere element = racine, 2e element = occurrences racine. Pour les autres on a la suite : mot et élément suivant occurrences de ce mot
    private LinkedList<LinkedList<String>> saved_list; //Contient la liste de départ, evite un nouveau parcours à chaque fois.
    private LinkedList<Integer> deja_choisi; //Contient les indices des mots déjà choisis
    private HashMap<String, Integer> map_cpt;//Contient racine / occurrences
    private HashMap<String, HashMap<String, Integer>> map_mots;//Contient racine - (mots / occurrences)
    private ArrayList<Observateur> listObservateur = new ArrayList<>(); //Liste des Observateurs
    // End of variables declaration                   

    /**
     * Met à jour le label où apparait les mots et le label compteur en fonction
     * du numéro du mot à étudier
     *
     * @param racine
     */
    private void setLabels(int racine) {
        LabelCpt.setText("Mot n°" + (racine + 1) + " / " + total_mots);
        LinkedList<String> line = list.get(racine);
        String label = line.get(0) + " :  " + line.get(1) + "\n\n";
        int i = 2;
        while (i < line.size()) {
            label += "\t" + line.get(i) + " : " + line.get(i + 1) + "\n";
            i += 2;
        }
        zoneRacine.setText(label);
    }

    @Override
    public void actionPerformed(ActionEvent arg) {

        if (arg.getSource() == Retour) {
            exit();
        }

        //Passe à l'étape suivante
        if (arg.getSource() == Terminer) {
            //Si le mot est sélectionné, on l'ajoute (si il n'était pas déjà ajouté)
            if (CheckBox.isSelected()) {
                if (!deja_choisi.contains(mot_courant)) {
                    add_entry(mot_courant);
                    deja_choisi.add(mot_courant);
                }
            }
            //Si il n'y a pas de fenêtre de l'étape 3 d'ouverte, on en ouvre une et on l'écoute
            if (!is_editor) {
                Editor editor = new Editor(map_cpt, map_mots);
                is_editor = true;
                this.setVisible(false); //on cache cette fenetre tant que l'autre est active
                //On place un écouteur
                editor.addObservateur(new Observateur() {
                    @Override
                    public void update() {
                        is_editor = false;
                        affiche(); //Quand on quitte la fenêtre de l'étape 3, on ré-affiche celle-ci
                    }
                });
            }
        }
        //Ajout du mot si nécessaire, passage au mot suivant
        if (arg.getSource() == Suivant) {
            if (CheckBox.isSelected()) {
                if (!deja_choisi.contains(mot_courant)) {
                    add_entry(mot_courant);
                    deja_choisi.add(mot_courant);
                }
            }
            if (mot_courant == total_mots - 1) {
                mot_courant = 0;
            } else {
                ++mot_courant;
            }
            setLabels(mot_courant);
            CheckBox.setSelected(false);
        }
        //Ajout du mot si nécessaire, passage au mot précédent
        if (arg.getSource() == Precedent) {
            if (CheckBox.isSelected()) {
                if (!deja_choisi.contains(mot_courant)) {
                    add_entry(mot_courant);
                    deja_choisi.add(mot_courant);
                }
            }
            if (mot_courant == 0) {
                mot_courant = total_mots - 1;
            } else {
                --mot_courant;
            }
            setLabels(mot_courant);
            CheckBox.setSelected(false);
        }

        if (arg.getSource() == item_reinit) {
            re_init();
            String msg = "Retour au début du fichier\n";
            JOptionPane.showMessageDialog(null, msg, "ré-initialisation", JOptionPane.INFORMATION_MESSAGE);
        }

        if (arg.getSource() == item_help) {
            String msg
                    = "Cette fenêtre est la deuxième étape de l'action permettant d'exploiter la liste des mots clés.\n\n"
                    + "Les entrées du fichier sont présentées une à une sous la forme :\n"
                    + "Racine : nombre occurrences total de la racine\n"
                    + "      mot dérivé n°1 : occurrences\n"
                    + "      mot dérivé n°2 : occurrences\n"
                    + "      etc.\n\n"
                    + "Pour ajouter un mot à la liste de ceux à traiter, cocher la case \"Choisir ce mot\".\n"
                    + "La liste des mots déjà choisis apparaît sur la fenêtre de droite. \n"
                    + "\nIl est toujours possible de revenir en arrière pour sélectionner un mot oublié,\n"
                    + "par contre il n'est pas possible de l'enlever de la liste. Mais pas de panique,\n"
                    + "il suffira de l'ignorer à l'étape d'après.\n"
                    + "\nUne fois la liste complète, appuyez sur \"Terminer\" afin de passer à l'étape suivante.\n"
                    + "\nNote : Il est possible d'enlever tous les mots en appuyant sur \"F2\"";
            JOptionPane.showMessageDialog(null, msg, "Aide", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Fonction de ré-initialisation de la fenetre. Vide les listes crées
     * jusqu'à présent et remet la fenetre à la position 0
     */
    private void re_init() {
        mot_courant = 0;
        setLabels(mot_courant);
        zoneMots.setText("Liste des mots sélectionnés jusqu'à présent :");
        list = saved_list;
        deja_choisi = new LinkedList<>();
        map_cpt = new HashMap<>();
        map_mots = new HashMap<>();
    }

    //Ajoute un observateur à la liste
    @Override
    public void addObservateur(Observateur obs) {
        this.listObservateur.add(obs);
    }

    //Retire tous les observateurs de la liste
    @Override
    public void delObservateur() {
        this.listObservateur = new ArrayList<>();
    }

    //Avertit les observateurs que l'objet observable a changé 
    //et invoque la méthode update() de chaque observateur
    @Override
    public void updateObservateur() {
        for (Observateur obs : this.listObservateur) {
            obs.update();
        }
    }

    /**
     * Remplace la fonction de base : permet d'update les observateurs avant de
     * quitter.
     */
    public void exit() {
        updateObservateur();
        dispose();
    }

    /**
     * Lit le fichier et le retourne sous une forme particulière, pour chaque
     * racine, retour : - 0 : racine - 1 : nbre occurrences racine - 2, 4, ...,
     * 2n : mots - 3, 5, ..., 2n+1 : nombres occurrences mots
     *
     * @param file fichier retourné à l'étape d'extraction de mot clé potentiels
     * @return Un tableau de String pour chaque entrée du fichier
     */
    private LinkedList<LinkedList<String>> init_list(File file) throws FileNotFoundException, IOException {
        LinkedList<LinkedList<String>> result = new LinkedList<>();
        BufferedReader buff = new BufferedReader(new FileReader(file));
        LinkedList<String> lines = new LinkedList<>(); //Contient les lignes non-vides du fichier
        String line;
        String[] tokens;
        while ((line = buff.readLine()) != null) {
            if (is_not_empty(line)) {
                lines.add(line);
            }
        }
        buff.close();

        //Parcours des lignes
        int i = 0;
        while (i < lines.size()) {
            LinkedList<String> elements = new LinkedList<>();
            line = lines.get(i); //Toujours une racine (vrai pour i = 0, et la 2e boucle while assure que c'est vrai pour tout autre i)
            tokens = line.split(":"); //Tokenization grace au ":"
            elements.add(tokens[0]); //Toujours la racine 
            elements.add(enlever_espace(tokens[1])); //Toujours le nombre d'occurrences de la racine
            ++i;
            //On va chercher tous les mots pour cette racine -> Parcours de la liste tant que l'on n'est pas à une nouvelle racine 
            //Les mots sont différenciés des racines par le fait qu'ils commencent par une tabulation
            while ((i < lines.size()) && ((line = lines.get(i)).charAt(0) == ('\t'))) {
                tokens = line.split(":");
                elements.add(tokens[0]); //Toujours un mot
                elements.add(enlever_espace(tokens[1])); //Toujours son occurrence
                ++i;
            }
            result.add(elements);
        }
        return result;
    }

    /**
     *
     * @param line Une ligne (chaine de caractère sans \n)
     * @return True si la ligne contient autre chose que des espaces ou des
     * tabulations, false sinon
     */
    public static boolean is_not_empty(String line) {
        char c;
        int i = 0;
        while (i < line.length()) {
            c = line.charAt(i);
            if ((c != '\t') && (c != ' ')) {
                return true;
            }
            ++i;
        }
        return false;
    }

    /**
     * Met à jours la table avec les mots sélectionnés et ajoute la racine au
     * label LabelMots
     *
     * @param entry
     */
    private void add_entry(int entry) {

        LinkedList<String> line = list.get(entry);
        String racine = line.get(0);
        int cpt = Integer.parseInt(line.get(1));
        map_cpt.put(racine, cpt);

        map_mots.put(racine, new HashMap<String, Integer>());
        int i = 2;//Les deux premieres String sont la racine et son nombre d'occurence
        String mot;
        while (i < line.size()) {
            mot = line.get(i);
            cpt = Integer.parseInt(line.get(i + 1));
            map_mots.get(racine).put(mot, cpt);
            i += 2;
        }
        String label = zoneMots.getText();
        label += "\n- " + racine;
        zoneMots.setText(label);

    }

    /**
     * Retire tous les espaces / tabulations / saut de ligne d'une string
     *
     * @param string Une chaine de caractère quelconque
     * @return la chaine de caractère sans les espaces
     */
    private String enlever_espace(String string) {
        int i = 0;
        char c;
        String res = "";
        while (i < string.length()) {
            c = string.charAt(i);
            if ((c != ' ') && (c != '\t') && (c != '\n')) {
                res += c;
            }
            ++i;
        }
        return res;
    }

    /**
     * Affiche la fenetre.
     */
    public void affiche() {
        this.setVisible(true);
    }
}
