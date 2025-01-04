package fr.cda.campingcar.controller;

import fr.cda.campingcar.util.FXMLRender;
import fr.cda.campingcar.util.Validator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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

public class SendMailController implements Initializable
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
        this.validButton.setDisable(true);
    }

    @FXML
    private void submitForm(MouseEvent event)
    {
        String email = emailField.getText();
        hintLabel.setText("");

        if (email.isEmpty()) {
            hintLabel.setText("Veuillez entrer un email !");
        } else if (!Validator.isMail(email)) {
            hintLabel.setText("Votre email ne respecte pas la norme RFC2822 !");
        }
    }

    @FXML
    private void closeWindow()
    {
        FXMLRender.closeWindow("window/sendMail.fxml");
    }
}
