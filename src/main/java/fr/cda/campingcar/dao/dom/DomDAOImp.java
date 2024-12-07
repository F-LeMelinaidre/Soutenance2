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
        String sql = "SELECT dp.site_id, dp.dom_element_id, dp.path, dp.dom_path_parent_id, " +
                     "de.nom, mc.mot " +
                     "FROM dom_path AS dp " +
                     "LEFT JOIN dom_path AS dp2 ON dp2.site_id = dp.site_id AND dp2.dom_element_id = dp.dom_path_parent_id " +
                     "LEFT JOIN dom_element AS de ON de.id = dp.dom_element_id " +
                     "LEFT JOIN mot_cle AS mc ON dp.site_id = mc.site_id AND dp.dom_element_id = mc.dom_element_id " +
                     "WHERE dp.site_id = ? ";

        Map<Integer, Dom> domMap = new HashMap<>();

        try (Connection conn = this.conn;
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, siteId);
            ResultSet rs = ps.executeQuery();

            while ( rs.next() ) {

                siteId = rs.getInt("site_id");
                int    domKey    = rs.getInt("dom_element_id");
                String path      = rs.getString("path");
                int    domParent = rs.getInt("dom_path_parent_id");
                String nom       = rs.getString("nom");
                String motCle    = rs.getString("mot");

                Dom dom = domMap.get(domKey);
                if ( dom == null ) {
                    dom = new Dom(siteId, domKey, nom, path, domParent);
                    domMap.put(domKey, dom);
                }

                if ( motCle != null ) {
                    dom.addMotCle(motCle);
                }

            }

        } catch ( SQLException e ) {
            System.out.println(e.getMessage());
        }
        List<Dom> domList = new ArrayList<>(domMap.values());
        return this.buildHierarchy(domList, 0);
    }


    private Map<String, Dom> buildHierarchy(List<Dom> domList, Integer parentId)
    {
        Map<String, Dom> hierarchyMap = new HashMap<>();

        for ( Dom dom : domList ) {
            // Vérifiication si dom est un enfant du parentId donné
            if ( (parentId == null && dom.getDomParent() == null) ||
                 (parentId != null && parentId.equals(dom.getDomParent())) ) {
                // Ajout dom à la hiérarchie
                hierarchyMap.put(dom.getNom(), dom);

                // Appel récursif pour trouver les enfants
                Map<String, Dom> enfants = this.buildHierarchy(domList, dom.getDomElementId());

                // Ajout des enfants à dom
                dom.setEnfants(new ArrayList<>(enfants.values()));
            }
        }

        return hierarchyMap;
    }

}
