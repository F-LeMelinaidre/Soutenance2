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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class FXMLRender {

    private Object parentController;
    private Pane targetContainer;
    private String fxmlPath;
    private String viewId;

    private String newWindowTitle;
    private int newWindowWidth;
    private int newWindowHeight;
    private boolean newWindow = false;


    public FXMLRender(Pane targetContainer, Object parentController) {
        this.parentController = parentController;
        this.targetContainer = targetContainer;
    }

    /**
     * Méthode pour charger ou remplacer le FXML.
     * Si replaceId est null on charge ou remplace la vue dans le conteneur principal, <br>
     * Sin non null remplace la vue replaceId par la nouvelle vue fxmlPath
     * @param fxmlPath
     * @param replaceId
     * @return le controller du FXML chargé
     * @param <T>
     */
    public <T> T loadFXML(String fxmlPath, String replaceId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Config.FXML_ROOT_PATH + fxmlPath));
            Parent view = loader.load();
            T controller = loader.getController();

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

            return controller;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
            Scene scene = new Scene(loader.load(), width, height);
            Stage stage = new Stage();
            //stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addTopBar() {

    }
}
