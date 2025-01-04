package fr.cda.campingcar.model;

import fr.cda.campingcar.scraping.ScrapingModelInt;

import java.util.ArrayList;
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
public class Recherche<Location extends ScrapingModelInt<Object>>
{
    private final List<Site> sites;
    private final Map<String, ?> critereRecherche;
    private final List<ScrapingModelInt<Object>> resultats = new ArrayList<>();
    private final Supplier<Location> scrapingSupplier;


    public Recherche(List<Site> site, Map<String, ?> critereRecherche, Supplier<Location> scrapingSupplier)
    {
        this.sites            = site;
        this.critereRecherche = critereRecherche;
        this.scrapingSupplier = scrapingSupplier;
    }

    public List<Site> getListSites()
    {
        return sites;
    }

    public Map<String, ?> getCritereRecherche()
    {
        return critereRecherche;
    }

    public void addResultats(List<Location> resultats)
    {
        this.resultats.addAll(resultats);
    }

    public Location createAndAddScrapingSupplier()
    {
        Location resultat = scrapingSupplier.get();
        this.resultats.add(resultat);
        return resultat;
    }

    public void removeResultat(ScrapingModelInt<Object> resultat)
    {
        this.resultats.remove(resultat);
    }

    public List<ScrapingModelInt<Object>> getResultats()
    {
        return this.resultats;
    }
}
