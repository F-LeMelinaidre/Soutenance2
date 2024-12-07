package fr.cda.campingcar.dao;

/*
 * Soutenance Scraping
 * 2024/nov.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */


import fr.cda.campingcar.dao.site.SiteDAOImp;
import fr.cda.campingcar.dao.urlParam.URLParamDAOImp;
import fr.cda.campingcar.dao.vehicule.TypeVehiculeDAOImp;
import fr.cda.campingcar.dao.vehicule.VehiculeDAOImp;
import fr.cda.campingcar.dao.dom.DomDAOImp;
import fr.cda.campingcar.settings.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

// TODO LOG4J

public class DAOFactory {

    private static Connection _conn;

    public static DAOFactory getInstance() {
        try {
            Class.forName(Config.JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return new DAOFactory();
    }

    public Connection getConnection() throws SQLException {
        if (_conn == null || _conn.isClosed()) {
            String url = Config.SQL_SERVER + Config.SQL_DATA_BASE;
            _conn = DriverManager.getConnection(url, Config.SQL_USER, Config.SQL_PASS);
        }
        return _conn;
    }

    // TODO REGARDER LA FACTORY DE MEDIATHEQUE 3
    public void closeConnection() throws SQLException {
        if (_conn != null) {
            try {
                getConnection().close();
                _conn = null;
            } catch (SQLException e) {
                _conn = null;
                System.out.println(e.getSQLState());
            }

        }
    }

    // TODO REGARDER LA FACTORY DE MEDIATHEQUE 3
    public void closeResultSet(ResultSet rs)
    {
        if (rs != null) {
            try {
                rs.close();
                rs = null;
            } catch (SQLException e) {
                System.out.println(e.getSQLState());
            }
        }
    }

    public SiteDAOImp getSiteDAO() throws SQLException {
        return new SiteDAOImp(this);
    }

    public URLParamDAOImp getURLParamDAO() throws SQLException {
        return new URLParamDAOImp(this);
    }

    public TypeVehiculeDAOImp getTypeVehiculeDAO() throws SQLException {
        return new TypeVehiculeDAOImp(this);
    }

    public VehiculeDAOImp getVehiculeDAO() throws SQLException {
        return new VehiculeDAOImp();
    }

    public DomDAOImp getDomDAO() throws SQLException {
        return new DomDAOImp(this);
    }
}
