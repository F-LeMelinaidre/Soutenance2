package fr.cda.campingcar.dao.web.rent;

/*
 * Soutenance Scraping
 * 2024/déc.
 *
 * Le Frédéric Le Mélianidre
 * Formation CDA
 * Greta Vannes
 */

import fr.cda.campingcar.dao.web.WebDAOFactory;
import fr.cda.campingcar.model.City;
import fr.cda.campingcar.model.Rent;
import fr.cda.campingcar.util.DebugHelper;
import fr.cda.campingcar.util.LoggerConfig;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
// TODO FAIRE UNE TABLE CITY
public class RentDAO implements RentDAOInt
{

    private static final Logger LOGGER_DAO = LoggerConfig.getLoggerScraping();
    protected Connection conn;
    private WebDAOFactory factory;

    public RentDAO(WebDAOFactory daoFactory) throws SQLException
    {
        try {
            this.factory = daoFactory;
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
    public boolean saveAll(List<Rent> rents)
    {
        String sql = "INSERT INTO rent (titre, fuel, gearbox, seat, bed, shower, wc, description, price, link, picture, city) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        boolean result = false;
        PreparedStatement pst = null;
        try {
            this.conn.setAutoCommit(false);
            pst= this.conn.prepareStatement(sql);

            for (Rent rent : rents) {
                pst.setObject(1, rent.getTitle());
                pst.setObject(2, rent.getFuel());
                pst.setObject(3, rent.getGearBox());
                pst.setObject(4, rent.getSeat());
                pst.setObject(5, rent.getBed());
                pst.setObject(6, rent.getShower());
                pst.setObject(7, rent.getWc());
                pst.setObject(8, rent.getDescription());
                pst.setObject(9, rent.getPrice());
                pst.setObject(10, rent.getUrl());
                pst.setObject(11, rent.getImage());
                pst.setObject(12, rent.getCity());
                pst.addBatch();
            }

            pst.executeBatch();
            this.conn.commit();

            result = true;
        } catch ( SQLException e ) {
            DebugHelper.debug("RentDAO", "saveAll", "ERROR", e.getSQLState(), false);
            LOGGER_DAO.error("saveAll ERROR - SQL State : {}, Message: {}",
                             e.getSQLState(), e.getMessage(), e);
        } finally {
            try {
                this.factory.closeConnection();
            } catch ( SQLException e ) {
                e.printStackTrace();
            }

        }

        return result;
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
