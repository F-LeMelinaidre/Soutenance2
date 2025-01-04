package fr.cda.campingcar.dao.location;

import fr.cda.campingcar.model.Location;

import java.util.List;

/*
 * Soutenance Scraping
 * 2024/déc.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */
public interface LocationDAOInt
{
    boolean save(Location location);
    boolean saveAll(List<Location> locations);
    boolean update(Location location);
    Location findById(int id);
    List<Location> findAll();
    boolean delete(Location location);
}
