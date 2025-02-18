package fr.cda.campingcar.dao.desktop.site;

/*
 * Soutenance Scraping
 * 2024/nov.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */

import fr.cda.campingcar.model.Site;

import java.util.List;

public interface SiteDAOInt
{

    Site find(int id);
    List<Site> findAll();
    List<Site> findAllSitesWithVehiclesParamsAndXPaths();

}
