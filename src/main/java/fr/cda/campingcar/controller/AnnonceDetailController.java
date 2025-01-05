package fr.cda.campingcar.controller;

import fr.cda.campingcar.model.Location;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

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

public class AnnonceDetailController extends AnnonceController
{
    @FXML
    private WebView descriptionTextArea;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        this.mainPane = this.annoncePane;
        super.initialize(url, resourceBundle);
    }

    @Override
    public void setData(Location annonce)
    {
        super.setData(annonce);
        this.setDescription(annonce.getDescription());
    }

    @Override
    protected void setImage(String path)
    {
        Platform.runLater(() -> {

            super.setImage(path);
            this.resizeImage(600, 300);


        });
    }

    private void setDescription(String description)
    {
        Platform.runLater(() -> {

            WebEngine webEngine = this.descriptionTextArea.getEngine();
            webEngine.loadContent(description);
            webEngine.executeScript(
                    "document.body.style.backgroundColor = 'transparent';" +
                    "document.body.style.margin = '0';"
                                   );


        });
        this.refreshWindow();
    }
}