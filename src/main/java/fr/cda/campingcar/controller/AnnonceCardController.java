package fr.cda.campingcar.controller;

import fr.cda.campingcar.settings.Config;
import fr.cda.campingcar.util.render.FXMLRender;
import javafx.event.Event;
import javafx.fxml.FXML;
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
public class AnnonceCardController extends AnnonceController
{

    @FXML
    public void showDetail()
    {
        AnnonceDetailController annonceDetailController = FXMLRender.openNewWindow("window/annonceDetail.fxml", "Detail");
        annonceDetailController.setData(this.annonce);
    }

    @Override
    protected void setImage(String path)
    {
        super.setImage(path);
        if ( path != null ) {
            this.resizeImage(350, 150);
        }

    }

}
