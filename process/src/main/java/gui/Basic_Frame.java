package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.showInputDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import static com.ird.enrichissement_eco.Utilitaires.normalize;

/**
 * Fenêtre comportant les composants que toutes les autres fenêtres du programme hérite.
 * Possède la barre de menu et l'attribut du service endpoint
 * @author jimmy
 */
public class Basic_Frame extends JFrame {

    //Configuration
    String endpoint;

    //partie MenuBar
    JMenuBar menuBar;
    JMenu menu_fichier;
    JMenu menu_apropos;
    JMenuItem item_config;
    JMenuItem item_reinit;
    JMenuItem item_fermer;
    JMenuItem item_apropos;
    JMenuItem item_help;

    //panel
    JPanel panel;

    public Basic_Frame() {
        //Configuration de base du endpoint
        endpoint = "http://ecoscopebc.mpl.ird.fr:8080/joseki/ecoscope";

        this.setLocationRelativeTo(null);

        //Caractéristique de panel
        panel = new JPanel();

        //Lien entre les deux
        this.setContentPane(panel);

        //Configuration de la menuBar
        menuBar = new JMenuBar();
        menu_fichier = new JMenu("Fichier");
        menu_apropos = new JMenu("À propos");
        menuBar.add(menu_fichier);
        menuBar.add(menu_apropos);

        item_config = new JMenuItem("Configuration du SPARQL Endpoint");
        item_config.addActionListener(new ConfigListener());

        item_reinit = new JMenuItem("Réinitialiser tous les champs");
        item_reinit.setAccelerator(KeyStroke.getKeyStroke("F2"));

        item_fermer = new JMenuItem("Fermer");

        item_fermer.addActionListener(new FermerListener());

        item_apropos = new JMenuItem("à propos...");

        item_apropos.addActionListener(new AproposListener());

        item_help = new JMenuItem("aide");

        item_help.setAccelerator(KeyStroke.getKeyStroke("F1"));

        menu_fichier.add(item_config);

        menu_fichier.add(item_reinit);

        menu_fichier.add(item_fermer);

        menu_apropos.add(item_apropos);

        menu_apropos.add(item_help);

        this.setJMenuBar(menuBar);
    }

    class ConfigListener implements ActionListener {

        //Redéfinition de la méthode actionPerformed()
        @Override
        public void actionPerformed(ActionEvent arg0) {
            String endpoint_tmp = showInputDialog(null, "Adresse précédente : " + endpoint + "\nVeuillez entrer l'adresse du SPARQL Endpoint", "Initialisation du Endpoint", JOptionPane.QUESTION_MESSAGE);
            if (!normalize(endpoint_tmp).equals("")) {
                endpoint = endpoint_tmp;
            }
        }
    }

    class FermerListener implements ActionListener {

        //Redéfinition de la méthode actionPerformed()
        @Override
        public void actionPerformed(ActionEvent arg0) {
            System.exit(0);
        }
    }

    class AproposListener implements ActionListener {

        //Redéfinition de la méthode actionPerformed()
        @Override
        public void actionPerformed(ActionEvent arg0) {
            String msg
                    = "Cette application, et les fonctions la composant, a été réalisé par Jimmy Benoits dans le cadre\n"
                    + "d'un stage à l'IRD de Sète. Ce stage a été supervisé par Julien Barde";
            JOptionPane.showMessageDialog(null, msg, "à propos...", JOptionPane.INFORMATION_MESSAGE);
        }
    }

}
