package fr.cda.campingcar.controller;

import fr.cda.campingcar.util.Validator;
import fr.cda.campingcar.util.render.FXMLWindow;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

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

public class SendMailController extends FXMLWindow implements Initializable
{

    @FXML
    private VBox sendMailPane;

    @FXML
    private TextField emailField;

    @FXML
    private Label hintLabel;

    @FXML
    private Button validButton;

    @FXML
    private Button annulerBouton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {

        this.mainPane = this.sendMailPane;
        super.initialize(url, resourceBundle);
        this.validButton.setDisable(true);
    }

    @FXML
    private void submitForm(MouseEvent event)
    {
        String email = emailField.getText();

    }

    @Override
    public void closeWindow()
    {
        super.closeWindow();
    }

    @FXML
    public void validTextField(KeyEvent event)
    {
        String    value     =  this.emailField.getText();

        Validator.clearClass(this.emailField);
        Validator.clearClass(this.hintLabel);

        boolean isValid = Validator.isNotEmpty(value) && Validator.isMail(value);

        validButton.setDisable(!isValid);

        String style = Validator.getValidatorStyle();
        this.emailField.getStyleClass().add(style);
        this.hintLabel.getStyleClass().add(style);
        this.hintLabel.setText(Validator.getValidatorMessage());
    }
}
