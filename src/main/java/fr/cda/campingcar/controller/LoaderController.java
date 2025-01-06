package fr.cda.campingcar.controller;

import fr.cda.campingcar.scraping.CounterListenerInt;
import fr.cda.campingcar.scraping.TaskCounter;
import fr.cda.campingcar.settings.Config;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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

    private final Map<String, HBox> counters = new HashMap<>();
    private final Map<String, Map<String, Label>> subCountersLabels = new HashMap<>();
    private final Map<String, Map<String, AtomicInteger>> countersValues = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        TaskCounter.addListener(this);
        this.upDateProgressBar(0);
    }

    private void initMainProgressBloc(Map<String, AtomicInteger> counter)
    {

        this.countersValues.put("main", counter);
        this.counters.put("main", mainCounterBloc);

        this.mainCounterBloc.getStyleClass().clear();
        this.mainCounterBloc.getStyleClass().add("in-progress");

        this.mainCounterEnded.setText(counter.get("ended").toString());
        this.mainCounterTotal.setText(counter.get("total").toString());

        this.progressBar.setProgress(0);
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
        this.countersValues.get("main").get("ended").set(endedCount);
        this.mainCounterEnded.setText(String.valueOf(endedCount));
        this.applyCss("main");
    }

    private void mainCounterUpDateTotal(int totalCount)
    {
        this.countersValues.get("main").get("total").set(totalCount);
        this.mainCounterTotal.setText(String.valueOf(totalCount));
        this.applyCss("main");
    }

    private void addSubCounter(String counterName, Map<String, AtomicInteger> counter)
    {
        if ( !subCountersLabels.containsKey(counterName) ) {
            HBox subCounter = createSubCounter(counterName, counter);
            this.subCounterBloc.getChildren().add(subCounter);
        } else {
            this.subCounterUpDate(counterName, counter);
        }

    }

    private HBox createSubCounter(String title, Map<String, AtomicInteger> counter)
    {
        HBox subCounter = new HBox();
        subCounter.getStyleClass().addAll("sub-counter", Config.INPROGRESS_CSS);


        Label subCounterTitle = new Label(title + ":");
        subCounterTitle.getStyleClass().add("title");

        Label subCounterEnded = new Label(counter.get("ended").toString());
        subCounterEnded.getStyleClass().addAll("number", "ended");

        Label subCounterSeparator = new Label("/");
        subCounterSeparator.getStyleClass().add("separator");

        Label subCounterTotal = new Label(counter.get("total").toString());
        subCounterTotal.getStyleClass().addAll("number", "total");

        String state = (counter.get("total").get() > 1 )? "Annonces en cours" : "Annonce en cours";
        Label subCounterSuffix = new Label(state);
        subCounterSuffix.getStyleClass().add("state");

        subCounter.getChildren().addAll(subCounterTitle, subCounterEnded, subCounterSeparator, subCounterTotal, subCounterSuffix);

        this.countersValues.put(title, counter);
        this.subCountersLabels.put(title, new HashMap<>(Map.of("ended", subCounterEnded,
                                                               "total", subCounterTotal)));
        this.counters.put(title, subCounter);

        return subCounter;
    }

    private void subCounterUpDate(String counterName, Map<String, AtomicInteger> counter)
    {
        this.countersValues.get(counterName).get("ended").set(counter.get("ended").get());
        this.countersValues.get(counterName).get("total").set(counter.get("total").get());

        this.subCounterUpDateEnded(counterName, counter.get("ended").get());
        this.subCounterUpDateTotal(counterName, counter.get("total").get());

        this.applyCss(counterName);
    }

    private void subCounterUpDateEnded(String counterName, int count)
    {
        Map counter = this.subCountersLabels.get(counterName);
        if ( counter != null ) {
            this.countersValues.get(counterName).get("ended").set(count);
            Label endedLabel = (Label) counter.get("ended");
            endedLabel.setText(String.valueOf(count));
            this.applyCss(counterName);
            this.upStateLabel(counterName);
        }
    }

    private void subCounterUpDateTotal(String counterName, int count)
    {
        Map counter = this.subCountersLabels.get(counterName);
        if ( counter != null ) {
            this.countersValues.get(counterName).get("total").set(count);
            Label totalLabel = (Label) counter.get("total");
            totalLabel.setText(String.valueOf(count));
            this.applyCss(counterName);
        }
    }


    private void upDateProgressBar(double value)
    {
        this.progressBar.setProgress(value);
    }

    private void clearCounter()
    {
        this.subCounterBloc.getChildren().clear();
        this.counters.clear();
        this.subCountersLabels.clear();
        this.countersValues.clear();
    }

    private void upStateLabel(String counterName)
    {
        int total = this.countersValues.get(counterName).get("total").get();
        HBox counter = this.counters.get(counterName);
        if ( this.isCompleted(counterName) ) {

            for ( Node child : counter.getChildren() ) {

                if ( child.getStyleClass().contains(Config.STATE_CSS) ) {
                    Label stateLabel = (Label) child;

                    String state = (total > 1) ? "Annonces trouvée" : "Annonce trouvée";
                    stateLabel.setText(state);

                }
            }
        }
    }

    private void applyCss(String counterName)
    {
        HBox counter = this.counters.get(counterName);

        if ( this.isCompleted(counterName) ) {
            counter.getStyleClass().remove(Config.INPROGRESS_CSS);
            counter.getStyleClass().add(Config.COMPLETED_CSS);
        } else {
            counter.getStyleClass().remove(Config.COMPLETED_CSS);
            counter.getStyleClass().add(Config.INPROGRESS_CSS);
        }
    }

    private boolean isCompleted(String counterName)
    {
        int ended = this.countersValues.get(counterName).get("ended").get();
        int total = this.countersValues.get(counterName).get("total").get();
        return ended == total;
    }

    public void hide() {
        this.loaderPane.setVisible(false);
    }

    @Override
    public void onMainCounter(Map<String, AtomicInteger> counter)
    {
        Platform.runLater(() -> {
            this.clearCounter();
            this.initMainProgressBloc(counter);
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
