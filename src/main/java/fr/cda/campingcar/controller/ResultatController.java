package fr.cda.campingcar.controller;

import fr.cda.campingcar.model.Annonce;
import fr.cda.campingcar.util.FXMLRender;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.FlowPane;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/*
 * Soutenance Scraping
 * 2024/déc.
 *
 * Le Frédéric Le Mélianidre
 * Formation CDA
 * Greta Vannes
 */
public class ResultatController implements Initializable, ControllerRegistry
{
    @FXML
    private FlowPane resultatFlowPane;

    private FXMLRender fxmlRender;
    private HomeController homeController = null;

    @Override
    public void setMainController(Object parentController)
    {
        this.homeController = (HomeController) homeController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        this.fxmlRender = new FXMLRender(resultatFlowPane, this);
    }

    public void setAnnonce(List<Annonce> resultats) {
        System.out.println(resultats.toString());

        for (Annonce annonce : resultats) {
            AnnonceElement annonceElement = this.fxmlRender.loadFXML("annonceElement.fxml", null);
            annonceElement.setImage(annonce.getImage());
            annonceElement.setTitreLabelValue(annonce.getTitre());
            annonceElement.setVilleLabelValue(annonce.getVille());
            annonceElement.setTarifLabelValue(annonce.getTarif());
            annonceElement.setCarburantLabelValue(annonce.getCarburant());
            annonceElement.setTransmissionLabelValue(annonce.getTransmission());
            annonceElement.setPlaceLabelValue(annonce.getPlace());
            annonceElement.setCouchageLabelValue(annonce.getCouchage());
            annonceElement.setDoucheLabelValue(annonce.getDouche());
            annonceElement.setWcLabelValue(annonce.getWc());
        }
    }
}
