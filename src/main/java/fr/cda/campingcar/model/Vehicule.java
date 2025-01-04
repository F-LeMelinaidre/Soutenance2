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

public class Vehicule {
    private String model;
    private TypeVehicule type;
    private String carburant = null;
    private String transmission = null;
    private Integer nbPlace = null;
    private Integer nbCouchage = null;
    private Boolean douche = false;
    private Boolean wc = false;

    public void setModel(String model) {
        this.model = model;
    }

    public String getModel() {
        return  this.model;
    }

    public void setType(TypeVehicule type) {
        this.type = type;
    }

    public TypeVehicule getType() {
        return  this.type;
    }

    public void setCarburant(String value) {

        this.carburant = value;
    }

    public String getCarburant() {
        return  this.carburant;
    }

    public void setTransmission(String value) {
        this.transmission = value;
    }

    public String getTransmission() {
        return this.transmission;
    }

    public void setNbPlace(Integer value) {
        this.nbPlace = value;
    }

    public Integer getNbPlace() {
        return  this.nbPlace;
    }

    public void setNbCouchage(Integer value) {
        this.nbCouchage = value;
    }

    public Integer getNbCouchage() {
        return  this.nbCouchage;
    }

    public void setDouche(Boolean douche) {
        this.douche = douche;
    }

    public Boolean getDouche() {
        return  this.douche;
    }

    public void setWc(Boolean value) {
        this.wc = value;
    }

    public Boolean getWc() {
        return  this.wc;
    }

    public String toString(Boolean putTab)
    {
        String tab = (putTab ? "\t" : "");
        return Config.YELLOW + tab + "Model : " + Config.WHITE + this.model + "\n" +
               Config.YELLOW + tab + "Carburant : " + Config.WHITE + this.carburant + "\n" +
               Config.YELLOW + tab + "Boite : " + Config.WHITE + this.transmission + "\n" +
               Config.YELLOW + tab + "Nb Place : " + Config.WHITE + this.nbPlace + "\n" +
               Config.YELLOW + tab + "Nb Couchage : " + Config.WHITE + this.nbCouchage + "\n" +
               Config.YELLOW + tab + "Douche : " + Config.WHITE + this.douche + "\n" +
               Config.YELLOW + tab + "Wc : " + Config.WHITE + this.wc + "\n" + Config.RESET + "\n";
    }
}
