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
    private final int numero;
    private String nom;
    private final String region;

    public Departement(int numero, String nom, String region)
    {
        this.numero = numero;
        this.nom = nom;
        this.region = region;
    }

    public String getNumero()
    {
        return String.format("%02d", this.numero);
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
