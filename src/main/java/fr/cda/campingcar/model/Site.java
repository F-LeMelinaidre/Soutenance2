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
    private String nom;
    private final Url url;
    private Map<String, Dom> domMap;
    private final Map<Integer, TypeVehicule> typeVehicules = new HashMap<>();


    public Site(int id, String name, String url)
    {
        //System.out.println(Config.YELLOW + "Site: " + id + " " + name + Config.RESET);
        this.id  = id;
        this.nom = name;
        this.url = new Url(id, url);
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
        return this.nom;
    }

    public void setName(String name)
    {
        this.nom = name;
    }

    public Url getUrl()
    {
        return this.url;
    }

    public String getUrlRoot()
    {
        return this.url.getUrlRoot();
    }

    public String getUrlRecherche()
    {
        return this.url.getUrlRecherche();
    }

    public void setUrlRecherche(List<TypeVehicule> typeVehiculesSelected, Ville villeDep,
                                Ville villeArr, LocalDate dateDepart, LocalDate dateArrivee,
                                String budgetMin, String budgetMax)
    {
        this.url.setValueParams(typeVehiculesSelected, villeDep, villeArr, dateDepart, dateArrivee, budgetMin, budgetMax);
    }

    public void setDomMap(Map<String, Dom> domMap) {
        this.domMap = domMap;
    }

    public Map<String, Dom> getDomMap() {
        return this.domMap;
    }

    public void addTypeVehicule(TypeVehicule typeVehicule)
    {
        this.typeVehicules.put(typeVehicule.getId(), typeVehicule);
    }

    public Map<Integer, TypeVehicule> getTypeVehicules()
    {
        return this.typeVehicules;
    }

    public boolean typeVehiculeExiste(int vehiculeTypeId)
    {
        return this.typeVehicules.containsKey(vehiculeTypeId);
    }

    public List<TypeVehicule> filterVehicule(List<TypeVehicule> typeVehiculesSelected)
    {
        List<TypeVehicule> vehiculesParams = new ArrayList<>();
        for ( TypeVehicule typeVehicule : typeVehiculesSelected ) {
            int id = typeVehicule.getId();
            if ( this.typeVehicules.containsKey(id) ) {
                vehiculesParams.add(this.typeVehicules.get(id));
            }
        }

        return vehiculesParams;
    }

    public String toString()
    {
        return this.nom;
    }
}
