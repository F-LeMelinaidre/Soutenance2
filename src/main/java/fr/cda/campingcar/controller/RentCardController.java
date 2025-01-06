package fr.cda.campingcar.controller;

import fr.cda.campingcar.util.render.FXMLRender;
import javafx.fxml.FXML;

/*
 * Soutenance Scraping
 * 2024/déc.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */
public class RentCardController extends RentController
{

    @FXML
    public void showDetail()
    {
        RentDetailController annonceDetailController = FXMLRender.openNewWindow("window/annonceDetail.fxml", "Detail");
        annonceDetailController.setData(this.rent);
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
