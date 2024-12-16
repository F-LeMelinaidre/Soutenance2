package fr.cda.campingcar.dao.urlParam;

/*
 * Soutenance Scraping
 * 2024/nov.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */

import fr.cda.campingcar.model.UrlParam;

import java.util.List;

public interface URLParamDAOInt
{
    List<UrlParam> findBySite(int site_id);
}
