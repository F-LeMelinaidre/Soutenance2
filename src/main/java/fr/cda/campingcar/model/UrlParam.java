package fr.cda.campingcar.model;

/*
 * Soutenance Scraping
 * 2024/nov.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class UrlParam
{
    private int site_id;
    private int position;
    private String groupe;
    private String type;
    private String critere;
    private String key;
    private String defaultValue;
    private String value;
    private String format;

    public UrlParam(int site_id, int position, String groupe, String type, String critere, String key, String defaultValue, String format)
    {
        //System.out.println(Config.CYAN + "URLParam: " + site_id + " " + groupe + " " + reference + " " + paramKey + " " + format + Config.RESET);
        this.site_id  = site_id;
        this.position = position;
        this.groupe   = groupe;
        this.type     = type;
        this.critere = critere;
        this.key          = key;
        this.defaultValue = defaultValue;
        this.format       = format;
    }

    public UrlParam(int site_id, int position, String groupe, String type, String critere, String key, String value)
    {
        //System.out.println(Config.CYAN + "URLParam: " + site_id + " " + groupe + " " + reference + " " + paramKey + " " + format + Config.RESET);
        this.site_id  = site_id;
        this.position = position;
        this.groupe   = groupe;
        this.type     = type;
        this.critere  = critere;
        this.key          = key;
        this.defaultValue = value;
    }

    public int getPosition() {
        return this.position;
    }
    public String getGroupe()
    {
        return this.groupe;
    }

    public String getType()
    {
        return this.type;
    }

    public String getCritere()
    {
        return this.critere;
    }

    public <T> void setValue(T value) {
        if ( value instanceof Ville) {
            this.setLocalisationParam((Ville) value);
        } else if ( value instanceof LocalDate ) {
            this.setFormateDate((LocalDate) value);
        } else if( value instanceof String ) {
            this.value = (String)value;
        }
    }

    public String getKeyValueParam() {
        String value = (this.value == null) ? this.defaultValue : this.value;
        return this.key + "=" + value;
    }

    private void setLocalisationParam(Ville ville)
    {
        switch (this.type) {
            case "latitude":
                this.value = ville.getLat();
                break;
            case "longitude":
                this.value = ville.getLng();
                break;
            case "code_pays":
            case "pays":
            case "rayon":
                this.value = this.defaultValue;
                break;

            default:
                String val = ville.getNom();
                if ( format != null ) {
                    val = this.encodeUTF8(ville.getFormated(format));
                }

                this.value = val;
                break;
        }
    }

    private void setFormateDate(LocalDate value)
    {
        DateTimeFormatter formatSortie = DateTimeFormatter.ofPattern(this.format);
        this.value = encodeUTF8(value.format(formatSortie));
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

    @Override
    public String toString()
    {
        return "UrlParam{" +
               "site_id=" + site_id +
               ", position=" + position +
               ", groupe='" + groupe + '\'' +
               ", type='" + type + '\'' +
               ", critere='" + critere + '\'' +
               ", key='" + key + '\'' +
               ", value='" + value + '\'' +
               ", defaultValue=" + defaultValue +
               ", format='" + format + '\'' +
               '}';
    }
}
