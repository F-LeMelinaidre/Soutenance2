package fr.cda.campingcar.dao.dom;

/*
 * Soutenance Scraping
 * 2024/nov.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */

import fr.cda.campingcar.model.Dom;

import java.util.Map;

public interface DomDAOInt
{
    Map<String, Dom> findBySite(int id);
}
