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

    private int siteId;
    private int domElementId;
    private String nom;
    private String xpath;
    private int domParent;
    private List<Dom> listEnfants = new ArrayList<>();
    private List<String> motCle = new ArrayList();

    public Dom(int siteId, int domElementId, String nom, String xpath, int domParent) {
        this.siteId       = siteId;
        this.domElementId = domElementId;
        this.nom          = nom;
        this.xpath     = xpath;
        this.domParent = domParent;
    }

    public int getDomElementId()
    {
        return domElementId;
    }

    public String getNom()
    {
        return nom;
    }

    public String getXPath() {
        return this.xpath;
    }

    public Integer getDomParent() {
        return this.domParent;
    }

    public List<String> getMotCles() {
        return this.motCle;
    }

    public void addMotCle(String motCle) {
        this.motCle.add(motCle);
    }

    public void setEnfants(List<Dom> enfants) {
        this.listEnfants = enfants;
    }

    public List<Dom> getListEnfants() {
        return this.listEnfants;
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    public String toString(int indentLevel) {
        String indent = "  ".repeat(indentLevel);

        StringBuilder str = new StringBuilder();
        str.append(indent).append(Config.YELLOW).append("siteId : ").append(Config.WHITE).append(this.siteId).append("\n");
        str.append(indent).append(Config.YELLOW).append("domElementId : ").append(Config.WHITE).append(this.domElementId).append("\n");
        str.append(indent).append(Config.YELLOW).append("nom : ").append(Config.WHITE).append(this.nom).append("\n");
        str.append(indent).append(Config.YELLOW).append("xPath : ").append(Config.CYAN).append(this.xpath).append(Config.RESET).append("\n");


        return str.toString();
    }
}
