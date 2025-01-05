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
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class Validator
{

    private static String _validatorMessage;
    private static String _validatorStyle;


    public static boolean isNotEmpty(String text)
    {
        boolean isValid = text != null && !text.trim().isEmpty();
        _validatorMessage = (!isValid) ? "Champ requis !" : "";
        _validatorStyle   = (!isValid) ? "validator-warning" : "validator-valid";

        return isValid;
    }

    /**
     * Nom du serveur (adresse IP ou nom de domaine valide)
     *
     * @param serverName
     * @return
     */
    public static boolean isValidServerName(String serverName)
    {
        String regex = "^(?:(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}|localhost|\\d{1,3}(?:\\.\\d{1,3}){3})$";
        boolean isValid = serverName != null && serverName.matches(regex);
        _validatorMessage = (!isValid) ? "Doit être une IP ou un nom d'hôte valide." : "";
        _validatorStyle   = (!isValid) ? "validator-error" : "validator-valid";
        return isValid;
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
        boolean isValid = dbName != null && dbName.matches(regex);
        _validatorMessage = (!isValid) ? "1 à 64 caractères ( _$ inclus)." : "";
        _validatorStyle   = (!isValid) ? "validator-error" : "validator-valid";
        return isValid;
    }


    /**
     * Port MySQL (entre 1 et 65535)
     *
     * @param port
     * @return
     */
    public static boolean isValidPort(int port)
    {
        boolean isValid = port > 0 && port <= 65535;
        _validatorMessage = (!isValid) ? "Le port doit être un nombre entre 0 et 65535." : "";
        _validatorStyle   = (!isValid) ? "validator-error" : "validator-valid";
        return isValid;
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
        boolean isValid = login != null && login.matches(regex);
        _validatorMessage = (!isValid) ? "1 à 32 caractères alphanumériques ( .-_ inclus)." : "";
        _validatorStyle   = (!isValid) ? "validator-error" : "validator-valid";
        return isValid;
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
        boolean isValid = password != null && password.length() > 0 && password.length() <= 41;
        _validatorMessage = (!isValid) ? "8 à 32 caractères. (alphanumériques et/ou caractères spéciaux)" : "";
        _validatorStyle   = (!isValid) ? "validator-error" : "validator-valid";
        return isValid;
    }

    public static boolean isNumeric(String str)
    {
        boolean isValid = str != null && !str.isEmpty() && str.matches("\\d+");
        _validatorMessage = (!isValid) ? "Doit être un nombre entier valide." : "";
        _validatorStyle   = (!isValid) ? "validator-error" : "validator-valid";
        return isValid;
    }

    public static boolean isMail(String mail)
    {
        String regex = "^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        boolean isValid = mail != null && mail.matches(regex);
        _validatorMessage = (!isValid) ? "Votre email ne respete pas la norme RFC2822 !" : "";
        _validatorStyle   = (!isValid) ? "validator-error" : "validator-valid";
        return isValid;
    }



    public static boolean isValidCityName(String value)
    {
        String regex = "^[A-Za-zÀ-ÿ'\\s-]+$";
        boolean isValid = value != null && value.matches(regex);
        _validatorMessage = (!isValid) ? "Doit être un nom de ville valide !" : "";
        _validatorStyle   = (!isValid) ? "validator-error" : "validator-valid";
        return isValid;
    }

    public static Label getHintLabel()
    {
        String msg = _validatorMessage != null ? _validatorMessage : "";

        Label hintErreur = new Label(msg);
        hintErreur.getStyleClass().add(getValidatorStyle());
        GridPane.setHalignment(hintErreur, HPos.RIGHT);

        return hintErreur;
    }

    public static String getValidatorStyle()
    {
        return _validatorStyle;
    }

    public static String getValidatorMessage()
    {
        return _validatorMessage;
    }

    public static void clearClass(Node node)
    {
        node.getStyleClass().removeAll("validator-warning", "validator-error", "validator-valid");
    }
}