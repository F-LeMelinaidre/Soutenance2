package fr.cda.campingcar.model;

/*
 * Soutenance Scraping
 * 2025/janv.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */

/**
 * Interface représentant un critère de recherche.
 * Cette interface définit une méthode pour obtenir la valeur d'un critère.
 */
public interface SearchCriteria
{
    /**
     * Retourne la valeur associée à ce critère.
     *
     * @return la valeur de l'objet associé à ce critère sous forme de chaîne.
     */
    String valeur();
}
