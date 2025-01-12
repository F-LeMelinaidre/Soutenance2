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

public class DataBaseParameter implements Serializable
{
    private String server;
    private String dataBase;
    private Integer port;
    private String login;
    private String password;

    public DataBaseParameter(){}

    public DataBaseParameter(String server, String dataBase, Integer port, String login, String password)
    {
        this.server     = server;
        this.dataBase = dataBase;
        this.port       = port;
        this.login      = login;
        this.password   = password;
    }

    public String getServer()
    {
        return this.server;
    }

    public void setServer(String server)
    {
        this.server = server;
    }

    public String getJdbcUrlServer() {
        return "jdbc:mysql://" + this.server + ":" + this.port + "/" + this.dataBase;
    }

    public String getDataBase()
    {
        return this.dataBase;
    }

    public void setDataBase(String dataBase)
    {
        this.dataBase = dataBase;
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
               "serveur='" + this.server + '\'' +
               ", baseDonnee='" + this.dataBase + '\'' +
               ", port=" + this.port +
               ", login='" + this.login + '\'' +
               ", password='" + this.password + '\'' +
               '}';
    }
}
