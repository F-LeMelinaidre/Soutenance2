package fr.cda.campingcar.model;

import fr.cda.campingcar.scraping.XPath;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
 * Soutenance Scraping
 * 2024/nov.
 *
 * Le Frédéric Le Melinaidre
 * Formation CDA
 * Greta Vannes
 */
public class Recherche
{
    private final Site site;
    private final Ville villeDepart;
    private final Ville villeArrivee;
    private final LocalDate dateDepart;
    private final LocalDate dateArrivee;
    private final String budgetMin;
    private final String budgetMax;
    private String url;
    private List<TypeVehicule> typeVehiculeList;
    private List<UrlParam> urlParams;
    private Map<String, XPath> xPathMap;

    public Recherche(Site site, List<TypeVehicule> typeVehiculeList, Ville villeDepart, Ville villeArrivee,
                     LocalDate dateDepart, LocalDate dateArrivee, String budgetMin, String budgetMax)
    {
        this.site             = site;
        this.typeVehiculeList = typeVehiculeList;
        this.villeDepart      = villeDepart;
        this.villeArrivee     = villeArrivee;
        this.dateDepart       = dateDepart;
        this.dateArrivee      = dateArrivee;
        this.budgetMax        = budgetMax;
        this.budgetMin        = budgetMin;

        this.generateUrl(site.getUrl().getRootPath(), site.getUrl().getParams());
    }

    public String getSiteNom() {
        return site.getNom();
    }

    public String getUrl()
    {
        return this.url;
    }

    public void setXPath(Map<String, XPath> xPathMap)
    {
        this.xPathMap = xPathMap;
    }

    public Map<String, XPath> getXPath() {
        return this.xPathMap;
    }

    private void generateUrl(String rootPath, List<UrlParam> params)
    {
        this.url = rootPath;
        this.url += this.mapByCritere(params);
        this.url += "&" + this.filtreVehicule(typeVehiculeList, site.getTypeVehicules());
        System.out.println(this.url);
    }

    private String mapByCritere(List<UrlParam> urlParams)
    {
        List<String> params = new ArrayList<>();
        String       critere;

        for ( UrlParam urlParam : urlParams ) {
            String param = null;
            critere = urlParam.getCritere();

            switch (urlParam.getGroupe()) {
                case "localisation":
                    Ville ville = (critere.equals("depart")) ? this.villeDepart : this.villeArrivee;
                    param = this.getLocalisationParam(ville, urlParam);
                    break;
                case "periode":
                    LocalDate date = (critere.equals("depart")) ? this.dateDepart : this.dateArrivee;
                    param = this.formateDate(date, urlParam.getFormat());
                    break;
                case "budget":
                    String budget = (critere.equals("min")) ? this.budgetMin : this.budgetMax;
                    param = urlParam.getKey() + "=" + budget;
                    break;
                case "order":
                    param = urlParam.getKey() + "=" + urlParam.getDefaultValue();
                default:
                    throw new IllegalArgumentException("Paramètre non reconnu " + urlParam.getGroupe());

            }
            if ( param != null ) params.add(param);
        }
        return String.join("&", params);
    }

    private String getLocalisationParam(Ville ville, UrlParam urlParam)
    {
        String type              = urlParam.getType();
        String paramKey          = urlParam.getKey();
        String paramDefaultValue = urlParam.getDefaultValue();
        String format            = urlParam.getFormat();

        String param;
        switch (type) {
            case "latitude":
                param = paramKey + "=" + ville.getLat();
                break;
            case "longitude":
                param = paramKey + "=" + ville.getLng();
                break;
            case "code_pays":
            case "pays":
            case "rayon":
                param = paramKey + "=" + paramDefaultValue;
                break;

            default:
                String val = ville.getNom();
                if ( format != null ) {
                    val = this.encodeUTF8(ville.getFormated(format));
                }

                param = paramKey + "=" + val;
                break;
        }
        return param;
    }

    private String formateDate(LocalDate value, String pattern)
    {
        DateTimeFormatter formatSortie = DateTimeFormatter.ofPattern(pattern);
        return value.format(formatSortie);
    }

    private String filtreVehicule(List<TypeVehicule> selectionVehiculeList,
                                  Map<Integer, TypeVehicule> vehiculeSiteList)
    {
        List<String> vehiculesParams = new ArrayList<>();
        String       result          = "";
        for ( TypeVehicule typeVehicule : selectionVehiculeList ) {
            int id = typeVehicule.getId();
            if ( vehiculeSiteList.containsKey(id) ) {
                String paramKey   = vehiculeSiteList.get(id).getParamKey();
                String paramValue = vehiculeSiteList.get(id).getParamValue();
                vehiculesParams.add(paramKey + "=" + paramValue);
            }
        }

        return !vehiculesParams.isEmpty() ? String.join("&", vehiculesParams) : "";
    }

    private String encodeUTF8(String item)
    {
        String result = "";
        try {
            result = URLEncoder.encode(item, "UTF-8");
        } catch ( UnsupportedEncodingException e ) {
            throw new RuntimeException("Erreur d'encodage : " + e.getMessage(), e);
        }

        return result;
    }
}
