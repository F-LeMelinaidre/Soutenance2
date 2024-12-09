/*
 * Soutenance Scraping
 * 11/2024
 *
 * Le Mélinaidre Frédéric
 * Formation CDA
 * Greta Vannes
 */

package fr.cda.campingcar.controller;

import fr.cda.campingcar.model.Annonce;
import fr.cda.campingcar.model.Recherche;
import fr.cda.campingcar.scraping.ScrapingManager;
import fr.cda.campingcar.util.FXMLRender;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;

import java.util.List;

public class HomeController {

    @FXML
    private VBox homePane;
    @FXML
    private MenuItem saveFile;
    @FXML
    private MenuItem saveInDataBase;
    @FXML
    private MenuItem sendMail;
    @FXML
    private MenuItem paramDB;
    @FXML
    private MenuItem quit;

    private FXMLRender fxmlRender;

    @FXML
    public void initialize() {
        this.fxmlRender = new FXMLRender(this.homePane, this);
        this.fxmlRender.loadFXML("search.fxml", null);
    }

    @FXML
    public void handlerMenuBar(ActionEvent event) {
        MenuItem item = (MenuItem) event.getSource();

        switch (item.getId()) {
            case "saveFile":
            case "saveInDataBase":
                this.fxmlRender.openNewWindow("transmissionDB.fxml", "Sauvegarder En Base de Donnée", 450, 152);
                break;
            case "sendMail":
                this.fxmlRender.openNewWindow("sendMail.fxml", "Envoi Email", 400, 175);
                break;
            case "paramDB":
                this.fxmlRender.openNewWindow("parameterDB.fxml", "Paramètres Base de Donnée", 450, 315);
                break;
            case "quit":
            default:
                break;
        }
    }

    public void afficher() {

    }

    public void startScrapping(Recherche recherche)
    {

        ScrapingManager scrapingManager = ScrapingManager.getInstance();

        Task<Void> scrapingTask = scrapingManager.scrapTask(recherche);
        scrapingTask.setOnSucceeded(event -> {

            List<Annonce> resultats = recherche.getResultats();

            ResultatController resultatController = fxmlRender.loadFXML("resultat.fxml", null);
            if(resultatController != null) {
                resultatController.setAnnonce(resultats);
            }
            System.out.println("Scraping terminé avec succès !");
        });
        scrapingTask.setOnFailed(event -> System.out.println("Une erreur est survenue : " + scrapingTask.getException()));

        new Thread(scrapingTask).start();

    }
}