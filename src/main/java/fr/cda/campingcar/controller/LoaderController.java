package fr.cda.campingcar.controller;

import fr.cda.campingcar.scraping.CounterListenerInt;
import fr.cda.campingcar.scraping.TaskCounter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static fr.cda.campingcar.util.DebugHelper.debug;

/*
 * Soutenance Scraping
 * 2024/déc.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */
public class LoaderController implements Initializable, CounterListenerInt
{
    // TODO JAVADOC
    @FXML
    private VBox loaderPane;
    @FXML
    private HBox mainCounterBloc;
    @FXML
    private Label mainCounterTitle;
    @FXML
    private Label mainCounterEnded;
    @FXML
    private Label mainCounterSeparator;
    @FXML
    private Label mainCounterTotal;
    @FXML
    private VBox subCounterBloc;
    @FXML
    private ProgressBar progressBar;

    private final Map<String, Map<String, Label>> subCounters = new HashMap<>();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        TaskCounter.addListener(this);
        this.upDateProgressBar(0);
    }

    private void initMainProgressBloc(Map<String, AtomicInteger> counter)
    {
        this.mainCounterEnded.setText(counter.get("ended").toString());
        this.mainCounterTotal.setText(counter.get("total").toString());
        this.loaderPane.setVisible(true);
    }

    public void setTitleMainCounter(String title, String separator)
    {

        title     = "État " + title + " en cours :";
        separator = separator + " sur";

        this.mainCounterTitle.setText(title);
        this.mainCounterSeparator.setText(separator);

    }

    private void mainCounterUpDateEnded(int endedCount)
    {
        this.mainCounterEnded.setText(String.valueOf(endedCount));
    }

    private void mainCounterUpDateTotal(int totalCount)
    {
        this.mainCounterTotal.setText(String.valueOf(totalCount));
    }

    private void addSubCounter(String counterName, Map<String, AtomicInteger> counter)
    {
        if ( !subCounters.containsKey(counterName) ) {
            HBox subCounter = createSubCounter(counterName, counter);
            this.subCounterBloc.getChildren().add(subCounter);
        } else {
            this.subCounterUpDate(counterName, counter);
        }

    }

    private HBox createSubCounter(String title, Map<String, AtomicInteger> counter)
    {
        HBox subCounter = new HBox();
        subCounter.getStyleClass().add("sub-counter");


        Label subCounterTitle = new Label(title + ":");
        subCounterTitle.getStyleClass().add("title");

        Label subCounterEnded = new Label(counter.get("ended").toString());
        subCounterEnded.getStyleClass().addAll("number", "ended");
        Label subCounterSeparator = new Label("/");
        Label subCounterTotal = new Label(counter.get("total").toString());
        subCounterTotal.getStyleClass().addAll("number", "total");

        Label subCounterSuffix = new Label("Annonces terminées");

        subCounter.getChildren().addAll(subCounterTitle, subCounterEnded, subCounterSeparator, subCounterTotal, subCounterSuffix);

        this.subCounters.put(title, new HashMap<>(Map.of("ended", subCounterEnded,
                                                         "total", subCounterTotal)));

        return subCounter;
    }

    private void subCounterUpDate(String counterName, Map<String, AtomicInteger> counter)
    {
        this.subCounterUpDateEnded(counterName, counter.get("ended").get());
        this.subCounterUpDateTotal(counterName, counter.get("total").get());
    }

    private void clearSubCounterBloc()
    {
        this.subCounterBloc.getChildren().clear();
        this.subCounters.clear();
    }

    private void subCounterUpDateEnded(String counterName, int count)
    {
        Label endedLabel = this.subCounters.get(counterName).get("ended");
        if ( endedLabel != null ) {
            endedLabel.setText(String.valueOf(count));
        }
    }

    private void subCounterUpDateTotal(String counterName, int count)
    {
        Label totalLabel = this.subCounters.get(counterName).get("total");
        if ( totalLabel != null ) {
            totalLabel.setText(String.valueOf(count));
        }
    }


    private void upDateProgressBar(double value)
    {
        System.out.println(value);
        this.progressBar.setProgress(value);

    }

    @Override
    public void onMainCounter(Map<String, AtomicInteger> counter)
    {
        Platform.runLater(() -> {
            this.initMainProgressBloc(counter);
            this.clearSubCounterBloc();
        });
    }

    @Override
    public void onMainCounterUpdateEnded(int endedCount)
    {
        Platform.runLater(() -> this.mainCounterUpDateEnded(endedCount));
    }

    @Override
    public void onMainCounterUpdateTotal(int totalCount)
    {
        Platform.runLater(() -> this.mainCounterUpDateTotal(totalCount));
    }

    @Override
    public void onSubProgressBloc(String counterName, Map<String, AtomicInteger> subCounter)
    {
        Platform.runLater(() -> this.addSubCounter(counterName, subCounter));
    }

    @Override
    public void onSubCounter(String counterName, Map<String, AtomicInteger> subCounter)
    {
        Platform.runLater(() -> this.subCounterUpDate(counterName, subCounter));
    }

    @Override
    public void onSubCounterUpdateEnded(String counterName, int endedCount)
    {

        Platform.runLater(() -> this.subCounterUpDateEnded(counterName, endedCount));
    }

    @Override
    public void onSubCounterUpdateTotal(String counterName, int totalCount)
    {
        Platform.runLater(() -> this.subCounterUpDateTotal(counterName, totalCount));
    }

    @Override
    public void onProgressBarUpdated(double value)
    {
        Platform.runLater(() -> this.upDateProgressBar(value));
    }
}
