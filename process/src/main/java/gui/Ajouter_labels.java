package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * Première étape dans le traitement des mots clés, permet de choisir le fichier
 * des mots clés à traité.
 *
 * Voir l'action du bouton "Aide" pour plus de détails sur cette fenêtre.
 *
 *
 * @author jimmy
 */
public class Ajouter_labels extends Basic_Frame implements Observable, ActionListener {

    /**
     * Creates new form Ajouter_labels
     */
    public Ajouter_labels() {
        initComponents();
        this.setVisible(true);
    }

    private void initComponents() {

        file = null;

        this.setTitle("Ajouter des mots clés");

        fileIn = new javax.swing.JTextField();
        Open = new javax.swing.JButton();
        Retour = new javax.swing.JButton();
        Valider = new javax.swing.JButton();

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
        fileIn.setText("Fichier mots clés candidats...");

        Open.setText("Ouvrir...");
        Open.addActionListener(this);

        Retour.setText("Retour");
        Retour.addActionListener(this);

        Valider.setText("Valider");
        Valider.addActionListener(this);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(fileIn, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(51, 51, 51)
                        .addComponent(Open, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                        .addGap(300, 300, 300))
                .addGroup(layout.createSequentialGroup()
                        .addComponent(Retour, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Valider, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addGap(63, 63, 63)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(fileIn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(Open))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 141, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(Retour)
                                .addComponent(Valider)))
        );

        pack();
    }// </editor-fold>                        

    // Variables declaration - do not modify    
    private boolean is_adds = false; //Contrôle si une fenêtre de l'étape suivante est ouverte ou pas
    private javax.swing.JButton Open; //Bouton d'ouverture de l'exploreur
    private File file; //Fichier récupéré par l'exploreur
    private javax.swing.JButton Retour; //Retour au menu principal
    private javax.swing.JButton Valider; //Passage à l'étape suivante
    private javax.swing.JTextField fileIn; //Adresse du fichier sélectionné
    private ArrayList<Observateur> listObservateur = new ArrayList<>(); //Liste des Observateurs
    FileDrop fd = new FileDrop(panel, new FileDrop.Listener() { //Configuration du drag & drop
        @Override
        public void filesDropped(java.io.File[] files) {
            for (File file1 : files) {
                set_file(file1);
            }
        }   // end filesDropped
    }); // end FileDrop.Listener
    // End of variables declaration   

    /**
     * Permet d'associer un fichier à la fenetre
     *
     * @param f fichier contenant les mots clés
     */
    public void set_file(File f) {
        file = f;
        fileIn.setText(f.getAbsolutePath());
    }

    @Override
    public void actionPerformed(ActionEvent arg) {

        if (arg.getSource() == Open) {
            JFileChooser chooser = new JFileChooser();
            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                set_file(chooser.getSelectedFile());
            }
        }

        if (arg.getSource() == Valider) {
            if (file == null) {
                JOptionPane.showMessageDialog(null, "Vous n'avez sélectionné aucun fichier", "Pas de fichier !", JOptionPane.ERROR_MESSAGE);
            } else {
                if (!is_adds) {
                    Adds adds = new Adds(file);
                    is_adds = true;
                    this.setVisible(false);
                    //On place un écouteur sur l'horloge
                    adds.addObservateur(new Observateur() {
                        @Override
                        public void update() {
                            affiche();
                            is_adds = false;
                        }
                    });
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
                    + "- Chemin du fichier en entrée\n";
            JOptionPane.showMessageDialog(null, msg, "Champs ré-initialisés", JOptionPane.INFORMATION_MESSAGE);
        }

        if (arg.getSource() == item_help) {
            String msg
                    = "Cette fenêtre est la première étape de l'action permettant d'exploiter la liste des mots clés.\n\n"
                    + "Le but est de fournir une aide afin de sélectionner les termes qui méritent de devenir mots clés\n"
                    + "et de proposer une façon simple des les incorporer aux modèles RDF adéquats.\n\n"
                    + "Dans cette première étape, il est simplement demandé d'entrer le fichier de mot clé.\n"
                    + "\nAppuyez ensuite sur \"Valider\" afin de passer à l'étape suivante.";
            JOptionPane.showMessageDialog(null, msg, "Aide", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Ré-initialisation de la fenetre. Fichier à null, endpoint sur ecoscope,
     * et l'adresse du fichier sur l'invite
     */
    private void re_init() {
        file = null;
        endpoint = "http://ecoscopebc.mpl.ird.fr:8080/joseki/ecoscope";
        fileIn.setText("Fichier mots clés candidats...");
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
     * Quitte la fenetre en updatant ses observateurs.
     */
    public void exit() {
        updateObservateur();
        dispose();
    }

    /**
     * Affiche la fenetre.
     */
    public void affiche() {
        this.setVisible(true);
    }
}
