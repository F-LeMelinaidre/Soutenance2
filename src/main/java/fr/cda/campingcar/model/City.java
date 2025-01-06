/*
 * Soutenance $project.projectName$
 * 11/2024
 *
 * Le Mélinaidre Frédéric
 * Formation CDA
 * Greta Vannes
 */

package fr.cda.campingcar.model;

import fr.cda.campingcar.settings.Config;

import java.util.HashMap;
import java.util.Map;

public class City implements SearchCriteria
{
    private int geoId;
    private final int code;
    private int zipCode;
    private final double lat;
    private final double lng;
    private String name;
    private final Departement departement;

    public City(int code, String nom, double lat, double lng, int zipCode, Departement departement)
    {
        //System.out.println(Config.YELLOW + "Ville [ code: " + code + ", nom: " + nom + ", lat: " + lat + ", lng: " + lng + " ]"+Config.RESET);
        this.lat         = lat;
        this.lng         = lng;
        this.code        = code;
        this.name        = nom;
        this.departement = departement;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getCode()
    {
        return this.code;
    }

    public String getLat()
    {
        return String.valueOf(this.lat);
    }

    public String getLng()
    {
        return String.valueOf(this.lng);
    }

    public Departement getDepartement()
    {
        return this.departement;
    }


    public String getFormated(String pattern)
    {
        Map<String, String> resolvedValues = new HashMap<>();

        Config.VILLE_PATTERN.forEach((key, method) -> resolvedValues.put(key, method.apply(this)));

        for (Map.Entry<String, String> entry : resolvedValues.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            pattern = pattern.replace(placeholder, entry.getValue());
        }

        return pattern;
    }


    @Override
    public String toString()
    {
        return this.name;
    }

    @Override
    public String valeur()
    {
        return this.name + " " + this.departement.getName() + " (" + this.departement.getNumber() + ") " + this.departement.getRegion();
    }
}
