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
        StringBuilder str = new StringBuilder();
        str.append(Config.YELLOW).append(tab).append("Model : ").append(Config.WHITE).append(this.model).append("\n");
        str.append(Config.YELLOW).append(tab).append("Carburant : ").append(Config.WHITE).append(this.carburant).append("\n");
        str.append(Config.YELLOW).append(tab).append("Boite : ").append(Config.WHITE).append(this.transmission).append("\n");
        str.append(Config.YELLOW).append(tab).append("Nb Place : ").append(Config.WHITE).append(this.nbPlace).append("\n");
        str.append(Config.YELLOW).append(tab).append("Nb Couchage : ").append(Config.WHITE).append(this.nbCouchage).append("\n");
        str.append(Config.YELLOW).append(tab).append("Douche : ").append(Config.WHITE).append(this.douche).append("\n");
        str.append(Config.YELLOW).append(tab).append("Wc : ").append(Config.WHITE).append(this.wc).append("\n").append(Config.RESET).append("\n");
        return str.toString();
    }
}
