/*
 * Soutenance Scraping
 * 11/2024
 *
 * Le Mélinaidre Frédéric
 * Formation CDA
 * Greta Vannes
 */

package fr.cda.campingcar.controller;

import fr.cda.campingcar.api.GeoAPI;
import fr.cda.campingcar.dao.DAOFactory;
import fr.cda.campingcar.model.*;
import fr.cda.campingcar.api.GeoApiService;
import fr.cda.campingcar.model.Dom;
import fr.cda.campingcar.util.Validator;
import javafx.animation.PauseTransition;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.controlsfx.control.CheckComboBox;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

// TODO
//  MERITE D ÊTRE FACTORISE
//  AMELIORER LA GESTION DU BOUTON RECHERCHER
//  - VERIF FORMAT DE LA DATE - SI LE FORMAT EST BON ATTRIBUER ET SELECTIONNER LA DATE,
//    POUR NE PAS ETRE OBLIGé DE TAPER ENTRER
//  - VERIF CHAMP BUDGET EST UN INT
// TODO
//  - HELPER/HINT MESSAGE

public class SearchController implements Initializable, ControllerRegistry
{

    @FXML
    private VBox searchPane;
    @FXML
    private CheckComboBox<Site> siteComboBox;
    @FXML
    private CheckComboBox<TypeVehicule> typeVehiculeComboBox;
    @FXML
    private ComboBox<Ville> villeDepartField;
    @FXML
    private ComboBox<Ville> villeArriveeField;
    @FXML
    private DatePicker periodeDebutDatePicker;
    @FXML
    private DatePicker periodeFinDatePicker;
    @FXML
    private TextField budgetMin;
    @FXML
    private TextField budgetMax;
    @FXML
    private Button rechercherBouton;
    @FXML
    private Button effacerBouton;
    @FXML
    private Label hintLabel;

    private HomeController homeController = null;
    private final DAOFactory daoFactory;
    private final GeoApiService geoApiService;
    private final LocalDate dateCourante;

    private boolean disable = false;

    public SearchController()
    {
        this.daoFactory    = DAOFactory.getInstance();
        this.geoApiService = new GeoApiService(new GeoAPI());
        this.dateCourante  = LocalDate.now();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        this.loadSiteComboBox();
        this.configureVilleComboBox(this.villeDepartField);
        this.configureVilleComboBox(this.villeArriveeField);
        this.initializeDatePicker();
        this.initializeEventListener();


        this.rechercherBouton.setDisable(true);
    }

    @Override
    public void setMainController(Object homeController)
    {
        this.homeController = (HomeController) homeController;
    }

    @FXML
    private void reseach() throws SQLException
    {

        List<Site> sitesSelected = this.siteComboBox.getCheckModel().getCheckedItems();
        List<TypeVehicule> typeVehiculesSelected = this.typeVehiculeComboBox.getCheckModel().getCheckedItems();
        String budgetMax = this.budgetMax.getText();
        String budgetMin = this.budgetMin.getText();
        Ville villeDep = this.villeDepartField.getSelectionModel().getSelectedItem();
        Ville villeArr = this.villeArriveeField.getSelectionModel().getSelectedItem();
        String date = this.dateCourante.toString();
        LocalDate dateDepart = this.periodeDebutDatePicker.getValue();
        LocalDate dateArrivee = this.periodeFinDatePicker.getValue();

        Map<String, Object> critereRecherche = new HashMap<>()
        {{
            put("Liste Sites", sitesSelected);
            put("Liste Véhicules", typeVehiculesSelected);
            put("Budget Max", budgetMax);
            put("Budget Min", budgetMin);
            put("Ville Départ", villeDep);
            put("Ville Arrivée", villeArr);
            put("Date Départ", dateDepart);
            put("Date Retour", dateArrivee);

            put("Date de Recherche", date);
        }};


        for ( Site site : sitesSelected ) {
            List<TypeVehicule> typeVehiculesFiltred = site.filterVehicule(typeVehiculesSelected);

            Map<String, Dom> domMap = this.daoFactory.getDomDAO().findBySite(site.getId());
            site.setDomMap(domMap);
            site.setUrlRecherche(typeVehiculesFiltred, villeDep, villeArr, dateDepart, dateArrivee, budgetMin, budgetMax);

        }

        Recherche recherche = new Recherche(sitesSelected, critereRecherche, Location::new);

        this.homeController.startScrapping(recherche);

        this.toggleDisableForm();
    }

    @FXML
    private void effacer()
    {

        this.siteComboBox.getCheckModel().clearChecks();
        this.typeVehiculeComboBox.getCheckModel().clearChecks();

        this.villeDepartField.setValue(null);
        Validator.clearClass(this.villeDepartField);
        this.villeArriveeField.setValue(null);
        Validator.clearClass(this.villeArriveeField);

        this.periodeDebutDatePicker.setValue(null);
        this.periodeFinDatePicker.setValue(null);
        this.initializeDatePicker();

        this.budgetMin.clear();
        Validator.clearClass(this.budgetMin);
        this.budgetMax.clear();
        Validator.clearClass(this.budgetMax);

        this.updateRechercherBoutonState();
        this.homeController.clearResultat();
    }

    /**
     * Rafraichi la liste des types de véhicules proposés par les sites sélectionnés.
     */
    @FXML
    private void refreshTypeVehiculeComboBox()
    {

        Set<TypeVehicule> typesVehicules = new HashSet<>();

        // Ajouter les types de véhicules du/des sites selectionnés dans le Set
        for ( Site site : this.siteComboBox.getCheckModel().getCheckedItems() ) {
            typesVehicules.addAll(site.getTypeVehicules().values());
        }

        // Sauvegarde des éléments selectionnés
        List<TypeVehicule> selectedItems = new ArrayList<>(typeVehiculeComboBox.getCheckModel().getCheckedItems());
        typeVehiculeComboBox.getCheckModel().clearChecks();

        // Itérer les éléments actuels du ComboBox et ajuste la liste
        Iterator<TypeVehicule> iterator = typeVehiculeComboBox.getItems().iterator();
        while ( iterator.hasNext() ) {
            TypeVehicule type = iterator.next();
            if ( !typesVehicules.contains(type) ) {
                // Suppression dans la liste du ComboBox si non present dans la nouvelle liste
                iterator.remove();
            } else {
                typesVehicules.remove(type);
                // Suppression dans la nouvelle liste si présent dans le ComboBox
            }
        }

        // Ajoute les types restants dans la nouvelle liste au ComboBox
        typeVehiculeComboBox.getItems().addAll(typesVehicules);
        // Trie par ordre alphabétique après modification
        typeVehiculeComboBox.getItems().sort(Comparator.comparing(TypeVehicule::getType));

        // Restaure la selection
        for ( TypeVehicule type : selectedItems ) {
            typeVehiculeComboBox.getCheckModel().check(type);
        }
    }

    /**
     * Gère l'autocomplétion dans un ComboBox pour la recherche de villes. <br>
     * Si la saisie est supérieure ou égale à 3 caractères, <br>
     * lance une recherche après un délai 300 ms. <br>
     * Met à jour la liste de selection avec les résultats ou la vide si la saisie est trop courte.
     *
     * @param keyEvent L'événement de type KeyEvent déclenché lors de la saisie.
     */
    @FXML
    private void autocompleteVilleField(KeyEvent keyEvent)
    {
        this.validTextField(keyEvent);

        ComboBox<Ville> item = (ComboBox<Ville>) keyEvent.getSource();
        String communeRecherche = item.getEditor().getText();
        int minLength = 3;

        if ( communeRecherche.length() >= minLength ) {

            PauseTransition pause = new PauseTransition(Duration.millis(300));
            pause.setOnFinished(event -> {
                Task<List<Ville>> task = geoApiService.rechercherCommune(communeRecherche);

                task.setOnSucceeded(e -> {
                    List<Ville> communes = task.getValue();
                    item.getItems().clear();
                    item.getItems().setAll(communes);
                    item.show();
                });

                task.setOnFailed(e -> {
                    Throwable exception = task.getException();
                });

                new Thread(task).start();
            });

            pause.play();
        } else {
            item.getItems().clear();
        }
    }

    /**
     * {@link DatePicker} Gère l'événement de sélection de la date de debut.<br>
     * Met à jour la cellule du jour, et permet de verrouiller les dates antérieures à celle-ci,<br>
     * pour le {@link DatePicker} {@code periodeDebutFinPicker}<br>
     * Appel également la méthode de mise à jour d'état du bouton de recherche
     *
     * @param event l'événement {@link ActionEvent}
     */
    // TODO Deplacer l affichage de l agenda en fonction de la selection de date de fin
    @FXML
    private void onStartDateSelected(ActionEvent event)
    {
        DatePicker date = (DatePicker) event.getSource();
        LocalDate departDate = date.getValue();

        if ( date.getValue() != null ) {
            this.periodeFinDatePicker.setDayCellFactory(getDateCellFactory(departDate.plusDays(1), null));
            this.updateRechercherBoutonState();
        }
    }

    /**
     * {@link DatePicker} Gère l'événement de sélection de la date de fin.<br>
     * Met à jour la cellule du jour selectionné, et permet de verrouiller les dates supérieures à celle-ci,<br>
     * pour le {@link DatePicker} {@code periodeDebutDatePicker}<br>
     * Appel également la méthode de mise à jour d'état du bouton de recherche
     *
     * @param event l'événement {@link ActionEvent}
     */
    // TODO Modifier deplacer l affichage de l agenda en fonction de la date depart selectionné
    @FXML
    private void onEndDateSelected(ActionEvent event)
    {
        DatePicker date = (DatePicker) event.getSource();
        LocalDate dateFin = date.getValue();

        if ( date.getValue() != null ) {
            this.periodeDebutDatePicker.setDayCellFactory(
                    getDateCellFactory(this.dateCourante, dateFin.minusDays(1)));
            this.updateRechercherBoutonState();
        }
    }


    @FXML
    private void validTextField(KeyEvent event)
    {
        String fxId = null;
        Node node = (Node) event.getSource();

        Integer rowId;
        String value = null;

        if ( node instanceof ComboBox<?> comboBox ) {
            value = comboBox.getEditor().getText();
        } else if ( node instanceof TextField textField ) {
            value = textField.getText();
        }
        fxId  = node.getId();
        rowId = GridPane.getRowIndex(node);

        boolean isValid;

        switch (fxId) {
            case "villeDepartField":
            case "villeArriveeField":
                isValid = Validator.isNotEmpty(value) && Validator.isValidCityName(value);
                break;
            case "periodeDebutDatePicker":
            case "periodeFinDatePicker":
                break;
            case "budgetMin":
                isValid = Validator.isNotEmpty(value) && Validator.isNumeric(value);
                break;
            case "budgetMax":
                isValid = Validator.isNumeric(value);
                if ( value.isEmpty() ) {
                    fxId = null;
                    this.hintLabel.setText("");
                }
                break;
        }

        String style = Validator.getValidatorStyle();
        String message = Validator.getValidatorMessage();

        Validator.clearClass(node);
        Validator.clearClass(this.hintLabel);

        if ( fxId != null ) {

            node.getStyleClass().add(style);
            this.hintLabel.setText(message);
            this.hintLabel.getStyleClass().add(style);
        }
    }

    private void initializeEventListener()
    {
        this.siteComboBox.getCheckModel().getCheckedItems().addListener(
                (ListChangeListener<Site>) change -> this.refreshTypeVehiculeComboBox());
        this.typeVehiculeComboBox.getCheckModel().getCheckedItems().addListener(
                (ListChangeListener<TypeVehicule>) change -> this.updateRechercherBoutonState());
        this.budgetMin.textProperty().addListener(
                (observable, oldValue, newValue) -> this.updateRechercherBoutonState());
        this.villeDepartField.valueProperty().addListener(
                (observable, oldValue, newValue) -> this.updateRechercherBoutonState());
        this.villeArriveeField.valueProperty().addListener(
                (observable, oldValue, newValue) -> this.updateRechercherBoutonState());

    }

    /**
     * Charge la liste des sites dans le {@link ComboBox}. <br>
     * Cette méthode récupère tous les sites à partir de la base de données.
     */
    private void loadSiteComboBox()
    {
        List<Site> listeSites;

        try {
            listeSites = daoFactory.getSiteDAO().findAllSitesWithVehiclesParamsAndXPaths();
        } catch ( SQLException e ) {
            throw new RuntimeException(e);
        }
        this.siteComboBox.getItems().addAll(listeSites);
    }

    /**
     * Personnalise l'affichage des villes dans un {@link ComboBox} en affichant le nom et le code du département.<br>
     * Permet de retrouver l'objet {@link Ville} correspondant à la saisie de l'utilisateur.
     *
     * @param comboBox Le {@link ComboBox} à configurer.
     */
    private void configureVilleComboBox(ComboBox<Ville> comboBox)
    {

        // Personnalise l'affichage dans la liste déroulante
        comboBox.setCellFactory(lv -> new ListCell<>()
        {
            @Override
            protected void updateItem(Ville item, boolean empty)
            {
                super.updateItem(item, empty);
                if ( item != null && !empty ) {
                    setText(item.getFormated("{ville} ({numDep})"));
                } else {
                    setText(null);
                }
            }
        });

        // Gestion de la convertion Objet Ville et Text nom de la ville et numéro de departement
        comboBox.setConverter(new StringConverter<>()
        {
            @Override
            public String toString(Ville ville)
            {
                return ville == null ? "" : ville.getNom() + "(" + ville.getDepartement().getNumero() + ")";
            }


            // Rechercher une ville correspondant au formatage nom + numéro de département dans la liste du ComboBox
            @Override
            public Ville fromString(String villeName)
            {
                return comboBox.getItems().stream()
                               .filter(ville -> (
                                       ville.getNom() + "(" + ville.getDepartement().getNumero() + ")").equals(
                                       villeName))
                               .findFirst()
                               .orElse(null);
            }
        });
    }

    /**
     * Initialise les sélecteurs de date pour la période de début et de fin.<br>
     * Configure le {@link DatePicker} pour ne permettre la sélection que des dates à partir de la date actuelle.
     *
     * @see DatePicker
     * @see #getDateCellFactory(LocalDate, LocalDate)
     */
    private void initializeDatePicker()
    {
        this.periodeDebutDatePicker.setDayCellFactory(this.getDateCellFactory(LocalDate.now(), null));
        this.periodeFinDatePicker.setDayCellFactory(this.getDateCellFactory(LocalDate.now().plusDays(1), null));
    }


    /**
     * {@link Callback} utilisé par un {@link DatePicker} pour personnaliser les cellules de date. <br>
     * Désactive et modifie le style, des cellules dates avant {@code minDate} et après {@code maxDate}.<br>
     * // TODO METTRE DU CSS
     *
     * @param minDate La date minimale
     * @param maxDate La date maximale
     * @return Une {@link Callback} qui retourne une {@link DateCell} personnalisée utilisée par le {@link DatePicker}.
     */
    private Callback<DatePicker, DateCell> getDateCellFactory(LocalDate minDate, LocalDate maxDate)
    {
        return datePicker -> new DateCell()
        {
            @Override
            public void updateItem(LocalDate item, boolean empty)
            {
                super.updateItem(item, empty);
                if ( item == null || empty ) {
                    setDisable(true);
                } else {
                    if ( (minDate != null && item.isBefore(minDate)) ||
                         (maxDate != null && item.isAfter(maxDate)) ) {
                        setDisable(true);
                        getStyleClass().add("disabled");
                    } else {
                        setDisable(false);
                        getStyleClass().removeAll("disabled");
                    }
                }
            }
        };
    }


    private void updateRechercherBoutonState()
    {
        boolean disable = this.typeVehiculeComboBox.getCheckModel().getCheckedItems().isEmpty()
                          || this.budgetMin.getText().isEmpty()
                          || this.villeDepartField.getValue() == null
                          || this.villeArriveeField.getValue() == null
                          || this.periodeDebutDatePicker.getValue() == null
                          || this.periodeFinDatePicker.getValue() == null
                          || !this.hintLabel.getText().isEmpty();
        this.rechercherBouton.setDisable(disable);
    }

    public void toggleDisableForm()
    {
        this.disable = !this.disable;

        this.siteComboBox.setDisable(this.disable);
        this.typeVehiculeComboBox.setDisable(this.disable);
        this.budgetMin.setDisable(this.disable);
        this.budgetMax.setDisable(this.disable);
        this.villeDepartField.setDisable(this.disable);
        this.villeArriveeField.setDisable(this.disable);
        this.periodeDebutDatePicker.setDisable(this.disable);
        this.periodeFinDatePicker.setDisable(this.disable);

        this.effacerBouton.setDisable(this.disable);
        this.rechercherBouton.setDisable(this.disable);
    }
}
