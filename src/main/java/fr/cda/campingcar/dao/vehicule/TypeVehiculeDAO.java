package fr.cda.campingcar.dao.vehicule;

/*
 * Soutenance Scraping
 * 2024/nov.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */


import fr.cda.campingcar.model.TypeVehicule;

import java.util.List;

public interface TypeVehiculeDAO {

    TypeVehicule find(int id);
    List<TypeVehicule> findAll();
    List<TypeVehicule> findBySite(int siteId);

}
