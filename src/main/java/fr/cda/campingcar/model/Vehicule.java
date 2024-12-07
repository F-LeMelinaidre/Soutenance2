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
    private String carburant;
    private int boite;
    private int nbPlace;
    private int nbCouchage;
    private boolean douche;
    private boolean wc;

    public void setModel(String model) {
        this.model = model;
    }

    public String getModel() {
        return model;
    }

    public void setType(TypeVehicule type) {
        this.type = type;
    }

    public TypeVehicule getType() {
        return type;
    }

    public void setBoite(int value) {}

    public void setCarburant(String value) {}

    public void setNbPlace(int value) {}

    public int getNbPlace() {
        return nbPlace;
    }

    public void setNbCouchage(int nbCouchage) {
        this.nbCouchage = nbCouchage;
    }

    public int getNbCouchage() {
        return nbCouchage;
    }

    public void setDouche(boolean douche) {
        this.douche = douche;
    }

    public boolean getDouche() {
        return douche;
    }

    public void setWc(boolean value) {
        this.wc = value;
    }

    public boolean getWc() {
        return wc;
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
