/*
 * Soutenance $project.projectName$
 * 11/2024
 *
 * Le Mélinaidre Frédéric
 * Formation CDA
 * Greta Vannes
 */

package fr.cda.campingcar.model;

public class Departement
{
    private int numero;
    private String nom;
    private String region;

    public Departement(int numero, String nom, String region)
    {
        this.numero = numero;
        this.nom = nom;
        this.region = region;
    }

    public int getNumero()
    {
        return numero;
    }

    public String getNom()
    {
        return nom;
    }

    public void setNom(String nom)
    {
        this.nom = nom;
    }

    public String getRegion()
    {
        return region;
    }

    @Override
    public String toString()
    {
        return "Departement: " + numero + " - " + nom;
    }
}
