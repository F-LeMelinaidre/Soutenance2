package fr.cda.campingcar.dao.site;

/*
 * Soutenance Scraping
 * 2024/nov.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */


import fr.cda.campingcar.dao.DAOFactory;
import fr.cda.campingcar.model.*;
import fr.cda.campingcar.util.DebugHelper;
import fr.cda.campingcar.util.LoggerConfig;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SiteDAO implements SiteDAOInt
{
    private static final Logger LOGGER_DAO = LoggerConfig.getLoggerScraping();
    protected Connection conn;

    public SiteDAO(DAOFactory daoFactory) throws SQLException
    {
        try {
            this.conn = daoFactory.getConnection();
        } catch ( SQLException e ) {
            DebugHelper.debug("SiteDAO", "Constructor", "ERROR", e.getSQLState(), false);
            throw new SQLException("Erreur SiteDAO " + e);
        }
    }

    @Override
    public Site find(int id)
    {
        return null;
    }

    @Override
    public List<Site> findAll()
    {
        String     sql   = "SELECT * FROM site";
        List<Site> sites = new ArrayList<Site>();

        try (Connection conn = this.conn;
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            while ( rs.next() ) {
                int    id   = rs.getInt("id");
                String nom  = rs.getString("nom");
                String url  = rs.getString("url");
                Site   site = new Site(id, nom, url);

                sites.add(site);
            }
        } catch ( SQLException e ) {
            DebugHelper.debug("DAOFACTORY", "findAll", "ERROR", e.getSQLState(), false);
            LOGGER_DAO.error("Close Connection ERROR - SQL State : {}, Message: {}",
                             e.getSQLState(), e.getMessage(), e);
        }
        return sites;
    }

    /**
     * Map les objets Site, avec leur Objet Url, contenant leurs Objets Parametres d'Url, inclu a l'objet Site <br>
     * Les Objets typeVehicules
     * @return
     */
    @Override
    public List<Site> findAllSitesWithVehiclesParamsAndXPaths()
    {
        String sql = "SELECT s.id            AS site_id, " +
                     "       s.nom           AS site_nom, " +
                     "       s.url           AS site_url, " +
                     "       NULL            AS param_position, " +
                     "       NULL            AS param_groupe, " +
                     "       NULL            AS param_type, " +
                     "       NULL            AS param_critere, " +
                     "       svt.param_key   AS param_key, " +
                     "       svt.param_value AS param_value, " +
                     "       NULL            AS param_format, " +
                     "       vt.id           AS vehicule_type_id, " +
                     "       vt.type         AS vehicule_type " +
                     "FROM site AS s " +
                     "         JOIN site_has_vehicule_type AS svt ON svt.site_id = s.id " +
                     "         JOIN vehicule_type AS vt ON vt.id = svt.vehicule_type_id " +
                     "UNION ALL " +
                     "SELECT s.id           AS site_id, " +
                     "       s.nom          AS site_nom, " +
                     "       s.url          AS site_url, " +
                     "       up.position    AS param_position, " +
                     "       up.groupe      AS param_groupe, " +
                     "       up.type        AS param_type, " +
                     "       up.critere     AS param_critere, " +
                     "       up.param_key   AS param_key, " +
                     "       up.param_value AS param_value, " +
                     "       up.format      AS param_format, " +
                     "       NULL           AS vehicule_type_id, " +
                     "       NULL           AS vehicule_type " +
                     "FROM site AS s " +
                     "         LEFT JOIN url_param AS up ON up.site_id = s.id " +
                     "ORDER BY site_id, vehicule_type_id, param_position";

        // Map de retour de la liste des site
        Map<Integer, Site> siteMap           = new HashMap<>();
        // Accumulateur pour eviter les doublons
        List<String>       paramAccumulateur = new ArrayList<>();

        try (Connection conn = this.conn;
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs       = ps.executeQuery();
            int       compteur = 0;
            if ( rs.next() ) {
                do {
                    compteur++;
                    int    id   = rs.getInt("site_id");
                    String name  = rs.getString("site_nom");
                    String link = rs.getString("site_url");

                    int    vehicleTypeId     = rs.getInt("vehicule_type_id");
                    String vehicleType       = rs.getString("vehicule_type");
                    String vehicleParamKey   = rs.getString("param_key");
                    String vehicleParamValue = rs.getString("param_value");

                    int    position = rs.getInt("param_position");
                    String group   = (vehicleTypeId == 0) ? rs.getString("param_groupe") : "vehicule";
                    String type     = (vehicleTypeId == 0) ? rs.getString("param_type") : vehicleType;
                    String criteria  = (vehicleTypeId == 0) ? rs.getString("param_critere") : String.valueOf(vehicleTypeId);


                    String paramKey = (vehicleTypeId == 0) ? rs.getString("param_key") : vehicleParamKey;
                    String paramValue = (vehicleTypeId == 0) ? rs.getString("param_value") : vehicleParamValue;
                    String format = rs.getString("param_format");

                    // recupere l'object site dans le map
                    Site site = siteMap.get(id);
                    Url  url  = null;

                    // Si il n existe pas on le cree et l ajoute au map
                    if ( site == null ) {
                        site = new Site(id, name, link);
                        siteMap.put(id, site);
                    }

                    // recupere l'objet Url
                    url = site.getUrl();

                    // Controle sans doute inutile
                    if ( site != null ) {
                        // id = site_id | type = type de parametre | criteria = criteria du parametre
                        String index = id + type + criteria;

                        // le parametre ne se trouve pas dans l acculmulateur on le créé
                        if ( !paramAccumulateur.contains(index)) {
                            paramAccumulateur.add(index);
                            UrlParam urlParam = new UrlParam(id, position, group, type, criteria, paramKey, paramValue, format);
                            url.putParam(index, urlParam);
                        }

                        // Si vehiculeType != 0 le tuple n'est pas un parametre donc un type de vehicule,
                        // on le cree et l'ajoute à la liste du site
                        if ( vehicleTypeId != 0 && !site.typeVehiculeExiste(vehicleTypeId) ) {
                            VehicleType typeVehicule = new VehicleType(vehicleTypeId, vehicleType);
                            site.addTypeVehicule(typeVehicule);
                        }
                    }
                } while ( rs.next() );
            }
        } catch ( SQLException e ) {
            DebugHelper.debug("DAOFACTORY", "findAllSitesWithVehiclesParamsAndXPaths", "ERROR", e.getSQLState(), false);
            LOGGER_DAO.error("findAllSitesWithVehiclesParamsAndXPaths ERROR - SQL State : {}, Message: {}",
                             e.getSQLState(), e.getMessage(), e);
        }
        return new ArrayList<>(siteMap.values());
    }
}
