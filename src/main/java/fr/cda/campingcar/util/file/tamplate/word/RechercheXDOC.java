package fr.cda.campingcar.util.file.tamplate.word;

/*
 * Soutenance Scraping
 * 2025/janv.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */

import fr.cda.campingcar.model.Search;
import fr.cda.campingcar.scraping.ScrapingModel;
import fr.cda.campingcar.util.file.WordFile;
import javafx.concurrent.Task;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.util.Random;

import static fr.cda.campingcar.util.DebugHelper.debug;

public class RechercheXDOC extends WordFile
{
    private final Search<ScrapingModel<Object>> recherche;

    public RechercheXDOC(Search<ScrapingModel<Object>> recherche, String filePath)
    {
        super(filePath);

        this.recherche = recherche;
        this.newDocument();
    }

    private void header()
    {
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();

        run.setText("Recherche du " + this.recherche);
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
