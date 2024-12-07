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
import fr.cda.campingcar.model.TypeVehicule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// TODO LOG4J
public class TypeVehiculeDAOImp implements TypeVehiculeDAO {

    protected Connection conn;

    public TypeVehiculeDAOImp(DAOFactory daoFactory) throws SQLException {
        try {
            this.conn = daoFactory.getConnection();
        } catch (SQLException e) {
            throw new SQLException("Erreur getConnect() TypeVehiculeDAOImp() - " + e.getMessage());
        }
    }


    @Override
    public TypeVehicule find(int id) {
        return null;
    }

    @Override
    public List<TypeVehicule> findAll() {

        return List.of();
    }

    @Override
    public List<TypeVehicule> findBySite(int siteId) {
        String sql = "SELECT * FROM vehicule_type AS vt " +
                     "INNER JOIN site_has_vehicule_type AS svt ON svt.vehicule_type_id = vt.id " +
                     "WHERE site_id = ?";

        List<TypeVehicule> list = new ArrayList<TypeVehicule>();

        try (Connection conn = this.conn;
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, siteId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String type = rs.getString("type");
                TypeVehicule typeVehicule = new TypeVehicule(id, type);

                list.add(typeVehicule);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return list;
    }


}
