package gui;

import static com.ird.enrichissement_eco.ExtractionAgent.extraction_agents_gui;
import static com.ird.enrichissement_eco.ExtractionAgent.test_extraction_agents;
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
 * Fenetre permettant de créer un fichier RDF d'agents à partir d'un fichier csv
 * normé
 *
 * 
 * Voir l'action du bouton "Aide" pour plus de détails sur cette fenêtre.
 * 
 * 
 * @author jimmy
 */
public class CsvToRdf extends Basic_Frame implements Observable, ActionListener {

    /**
     * Creates new form CsvToRdf
     */
    public CsvToRdf() {
        initComponents();
        this.setVisible(true);
    }

    private void initComponents() {

        file = null;
        nameOutput = "";
        isTested = false;

        this.setTitle("csv vers RDF");
        fileIn = new javax.swing.JTextField();
        Open = new javax.swing.JButton();
        fileOut = new javax.swing.JTextField();
        Test = new javax.swing.JButton();
        Valider = new javax.swing.JButton();
        Retour = new javax.swing.JButton();
        SavoirPlus = new javax.swing.JButton();

        item_reinit.addActionListener(this);
        item_help.addActionListener(this);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });

        fileIn.setEditable(false);
        fileIn.setText("Fichier csv...");
        Open.setText("Ouvrir...");
        fileOut.setText("Entrez le nom du fichier en sortie...");
        Test.setText("Tester");
        Valider.setText("Valider");
        Retour.setText("Retour");
        SavoirPlus.setText("En savoir plus...");
        Open.addActionListener(this);
        Test.addActionListener(this);
        Valider.addActionListener(this);
        Retour.addActionListener(this);
        SavoirPlus.addActionListener(this);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(fileIn, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(39, 39, 39)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(Open, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(Test, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 52, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(fileOut, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(Valider, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(33, 33, 33))
                .addGroup(layout.createSequentialGroup()
                        .addComponent(Retour, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(SavoirPlus, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(fileIn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(Open)
                                .addComponent(fileOut, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 78, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(Test)
                                .addComponent(Valider))
                        .addGap(70, 70, 70)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(Retour)
                                .addComponent(SavoirPlus)))
        );

        pack();
    }// </editor-fold>                        

    // Variables declaration - do not modify                     
    private javax.swing.JButton Open; //FileOpener du fichier csv
    private javax.swing.JButton Test; //Lance la procédure de test du fichier csv
    private javax.swing.JButton Valider; //Lance la tentative d'extraction des agents dans un fichier RDF
    private javax.swing.JButton Retour; //Retour au menu principal
    private javax.swing.JButton SavoirPlus; //Ouvre une fenêtre d'explication des règles du fichier csv
    private javax.swing.JTextField fileIn; //Path du fichier d'entrée
    private javax.swing.JTextField fileOut;// Path du ficher de sortie
    private File file;   //Initialisé Open
    private String nameOutput; //Initialisé à Valider
    private boolean isTested; // Initialisé après chaque Open à false, après chaque test réussi à true
    private ArrayList<Observateur> listObservateur = new ArrayList<>(); //Liste des Observateurs
    FileDrop fd = new FileDrop(panel, new FileDrop.Listener() { //Drag & drop
        @Override
        public void filesDropped(java.io.File[] files) {
            for (File file1 : files) {
                set_file(file1);
            }
        }   // end filesDropped
    }); // end FileDrop.Listener
    // End of variables declaration 

    /**
     * Initialise le fichier csv
     *
     * @param f fichier csv
     */
    public void set_file(File f) {
        file = f;
        fileIn.setText(f.getAbsolutePath());
    }

    /**
     * Set le path du fichier de sortie, si le champs n'a pas été touché, met
     * "mesAgents.rdf"
     *
     * @param s String contenue dans le champs fileOut
     */
    public void set_nameOutput(String s) {
        if (!s.equals("Entrez le nom du fichier en sortie...")) {
            nameOutput = s;
        } else {
            nameOutput = "mesAgents.rdf";
        }
    }

    /**
     * Set le boolean isTested
     *
     * @param b true or false
     */
    public void set_isTested(boolean b) {
        isTested = b;
    }

    @Override
    public void actionPerformed(ActionEvent arg) {

        if (arg.getSource() == Open) {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "csv", "csv");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                set_file(chooser.getSelectedFile());
                set_isTested(false);
            }
        }

        if (arg.getSource() == Test) {
            if (file == null) {
                JOptionPane.showMessageDialog(null, "Vous n'avez sélectionné aucun fichier", "Pas de fichier !", JOptionPane.ERROR_MESSAGE);
            } else {
                int result = 100;
                try {
                    result = test_extraction_agents(file);
                } catch (IOException ex) {
                    Logger.getLogger(CsvToRdf.class.getName()).log(Level.SEVERE, null, ex);
                }
                switch (result) {
                    case 0:
                        JOptionPane.showMessageDialog(null, "Le fichier semble adéquat", "Test réussi", JOptionPane.PLAIN_MESSAGE);
                        set_isTested(true);
                        break;
                    case 1:
                        JOptionPane.showMessageDialog(null, "Une erreur est survenue lors du contrôle du champs nom.\nConsulter les règles grâce au bouton \"En savoir plus...\"", "Erreur champs n°1", JOptionPane.ERROR_MESSAGE);
                        break;
                    case 2:
                        JOptionPane.showMessageDialog(null, "Une erreur est survenue lors du contrôle du champs prénom.\nConsulter les règles grâce au bouton \"En savoir plus...\"", "Erreur champs n°2", JOptionPane.ERROR_MESSAGE);
                        break;
                    case 3:
                        JOptionPane.showMessageDialog(null, "Une erreur est survenue lors du contrôle date de naissance.\nConsulter les règles grâce au bouton \"En savoir plus...\"", "Erreur champs n°3", JOptionPane.ERROR_MESSAGE);
                        break;
                    case 4:
                        JOptionPane.showMessageDialog(null, "Une erreur est survenue lors du contrôle du champs genre.\nConsulter les règles grâce au bouton \"En savoir plus...\"", "Erreur champs n°4", JOptionPane.ERROR_MESSAGE);
                        break;
                    case 5:
                        JOptionPane.showMessageDialog(null, "Une erreur est survenue lors du contrôle du champs titre.\nConsulter les règles grâce au bouton \"En savoir plus...\"", "Erreur champs n°5", JOptionPane.ERROR_MESSAGE);
                        break;
                    case 6:
                        JOptionPane.showMessageDialog(null, "Une erreur est survenue lors du contrôle du champs numéro de téléphone.\nConsulter les règles grâce au bouton \"En savoir plus...\"", "Erreur champs n°6", JOptionPane.ERROR_MESSAGE);
                        break;
                    case 7:
                        JOptionPane.showMessageDialog(null, "Une erreur est survenue lors du contrôle des champs : pas assez de champs (min. 2).\nConsulter les règles grâce au bouton \"En savoir plus...\"", "Erreur champs", JOptionPane.ERROR_MESSAGE);
                        break;
                    case 100:
                        JOptionPane.showMessageDialog(null, "Une erreur inattendue semble être survenue.\nConsulter les règles grâce au bouton \"En savoir plus...\"", "Whooops !", JOptionPane.ERROR_MESSAGE);
                        break;
                }
            }

        }

        if (arg.getSource() == Valider) {
            if (file == null) {
                JOptionPane.showMessageDialog(null, "Vous n'avez sélectionné aucun fichier", "Pas de fichier !", JOptionPane.ERROR_MESSAGE);
            } else {
                if (!isTested) {
                    int option = JOptionPane.showConfirmDialog(null, "Le fichier ne semble pas avoir été testé au préalable. Voulez-vous lancer quand même l'opération ?\n(Le model créé pourra contenir des anomalies)", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (option == JOptionPane.OK_OPTION) {
                        set_nameOutput(fileOut.getText());
                        System.out.println("Appel de valider avec \nEntrée : " + file.getAbsolutePath() + "\nSortie : " + nameOutput);
                        extraction_agents_gui(file, nameOutput);
                    }
                } else {
                    set_nameOutput(fileOut.getText());
                    System.out.println("Appel de valider avec \nEntrée : " + file + "\nSortie : " + nameOutput);
                    extraction_agents_gui(file, nameOutput);
                }
            }
            System.out.println("Tache terminée");
        }

        if (arg.getSource() == Retour) {
            this.updateObservateur();
            this.dispose();
        }

        if (arg.getSource() == SavoirPlus) {
            String regles = "Le fichier csv doit être de la forme :\n"
                    + "- nom / name / family name /nom de famille {obligatoire}\n"
                    + "- prenom / given name /surname {obligatoire}\n"
                    + "- date de naissance / birthdate\n"
                    + "- genre / gender \n"
                    + "- titre / title\n"
                    + "- numéro de téléphone / téléphone / phone / phone number\n\n"
                    + "Tous les champs doivent être présent, mais seuls les champs {obligatoire}\n"
                    + "doivent absolument avoir une valeur";
            JOptionPane.showMessageDialog(null, regles, "à propos...", JOptionPane.INFORMATION_MESSAGE);
        }

        if (arg.getSource() == item_reinit) {
            re_init();
            String msg = "Les champs suivants ont été ré-initialisé :\n"
                    + "- Adresse du SPARQL Endpoint\n"
                    + "- Chemin du fichier en entrée\n"
                    + "- Nom du fichier en sortie\n";
            JOptionPane.showMessageDialog(null, msg, "Champs ré-initialisés", JOptionPane.INFORMATION_MESSAGE);
        }
        if (arg.getSource() == item_help) {
            String msg
                    = "Cette fenêtre permet de générer un fichier rdf contenant les triplets relatifs aux agents du laboratoire.\n"
                    + "En entrée, le programme attend un fichier csv bien formé (voir le bouton \"En savoir plus...\" pour \n"
                    + "connaître les règles qui doivent s'appliquer au fichier).\n\n"
                    + "Le bouton \"Tester\" permet de vérifier si le fichier respecte les règles établies.\n"
                    + "Attention, la vérification est effectuée sur le titres des colonnes et ne garantie pas que le traitement\n"
                    + "échouera ou non.\n\n"
                    + "Le bouton \"Valider\" permet de lancer le traitement. Si l'utilisateur n'a pas sélectionné de nom de sortie,\n"
                    + "le fichier sera nommé \"mes_agents.rdf\".\n\n\n"
                    + "NB : Si le fichier en sortie est un modèle rdf vide, veuillez vérifier que votre fichier csv respecte les\n"
                    + "règles définies.";
            JOptionPane.showMessageDialog(null, msg, "Aide", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * ré-initialisation de la fenêtre.
     */
    private void re_init() {
        file = null;
        endpoint = "http://ecoscopebc.mpl.ird.fr:8080/joseki/ecoscope";
        fileOut.setText("Entrez le nom du fichier en sortie...");
        isTested = false;
        fileIn.setText("Fichier csv...");
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
     * Ferme la fenêtre et averti ses observateurs.
     */
    public void exit() {
        updateObservateur();
        dispose();
    }

}
