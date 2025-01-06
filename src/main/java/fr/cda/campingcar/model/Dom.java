package fr.cda.campingcar.model;

/*
 * Soutenance Scraping
 * 2024/nov.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */

import fr.cda.campingcar.settings.Config;

import java.util.ArrayList;
import java.util.List;

public class Dom
{

    private final int id;
    private final int siteId;
    private final int domElementId;
    private final String name;
    private final String xpath;
    private final int domParentId;
    private final Boolean withTag;
    private final List<Dom> childrensList = new ArrayList<>();

    public Dom(int id, int siteId, int domElementId, String name, String xpath, int domParentId, Boolean withTag)
    {
        this.id           = id;
        this.siteId       = siteId;
        this.domElementId = domElementId;
        this.name         = name;
        this.xpath        = xpath;
        this.domParentId  = domParentId;
        this.withTag      = withTag;
    }

    public int getId()
    {
        return this.id;
    }

    public int getDomElementId()
    {
        return this.domElementId;
    }

    public String getName()
    {
        return this.name;
    }

    public String getXPath()
    {
        return this.xpath;
    }

    public Integer getDomParentId()
    {
        return this.domParentId;
    }

    public void setChildrensList(List<Dom> childrensList)
    {
        this.childrensList.addAll(childrensList);
    }

    public List<Dom> geChildrensList()
    {
        return this.childrensList;
    }

    @Override
    public String toString()
    {
        return this.toString(0);
    }

    public String toString(int indentLevel)
    {
        String indent = "  ".repeat(indentLevel);

        return indent + Config.YELLOW + "siteId : " + Config.WHITE + this.siteId + "\n" +
               indent + Config.YELLOW + "domElementId : " + Config.WHITE + this.domElementId + "\n" +
               indent + Config.YELLOW + "domParentId : " + Config.WHITE + this.domParentId + "\n" +
               indent + Config.YELLOW + "withTag : " + Config.WHITE + this.withTag + "\n" +
               indent + Config.YELLOW + "nom : " + Config.WHITE + this.name + "\n" +
               indent + Config.YELLOW + "xPath : " + Config.CYAN + this.xpath + "\n" +
               indent + Config.YELLOW + "xPath Child : " + Config.CYAN + this.childrensList + Config.RESET + "\n";
    }

}
