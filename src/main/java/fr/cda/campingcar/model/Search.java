package fr.cda.campingcar.model;

import fr.cda.campingcar.scraping.ScrapingModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
    private final List<Rent> results = new ArrayList<>();
    private final Supplier<Rent> scrapingSupplier;
    private LocalDate date;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy");
    private List<Site> sites;
    private List<VehicleType> vehiclesType;
    private String budgetMin;
    private String budgetMax;
    private City departureCity;
    private City arrivalCity;
    private LocalDate periodStart;
    private LocalDate periodEnd;

    public Search(Supplier<Rent> scrapingSupplier)
    {
        this.scrapingSupplier = scrapingSupplier;
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

    public List<Rent> getResults()
    {
        return this.results;
    }

    public void setDate(LocalDate date)
    {
        this.date = date;
    }
    public String getDateString()
    {
        return date.format(this.formatter);
    }

    public void setListSites(List<Site> sites)
    {
        this.sites = sites;
    }

    public List<Site> getListSites()
    {
        return sites;
    }

    public void setListVehicle(List<VehicleType> vehicles)
    {
        this.vehiclesType = vehicles;
    }

    public void setBugdetMin(String min)
    {
        this.budgetMin = (min.isEmpty() || min == null) ? "Non défini" : min;
    }

    public String getBudgetMin()
    {
        return budgetMin;
    }

    public void setBudgetMax(String max)
    {
        this.budgetMax = (max.isEmpty() || max == null) ? "Non défini" : max;
    }

    public String getBudgetMax()
    {
        return budgetMax;
    }

    public List<VehicleType> getVehiclesType()
    {
        return vehiclesType;
    }

    public LocalDate getDate()
    {
        return date;
    }

    public void setDepartureCity(City city)
    {
        this.departureCity = city;
    }

    public String getDepartureCityString()
    {
        return departureCity.getFormated("{ville} ({numDep}) {region}");
    }

    public void setArrivalCity(City city)
    {
        this.arrivalCity = city;
    }

    public String getArrivalCityString()
    {
        return arrivalCity.getFormated("{ville} ({numDep}) {region}");
    }

    public void setPeriodStart(LocalDate date)
    {
        this.periodStart = date;
    }

    public String getPeriodStartString()
    {
        return periodStart.format(this.formatter);
    }

    public void setPeriodEnd(LocalDate date)
    {
        this.periodEnd = date;
    }

    public String getPeriodEndString()
    {
        return periodEnd.format(this.formatter);
    }
}
