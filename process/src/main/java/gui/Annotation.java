package gui;

import static com.ird.enrichissement_eco.enrichissement_lucene.enrichissement;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Fenêtre permettant d'annoter des références bibliographiques grâce à des
 * triplets skos:prefLabel et skos:altLabel
 * 
 * Voir l'action du bouton "Aide" pour plus de détails sur cette fenêtre.
 *
 * @author jimmy
 */
public class Annotation extends Basic_Frame implements Observable, ActionListener {

    /**
     * Creates new form Annotation
     */
    public Annotation() {
        initComponents();
        this.setVisible(true);
    }

    private void initComponents() {

        bib = null;
        bl = null;
        modelAgents = null;
        modelEcosys = null;
        modelEcodef = null;

        this.setTitle("Annotation des références bibliographiques");

        fileIn_bib = new javax.swing.JTextField();
        fileIn_modelAgents = new javax.swing.JTextField();
        fileIn_modelEcosys = new javax.swing.JTextField();
        fileIn_modelEcodef = new javax.swing.JTextField();
        Open_bib = new javax.swing.JButton();
        Open_modelAgents = new javax.swing.JButton();
        Open_modelEcosys = new javax.swing.JButton();
        Open_modelEcodef = new javax.swing.JButton();
        Valider = new javax.swing.JButton();
        Retour = new javax.swing.JButton();
        Ajouter_bl = new javax.swing.JButton();

        item_reinit.addActionListener(this);
        item_help.addActionListener(this);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });

        fileIn_bib.setEditable(false);
        fileIn_bib.setText("Fichier bibliographie...");

        fileIn_modelAgents.setEditable(false);
        fileIn_modelAgents.setText("Modèle agents...");

        fileIn_modelEcosys.setEditable(false);
        fileIn_modelEcosys.setText("Modèle ecosystems...");

        fileIn_modelEcodef.setEditable(false);
        fileIn_modelEcodef.setText("Modèle ecosystems_def...");

        Open_bib.setText("Ouvrir...");
        Open_bib.addActionListener(this);

        Open_modelAgents.setText("Ouvrir...");
        Open_modelAgents.addActionListener(this);

        Open_modelEcosys.setText("Ouvrir...");
        Open_modelEcosys.addActionListener(this);

        Open_modelEcodef.setText("Ouvrir...");
        Open_modelEcodef.addActionListener(this);

        Valider.setText("Valider");
        Valider.addActionListener(this);

        Retour.setText("Retour");
        Retour.addActionListener(this);

        Ajouter_bl.setText("Ajouter une liste noire");
        Ajouter_bl.addActionListener(this);

        fd_bib = new FileDrop(fileIn_bib, new FileDrop.Listener() {
            @Override
            public void filesDropped(java.io.File[] files) {
                for (File file1 : files) {
                    set_bib(file1);
                }
            }   // end filesDropped
        }); // end FileDrop.Listener

        fd_agents = new FileDrop(fileIn_modelAgents, new FileDrop.Listener() {
            @Override
            public void filesDropped(java.io.File[] files) {
                for (File file1 : files) {
                    set_modelAgents(file1);
                }
            }   // end filesDropped
        }); // end FileDrop.Listener

        fd_ecosys = new FileDrop(fileIn_modelEcosys, new FileDrop.Listener() {
            @Override
            public void filesDropped(java.io.File[] files) {
                for (File file1 : files) {
                    set_modelEcosys(file1);
                }
            }   // end filesDropped
        }); // end FileDrop.Listener

        fd_ecodef = new FileDrop(fileIn_modelEcodef, new FileDrop.Listener() {
            @Override
            public void filesDropped(java.io.File[] files) {
                for (File file1 : files) {
                    set_modelEcodef(file1);
                }
            }   // end filesDropped
        }); // end FileDrop.Listener

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(fileIn_bib, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(94, 94, 94)
                                        .addComponent(Open_bib, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE))
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(fileIn_modelAgents, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(94, 94, 94)
                                        .addComponent(Open_modelAgents, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE))
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(fileIn_modelEcosys, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(94, 94, 94)
                                        .addComponent(Open_modelEcosys, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE))
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(fileIn_modelEcodef, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(94, 94, 94)
                                        .addComponent(Open_modelEcodef, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)))
                        .addGap(386, 386, 386))
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(Retour, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Ajouter_bl, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(Valider, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addGap(59, 59, 59)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(fileIn_bib, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(Open_bib))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(fileIn_modelAgents, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(Open_modelAgents))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(fileIn_modelEcosys, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(Open_modelEcosys))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(fileIn_modelEcodef, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(Open_modelEcodef))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 55, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(Valider)
                                .addComponent(Retour)
                                .addComponent(Ajouter_bl)))
        );

        pack();
    }// </editor-fold>                        

    // Variables declaration - do not modify                     
    private javax.swing.JButton Open_bib; //File Opener fichier bibliographique
    private File bib; //Fichier bibliographique
    private javax.swing.JButton Open_modelAgents; //File Opener fichier agents
    private File modelAgents; //Fichier agents
    private javax.swing.JButton Open_modelEcosys; //File Opener fichier Ecosystems
    private File modelEcosys; //fichier Ecosystems
    private javax.swing.JButton Open_modelEcodef; //File Opener fichier Ecosystems_def
    private File modelEcodef; //Fichier Ecosystems_def
    private javax.swing.JButton Retour; //Retour au menu principal
    private javax.swing.JButton Ajouter_bl; //Ajouter une liste noire (des labels qui ne seront pas annotés)
    private File bl; //Fichier contenant la liste noire
    private javax.swing.JButton Valider; //Execution
    private javax.swing.JTextField fileIn_bib; //Path fichier bibliographiquie
    private javax.swing.JTextField fileIn_modelAgents; //Path fichier Agents
    private javax.swing.JTextField fileIn_modelEcosys; //Path fichier Ecosystems
    private javax.swing.JTextField fileIn_modelEcodef; //Path fichier Ecosystems_def
    private ArrayList<Observateur> listObservateur = new ArrayList<>(); //Liste des Observateurs
    FileDrop fd_bib; //Drag & Drop
    FileDrop fd_agents;
    FileDrop fd_ecosys;
    FileDrop fd_ecodef;
// End of variables declaration      

    /**
     * Initialise le fichier bibliographique
     *
     * @param f fichier ouvert par le drag and drop ou l'exploreur
     */
    public void set_bib(File f) {
        bib = f;
        fileIn_bib.setText(f.getAbsolutePath());
    }

    /**
     * Initialise le fichier Agents
     *
     * @param f fichier ouvert par le drag and drop ou l'exploreur
     */
    public void set_modelAgents(File f) {
        modelAgents = f;
        fileIn_modelAgents.setText(f.getAbsolutePath());
    }

    /**
     * Initialise le fichier Ecosystems
     *
     * @param f fichier ouvert par le drag and drop ou l'exploreur
     */
    public void set_modelEcosys(File f) {
        modelEcosys = f;
        fileIn_modelEcosys.setText(f.getAbsolutePath());
    }

    /**
     * Initialise le fichier Ecosystems_def
     *
     * @param f fichier ouvert par le drag and drop ou l'exploreur
     */
    public void set_modelEcodef(File f) {
        modelEcodef = f;
        fileIn_modelEcodef.setText(f.getAbsolutePath());
    }

    /**
     * Initialise le fichier de liste noire
     *
     * @param f fichier ouvert par le drag and drop ou l'exploreur
     */
    public void set_bl(File f) {
        bl = f;
    }

    @Override
    public void actionPerformed(ActionEvent arg) {

        if (arg.getSource() == Open_bib) {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "RDF & OWL", "rdf", "owl");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                set_bib(chooser.getSelectedFile());
            }
        }
        if (arg.getSource() == Open_modelAgents) {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "RDF & OWL", "rdf", "owl");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                set_modelAgents(chooser.getSelectedFile());
            }
        }
        if (arg.getSource() == Open_modelEcosys) {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "RDF & OWL", "rdf", "owl");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                set_modelEcosys(chooser.getSelectedFile());
            }
        }
        if (arg.getSource() == Open_modelEcodef) {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "RDF & OWL", "rdf", "owl");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                set_modelEcodef(chooser.getSelectedFile());
            }
        }

        if (arg.getSource() == Ajouter_bl) {
            JFileChooser chooser = new JFileChooser();
            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                set_bl(chooser.getSelectedFile());
            }
        }

        if (arg.getSource() == Valider) {
            if (bib == null) {
                JOptionPane.showMessageDialog(null, "Vous n'avez sélectionné aucun fichier de bibliographie", "Pas de fichier !", JOptionPane.ERROR_MESSAGE);
            } else {
                //Si un paramètre est laissé vide, le programme créera ses propres modèles
                String pathAgents = "";
                String pathEcosys = "";
                String pathEcodef = "";
                String blacklist = "";
                if (modelAgents != null) {
                    pathAgents = modelAgents.getAbsolutePath();
                }
                if (modelEcosys != null) {
                    pathEcosys = modelEcosys.getAbsolutePath();
                }
                if (modelEcodef != null) {
                    pathEcodef = modelEcodef.getAbsolutePath();
                }
                if (bl != null) {
                    blacklist = bl.getAbsolutePath();
                }
                try {
                    System.out.println("Appel avec en entrée :\n-" + bib.getAbsolutePath() + "\n-" + pathAgents + "\n-" + pathEcosys + "\n-" + pathEcodef + "\n-" + blacklist + "\n-" + endpoint);
                    enrichissement(bib.getAbsolutePath(), pathAgents, pathEcosys, pathEcodef, blacklist, endpoint);
                } catch (IOException ex) {
                    Logger.getLogger(Annotation.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }

        if (arg.getSource() == Retour) {
            this.updateObservateur();
            this.dispose();
        }
        if (arg.getSource() == item_reinit) {
            re_init();
            String msg = "Les champs suivants ont été ré-initialisé :\n"
                    + "- Adresse du SPARQL Endpoint\n"
                    + "- Chemin des fichiers en entrées\n";
            JOptionPane.showMessageDialog(null, msg, "Champs ré-initialisés", JOptionPane.INFORMATION_MESSAGE);
        }
        if (arg.getSource() == item_help) {
            String msg
                    = "Cette fenêtre permet l'ajout de triplets RDF afin de marquer l'apparition de mots clés\n"
                    + "dans un article. Lorsqu'un mot clé (liste généré à l'étape 3, traitement à l'étape 4)\n"
                    + "est présent dans un article, un lien est construit entre les deux.\n\n"
                    + "Le premier champ attend un fichier bibliographique (idéalement produit lors de l'étape 2)\n"
                    + "Il ne peut pas etre laissé vide.\n\n"
                    + "Les 3 autres champs sont facultatifs :\n"
                    + "- Le second attend un fichier agents (idéalement généré à l'étape 1). \n"
                    + "Nom par défaut : Agents_ext.rdf\n\n"
                    + "- Le troisième attend un fichier ecosystems.\n"
                    + "Nom par défaut : Ecosystems_ext.rdf\n\n"
                    + "- Le quatrième attend un fichier ecosystem_def.\n"
                    + "Non par défaut : Ecosystem_def_ext.rdf\n\n"
                    + "Il est possible d'ajouter une liste noire. Ainsi meme si un mot de la liste noire est un mot clé\n"
                    + "et qu'il est présent dans un article, il ne sera pas utilisé.\n\n"
                    + "Si un fichier est passé en paramètre, le programme alimente son modèle.\n"
                    + "Sinon, il crée un modèle vide en utilisant les noms par défaut et le rempli.";
            JOptionPane.showMessageDialog(null, msg, "Aide", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * ré-initialisation de la fenêtre. Remise à null des fichiers / reset des
     * labels / reset du service endpoint
     */
    private void re_init() {
        bib = null;
        modelAgents = null;
        modelEcosys = null;
        modelEcodef = null;
        endpoint = "http://ecoscopebc.mpl.ird.fr:8080/joseki/ecoscope";
        fileIn_bib.setText("Fichier bibliographie...");
        fileIn_modelAgents.setText("Modèle agents...");
        fileIn_modelEcosys.setText("Modèle ecosystems...");
        fileIn_modelEcodef.setText("Modèle ecosystems_def...");
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
     * Quitte la fenêtre en prévenant ses observateurs.
     */
    public void exit() {
        updateObservateur();
        dispose();
    }
}
