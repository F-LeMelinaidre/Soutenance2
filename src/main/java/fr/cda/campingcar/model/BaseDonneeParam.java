package fr.cda.campingcar.model;

/*
 * Soutenance Scraping
 * 2024/déc.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */

import java.io.Serializable;

public class BaseDonneeParam implements Serializable
{
    private String serveur;
    private String baseDonnee;
    private Integer port;
    private String login;
    private String password;

    public BaseDonneeParam(){}

    public BaseDonneeParam(String serveur, String baseDonnee, Integer port, String login, String password)
    {
        this.serveur    = serveur;
        this.baseDonnee = baseDonnee;
        this.port       = port;
        this.login      = login;
        this.password   = password;
    }

    public String getServeur()
    {
        return this.serveur;
    }

    public void setServeur(String serveur)
    {
        this.serveur = serveur;
    }

    public String getBaseDonnee()
    {
        return this.baseDonnee;
    }

    public void setBaseDonnee(String baseDonnee)
    {
        this.baseDonnee = baseDonnee;
    }

    public Integer getPort()
    {
        return this.port;
    }

    public void setPort(Integer port)
    {
        this.port = port;
    }

    public String getLogin()
    {
        return this.login;
    }

    public void setLogin(String login)
    {
        this.login = login;
    }

    public String getPassword()
    {
        return this.password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    @Override
    public String toString()
    {
        return "ParametreDB{" +
               "serveur='" + serveur + '\'' +
               ", baseDonnee='" + baseDonnee + '\'' +
               ", port=" + port +
               ", login='" + login + '\'' +
               ", password='" + password + '\'' +
               '}';
    }
}
