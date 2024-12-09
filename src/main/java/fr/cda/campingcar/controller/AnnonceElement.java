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
    private Label titre;
    @FXML
    private Label ville;
    @FXML
    private Label tarif;
    @FXML
    private Label place;
    @FXML
    private Label couchage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {

    }

    public void setImage (String path) {
        if (path != null) {
            Image image = new Image(path);
            this.imageView.setImage(image);

            // Rogne l image dans sa hauteur
            Rectangle clip = new Rectangle(350, 150);
            imageView.setClip(clip);
        }

    }

    public void setTitre (String titre) {
        this.titre.setText(titre);
    }

    public void setVille(String ville)
    {
        this.ville.setText(ville);
    }

    public void setTarif(int tarif)
    {
        this.tarif.setText(String.valueOf(tarif) + "€");
    }

    public void setPlace(int place)
    {
        this.place.setText(String.valueOf(place));
    }

    public void setCouchage(int couchage)
    {
        this.couchage.setText(String.valueOf(couchage));
    }
}
