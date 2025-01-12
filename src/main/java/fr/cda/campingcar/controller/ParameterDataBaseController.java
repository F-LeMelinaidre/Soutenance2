package fr.cda.campingcar.controller;

import fr.cda.campingcar.model.DataBaseParameter;
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


public class ParameterDataBaseController extends FXMLWindow implements Initializable
{

    @FXML
    private VBox parameterDataBasePane;

    @FXML
    private GridPane gridPane;

    @FXML
    private TextField serverField;

    @FXML
    private TextField dataBaseField;

    @FXML
    private TextField portField;

    @FXML
    private TextField loginField;

    @FXML
    private TextField passwordField;

    @FXML
    private Button validateButton;

    @FXML
    private Button cancelBouton;

    private static final Logger LOGGER_FILE = LoggerConfig.getLoggerFile();
    private final BinarieFile<DataBaseParameter> parameterDataBaseBinarie = new BinarieFile<>("param_db");
    private DataBaseParameter dataBaseParameter;
    private final Map<String, Label> errorsMap = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        this.mainPane = this.parameterDataBasePane;
        super.initialize(url, resourceBundle);

        try {
            this.dataBaseParameter = parameterDataBaseBinarie.readFile();

            if ( this.dataBaseParameter == null ) {
                DebugHelper.debug("Ouverture Binaire file", "Le fichier binaire est vide ou inexistant", false);
            } else {
                this.serverField.setText(this.dataBaseParameter.getServer());
                this.dataBaseField.setText(this.dataBaseParameter.getDataBase());
                this.portField.setText(this.dataBaseParameter.getPort().toString());
                this.loginField.setText(this.dataBaseParameter.getLogin());
                this.passwordField.setText(this.dataBaseParameter.getPassword());
                DebugHelper.debug("Ouverture Binaire file", this.dataBaseParameter.toString(), true);
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
        if ( this.dataBaseParameter == null ) this.dataBaseParameter = new DataBaseParameter();

        this.dataBaseParameter.setServer(
                this.serverField.getText());
        this.dataBaseParameter.setDataBase(
                this.dataBaseField.getText());
        this.dataBaseParameter.setPort(
                Integer.parseInt(this.portField.getText()));
        this.dataBaseParameter.setLogin(
                this.loginField.getText());
        this.dataBaseParameter.setPassword(
                this.passwordField.getText());

        try {
            this.parameterDataBaseBinarie.writeFile(this.dataBaseParameter);
            LOGGER_FILE.info("Succès de l'écriture du fichier : {}", this.dataBaseParameter.toString());
            DebugHelper.debug("Écriture Fichier Binaire", "Succès: {}" + this.dataBaseParameter.toString(), true);

            this.closeWindow();

            AlertMessageController alertMessageController = FXMLRender.openNewWindow("window/alertMessage.fxml", "Sauvegarde");
            alertMessageController.setMessage("Paramètres sauvegardés avec succès.", "valid");

        } catch ( IOException e ) {
            LOGGER_FILE.info("Erreur lors de l'écriture des données : {}", this.dataBaseParameter.toString(), e);
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
            case "serverField":
                isValid = Validator.isNotEmpty(value) && Validator.isValidServerName(value);
                break;
            case "dataBaseField":
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

        this.updateValidateButton();
    }

    private void updateErrorLabel(String fxId, Integer rowId, boolean isValid)
    {

        System.out.println(isValid);
        System.out.println(fxId);
        System.out.println(rowId);

        Label current = this.errorsMap.get(fxId);
        if ( current != null ) {
            this.gridPane.getChildren().remove(current);
            this.errorsMap.remove(fxId);
        }

        if ( !isValid ) {
            Label hint = Validator.getHintLabel();
            this.errorsMap.put(fxId, hint);
            GridPane.setRowIndex(hint, rowId + 1);
            GridPane.setColumnIndex(hint, 0);
            GridPane.setColumnSpan(hint, this.gridPane.getColumnCount());
            this.gridPane.getChildren().add(hint);
        }
    }

    private void updateValidateButton()
    {
        TextField[] fields = { this.serverField, this.dataBaseField, this.portField, this.loginField, this.passwordField };

        boolean notEmpty = false;
        for ( TextField field : fields ) {
            if ( field.getText() == null || field.getText().isEmpty() ) {
                notEmpty = true;
                break;
            }
        }


        boolean disable = notEmpty && this.errorsMap.isEmpty();
        this.validateButton.setDisable(disable);
    }

}
