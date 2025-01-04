package fr.cda.campingcar.controller;

/*
 * Soutenance Scraping
 * 2024/déc.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */

import fr.cda.campingcar.model.Location;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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
public abstract class AnnonceController implements Initializable {

    @FXML
    protected ImageView imageView;
    @FXML
    protected Label titreLabelValue;
    @FXML
    protected Label villeLabelValue;
    @FXML
    protected Label tarifLabelValue;
    @FXML
    protected Label carburantLabelValue;
    @FXML
    protected Label transmissionLabelValue;
    @FXML
    protected Label placeLabelValue;
    @FXML
    protected Label couchageLabelValue;
    @FXML
    protected Label doucheLabelValue;
    @FXML
    protected Label wcLabelValue;

    protected Location annonce;
    protected Image imageOriginal;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        
    }

    public void setData(Location annonce) {
        this.annonce = annonce;
        this.setImage(annonce.getImage());
        this.setTitreLabelValue(annonce.getTitre());
        this.setVilleLabelValue(annonce.getVille());
        this.setTarifLabelValue(annonce.getTarif());
        this.setCarburantLabelValue(annonce.getCarburant());
        this.setTransmissionLabelValue(annonce.getTransmission());
        this.setPlaceLabelValue(annonce.getPlace());
        this.setCouchageLabelValue(annonce.getCouchage());
        this.setDoucheLabelValue(annonce.getDouche());
        this.setWcLabelValue(annonce.getWc());
    }

    protected void setImage(String path) {
        if (path != null) {
            this.imageOriginal = new Image(path);
            this.imageView.setImage(this.imageOriginal);
        }
    }


    /**
     * Methode pour afficher une image redimensionné et rogné à une dimension voulu.
     * <p>
     * Exemple pour afficher une image dans un ImageView de 350 par 150. <br>
     * Le dimensionnement de ImageView et de viewPort en proportion aux dimensions recherché, <br>
     * donnera un conteneur de 350 par 150, avec une image rogné est centré dans ca hauteur.
     * </p>
     * <p>
     * 1er Etape Set largeur et hauteur de ImageView:<br>
     * Set d'imageView porportionnel aux dimensions de l'image originale pour une largeur de 350.<br>
     * L'étape suivante sur le ViewPort fixera la hauteur à 150.
     * </p>
     * <p>
     * 2nd Etape dimensionnement du viewPort de l'ImageView (zone à afficher)<br>
     * Pour le viewPort on travaille sur les dimensions originale de l'image.<br>
     * On calcul se que represente une hauteur de 150 pour la largeur original.<br>
     * La position y = Hauteur Original - hauteur précédement calculé / 2
     * On applique les dimensions et positions a {@link Rectangle2D} puis setViewPort avec celui ci.
     * </p>
     */
    protected void resizeImage(int width, int height)
    {
        long  fitHeight = Math.round((width /this.imageOriginal.getWidth()) * this.imageOriginal.getHeight());
        this.imageView.setFitWidth(width);
        this.imageView.setFitHeight(fitHeight);

        double widthOrigin = this.imageOriginal.getWidth();
        double heightOrigin = this.imageOriginal.getHeight();

        long viewPortHeight = Math.round((widthOrigin /width) * height);
        long        posY        = Math.round((heightOrigin - viewPortHeight) / 2);
        Rectangle2D rectangle2D = new Rectangle2D(0, posY, widthOrigin, viewPortHeight);
        this.imageView.setViewport(rectangle2D);
    }

    protected void setTitreLabelValue(String titreLabelValue) {
        this.titreLabelValue.setText(titreLabelValue);
    }

    protected void setVilleLabelValue(String villeLabelValue) {
        this.villeLabelValue.setText(villeLabelValue);
    }

    protected void setTarifLabelValue(int tarifLabelValue) {
        String value = (tarifLabelValue == 0) ? "??" : String.valueOf(tarifLabelValue);
        this.tarifLabelValue.setText(value + "€");
        this.setStyle(this.tarifLabelValue, tarifLabelValue);
    }

    protected void setCarburantLabelValue(String carburantLabelValue) {
        String value = (carburantLabelValue == null) ? "??" : carburantLabelValue;
        this.carburantLabelValue.setText(value);
        this.setStyle(this.carburantLabelValue, carburantLabelValue);
    }

    protected void setTransmissionLabelValue(String transmissionLabelValue) {
        String value = (transmissionLabelValue == null || transmissionLabelValue.isEmpty()) ? "??" : transmissionLabelValue;
        this.transmissionLabelValue.setText(value);
        this.setStyle(this.transmissionLabelValue, transmissionLabelValue);
    }

    protected void setPlaceLabelValue(Integer placeLabelValue) {
        String value = (placeLabelValue == null || placeLabelValue == 0) ? "??" : String.valueOf(placeLabelValue);
        this.placeLabelValue.setText(value);
        this.setStyle(this.placeLabelValue, placeLabelValue);
    }

    protected void setCouchageLabelValue(Integer couchageLabelValue) {
        String value = (couchageLabelValue == null || couchageLabelValue == 0) ? "??" : String.valueOf(couchageLabelValue);
        this.couchageLabelValue.setText(value);
        this.setStyle(this.couchageLabelValue, couchageLabelValue);
    }

    protected void setDoucheLabelValue(Boolean doucheLabelValue) {
        String value = (doucheLabelValue == null) ? "??" : (doucheLabelValue ? "V" : "x");
        this.doucheLabelValue.setText(value);
        this.setStyle(this.doucheLabelValue, doucheLabelValue);
    }

    protected void setWcLabelValue(Boolean wcLabelValue) {
        String value = (wcLabelValue == null) ? "??" : (wcLabelValue ? "V" : "x");
        this.wcLabelValue.setText(value);
        this.setStyle(this.wcLabelValue, wcLabelValue);
    }

    private void setStyle(Label label, String value) {
        if (value == null) {
            label.getStyleClass().add("warning");
            label.applyCss();
            label.layout();
        }
    }

    private void setStyle(Label label, Integer value) {
        if (value == null || value == 0) {
            label.getStyleClass().add("warning");
            label.applyCss();
            label.layout();
        }
    }

    private void setStyle(Label label, Boolean value) {
        if (Boolean.TRUE.equals(value)) {
            label.getStyleClass().add("valid");
        } else if (Boolean.FALSE.equals(value)) {
            label.getStyleClass().add("invalid");
        } else if (value == null) {
            label.getStyleClass().add("warning");
        }

        if(value == null || Boolean.TRUE.equals(value) || Boolean.FALSE.equals(value)) {
            label.applyCss();
            label.layout();
        }
    }
}
