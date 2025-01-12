package fr.cda.campingcar.dao;

import fr.cda.campingcar.model.DataBaseParameter;
import fr.cda.campingcar.settings.Config;
import fr.cda.campingcar.util.DebugHelper;
import fr.cda.campingcar.util.LoggerConfig;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * Soutenance Scraping
 * 2025/janv.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */
public abstract class DAOFactory
{
    protected static final Logger LOGGER_DAO = LoggerConfig.getLoggerScraping();


    private static Connection _conn;
    private DataBaseParameter dataBaseParameter;

    protected DAOFactory(DataBaseParameter dataBaseParameter) {
        this.dataBaseParameter = dataBaseParameter;
    }

    protected static void loadJdbcDriver() {
        try {
            Class.forName(Config.JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            DebugHelper.debug("DAOFACTORY", "getInstance", "ERROR", e.getMessage(), false);
            LOGGER_DAO.error("Get Instance ERROR - Message: {}",
                             e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() throws SQLException
    {
        if (_conn == null || _conn.isClosed()) {

            _conn = DriverManager.getConnection(this.dataBaseParameter.getJdbcUrlServer(), this.dataBaseParameter.getLogin(), this.dataBaseParameter.getPassword());
        }
        return _conn;
    }

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
}
