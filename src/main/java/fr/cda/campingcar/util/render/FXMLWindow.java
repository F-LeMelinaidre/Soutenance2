package fr.cda.campingcar.util.render;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/*
 * Soutenance Scraping
 * 2025/janv.
 *
 * Le Frédéric Le Mélinaidre
 * Formation CDA
 * Greta Vannes
 */
public abstract class FXMLWindow implements Initializable
{
    private Button closeBtnTopBar = new Button();
    private double[] delta = new double[2];// Tableau pour stocker les coordonnées de décalage
    private Parent root;
    private final Label titleTopBar = new Label();
    private final AnchorPane topBar = new AnchorPane();

    protected Pane mainPane;
    protected Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        this.initTopBar();
    }

    public void initialize(Stage stage, Parent root, String title)
    {
        this.root = root;
        this.stage = stage;
        this.titleTopBar.setText(title);
        this.mainPane.getChildren().addFirst(this.topBar);
    }


    private void initTopBar()
    {
        this.topBar.getStyleClass().add("top-bar");
        this.topBar.setPrefHeight(26);
        this.topBar.setMinHeight(26);

        this.initTitle();
        this.initCloseBtn();
        this.windowDragging();

    }

    private void initTitle()
    {
        this.titleTopBar.getStyleClass().add("title-label");
        this.titleTopBar.setAlignment(Pos.CENTER_LEFT);
        this.titleTopBar.setContentDisplay(ContentDisplay.CENTER);

        AnchorPane.setTopAnchor(this.titleTopBar, 0.0);
        AnchorPane.setBottomAnchor(this.titleTopBar, 0.0);
        AnchorPane.setLeftAnchor(this.titleTopBar, 8.0);
        AnchorPane.setRightAnchor(this.titleTopBar, 26.0);

        this.topBar.getChildren().add(this.titleTopBar);

    }

    private void initCloseBtn()
    {

        this.closeBtnTopBar = new Button("X");
        this.closeBtnTopBar.getStyleClass().add("btn-close");
        this.closeBtnTopBar.setPrefHeight(25);
        this.closeBtnTopBar.setMinHeight(25);
        this.closeBtnTopBar.setPrefWidth(25);

        AnchorPane.setRightAnchor(this.closeBtnTopBar, 0.0);
        AnchorPane.setTopAnchor(this.closeBtnTopBar, 0.0);

        this.closeBtnTopBar.setOnAction(event -> {
            this.stage.close();
        });

        this.topBar.getChildren().add(this.closeBtnTopBar);
    }

    @FXML
    public void closeWindow()
    {
        this.stage.close();
    }

    /**
     * Gère le deplacement de la fenêtre
     */
    private void windowDragging()
    {

        topBar.setOnMousePressed(event -> {
            this.delta[0] = event.getScreenX() - this.root.getScene().getWindow().getX();
            this.delta[1] = event.getScreenY() - this.root.getScene().getWindow().getY();
        });

        topBar.setOnMouseDragged(event -> {
            this.stage.setX(event.getScreenX() - this.delta[0]);
            this.stage.setY(event.getScreenY() - this.delta[1]);
        });
    }

    public void refreshWindow()
    {
        this.stage.sizeToScene();
        this.stage.centerOnScreen();
    }
}
