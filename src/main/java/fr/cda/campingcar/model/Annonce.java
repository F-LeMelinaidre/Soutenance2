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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Annonce implements ScrapingModel
{

    private int site_id;
    private String domainUrl;
    private String url;
    private String image;
    private String titre;
    private String ville;
    private Integer tarif;
    private Vehicule vehicule;

    public Annonce()
    {
        this.vehicule = new Vehicule();
    }

    @Override
    public void setSiteId(int id) {
        this.site_id = id;
    }

    @Override
    public void setDomainUrl(String domainUrl)
    {
        this.domainUrl = domainUrl;
    }

    @Override
    public String getDomainUrl()
    {
        return this.domainUrl;
    }

    @Override
    public String getUrl()
    {
        return (url.startsWith("/") || !url.contains("https://www") ) ? this.domainUrl + this.url : this.url;
    }

    public String getImage() { return this.image;}

    public String getTitre() {
        return this.titre;
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

    public String getCarburant() {
        return this.vehicule.getCarburant();
    }

    public Integer getTransmission() {
        return this.vehicule.getTransmission();
    }

    public Integer getPlace() {
        return this.vehicule.getNbPlace();
    }

    public Integer getCouchage() {
        return this.vehicule.getNbCouchage();
    }

    public Boolean getDouche() {
        return this.vehicule.getDouche();
    }

    public Boolean getWc() {
        return this.vehicule.getWc();
    }

    /**
     * Extrait le premier Int de la chaine de caratère
     *
     * @param value
     * @return
     */
    private int extractInt(String value)
    {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(value);

        return (matcher.find()) ? Integer.parseInt(matcher.group()) : 0;
    }

    private String formateVille(String value) {
        Pattern pattern = Pattern.compile("\\(\\d+\\)");
        Matcher matcher = pattern.matcher(value);
        if(matcher.find()) {
            value = value.replaceAll("\\s*\\(\\d+\\)", "");
        }

        if(value.contains("Disponible à")) {
            value = value.replaceAll("Disponible à\\s*", "");
        }

        return value;
    }

    @Override
    public void setPropertieModel(String key, String value)
    {
        int resultInt;
        switch (key) {
            case "image":
                this.image = value;
                break;
            case "titre":
                this.titre = value;
                break;
            case "lien principal":
                this.url = value;
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
                resultInt = this.extractInt(value);
                this.vehicule.setTransmission(resultInt);
                break;
            case "nombre de place":
                resultInt = this.extractInt(value);
                this.vehicule.setNbPlace(resultInt);
                break;
            case "nombre de couchage":
                resultInt = this.extractInt(value);
                this.vehicule.setNbCouchage(resultInt);
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
        StringBuilder str = new StringBuilder();
        str.append(Config.YELLOW).append("Titre : ").append(Config.WHITE).append(this.titre).append("\n");
        str.append(Config.YELLOW).append("Lien Domain : ").append(Config.WHITE).append(this.domainUrl).append("\n");
        str.append(Config.YELLOW).append("Lien Recherche : ").append(Config.WHITE).append(this.url).append("\n");
        str.append(Config.YELLOW).append("Ville : ").append(Config.WHITE).append(this.ville).append("\n");
        str.append(Config.YELLOW).append("Tarif : ").append(Config.WHITE).append(this.tarif).append("\n");
        str.append(Config.YELLOW).append("Vehicule : ").append("\n").append(Config.CYAN).append(this.vehicule.toString(true)).append(
                Config.RESET).append("\n");
        return str.toString();
    }
}
