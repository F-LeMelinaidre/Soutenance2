package fr.cda.campingcar.scraping;

import java.util.Map;

/*
 * Soutenance Scraping
 * 2024/déc.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */
public interface ScrapingModel<T>
{
    void setDomainUrl(String domainUrl);

    String getUrl();

    void setPropertieModel(String key, String value);
}
