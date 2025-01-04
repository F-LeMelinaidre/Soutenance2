package fr.cda.campingcar.controller;

import fr.cda.campingcar.model.Location;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;


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
    private VBox detailPane;
    @FXML
    private WebView descriptionTextArea;

    @Override
    public void setData(Location annonce)
    {
        super.setData(annonce);
        this.setDescription(annonce.getDescription());
    }

    @Override
    protected void setImage(String path)
    {
        super.setImage(path);
        this.resizeImage(600, 300);
    }

    private void setDescription(String description)
    {
        WebEngine webEngine = this.descriptionTextArea.getEngine();
        webEngine.loadContent(description);
        webEngine.executeScript(
                "document.body.style.backgroundColor = 'transparent';" +
                "document.body.style.margin = '0';"
                               );
    }
}