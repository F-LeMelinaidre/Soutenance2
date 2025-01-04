package fr.cda.campingcar.util;

/*
 * Soutenance Scraping
 * 2024/déc.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */

import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class Validator
{

    private static String message;

    /**
     * Nom du serveur (adresse IP ou nom de domaine valide)
     *
     * @param serverName
     * @return
     */
    public static boolean isValidServerName(String serverName)
    {
        String regex = "^(?:(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}|localhost|\\d{1,3}(?:\\.\\d{1,3}){3})$";
        message = "Doit être une IP ou un nom d'hôte valide.";
        return serverName != null && serverName.matches(regex);
    }

    /**
     * Nom de la base de données (lettres, chiffres, _ $, max 64 caractères)
     *
     * @param dbName
     * @return
     */
    public static boolean isValidDatabaseName(String dbName)
    {
        String regex = "^[a-zA-Z0-9_$]{1,64}$";
        message = "1 à 64 caractères ( _$ inclus).";
        return dbName != null && dbName.matches(regex);
    }

    /**
     * Port MySQL (entre 1 et 65535)
     *
     * @param port
     * @return
     */
    public static boolean isValidPort(int port)
    {
        message = "Le port doit être un nombre entre 0 et 65535.";
        return port > 0 && port <= 65535;
    }

    /**
     * Login (lettres, chiffres, . _ -, max 32 caractères)
     * Format valide pour une DB Mysql
     *
     * @param login
     * @return
     */
    public static boolean isValidMySQLLogin(String login)
    {
        String regex = "^[a-zA-Z0-9._-]{1,32}$";
        message = "1 à 32 caractères alphanumériques ( .-_ inclus).";
        return login != null && login.matches(regex);
    }

    /**
     * Mot de passe (au moins 1 caractère, pas de restriction spécifique)
     * Format valide pour une DB Mysql
     *
     * @param password
     * @return
     */
    public static boolean isValidMySQLPassword(String password)
    {
        message = "8 à 32 caractères. (alphanumériques et/ou caractères spéciaux)";
        return password != null && password.length() > 0 && password.length() <= 41;
    }

    public static boolean isNumeric(String str)
    {
        message = "Le port doit être un nombre entier valide.";
        return str != null && !str.isEmpty() && str.matches("\\d+");
    }

    public static boolean isMail(String mail)
    {
        String regex = "^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        message = "Votre email ne respete pas la norme RFC2822 !";
        return mail != null && mail.matches(regex);
    }

    public static Label getHintLabel()
    {
        String msg = message != null ? message : "";

        Label hintErreur = new Label(msg);
        hintErreur.getStyleClass().add("hint-error");
        GridPane.setHalignment(hintErreur, HPos.RIGHT);

        return hintErreur;
    }
}