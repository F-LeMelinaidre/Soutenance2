package fr.cda.campingcar.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/*
 * Soutenance Scraping
 * 2024/déc.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */


public class ParametreBDController implements Initializable, ControllerRegistry
{
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
    private Button validerBouton;

    @FXML
    private Button fermerBouton;

    @Override
    public void setMainController(Object parentController)
    {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        this.serveurField.setPromptText("localhost");
        this.baseDeDonneeField.setPromptText("thousand_miles_web");
        this.portField.setPromptText("3306");
        this.loginField.setPromptText("admin");
        this.passwordField.setPromptText("admin");

    }

    @FXML
    private void closeWindow(MouseEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void submitForm(MouseEvent event) {

    }
}
