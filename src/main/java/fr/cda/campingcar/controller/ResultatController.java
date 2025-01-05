package fr.cda.campingcar.controller;

import fr.cda.campingcar.model.Location;
import fr.cda.campingcar.scraping.ScrapingModelInt;
import fr.cda.campingcar.scraping.TaskCounter;
import fr.cda.campingcar.settings.Config;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/*
 * Soutenance Scraping
 * 2024/déc.
 *
 * Le Frédéric Le Mélianidre
 * Formation CDA
 * Greta Vannes
 */

public class ResultatController implements ControllerRegistry, Initializable
{
    @FXML
    private FlowPane flowPane;

    private TaskCounter counterTask;
    private HomeController homeController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        this.counterTask = TaskCounter.getInstance();
    }

    @Override
    public void setMainController(Object parentController)
    {
        this.homeController = (HomeController) parentController;
    }

    public void loadAndShow(List<ScrapingModelInt<Object>> resultats, Runnable onComplete)
    {
        Task<Void> loadResultsTask = new Task<Void>()
        {
            @Override
            protected Void call() throws Exception
            {
                Parent view = null;

                for ( Object annonce : resultats ) {

                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource(Config.FXML_ROOT_PATH + "component/annonceCard.fxml"));
                            view = loader.load();
                            AnnonceCardController controller = loader.getController();
                            controller.setData((Location) annonce);

                            counterTask.incrementMainCounterEnded();
                            counterTask.incrementGlobalCounterEnded();

                        } catch ( IOException e ) {

                            counterTask.decrementMainCounterTotal();
                            counterTask.decrementGlobalCounterTotal();
                            throw new RuntimeException(e);
                        }

                    Parent finalView = view;
                    Platform.runLater(() -> {
                        flowPane.getChildren().add(finalView);
                    });

                }

                return null;
            }
        };
        loadResultsTask.setOnScheduled(e -> {
            counterTask.setMainCounterTotal(resultats.size());
            counterTask.setProgressCounter(resultats.size());
        });

        loadResultsTask.setOnSucceeded(e -> {
            Platform.runLater(onComplete);
            Scene scene = this.flowPane.getScene();
            Node loader = scene.lookup("#loaderPane");
            try {
                Thread.sleep(500);
                loader.setVisible(false);
            } catch ( InterruptedException ex ) {
                Thread.currentThread().interrupt();
            }
        });

        Thread scrapingThread = new Thread(loadResultsTask);
        scrapingThread.setDaemon(true); // Définit le thread comme un daemon
        scrapingThread.start();
    }

    public void clear() {
        this.flowPane.getChildren().clear();
    }
}
