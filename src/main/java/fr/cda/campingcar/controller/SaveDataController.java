package fr.cda.campingcar.controller;

import fr.cda.campingcar.dao.web.WebDAOFactory;
import fr.cda.campingcar.model.DataBaseParameter;
import fr.cda.campingcar.model.Rent;
import fr.cda.campingcar.util.DebugHelper;
import fr.cda.campingcar.util.render.FXMLRender;
import fr.cda.campingcar.util.LoggerConfig;
import fr.cda.campingcar.util.file.BinarieFile;
import fr.cda.campingcar.util.render.FXMLWindow;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
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
    private static final Logger LOGGER_DAO = LoggerConfig.getLoggerDaoError();
    private WebDAOFactory daoFactory;
    private final BinarieFile<DataBaseParameter> dataBaseBinarie = new BinarieFile<>("param_db");
    private DataBaseParameter dataBaseParameter;
    private List<Rent> rentList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        this.mainPane = this.saveDataPane;
        super.initialize(url, resourceBundle);

        this.loadDataBaseParameter();

    }

    private void loadDataBaseParameter()
    {
        Platform.runLater(() -> {
            try {

                this.dataBaseParameter = dataBaseBinarie.readFile();

                if ( this.dataBaseParameter == null ) {
                    this.closeWindow();

                    AlertMessageController alertMessageController = FXMLRender.openNewWindow("window/alertMessage.fxml", "Sauvegarde");
                    alertMessageController.setMessage(
                            "Les paramètres de connexion à la base de données ne sont pas définis.\nVeuillez les configurer.", "warning");

                } else {
                    DebugHelper.debug("Ouverture Binaire file", this.dataBaseParameter.toString(), true);

                    this.daoFactory = WebDAOFactory.getInstance(this.dataBaseParameter);
                }

            } catch ( IOException e ) {
                DebugHelper.debug("Ouverture Binaire file", "Erreur lors de la lecture du binaire", false);
                LOGGER_FILE.error("Erreur lors de la lecture du binaire: {}", e.getMessage());

                AlertMessageController alertMessageController = FXMLRender.openNewWindow("window/alertMessage.fxml", "Sauvegarde");
                alertMessageController.setMessage("Erreur lors de la lecture du binaire!", "error");
            }
        });
    }

    public void setSearchResult(List<Rent> search)
    {
        this.rentList = search;
    }

    @FXML
    private void submitForm(MouseEvent event)
    {
        Task<Boolean> saveRentsTask = new Task<>()
        {
            @Override
            protected Boolean call() throws SQLException
            {
                return daoFactory.getRentDAO().saveAll(rentList);
            }
        };
        //LOGGER_DAO.error("Erreur DAO à la synchronisation", e.getMessage());
        AlertMessageController alertMessageController = FXMLRender.openNewWindow("window/alertMessage.fxml", "Synchronisation Des Données");

        saveRentsTask.setOnSucceeded(e -> {
            Boolean result = saveRentsTask.getValue();
            if (result) {
                alertMessageController.setMessage("Succès de la synchronisation", "valid");
                LOGGER_DAO.info("Succès de la synchronisation.");
            } else {
                alertMessageController.setMessage("Échec de la synchronisation", "valid");
                LOGGER_DAO.error("Échec de la synchronisation.");
            }
        });

        saveRentsTask.setOnFailed(e -> {
            Throwable exception = saveRentsTask.getException();
            alertMessageController.setMessage("Échec de la synchronisation", "valid");
            LOGGER_DAO.error("Échec de la synchronisation.", exception);
        });

        saveRentsTask.setOnScheduled(e -> {
            this.closeWindow();
            alertMessageController.setMessage("Synchronisation en cours.", "load");
            LOGGER_DAO.info("La tâche de sauvegarde des locations a démarré.");
        });

        new Thread(saveRentsTask).start();

    }

    @Override
    public void closeWindow()
    {
        super.closeWindow();
    }
}
