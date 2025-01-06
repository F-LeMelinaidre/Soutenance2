package fr.cda.campingcar.controller;

import fr.cda.campingcar.model.Rent;
import fr.cda.campingcar.scraping.ScrapingModel;
import fr.cda.campingcar.scraping.TaskCounter;
import fr.cda.campingcar.settings.Config;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
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

public class ResultController implements ControllerRegistry, Initializable
{
    @FXML
    private AnchorPane resultAnchor;
    @FXML
    public ScrollPane resultScrollPane;
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

    public void load(List<ScrapingModel<Object>> results)
    {
        Task<Void> loadResultsTask = new Task<Void>()
        {
            @Override
            protected Void call() throws Exception
            {

                for ( Object annonce : results ) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(Config.FXML_ROOT_PATH + "component/annonceCard.fxml"));

                        try {
                            Parent view = loader.load();
                            RentCardController controller = loader.getController();
                            controller.setData((Rent) annonce);

                            counterTask.incrementMainCounterEnded();
                            counterTask.incrementGlobalCounterEnded();


                            Platform.runLater(() -> {
                                flowPane.getChildren().add(view);
                            });

                        } catch ( IOException e ) {
                            System.out.println("erreur recherche");

                            counterTask.decrementMainCounterTotal();
                            counterTask.decrementGlobalCounterTotal();
                            throw new RuntimeException(e);
                        }

                }

                return null;
            }
        };
        loadResultsTask.setOnScheduled(e -> {
            counterTask.setMainCounterTotal(results.size());
            counterTask.setProgressCounter(results.size());
        });

        loadResultsTask.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                homeController.showResult(this.resultAnchor);
            });

        });

        Thread scrapingThread = new Thread(loadResultsTask);
        scrapingThread.setDaemon(true); // Définit le thread comme un daemon
        scrapingThread.start();
    }

    public void clear() {
        this.flowPane.getChildren().clear();
    }
}
