package fr.cda.campingcar.dao.desktop;

/*
 * Soutenance Scraping
 * 2024/nov.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */


import fr.cda.campingcar.dao.DAOFactory;
import fr.cda.campingcar.dao.desktop.site.SiteDAO;
import fr.cda.campingcar.dao.desktop.vehicule.VehicleTypeDAO;
import fr.cda.campingcar.dao.desktop.dom.DomDAO;
import fr.cda.campingcar.model.DataBaseParameter;
import fr.cda.campingcar.settings.Config;
import java.sql.SQLException;


public class DeskTopDAOFactory extends DAOFactory
{

    private static DeskTopDAOFactory _instance;

    private DeskTopDAOFactory(DataBaseParameter dataBaseParameter)
    {
        super(dataBaseParameter);
    }

    public static DeskTopDAOFactory getInstance() {
        if (_instance == null) {
            loadJdbcDriver();
            _instance = new DeskTopDAOFactory(new DataBaseParameter(Config.SQL_SERVER, Config.SQL_DATA_BASE, Config.SQL_PORT, Config.SQL_USER, Config.SQL_PASS));
        }
        return _instance;
    }

    public SiteDAO getSiteDAO() throws SQLException {
        return new SiteDAO(this);
    }

    public VehicleTypeDAO getTypeVehiculeDAO() throws SQLException {
        return new VehicleTypeDAO(this);
    }

    public DomDAO getDomDAO() throws SQLException {
        return new DomDAO(this);
    }
}
