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
import fr.cda.campingcar.controller.HomeController;
import fr.cda.campingcar.settings.Config;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class FXMLRender {

    protected static final Logger LOGGER = LoggerConfig.getLogger();

    private Object parentController;
    private Pane targetContainer;
    private String fxmlPath;
    private String viewId;

    private String newWindowTitle;
    private int newWindowWidth;
    private int newWindowHeight;
    private boolean newWindow = false;


    /**
     *
     * @param targetContainer Conteneur qui recevera la vue
     * @param parentController Controller appelant ou autre controller que le controller de vue aurait besoin pour toutes intercations.
     */
    public FXMLRender(Pane targetContainer, Object parentController) {
        this.parentController = parentController;
        this.targetContainer = targetContainer;
    }

    /**
     * Méthode pour charger ou remplacer le FXML.
     * Si {@code replaceId} est null on charge la vue dans le conteneur principal {@code targetContainer} défini à l'instaniation de {@link FXMLRender}, <br>
     * Sinon  on remplace la vue {@code replaceId} par la nouvelle vue {@code fxmlPath}.
     * @param fxmlPath
     * @param replaceId
     * @return le controller du FXML chargé
     * @param <T>
     */
    public <T> T loadFXML(String fxmlPath, String replaceId) {
        T controller = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Config.FXML_ROOT_PATH + fxmlPath));
            Parent view = loader.load();
            controller = loader.getController();

            // Si le controller du FXML implémente ControllerRegistry, on injecte le HomeController
            if (controller instanceof ControllerRegistry) {

                ((ControllerRegistry) controller).setMainController(this.parentController);
            }

            // Remplacement ou ajout de la vue
            if (replaceId != null) {
                Parent existingView = findViewById(replaceId);
                if (existingView != null) {
                    targetContainer.getChildren().set(targetContainer.getChildren().indexOf(existingView), view);
                } else {
                    targetContainer.getChildren().add(view);
                }
            } else {
                targetContainer.getChildren().add(view);
            }

        } catch (IOException e) {
            LOGGER.error("FXML RENDER loadFXML ERROR: " + e.getMessage(), e);
        }
        return controller;
    }

    /**
     * Méthode pour trouver un FXML par son ID
     * @param viewId
     * @return
     */
    private Parent findViewById(String viewId) {
        for (Node node : targetContainer.getChildren()) {
            if (node instanceof Parent && node.getId().equals(viewId)) {
                return (Parent) node;
            }
        }
        return null;
    }

    /**
     * Méthode pour ouvrir une nouvelle fenêtre avec un FXML
     * @param fxmlPath
     * @param title
     * @param width
     * @param height
     */
    public void openNewWindow(String fxmlPath, String title, int width, int height) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Config.FXML_ROOT_PATH + fxmlPath));
            Parent root = loader.load();
            root.getStyleClass().add("window");
            addTopBar(root, title);

            Stage stage = new Stage();
            Scene scene = new Scene(root, width, height);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            String errorMessage = "FXML RENDER openNewWindow ERROR: New Window " + title + "'.";
            LOGGER.error(errorMessage + e.getMessage(), e);
            System.err.println(errorMessage + " | Error : " + e.getMessage());
        }
    }

    /**
     * Cree une topBar Sur les nouvelles fenêtres
     * @param root
     * @param title
     */
    private void addTopBar(Parent root, String title) {
        if (!(root instanceof Pane pane)) {
            String errorMessage = "FXML RENDER addTopBar ERROR: New Window '" + title + "' it's not a Pane.";
            LOGGER.error(errorMessage);
            System.err.println(errorMessage);
            return;
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

        buttonClose.setOnAction(event -> ((Stage) root.getScene().getWindow()).close());

        AnchorPane.setRightAnchor(buttonClose, 0.0);
        AnchorPane.setTopAnchor(buttonClose, 0.0);

        topBar.getChildren().addAll(titleLabel, buttonClose);

        pane.getChildren().add(0, topBar);

        this.windowDragging(topBar, root);
    }

    /**
     * Gère le deplacement de la fenêtre
     * @param topBar
     * @param root
     */
    private void windowDragging(Node topBar, Parent root) {
        final double[] delta = new double[2]; // Tableau pour stocker les coordonnées de décalage

        topBar.setOnMousePressed(event -> {
            delta[0] = event.getScreenX() - ((Stage) root.getScene().getWindow()).getX();
            delta[1] = event.getScreenY() - ((Stage) root.getScene().getWindow()).getY();
        });

        topBar.setOnMouseDragged(event -> {
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setX(event.getScreenX() - delta[0]);
            stage.setY(event.getScreenY() - delta[1]);
        });
    }
}
