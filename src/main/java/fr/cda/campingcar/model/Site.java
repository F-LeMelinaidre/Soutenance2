package fr.cda.campingcar.model;

/*
 * Soutenance Scraping
 * 2024/nov.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Site
{

    private int id;
    private String name;
    private final Url url;
    private Map<String, Dom> domMap;
    private final Map<Integer, VehicleType> vehiclesType = new HashMap<>();


    public Site(int id, String name, String url)
    {
        //System.out.println(Config.YELLOW + "Site: " + id + " " + name + Config.RESET);
        this.id   = id;
        this.name = name;
        this.url  = new Url(id, url);
    }

    public int getId()
    {
        return this.id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Url getUrl()
    {
        return this.url;
    }

    public String getUrlRoot()
    {
        return this.url.getUrlRoot();
    }

    public String getSearchUrl()
    {
        return this.url.getSearchUrl();
    }

    public void setUrlSearch(List<VehicleType> vehiclesTypeSelected, City cityDep,
                             City cityArr, LocalDate dateStart, LocalDate dateEnd,
                             String budgetMin, String budgetMax)
    {
        this.url.setValueParams(vehiclesTypeSelected, cityDep, cityArr, dateStart, dateEnd, budgetMin, budgetMax);
    }

    public void setDomMap(Map<String, Dom> domMap) {
        this.domMap = domMap;
    }

    public Map<String, Dom> getDomMap() {
        return this.domMap;
    }

    public void addTypeVehicule(VehicleType vehicleType)
    {
        this.vehiclesType.put(vehicleType.getId(), vehicleType);
    }

    public Map<Integer, VehicleType> getVehiculesType()
    {
        return this.vehiclesType;
    }

    public boolean typeVehiculeExiste(int vehicleTypeId)
    {
        return this.vehiclesType.containsKey(vehicleTypeId);
    }

    public List<VehicleType> filterVehicle(List<VehicleType> vehiclesTypeSelected)
    {
        List<VehicleType> vehiclesParams = new ArrayList<>();
        for ( VehicleType vehiculeType : vehiclesTypeSelected ) {
            int id = vehiculeType.getId();
            if ( this.vehiclesType.containsKey(id) ) {
                vehiclesParams.add(this.vehiclesType.get(id));
            }
        }

        return vehiclesParams;
    }

    public String toString()
    {
        return this.name;
    }
}
