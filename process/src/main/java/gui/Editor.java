package gui;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import static com.ird.enrichissement_eco.Utilitaires.normalize;
import static gui.Adds.is_not_empty;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Dernière étape dans le traitement des mots clés, permet d'ajouter les
 * triplets aux mots choisis
 * 
 * Voir l'action du bouton "Aide" pour plus de détails sur cette fenêtre.
 *
 * @author jimmy
 */
public class Editor extends Basic_Frame implements Observable, ActionListener {

    /**
     * Creates new form editor
     *
     * @param map1 Racine <-> Occurrences racines
     * @param map2 Racine <-> (Mots <-> Occurrences mots)
     */
    public Editor(HashMap<String, Integer> map1, HashMap<String, HashMap<String, Integer>> map2) {
        initComponents(map1, map2);
    }

    private void initComponents(HashMap<String, Integer> map1, HashMap<String, HashMap<String, Integer>> map2) {

        this.setTitle("Ajouts des triplets");
        map_cpt = map1;
        map_mots = map2;

        list = new LinkedList<>();
        Label_modele = new javax.swing.JLabel();
        Field_modele = new javax.swing.JTextField();
        Open = new javax.swing.JButton();
        Label_URI = new javax.swing.JLabel();
        Field_URI = new javax.swing.JTextField();
        Combo_pref = new javax.swing.JComboBox();
        Label_pref1 = new javax.swing.JLabel();
        Field_pref1 = new javax.swing.JTextField();
        Combo_pref1 = new javax.swing.JComboBox();
        Label_pref2 = new javax.swing.JLabel();
        Field_pref2 = new javax.swing.JTextField();
        Combo_pref2 = new javax.swing.JComboBox();
        Label_pref3 = new javax.swing.JLabel();
        Field_pref3 = new javax.swing.JTextField();
        Combo_pref3 = new javax.swing.JComboBox();
        Combo_alt = new javax.swing.JComboBox();
        Label_alt1 = new javax.swing.JLabel();
        Field_alt1 = new javax.swing.JTextField();
        Combo_alt1 = new javax.swing.JComboBox();
        Label_alt2 = new javax.swing.JLabel();
        Field_alt2 = new javax.swing.JTextField();
        Combo_alt2 = new javax.swing.JComboBox();
        Label_alt3 = new javax.swing.JLabel();
        Field_alt3 = new javax.swing.JTextField();
        Combo_alt3 = new javax.swing.JComboBox();
        Label_fao = new javax.swing.JLabel();
        Field_fao = new javax.swing.JTextField();
        Label_worms = new javax.swing.JLabel();
        Field_worms = new javax.swing.JTextField();
        Scroll_Note2 = new javax.swing.JScrollPane();
        Panel_Note2 = new javax.swing.JTextPane();
        Label_Note2 = new javax.swing.JLabel();
        Combo_note2 = new javax.swing.JComboBox();
        Scroll_Note1 = new javax.swing.JScrollPane();
        Panel_Note1 = new javax.swing.JTextPane();
        Scroll_Note3 = new javax.swing.JScrollPane();
        Panel_Note3 = new javax.swing.JTextPane();
        Label_Note1 = new javax.swing.JLabel();
        Combo_note1 = new javax.swing.JComboBox();
        Combo_note3 = new javax.swing.JComboBox();
        Label_Note3 = new javax.swing.JLabel();
        Terminer = new javax.swing.JButton();
        Retour = new javax.swing.JButton();
        Suivant = new javax.swing.JButton();
        Ignorer = new javax.swing.JButton();
        Precedent = new javax.swing.JButton();
        Scroll_mot = new javax.swing.JScrollPane();
        Panel_mot = new javax.swing.JTextPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });

        champs = new LinkedList<>();
        Champs_editor tmp;
        for (Entry<String, Integer> entry : map_cpt.entrySet()) {
            tmp = new Champs_editor();
            champs.add(tmp);
            list.add(entry.getKey());
        }
        max_entries = list.size();

        Label_modele.setText("Modèle : ");

        Field_modele.setText("Modèle RDF...");
        Field_modele.setEditable(false);

        Open.setText("Ouvrir...");
        Open.addActionListener(this);

        Label_URI.setText("URI :");

        Field_URI.setText("http://www.ecoscope.org/");

        Combo_pref.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"1 label", "2 labels", "3 labels"}));
        Combo_pref.addActionListener(this);

        Label_pref1.setText("prefLabel :");
        Combo_pref1.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Français", "English", "Español"}));

        Label_pref2.setText("prefLabel :");
        Label_pref2.setVisible(false);
        Field_pref2.setVisible(false);
        Combo_pref2.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Français", "English", "Español"}));
        Combo_pref2.setVisible(false);

        Label_pref3.setText("prefLabel :");
        Label_pref3.setVisible(false);
        Field_pref3.setVisible(false);
        Combo_pref3.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Français", "English", "Español"}));
        Combo_pref3.setVisible(false);

        Combo_alt.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"1 label", "2 labels", "3 labels"}));
        Combo_alt.addActionListener(this);

        Label_alt1.setText("altLabel :");
        Combo_alt1.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Français", "English", "Español"}));

        Label_alt2.setText("altLabel :");
        Label_alt2.setVisible(false);
        Field_alt2.setVisible(false);
        Combo_alt2.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Français", "English", "Español"}));
        Combo_alt2.setVisible(false);

        Label_alt3.setText("altLabel :");
        Label_alt3.setVisible(false);
        Field_alt3.setVisible(false);
        Combo_alt3.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Français", "English", "Español"}));
        Combo_alt3.setVisible(false);

        Label_fao.setText("fao ID :");

        Label_worms.setText("worms ID :");

        Scroll_Note2.setViewportView(Panel_Note2);

        Label_Note2.setText("Note :");

        Combo_note2.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Français", "English", "Español"}));

        Scroll_Note1.setViewportView(Panel_Note1);

        Scroll_Note3.setViewportView(Panel_Note3);

        Label_Note1.setText("Note :");

        Combo_note1.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Français", "English", "Español"}));

        Combo_note3.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Français", "English", "Español"}));

        Label_Note3.setText("Note :");

        Terminer.setText("Terminer");
        Terminer.addActionListener(this);

        Retour.setText("Retour");
        Retour.addActionListener(this);

        Suivant.setText("Suivant");
        Suivant.addActionListener(this);

        Ignorer.setText("Ignorer");
        Ignorer.addActionListener(this);

        Precedent.setText("Précédent");
        Precedent.addActionListener(this);

        item_reinit.addActionListener(this);
        item_help.addActionListener(this);

        Scroll_mot.setViewportView(Panel_mot);
        Panel_mot.setEditable(false);
        set_PanelMot(position);

        setPreferredSize(new java.awt.Dimension(1189, 600));
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                        .addGap(0, 0, Short.MAX_VALUE)
                                                        .addComponent(Precedent, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(53, 53, 53))
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                                .addGroup(layout.createSequentialGroup()
                                                                        .addGap(0, 0, Short.MAX_VALUE)
                                                                        .addComponent(Field_URI, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGroup(layout.createSequentialGroup()
                                                                        .addComponent(Label_modele)
                                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                        .addComponent(Field_modele, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                        .addGap(18, 18, 18)
                                                        .addComponent(Open, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(Ignorer))
                                .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(layout.createSequentialGroup()
                                                        .addComponent(Combo_pref, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(18, 18, 18)
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                        .addComponent(Label_pref1)
                                                                        .addGap(18, 18, 18)
                                                                        .addComponent(Field_pref1, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addGap(12, 12, 12))
                                                                .addGroup(layout.createSequentialGroup()
                                                                        .addComponent(Label_pref3)
                                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                        .addComponent(Field_pref3, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addGap(18, 18, 18)))
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addComponent(Combo_pref1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(Combo_pref3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addComponent(Label_URI)
                                                .addGroup(layout.createSequentialGroup()
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                                .addGroup(layout.createSequentialGroup()
                                                                        .addComponent(Label_worms)
                                                                        .addGap(18, 18, 18)
                                                                        .addComponent(Field_worms, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addComponent(Field_alt3, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                                        .addComponent(Combo_alt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addGap(18, 18, 18)
                                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                                                .addGroup(layout.createSequentialGroup()
                                                                                        .addComponent(Label_alt1)
                                                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
                                                                                        .addComponent(Field_alt1, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                .addGroup(layout.createSequentialGroup()
                                                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                .addComponent(Label_alt2)
                                                                                                .addComponent(Label_alt3))
                                                                                        .addGap(0, 0, Short.MAX_VALUE))))
                                                                .addComponent(Field_alt2, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGroup(layout.createSequentialGroup()
                                                                        .addComponent(Label_fao)
                                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                        .addComponent(Field_fao, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                        .addGap(18, 18, 18)
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addComponent(Combo_alt2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(Combo_alt1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(Combo_alt3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addGroup(layout.createSequentialGroup()
                                                        .addGap(102, 102, 102)
                                                        .addComponent(Label_pref2)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(Field_pref2, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(18, 18, 18)
                                                        .addComponent(Combo_pref2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGap(0, 88, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(layout.createSequentialGroup()
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                                                .addComponent(Combo_note2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addComponent(Combo_note1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addComponent(Combo_note3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                        .addGap(18, 18, 18)
                                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                .addComponent(Label_Note2)
                                                                                .addComponent(Label_Note3))
                                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                                                                .addGroup(layout.createSequentialGroup()
                                                                        .addGap(107, 107, 107)
                                                                        .addComponent(Label_Note1)
                                                                        .addGap(6, 6, 6)))
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addComponent(Scroll_Note2, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(Scroll_Note1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addGroup(layout.createSequentialGroup()
                                                        .addGap(152, 152, 152)
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addComponent(Suivant, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(Scroll_Note3, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                        .addGap(92, 92, 92))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(Scroll_mot, javax.swing.GroupLayout.PREFERRED_SIZE, 569, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addContainerGap())))
                .addGroup(layout.createSequentialGroup()
                        .addComponent(Retour, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Terminer, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(Scroll_mot, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(Label_modele)
                                                .addComponent(Field_modele, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(Open))
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(Field_URI, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(Label_URI))))
                        .addGap(21, 21, 21)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                        .addGap(15, 15, 15)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(Label_Note1)
                                                        .addComponent(Combo_note1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addComponent(Scroll_Note1, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(22, 22, 22)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(Scroll_Note2, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(Label_Note2)
                                                        .addComponent(Combo_note2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGap(26, 26, 26)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(Combo_note3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(Label_Note3))
                                                .addComponent(Scroll_Note3, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(0, 0, Short.MAX_VALUE))
                                .addGroup(layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(Label_pref1)
                                                .addComponent(Field_pref1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(Combo_pref, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(Combo_pref1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(Label_pref2)
                                                .addComponent(Field_pref2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(Combo_pref2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(Label_pref3)
                                                .addComponent(Field_pref3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(Combo_pref3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(Combo_alt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(Label_alt1)
                                                .addComponent(Field_alt1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(Combo_alt1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(Label_alt2)
                                                .addComponent(Field_alt2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(Combo_alt2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(Label_alt3)
                                                .addComponent(Field_alt3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(Combo_alt3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(Label_fao)
                                                .addComponent(Field_fao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(Label_worms)
                                                .addComponent(Field_worms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 49, Short.MAX_VALUE)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(Terminer)
                                                .addComponent(Retour)
                                                .addComponent(Suivant)
                                                .addComponent(Ignorer)
                                                .addComponent(Precedent)))))
        );
        pack();
        this.setVisible(true);
    }

    // Variables declaration - do not modify 
    private javax.swing.JComboBox Combo_note1; //Choix de la langue pour le triplet skos:Note
    private javax.swing.JLabel Label_Note1;
    private javax.swing.JComboBox Combo_note2;
    private javax.swing.JLabel Label_Note2;
    private javax.swing.JComboBox Combo_note3;
    private javax.swing.JLabel Label_Note3;
    private javax.swing.JComboBox Combo_alt; //Choix du nombre de skos:altLabel
    private javax.swing.JComboBox Combo_alt1;//Choix de la langue pour le triplet skos:altLabel
    private javax.swing.JComboBox Combo_alt2;
    private javax.swing.JComboBox Combo_alt3;
    private javax.swing.JTextField Field_alt1;
    private javax.swing.JTextField Field_alt2;
    private javax.swing.JTextField Field_alt3;
    private javax.swing.JLabel Label_alt1;
    private javax.swing.JLabel Label_alt2;
    private javax.swing.JLabel Label_alt3;
    private javax.swing.JComboBox Combo_pref;//Choix du nombre de skos:prefLabel
    private javax.swing.JComboBox Combo_pref1;//Choix de la langue pour le triplet skos:prefLabel
    private javax.swing.JComboBox Combo_pref2;
    private javax.swing.JComboBox Combo_pref3;
    private javax.swing.JTextField Field_pref1;
    private javax.swing.JTextField Field_pref2;
    private javax.swing.JTextField Field_pref3;
    private javax.swing.JLabel Label_pref1;
    private javax.swing.JLabel Label_pref2;
    private javax.swing.JLabel Label_pref3;
    private javax.swing.JTextField Field_URI;
    private javax.swing.JLabel Label_URI;
    private javax.swing.JTextField Field_fao;
    private javax.swing.JLabel Label_fao;
    private javax.swing.JTextField Field_modele; //path du fichier contenant le modele dans lequel les triplets pour ce mot doit etre ajouté
    private javax.swing.JLabel Label_modele;
    private javax.swing.JTextField Field_worms;
    private javax.swing.JLabel Label_worms;
    private javax.swing.JButton Ignorer; //Passe au mot suivant, n'enregistre pas les fields remplis
    private javax.swing.JButton Open; //fileOpener pour le fichier contenant le modele dans lequel les triplets pour ce mot doit etre ajouté 
    private javax.swing.JTextPane Panel_Note1;
    private javax.swing.JTextPane Panel_Note2;
    private javax.swing.JTextPane Panel_Note3;
    private javax.swing.JTextPane Panel_mot; //Panel contenant le mot à étudier
    private javax.swing.JButton Precedent; //Mot précédent, enregistre les champs
    private javax.swing.JButton Retour; //Retour à l'étape 2
    private javax.swing.JScrollPane Scroll_Note1;
    private javax.swing.JScrollPane Scroll_Note2;
    private javax.swing.JScrollPane Scroll_Note3;
    private javax.swing.JScrollPane Scroll_mot;
    private javax.swing.JButton Suivant; //Passage au mot suivant, enregistre les champs
    private javax.swing.JButton Terminer; //Traitement, écrit dans les modeles les nouveaux triplets
    private HashMap<String, Integer> map_cpt; //Table contenant <Racine, nombre occurrences racine>, passé en paramètre du constructeur
    private HashMap<String, HashMap<String, Integer>> map_mots;//Table contenant <Racine, Table<Mot, occurrences mots>>, passé en paramètre du constructeur
    private LinkedList<String> list; //Liste des racines
    private LinkedList<Champs_editor> champs; //Correspondance entre les racines et les champs
    private int position = 0; //Indice pour se déplacer dans list / champs
    private int max_entries; //Nombre total de mots
    private ArrayList<Observateur> listObservateur = new ArrayList<>(); //Liste des Observateurs
    FileDrop fd = new FileDrop(panel, new FileDrop.Listener() { //drag & drop
        @Override
        public void filesDropped(java.io.File[] files) {
            for (File file : files) {
                Field_modele.setText(file.getAbsolutePath());
            }
        }   // end filesDropped
    }); // end FileDrop.Listener
    // End of variables declaration                   

    @Override
    public void actionPerformed(ActionEvent arg) {

        if (arg.getSource() == Combo_alt) {
            int choix = Combo_alt.getSelectedIndex();
            if (choix == 0) {
                Field_alt2.setVisible(false);
                Combo_alt2.setVisible(false);
                Label_alt2.setVisible(false);

                Field_alt3.setVisible(false);
                Combo_alt3.setVisible(false);
                Label_alt3.setVisible(false);
            }
            if (choix == 1) {
                Field_alt2.setVisible(true);
                Combo_alt2.setVisible(true);
                Label_alt2.setVisible(true);

                Field_alt3.setVisible(false);
                Combo_alt3.setVisible(false);
                Label_alt3.setVisible(false);
            }
            if (choix == 2) {
                Field_alt2.setVisible(true);
                Combo_alt2.setVisible(true);
                Label_alt2.setVisible(true);

                Field_alt3.setVisible(true);
                Combo_alt3.setVisible(true);
                Label_alt3.setVisible(true);
            }
        }

        if (arg.getSource() == Combo_pref) {
            int choix = Combo_pref.getSelectedIndex();
            if (choix == 0) {
                Field_pref2.setVisible(false);
                Combo_pref2.setVisible(false);
                Label_pref2.setVisible(false);

                Field_pref3.setVisible(false);
                Combo_pref3.setVisible(false);
                Label_pref3.setVisible(false);
            }
            if (choix == 1) {
                Field_pref2.setVisible(true);
                Combo_pref2.setVisible(true);
                Label_pref2.setVisible(true);

                Field_pref3.setVisible(false);
                Combo_pref3.setVisible(false);
                Label_pref3.setVisible(false);
            }
            if (choix == 2) {
                Field_pref2.setVisible(true);
                Combo_pref2.setVisible(true);
                Label_pref2.setVisible(true);

                Field_pref3.setVisible(true);
                Combo_pref3.setVisible(true);
                Label_pref3.setVisible(true);
            }
        }
        if (arg.getSource() == Open) {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "rdf & owl", "rdf", "owl");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                Field_modele.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        }

        if (arg.getSource() == Ignorer) {
            if (position < max_entries - 1) {
                ++position;
            } else {
                position = 0;
            }
            set_champs(position);
            set_PanelMot(position);
        }

        if (arg.getSource() == Retour) {
            exit();
        }

        if (arg.getSource() == Terminer) {
            //On met à jour le dernier élément (comme pour un précédent / suivant)
            champs.get(position).set_entry(get_champs());
            //Récupération des prédicats nécessaires
            String dir = System.getProperty("user.dir");
            String path = "file:" + dir + "/ouputs/predicats.rdf";            Model tmp = ModelFactory.createDefaultModel();
            tmp.read(path);
            Property skos_prefLabel;
            Property skos_altLabel;
            Property fao;
            Property worms;
            Property skos_note;
            //Charge le model présent dans path pour récupérer les Properties
            skos_prefLabel = getProperty_prefLabel(path);
            skos_altLabel = getProperty_altLabel(path);
            fao = getProperty_faoId(path);
            worms = getProperty_worms(path);
            skos_note = getProperty_note(path);
            int combo;
            LinkedList<String> models = new LinkedList<>();
            String model;
            String[] args;
            Resource add;
            int nbre_models = 0;
            for (Champs_editor element : champs) {
                model = element.getModele();
                if (!models.contains(model)) {
                    ++nbre_models;
                    models.add(model);
                }
            }
            int i = 0;
            while (i < nbre_models) {
                model = models.get(i);
                Model Model = ModelFactory.createDefaultModel();
                if (!model.equals("Modèle RDF...")) {
                    Model.read("file:" + model);
                }
                for (Champs_editor element : champs) {
                    if (element.getModele().equals(model)) {
                        args = element.get_args();
                        if (is_valid_URI(args[0])) {
                            add = Model.createResource(args[0]);
                            if (is_not_empty(args[2])) {
                                combo = Combo_pref1.getSelectedIndex();
                                if (combo == 0) {
                                    add.addProperty(skos_prefLabel, Model.createLiteral(args[2], "fr"));
                                }
                                if (combo == 1) {
                                    add.addProperty(skos_prefLabel, Model.createLiteral(args[2], "en"));
                                }
                                if (combo == 2) {
                                    add.addProperty(skos_prefLabel, Model.createLiteral(args[2], "es"));

                                }
                            }
                            if (is_not_empty(args[3])) {
                                combo = Combo_pref2.getSelectedIndex();
                                if (combo == 0) {
                                    add.addProperty(skos_prefLabel, Model.createLiteral(args[3], "fr"));
                                }
                                if (combo == 1) {
                                    add.addProperty(skos_prefLabel, Model.createLiteral(args[3], "en"));
                                }
                                if (combo == 2) {
                                    add.addProperty(skos_prefLabel, Model.createLiteral(args[3], "es"));

                                }

                            }
                            if (is_not_empty(args[4])) {
                                combo = Combo_pref3.getSelectedIndex();
                                if (combo == 0) {
                                    add.addProperty(skos_prefLabel, Model.createLiteral(args[4], "fr"));
                                }
                                if (combo == 1) {
                                    add.addProperty(skos_prefLabel, Model.createLiteral(args[4], "en"));
                                }
                                if (combo == 2) {
                                    add.addProperty(skos_prefLabel, Model.createLiteral(args[4], "es"));

                                }

                            }
                            if (is_not_empty(args[5])) {
                                combo = Combo_alt1.getSelectedIndex();
                                if (combo == 0) {
                                    add.addProperty(skos_altLabel, Model.createLiteral(args[5], "fr"));
                                }
                                if (combo == 1) {
                                    add.addProperty(skos_altLabel, Model.createLiteral(args[5], "en"));
                                }
                                if (combo == 2) {
                                    add.addProperty(skos_altLabel, Model.createLiteral(args[5], "es"));

                                }

                            }
                            if (is_not_empty(args[6])) {
                                combo = Combo_alt2.getSelectedIndex();
                                if (combo == 0) {
                                    add.addProperty(skos_altLabel, Model.createLiteral(args[6], "fr"));
                                }
                                if (combo == 1) {
                                    add.addProperty(skos_altLabel, Model.createLiteral(args[6], "en"));
                                }
                                if (combo == 2) {
                                    add.addProperty(skos_altLabel, Model.createLiteral(args[6], "es"));

                                }

                            }
                            if (is_not_empty(args[7])) {
                                combo = Combo_alt3.getSelectedIndex();
                                if (combo == 0) {
                                    add.addProperty(skos_altLabel, Model.createLiteral(args[7], "fr"));
                                }
                                if (combo == 1) {
                                    add.addProperty(skos_altLabel, Model.createLiteral(args[7], "en"));
                                }
                                if (combo == 2) {
                                    add.addProperty(skos_altLabel, Model.createLiteral(args[7], "es"));

                                }

                            }
                            if (is_not_empty(args[8])) {
                                add.addProperty(fao, args[8]);

                            }
                            if (is_not_empty(args[9])) {
                                add.addProperty(worms, args[9]);

                            }
                            if (is_not_empty(args[10])) {
                                combo = Combo_note1.getSelectedIndex();
                                if (combo == 0) {
                                    add.addProperty(skos_note, Model.createLiteral(args[10], "fr"));
                                }
                                if (combo == 1) {
                                    add.addProperty(skos_note, Model.createLiteral(args[10], "en"));
                                }
                                if (combo == 2) {
                                    add.addProperty(skos_note, Model.createLiteral(args[10], "es"));
                                }
                            }
                            if (is_not_empty(args[11])) {
                                combo = Combo_note2.getSelectedIndex();
                                if (combo == 0) {
                                    add.addProperty(skos_note, Model.createLiteral(args[11], "fr"));
                                }
                                if (combo == 1) {
                                    add.addProperty(skos_note, Model.createLiteral(args[11], "en"));
                                }
                                if (combo == 2) {
                                    add.addProperty(skos_note, Model.createLiteral(args[11], "es"));
                                }
                            }
                            if (is_not_empty(args[12])) {
                                combo = Combo_note3.getSelectedIndex();
                                if (combo == 0) {
                                    add.addProperty(skos_note, Model.createLiteral(args[12], "fr"));
                                }
                                if (combo == 1) {
                                    add.addProperty(skos_note, Model.createLiteral(args[12], "en"));
                                }
                                if (combo == 2) {
                                    add.addProperty(skos_note, Model.createLiteral(args[12], "es"));
                                }
                            }
                        }
                    }
                    PrintWriter fluxModel;
                    if (!model.equals("Modèle RDF...")) {
                        try {
                            fluxModel = new PrintWriter(new FileOutputStream(model));
                            Model.write(fluxModel);
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(Editor.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        try {
                            fluxModel = new PrintWriter(new FileOutputStream("Model_ext.rdf"));
                            Model.write(fluxModel);
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(Editor.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                }
                ++i;
            }
            JOptionPane.showMessageDialog(null, "Action terminée, retour au menu principal", "Terminé !", JOptionPane.INFORMATION_MESSAGE);
            exit();
        }

        if (arg.getSource() == Suivant) {
            champs.get(position).set_entry(get_champs());
            if (position < max_entries - 1) {
                ++position;
            } else {
                position = 0;
            }
            set_champs(position);
            set_PanelMot(position);
        }

        if (arg.getSource() == Precedent) {
            champs.get(position).set_entry(get_champs());
            if (position > 0) {
                --position;
            } else {
                position = max_entries - 1;
            }
            set_champs(position);
            set_PanelMot(position);
        }

        if (arg.getSource() == item_reinit) {
            re_init();
            String msg = "Retour au début des mots clés\n";
            JOptionPane.showMessageDialog(null, msg, "ré-initialisation", JOptionPane.INFORMATION_MESSAGE);
        }

        if (arg.getSource() == item_help) {
            String msg
                    = "Cette fenêtre est la dernière étape de l'action permettant d'exploiter la liste des mots clés.\n\n"
                    + "Les mots sélectionnés apparaissent un à un dans un formulaire à remplir : \n"
                    + "\n- Modèle : Entrez ici le modèle dans lequel le mot doit être ajouté. Si laissé vide\n"
                    + "le programme génère un modèle générique \"Model_ext.rdf\".\n"
                    + "\n- URI : Seule entrée obligatoire, sans cela le mot ne sera pas ajouté. L'URI entrée doit être correcte.\n"
                    + "\n- prefLabel : Permet d'entrer jusqu'à 3 différents labels(liste déroulante de gauche) dans 3 langues\n"
                    + "(liste déroule de droite : Français, Anglais, Espagnol). Ajoute des triplets skos:prefLabel\n"
                    + "\n- altLabel : Même traitement que prefLabel. Ajoute des triplets skos:altLabel\n"
                    + "\n- fao ID : Code d'identification fao. Ajoute un triplet fad_ID.\n"
                    + "\n- Worms : Code d'identification Worms. Ajoute un triplet worms_ID.\n"
                    + "\n- Note : Permet d'entrer jusqu'à 3 notes concernant le sujet. Ajoute des triplets skos:note.\n"
                    + "\n\n\"Ignorer\" Permet de ne pas sauvegarder les champs remplis et de passer directement au mot suivant.\n"
                    + "Les informations sur un mot sont conservées et peuvent donc être modifiées a postiori.\n"
                    + "L'écriture des nouveaux triplets se fait lors de l'appuie sur \"Terminer\".\n\n\n"
                    + "ATTENTION : le fichier \"predicat.rdf\" (path /~/Bureau/Sources/Start/predicat.rdf) doit être accessible.";
            JOptionPane.showMessageDialog(null, msg, "Aide", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Ré-initialise la fenêtre. Remet les champs à 0, affiche la fenêtre à la
     * position initiale
     */
    private void re_init() {

        champs = new LinkedList<>();
        Champs_editor tmp;
        for (int i = 0; i < max_entries; ++i) {
            tmp = new Champs_editor();
            champs.add(tmp);
        }
        position = 0;
        set_PanelMot(position);
        set_champs(position);
    }

    /**
     * Set le panneau indiquant le mot à traiter à partir d'une positon (donné
     * par la position)
     *
     * @param position Indice de la racine dans "list"
     */
    private void set_PanelMot(int position) {
        String text;
        String racine = list.get(position);
        Integer cpt_racine = map_cpt.get(racine);
        HashMap<String, Integer> mots = map_mots.get(racine);
        text = racine + " : " + cpt_racine + "\n\n";
        for (Entry<String, Integer> entry : mots.entrySet()) {
            text += "\t" + entry.getKey() + " : " + entry.getValue() + "\n";
        }
        Panel_mot.setText(text);
    }

    /**
     * Set tous les champs d'après ce qui a été sauvegardé pour un mot
     *
     * @param position Mot à chargé
     */
    private void set_champs(int position) {

        Combo_alt.setSelectedIndex(0);
        Combo_alt1.setSelectedIndex(0);
        Combo_alt2.setSelectedIndex(0);
        Combo_alt3.setSelectedIndex(0);
        Combo_pref.setSelectedIndex(0);
        Combo_pref1.setSelectedIndex(0);
        Combo_pref2.setSelectedIndex(0);
        Combo_pref3.setSelectedIndex(0);
        Combo_note1.setSelectedIndex(0);
        Combo_note2.setSelectedIndex(0);
        Combo_note3.setSelectedIndex(0);

        Champs_editor tmp = champs.get(position);
        String[] args = tmp.get_args();
        if (is_not_empty(args[0])) {
            Field_URI.setText(args[0]);
        } else {
            Field_URI.setText("http://www.ecoscope.org/");
        }
        if (is_not_empty(args[1])) {
            Field_modele.setText(args[1]);
        } else {
            Field_modele.setText("Modèle RDF...");
        }
        Field_pref1.setText(args[2]);
        Field_pref2.setText(args[3]);
        Field_pref3.setText(args[4]);
        Field_alt1.setText(args[5]);
        Field_alt2.setText(args[6]);
        Field_alt3.setText(args[7]);
        Field_fao.setText(args[8]);
        Field_worms.setText(args[9]);
        Panel_Note1.setText(args[10]);
        Panel_Note2.setText(args[11]);
        Panel_Note3.setText(args[12]);
    }

    /**
     * Retourne les champs sous la forme d'un tableau de String
     *
     * @return Tableau de taille 13 contenant tous les champs du mot courant
     */
    private String[] get_champs() {
        String[] result = {Field_URI.getText(),
            Field_modele.getText(),
            Field_pref1.getText(),
            Field_pref2.getText(),
            Field_pref3.getText(),
            Field_alt1.getText(),
            Field_alt2.getText(),
            Field_alt3.getText(),
            Field_fao.getText(),
            Field_worms.getText(),
            Panel_Note1.getText(),
            Panel_Note2.getText(),
            Panel_Note3.getText()};
        return result;
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

    /**
     * Recherche dans le modèle un argument skos:prefLabel
     *
     * @param path chemin du fichier contenant le modèle
     * @return Le predicat skos:prefLabel
     */
    private Property getProperty_prefLabel(String path) {
        Model tmp = ModelFactory.createDefaultModel();
        tmp.read(path);
        Statement state;
        Resource subject;
        String s;
        StmtIterator iter = tmp.listStatements();
        while (iter.hasNext()) {
            state = iter.nextStatement();
            subject = state.getSubject();
            s = subject.toString();
            if (s.equals("http://www.skosprefLabel/Sujet")) {
                return state.getPredicate();
            }
        }
        return null;
    }

    /**
     * Recherche dans le modèle un argument skos:altLabel
     *
     * @param path chemin du fichier contenant le modèle
     * @return Le predicat skos:altLabel
     */
    private Property getProperty_altLabel(String path) {
        Model tmp = ModelFactory.createDefaultModel();
        tmp.read(path);
        Statement state;
        Resource subject;
        String s;
        StmtIterator iter = tmp.listStatements();
        while (iter.hasNext()) {
            state = iter.nextStatement();
            subject = state.getSubject();
            s = subject.toString();
            if (s.equals("http://www.skosaltLabel/Sujet")) {
                return state.getPredicate();
            }
        }
        return null;
    }

    /**
     * Recherche dans le modèle un argument ecosystems_def:faoId
     *
     * @param path chemin du fichier contenant le modèle
     * @return Le predicat ecosystems_def:faoId
     */
    private Property getProperty_faoId(String path) {
        Model tmp = ModelFactory.createDefaultModel();
        tmp.read(path);
        Statement state;
        Resource subject;
        String s;
        StmtIterator iter = tmp.listStatements();
        while (iter.hasNext()) {
            state = iter.nextStatement();
            subject = state.getSubject();
            s = subject.toString();
            if (s.equals("http://www.ecosysFaoId/Sujet")) {
                return state.getPredicate();
            }
        }
        return null;
    }

    /**
     * Recherche dans le modèle un argument ecosystems_def:worms
     *
     * @param path chemin du fichier contenant le modèle
     * @return Le predicat ecosystems_def:worms
     */
    private Property getProperty_worms(String path) {
        Model tmp = ModelFactory.createDefaultModel();
        tmp.read(path);
        Statement state;
        Resource subject;
        String s;
        StmtIterator iter = tmp.listStatements();
        while (iter.hasNext()) {
            state = iter.nextStatement();
            subject = state.getSubject();
            s = subject.toString();
            if (s.equals("http://www.ecosysWormsId/Sujet")) {
                return state.getPredicate();
            }
        }
        return null;
    }

    /**
     * Recherche dans le modèle un argument skos:note
     *
     * @param path chemin du fichier contenant le modèle
     * @return Le predicat skos:note
     */
    private Property getProperty_note(String path) {
        Model tmp = ModelFactory.createDefaultModel();
        tmp.read(path);
        Statement state;
        Resource subject;
        String s;
        StmtIterator iter = tmp.listStatements();
        while (iter.hasNext()) {
            state = iter.nextStatement();
            subject = state.getSubject();
            s = subject.toString();
            if (s.equals("http://www.skosnote/Sujet")) {
                return state.getPredicate();
            }
        }
        return null;
    }

    /**
     * Vérifie si le champs URI a bien été modifié ou si il a été laissé à http://ecoscope.org/ (aux espaces et caractères spéciaux près)
     * @param uri String entrée dans le champs URI
     * @return true si l'uri a été modifiée, false sinon.
     */
    private boolean is_valid_URI(String uri) {
        return !normalize(uri).equals("httpecoscopeorg");
    }

}
