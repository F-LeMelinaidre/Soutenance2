package fr.cda.campingcar.controller;

import fr.cda.campingcar.model.BaseDonneeParam;
import fr.cda.campingcar.util.DebugHelper;
import fr.cda.campingcar.util.render.FXMLRender;
import fr.cda.campingcar.util.LoggerConfig;
import fr.cda.campingcar.util.file.BinarieFile;
import fr.cda.campingcar.util.Validator;
import fr.cda.campingcar.util.render.FXMLWindow;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/*
 * Soutenance Scraping
 * 2024/déc.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */


public class ParametreBDController extends FXMLWindow implements Initializable
{

    @FXML
    private VBox parametreDBPane;

    @FXML
    private GridPane gridPane;

    @FXML
    private TextField serveurField;

    @FXML
    private TextField baseDeDonneeField;

    @FXML
    private TextField portField;

    @FXML
    private TextField loginField;

    @FXML
    private TextField passwordField;

    @FXML
    private Button validateButton;

    @FXML
    private Button annulerBouton;

    private static final Logger LOGGER_FILE = LoggerConfig.getLoggerFile();
    private final BinarieFile<BaseDonneeParam> paramDBBinarieFile = new BinarieFile<>("param_db");
    private BaseDonneeParam parametreDB;
    private final Map<String, Label> erreursMap = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        this.mainPane = this.parametreDBPane;
        super.initialize(url, resourceBundle);

        try {
            this.parametreDB = paramDBBinarieFile.readFile();

            if ( this.parametreDB == null ) {
                DebugHelper.debug("Ouverture Binaire file", "Le fichier binaire est vide ou inexistant", false);
            } else {
                this.serveurField.setText(this.parametreDB.getServeur());
                this.baseDeDonneeField.setText(this.parametreDB.getBaseDonnee());
                this.portField.setText(this.parametreDB.getPort().toString());
                this.loginField.setText(this.parametreDB.getLogin());
                this.passwordField.setText(this.parametreDB.getPassword());
                DebugHelper.debug("Ouverture Binaire file", this.parametreDB.toString(), true);
            }

        } catch ( IOException e ) {
            DebugHelper.debug("Ouverture Binaire file", "Erreur lors de la lecture du binaire", false);
            LOGGER_FILE.error("Erreur lors de la lecture du binaire : {}", e.getMessage());

            // TODO VOIR LA POSSIBILITé DE SUPPRIMER LE FICHIER CORROMPU POUR LAISSER LA POSSIBILITé DANS CRéER UN NOUVEAU
            AlertMessageController alertMessageController = FXMLRender.openNewWindow("window/alertMessage.fxml", "Sauvegarde");
            alertMessageController.setMessage("Erreur lors de la lecture du binaire!", "error");
        }

        this.updateValidateButton();
    }

    @Override
    public void closeWindow()
    {
        super.closeWindow();
    }

    @FXML
    private void submitForm(MouseEvent event)
    {
        if ( this.parametreDB == null ) this.parametreDB = new BaseDonneeParam();

        this.parametreDB.setServeur(
                this.serveurField.getText());
        this.parametreDB.setBaseDonnee(
                this.baseDeDonneeField.getText());
        this.parametreDB.setPort(
                Integer.parseInt(this.portField.getText()));
        this.parametreDB.setLogin(
                this.loginField.getText());
        this.parametreDB.setPassword(
                this.passwordField.getText());

        try {
            this.paramDBBinarieFile.writeFile(this.parametreDB);
            LOGGER_FILE.info("Succès de l'écriture du fichier : {}", this.parametreDB.toString());
            DebugHelper.debug("Écriture Fichier Binaire", "Succès: {}" + this.parametreDB.toString(), true);

            this.closeWindow();

            AlertMessageController alertMessageController = FXMLRender.openNewWindow("window/alertMessage.fxml", "Sauvegarde");
            alertMessageController.setMessage("Paramètres sauvegardés avec succès.", "valid");

        } catch ( IOException e ) {
            LOGGER_FILE.info("Erreur lors de l'écriture des données : {}", this.parametreDB.toString(), e);
            DebugHelper.debug("Écriture Fichier Binaire", "Erreur" + e.getMessage(), false);

            this.closeWindow();

            AlertMessageController alertMessageController = FXMLRender.openNewWindow("window/alertMessage.fxml", "Sauvegarde");
            alertMessageController.setMessage("Erreur lors de l'écriture des données!", "error");
        }
    }

    @FXML
    private void validTextField(KeyEvent event)
    {
        TextField textField = (TextField) event.getSource();
        Integer rowId = GridPane.getRowIndex(textField);
        String fxId = textField.getId();
        String value = textField.getText();

        Validator.clearClass(textField);

        boolean isValid;

        // Validation basée sur l'ID du champ de texte
        switch (fxId) {
            case "serveurField":
                isValid = Validator.isNotEmpty(value) && Validator.isValidServerName(value);
                break;
            case "baseDeDonneeField":
                isValid = Validator.isNotEmpty(value) && Validator.isValidDatabaseName(value);
                break;
            case "portField":
                if ( Validator.isNotEmpty(value) && Validator.isNumeric(value) ) {
                    int port = Integer.parseInt(value);
                    isValid = Validator.isValidPort(port);
                } else {
                    isValid = false;
                }
                break;
            case "loginField":
                isValid = Validator.isNotEmpty(value) && Validator.isValidMySQLLogin(value);
                break;
            case "passwordField":
                isValid = Validator.isNotEmpty(value) && Validator.isValidMySQLPassword(value);
                break;
            default:
                isValid = false;
        }

        String style = Validator.getValidatorStyle();
        textField.getStyleClass().add(style);

        this.updateErrorLabel(fxId, rowId, isValid);

        this.validateButton.setDisable(!erreursMap.isEmpty());
    }

    private void updateErrorLabel(String fxId, Integer rowId, boolean isValid)
    {
        TextField textField = (TextField) this.gridPane.lookup("#" + fxId);


        Label current = this.erreursMap.get(fxId);
        if ( current != null ) {
            this.gridPane.getChildren().remove(current);
            this.erreursMap.remove(fxId);
        }

        if ( !isValid ) {
            Label hint = Validator.getHintLabel();
            this.erreursMap.put(fxId, hint);
            GridPane.setRowIndex(hint, rowId + 1);
            GridPane.setColumnIndex(hint, 0);
            GridPane.setColumnSpan(hint, this.gridPane.getColumnCount());
            this.gridPane.getChildren().add(hint);
        }
    }

    private void updateValidateButton()
    {
        TextField[] fields = { this.serveurField, this.baseDeDonneeField, this.portField, this.loginField, this.passwordField };

        boolean disable = false;
        for ( TextField field : fields ) {
            if ( field.getText() == null || field.getText().isEmpty() ) {
                disable = true;
                break;
            }
        }

        this.validateButton.setDisable(disable);
    }

}
