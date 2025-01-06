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
    private final int number;
    private String name;
    private final String region;

    public Departement(int number, String name, String region)
    {
        this.number = number;
        this.name   = name;
        this.region = region;
    }

    public String getNumber()
    {
        return String.format("%02d", this.number);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getRegion()
    {
        return this.region;
    }

    @Override
    public String toString()
    {
        return "Departement: " + this.number + " - " + this.name;
    }
}
