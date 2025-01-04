/*
 * Soutenance Scraping
 * 11/2024
 *
 * Le Mélinaidre Frédéric
 * Formation CDA
 * Greta Vannes
 */

package fr.cda.campingcar.controller;

import fr.cda.campingcar.model.Recherche;
import fr.cda.campingcar.scraping.ScrapingManager;
import fr.cda.campingcar.scraping.ScrapingModelInt;
import fr.cda.campingcar.util.FXMLRender;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class HomeController
{

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
    @FXML
    private StackPane mainContainer;

    private FXMLRender fxmlRender;
    private LoaderController loaderController;
    private ResultatController resultatController;
    private List<ScrapingModelInt<Object>> resultats;
    private SearchController searchController;

    @FXML
    public void initialize()
    {
        this.fxmlRender  = new FXMLRender(this.homePane, this);
        searchController = this.fxmlRender.loadAtPosition("component/search.fxml", 2);
    }

    @FXML
    public void handlerMenuBar(ActionEvent event)
    {
        MenuItem item = (MenuItem) event.getSource();

        switch (item.getId()) {
            case "saveFile":
                FileChooser fileDialog = new FileChooser();
                fileDialog.setTitle("Save file");
                fileDialog.showSaveDialog(this.homePane.getScene().getWindow());
                break;
            case "saveInDataBase":
                FXMLRender.openNewWindow("window/transmissionDB.fxml", "Sauvegarder En Base de Donnée");
                break;
            case "sendMail":
                FXMLRender.openNewWindow("window/sendMail.fxml", "Envoi Email");
                break;
            case "paramDB":
                FXMLRender.openNewWindow("window/parameterDB.fxml", "Paramètres Base de Donnée");
                break;
            case "quit":
            default:
                break;
        }
    }


    public void startScrapping(Recherche<ScrapingModelInt<Object>> recherche)
    {

        if (this.resultatController == null) this.resultatController = this.fxmlRender.loadFXML("resultat.fxml", this.mainContainer);

        Task<Void> scrapingTask = new ScrapingManager(recherche).scrapTask();

        scrapingTask.setOnScheduled(event -> {
            if(this.loaderController == null) {
                this.loaderController = this.fxmlRender.loadFXML("component/loader.fxml", this.mainContainer);
            }
            this.loaderController.setTitleMainCounter("de la recherche", "Site");
        });

        scrapingTask.setOnSucceeded(event -> {
            CompletableFuture.runAsync(() -> {
                // Travail du second thread
                System.out.println("Traitement des résultats...");
            }).thenRun(() -> {
                // Action à exécuter une fois le travail terminé
            });
            this.loaderController.setTitleMainCounter("du chargement", "Annonce");
            this.resultats = recherche.getResultats();
            this.resultatController.loadAndShow(this.resultats, () -> this.searchController.toggleDisableForm());

        });

        Thread scrapingThread = new Thread(scrapingTask);
        scrapingThread.setDaemon(true);
        scrapingThread.start();

    }

    public void clearResultat() {
        if(this.resultatController != null) {
            this.resultatController.clear();
        }
    }
}