/*
 * Soutenance Scraping
 * 11/2024
 *
 * Le Mélinaidre Frédéric
 * Formation CDA
 * Greta Vannes
 */

package fr.cda.campingcar;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class Main extends Application
{

    // TODO EXCEPTION LOG4J
    //  DAO UTILISER LES METHODES CLOSE, VOIR TP-MEDIATHEQUE
    //  ENREGISTREMENT DES DONNEES DE BD
    //  ENVOI MAIL
    //  CREATION FICHIER, ENREGISTREMENT DU FICHIER
    //  ENREGISTREMENT DE LA RECHERCHE EN BD
    //  TEST UNITAIRE


    @Override
    public void start(Stage stage) throws IOException, SQLException
    {
        Font.loadFont(Main.class.getResourceAsStream("/fonts/poppins-regular.ttf"), 14);
        Font.loadFont(Main.class.getResourceAsStream("/fonts/poppins-bold.ttf"), 14);
        Font.loadFont(Main.class.getResourceAsStream("/fonts/poppins-italic.ttf"), 14);
        Font.loadFont(Main.class.getResourceAsStream("/fonts/poppins-medium.ttf"), 14);


        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("home.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 750);
        stage.setTitle("Scraping");
        stage.setScene(scene);

        //Ferme proprement l application et les fenêtres secondaires potentiellement ouvertes
        stage.setOnCloseRequest(event -> {
            Platform.exit();
        });

        stage.show();
    }

    public static void main(String[] args) throws SQLException
    {
        launch();
    }
    
}