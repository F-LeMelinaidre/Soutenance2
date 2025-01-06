package fr.cda.campingcar.controller;

import fr.cda.campingcar.model.DataBaseParameter;
import fr.cda.campingcar.model.Rent;
import fr.cda.campingcar.util.DebugHelper;
import fr.cda.campingcar.util.render.FXMLRender;
import fr.cda.campingcar.util.LoggerConfig;
import fr.cda.campingcar.util.file.BinarieFile;
import fr.cda.campingcar.util.render.FXMLWindow;
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
public class SaveDataController extends FXMLWindow implements Initializable
{

    @FXML
    private VBox saveDataPane;

    @FXML
    private Button validButton;

    @FXML
    private Button cancelButton;

    private static final Logger LOGGER_FILE = LoggerConfig.getLoggerFile();
    private final BinarieFile<DataBaseParameter> dataBaseBinarie = new BinarieFile<>("param_db");
    private DataBaseParameter dataBaseParameter;
    private List<Rent> rentList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {

        this.mainPane = this.saveDataPane;
        super.initialize(url, resourceBundle);

        Platform.runLater(() -> {
            try {

                this.dataBaseParameter = dataBaseBinarie.readFile();

                if ( this.dataBaseParameter == null) {
                    this.closeWindow();

                    AlertMessageController alertMessageController = FXMLRender.openNewWindow("window/alertMessage.fxml", "Sauvegarde");
                    alertMessageController.setMessage("Les paramètres de connexion à la base de données ne sont pas définis.\nVeuillez les configurer.", "warning");

                } else {
                    DebugHelper.debug("Ouverture Binaire file", this.dataBaseParameter.toString(), true);
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

    @Override
    public void closeWindow()
    {
        super.closeWindow();
    }
}
