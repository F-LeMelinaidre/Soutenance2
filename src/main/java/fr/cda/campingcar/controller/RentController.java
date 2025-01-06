package fr.cda.campingcar.controller;

/*
 * Soutenance Scraping
 * 2024/déc.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */

import fr.cda.campingcar.model.Rent;
import fr.cda.campingcar.settings.Config;
import fr.cda.campingcar.util.render.FXMLWindow;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
public abstract class RentController extends FXMLWindow implements Initializable {

    @FXML
    protected Pane rentPane;
    @FXML
    protected ImageView imageView;
    @FXML
    protected Label titleLabel;
    @FXML
    protected Label cityLabel;
    @FXML
    protected Label priceLabel;
    @FXML
    protected Label fuelLabel;
    @FXML
    protected Label gearBoxLabel;
    @FXML
    protected Label seatLabel;
    @FXML
    protected Label bedLabel;
    @FXML
    protected Label showerLabel;
    @FXML
    protected Label wcLabel;

    protected Rent rent;
    protected Image originalImage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        super.initialize(url, resourceBundle);
    }

    public void setData(Rent rent) {
        this.rent = rent;
        this.setImage(rent.getImage());
        this.setTitleLabel(rent.getTitle());
        this.setCityLabel(rent.getCity());
        this.setPriceLabel(rent.getPrice());
        this.setFuelLabel(rent.getFuel());
        this.setGearBoxLabel(rent.getGearBox());
        this.setSeatLabel(rent.getSeat());
        this.setBedLabel(rent.getBed());
        this.setShowerLabel(rent.getShower());
        this.setWcLabel(rent.getWc());
    }

    protected void setImage(String path) {
        if (path != null) {
            this.originalImage = new Image(path);
            this.imageView.setImage(this.originalImage);
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
        long  fitHeight = Math.round((width /this.originalImage.getWidth()) * this.originalImage.getHeight());
        this.imageView.setFitWidth(width);
        this.imageView.setFitHeight(fitHeight);

        double widthOrigin = this.originalImage.getWidth();
        double heightOrigin = this.originalImage.getHeight();

        long viewPortHeight = Math.round((widthOrigin /width) * height);
        long        posY        = Math.round((heightOrigin - viewPortHeight) / 2);
        Rectangle2D rectangle2D = new Rectangle2D(0, posY, widthOrigin, viewPortHeight);
        this.imageView.setViewport(rectangle2D);
    }

    protected void setTitleLabel(String title) {
        this.titleLabel.setText(title);
    }

    protected void setCityLabel(String cityName) {
        this.cityLabel.setText(cityName);
    }

    protected void setPriceLabel(int price) {
        String value = (price == 0) ? "??" : String.valueOf(price);
        this.priceLabel.setText(value + "€");
        this.setStyle(this.priceLabel, price);
    }

    protected void setFuelLabel(String fuel) {
        String value = (fuel == null) ? "??" : fuel;
        this.fuelLabel.setText(value);
        this.setStyle(this.fuelLabel, fuel);
    }

    protected void setGearBoxLabel(String gearBox) {
        String value = (gearBox == null || gearBox.isEmpty()) ? "??" : gearBox;
        this.gearBoxLabel.setText(value);
        this.setStyle(this.gearBoxLabel, gearBox);
    }

    protected void setSeatLabel(Integer seat) {
        String value = (seat == null || seat == 0) ? "??" : String.valueOf(seat);
        this.seatLabel.setText(value);
        this.setStyle(this.seatLabel, seat);
    }

    protected void setBedLabel(Integer bed) {
        String value = (bed == null || bed == 0) ? "??" : String.valueOf(bed);
        this.bedLabel.setText(value);
        this.setStyle(this.bedLabel, bed);
    }

    protected void setShowerLabel(Boolean shower) {
        String value = (shower == null) ? "??" : (shower ? "V" : "x");
        this.showerLabel.setText(value);
        this.setStyle(this.showerLabel, shower);
    }

    protected void setWcLabel(Boolean wc) {
        String value = (wc == null) ? "??" : (wc ? "V" : "x");
        this.wcLabel.setText(value);
        this.setStyle(this.wcLabel, wc);
    }

    private void setStyle(Label label, String value) {
        if (value == null) {
            label.getStyleClass().add(Config.WARNING_CSS);
            label.applyCss();
            label.layout();
        }
    }

    private void setStyle(Label label, Integer value) {
        if (value == null || value == 0) {
            label.getStyleClass().add(Config.WARNING_CSS);
            label.applyCss();
            label.layout();
        }
    }

    private void setStyle(Label label, Boolean value) {
        if (Boolean.TRUE.equals(value)) {
            label.getStyleClass().add(Config.VALID_CSS);
        } else if (Boolean.FALSE.equals(value)) {
            label.getStyleClass().add(Config.INVALID_CSS);
        } else if (value == null) {
            label.getStyleClass().add(Config.WARNING_CSS);
        }

        if(value == null || Boolean.TRUE.equals(value) || Boolean.FALSE.equals(value)) {
            label.applyCss();
            label.layout();
        }
    }
}
