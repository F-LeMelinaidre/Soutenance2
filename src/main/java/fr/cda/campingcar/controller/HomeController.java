/*
 * Soutenance Scraping
 * 11/2024
 *
 * Le Mélinaidre Frédéric
 * Formation CDA
 * Greta Vannes
 */

package fr.cda.campingcar.controller;

import fr.cda.campingcar.model.Search;
import fr.cda.campingcar.scraping.ScrapingManager;
import fr.cda.campingcar.scraping.ScrapingModel;
import fr.cda.campingcar.util.render.FXMLRender;
import fr.cda.campingcar.util.file.tamplate.word.RechercheXDOC;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
    private ResultController resultController;
    private List<ScrapingModel<Object>> results;
    private SearchController searchController;
    private Search<ScrapingModel<Object>> search;
    private Pane currentResultView;

    @FXML
    public void initialize()
    {
        this.fxmlRender  = new FXMLRender(this.homePane, this);
        searchController = this.fxmlRender.loadAtPosition("component/search.fxml", 2);
    }

    @FXML
    public void handlerMenuBar(ActionEvent event) throws IOException
    {
        MenuItem item = (MenuItem) event.getSource();

        switch (item.getId()) {
            case "saveFile":
                this.saveSearch();
                /*System.out.println(Config.CYAN + " Resultats :");
                if(this.resultats != null) {
                    for(ScrapingModelInt<Object> model : this.resultats) {
                        System.out.println(Config.YELLOW + model.getSite().getName());
                    }
                }*/
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

    private void saveSearch() {
        FileChooser fileDialog = new FileChooser();
        fileDialog.setTitle("Save file");
        File file = fileDialog.showSaveDialog(this.homePane.getScene().getWindow());

        if (file != null) {
            String filePath = file.getAbsolutePath();
            Task<Void> saveDocumentTask = new RechercheXDOC(this.search, filePath).save();

            AlertMessageController alertMessageController = FXMLRender.openNewWindow("window/alertMessage.fxml", "Sauvegarde");

            saveDocumentTask.setOnScheduled(event -> {
                alertMessageController.setMessage("Enregistrement du fichier:\n" + filePath + " en cours.", "load");
            });

            saveDocumentTask.setOnSucceeded(event -> {
                alertMessageController.setMessage("Succès de l'enregistrement du fichier:\n" + filePath, "valid");
            });

            saveDocumentTask.setOnFailed(event -> {
                alertMessageController.setMessage("Échec de l'enregistrement du fichier:\n" + filePath, "error");
            });

            Thread saveThread = new Thread(saveDocumentTask);
            saveThread.setDaemon(true);
            saveThread.start();
        }
    }


    public void startScrapping(Search<ScrapingModel<Object>> recherche)
    {
        this.search = recherche;

        if ( this.resultController == null) this.resultController = this.fxmlRender.loadFXML("resultat.fxml", this.mainContainer);
        this.resultController.clear();

        Task<Void> scrapingTask = new ScrapingManager(recherche).scrapTask();

        scrapingTask.setOnScheduled(event -> {
            if(this.loaderController == null) {
                this.loaderController = this.fxmlRender.loadFXML("component/loader.fxml", this.mainContainer);
            }
            this.loaderController.setTitleMainCounter("de la recherche", "Site");
        });

        scrapingTask.setOnSucceeded(event -> {
            this.loaderController.setTitleMainCounter("du chargement", "Annonce");
            this.results = this.search.getResults();
            this.resultController.load(this.results);
        });

        scrapingTask.setOnFailed(event -> {
            AlertMessageController alertMessageController = FXMLRender.openNewWindow("window/alertMessage.fxml", "Sauvegarde");
            alertMessageController.setMessage("Échec de la recherche", "error");
            this.searchController.toggleDisableForm();
        });

        Thread scrapingThread = new Thread(scrapingTask);
        scrapingThread.setDaemon(true);
        scrapingThread.start();

    }

    public void showResult(Pane view) {
        this.searchController.toggleDisableForm();
        this.loaderController.hide();
    }

    public void clearScrapResult() {
        if( this.resultController != null) {
            this.resultController.clear();
        }
    }
}