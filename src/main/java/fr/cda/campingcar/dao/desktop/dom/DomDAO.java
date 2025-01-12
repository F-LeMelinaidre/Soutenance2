package fr.cda.campingcar.dao.desktop.dom;

/*
 * Soutenance Scraping
 * 2024/nov.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */

import fr.cda.campingcar.dao.desktop.DeskTopDAOFactory;
import fr.cda.campingcar.model.Dom;
import fr.cda.campingcar.util.DebugHelper;
import fr.cda.campingcar.util.LoggerConfig;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DomDAO implements DomDAOInt
{

    private static final Logger LOGGER_DAO = LoggerConfig.getLoggerScraping();
    protected Connection conn;
    private DeskTopDAOFactory factory;

    public DomDAO(DeskTopDAOFactory daoFactory) throws SQLException
    {
        try {
            this.factory = daoFactory;
            this.conn    = daoFactory.getConnection();
        } catch ( SQLException e ) {
            DebugHelper.debug("DomDAO", "Constructor", "ERROR", e.getSQLState(), false);
            throw new SQLException("Erreur DomDAO - " + e.getMessage());
        }
    }

    @Override
    public Map<String, Dom> findBySite(int siteId)
    {
        String sql = "SELECT dp.id, dp.site_id, dp.dom_element_id, dp.path, dp.dom_path_parent_id, dp.with_tag , " +
                     "de.nom " +
                     "FROM dom_path AS dp " +
                     "LEFT JOIN dom_path AS dp2 ON dp2.dom_element_id = dp.dom_path_parent_id " +
                     "LEFT JOIN dom_element AS de ON de.id = dp.dom_element_id " +
                     "WHERE dp.site_id = ? ";

        Map<Integer, Dom> domMap = new HashMap<>();

        try (PreparedStatement ps = this.conn.prepareStatement(sql)) {
            ps.setInt(1, siteId);
            ResultSet rs = ps.executeQuery();

            while ( rs.next() ) {

                int id = rs.getInt("id");
                siteId = rs.getInt("site_id");
                int domElementId = rs.getInt("dom_element_id");
                String path = rs.getString("path");
                int domParentId = rs.getInt("dom_path_parent_id");
                Boolean withTag = rs.getBoolean("with_tag");
                String name = rs.getString("nom");

                Dom dom = domMap.get(id);
                if ( dom == null ) {
                    dom = new Dom(id, siteId, domElementId, name, path, domParentId, withTag);
                    domMap.put(id, dom);
                }

            }

            this.factory.closeResultSet(rs);

        } catch ( SQLException e ) {
            DebugHelper.debug("DomDAO", "findBySite", "ERROR", e.getSQLState(), false);
            LOGGER_DAO.error("findBySite ERROR - SQL State : {}, Message: {}",
                             e.getSQLState(), e.getMessage(), e);
        } finally {
            try {
                this.factory.closeConnection();
            } catch ( SQLException e ) {
                e.printStackTrace();
            }

        }
        List<Dom> domList = new ArrayList<>(domMap.values());
        Map<String, Dom> results = this.buildHierarchy(domList, 0);
        return results;
    }

    private Map<String, Dom> buildHierarchy(List<Dom> domList, Integer parentId)
    {
        Map<String, Dom> hierarchy = new HashMap<>();

        for ( Dom dom : domList ) {
            if ( (parentId == null && dom.getDomParentId() == 0) ||
                 (parentId != null && dom.getDomParentId().equals(parentId)) ) {

                // Ajouter l'élément au niveau actuel
                hierarchy.put(dom.getName(), dom);

                // Appel récursif pour construire les enfants
                dom.setChildrensList(new ArrayList<>(buildHierarchy(domList, dom.getDomElementId()).values()));
            }
        }

        return hierarchy;
    }

}
