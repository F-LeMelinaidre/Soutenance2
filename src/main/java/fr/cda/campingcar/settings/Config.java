package fr.cda.campingcar.settings;

/*
 * Soutenance Scraping
 * 2024/nov.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */

import fr.cda.campingcar.model.City;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

public class Config
{
    public static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    public static final String SQL_SERVER = "localhost";
    public static final int SQL_PORT = 3306;
    public static final String SQL_DATA_BASE = "thousand_miles_desktop";
    public static final String SQL_USER = "admin";
    public static final String SQL_PASS = "admin";

    /**
     * Racine du dossier des fichiers FXML
     */
    public static final String FXML_ROOT_PATH = "/fr/cda/campingcar/";

    /**
     * Paramètre pour enregistrement de fichier
     */
    public static final String DEFAULT_XDOC_NAME = "rent.docx";
    public static final String DEFAULT_FILE_PATH = "C:\\Users\\"+System.getProperty("user.name")+"\\Documents";

    public static final String BINARIE_FOLDER_PATH = "binaries/";
    public static final String BINARIE_EXT = "bin";

    /**
     * Les urls des sites enregistré sont celle de recherche
     * Ce pattern permet de recuperer la racine protocole + nom de domaine,
     * pour les liens d'annonces sans protocole et nom de domaine
     */
    public static final Pattern HTTPS_WWW_PATTERN = Pattern.compile("(^https:\\/\\/www\\.[^\\/]+)");

    /**
     * CSS
     */
    public static final String VALID_CSS = "valid";
    public static final String INVALID_CSS = "invalid";
    public static final String WARNING_CSS = "warning";
    public static final String ERROR_CSS = "error";
    public static final String INPROGRESS_CSS = "in-progress";
    public static final String COMPLETED_CSS = "completed";
    public static final String STATE_CSS = "state";

    /**
     * Utilisé dans les methodes getFormated() de la class Ville pour retourner le nom de la ville en fonction d'un pattern
     * {ville} ( {numDep} ) retournera Vannes ( 56 )
     */
    public static final Map<String, Function<City, String>> VILLE_PATTERN = Map.of(
            "ville", City::getName,
            "numDep", ville -> String.valueOf(ville.getDepartement().getNumber()),
            "region", ville -> ville.getDepartement().getRegion()
                                                                                  );

    /**
     * Mise en couleur du texte console
     */
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

}
