package fr.cda.campingcar.util;

/*
 * Soutenance Scraping
 * 2024/nov.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */

import fr.cda.campingcar.controller.ControllerRegistry;
import fr.cda.campingcar.settings.Config;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FXMLRender
{

    protected static final Logger LOGGER = LoggerConfig.getLogger();

    private final Object parentController;
    private Pane targetContainer;
    private String fxmlPath;
    private String viewId;
    private static final Map<String, Stage> openWindows = new HashMap<>();


    /**
     * @param targetContainer  Conteneur qui recevera la vue
     * @param parentController Controller appelant ou autre controller que le controller de vue aurait besoin pour toutes intercations.
     */
    public FXMLRender(Pane targetContainer, Object parentController)
    {
        this.parentController = parentController;
        this.targetContainer  = targetContainer;
    }

    public void setTargetContainer(Pane targetContainer)
    {
        this.targetContainer = targetContainer;
    }

    /**
     * Méthode pour charger un FXML.
     *
     * @param fxmlPath chemin de la vue
     * @param <T>
     * @return le controller du FXML chargé
     */
    private <T> Pair<T, Parent> loadFXMLInternal(String fxmlPath) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(Config.FXML_ROOT_PATH + fxmlPath));
        Parent view = loader.load();
        T controller = loader.getController();

        // Si le controller du FXML implémente ControllerRegistry, on injecte le HomeController
        if (controller instanceof ControllerRegistry) {
            ((ControllerRegistry) controller).setMainController(this.parentController);
        }

        return new Pair<>(controller, view);
    }

    /**
     * Chargement classic d'une vue, dans un conteur autre que {@code targetConteneur} si {@code target} n'est pas null.
     *
     * @return le controller du FXML chargé
     */
    public <T> T loadFXML(String fxmlPath, Pane target) {
        T controller = null;
        target = (target != null) ? target : this.targetContainer;
        try {
            Pair<T, Parent> result = loadFXMLInternal(fxmlPath);
            controller = result.getKey();
            Parent view = result.getValue();

            // Ajoutez simplement la vue au conteneur
            target.getChildren().add(view);
        } catch (IOException e) {
            LOGGER.error("FXML RENDER loadFXML ERROR: {}", e.getMessage(), e);
        }
        return controller;
    }

    /**
     * Charge la vue à une position voulu. Uniquement utilisable avec un targetConteneur VBox ou HBox<br>
     * Si la position n'existe pas la vue est chargé à la suite.<br>
     * Le conteneur de destination {@code targetContainer} est défini à l'instaniation de {@link FXMLRender}.
     *
     * @param position Position de chargement de la vue
     * @return le controller du FXML chargé
     */
    public <T> T loadAtPosition(String fxmlPath, int position) {
        return this.loadAtPosition(fxmlPath, position, this.targetContainer);
    }

    /**
     * Surcharge de methode pour charger dans un conteneur specifique
     */
    public <T> T loadAtPosition(String fxmlPath, int position, Pane target) {
        T controller = null;
        try {
            Pair<T, Parent> result = loadFXMLInternal(fxmlPath);
            controller = result.getKey();
            Parent view = result.getValue();

            if ( target instanceof VBox || target instanceof HBox || target instanceof StackPane ) {
                position = position - 1;
                int size = target.getChildren().size();

                if (position >= 0 && position <= size) {
                    target.getChildren().add(position, view);
                } else {
                    target.getChildren().add(view);
                    LOGGER.warn("Position out of bounds: {}. Adding to the end.", position);
                }
            } else {
                LOGGER.error("targetContainer doit être un VBox, HBox ou StackPane. Type actuel: {}", target.getClass().getSimpleName());
            }
        } catch (IOException e) {
            LOGGER.error("FXML RENDER loadAtPosition ERROR: {}", e.getMessage(), e);
        }
        return controller;
    }

    /**
     * Méthode pour charger et remplacer le FXML.
     * Remplace la vue {@code replaceId} par la nouvelle vue {@code fxmlPath}<br>
     * Le conteneur de destination {@code targetContainer} est défini à l'instaniation de {@link FXMLRender}.<br>
     * Si {@code replaceId} n'existe pas la vue est ajouter dans le {@code targetContainer} sans rien remplacer.
     *
     * @param replaceId
     * @return le controller du FXML chargé
     */
    public <T> T loadAndReplace(String fxmlPath, String replaceId) {
        return this.loadAndReplace(fxmlPath, replaceId, this.targetContainer);
    }

    /**
     * Surcharge de methode pour charger dans un conteneur specifique
     */
    public <T> T loadAndReplace(String fxmlPath, String replaceId, Pane target)
    {
        T controller = null;
        try {
            Pair<T, Parent> result = loadFXMLInternal(fxmlPath);
            controller = result.getKey();
            Parent view = result.getValue();

            Parent existingView = findViewById(replaceId, target);
            if (existingView != null) {
                target.getChildren().set(target.getChildren().indexOf(existingView), view);
            } else {
                target.getChildren().add(view);
            }
        } catch (IOException e) {
            LOGGER.error("FXML RENDER loadAndReplace ERROR: {}", e.getMessage(), e);
        }
        return controller;
    }

    /**
     * Méthode pour trouver un FXML par son ID
     *
     * @param viewId
     * @return
     */
    private Parent findViewById(String viewId, Pane target)
    {
        for ( Node node : target.getChildren() ) {
            if ( node instanceof Parent && node.getId() != null && node.getId().equals(viewId) ) {
                return (Parent) node;
            }
        }
        return null;
    }

    /**
     * Méthode pour ouvrir une nouvelle fenêtre ou une fenêtre déjà existante
     * Si la fenêtre est existante dans le MAP {@code openWindows}, celle-ci est mise en premier plan.<br>
     * Si elle n'existe pas dans {@code openWindows}, on l'a créé {@link #createNewWindow}. <br>
     * Si elle n'existe plus mais presente dans {@code openWindows}, on l'a reitre du MAP
     *
     * @param fxmlPath Nom du fxml avec extention
     * @param title    Titre de la fenêtre
     * @return Le controller de la vue
     */
    public static <T> T openNewWindow(String fxmlPath, String title)
    {
        T controller = null;

        Stage existingStage = openWindows.get(fxmlPath);
        if ( existingStage != null ) {
            existingStage.toFront();
            controller = (T) existingStage.getUserData();
        } else {
            controller = createNewWindow(fxmlPath, title);
        }

        return controller;
    }

    /**
     * Méthode qui créé la nouvelle fenêtre
     *
     * @param fxmlPath Nom du fxml avec extention
     * @param title    Titre de la fenêtre
     * @param <T>      Class Controller
     * @return Controller de la vue
     */
    private static <T> T createNewWindow(String fxmlPath, String title)
    {
        T controller = null;
        try {
            FXMLLoader loader = new FXMLLoader(FXMLRender.class.getResource(Config.FXML_ROOT_PATH + fxmlPath));
            Parent root = loader.load();
            root.getStyleClass().add("window");

            addTopBar(root, title, fxmlPath);

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));

            // Stock le contrôleur pour le restituer ultérieurement
            stage.setUserData(loader.getController());

            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle(title);
            stage.setResizable(true);
            stage.setAlwaysOnTop(true);

            stage.show();

            // Stocker la référence du Stage dans la Map
            openWindows.put(fxmlPath, stage);

            // Retirer la fenêtre de la Map lorsque celle-ci se ferme
            stage.setOnCloseRequest(event -> openWindows.remove(fxmlPath));

            controller = loader.getController();

        } catch ( IOException e ) {

            String errorMessage = "FXML RENDER openNewWindow ERROR: New Window " + title + "'.";
            LOGGER.error("{} {}", errorMessage, e.getMessage(), e);
        }

        return controller;
    }

    /**
     * Ferme la fenêtre par son nom de fichier fxml, stocké dans {@code openWindows}
     *
     * @param fxmlPath fichier fxml avec extention
     */
    public static void closeWindow(String fxmlPath)
    {
        Stage stage = openWindows.get(fxmlPath);
        if ( stage != null ) {
            openWindows.remove(fxmlPath);
            stage.close();
        }
    }

    public void addProgressIndicator()
    {
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setPrefSize(50, 50);
        this.targetContainer.getChildren().add(progressIndicator);
    }

    /**
     * Cree une topBar Sur les nouvelles fenêtres
     *
     * @param root
     * @param title
     * @return
     */
    private static Pane addTopBar(Parent root, String title, String fxmlPath)
    {
        if ( !(root instanceof Pane pane) ) {
            String errorMessage = "FXML RENDER addTopBar ERROR: New Window '" + title + "' it's not a Pane.";
            LOGGER.error(errorMessage);
            System.err.println(errorMessage);
            return null;
        }

        AnchorPane topBar = new AnchorPane();
        topBar.getStyleClass().add("top-bar");
        topBar.setPrefHeight(26);
        topBar.setMinHeight(26);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("title-label");
        titleLabel.setAlignment(Pos.CENTER_LEFT);
        titleLabel.setContentDisplay(ContentDisplay.CENTER);

        AnchorPane.setTopAnchor(titleLabel, 0.0);
        AnchorPane.setBottomAnchor(titleLabel, 0.0);
        AnchorPane.setLeftAnchor(titleLabel, 8.0);
        AnchorPane.setRightAnchor(titleLabel, 26.0);

        Button buttonClose = new Button("X");
        buttonClose.getStyleClass().add("btn-close");
        buttonClose.setPrefHeight(25);
        buttonClose.setMinHeight(25);
        buttonClose.setPrefWidth(25);

        buttonClose.setOnAction(event -> {
            openWindows.remove(fxmlPath);
            Stage stage = (Stage) root.getScene().getWindow();
            stage.close();
        });

        AnchorPane.setRightAnchor(buttonClose, 0.0);
        AnchorPane.setTopAnchor(buttonClose, 0.0);

        topBar.getChildren().addAll(titleLabel, buttonClose);

        pane.getChildren().add(0, topBar);

        windowDragging(topBar, root);

        return topBar;
    }

    /**
     * Gère le deplacement de la fenêtre
     *
     * @param topBar
     * @param root
     */
    private static void windowDragging(Node topBar, Parent root)
    {
        final double[] delta = new double[2]; // Tableau pour stocker les coordonnées de décalage

        topBar.setOnMousePressed(event -> {
            delta[0] = event.getScreenX() - root.getScene().getWindow().getX();
            delta[1] = event.getScreenY() - root.getScene().getWindow().getY();
        });

        topBar.setOnMouseDragged(event -> {
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setX(event.getScreenX() - delta[0]);
            stage.setY(event.getScreenY() - delta[1]);
        });
    }
}
