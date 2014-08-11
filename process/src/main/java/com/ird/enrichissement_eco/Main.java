package com.ird.enrichissement_eco;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import static com.ird.enrichissement_eco.EnrichissementBiblio.enrichissement;
import static com.ird.enrichissement_eco.ExtractionAgent.extraction_agent;
import static com.ird.enrichissement_eco.Zotero.export_to_zotero;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Programme permettant l'enrichissement d'un fichier de bibliographie issue de
 * Zotero (format RDF) et d'un fichier d'agents (format RDF). Le programme crée
 * deux fichiers : ModelBiblio_out.rdf et ModelAgents_out.rdf
 * ModelBiblio_out.rdf contient l'ensemble des références bibliographiques
 * contenus dans le fichier Zotero avec pour chacune (lorsque c'est possible) :
 * * Son titre * Ses auteurs (URI du fichier ModelAgent_out.rdf) * Sa date de
 * publication * Un résumé * Un lien vers la ressource en ligne
 *
 * ModelAgent_out.rdf contient, pour chaque agent, l'ensemble de ses
 * publications (URI du fichier ModelBiblio_out.rdf)
 *
 * @author jimmy
 */
public class Main {

    /**
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        //Location of files 
        String file_agents = "/home/jimmy/Bureau/Sources/Start/toto.csv"; //Chemin du fichier csv

        //location of models
        String refBiblio = "file:/home/jimmy/Bureau/Sources/Start/Zbibliotontology.rdf";
        String agentEcoscope = "file:/home/jimmy/Bureau/Sources/Start/agents.owl";

        // Les modèles dans lesquels on importe les triplets RDF des fichiers du dessus
        //UNIQUEMENT EN LECTURE
        Model ModelBiblio = ModelFactory.createDefaultModel();
        Model ModelAgent = ModelFactory.createDefaultModel();

        //UNIQUEMENT EN ECRITURE
        Model ModelZotero = ModelFactory.createDefaultModel();
        Model ModelBiblio_out = ModelFactory.createDefaultModel();
        Model ModelAgent_out = ModelFactory.createDefaultModel();

        //On alimente les modèles créés. Load bibliographic ref into the model ModelBiblio
        ModelBiblio.read(refBiblio);

        //Pour Agent, deux versions possibles : read d'un fichier et utilisation d'extractionAgent depuis un fichier csv
        ModelAgent.read(agentEcoscope);

        Model Model_mesAgents = ModelFactory.createDefaultModel();
        extraction_agent(Model_mesAgents, file_agents);
        try {
            PrintWriter fluxMes_agents = new PrintWriter(new FileOutputStream("mes_agent.rdf"));
            Model_mesAgents.write(fluxMes_agents);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Crée un fichier exploitable par Zotero sans doublons
        export_to_zotero(ModelBiblio, ModelZotero);
        try {
            PrintWriter fluxZotero = new PrintWriter(new FileOutputStream("Zotero.rdf"));
            ModelZotero.write(fluxZotero);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Mise en lien de ModelBiblio et Model Agent
        enrichissement(ModelBiblio, ModelAgent, ModelBiblio_out, ModelAgent_out);
        enrichissement(ModelBiblio, Model_mesAgents, ModelBiblio_out, ModelAgent_out);
        try {
            PrintWriter fluxModelBiblio = new PrintWriter(new FileOutputStream("Biblio_ext.rdf"));
            ModelBiblio_out.write(fluxModelBiblio);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            PrintWriter fluxModelAgent = new PrintWriter(new FileOutputStream("Agents_ext.rdf"));
            ModelAgent_out.write(fluxModelAgent);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
