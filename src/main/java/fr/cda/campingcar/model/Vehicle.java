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

public class Vehicle
{
    private String model;
    private VehicleType type;
    private String fuel = null;
    private String gearBox = null;
    private Integer nbSeat = null;
    private Integer nbBed = null;
    private Boolean shower = false;
    private Boolean wc = false;

    public void setModel(String model) {
        this.model = model;
    }

    public String getModel() {
        return  this.model;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }

    public VehicleType getType() {
        return  this.type;
    }

    public void setFuel(String value) {

        this.fuel = value;
    }

    public String getFuel() {
        return  this.fuel;
    }

    public void setGearBox(String value) {
        this.gearBox = value;
    }

    public String getGearBox() {
        return this.gearBox;
    }

    public void setNbSeat(Integer value) {
        this.nbSeat = value;
    }

    public Integer getNbSeat() {
        return  this.nbSeat;
    }

    public void setNbBed(Integer value) {
        this.nbBed = value;
    }

    public Integer getNbBed() {
        return  this.nbBed;
    }

    public void setShower(Boolean shower) {
        this.shower = shower;
    }

    public Boolean getShower() {
        return  this.shower;
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
               Config.YELLOW + tab + "Carburant : " + Config.WHITE + this.fuel + "\n" +
               Config.YELLOW + tab + "Boite : " + Config.WHITE + this.gearBox + "\n" +
               Config.YELLOW + tab + "Nb Place : " + Config.WHITE + this.nbSeat + "\n" +
               Config.YELLOW + tab + "Nb Couchage : " + Config.WHITE + this.nbBed + "\n" +
               Config.YELLOW + tab + "Douche : " + Config.WHITE + this.shower + "\n" +
               Config.YELLOW + tab + "Wc : " + Config.WHITE + this.wc + "\n" + Config.RESET + "\n";
    }
}
