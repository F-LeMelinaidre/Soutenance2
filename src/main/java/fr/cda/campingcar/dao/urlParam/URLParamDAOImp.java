package fr.cda.campingcar.dao.urlParam;

/*
 * Soutenance Scraping
 * 2024/nov.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */

import fr.cda.campingcar.dao.DAOFactory;
import fr.cda.campingcar.model.UrlParam;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class URLParamDAOImp implements URLParamDAO {

    protected Connection conn;

    public URLParamDAOImp(DAOFactory daoFactory) throws SQLException {
        try {
            this.conn = daoFactory.getConnection();
        } catch (SQLException e) {
            throw new SQLException("Erreur getConnect() URLParamDAOImp() " + e);
        }
    }

    @Override
    public List<UrlParam> findBySite(int site_id) {
        return null;
    }
}
