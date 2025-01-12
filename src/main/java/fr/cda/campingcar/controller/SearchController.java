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
import fr.cda.campingcar.dao.desktop.DeskTopDAOFactory;
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
import java.util.*;

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
    private CheckComboBox<VehicleType> vehicleTypeComboBox;
    @FXML
    private ComboBox<City> departureCityField;
    @FXML
    private ComboBox<City> arrivalCityField;
    @FXML
    private DatePicker periodStartDatePicker;
    @FXML
    private DatePicker periodEndDatePicker;
    @FXML
    private TextField budgetMin;
    @FXML
    private TextField budgetMax;
    @FXML
    private Button searchButton;
    @FXML
    private Button clearButton;
    @FXML
    private Label hintLabel;

    private HomeController homeController = null;
    private final DeskTopDAOFactory daoFactory;
    private final GeoApiService geoApiService;
    private final LocalDate currentDate;

    private boolean disable = false;

    public SearchController()
    {
        this.daoFactory    = DeskTopDAOFactory.getInstance();
        this.geoApiService = new GeoApiService(new GeoAPI());
        this.currentDate   = LocalDate.now();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        this.loadSiteComboBox();
        this.configureCityComboBox(this.departureCityField);
        this.configureCityComboBox(this.arrivalCityField);
        this.initializeDatePicker();
        this.initializeEventListener();


        this.searchButton.setDisable(true);
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
        List<VehicleType> vehiclesTypeSelected = this.vehicleTypeComboBox.getCheckModel().getCheckedItems();
        String budgetMax = this.budgetMax.getText();
        String budgetMin = this.budgetMin.getText();
        City cityDep = this.departureCityField.getSelectionModel().getSelectedItem();
        City cityArr = this.arrivalCityField.getSelectionModel().getSelectedItem();
        LocalDate periodStart = this.periodStartDatePicker.getValue();
        LocalDate periodeEnd = this.periodEndDatePicker.getValue();


        for ( Site site : sitesSelected ) {
            List<VehicleType> typeVehiclesFiltred = site.filterVehicle(vehiclesTypeSelected);

            Map<String, Dom> domMap = this.daoFactory.getDomDAO().findBySite(site.getId());
            site.setDomMap(domMap);
            site.setUrlSearch(typeVehiclesFiltred, cityDep, cityArr, periodStart, periodeEnd, budgetMin, budgetMax);

        }

        Search<Rent> search = new Search(Rent::new);
        search.setDate(this.currentDate);
        search.setListSites(sitesSelected);
        search.setListVehicle(vehiclesTypeSelected);
        search.setBugdetMin(budgetMin);
        search.setBudgetMax(budgetMax);
        search.setDepartureCity(cityDep);
        search.setArrivalCity(cityArr);
        search.setPeriodStart(periodStart);
        search.setPeriodEnd(periodeEnd);

        this.homeController.startScrapping(search);

        this.toggleDisableForm();
    }

    @FXML
    private void effacer()
    {

        this.siteComboBox.getCheckModel().clearChecks();
        this.vehicleTypeComboBox.getCheckModel().clearChecks();

        this.departureCityField.setValue(null);
        Validator.clearClass(this.departureCityField);
        this.arrivalCityField.setValue(null);
        Validator.clearClass(this.arrivalCityField);

        this.periodStartDatePicker.setValue(null);
        this.periodEndDatePicker.setValue(null);
        this.initializeDatePicker();

        this.budgetMin.clear();
        Validator.clearClass(this.budgetMin);
        this.budgetMax.clear();
        Validator.clearClass(this.budgetMax);

        this.updateSearchButtonState();
        this.homeController.clearScrapResult();
    }

    /**
     * Rafraichi la liste des types de véhicules proposés par les sites sélectionnés.
     */
    @FXML
    private void refreshVehicleTypeComboBox()
    {

        Set<VehicleType> vehicleTypes = new HashSet<>();

        // Ajouter les types de véhicules du/des sites selectionnés dans le Set
        for ( Site site : this.siteComboBox.getCheckModel().getCheckedItems() ) {
            vehicleTypes.addAll(site.getVehiculesType().values());
        }

        // Sauvegarde des éléments selectionnés
        List<VehicleType> selectedItems = new ArrayList<>(vehicleTypeComboBox.getCheckModel().getCheckedItems());
        vehicleTypeComboBox.getCheckModel().clearChecks();

        // Itérer les éléments actuels du ComboBox et ajuste la liste
        Iterator<VehicleType> iterator = vehicleTypeComboBox.getItems().iterator();
        while ( iterator.hasNext() ) {
            VehicleType type = iterator.next();
            if ( !vehicleTypes.contains(type) ) {
                // Suppression dans la liste du ComboBox si non present dans la nouvelle liste
                iterator.remove();
            } else {
                vehicleTypes.remove(type);
                // Suppression dans la nouvelle liste si présent dans le ComboBox
            }
        }

        // Ajoute les types restants dans la nouvelle liste au ComboBox
        vehicleTypeComboBox.getItems().addAll(vehicleTypes);
        // Trie par ordre alphabétique après modification
        vehicleTypeComboBox.getItems().sort(Comparator.comparing(VehicleType::getType));

        // Restaure la selection
        for ( VehicleType type : selectedItems ) {
            vehicleTypeComboBox.getCheckModel().check(type);
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

        ComboBox<City> item = (ComboBox<City>) keyEvent.getSource();
        String searchCity = item.getEditor().getText();
        int minLength = 3;

        if ( searchCity.length() >= minLength ) {

            PauseTransition pause = new PauseTransition(Duration.millis(300));
            pause.setOnFinished(event -> {
                Task<List<City>> task = geoApiService.searchCity(searchCity);

                task.setOnSucceeded(e -> {
                    List<City> cities = task.getValue();
                    item.getItems().clear();
                    item.getItems().setAll(cities);
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
        LocalDate startDate = date.getValue();

        if ( date.getValue() != null ) {
            this.periodEndDatePicker.setDayCellFactory(getDateCellFactory(startDate.plusDays(1), null));
            this.updateSearchButtonState();
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
        LocalDate endDate = date.getValue();

        if ( date.getValue() != null ) {
            this.periodStartDatePicker.setDayCellFactory(
                    getDateCellFactory(this.currentDate, endDate.minusDays(1)));
            this.updateSearchButtonState();
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
            case "departureCityField":
            case "arrivalCityField":
                isValid = Validator.isNotEmpty(value) && Validator.isValidCityName(value);
                break;
            case "periodStartDatePicker":
            case "periodEndDatePicker":
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
                (ListChangeListener<Site>) change -> this.refreshVehicleTypeComboBox());
        this.vehicleTypeComboBox.getCheckModel().getCheckedItems().addListener(
                (ListChangeListener<VehicleType>) change -> this.updateSearchButtonState());
        this.budgetMin.textProperty().addListener(
                (observable, oldValue, newValue) -> this.updateSearchButtonState());
        this.departureCityField.valueProperty().addListener(
                (observable, oldValue, newValue) -> this.updateSearchButtonState());
        this.arrivalCityField.valueProperty().addListener(
                (observable, oldValue, newValue) -> this.updateSearchButtonState());

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
     * Permet de retrouver l'objet {@link City} correspondant à la saisie de l'utilisateur.
     *
     * @param comboBox Le {@link ComboBox} à configurer.
     */
    private void configureCityComboBox(ComboBox<City> comboBox)
    {

        // Personnalise l'affichage dans la liste déroulante
        comboBox.setCellFactory(lv -> new ListCell<>()
        {
            @Override
            protected void updateItem(City item, boolean empty)
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
            public String toString(City city)
            {
                return city == null ? "" : city.getFormated("{ville} ({numDep})");
            }


            // Rechercher une ville correspondant au formatage nom + numéro de département dans la liste du ComboBox
            @Override
            public City fromString(String cityName)
            {
                return comboBox.getItems().stream()
                               .filter(city -> (
                                       city.getFormated("{ville} ({numDep})")).equals(
                                       cityName))
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
        this.periodStartDatePicker.setDayCellFactory(this.getDateCellFactory(LocalDate.now(), null));
        this.periodEndDatePicker.setDayCellFactory(this.getDateCellFactory(LocalDate.now().plusDays(1), null));
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


    private void updateSearchButtonState()
    {
        boolean disable = this.vehicleTypeComboBox.getCheckModel().getCheckedItems().isEmpty()
                          || this.budgetMin.getText().isEmpty()
                          || this.departureCityField.getValue() == null
                          || this.arrivalCityField.getValue() == null
                          || this.periodStartDatePicker.getValue() == null
                          || this.periodEndDatePicker.getValue() == null
                          || !this.hintLabel.getText().isEmpty();
        this.searchButton.setDisable(disable);
    }

    public void toggleDisableForm()
    {
        this.disable = !this.disable;

        this.siteComboBox.setDisable(this.disable);
        this.vehicleTypeComboBox.setDisable(this.disable);
        this.budgetMin.setDisable(this.disable);
        this.budgetMax.setDisable(this.disable);
        this.departureCityField.setDisable(this.disable);
        this.arrivalCityField.setDisable(this.disable);
        this.periodStartDatePicker.setDisable(this.disable);
        this.periodEndDatePicker.setDisable(this.disable);

        this.clearButton.setDisable(this.disable);
        this.searchButton.setDisable(this.disable);
    }
}
