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

public class Ville
{
    private int geoId;
    private final int code;
    private int codePostal;
    private final double lat;
    private final double lng;
    private String nom;
    private final Departement departement;

    public Ville(int code, String nom, double lat, double lng, int codePostal, Departement departement)
    {
        //System.out.println(Config.YELLOW + "Ville [ code: " + code + ", nom: " + nom + ", lat: " + lat + ", lng: " + lng + " ]"+Config.RESET);
        this.lat         = lat;
        this.lng         = lng;
        this.code        = code;
        this.nom         = nom;
        this.departement = departement;
    }

    public String getNom()
    {
        return nom;
    }

    public void setNom(String nom)
    {
        this.nom = nom;
    }

    public int getCode()
    {
        return code;
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
        return departement;
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
        return this.nom;
    }
}
