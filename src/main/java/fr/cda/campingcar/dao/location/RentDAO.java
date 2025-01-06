package fr.cda.campingcar.dao.location;

/*
 * Soutenance Scraping
 * 2024/déc.
 *
 * Le Frédéric Le Mélianidre
 * Formation CDA
 * Greta Vannes
 */

import fr.cda.campingcar.dao.DAOFactory;
import fr.cda.campingcar.model.Rent;
import fr.cda.campingcar.util.DebugHelper;
import fr.cda.campingcar.util.LoggerConfig;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class RentDAO implements RentDAOInt
{

    private static final Logger LOGGER_DAO = LoggerConfig.getLoggerScraping();
    protected Connection conn;

    public RentDAO(DAOFactory daoFactory) throws SQLException
    {
        try {
            this.conn = daoFactory.getConnection();
        } catch ( SQLException e ) {
            DebugHelper.debug("LocationDAO", "Constructor", "ERROR", e.getSQLState(), false);
            throw new SQLException("Erreur DomDAO - " + e.getMessage());
        }
    }

    @Override
    public boolean save(Rent location)
    {
        return false;
    }

    @Override
    public boolean saveAll(List<Rent> locations)
    {
        return false;
    }

    @Override
    public boolean update(Rent location)
    {
        return false;
    }

    @Override
    public Rent findById(int id)
    {
        return null;
    }

    @Override
    public List<Rent> findAll()
    {
        return List.of();
    }

    @Override
    public boolean delete(Rent location)
    {
        return false;
    }
}
