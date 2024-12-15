package fr.cda.campingcar.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

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
public class AnnonceElement extends VBox implements Initializable
{
    @FXML
    private VBox annonceVBox;
    @FXML
    private ImageView imageView;
    @FXML
    private Label titreLabel;
    @FXML
    private Label titreLabelValue;
    @FXML
    private Label villeLabel;
    @FXML
    private Label villeLabelValue;
    @FXML
    private Label tarifLabel;
    @FXML
    private Label tarifLabelValue;
    @FXML
    private Label placeLabel;
    @FXML
    private Label carburantLabel;
    @FXML
    private Label carburantLabelValue;
    @FXML
    private Label transmissionLabel;
    @FXML
    private Label transmissionLabelValue;
    @FXML
    private Label placeLabelValue;
    @FXML
    private Label couchageLabel;
    @FXML
    private Label couchageLabelValue;
    @FXML
    private Label doucheLabel;
    @FXML
    private Label doucheLabelValue;
    @FXML
    private Label wcLabel;
    @FXML
    private Label wcLabelValue;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {

    }

    public void setImage(String path)
    {
        if ( path != null ) {
            Image image = new Image(path);
            this.imageView.setImage(image);

            // Rogne l image dans sa hauteur
            Rectangle clip = new Rectangle(350, 150);
            imageView.setClip(clip);
        }

    }

    public void setTitreLabelValue(String titreLabelValue)
    {
        this.titreLabelValue.setText(titreLabelValue);
    }

    public void setVilleLabelValue(String villeLabelValue)
    {
        this.villeLabelValue.setText(villeLabelValue);
    }

    public void setTarifLabelValue(int tarifLabelValue)
    {
        String value = (tarifLabelValue == 0) ? "??" : String.valueOf(tarifLabelValue);
        this.tarifLabelValue.setText(value + "€");
        this.setStyle(this.tarifLabelValue, tarifLabelValue);
    }

    public void setCarburantLabelValue(String carburantLabelValue)
    {
        String value = (carburantLabelValue == null) ? "??" : carburantLabelValue;
        this.carburantLabelValue.setText(value);
        this.setStyle(this.carburantLabelValue, carburantLabelValue);
    }

    public void setTransmissionLabelValue(String transmissionLabelValue)
    {
        String value = (transmissionLabelValue == null || transmissionLabelValue.isEmpty()) ? "??" : transmissionLabelValue;
        this.transmissionLabelValue.setText(value);
        this.setStyle(this.transmissionLabelValue, transmissionLabelValue);
    }

    public void setPlaceLabelValue(Integer placeLabelValue)
    {
        String value = (placeLabelValue == null || placeLabelValue == 0) ? "??" : String.valueOf(placeLabelValue);
        this.placeLabelValue.setText(value);
        this.setStyle(this.placeLabelValue, placeLabelValue);
    }

    public void setCouchageLabelValue(Integer couchageLabelValue)
    {
        String value = (couchageLabelValue == null || couchageLabelValue == 0) ? "??" : String.valueOf(couchageLabelValue);
        this.couchageLabelValue.setText(value);
        this.setStyle(this.couchageLabelValue, couchageLabelValue);
    }

    public void setDoucheLabelValue(Boolean doucheLabelValue)
    {
        String value = (doucheLabelValue == null) ? "??" : (doucheLabelValue ? "V" : "x");
        this.doucheLabelValue.setText(value);
        this.setStyle(this.doucheLabelValue, doucheLabelValue);
    }

    public void setWcLabelValue(Boolean wcLabelValue)
    {
        String value = (wcLabelValue == null) ? "??" : (wcLabelValue ? "V" : "x");
        this.wcLabelValue.setText(value);
        this.setStyle(this.wcLabelValue, wcLabelValue);
    }

    private void setStyle(Label label, String value)
    {
        label.getStyleClass().clear();
        label.getStyleClass().add("value-label");
        if ( value == null ) {
            label.getStyleClass().add("warning");
        }
        label.applyCss();
        label.layout();
    }

    private void setStyle(Label label, Integer value)
    {
        label.getStyleClass().clear();
        label.getStyleClass().add("value-label");
        if ( value == null || value == 0) {
            label.getStyleClass().add("warning");
        }
        label.applyCss();
        label.layout();
    }

    private void setStyle(Label label, Boolean value)
    {
        label.getStyleClass().clear();
        label.getStyleClass().add("value-label");
        if ( Boolean.TRUE.equals(value) ) {
            label.getStyleClass().add("valid");
        } else if ( Boolean.FALSE.equals(value) ) {
            label.getStyleClass().add("invalid");
        } else if (value == null) {
            label.getStyleClass().add("warning");
        }
        label.applyCss();
        label.layout();
    }
}
