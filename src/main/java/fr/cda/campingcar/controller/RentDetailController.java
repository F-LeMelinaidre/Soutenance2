package fr.cda.campingcar.controller;

import fr.cda.campingcar.model.Rent;
import javafx.application.Platform;
import javafx.fxml.FXML;
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

public class RentDetailController extends RentController
{
    @FXML
    private WebView descriptionTextArea;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        this.mainPane = this.rentPane;
        super.initialize(url, resourceBundle);
    }

    @Override
    public void setData(Rent rent)
    {
        super.setData(rent);
        this.setDescription(rent.getDescription());
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