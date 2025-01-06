package fr.cda.campingcar.model;

/*
 * Soutenance Scraping
 * 2024/nov.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */

import fr.cda.campingcar.scraping.ScrapingModel;
import fr.cda.campingcar.settings.Config;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Rent implements ScrapingModel<Object>
{

    private Site site;
    private String url;
    private List<String> link;
    private String image;
    private String title;
    private String city;
    private Integer price;
    private String description;
    private final Vehicle vehicle;

    public Rent()
    {
        this.vehicle = new Vehicle();
    }

    @Override
    public void setSite(Site site)
    {
        this.site = site;
    }

    @Override
    public Site getSite()
    {
        return site;
    }

    @Override
    public String getUrl()
    {
        return (this.url.startsWith("/") || !url.contains("https://www")) ? this.site.getUrlRoot() + this.url : this.url;
    }

    public String getImage() {return this.image;}

    public String getTitle()
    {
        return this.title;
    }

    public String getDescription()
    {
        return this.description;
    }

    public String getCity()
    {
        return this.city;
    }

    public int getPrice()
    {
        return this.price;
    }

    public Vehicle getVehicle()
    {
        return vehicle;
    }

    public String getFuel()
    {
        return this.vehicle.getFuel();
    }

    public String getGearBox()
    {
        return this.vehicle.getGearBox();
    }

    public Integer getSeat()
    {
        return this.vehicle.getNbSeat();
    }

    public Integer getBed()
    {
        return this.vehicle.getNbBed();
    }

    public Boolean getShower()
    {
        return this.vehicle.getShower();
    }

    public Boolean getWc()
    {
        return this.vehicle.getWc();
    }

    /**
     * Extrait le premier Int de la chaine de caratère
     */
    private int extractInt(String value)
    {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(value);

        return (matcher.find()) ? Integer.parseInt(matcher.group()) : 0;
    }

    // TODO VOIR A UTILISER API
    private String formatCity(String value)
    {
        Pattern pattern = Pattern.compile("\\(\\d+\\)");
        Matcher matcher = pattern.matcher(value);
        if ( matcher.find() ) {
            value = value.replaceAll("\\s*\\(\\d+\\)", "");
        }

        if ( value.contains("Disponible à") ) {
            value = value.replaceAll("Disponible à\\s*", "");
        }

        return value;
    }

    @Override
    public void setPropertieModel(String key, String value)
    {
        /*System.out.println(Config.YELLOW + "Url: " + Config.GREEN + this.url + Config.RESET);
        System.out.println(Config.YELLOW + "Key: " + Config.GREEN + key + Config.RESET);
        System.out.println(Config.YELLOW + "Value: " + Config.GREEN + value.toString() + Config.RESET);*/
        switch (key) {
            case "image":
                this.image = value;
                break;
            case "titre":
                this.title = value;
                break;
            case "lien":
                this.url = value;
                break;
            case "description":
                this.description = value;
                break;
            case "ville":
                this.city = this.formatCity(value);
                break;
            case "tarif":
                this.price = this.extractInt(value);
                break;
            case "model":
                this.vehicle.setModel(value);
                break;
            case "carburant":
                this.vehicle.setFuel(value);
                break;
            case "boite de vitesse":
                this.vehicle.setGearBox(value);
                break;
            case "nombre de place":
                Integer nbPlace = this.extractInt(value);
                this.vehicle.setNbSeat(nbPlace);
                break;
            case "nombre de couchage":
                Integer nbCouchage = this.extractInt(value);
                this.vehicle.setNbBed(nbCouchage);
                break;
            case "douche":
                boolean douche = value != null;
                this.vehicle.setShower(douche);
                break;
            case "wc":
                boolean wc = value != null;
                this.vehicle.setWc(wc);
                break;
        }
    }

    @Override
    public String toString()
    {
        return toPrint();
        /*return "Annonce{" +
               "domainUrl='" + domainUrl + '\'' +
               ", url='" + url + '\'' +
               ", titre='" + titre + '\'' +
               ", ville='" + ville + '\'' +
               ", tarif=" + tarif +
               ", vehicule=" + vehicule +
               '}';*/
    }

    public String toPrint()
    {
        return Config.YELLOW + "Titre : " + Config.WHITE + this.title + "\n" +
               Config.YELLOW + "Lien Domain : " + Config.WHITE + this.site.getUrlRoot() + "\n" +
               Config.YELLOW + "Lien Recherche : " + Config.WHITE + this.url + "\n" +
               // Config.YELLOW + "Ville : " + Config.WHITE + this.ville + "\n" +
               // Config.YELLOW + "Tarif : " + Config.WHITE + this.tarif + "\n" +
               // Config.YELLOW + "Vehicule : " + "\n" + Config.CYAN + this.vehicule.toString(true) +
               // Config.YELLOW + "Description : " + "\n" + Config.CYAN + this.description + "\n" +
               Config.RESET + "\n";
    }
}
