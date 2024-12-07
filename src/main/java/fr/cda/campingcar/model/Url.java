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
    private int site_id;
    private String url;
    private String urlRecherche = null;
    private Map<String, UrlParam> params = new HashMap<>() {};
    private Map<String, Dom> xpath = new HashMap<String, Dom>();

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

    public String getUrlRecherche() {
        return this.url + this.urlRecherche;
    }

    public void putParam(String key, UrlParam urlParam)
    {
        this.params.put(key, urlParam);
    }

    private void buildUrlRecherche(Map<Integer, String> urlParams) {
        List<String> params = new ArrayList<>(urlParams.values());
        this.urlRecherche = String.join("&", params);
    }

    /**
     * En fonction de la recherche prepare les parametres d'url
     * La paire clé valeur est enregistré en bd, avec pour certain un format
     * Formate les parametres
     * @param typeVehiculesSelected
     * @param villeDep
     * @param villeArr
     * @param dateDepart
     * @param dateArrivee
     * @param budgetMin
     * @param budgetMax
     */
    public void setValueParams(List<TypeVehicule> typeVehiculesSelected, Ville villeDep, Ville villeArr,
                               LocalDate dateDepart, LocalDate dateArrivee, String budgetMin, String budgetMax)
    {

        String critere;
        Map<Integer, String> urlParams = new HashMap<>();

        for ( UrlParam urlParam : this.params.values() ) {
            critere = urlParam.getCritere();

            switch (urlParam.getGroupe()) {
                case "localisation":
                    Ville ville = (critere.equals("depart")) ? villeDep : villeArr;
                    urlParam.setValue(ville);
                    break;
                case "periode":
                    LocalDate date = (critere.equals("depart")) ? dateDepart : dateArrivee;
                    urlParam.setValue(date);
                    break;
                case "budget":
                    String budget = (critere.equals("min")) ? budgetMin : budgetMax;
                    urlParam.setValue(budget);
                    break;
            }

            if ( !urlParam.getGroupe().equals("vehicule") )
                urlParams.put(urlParam.getPosition(),urlParam.getKeyValueParam());
        }


        int position = urlParams.size()+1;
        this.resolveTypeVehiculeParams(position, typeVehiculesSelected, urlParams);

        this.buildUrlRecherche(urlParams);
    }

    /**
     * Ajoute les vehicules à la map urlParams en fonction de la selection et des vehicules propsoé par le site
     * @param position position du parametre dans l url
     * @param typeVehiculesSelected
     * @param urlParams
     */
    private void resolveTypeVehiculeParams(int position, List<TypeVehicule> typeVehiculesSelected, Map<Integer, String> urlParams) {
        for (TypeVehicule typeVehicule : typeVehiculesSelected) {
            int id = typeVehicule.getId();
            String type = typeVehicule.getType();
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
