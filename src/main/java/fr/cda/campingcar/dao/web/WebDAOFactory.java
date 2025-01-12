package fr.cda.campingcar.dao.web;

/*
 * Soutenance Scraping
 * 2024/nov.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */


import fr.cda.campingcar.dao.DAOFactory;
import fr.cda.campingcar.dao.web.rent.RentDAO;
import fr.cda.campingcar.model.DataBaseParameter;

import java.sql.SQLException;


public class WebDAOFactory extends DAOFactory
{

    private static WebDAOFactory _instance;

    private WebDAOFactory(DataBaseParameter dataBaseParameter)
    {
        super(dataBaseParameter);
    }

    public static WebDAOFactory getInstance(DataBaseParameter dataBaseParameter) {
        if (_instance == null) {
            loadJdbcDriver();
            _instance = new WebDAOFactory(dataBaseParameter);
        }
        return _instance;
    }


    public RentDAO getRentDAO() throws SQLException {
        return new RentDAO(this);
    }
}
