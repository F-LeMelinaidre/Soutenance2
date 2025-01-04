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
import javafx.animation.PauseTransition;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.controlsfx.control.CheckComboBox;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

// TODO
//  MERITE D ÊTRE FACTORISE
//  AMELIORER LA GESTION DU BOUTON RECHERCHER
//  - VERIF FORMAT DE LA DATE - SI LE FORMAT EST BON ATTRIBUER ET SELECTIONNER LA DATE,
//    POUR NE PAS ETRE OBLIGé DE TAPER ENTRER
//  - VERIF CHAMP BUDGET EST UN INT
/*Utiliser pour les dates
/*numberField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
        // Si l'entrée n'est pas un chiffre ou une touche de navigation (flèches, suppr, etc.)
        if (!event.getText().matches("[0-9]") && !event.getCode().isNavigationKey()) {
        event.consume();  // Empêche l'entrée d'un caractère non numérique
    }
            });*/
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

        List<Site>         sitesSelected         = this.siteComboBox.getCheckModel().getCheckedItems();
        List<TypeVehicule> typeVehiculesSelected = this.typeVehiculeComboBox.getCheckModel().getCheckedItems();
        String             budgetMax             = this.budgetMax.getText();
        String             budgetMin             = this.budgetMin.getText();
        Ville              villeDep              = this.villeDepartField.getSelectionModel().getSelectedItem();
        Ville              villeArr              = this.villeArriveeField.getSelectionModel().getSelectedItem();
        String             date                  = this.dateCourante.toString();
        LocalDate          dateDepart            = this.periodeDebutDatePicker.getValue();
        LocalDate          dateArrivee           = this.periodeFinDatePicker.getValue();

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
        this.villeArriveeField.setValue(null);

        this.periodeDebutDatePicker.setValue(null);
        this.periodeFinDatePicker.setValue(null);
        this.initializeDatePicker();

        this.budgetMin.clear();
        this.budgetMax.clear();

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
     * Vérifie si la valeur saisie dans le {@link TextField} est un entier.<br>
     * Si le texte ne correspond pas, un message d'indication est affiché et le champ de texte est vidé.<br>
     * Si le texte est valide, appel la méthode de mise à jour d'état du bouton de recherche.
     *
     * @param event l'événement {@link KeyEvent}
     */
    @FXML
    private void isInteger(KeyEvent event)
    {
        this.clearHintLabel();

        TextField textField = (TextField) event.getSource();

        String currentValue = textField.getText();
        if ( !currentValue.matches("\\d*") ) {
            String msg = "Budget journalier: Veuillez saisir une valeur numérique!";
            this.setHintLabel(msg, "error");

            textField.getStyleClass().add("textfield-error");
            textField.clear();
        } else {
            updateRechercherBoutonState();
            textField.getStyleClass().remove("textfield-error");
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
        ComboBox<Ville> item             = (ComboBox<Ville>) keyEvent.getSource();
        String          communeRecherche = item.getEditor().getText();
        int             minLength        = 3;

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
        DatePicker date       = (DatePicker) event.getSource();
        LocalDate  departDate = date.getValue();

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
        DatePicker date    = (DatePicker) event.getSource();
        LocalDate  dateFin = date.getValue();

        if ( date.getValue() != null ) {
            this.periodeDebutDatePicker.setDayCellFactory(
                    getDateCellFactory(this.dateCourante, dateFin.minusDays(1)));
            this.updateRechercherBoutonState();
        }
    }

    // TODO AJOUTER EVENT POUR RETIRER LE MESSAGE DE DEMANDE DE VALIDATION PAR ENTRER POUR LES DATEPICKERS
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

        // Vérifie et indique si le format de la date saisi manuellement est valide
        this.periodeDebutDatePicker.getEditor().textProperty().addListener(
                (observable, oldValue, newValue) -> this.isDate(newValue, this.periodeDebutDatePicker));
        this.periodeFinDatePicker.getEditor().textProperty().addListener(
                (observable, oldValue, newValue) -> this.isDate(newValue, this.periodeFinDatePicker));

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

    private void isDate(String date, DatePicker datePicker)
    {
        this.clearHintLabel();
        datePicker.getStyleClass().remove("textfield-error");

        String dateFormatRegex = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/(\\d{4})$";// '?' pour rendre l'année optionnelle
        String msg             = "Période: Valider votre saisi par Entrer!";

        // Vérifier si la saisie contient des caractères alpha
        if ( date.matches(".*[a-zA-Z]+.*") || !date.matches(dateFormatRegex) ) {
            msg = "Période: Veuillez saisir une date au format jj/mm/aaaa!";
            this.setHintLabel(msg, "error");
            datePicker.getStyleClass().add("textfield-error");
        } else {
            DateTimeFormatter formatter    = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate         formatedDate = LocalDate.parse(date, formatter);

            if ( formatedDate.isBefore(this.dateCourante) ) {
                msg = "Période: Veuillez saisir une date supérieur à la date du jour!";
                this.setHintLabel(msg, "error");
                datePicker.getStyleClass().add("textfield-error");
            } else if ( datePicker.getValue() == null ) {
                this.setHintLabel(msg, "warning");
            }
        }

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

    private void setHintLabel(String msg, String style)
    {
        this.hintLabel.setText(msg);
        if ( style.equals("error") ) {
            this.hintLabel.getStyleClass().remove("hint-warning");
            this.hintLabel.getStyleClass().add("hint-error");
        } else if ( style.equals("warning") ) {
            this.hintLabel.getStyleClass().remove("hint-error");
            this.hintLabel.getStyleClass().add("hint-warning");
        }
    }

    private void clearHintLabel()
    {
        this.hintLabel.setText("");
        this.hintLabel.getStyleClass().remove("hint-warning");
        this.hintLabel.getStyleClass().remove("hint-error");
    }

    private void updateRechercherBoutonState()
    {
        boolean disable = this.typeVehiculeComboBox.getCheckModel().getCheckedItems().isEmpty()
                          || this.budgetMin.getText().isEmpty()
                          || this.villeDepartField.getValue() == null
                          || this.villeArriveeField.getValue() == null
                          || this.periodeDebutDatePicker.getValue() == null
                          || this.periodeFinDatePicker.getValue() == null;

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
