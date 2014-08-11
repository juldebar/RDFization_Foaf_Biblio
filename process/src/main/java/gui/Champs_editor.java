/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

/**
 * Classe correspondant à un objet manipulé dans Editor.
 * Contient tous les champs de la fenêtre Editor
 * @author jimmy
 */
public class Champs_editor {

    private String uri;
    private String modele;
    private String pref1;
    private String pref2;
    private String pref3;
    private String alt1;
    private String alt2;
    private String alt3;
    private String fao;
    private String worms;
    private String note1;
    private String note2;
    private String note3;

    public Champs_editor() {
        uri = "";
        modele = "";
        pref1 = "";
        pref2 = "";
        pref3 = "";
        alt1 = "";
        alt2 = "";
        alt3 = "";
        fao = "";
        worms = "";
        note1 = "";
        note2 = "";
        note3 = "";
    }

    /**
     * Retourne un tableau de String de taille 13 contenant tous les attributs
     * @return tableau de String de taille 13 contenant tous les attributs
     */
    public String[] get_args() {
        String[] result = {getUri(), getModele(), getPref1(), getPref2(), getPref3(), getAlt1(), getAlt2(), getAlt3(), getFao(), getWorms(), getNote1(), getNote2(), getNote3()};
        return result;
    }

    /**
     * Met à jour tous les arguments à partir d'un tableau de taille 13
     * @param args Tableau de String de taille 13
     */
    public void set_entry(String[] args) {
        if (args.length == 13) {
            setUri(args[0]);
            setModele(args[1]);
            setPref1(args[2]);
            setPref2(args[3]);
            setPref3(args[4]);
            setAlt1(args[5]);
            setAlt2(args[6]);
            setAlt3(args[7]);
            setFao(args[8]);
            setWorms(args[9]);
            setNote1(args[10]);
            setNote2(args[11]);
            setNote3(args[12]);
        }
    }

    /**
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * @param uri the uri to set
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * @return the modele
     */
    public String getModele() {
        return modele;
    }

    /**
     * @param modele the modele to set
     */
    public void setModele(String modele) {
        this.modele = modele;
    }

    /**
     * @return the pref1
     */
    public String getPref1() {
        return pref1;
    }

    /**
     * @param pref1 the pref1 to set
     */
    public void setPref1(String pref1) {
        this.pref1 = pref1;
    }

    /**
     * @return the pref2
     */
    public String getPref2() {
        return pref2;
    }

    /**
     * @param pref2 the pref2 to set
     */
    public void setPref2(String pref2) {
        this.pref2 = pref2;
    }

    /**
     * @return the pref3
     */
    public String getPref3() {
        return pref3;
    }

    /**
     * @param pref3 the pref3 to set
     */
    public void setPref3(String pref3) {
        this.pref3 = pref3;
    }

    /**
     * @return the alt1
     */
    public String getAlt1() {
        return alt1;
    }

    /**
     * @param alt1 the alt1 to set
     */
    public void setAlt1(String alt1) {
        this.alt1 = alt1;
    }

    /**
     * @return the alt2
     */
    public String getAlt2() {
        return alt2;
    }

    /**
     * @param alt2 the alt2 to set
     */
    public void setAlt2(String alt2) {
        this.alt2 = alt2;
    }

    /**
     * @return the alt3
     */
    public String getAlt3() {
        return alt3;
    }

    /**
     * @param alt3 the alt3 to set
     */
    public void setAlt3(String alt3) {
        this.alt3 = alt3;
    }

    /**
     * @return the fao
     */
    public String getFao() {
        return fao;
    }

    /**
     * @param fao the fao to set
     */
    public void setFao(String fao) {
        this.fao = fao;
    }

    /**
     * @return the worms
     */
    public String getWorms() {
        return worms;
    }

    /**
     * @param worms the worms to set
     */
    public void setWorms(String worms) {
        this.worms = worms;
    }

    /**
     * @return the note1
     */
    public String getNote1() {
        return note1;
    }

    /**
     * @param note1 the note1 to set
     */
    public void setNote1(String note1) {
        this.note1 = note1;
    }

    /**
     * @return the note2
     */
    public String getNote2() {
        return note2;
    }

    /**
     * @param note2 the note2 to set
     */
    public void setNote2(String note2) {
        this.note2 = note2;
    }

    /**
     * @return the note3
     */
    public String getNote3() {
        return note3;
    }

    /**
     * @param note3 the note3 to set
     */
    public void setNote3(String note3) {
        this.note3 = note3;
    }
}
