package fr.cda.campingcar.util.render;

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
import fr.cda.campingcar.util.LoggerConfig;
import javafx.fxml.FXMLLoader;
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
    private static final Map<String, Stage> _openWindows = new HashMap<>();
    private static Button _buttonClose;

    private final Object parentController;
    private Pane targetContainer;
    private String fxmlPath;
    private String viewId;


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

        Stage existingStage = _openWindows.get(fxmlPath);
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

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setResizable(true);
            stage.centerOnScreen();
            stage.setAlwaysOnTop(true);

            // Stock le contrôleur pour le restituer ultérieurement
            stage.setUserData(loader.getController());

            if (loader.getController() instanceof FXMLWindow) {
                ((FXMLWindow) loader.getController()).initialize(stage, root, title);
            }


            controller = loader.getController();

            stage.show();

        } catch ( IOException e ) {

            String errorMessage = "FXML RENDER openNewWindow ERROR: New Window " + title + "'.";
            LOGGER.error("{} {}", errorMessage, e.getMessage(), e);
        }

        return controller;
    }


    public void hideCloseButton()
    {
        _buttonClose.setVisible(false);
    }
}
