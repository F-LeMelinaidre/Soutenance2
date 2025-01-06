package fr.cda.campingcar.model;

import fr.cda.campingcar.scraping.ScrapingModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/*
 * Soutenance Scraping
 * 2024/nov.
 *
 * Le Frédéric Le Melinaidre
 * Formation CDA
 * Greta Vannes
 */
public class Search<Rent extends ScrapingModel<Object>>
{
    private final List<ScrapingModel<Object>> results = new ArrayList<>();
    private final Supplier<Rent> scrapingSupplier;
    private LocalDate date;
    private List<Site> sites;


    public Search(Supplier<Rent> scrapingSupplier)
    {
        this.scrapingSupplier = scrapingSupplier;
    }

    public List<Site> getListSites()
    {
        return this.sites;
    }

    public Rent createAndAddScrapingSupplier()
    {
        Rent result = this.scrapingSupplier.get();
        this.results.add(result);
        return result;
    }

    public void removeResult(ScrapingModel<Object> result)
    {
        this.results.remove(result);
    }

    public List<ScrapingModel<Object>> getResults()
    {
        return this.results;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setSitesList(List<Site> sites) {
        this.sites = sites;
    }

    public void addCriteria(String nom, String value){}
    public void addCriteria(String nom, int value){}
    public void addCriteria(String nom, LocalDate value){}
    public void addCriteria(String nom, List<SearchCriteria> criteriaList) {}
}
