/*
 * Soutenance Scraping
 * 11/2024
 *
 * Le Mélinaidre Frédéric
 * Formation CDA
 * Greta Vannes
 */

package fr.cda.campingcar;

import fr.cda.campingcar.dao.DAOFactory;
import fr.cda.campingcar.dao.dom.DomDAOImp;
import fr.cda.campingcar.model.Dom;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class Main extends Application
{

    // TODO EXCEPTION LOG4J
    //  VALIDATION DE LA SAISIE DU MONTANT JOURNALIER
    //  DAO UTILISER LES METHODES CLOSE, VOIR TP-MEDIATHEQUE
    //  ENREGISTREMENT DES DONNEES DE BD
    //  ENVOI MAIL
    //  CREATION FICHIER, ENREGISTREMENT DU FICHIER
    //  AFFICHER LA RECHERCHE
    //  ENREGISTREMENT DE LA RECHERCHE EN BD
    //  TEST UNITAIRE
    //  THREAD


    @Override
    public void start(Stage stage) throws IOException, SQLException
    {
        /*DAOFactory daoFactory = DAOFactory.getInstance();

        Map<String, Dom> domList = daoFactory.getDomDAO().findBySite(3);
        System.out.println(domList.toString());*/

        Font.loadFont(Main.class.getResourceAsStream("/fonts/poppins-regular.ttf"), 14);
        Font.loadFont(Main.class.getResourceAsStream("/fonts/poppins-bold.ttf"), 14);
        Font.loadFont(Main.class.getResourceAsStream("/fonts/poppins-italic.ttf"), 14);
        Font.loadFont(Main.class.getResourceAsStream("/fonts/poppins-medium.ttf"), 14);

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("home.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 750);
        stage.setTitle("Scraping");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws SQLException
    {

        launch();
    }
}