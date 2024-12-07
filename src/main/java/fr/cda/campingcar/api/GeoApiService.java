/*
 * Soutenance Scraping
 * 11/2024
 *
 * Le Mélinaidre Frédéric
 * Formation CDA
 * Greta Vannes
 */

package fr.cda.campingcar.api;

import fr.cda.campingcar.model.Ville;
import javafx.concurrent.Task;

import java.util.List;

public class GeoApiService
{
    private final GeoAPI geoAPI;

    public GeoApiService(GeoAPI geoAPI) {
        this.geoAPI = geoAPI;
    }

    public Task<List<Ville>> rechercherCommune(String communeRecherche)
    {
        return new Task<List<Ville>>() {
            @Override
            protected List<Ville> call() throws Exception {
                return geoAPI.rechercheCommune(communeRecherche);
            }
        };
    }
}
