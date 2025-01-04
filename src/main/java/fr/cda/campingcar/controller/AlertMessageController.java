package fr.cda.campingcar.controller;

import fr.cda.campingcar.util.FXMLRender;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
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
public class AlertMessageController implements Initializable
{
    @FXML
    private Pane alertMessagePane;
    @FXML
    private Pane warningIcon;
    @FXML
    private Pane errorIcon;
    @FXML
    private Pane validIcon;
    @FXML
    private Label messageLabel;
    @FXML
    private Button closeButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        this.disableIcon();
    }

    @FXML
    public void closeWindow()
    {
        FXMLRender.closeWindow("window/alertMessage.fxml");
    }

    public void setMessage(String message, String icon)
    {
        this.disableIcon();
        this.messageLabel.setText(message);

        switch (icon) {
            case "valid":
                this.validIcon.setVisible(true);
                break;
            case "warning":
                this.warningIcon.setVisible(true);
                break;
            case "error":
                this.errorIcon.setVisible(true);
                break;
            default:
                this.errorIcon.setVisible(true);
                this.messageLabel.setText(this.messageLabel.getText() + "\n Le parametre icon de la methode est inconnu !");
                break;
        }
        Stage stage = (Stage) this.alertMessagePane.getScene().getWindow();
        stage.sizeToScene();
    }

    private void disableIcon()
    {
        this.warningIcon.setVisible(false);
        this.errorIcon.setVisible(false);
        this.validIcon.setVisible(false);
    }
}
