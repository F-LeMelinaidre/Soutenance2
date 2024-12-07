/*
 * Soutenance Scraping
 * 11/2024
 *
 * Le Mélinaidre Frédéric
 * Formation CDA
 * Greta Vannes
 */
package fr.cda.campingcar.api;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.cda.campingcar.model.Ville;
import fr.cda.campingcar.model.Departement;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * GeoAPI cette classe permet d'interagir avec l'API (https://api-adresse.data.gouv.fr) pour rechercher des villes en
 * fonction de leur nom.
 * <p>
 * Utilise les dépendances :
 * </p>
 * <ul>
 *     <li>Le client HTTP de Java pour envoyer les requêtes GET.</li>
 *     <li>Jackson Databind pour parser la réponse JSON.</li>
 * </ul>
 */
public class GeoAPI {
    // TODO modifier l'url si la class est étendu à d'autre recherche que par ville
    private static final String _api_url = "https://geo.api.gouv.fr/communes?nom=%s&fields=departement,centre,region&limit=5";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper jsonMapper = new ObjectMapper();

    public List<Ville> rechercheCommune(String villeRecherche) throws IOException, InterruptedException {
        List<Ville> villes = new ArrayList<>();

        String url = String.format(_api_url, villeRecherche);
        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(URI.create(url))
                                         .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JsonNode responseNode = jsonMapper.readTree(response.body());

            for (JsonNode node : responseNode) {

                int codeCommune = node.path("code").asInt();
                String nomCommune = node.path("nom").asText();
                JsonNode coordinate = node.path("centre").path("coordinates");
                double lat = coordinate.get(1).asDouble();
                double lng = coordinate.get(0).asDouble();

                int codePostal = node.path("codePostal").asInt();

                int codeDepartement = node.path("departement").path("code").asInt();
                String nomDepartement = node.path("departement").path("nom").asText();
                String nomRegion = node.path("region").path("nom").asText();

                Departement departement = new Departement(codeDepartement, nomDepartement, nomRegion);
                Ville commune = new Ville(codeCommune, nomCommune, lat, lng, codePostal, departement);
                villes.add(commune);
            }

        } else {
            // TODO Log4J
        }
        return villes;
    }
}
