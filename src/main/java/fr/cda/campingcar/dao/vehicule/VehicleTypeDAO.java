package fr.cda.campingcar.dao.vehicule;

/*
 * Soutenance Scraping
 * 2024/nov.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */


import fr.cda.campingcar.dao.DAOFactory;
import fr.cda.campingcar.model.VehicleType;
import fr.cda.campingcar.util.DebugHelper;
import fr.cda.campingcar.util.LoggerConfig;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VehicleTypeDAO implements VehiculeTypeDAOInt
{

    protected Connection conn;
    private static final Logger LOGGER_DAO = LoggerConfig.getLoggerScraping();

    public VehicleTypeDAO(DAOFactory daoFactory) throws SQLException {
        try {
            this.conn = daoFactory.getConnection();
        } catch (SQLException e) {
            DebugHelper.debug("TypeVehiculeDAO", "Constructor", "ERROR", e.getSQLState(), false);
            throw new SQLException("Erreur VehiculeDAO - " + e.getMessage());
        }
    }


    @Override
    public VehicleType find(int id) {
        return null;
    }

    @Override
    public List<VehicleType> findAll() {

        return List.of();
    }

    @Override
    public List<VehicleType> findBySite(int siteId) {
        String sql = "SELECT * FROM vehicule_type AS vt " +
                     "INNER JOIN site_has_vehicule_type AS svt ON svt.vehicule_type_id = vt.id " +
                     "WHERE site_id = ?";

        List<VehicleType> list = new ArrayList<VehicleType>();

        try (Connection conn = this.conn;
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, siteId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String type = rs.getString("type");
                VehicleType typeVehicule = new VehicleType(id, type);

                list.add(typeVehicule);
            }

        } catch (SQLException e) {
            DebugHelper.debug("TypeVehiculeDAO", "findBySite", "ERROR", e.getSQLState(), false);
            LOGGER_DAO.error("findBySite ERROR - SQL State : {}, Message: {}",
                             e.getSQLState(), e.getMessage(), e);
            System.out.println(e.getMessage());
        }

        return list;
    }


}
