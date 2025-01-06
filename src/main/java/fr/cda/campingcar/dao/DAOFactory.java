package fr.cda.campingcar.dao;

/*
 * Soutenance Scraping
 * 2024/nov.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */


import fr.cda.campingcar.dao.site.SiteDAO;
import fr.cda.campingcar.dao.vehicule.VehicleTypeDAO;
import fr.cda.campingcar.dao.dom.DomDAO;
import fr.cda.campingcar.settings.Config;
import fr.cda.campingcar.util.DebugHelper;
import fr.cda.campingcar.util.LoggerConfig;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;


public class DAOFactory {

    private static Connection _conn;
    private final String sqlSever = Config.SQL_SERVER;
    private final String sqlDataBase = Config.SQL_DATA_BASE;
    private final String sqlUser = Config.SQL_USER;
    private final String sqlPass = Config.SQL_PASS;

    private static final Logger LOGGER_DAO = LoggerConfig.getLoggerScraping();

    public static DAOFactory getInstance() {
        try {
            Class.forName(Config.JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            DebugHelper.debug("DAOFACTORY", "getInstance", "ERROR", e.getMessage(), false);
            LOGGER_DAO.error("Get Instance ERROR - Message: {}",
                             e.getMessage(), e);
            throw new RuntimeException(e);
        }

        return new DAOFactory();
    }

    public Connection getConnection() throws SQLException {
        if (_conn == null || _conn.isClosed()) {
            String url = this.sqlSever + this.sqlDataBase;
            _conn = DriverManager.getConnection(url, this.sqlUser, this.sqlPass);
        }
        return _conn;
    }

    // TODO REGARDER LA METHODE DANS LES DAO
    public void closeConnection() throws SQLException {
        if (_conn != null) {
            try {
                getConnection().close();
                _conn = null;
            } catch (SQLException e) {
                _conn = null;
                DebugHelper.debug("DAOFACTORY", "Connection", "ERROR", e.getSQLState(), false);
                LOGGER_DAO.error("Close Connection ERROR - SQL State : {}, Message: {}",
                                 e.getSQLState(), e.getMessage(), e);
            }

        }
    }

    // TODO UTILISER LA METHODE DANS LES DAO
    public void closeResultSet(ResultSet rs)
    {
        if (rs != null) {
            try {
                rs.close();
                rs = null;
            } catch (SQLException e) {
                DebugHelper.debug("DAOFACTORY", "ResultSet", "ERROR", e.getSQLState(), false);
                LOGGER_DAO.error("Close ResultSet ERROR - SQL State : {}, Message: {}",
                                 e.getSQLState(), e.getMessage(), e);
            }
        }
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
