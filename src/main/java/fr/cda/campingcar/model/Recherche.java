package fr.cda.campingcar.model;

import fr.cda.campingcar.scraping.ScrapingModel;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/*
 * Soutenance Scraping
 * 2024/nov.
 *
 * Le Frédéric Le Melinaidre
 * Formation CDA
 * Greta Vannes
 */
public class Recherche<T extends ScrapingModel>
{
    private List<Site> sites;
    private Map<String, Object> critereRecherche;
    private Supplier<T> scrapingSupplier;

    public Recherche(List<Site> site, Map<String, Object> critereRecherche, Supplier<T> scrapingSupplier)
    {
        this.sites = site;
        this.critereRecherche = critereRecherche;
        this.scrapingSupplier    = scrapingSupplier;
    }

    public List<Site> getListSites() {
        return sites;
    }

    public T createScrapingSupplier() {
        return this.scrapingSupplier.get();
    }
}
