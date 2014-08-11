package gui;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import static com.ird.enrichissement_eco.EnrichissementBiblio.enrichissement;
import static com.ird.enrichissement_eco.Zotero.export_to_zotero;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Fenêtre permettant de faire le lien entre un fichier bibliographique et une
 * fichiers d'agents
 *
 * Voir l'action du bouton "Aide" pour plus de détails sur cette fenêtre.
 *
 * @author jimmy
 */
public class LienZotero extends Basic_Frame implements Observable, ActionListener {

    /**
     * Creates new form LienZotero
     */
    public LienZotero() {
        initComponents();
        this.setVisible(true);
    }

    private void initComponents() {

        file_zotero = null;
        nameOutput_zotero = "";
        file_agents = null;
        nameOutput_agents = "";
        nameOutput_clean = "";

        this.setTitle("Lien Zotero / Agents");

        fileIn_zotero = new javax.swing.JTextField();
        Open_zotero = new javax.swing.JButton();
        fileOut_zotero = new javax.swing.JTextField();
        fileIn_agents = new javax.swing.JTextField();
        Open_agents = new javax.swing.JButton();
        fileOut_agents = new javax.swing.JTextField();
        Retour = new javax.swing.JButton();
        Valider = new javax.swing.JButton();
        fileOut_clean = new javax.swing.JTextField();
        Desactiver_clean = new javax.swing.JToggleButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });
        fileIn_zotero.setEditable(false);
        fileIn_zotero.setText("Fichier Zotero...");

        Open_zotero.setText("Ouvrir...");
        Open_zotero.addActionListener(this);

        fileOut_zotero.setText("Entrez le nom du fichier en sortie...");

        fileIn_agents.setEditable(false);
        fileIn_agents.setText("Fichier Agents...");

        Open_agents.setText("Ouvrir...");
        Open_agents.addActionListener(this);

        fileOut_agents.setText("Entrez le nom du fichier en sortie...");

        Retour.setText("Retour");
        Retour.addActionListener(this);

        Valider.setText("Valider");
        Valider.addActionListener(this);

        Desactiver_clean.setText("Désactiver nettoyage");
        Desactiver_clean.addActionListener(this);

        fileOut_clean.setText("Entrez le nom du fichier en sortie...");

        item_help.addActionListener(this);
        item_reinit.addActionListener(this);

        fd_agents = new FileDrop(fileIn_agents, new FileDrop.Listener() {
            @Override
            public void filesDropped(java.io.File[] files) {
                for (File file1 : files) {
                    set_Agents(file1);
                }
            }   // end filesDropped
        }); // end FileDrop.Listener
        fd_zotero = new FileDrop(fileIn_zotero, new FileDrop.Listener() {
            @Override
            public void filesDropped(java.io.File[] files) {
                for (File file1 : files) {
                    set_Zotero(file1);
                }
            }   // end filesDropped
        }); // end FileDrop.Listener

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(Retour, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Valider, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createSequentialGroup()
                        .addGap(66, 66, 66)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(fileIn_zotero, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(fileIn_agents, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(68, 68, 68)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(Open_zotero, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
                                        .addGap(36, 36, 36)
                                        .addComponent(fileOut_zotero, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(Desactiver_clean, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(Open_agents, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE))
                                        .addGap(36, 36, 36)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(fileOut_agents, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(fileOut_clean, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addGap(72, 72, 72)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(fileIn_zotero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(Open_zotero)
                                .addComponent(fileOut_zotero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(75, 75, 75)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(fileIn_agents, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(Open_agents)
                                .addComponent(fileOut_agents, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(80, 80, 80)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(fileOut_clean, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(Desactiver_clean))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 109, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(Retour)
                                .addComponent(Valider)))
        );
        pack();
    }// </editor-fold>                        

    // Variables declaration - do not modify                     
    private javax.swing.JButton Open_agents; //FileOpener agents
    private javax.swing.JButton Open_zotero; //FileOpener Fichier biblio
    private javax.swing.JButton Retour; //Retour au menu principal
    private javax.swing.JButton Valider; //Execution
    private javax.swing.JTextField fileIn_agents; //Path du fichier agents en entrée
    private javax.swing.JTextField fileIn_zotero;//Path du fichier biblio en entrée
    private javax.swing.JTextField fileOut_agents; //Path du fichier agents en sortie
    private String nameOutput_agents; //Initialisé à Valider, nom du fichier agents en sortie
    private javax.swing.JTextField fileOut_zotero; //path du fichier biblio en sortie
    private String nameOutput_zotero; //Initialisé à Valider, nom du fichier biblio en sortie
    private String nameOutput_clean; //Initialisé à Valider, nom du fichier Zotero en sortie (fichier sans les doublons)
    private File file_zotero;   //Initialisé Open, fichier biblio en entrée
    private File file_agents;   //Initialisé Open, fichier agents en entrée
    private javax.swing.JToggleButton Desactiver_clean; //Toggle button pour désactiver le nétoyage du fichier Zotero
    private javax.swing.JTextField fileOut_clean;//path du fichier Zotero en sortie
    private ArrayList<Observateur> listObservateur = new ArrayList<>(); //Liste des Observateurs
    FileDrop fd_zotero;//drag and drop
    FileDrop fd_agents;
    // End of variables declaration   

    /**
     * Set fichier biblio
     *
     * @param zotero fichier biblio extrait de Zotero (format Zotero RDF)
     */
    public void set_Zotero(File zotero) {
        file_zotero = zotero;
        fileIn_zotero.setText(zotero.getAbsolutePath());
    }

    /**
     * Set fichier agents
     *
     * @param agents fichiers agents, obtenu à partir d'un fichier csv dans une
     * étape précédente
     */
    public void set_Agents(File agents) {
        file_agents = agents;
        fileIn_agents.setText(agents.getAbsolutePath());
    }

    /**
     * Set les path des fichiers en sortie, si vide pour un fichier le programme
     * utilise un nom par défaut
     *
     * @param zotero path du fichier biblio
     * @param agents path du fichier agents
     * @param clean path du fichier Zotero nétoyé
     */
    public void set_nameOutput(String zotero, String agents, String clean) {
        if (!zotero.equals("Entrez le nom du fichier en sortie...")) {
            nameOutput_zotero = zotero;
        } else {
            nameOutput_zotero = "ma_bibliontology.rdf";
        }
        if (!agents.equals("Entrez le nom du fichier en sortie...")) {
            nameOutput_agents = agents;
        } else {
            nameOutput_agents = "mes_agents_ext.rdf";
        }
        if (!clean.equals("Entrez le nom du fichier en sortie...")) {
            nameOutput_clean = agents;
        } else {
            nameOutput_clean = "zotero.rdf";
        }
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
     * Ferme la fenêtre en prévenant les observateurs.
     */
    public void exit() {
        updateObservateur();
        dispose();
    }

    @Override
    public void actionPerformed(ActionEvent arg) {

        if (arg.getSource() == Open_zotero) {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "rdf & owl", "rdf", "owl");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                set_Zotero(chooser.getSelectedFile());
            }
        }
        if (arg.getSource() == Open_agents) {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "rdf & owl", "rdf", "owl");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                set_Agents(chooser.getSelectedFile());
            }
        }
        if (arg.getSource() == Desactiver_clean) {
            if (Desactiver_clean.isSelected()) {
                fileOut_clean.setVisible(false);
            } else {
                fileOut_clean.setVisible(true);
            }
        }

        if (arg.getSource() == Valider) {
            if (file_zotero == null) {
                JOptionPane.showMessageDialog(null, "Vous n'avez sélectionné aucun fichier de bibliographie", "Pas de fichier !", JOptionPane.ERROR_MESSAGE);
            } else {
                if (file_agents == null) {
                    JOptionPane.showMessageDialog(null, "Vous n'avez sélectionné aucun fichier d'agents", "Pas de fichier !", JOptionPane.ERROR_MESSAGE);
                } else {
                    set_nameOutput(fileOut_zotero.getText(), fileOut_agents.getText(), fileOut_clean.getText());
                    String refBiblio = "file:" + file_zotero.getAbsolutePath();
                    String agentEcoscope = "file:" + file_agents.getAbsolutePath();
                    System.out.println("Appel de valider avec \nEntrée :\n\t" + refBiblio + "\n\t" + agentEcoscope + "\nSorties :\n\t" + nameOutput_zotero + "\n\t" + nameOutput_agents);
                    Model ModelBiblio = ModelFactory.createDefaultModel();
                    Model ModelAgents = ModelFactory.createDefaultModel();
                    Model ModelBiblio_out = ModelFactory.createDefaultModel();
                    Model ModelAgent_out = ModelFactory.createDefaultModel();
                    ModelBiblio.read(refBiblio);
                    ModelAgents.read(agentEcoscope);
                    enrichissement(ModelBiblio, ModelAgents, ModelBiblio_out, ModelAgent_out);
                    if (!Desactiver_clean.isSelected()) {
                        System.out.println("Avec option Zotero (nom sortie : " + nameOutput_clean + ")");
                        Model ModelZotero = ModelFactory.createDefaultModel();
                        export_to_zotero(ModelBiblio, ModelZotero);
                        try {
                            PrintWriter fluxZotero = new PrintWriter(new FileOutputStream(nameOutput_clean));
                            ModelZotero.write(fluxZotero);
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(LienZotero.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    try {
                        PrintWriter fluxModelBiblio = new PrintWriter(new FileOutputStream(nameOutput_zotero));
                        ModelBiblio_out.write(fluxModelBiblio);
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(LienZotero.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    try {
                        PrintWriter fluxModelAgent = new PrintWriter(new FileOutputStream(nameOutput_agents));
                        ModelAgent_out.write(fluxModelAgent);
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(LienZotero.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    System.out.println("Tache terminée");
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
                    + "- Chemin du fichier en entrée : bibliographie Zotero\n"
                    + "- Chemin du fichier en entrée : Agents\n"
                    + "- Nom du fichier en sortie : bibliographie\n"
                    + "- Nom du fichier en sortie : Agents\n"
                    + "- Nom du fichier en sortie : zotero";
            JOptionPane.showMessageDialog(null, msg, "Champs ré-initialisés", JOptionPane.INFORMATION_MESSAGE);
        }
        if (arg.getSource() == item_help) {
            String msg
                    = "Cette fenêtre permet de faire le lien entre un fichier exporté de Zotero\n"
                    + "(format bibliontology RDF) et un fichier rdf d'agents (idéalement crée à\n"
                    + "l'étape 1). Ces deux fichiers sont croisés pour en former deux autres :\n\n"
                    + "- ma_bibliontology.rdf : Pour chaque article, ce fichier comprend :\n"
                    + "     * Une URI formée à partir du titre\n"
                    + "     * Son titre\n"
                    + "     * Ses auteurs\n"
                    + "     * Son résumé\n"
                    + "     * Sa date\n"
                    + "     * Un lien vers la ressource\n\n"
                    + "- mes_agents_ext.rdf : extension du fichier des agents passé en entrée.\n"
                    + "Permet d'ajouter leurs publications à chaque agents.\n\n"
                    + "Les noms par défaut des fichiers peuvent être modifié grâce aux champs de droite.\n\n"
                    + "Enfin un fichier zotero.rdf est crée. Il propose une version propre du fichier zotero\n"
                    + "passé en entrée afin de pouvoir le ré-injecter dans Zotero. Les doublons auront été\n"
                    + "éliminés, une URI aura été donné aux noeuds anonymes et les noms des agents aura\n"
                    + "été complété lorsque cela aurait été nécessaire et possible.";
            JOptionPane.showMessageDialog(null, msg, "Aide", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Ré-intialise la fenêtre.
     */
    private void re_init() {
        file_zotero = null;
        file_agents = null;
        endpoint = "http://ecoscopebc.mpl.ird.fr:8080/joseki/ecoscope";
        fileOut_zotero.setText("Entrez le nom du fichier en sortie...");
        fileOut_agents.setText("Entrez le nom du fichier en sortie...");
        fileOut_clean.setText("Entrez le nom du fichier en sortie...");
        fileOut_clean.setVisible(true);
        fileIn_zotero.setText("Fichier Zotero...");
        fileIn_agents.setText("Fichier Agents...");
        Desactiver_clean.setSelected(false);
    }

}
