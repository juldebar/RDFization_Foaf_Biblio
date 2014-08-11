package gui;

import static com.ird.enrichissement_eco.Potentiel_keywords.extraire_mots;
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
 * fenêtre permettant l'extraction de mots candidats à devenir mot clés dans un
 * fichier de bibliographie. Cette fenêtre permet l'extraction des mots et
 * expressions (3mots max.) les plus courantes dans le fichier (idéalement crée
 * à l'étape 2). Il est possible d'ajouter un fichier de mots à ne pas traiter.
 * Le fichier doit comporter 1 mot par ligne. Ces mots seront ajoutés à une
 * liste présente par défaut. Un fichier log.txt est crée lorsque l'on active la
 * recherche de labels déjà existants. Il contient les mots qui étaient déjà
 * présent sous forme de label sur le service endpoint.
 *
 * @author jimmy
 */
public class Mots_candidats extends Basic_Frame implements Observable, ActionListener {

    /**
     * Creates new form Mot_candidats
     */
    public Mots_candidats() {
        initComponents();
        this.setVisible(true);
    }

    private void initComponents() {

        this.setTitle("Identifier les nouveaux mots clés");

        fileIn_bib = new javax.swing.JTextField();
        Open_bib = new javax.swing.JButton();
        fileOut = new javax.swing.JTextField();
        fileIn_liste = new javax.swing.JTextField();
        Open_liste = new javax.swing.JButton();
        Activer_liste = new javax.swing.JToggleButton();
        Activer_endpoint = new javax.swing.JToggleButton();
        Retour = new javax.swing.JButton();
        Valider = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });

        file_bib = null;
        file_liste = null;
        nameOutput = "";

        fileIn_bib.setEditable(false);
        fileIn_bib.setText("Fichier bibliographie...");

        Open_bib.setText("Ouvrir...");
        Open_bib.addActionListener(this);

        fileOut.setText("Entrez le nom du fichier en sortie...");

        fileIn_liste.setEditable(false);
        fileIn_liste.setText("Fichier liste noire...");
        fileIn_liste.setVisible(false);

        Open_liste.setText("Ouvrir...");
        Open_liste.addActionListener(this);
        Open_liste.setVisible(false);

        Activer_liste.setText("Activer liste noire");
        Activer_liste.addActionListener(this);
        Retour.setText("Retour");
        Retour.addActionListener(this);

        Valider.setText("Valider");
        Valider.addActionListener(this);

        Activer_endpoint.setText("Activer Recherche Labels Existants");
        Activer_endpoint.addActionListener(this);

        item_help.addActionListener(this);
        item_reinit.addActionListener(this);

        fd_bib = new FileDrop(fileIn_bib, new FileDrop.Listener() {
            @Override
            public void filesDropped(java.io.File[] files) {
                for (File file1 : files) {
                    set_bib(file1);
                }
            }   // end filesDropped
        }); // end FileDrop.Listener
        fd_liste = new FileDrop(fileIn_liste, new FileDrop.Listener() {
            @Override
            public void filesDropped(java.io.File[] files) {
                for (File file1 : files) {
                    set_liste(file1);
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
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(fileIn_bib, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(90, 90, 90)
                                        .addComponent(Open_bib, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(fileIn_liste, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(90, 90, 90)
                                        .addComponent(Open_liste, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)))
                        .addGap(91, 91, 91)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(Activer_endpoint, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(fileOut, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                                .addComponent(Activer_liste, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addGap(71, 71, 71)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(fileIn_bib, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(Open_bib)
                                .addComponent(fileOut, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(65, 65, 65)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(fileIn_liste, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(Open_liste)
                                .addComponent(Activer_liste))
                        .addGap(64, 64, 64)
                        .addComponent(Activer_endpoint)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 85, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(Retour)
                                .addComponent(Valider)))
        );
        pack();
    }// </editor-fold>                        

    // Variables declaration - do not modify                     
    private javax.swing.JButton Open_bib; //FileOpener de fichier bibliographique
    private javax.swing.JButton Open_liste;//FileOpener de la liste noire
    private javax.swing.JButton Retour; // retour au menu principal
    private javax.swing.JButton Valider; // Execution 
    private javax.swing.JTextField fileIn_bib; //path du fichier biblio
    private javax.swing.JTextField fileIn_liste;//path du fichier liste noire
    private javax.swing.JTextField fileOut;//path du fichier en sortie
    private String nameOutput; //nom du fichier en sortie
    private javax.swing.JToggleButton Activer_liste; //Toggle Button pour activer une liste noire venant de l'extérieure
    private javax.swing.JToggleButton Activer_endpoint;//Toggle Button pour activer la recherche de doubon sur le endpoint
    private ArrayList<Observateur> listObservateur = new ArrayList<>(); //Liste des Observateurs
    private File file_bib; //fichier biblio
    private File file_liste;//fichier liste noire
    FileDrop fd_bib;//drag & drop
    FileDrop fd_liste;
    // End of variables declaration   

    /**
     * Ré-initialise la fenêtre.
     */
    private void re_init() {
        file_bib = null;
        file_liste = null;
        nameOutput = "";
        endpoint = "http://ecoscopebc.mpl.ird.fr:8080/joseki/ecoscope";
        fileOut.setText("Entrez le nom du fichier en sortie...");
        fileIn_bib.setText("Fichier bibliographie...");
        fileIn_liste.setText("Fichier liste noire...");
        fileIn_liste.setVisible(false);
        Activer_liste.setSelected(false);
        Open_liste.setVisible(false);
    }

    /**
     * Set le fichier biblio (doit être un fichier généré par le programme aux
     * étapes précédentes)
     *
     * @param f fichier de bibliographie rdf
     */
    public void set_bib(File f) {
        file_bib = f;
        fileIn_bib.setText(f.getAbsolutePath());
    }

    /**
     * Set le fichier liste noire, le fichier doit contenir 1mot par ligne
     *
     * @param f fichier liste noire
     */
    public void set_liste(File f) {
        file_liste = f;
        fileIn_liste.setText(f.getAbsolutePath());
    }

    /**
     * Set le nom du fichier en sortie, si le champs est laissé tel quel,
     * associe un nom par défault
     *
     * @param s champs du fichier de sortie
     */
    public void set_nameOutput(String s) {
        if (!s.equals("Entrez le nom du fichier en sortie...")) {
            nameOutput = s;
        } else {
            String path = System.getProperty("user.dir");
            nameOutput = path + "/outputs/potentiel_keywods.txt";
        }
    }

    @Override
    public void actionPerformed(ActionEvent arg) {

        if (arg.getSource() == Open_bib) {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "rdf & owl", "rdf", "owl");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                set_bib(chooser.getSelectedFile());
            }
        }
        if (arg.getSource() == Open_liste) {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "txt", "txt");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                set_liste(chooser.getSelectedFile());
            }
        }

        if (arg.getSource() == Activer_liste) {
            if (Activer_liste.isSelected()) {
                Open_liste.setVisible(true);
                fileIn_liste.setVisible(true);
            } else {
                Open_liste.setVisible(false);
                fileIn_liste.setVisible(false);
            }
        }

        if (arg.getSource() == Valider) {
            set_nameOutput(fileOut.getText());
            if (file_bib == null) {
                JOptionPane.showMessageDialog(null, "Vous n'avez sélectionné aucun fichier de bibliographie", "Pas de fichier !", JOptionPane.ERROR_MESSAGE);
            } else {
                String refBiblio = "file:" + file_bib.getAbsolutePath();
                String path_liste = "";
                if (file_liste != null) {
                    path_liste = file_liste.getAbsolutePath();
                }
                try {
                    if (Activer_endpoint.isSelected()) {
                        System.out.println("Action :\nfichier biblio :  " + refBiblio + "\nmots à bannir : " + path_liste + "\nendpoint : " + endpoint + "\nSortie : " + nameOutput);
                        extraire_mots(refBiblio, path_liste, endpoint, nameOutput);
                    } else {
                        System.out.println("Action :\nfichier biblio :  " + refBiblio + "\nmots à bannir : " + path_liste + "\nSortie : " + nameOutput);
                        extraire_mots(refBiblio, path_liste, nameOutput);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Mots_candidats.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            System.out.println("Tache terminée");
        }

        if (arg.getSource() == Retour) {
            this.updateObservateur();
            this.dispose();
        }

        if (arg.getSource() == item_reinit) {
            re_init();
            String msg = "Les champs suivants ont été ré-initialisé :\n"
                    + "- Adresse du SPARQL Endpoint\n"
                    + "- Chemin du fichier en entrée : bibliographie\n"
                    + "- Chemin du fichier en entrée : liste noire\n"
                    + "- Nom du fichier en sortie\n";
            JOptionPane.showMessageDialog(null, msg, "Champs ré-initialisés", JOptionPane.INFORMATION_MESSAGE);
        }
        if (arg.getSource() == item_help) {
            String msg
                    = "Cette fenêtre permet l'extraction des mots et expressions (3mots max.) les plus courantes\n"
                    + "dans le fichier (idéalement crée à l'étape 2).\n\n"
                    + "Il est possible d'ajouter un fichier de mots à ne pas traiter. Le fichier doit comporter\n"
                    + "1 mot par ligne. Ces mots seront ajoutés à une liste présente par défaut.\n\n"
                    + "Un fichier log.txt est crée lorsque l'on active la recherche de labels déjà existants.\n"
                    + "Il contient les mots qui étaient déjà présent sous forme de label sur le service endpoint.";
            JOptionPane.showMessageDialog(null, msg, "Aide", JOptionPane.INFORMATION_MESSAGE);
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
     * Ferme la fenêtre et prévient les observateurs.
     */
    public void exit() {
        updateObservateur();
        dispose();
    }
}
