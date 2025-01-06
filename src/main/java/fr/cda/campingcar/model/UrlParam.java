package fr.cda.campingcar.model;

/*
 * Soutenance Scraping
 * 2024/nov.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UrlParam
{
    private final int site_id;
    private final int position;
    private final String group;
    private final String type;
    private final String criteria;
    private final String key;
    private final String defaultValue;
    private String value;
    private String format;

    public UrlParam(int site_id, int position, String group, String type, String criteria, String key, String defaultValue, String format)
    {
        //System.out.println(Config.CYAN + "URLParam: " + site_id + " " + group + " " + reference + " " + paramKey + " " + format + Config.RESET);
        this.site_id      = site_id;
        this.position     = position;
        this.group       = group;
        this.type         = type;
        this.criteria      = criteria;
        this.key          = key;
        this.defaultValue = defaultValue;
        this.format       = format;
    }

    public UrlParam(int site_id, int position, String group, String type, String criteria, String key, String value)
    {
        //System.out.println(Config.CYAN + "URLParam: " + site_id + " " + group + " " + reference + " " + paramKey + " " + format + Config.RESET);
        this.site_id      = site_id;
        this.position     = position;
        this.group       = group;
        this.type         = type;
        this.criteria      = criteria;
        this.key          = key;
        this.defaultValue = value;
    }

    public int getPosition()
    {
        return this.position;
    }

    public String getGroup()
    {
        return this.group;
    }

    public String getType()
    {
        return this.type;
    }

    public String getCriteria()
    {
        return this.criteria;
    }

    public <T> void setValue(T value)
    {
        if ( value instanceof City ) {
            this.setLocalisationParam((City) value);
        } else if ( value instanceof LocalDate ) {
            this.setFormateDate((LocalDate) value);
        } else if ( value instanceof String ) {
            this.value = (String) value;
        }
    }

    public String getKeyValueParam()
    {
        String value = (this.value == null) ? this.defaultValue : this.value;
        return this.key + "=" + value;
    }

    private void setLocalisationParam(City city)
    {
        switch (this.type) {
            case "latitude":
                this.value = city.getLat();
                break;
            case "longitude":
                this.value = city.getLng();
                break;
            case "code_pays":
            case "pays":
            case "rayon":
                this.value = this.defaultValue;
                break;

            default:
                String val = city.getName();
                if ( format != null ) {
                    val = this.encodeUTF8(city.getFormated(format));
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
        String result;
        result = URLEncoder.encode(item, StandardCharsets.UTF_8);

        return result;
    }

    @Override
    public String toString()
    {
        return "UrlParam{" +
               "site_id=" + this.site_id +
               ", position=" + this.position +
               ", group='" + this.group + '\'' +
               ", type='" + this.type + '\'' +
               ", criteria='" + this.criteria + '\'' +
               ", key='" + this.key + '\'' +
               ", value='" + this.value + '\'' +
               ", defaultValue=" + this.defaultValue +
               ", format='" + this.format + '\'' +
               '}';
    }
}
