package gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Fênetre principale du programme, permet de choisir l'opération a effectuer.
 * Contient notemment le main permettant l'execution du programme.
 *
 * "Vous êtes ici sur le panneau d'accueil de l'application. 6 choix vous sont
 * proposés : (1)- Générer un fichier Agents à partir d'un fichier csv : Permet,
 * à partir d'un fichier csv bien formé de produire un fichier rdf contenant les
 * informations relatives aux agents du laboratoire. (2)- Créer du lien entre
 * une bibliographie Zotero et un fichier Agents : Permet, à patrtir d'un
 * fichier rdf issu de Zotero (export en format Bibliontologie RDF) et un
 * fichier RDF portant sue les agents (idéalement produit par l'étape n°1)
 * d'extraire les articles rédigés par les agents du laboratoire et d'établir le
 * lien "auteur" a été écrit par" (3)- Générer les mots clés candidats : Permet,
 * à partir d'un fichier produit par l'étape n°2, de produire une liste de mots
 * candidats à devenir mots clés du domaine. Il est possible d'ajouter des mots
 * à la StopList (mots non-pertinents) ou bien de préciser un modèle dans lequel
 * se trouve déjà des mots clés (fichiers rdf_3max_ ou requête Sparql Endpoint.
 * (4)- Ajouter les mots clés aux models : Permet, à partir d'une liste de mots
 * clés candidats (générées à l'étape n°3), de proposer une interface afin de
 * les ajouter aux fichiers rdf concernés. (5)- Annotation des mots clés :
 * Permet de créer des tags sur les articles en fonction des mots clés présents
 * dans un ou des modèles. Il est proposer d'entrer un fichier de bibliographie
 * (produit à l'étape n°2), si le champs est vide le programme utilisera le
 * serveur endpoint. Il est aussi proposer d'entrer une liste de modèles
 * contenant les mots clés. Si ces champs sont vides, le "programme utilisera le
 * serveur endpoint. Enfin, il existe une possibilité d'entrer un fichier
 * contenant les mots à ne pas tag, même s'ils apparaissent dans les articles.
 * (6)- Exécution complète : Permet l'exécution successive des 5 étapes
 * précédentes.
 *
 * @author jimmy benoits
 */
public class MainFrame extends Basic_Frame implements ActionListener {

    //Partie bouton => lance les fenêtres pour effectuer les actions
    JButton button_csv;
    JButton button_lien;
    JButton button_potentielKeywords;
    JButton button_addKeywords;
    JButton button_useKeywords;
    JButton button_fullExe;

    //Contrôle des fenêtres ouvertes => empêche d'avoir deux fois la même fenêtre active
    boolean is_t1;
    boolean is_t2;
    boolean is_t3;
    boolean is_t4;
    boolean is_t5;
    boolean is_t6;

    //Layout
    GridLayout layout;

    public MainFrame() {

        //Configuration de base du endpoint
        endpoint = "http://ecoscopebc.mpl.ird.fr:8080/joseki/ecoscope";

        //initialisation des contrôles
        is_t1 = false;
        is_t2 = false;
        is_t3 = false;
        is_t4 = false;
        is_t5 = false;
        is_t6 = false;

        //Caractéristiques de la fenêtre
        this.setTitle("Enrichissement Ecoscope");
        this.setSize(700, 500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Layout
        layout = new GridLayout(3, 2);
        layout.setHgap(3);
        layout.setVgap(3);
        this.setLayout(layout);

        //Initialisation des boutons
        button_csv = new JButton("Générer un fichier Agents.rdf à partir d'un fichier csv");
        this.getContentPane().add(button_csv);
        button_lien = new JButton("Créer du lien entre une bibliographie Zotero et un fichier agent");
        this.getContentPane().add(button_lien);
        button_potentielKeywords = new JButton("Générer les mots clés candidats");
        this.getContentPane().add(button_potentielKeywords);
        button_addKeywords = new JButton("Ajouter les mots clés aux models");
        this.getContentPane().add(button_addKeywords);
        button_useKeywords = new JButton("Annotation des mots clés");
        this.getContentPane().add(button_useKeywords);
        button_fullExe = new JButton("Exécution complète");
        this.getContentPane().add(button_fullExe);

        //Listeners pour les boutons du panel
        button_csv.addActionListener(this);
        button_lien.addActionListener(this);
        button_potentielKeywords.addActionListener(this);
        button_addKeywords.addActionListener(this);
        button_useKeywords.addActionListener(this);
        button_fullExe.addActionListener(this);

        //Listener des boutons d'aide et de ré-initialisation. Leurs actions
        //sont différentes dans chaque fenêtre et ne peuvent donc pas être
        //dérivées de la super classe
        item_reinit.addActionListener(this);
        item_help.addActionListener(this);

        //Fin du constructeur
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent arg) {
        //Partie Bouton du Panel
        if (arg.getSource() == button_csv) {
            //Si la fenêtre de l'action 1 n'est pas ouverte, on l'ouvre
            if (!is_t1) {
                CsvToRdf t1 = new CsvToRdf();
                is_t1 = true; //Empêche de futur ouverture
                //On place un écouteur
                t1.addObservateur(new Observateur() {
                    @Override
                    public void update() {//Lorsque t1 se quittera, il lancera cette fonction
                        is_t1 = false;
                    }
                });
            }
        }
        //Même opération pour action 2 etc.
        if (arg.getSource() == button_lien) {
            if (!is_t2) {
                LienZotero t2 = new LienZotero();
                is_t2 = true;
                //On place un écouteur 
                t2.addObservateur(new Observateur() {
                    @Override
                    public void update() {
                        is_t2 = false;
                    }
                });
            }
        }
        if (arg.getSource() == button_potentielKeywords) {
            if (!is_t3) {
                Mots_candidats t3 = new Mots_candidats();
                is_t3 = true;
                //On place un écouteur
                t3.addObservateur(new Observateur() {
                    @Override
                    public void update() {
                        is_t3 = false;
                    }
                });
            }
        }
        if (arg.getSource() == button_addKeywords) {
            if (!is_t4) {
                Ajouter_labels t4 = new Ajouter_labels();
                is_t4 = true;
                //On place un écouteur
                t4.addObservateur(new Observateur() {
                    @Override
                    public void update() {
                        is_t4 = false;
                    }
                });
            }
        }
        if (arg.getSource() == button_useKeywords) {
            if (!is_t5) {
                Annotation t5 = new Annotation();
                is_t5 = true;
                //On place un écouteur
                t5.addObservateur(new Observateur() {
                    @Override
                    public void update() {
                        is_t5 = false;
                    }
                });
            }
        }
        //Toutes les opérations les unes à la suite des autres
        if (arg.getSource() == button_fullExe) {
            if (!is_t6) {
                if (!is_t1) {
                    CsvToRdf t1 = new CsvToRdf();
                    is_t1 = true;
                    //On place un écouteur
                    t1.addObservateur(new Observateur() {
                        @Override
                        public void update() {
                            is_t1 = false;
                            if (!is_t2) {
                                LienZotero t2 = new LienZotero();
                                is_t2 = true;
                                //On place un écouteur
                                t2.addObservateur(new Observateur() {
                                    @Override
                                    public void update() {
                                        is_t2 = false;
                                        if (!is_t3) {
                                            Mots_candidats t3 = new Mots_candidats();
                                            is_t3 = true;
                                            //On place un écouteur
                                            t3.addObservateur(new Observateur() {
                                                @Override
                                                public void update() {
                                                    is_t3 = false;
                                                    if (!is_t4) {
                                                        Ajouter_labels t4 = new Ajouter_labels();
                                                        is_t4 = true;
                                                        //On place un écouteur
                                                        t4.addObservateur(new Observateur() {
                                                            @Override
                                                            public void update() {
                                                                is_t4 = false;
                                                                if (!is_t5) {
                                                                    Annotation t5 = new Annotation();
                                                                    is_t5 = true;
                                                                    //On place un écouteur
                                                                    t5.addObservateur(new Observateur() {
                                                                        @Override
                                                                        public void update() {
                                                                            is_t4 = false;
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        }

        //Affichage de l'aide (F1)
        if (arg.getSource() == item_help) {
            String msg = "Vous êtes ici sur le panneau d'accueil de l'application. 6 choix vous sont proposés :\n\n"
                    + "- Générer un fichier Agents à partir d'un fichier csv : Permet, à partir d'un fichier csv bien formé\n"
                    + "de produire un fichier rdf contenant les informations relatives aux agents du laboratoire.\n\n"
                    + "- Créer du lien entre une bibliographie Zotero et un fichier Agents : Permet, à patrtir d'un fichier rdf issu \n"
                    + "de Zotero (export en format Bibliontologie RDF) et un fichier RDF portant sue les agents (idéalement produit \n"
                    + "par l'étape n°1) d'extraire les articles rédigés par les agents du laboratoire et d'établir le lien \n\"auteur "
                    + "/ a été écrit par\".\n\n"
                    + "- Générer les mots clés candidats : Permet, à partir d'un fichier produit par l'étape n°2, de produire une liste \n"
                    + "de mots candidats à devenir mots clés du domaine. Il est possible d'ajouter des mots à la StopList (mots non-pertinents) \n"
                    + "ou bien de préciser un modèle dans lequel se trouve déjà des mots clés (fichiers rdf_3max_ ou requête Sparql Endpoint.\n\n"
                    + "- Ajouter les mots clés aux models : Permet, à partir d'une liste de mots clés candidats (générées à l'étape n°3), de proposer \n"
                    + "une interface afin de les ajouter aux fichiers rdf concernés.\n\n"
                    + "- Annotation des mots clés : Permet de créer des tags sur les articles en fonction des mots clés présents dans un ou des modèles. \n"
                    + "Il est proposer d'entrer un fichier de bibliographie (produit à l'étape n°2), si le champs est vide le programme utilisera le \n"
                    + "serveur endpoint. Il est aussi proposer d'entrer une liste de modèles contenant les mots clés. Si ces champs sont vides, le \n"
                    + "programme utilisera le serveur endpoint. Enfin, il existe une possibilité d'entrer un fichier contenant les mots à ne pas tag, \n"
                    + "même s'ils apparaissent dans les articles.\n\n"
                    + "- Exécution complète : Permet l'exécution successive des 5 étapes précédentes.\n";
            JOptionPane.showMessageDialog(null, msg, "Aide du menu principal", JOptionPane.INFORMATION_MESSAGE);

        }
        //Ré-initialisation
        if (arg.getSource() == item_reinit) {
            endpoint = "http://ecoscopebc.mpl.ird.fr:8080/joseki/ecoscope";
            String msg = "Les champs suivants ont été ré-initialisé :\n"
                    + "- Adresse du SPARQL Endpoint\n";
            JOptionPane.showMessageDialog(null, msg, "Champs ré-initialisé", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Programme principal : démarre le processus
     *
     * @param args
     */
    public static void main(String[] args) {
        MainFrame frame = new MainFrame();
    }
}
