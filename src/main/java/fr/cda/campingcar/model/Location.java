package fr.cda.campingcar.model;

/*
 * Soutenance Scraping
 * 2024/nov.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */

import fr.cda.campingcar.scraping.ScrapingModelInt;
import fr.cda.campingcar.settings.Config;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Location implements ScrapingModelInt<Object>
{
    public static List<Location> locationList;

    private Site site;
    private String url;
    private List<String> lien;
    private String image;
    private String titre;
    private String ville;
    private Integer tarif;
    private String description;
    private final Vehicule vehicule;

    public Location()
    {
        this.vehicule = new Vehicule();
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

    public String getTitre()
    {
        return this.titre;
    }

    public String getDescription()
    {
        return this.description;
    }

    public String getVille()
    {
        return this.ville;
    }

    public Integer getTarif()
    {
        return this.tarif;
    }

    public Vehicule getVehicule()
    {
        return vehicule;
    }

    public String getCarburant()
    {
        return this.vehicule.getCarburant();
    }

    public String getTransmission()
    {
        return this.vehicule.getTransmission();
    }

    public Integer getPlace()
    {
        return this.vehicule.getNbPlace();
    }

    public Integer getCouchage()
    {
        return this.vehicule.getNbCouchage();
    }

    public Boolean getDouche()
    {
        return this.vehicule.getDouche();
    }

    public Boolean getWc()
    {
        return this.vehicule.getWc();
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

    private String formateVille(String value)
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
                this.titre = value;
                break;
            case "lien":
                this.url = value;
                break;
            case "description":
                this.description = value;
                break;
            case "ville":
                this.ville = this.formateVille(value);
                break;
            case "tarif":
                this.tarif = this.extractInt(value);
                break;
            case "model":
                this.vehicule.setModel(value);
                break;
            case "carburant":
                this.vehicule.setCarburant(value);
                break;
            case "boite de vitesse":
                this.vehicule.setTransmission(value);
                break;
            case "nombre de place":
                Integer nbPlace = this.extractInt(value);
                this.vehicule.setNbPlace(nbPlace);
                break;
            case "nombre de couchage":
                Integer nbCouchage = this.extractInt(value);
                this.vehicule.setNbCouchage(nbCouchage);
                break;
            case "douche":
                boolean douche = value != null;
                this.vehicule.setDouche(douche);
                break;
            case "wc":
                boolean wc = value != null;
                this.vehicule.setWc(wc);
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
        return Config.YELLOW + "Titre : " + Config.WHITE + this.titre + "\n" +
               Config.YELLOW + "Lien Domain : " + Config.WHITE + this.site.getUrlRoot() + "\n" +
               Config.YELLOW + "Lien Recherche : " + Config.WHITE + this.url + "\n" +
               // Config.YELLOW + "Ville : " + Config.WHITE + this.ville + "\n" +
               // Config.YELLOW + "Tarif : " + Config.WHITE + this.tarif + "\n" +
               // Config.YELLOW + "Vehicule : " + "\n" + Config.CYAN + this.vehicule.toString(true) +
               // Config.YELLOW + "Description : " + "\n" + Config.CYAN + this.description + "\n" +
               Config.RESET + "\n";
    }
}
