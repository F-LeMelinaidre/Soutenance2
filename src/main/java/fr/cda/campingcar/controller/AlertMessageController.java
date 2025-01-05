package fr.cda.campingcar.controller;

import fr.cda.campingcar.util.render.FXMLWindow;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Pane;

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
public class AlertMessageController extends FXMLWindow
{
    @FXML
    private Pane alertMessagePane;
    @FXML
    private ProgressIndicator progressIndicator;
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
    private boolean buttonActived;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        this.mainPane = alertMessagePane;
        super.initialize(url, resourceBundle);
        this.disableIcon();
    }

    @Override
    public void closeWindow()
    {
        super.closeWindow();
    }

    public void setMessage(String message, String icon)
    {
        this.disableIcon();
        this.messageLabel.setText(message);
        this.closeButton.setDisable(false);
        this.closeButton.setVisible(true);

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
            case "load":
                this.progressIndicator.setVisible(true);
                this.closeButton.setVisible(false);
                break;
            default:
                this.errorIcon.setVisible(true);
                this.messageLabel.setText(this.messageLabel.getText() + "\n Le parametre icon de la methode est inconnu !");
                break;
        }
        this.refreshWindow();
    }

    private void disableIcon()
    {
        this.warningIcon.setVisible(false);
        this.errorIcon.setVisible(false);
        this.validIcon.setVisible(false);
        this.progressIndicator.setVisible(false);
    }

}
