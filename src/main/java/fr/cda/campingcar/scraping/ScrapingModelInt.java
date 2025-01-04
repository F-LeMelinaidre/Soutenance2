package fr.cda.campingcar.scraping;

import fr.cda.campingcar.model.Site;

/*
 * Soutenance Scraping
 * 2024/déc.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */
public interface ScrapingModelInt<Object>
{
    void setSite(Site site);

    Site getSite();

    String getUrl();

    void setPropertieModel(String key, String value);
}
