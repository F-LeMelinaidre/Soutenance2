package fr.cda.campingcar.util.file.tamplate.word;

/*
 * Soutenance Scraping
 * 2025/janv.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */

import fr.cda.campingcar.model.Recherche;
import fr.cda.campingcar.scraping.ScrapingModelInt;
import javafx.concurrent.Task;

import java.util.Random;

import static fr.cda.campingcar.util.DebugHelper.debug;

public class RechercheXDOC
{

    public RechercheXDOC(Recherche<ScrapingModelInt<Object>> recherche, String filePath)
    {



    }

    public Task<Void> save() {
        return new Task<>() {
            @Override
            protected Void call() throws Exception
            {
                pauseExecution(new Random().nextInt(5000) + 2000);
                return null;
            }
        };
    }

    private void pauseExecution(int time)
    {
        try {
            Thread.sleep(time);
        } catch ( InterruptedException e ) {
            debug("ScrapingManager", "pauseExecution", "InterruptedException", e.getMessage(), false);

            Thread.currentThread().interrupt();
        }
    }
}
