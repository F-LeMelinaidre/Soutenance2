package fr.cda.campingcar.controller;

import fr.cda.campingcar.model.Location;
import fr.cda.campingcar.settings.Config;
import fr.cda.campingcar.util.FXMLRender;
import javafx.fxml.FXML;
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
    private VBox cardPane;

    @Override
    protected void setImage(String path)
    {
        super.setImage(path);
        if(path != null) {
            this.resizeImage(350, 150);
        }

    }

    @Override
    protected void setTitreLabelValue(String titre)
    {
        super.setTitreLabelValue(titre);
        this.titreLabelValue.setOnMouseClicked(event -> {
            System.out.println(Config.CYAN + "setTitre MouseClique: " + titre + Config.RESET + "\n" + this.annonce.toString());
            AnnonceDetailController annonceDetailController = FXMLRender.openNewWindow("window/annonceDetail.fxml", "Detail");
            if (annonceDetailController != null) {

                annonceDetailController.setData(this.annonce);
            }
        });
    }

}
