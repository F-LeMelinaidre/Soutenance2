package fr.cda.campingcar.scraping;

/*
 * Soutenance Scraping
 * 2024/déc.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */
public interface ScrapingModelInt<T>
{
    void setDomainUrl(String domainUrl);

    String getDomainUrl();

    String getUrl();

    <T> void setPropertieModel(String key, T value);

    void setSiteId(int id);

}
