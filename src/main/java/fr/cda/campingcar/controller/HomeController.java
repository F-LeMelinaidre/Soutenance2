/*
 * Soutenance Scraping
 * 11/2024
 *
 * Le Mélinaidre Frédéric
 * Formation CDA
 * Greta Vannes
 */

package fr.cda.campingcar.controller;

import fr.cda.campingcar.model.Rent;
import fr.cda.campingcar.model.Search;
import fr.cda.campingcar.scraping.ScrapingManager;
import fr.cda.campingcar.scraping.ScrapingModel;
import fr.cda.campingcar.settings.Config;
import fr.cda.campingcar.util.render.FXMLRender;
import fr.cda.campingcar.util.file.tamplate.word.SearchRentXDOC;
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
    private List<Rent> results;
    private SearchController searchController;
    private Search<Rent> search;
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
            case "paramDB":
                FXMLRender.openNewWindow("window/parameterDB.fxml", "Paramètres Base de Donnée");
                break;
            case "saveFile":
                if(this.search != null && this.search.getResults().size() > 0 ) {
                    this.saveSearch();
                } else {
                    String msg = (this.search == null) ? "Aucune recherche n'a été effectué!" : "Aucune annonce a sauvegarder!";
                    AlertMessageController alertMessageController = FXMLRender.openNewWindow("window/alertMessage.fxml", "Sauvegarde de la recherche");
                    alertMessageController.setMessage(msg, "warning");
                }
                break;
            case "saveInDataBase":
                if(this.search != null && this.search.getResults().size() > 0) {
                    SaveDataController saveDataController = FXMLRender.openNewWindow("window/transmissionDB.fxml",
                                                                                     "Sauvegarder En Base de Donnée");
                    saveDataController.setSearchResult((List<Rent>) this.search.getResults());
                } else {
                    String msg = (this.search == null) ? "Aucune recherche n'a été effectué!" : "Aucune annonce a sauvegarder!";
                    AlertMessageController alertMessageController = FXMLRender.openNewWindow("window/alertMessage.fxml", "Sauvegarde de la recherche");
                    alertMessageController.setMessage(msg, "warning");
                }
                break;
            case "sendMail":
                SendMailController sendMailController = FXMLRender.openNewWindow("window/sendMail.fxml", "Envoi Email");
                if(this.search != null) {
                    sendMailController.setSearchResult(this.search);
                } else {
                    AlertMessageController alertMessageController = FXMLRender.openNewWindow("window/alertMessage.fxml", "Sauvegarde de la recherche");
                    alertMessageController.setMessage("Aucune recherche n'a été effectué!", "warning");
                }
                break;
            case "quit":
            default:
                break;
        }
    }

    private void saveSearch() {
        FileChooser fileDialog = new FileChooser();
        fileDialog.setTitle("Save file");
        fileDialog.setInitialDirectory(new File(Config.DEFAULT_FILE_PATH));
        fileDialog.setInitialFileName(Config.DEFAULT_XDOC_NAME);
        FileChooser.ExtensionFilter filterText = new FileChooser.ExtensionFilter("Fichiers texte (*.docx)", "*.docx");
        FileChooser.ExtensionFilter filterAll = new FileChooser.ExtensionFilter("Tous les fichiers (*.*)", "*.*");
        fileDialog.getExtensionFilters().addAll(filterText, filterAll);

        File file = fileDialog.showSaveDialog(this.homePane.getScene().getWindow());

        if (file != null) {
            String filePath = file.getAbsolutePath();
            Task<Void> saveDocumentTask = new SearchRentXDOC(this.search, filePath).save();

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


    public void startScrapping(Search<Rent> recherche)
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
            this.search = null;
        }
    }
}