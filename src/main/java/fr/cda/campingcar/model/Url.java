package fr.cda.campingcar.model;

import fr.cda.campingcar.settings.Config;

import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;

/*
 * Soutenance Scraping
 * 2024/nov.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */
public class Url
{
    private final int site_id;
    private final String url;
    private String searchUrl = null;
    private final Map<String, UrlParam> params = new HashMap<>() {};
    private final Map<String, Dom> xpath = new HashMap<>();

    public Url(int site_id, String url)
    {
        this.site_id = site_id;
        this.url = url;
    }

    public String getUrlRoot()
    {
        Matcher matcher = Config.HTTPS_WWW_PATTERN.matcher(this.url);
        return (matcher.find()) ? matcher.group(1) : this.url;
    }

    public String getSearchUrl() {
        return this.url + this.searchUrl;
    }

    public void putParam(String key, UrlParam urlParam)
    {
        this.params.put(key, urlParam);
    }

    private void buildUrlRecherche(Map<Integer, String> urlParams) {
        List<String> params = new ArrayList<>(urlParams.values());
        this.searchUrl = String.join("&", params);
    }

    /**
     * En fonction de la recherche prepare les parametres d'url
     * La paire clé valeur est enregistré en bd, avec pour certain un format
     * Formate les parametres
     */
    public void setValueParams(List<VehicleType> vehiclesTypeSelected, City cityDep, City cityArr,
                               LocalDate dateStart, LocalDate dateEnd, String budgetMin, String budgetMax)
    {

        String critere;
        Map<Integer, String> urlParams = new HashMap<>();

        for ( UrlParam urlParam : this.params.values() ) {
            critere = urlParam.getCriteria();

            switch (urlParam.getGroup()) {
                case "localisation":
                    City ville = (critere.equals("depart")) ? cityDep : cityArr;
                    urlParam.setValue(ville);
                    break;
                case "periode":
                    LocalDate date = (critere.equals("depart")) ? dateStart : dateEnd;
                    urlParam.setValue(date);
                    break;
                case "budget":
                    String budget = (critere.equals("min")) ? budgetMin : budgetMax;
                    urlParam.setValue(budget);
                    break;
            }

            if ( !urlParam.getGroup().equals("vehicule") )
                urlParams.put(urlParam.getPosition(),urlParam.getKeyValueParam());
        }


        int position = urlParams.size()+1;
        this.resolvevehicleTypeParams(position, vehiclesTypeSelected, urlParams);

        this.buildUrlRecherche(urlParams);
    }

    /**
     * Ajoute les vehicules à la map urlParams en fonction de la selection et des vehicules propsoé par le site
     * @param position position du parametre dans l url
     */
    private void resolvevehicleTypeParams(int position, List<VehicleType> vehiclesTypeSelected, Map<Integer, String> urlParams) {
        for ( VehicleType vehicleType : vehiclesTypeSelected) {
            int id = vehicleType.getId();
            String type = vehicleType.getType();
            String key = this.site_id + type + id;

            if(this.params.get(key) != null) {
                UrlParam urlParam = this.params.get(key);
                urlParams.put(position, urlParam.getKeyValueParam());
                position++;
            }
        }
    }

    @Override
    public String toString()
    {
        return "URL{" +
               "index='" + getUrlRoot() + '\'' +
               ", paramsMap=" + params +
               ", xpath=" + xpath +
               '}';
    }
}
