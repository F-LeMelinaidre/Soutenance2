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
    private String carburant = "non connu!";
    private int boite = 0;
    private int nbPlace = 0;
    private int nbCouchage = 0;
    private boolean douche = false;
    private boolean wc = false;

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

    public void setBoite(int value) {}

    public void setCarburant(String value) {}

    public void setNbPlace(int value) {
        this.nbPlace = value;
    }

    public int getNbPlace() {
        return  this.nbPlace;
    }

    public void setNbCouchage(int value) {
        this.nbCouchage = value;
    }

    public int getNbCouchage() {
        return  this.nbCouchage;
    }

    public void setDouche(boolean douche) {
        this.douche = douche;
    }

    public boolean getDouche() {
        return  this.douche;
    }

    public void setWc(boolean value) {
        this.wc = value;
    }

    public boolean getWc() {
        return  this.wc;
    }

    public String toString(boolean putTab)
    {
        String tab = (putTab ? "\t" : "");
        StringBuilder str = new StringBuilder();
        str.append(Config.YELLOW).append(tab).append("Model : ").append(Config.WHITE).append(this.model).append("\n");
        str.append(Config.YELLOW).append(tab).append("Carburant : ").append(Config.WHITE).append(this.carburant).append("\n");
        str.append(Config.YELLOW).append(tab).append("Boite : ").append(Config.WHITE).append(this.boite).append("\n");
        str.append(Config.YELLOW).append(tab).append("Nb Place : ").append(Config.WHITE).append(this.nbPlace).append("\n");
        str.append(Config.YELLOW).append(tab).append("Nb Couchage : ").append(Config.WHITE).append(this.nbCouchage).append("\n");
        str.append(Config.YELLOW).append(tab).append("Douche : ").append(Config.WHITE).append(this.douche).append("\n");
        str.append(Config.YELLOW).append(tab).append("Wc : ").append(Config.WHITE).append(this.wc).append("\n").append(Config.RESET).append("\n");
        return str.toString();
    }
}
