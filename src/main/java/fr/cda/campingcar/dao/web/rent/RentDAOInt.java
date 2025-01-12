package fr.cda.campingcar.dao.web.rent;

import fr.cda.campingcar.model.Rent;

import java.util.List;

/*
 * Soutenance Scraping
 * 2024/déc.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */
public interface RentDAOInt
{
    boolean save(Rent location);
    boolean saveAll(List<Rent> locations);
    boolean update(Rent location);
    Rent findById(int id);
    List<Rent> findAll();
    boolean delete(Rent location);
}
