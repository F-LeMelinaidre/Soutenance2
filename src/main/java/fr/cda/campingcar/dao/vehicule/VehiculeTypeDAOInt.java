package fr.cda.campingcar.dao.vehicule;

/*
 * Soutenance Scraping
 * 2024/nov.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */


import fr.cda.campingcar.model.VehicleType;

import java.util.List;

public interface VehiculeTypeDAOInt
{

    VehicleType find(int id);
    List<VehicleType> findAll();
    List<VehicleType> findBySite(int siteId);

}
