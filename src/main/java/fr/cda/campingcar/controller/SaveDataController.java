package fr.cda.campingcar.controller;

import fr.cda.campingcar.model.BaseDonneeParam;
import fr.cda.campingcar.model.Location;
import fr.cda.campingcar.util.DebugHelper;
import fr.cda.campingcar.util.FXMLRender;
import fr.cda.campingcar.util.LoggerConfig;
import fr.cda.campingcar.util.file.BinarieFile;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/*
 * Soutenance Scraping
 * 2024/déc.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */
public class SaveDataController implements Initializable
{

    @FXML
    private VBox saveDataPane;

    @FXML
    private Button validerBouton;

    @FXML
    private Button annulerBouton;

    private static final Logger LOGGER_FILE = LoggerConfig.getLoggerFile();
    private final BinarieFile<BaseDonneeParam> paramDBBinarieFile = new BinarieFile<>("param_db");
    private BaseDonneeParam parametreDB;
    private List<Location> locationList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        Platform.runLater(() -> {
            try {

                this.parametreDB = paramDBBinarieFile.readFile();

                if (this.parametreDB == null) {
                    this.closeWindow();

                    AlertMessageController alertMessageController = FXMLRender.openNewWindow("window/alertMessage.fxml", "Sauvegarde");
                    alertMessageController.setMessage("Les paramètres de connexion à la base de données ne sont pas définis.\nVeuillez les configurer avant de continuer.", "warning");

                } else {
                    DebugHelper.debug("Ouverture Binaire file", this.parametreDB.toString(), true);
                }

            } catch (IOException e) {
                DebugHelper.debug("Ouverture Binaire file", "Erreur lors de la lecture du binaire", false);
                LOGGER_FILE.error("Erreur lors de la lecture du binaire: {}", e.getMessage());

                AlertMessageController alertMessageController = FXMLRender.openNewWindow("window/alertMessage.fxml", "Sauvegarde");
                alertMessageController.setMessage("Erreur lors de la lecture du binaire!", "error");
            }
        });

    }

    @FXML
    private void submitForm(MouseEvent event)
    {

    }

    @FXML
    private void closeWindow()
    {
        String fxml = "window/transmissionDB.fxml";
        FXMLRender.closeWindow(fxml);
    }
}
