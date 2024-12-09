package fr.cda.campingcar.dao.dom;

/*
 * Soutenance Scraping
 * 2024/nov.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */

import fr.cda.campingcar.dao.DAOFactory;
import fr.cda.campingcar.model.Dom;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DomDAOImp implements DomDAO
{

    protected Connection conn;

    public DomDAOImp(DAOFactory daoFactory) throws SQLException
    {
        try {
            this.conn = daoFactory.getConnection();
        } catch ( SQLException e ) {
            throw new SQLException("Erreur getConnect() DomMappingDAOImp() - " + e.getMessage());
        }
    }

    @Override
    public Map<String, Dom> findBySite(int siteId)
    {
        String sql = "SELECT dp.id, dp.site_id, dp.dom_element_id, dp.path, dp.dom_path_parent_id, " +
                     "de.nom " +
                     "FROM dom_path AS dp " +
                     "LEFT JOIN dom_path AS dp2 ON dp2.dom_element_id = dp.dom_path_parent_id " +
                     "LEFT JOIN dom_element AS de ON de.id = dp.dom_element_id " +
                     "WHERE dp.site_id = ? ";

        Map<Integer, Dom> domMap = new HashMap<>();

        try (Connection conn = this.conn;
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, siteId);
            ResultSet rs = ps.executeQuery();

            while ( rs.next() ) {

                int id = rs.getInt("id");
                siteId = rs.getInt("site_id");
                int    domElementId    = rs.getInt("dom_element_id");
                String path      = rs.getString("path");
                int    domParentId = rs.getInt("dom_path_parent_id");
                String nom       = rs.getString("nom");

                Dom dom = domMap.get(id);
                if ( dom == null ) {
                    dom = new Dom(id, siteId, domElementId, nom, path, domParentId);
                    domMap.put(id, dom);
                }

            }

        } catch ( SQLException e ) {
            System.out.println(e.getMessage());
        }
        List<Dom> domList = new ArrayList<>(domMap.values());
        Map<String, Dom> results = this.buildHierarchy(domList, 0);
        return results;
    }

    private Map<String, Dom> buildHierarchy(List<Dom> domList, Integer parentId) {
        Map<String, Dom> hierarchy = new HashMap<>();

        for (Dom dom : domList) {
            if ( (parentId == null && dom.getDomParentId() == 0) ||
                 (parentId != null && dom.getDomParentId().equals(parentId))) {

                // Ajouter l'élément au niveau actuel
                hierarchy.put(dom.getNom(), dom);

                // Appel récursif pour construire les enfants
                dom.setEnfants(new ArrayList<>(buildHierarchy(domList, dom.getDomElementId()).values()));
            }
        }

        return hierarchy;
    }

}
