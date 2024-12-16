package fr.cda.campingcar.settings;

/*
 * Soutenance Scraping
 * 2024/nov.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */

import fr.cda.campingcar.model.Ville;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

public class Config
{
    public static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    public static final String SQL_SERVER = "jdbc:mysql://localhost:3306/";
    public static final String SQL_DATA_BASE = "thousand_miles_desktop";
    public static final String SQL_USER = "admin";
    public static final String SQL_PASS = "admin";

    /**
     * Racine du dossier des fichiers FXML
     */
    public static final String FXML_ROOT_PATH = "/fr/cda/campingcar/";
    public static final String BINARIE_FOLDER_PATH = "binaries/";
    public static final String BINARIE_EXT = "bin";

    public static final int THREAD_POOL_SIZE = 50;

    /**
     * non utilisé pour le moment
     */
    public static final Map<Integer, String> STEP_MAP;
    static {
        Map<Integer, String> map = new HashMap<>();
        map.put(1, "card");
        map.put(2, "page");
        STEP_MAP = Collections.unmodifiableMap(map);
    }

    /**
     * Les urls des sites enregistré sont celle de recherche
     * Ce pattern permet de recuperer la racine protocole + nom de domaine,
     * pour les liens d'annonces sans protocole et nom de domaine
     */
    public static final Pattern HTTPS_WWW_PATTERN = Pattern.compile("(^https:\\/\\/www\\.[^\\/]+)");

    /**
     * Utilisé dans les methodes getFormated() de la class Ville pour retourner le nom de la ville en fonction d'un pattern
     * {ville} ( {numDep} ) retournera Vannes ( 56 )
     */
    public static final Map<String, Function<Ville, String>> VILLE_PATTERN = Map.of(
            "ville", Ville::getNom,
            "numDep", ville -> String.valueOf(ville.getDepartement().getNumero()),
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
