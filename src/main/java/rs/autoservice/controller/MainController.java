package rs.autoservice.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import rs.autoservice.client.NetworkClient;
import rs.autoservice.model.*;
import rs.autoservice.service.PriceCalculator;
import rs.autoservice.service.StatisticsService;
import rs.autoservice.util.AlertUtil;
import rs.autoservice.util.GenericFilter;
import rs.autoservice.util.ValidationUtil;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

/**
 * Glavni JavaFX kontroler za upravljanje Dashboard-om i svim modulima sistema.
 * Obuhvata Klijente, Vozila, Zaposlene, Usluge, Termine, Statistiku i Jsoup Vesti.
 *
 * @author Vukasin Miljkovic
 */
public class MainController implements Initializable {

    private final NetworkClient networkClient = NetworkClient.getInstance();

    // ==========================================
    // FXML HEADER & NAVIGATION
    // ==========================================
    @FXML private Label userBadgeLabel;
    @FXML private TabPane mainTabPane;

    // ==========================================
    // FXML TAB 1: DASHBOARD
    // ==========================================
    @FXML private Label dashClientsCount;
    @FXML private Label dashVehiclesCount;
    @FXML private Label dashAppointmentsCount;
    @FXML private Label dashRevenue;
    @FXML private TableView<Appointment> dashAppointmentsTable;
    @FXML private TableColumn<Appointment, Integer> dashColId;
    @FXML private TableColumn<Appointment, String> dashColDate;
    @FXML private TableColumn<Appointment, String> dashColTime;
    @FXML private TableColumn<Appointment, String> dashColCustomer;
    @FXML private TableColumn<Appointment, String> dashColVehicle;
    @FXML private TableColumn<Appointment, String> dashColService;
    @FXML private TableColumn<Appointment, String> dashColStatus;
    @FXML private TableColumn<Appointment, Double> dashColPrice;

    // ==========================================
    // FXML TAB 2: KLIJENTI (CUSTOMERS)
    // ==========================================
    @FXML private TextField customerSearchField;
    @FXML private TableView<Customer> customersTable;
    @FXML private TableColumn<Customer, Integer> colCustId;
    @FXML private TableColumn<Customer, String> colCustFirstName;
    @FXML private TableColumn<Customer, String> colCustLastName;
    @FXML private TableColumn<Customer, String> colCustPhone;
    @FXML private TableColumn<Customer, String> colCustEmail;

    @FXML private TextField custFirstNameField;
    @FXML private TextField custLastNameField;
    @FXML private TextField custPhoneField;
    @FXML private TextField custEmailField;
    private Customer selectedCustomer = null;

    // ==========================================
    // FXML TAB 3: VOZILA (VEHICLES)
    // ==========================================
    @FXML private TextField vehicleSearchField;
    @FXML private TableView<Vehicle> vehiclesTable;
    @FXML private TableColumn<Vehicle, Integer> colVehId;
    @FXML private TableColumn<Vehicle, String> colVehOwner;
    @FXML private TableColumn<Vehicle, String> colVehBrand;
    @FXML private TableColumn<Vehicle, String> colVehModel;
    @FXML private TableColumn<Vehicle, Integer> colVehYear;
    @FXML private TableColumn<Vehicle, String> colVehPlate;

    @FXML private ComboBox<Customer> vehOwnerCombo;
    @FXML private TextField vehBrandField;
    @FXML private TextField vehModelField;
    @FXML private TextField vehYearField;
    @FXML private TextField vehPlateField;
    private Vehicle selectedVehicle = null;

    // ==========================================
    // FXML TAB 4: ZAPOSLENI (EMPLOYEES)
    // ==========================================
    @FXML private TextField employeeSearchField;
    @FXML private TableView<Employee> employeesTable;
    @FXML private TableColumn<Employee, Integer> colEmpId;
    @FXML private TableColumn<Employee, String> colEmpFirstName;
    @FXML private TableColumn<Employee, String> colEmpLastName;
    @FXML private TableColumn<Employee, String> colEmpPosition;
    @FXML private TableColumn<Employee, String> colEmpPhone;

    @FXML private TextField empFirstNameField;
    @FXML private TextField empLastNameField;
    @FXML private ComboBox<String> empPositionCombo;
    @FXML private TextField empPhoneField;
    private Employee selectedEmployee = null;

    // ==========================================
    // FXML TAB 5: USLUGE (SERVICES)
    // ==========================================
    @FXML private TextField serviceSearchField;
    @FXML private TableView<ServiceItem> servicesTable;
    @FXML private TableColumn<ServiceItem, Integer> colServId;
    @FXML private TableColumn<ServiceItem, String> colServName;
    @FXML private TableColumn<ServiceItem, Double> colServPrice;
    @FXML private TableColumn<ServiceItem, Integer> colServDuration;
    @FXML private TableColumn<ServiceItem, String> colServDesc;

    @FXML private TextField servNameField;
    @FXML private TextField servPriceField;
    @FXML private TextField servDurationField;
    @FXML private TextArea servDescField;
    private ServiceItem selectedService = null;

    // ==========================================
    // FXML TAB 6: TERMINI (APPOINTMENTS)
    // ==========================================
    @FXML private DatePicker filterDatePicker;
    @FXML private ComboBox<String> filterStatusCombo;
    @FXML private TableView<Appointment> appointmentsTable;
    @FXML private TableColumn<Appointment, Integer> colAppId;
    @FXML private TableColumn<Appointment, String> colAppDate;
    @FXML private TableColumn<Appointment, String> colAppTime;
    @FXML private TableColumn<Appointment, String> colAppCustomer;
    @FXML private TableColumn<Appointment, String> colAppVehicle;
    @FXML private TableColumn<Appointment, String> colAppEmployee;
    @FXML private TableColumn<Appointment, String> colAppService;
    @FXML private TableColumn<Appointment, String> colAppStatus;
    @FXML private TableColumn<Appointment, Double> colAppPrice;

    @FXML private ComboBox<Customer> appCustomerCombo;
    @FXML private ComboBox<Vehicle> appVehicleCombo;
    @FXML private ComboBox<ServiceItem> appServiceCombo;
    @FXML private ComboBox<Employee> appEmployeeCombo;
    @FXML private DatePicker appDatePicker;
    @FXML private ComboBox<String> appTimeCombo;
    @FXML private TextField appPartsPriceField;
    @FXML private TextField appNotesField;
    @FXML private Label appCalculatedPriceLabel;
    private Appointment selectedAppointment = null;

    // ==========================================
    // FXML TAB 7: STATISTIKA
    // ==========================================
    @FXML private Label statTotalAppointments;
    @FXML private Label statCompletedAppointments;
    @FXML private Label statCancelledAppointments;
    @FXML private Label statCompletionRate;
    @FXML private Label statTotalRevenue;
    @FXML private PieChart statusPieChart;
    @FXML private BarChart<String, Number> servicesBarChart;

    // ==========================================
    // FXML TAB 8: JSOUP VESTI
    // ==========================================
    @FXML private ListView<NewsArticle> newsListView;

    // ==========================================
    // INTERNE LISTE
    // ==========================================
    private final ObservableList<Customer> customerList = FXCollections.observableArrayList();
    private final ObservableList<Vehicle> vehicleList = FXCollections.observableArrayList();
    private final ObservableList<Employee> employeeList = FXCollections.observableArrayList();
    private final ObservableList<ServiceItem> serviceList = FXCollections.observableArrayList();
    private final ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();
    private final ObservableList<NewsArticle> newsList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initUserInfo();
        initTableColumns();
        initComboBoxes();
        initSearchAndFilterListeners();
        initTableSelectionListeners();
        initPriceCalculationListeners();
        initNewsListView();

        // Učitavanje svih podataka sa servera
        refreshAllData();
    }

    private void initUserInfo() {
        User user = networkClient.getCurrentUser();
        if (user != null) {
            userBadgeLabel.setText("👤 " + user.getFullName() + " (" + user.getRole() + ")");
        }
    }

    private void initTableColumns() {
        // Customers
        colCustId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCustFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colCustLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colCustPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colCustEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        customersTable.setItems(customerList);

        // Vehicles
        colVehId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colVehOwner.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colVehBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colVehModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        colVehYear.setCellValueFactory(new PropertyValueFactory<>("year"));
        colVehPlate.setCellValueFactory(new PropertyValueFactory<>("licensePlate"));
        vehiclesTable.setItems(vehicleList);

        // Employees
        colEmpId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colEmpFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colEmpLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colEmpPosition.setCellValueFactory(new PropertyValueFactory<>("position"));
        colEmpPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        employeesTable.setItems(employeeList);

        // Services
        colServId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colServName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colServPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colServDuration.setCellValueFactory(new PropertyValueFactory<>("durationMinutes"));
        colServDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        servicesTable.setItems(serviceList);

        // Appointments (Main)
        colAppId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colAppDate.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        colAppTime.setCellValueFactory(new PropertyValueFactory<>("appointmentTime"));
        colAppCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colAppVehicle.setCellValueFactory(new PropertyValueFactory<>("vehicleDetails"));
        colAppEmployee.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        colAppService.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        colAppStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colAppPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        appointmentsTable.setItems(appointmentList);

        // Dashboard Appointments
        dashColId.setCellValueFactory(new PropertyValueFactory<>("id"));
        dashColDate.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        dashColTime.setCellValueFactory(new PropertyValueFactory<>("appointmentTime"));
        dashColCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        dashColVehicle.setCellValueFactory(new PropertyValueFactory<>("vehicleDetails"));
        dashColService.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        dashColStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        dashColPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        dashAppointmentsTable.setItems(appointmentList);
    }

    private void initComboBoxes() {
        // Pozicije zaposlenih
        empPositionCombo.setItems(FXCollections.observableArrayList(
                "Glavni automehaničar", "Auto-električar", "Dijagnostičar", "Vulkanizer i trap", "Prijemnik vozila"
        ));

        // Vremenski termini
        ObservableList<String> times = FXCollections.observableArrayList();
        for (int h = 8; h <= 17; h++) {
            times.add(String.format("%02d:00", h));
            times.add(String.format("%02d:30", h));
        }
        appTimeCombo.setItems(times);

        // Statusi filtera
        filterStatusCombo.setItems(FXCollections.observableArrayList("Svi statusi", "ZAKAZANO", "ZAVRŠENO", "OTKAZANO"));
        filterStatusCombo.getSelectionModel().selectFirst();

        // Povezivanje ComboBox-ova za forme
        vehOwnerCombo.setItems(customerList);
        appCustomerCombo.setItems(customerList);
        appEmployeeCombo.setItems(employeeList);
        appServiceCombo.setItems(serviceList);

        // Kada se izabere klijent u formi za zakazivanje, filtriramo njegova vozila
        appCustomerCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                List<Vehicle> clientVehicles = GenericFilter.filter(vehicleList, v -> v.getCustomerId() == newVal.getId());
                appVehicleCombo.setItems(FXCollections.observableArrayList(clientVehicles));
                if (!clientVehicles.isEmpty()) {
                    appVehicleCombo.getSelectionModel().selectFirst();
                } else {
                    appVehicleCombo.getSelectionModel().clearSelection();
                    appVehicleCombo.setPromptText("⚠️ Klijent nema vozila (dodajte u tabu 'Vozila')");
                }
            } else {
                appVehicleCombo.setItems(FXCollections.observableArrayList());
                appVehicleCombo.setPromptText("Izaberite vozilo klijenta...");
            }
            updateCalculatedPrice();
        });
    }

    private void initPriceCalculationListeners() {
        appServiceCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateCalculatedPrice());
        appPartsPriceField.textProperty().addListener((obs, oldVal, newVal) -> updateCalculatedPrice());
    }

    private void updateCalculatedPrice() {
        ServiceItem service = appServiceCombo.getValue();
        Customer customer = appCustomerCombo.getValue();

        double basePrice = (service != null) ? service.getPrice() : 0.0;
        double partsPrice = 0.0;

        try {
            String partsText = appPartsPriceField.getText();
            if (partsText != null && !partsText.trim().isEmpty()) {
                partsPrice = Double.parseDouble(partsText.trim());
            }
        } catch (NumberFormatException ignored) {
        }

        // Brojimo prethodne zavrsene posete klijenta za lojalti popust (Dodatna operacija)
        int pastVisits = 0;
        if (customer != null) {
            pastVisits = GenericFilter.countMatching(appointmentList,
                    a -> a.getCustomerId() == customer.getId() && "ZAVRŠENO".equalsIgnoreCase(a.getStatus()));
        }

        double total = PriceCalculator.calculateTotal(basePrice, partsPrice, pastVisits);
        String discountNote = (pastVisits >= PriceCalculator.LOYALTY_THRESHOLD_VISITS) ? " (10% lojalni popust uračunat!)" : "";
        appCalculatedPriceLabel.setText(String.format("%.2f RSD%s", total, discountNote));
    }

    private void initSearchAndFilterListeners() {
        // Pretraga klijenata
        customerSearchField.textProperty().addListener((obs, oldVal, query) -> {
            if (query == null || query.trim().isEmpty()) {
                customersTable.setItems(customerList);
            } else {
                List<Customer> filtered = GenericFilter.search(customerList,
                        c -> c.getFirstName() + " " + c.getLastName() + " " + c.getPhone() + " " + c.getEmail(), query);
                customersTable.setItems(FXCollections.observableArrayList(filtered));
            }
        });

        // Pretraga vozila
        vehicleSearchField.textProperty().addListener((obs, oldVal, query) -> {
            if (query == null || query.trim().isEmpty()) {
                vehiclesTable.setItems(vehicleList);
            } else {
                List<Vehicle> filtered = GenericFilter.search(vehicleList,
                        v -> v.getBrand() + " " + v.getModel() + " " + v.getLicensePlate() + " " + v.getCustomerName(), query);
                vehiclesTable.setItems(FXCollections.observableArrayList(filtered));
            }
        });

        // Pretraga zaposlenih
        employeeSearchField.textProperty().addListener((obs, oldVal, query) -> {
            if (query == null || query.trim().isEmpty()) {
                employeesTable.setItems(employeeList);
            } else {
                List<Employee> filtered = GenericFilter.search(employeeList,
                        e -> e.getFullName() + " " + e.getPosition() + " " + e.getPhone(), query);
                employeesTable.setItems(FXCollections.observableArrayList(filtered));
            }
        });

        // Pretraga usluga
        serviceSearchField.textProperty().addListener((obs, oldVal, query) -> {
            if (query == null || query.trim().isEmpty()) {
                servicesTable.setItems(serviceList);
            } else {
                List<ServiceItem> filtered = GenericFilter.search(serviceList,
                        s -> s.getName() + " " + s.getDescription(), query);
                servicesTable.setItems(FXCollections.observableArrayList(filtered));
            }
        });
    }

    private void initTableSelectionListeners() {
        // Selekcija klijenta
        customersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            selectedCustomer = selected;
            if (selected != null) {
                custFirstNameField.setText(selected.getFirstName());
                custLastNameField.setText(selected.getLastName());
                custPhoneField.setText(selected.getPhone());
                custEmailField.setText(selected.getEmail());
            }
        });

        // Selekcija vozila
        vehiclesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            selectedVehicle = selected;
            if (selected != null) {
                vehBrandField.setText(selected.getBrand());
                vehModelField.setText(selected.getModel());
                vehYearField.setText(String.valueOf(selected.getYear()));
                vehPlateField.setText(selected.getLicensePlate());
                for (Customer c : customerList) {
                    if (c.getId() == selected.getCustomerId()) {
                        vehOwnerCombo.setValue(c);
                        break;
                    }
                }
            }
        });

        // Selekcija zaposlenog
        employeesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            selectedEmployee = selected;
            if (selected != null) {
                empFirstNameField.setText(selected.getFirstName());
                empLastNameField.setText(selected.getLastName());
                empPositionCombo.setValue(selected.getPosition());
                empPhoneField.setText(selected.getPhone());
            }
        });

        // Selekcija usluge
        servicesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            selectedService = selected;
            if (selected != null) {
                servNameField.setText(selected.getName());
                servPriceField.setText(String.valueOf(selected.getPrice()));
                servDurationField.setText(String.valueOf(selected.getDurationMinutes()));
                servDescField.setText(selected.getDescription());
            }
        });

        // Selekcija termina
        appointmentsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            selectedAppointment = selected;
            if (selected != null) {
                for (Customer c : customerList) {
                    if (c.getId() == selected.getCustomerId()) {
                        appCustomerCombo.setValue(c);
                        break;
                    }
                }
                for (Vehicle v : vehicleList) {
                    if (v.getId() == selected.getVehicleId()) {
                        appVehicleCombo.setValue(v);
                        break;
                    }
                }
                for (ServiceItem s : serviceList) {
                    if (s.getId() == selected.getServiceId()) {
                        appServiceCombo.setValue(s);
                        break;
                    }
                }
                for (Employee e : employeeList) {
                    if (e.getId() == selected.getEmployeeId()) {
                        appEmployeeCombo.setValue(e);
                        break;
                    }
                }
                if (selected.getAppointmentDate() != null) {
                    appDatePicker.setValue(LocalDate.parse(selected.getAppointmentDate()));
                }
                appTimeCombo.setValue(selected.getAppointmentTime());
                appNotesField.setText(selected.getNotes());
                updateCalculatedPrice();
            }
        });
    }

    private void initNewsListView() {
        newsListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(NewsArticle article, boolean empty) {
                super.updateItem(article, empty);
                if (empty || article == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox box = new VBox(4);
                    Label titleLbl = new Label("📰 " + article.getTitle());
                    titleLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #1e293b;");

                    Label catLbl = new Label("[" + article.getCategory() + "]");
                    catLbl.setStyle("-fx-text-fill: #2563eb; -fx-font-size: 11px; -fx-font-weight: bold;");

                    Label summaryLbl = new Label(article.getSummary());
                    summaryLbl.setWrapText(true);
                    summaryLbl.setStyle("-fx-text-fill: #475569; -fx-font-size: 12px;");

                    box.getChildren().addAll(new HBox(8, catLbl, titleLbl), summaryLbl);
                    setGraphic(box);
                }
            }
        });
    }

    // ==========================================
    // REFRESH & LOAD DATA
    // ==========================================

    @FXML
    public void refreshAllData() {
        // 1. Customers
        Response<List<Customer>> respCust = networkClient.getCustomers();
        if (respCust.isSuccess() && respCust.getData() != null) {
            customerList.setAll(respCust.getData());
        }

        // 2. Vehicles
        Response<List<Vehicle>> respVeh = networkClient.getVehicles();
        if (respVeh.isSuccess() && respVeh.getData() != null) {
            vehicleList.setAll(respVeh.getData());
        }

        // 3. Employees
        Response<List<Employee>> respEmp = networkClient.getEmployees();
        if (respEmp.isSuccess() && respEmp.getData() != null) {
            employeeList.setAll(respEmp.getData());
        }

        // 4. Services
        Response<List<ServiceItem>> respServ = networkClient.getServices();
        if (respServ.isSuccess() && respServ.getData() != null) {
            serviceList.setAll(respServ.getData());
        }

        // 5. Appointments
        Response<List<Appointment>> respApp = networkClient.getAppointments();
        if (respApp.isSuccess() && respApp.getData() != null) {
            appointmentList.setAll(respApp.getData());
        }

        // 6. Statistika & Dashboard KPIs
        refreshStatistics();

        // 7. News
        handleRefreshNews();
    }

    @FXML
    public void refreshStatistics() {
        Response<Map<String, Object>> statsResp = networkClient.getStatistics();
        if (statsResp.isSuccess() && statsResp.getData() != null) {
            Map<String, Object> stats = statsResp.getData();

            int total = ((Number) stats.getOrDefault("total", 0)).intValue();
            int completed = ((Number) stats.getOrDefault("completed", 0)).intValue();
            int scheduled = ((Number) stats.getOrDefault("scheduled", 0)).intValue();
            int cancelled = ((Number) stats.getOrDefault("cancelled", 0)).intValue();
            double revenue = ((Number) stats.getOrDefault("revenue", 0.0)).doubleValue();

            // Dashboard KPIs
            dashClientsCount.setText(String.valueOf(customerList.size()));
            dashVehiclesCount.setText(String.valueOf(vehicleList.size()));
            dashAppointmentsCount.setText(String.valueOf(scheduled));
            dashRevenue.setText(String.format("%,.2f RSD", revenue));

            // Statistics Tab KPIs
            statTotalAppointments.setText(String.valueOf(total));
            statCompletedAppointments.setText(String.valueOf(completed));
            statCancelledAppointments.setText(String.valueOf(cancelled));
            statTotalRevenue.setText(String.format("%,.2f RSD", revenue));

            double rate = StatisticsService.calculateCompletionRate(completed, total);
            statCompletionRate.setText(String.format("%.1f%%", rate));

            // PieChart (Statusi)
            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
            if (completed > 0) pieData.add(new PieChart.Data("Završeno (" + completed + ")", completed));
            if (scheduled > 0) pieData.add(new PieChart.Data("Zakazano (" + scheduled + ")", scheduled));
            if (cancelled > 0) pieData.add(new PieChart.Data("Otkazano (" + cancelled + ")", cancelled));
            statusPieChart.setData(pieData);
            statusPieChart.setLabelsVisible(false);
            statusPieChart.setLegendVisible(true);

            // BarChart (Usluge)
            @SuppressWarnings("unchecked")
            Map<String, Object> serviceCounts = (Map<String, Object>) stats.get("serviceCounts");
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Broj realizovanih servisa");

            if (serviceCounts != null) {
                for (Map.Entry<String, Object> entry : serviceCounts.entrySet()) {
                    int count = ((Number) entry.getValue()).intValue();
                    series.getData().add(new XYChart.Data<>(entry.getKey(), count));
                }
            }
            servicesBarChart.getData().clear();
            servicesBarChart.getData().add(series);
        }
    }

    // ==========================================
    // CRUD: CUSTOMERS
    // ==========================================

    @FXML
    private void handleSaveCustomer(ActionEvent event) {
        String fn = custFirstNameField.getText();
        String ln = custLastNameField.getText();
        String ph = custPhoneField.getText();
        String em = custEmailField.getText();

        if (!ValidationUtil.isNotEmpty(fn) || !ValidationUtil.isNotEmpty(ln)) {
            AlertUtil.showError("Greška validacije", "Ime i prezime su obavezna polja.");
            return;
        }
        if (!ValidationUtil.isValidPhone(ph)) {
            AlertUtil.showError("Greška validacije", "Unesite ispravan broj telefona.");
            return;
        }
        if (!ValidationUtil.isValidEmail(em)) {
            AlertUtil.showError("Greška validacije", "Unesite ispravan format email adrese (npr. ime@domen.com).");
            return;
        }

        Customer c = new Customer(fn.trim(), ln.trim(), ph.trim(), em.trim());
        Response<Customer> resp = networkClient.addCustomer(c);
        if (resp.isSuccess()) {
            AlertUtil.showInfo("Uspeh", "Klijent je uspešno unet!");
            handleClearCustomerForm(null);
            refreshAllData();
        } else {
            AlertUtil.showError("Greška", resp.getMessage());
        }
    }

    @FXML
    private void handleUpdateCustomer(ActionEvent event) {
        if (selectedCustomer == null) {
            AlertUtil.showWarning("Upozorenje", "Prvo izaberite klijenta iz tabele za izmenu.");
            return;
        }
        String fn = custFirstNameField.getText();
        String ln = custLastNameField.getText();
        String ph = custPhoneField.getText();
        String em = custEmailField.getText();

        if (!ValidationUtil.isNotEmpty(fn) || !ValidationUtil.isNotEmpty(ln) || !ValidationUtil.isValidEmail(em)) {
            AlertUtil.showError("Greška validacije", "Proverite uneta polja.");
            return;
        }

        selectedCustomer.setFirstName(fn.trim());
        selectedCustomer.setLastName(ln.trim());
        selectedCustomer.setPhone(ph.trim());
        selectedCustomer.setEmail(em.trim());

        Response<Void> resp = networkClient.updateCustomer(selectedCustomer);
        if (resp.isSuccess()) {
            AlertUtil.showInfo("Uspeh", "Podaci klijenta su uspešno izmenjeni!");
            refreshAllData();
        } else {
            AlertUtil.showError("Greška", resp.getMessage());
        }
    }

    @FXML
    private void handleDeleteCustomer(ActionEvent event) {
        if (selectedCustomer == null) {
            AlertUtil.showWarning("Upozorenje", "Prvo izaberite klijenta iz tabele za brisanje.");
            return;
        }
        boolean confirm = AlertUtil.showConfirmation("Potvrda brisanja",
                "Da li ste sigurni da želite da obrišete klijenta: " + selectedCustomer.getFullName() + "?");
        if (confirm) {
            Response<Void> resp = networkClient.deleteCustomer(selectedCustomer.getId());
            if (resp.isSuccess()) {
                AlertUtil.showInfo("Uspeh", "Klijent je obrisan.");
                handleClearCustomerForm(null);
                refreshAllData();
            } else {
                AlertUtil.showError("Greška", resp.getMessage());
            }
        }
    }

    @FXML
    private void handleClearCustomerForm(ActionEvent event) {
        selectedCustomer = null;
        custFirstNameField.clear();
        custLastNameField.clear();
        custPhoneField.clear();
        custEmailField.clear();
        customersTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleClearCustomerSearch(ActionEvent event) {
        customerSearchField.clear();
    }

    // ==========================================
    // CRUD: VEHICLES
    // ==========================================

    @FXML
    private void handleSaveVehicle(ActionEvent event) {
        Customer owner = vehOwnerCombo.getValue();
        String brand = vehBrandField.getText();
        String model = vehModelField.getText();
        String yearStr = vehYearField.getText();
        String plate = vehPlateField.getText();

        if (owner == null) {
            AlertUtil.showError("Greška", "Morate izabrati vlasnika vozila.");
            return;
        }
        if (!ValidationUtil.isNotEmpty(brand) || !ValidationUtil.isNotEmpty(model)) {
            AlertUtil.showError("Greška", "Marka i model vozila su obavezni.");
            return;
        }
        int year;
        try {
            year = Integer.parseInt(yearStr.trim());
            if (!ValidationUtil.isValidYear(year)) {
                AlertUtil.showError("Greška", "Godina proizvodnje mora biti između 1900 i tekuće godine.");
                return;
            }
        } catch (NumberFormatException e) {
            AlertUtil.showError("Greška", "Godina proizvodnje mora biti ceo broj.");
            return;
        }

        if (!ValidationUtil.isValidLicensePlate(plate)) {
            AlertUtil.showError("Greška", "Format tablice nije validan (primer: BG-123-AA).");
            return;
        }

        Vehicle v = new Vehicle(owner.getId(), brand.trim(), model.trim(), year, plate.trim().toUpperCase());
        Response<Vehicle> resp = networkClient.addVehicle(v);
        if (resp.isSuccess()) {
            AlertUtil.showInfo("Uspeh", "Vozilo je uspešno evidentirano!");
            handleClearVehicleForm(null);
            refreshAllData();
        } else {
            AlertUtil.showError("Greška", resp.getMessage());
        }
    }

    @FXML
    private void handleUpdateVehicle(ActionEvent event) {
        if (selectedVehicle == null) {
            AlertUtil.showWarning("Upozorenje", "Izaberite vozilo za izmenu.");
            return;
        }
        Customer owner = vehOwnerCombo.getValue();
        if (owner == null) {
            AlertUtil.showError("Greška", "Izaberite vlasnika.");
            return;
        }

        try {
            int year = Integer.parseInt(vehYearField.getText().trim());
            selectedVehicle.setCustomerId(owner.getId());
            selectedVehicle.setBrand(vehBrandField.getText().trim());
            selectedVehicle.setModel(vehModelField.getText().trim());
            selectedVehicle.setYear(year);
            selectedVehicle.setLicensePlate(vehPlateField.getText().trim().toUpperCase());

            Response<Void> resp = networkClient.updateVehicle(selectedVehicle);
            if (resp.isSuccess()) {
                AlertUtil.showInfo("Uspeh", "Podaci o vozilu su ažurirani!");
                refreshAllData();
            } else {
                AlertUtil.showError("Greška", resp.getMessage());
            }
        } catch (Exception e) {
            AlertUtil.showError("Greška", "Proverite unete podatke: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteVehicle(ActionEvent event) {
        if (selectedVehicle == null) {
            AlertUtil.showWarning("Upozorenje", "Izaberite vozilo za brisanje.");
            return;
        }
        boolean confirm = AlertUtil.showConfirmation("Potvrda", "Obrisati vozilo " + selectedVehicle.getDisplayName() + "?");
        if (confirm) {
            Response<Void> resp = networkClient.deleteVehicle(selectedVehicle.getId());
            if (resp.isSuccess()) {
                AlertUtil.showInfo("Uspeh", "Vozilo je obrisano.");
                handleClearVehicleForm(null);
                refreshAllData();
            } else {
                AlertUtil.showError("Greška", resp.getMessage());
            }
        }
    }

    @FXML
    private void handleClearVehicleForm(ActionEvent event) {
        selectedVehicle = null;
        vehOwnerCombo.getSelectionModel().clearSelection();
        vehBrandField.clear();
        vehModelField.clear();
        vehYearField.clear();
        vehPlateField.clear();
        vehiclesTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleClearVehicleSearch(ActionEvent event) {
        vehicleSearchField.clear();
    }

    // ==========================================
    // CRUD: EMPLOYEES
    // ==========================================

    @FXML
    private void handleSaveEmployee(ActionEvent event) {
        String fn = empFirstNameField.getText();
        String ln = empLastNameField.getText();
        String pos = empPositionCombo.getValue();
        String ph = empPhoneField.getText();

        if (!ValidationUtil.isNotEmpty(fn) || !ValidationUtil.isNotEmpty(ln) || !ValidationUtil.isNotEmpty(pos)) {
            AlertUtil.showError("Greška", "Ime, prezime i pozicija su obavezna polja.");
            return;
        }

        Employee emp = new Employee(fn.trim(), ln.trim(), pos.trim(), ph.trim());
        Response<Employee> resp = networkClient.addEmployee(emp);
        if (resp.isSuccess()) {
            AlertUtil.showInfo("Uspeh", "Zaposleni je uspešno dodat!");
            handleClearEmployeeForm(null);
            refreshAllData();
        } else {
            AlertUtil.showError("Greška", resp.getMessage());
        }
    }

    @FXML
    private void handleUpdateEmployee(ActionEvent event) {
        if (selectedEmployee == null) {
            AlertUtil.showWarning("Upozorenje", "Izaberite zaposlenog za izmenu.");
            return;
        }

        selectedEmployee.setFirstName(empFirstNameField.getText().trim());
        selectedEmployee.setLastName(empLastNameField.getText().trim());
        selectedEmployee.setPosition(empPositionCombo.getValue());
        selectedEmployee.setPhone(empPhoneField.getText().trim());

        Response<Void> resp = networkClient.updateEmployee(selectedEmployee);
        if (resp.isSuccess()) {
            AlertUtil.showInfo("Uspeh", "Podaci o zaposlenom su izmenjeni!");
            refreshAllData();
        } else {
            AlertUtil.showError("Greška", resp.getMessage());
        }
    }

    @FXML
    private void handleDeleteEmployee(ActionEvent event) {
        if (selectedEmployee == null) {
            AlertUtil.showWarning("Upozorenje", "Izaberite zaposlenog za brisanje.");
            return;
        }
        boolean confirm = AlertUtil.showConfirmation("Potvrda", "Obrisati radnika " + selectedEmployee.getFullName() + "?");
        if (confirm) {
            Response<Void> resp = networkClient.deleteEmployee(selectedEmployee.getId());
            if (resp.isSuccess()) {
                AlertUtil.showInfo("Uspeh", "Zaposleni je obrisan.");
                handleClearEmployeeForm(null);
                refreshAllData();
            } else {
                AlertUtil.showError("Greška", resp.getMessage());
            }
        }
    }

    @FXML
    private void handleClearEmployeeForm(ActionEvent event) {
        selectedEmployee = null;
        empFirstNameField.clear();
        empLastNameField.clear();
        empPositionCombo.getSelectionModel().clearSelection();
        empPhoneField.clear();
        employeesTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleClearEmployeeSearch(ActionEvent event) {
        employeeSearchField.clear();
    }

    // ==========================================
    // CRUD: SERVICES
    // ==========================================

    @FXML
    private void handleSaveService(ActionEvent event) {
        String name = servNameField.getText();
        String priceStr = servPriceField.getText();
        String durStr = servDurationField.getText();
        String desc = servDescField.getText();

        if (!ValidationUtil.isNotEmpty(name)) {
            AlertUtil.showError("Greška", "Naziv usluge je obavezan.");
            return;
        }

        try {
            double price = Double.parseDouble(priceStr.trim());
            int duration = Integer.parseInt(durStr.trim());
            if (price <= 0 || duration <= 0) {
                AlertUtil.showError("Greška", "Cena i trajanje moraju biti veći od 0.");
                return;
            }

            ServiceItem s = new ServiceItem(name.trim(), desc.trim(), price, duration);
            Response<ServiceItem> resp = networkClient.addService(s);
            if (resp.isSuccess()) {
                AlertUtil.showInfo("Uspeh", "Usluga je uspešno dodata!");
                handleClearServiceForm(null);
                refreshAllData();
            } else {
                AlertUtil.showError("Greška", resp.getMessage());
            }
        } catch (NumberFormatException e) {
            AlertUtil.showError("Greška", "Cena i trajanje moraju biti brojevi.");
        }
    }

    @FXML
    private void handleUpdateService(ActionEvent event) {
        if (selectedService == null) {
            AlertUtil.showWarning("Upozorenje", "Izaberite uslugu za izmenu.");
            return;
        }

        try {
            selectedService.setName(servNameField.getText().trim());
            selectedService.setPrice(Double.parseDouble(servPriceField.getText().trim()));
            selectedService.setDurationMinutes(Integer.parseInt(servDurationField.getText().trim()));
            selectedService.setDescription(servDescField.getText().trim());

            Response<Void> resp = networkClient.updateService(selectedService);
            if (resp.isSuccess()) {
                AlertUtil.showInfo("Uspeh", "Podaci o usluzi su ažurirani!");
                refreshAllData();
            } else {
                AlertUtil.showError("Greška", resp.getMessage());
            }
        } catch (Exception e) {
            AlertUtil.showError("Greška", "Proverite unete vrednosti: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteService(ActionEvent event) {
        if (selectedService == null) {
            AlertUtil.showWarning("Upozorenje", "Izaberite uslugu za brisanje.");
            return;
        }
        boolean confirm = AlertUtil.showConfirmation("Potvrda", "Obrisati uslugu " + selectedService.getName() + "?");
        if (confirm) {
            Response<Void> resp = networkClient.deleteService(selectedService.getId());
            if (resp.isSuccess()) {
                AlertUtil.showInfo("Uspeh", "Usluga je obrisana.");
                handleClearServiceForm(null);
                refreshAllData();
            } else {
                AlertUtil.showError("Greška", resp.getMessage());
            }
        }
    }

    @FXML
    private void handleClearServiceForm(ActionEvent event) {
        selectedService = null;
        servNameField.clear();
        servPriceField.clear();
        servDurationField.clear();
        servDescField.clear();
        servicesTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleClearServiceSearch(ActionEvent event) {
        serviceSearchField.clear();
    }

    // ==========================================
    // CRUD: APPOINTMENTS & ZAKAZIVANJE
    // ==========================================

    @FXML
    public void handleNewAppointmentClick(ActionEvent event) {
        handleClearAppointmentForm(event);
        appDatePicker.setValue(LocalDate.now());
        if (appTimeCombo.getItems() != null && !appTimeCombo.getItems().isEmpty()) {
            appTimeCombo.getSelectionModel().selectFirst();
        }
        appCustomerCombo.requestFocus();
    }

    @FXML
    private void handleBookAppointment(ActionEvent event) {
        Customer customer = appCustomerCombo.getValue();
        Vehicle vehicle = appVehicleCombo.getValue();
        ServiceItem service = appServiceCombo.getValue();
        Employee employee = appEmployeeCombo.getValue();
        LocalDate date = appDatePicker.getValue();
        String time = appTimeCombo.getValue();
        String notes = appNotesField.getText();

        if (customer == null) {
            AlertUtil.showError("Greška validacije", "Molimo izaberite klijenta iz padajuće liste.");
            return;
        }
        if (vehicle == null) {
            AlertUtil.showError("Greška validacije", "Izabrani klijent nema izabrano vozilo!\nAko klijent nema uneto vozilo, idite u tab 'Vozila' i dodajte vozilo.");
            return;
        }
        if (service == null) {
            AlertUtil.showError("Greška validacije", "Molimo izaberite servisnu uslugu.");
            return;
        }
        if (employee == null) {
            AlertUtil.showError("Greška validacije", "Molimo izaberite servisera.");
            return;
        }
        if (date == null) {
            AlertUtil.showError("Greška validacije", "Molimo izaberite datum termina.");
            return;
        }
        if (time == null || time.trim().isEmpty()) {
            AlertUtil.showError("Greška validacije", "Molimo izaberite vreme termina.");
            return;
        }

        if (date.isBefore(LocalDate.now())) {
            AlertUtil.showWarning("Upozorenje", "Datum termina ne može biti u prošlosti.");
            return;
        }

        // Izračunavanje cene sa popustom
        int pastVisits = GenericFilter.countMatching(appointmentList,
                a -> a.getCustomerId() == customer.getId() && "ZAVRŠENO".equalsIgnoreCase(a.getStatus()));
        double partsPrice = 0.0;
        try {
            if (ValidationUtil.isNotEmpty(appPartsPriceField.getText())) {
                partsPrice = Double.parseDouble(appPartsPriceField.getText().trim());
            }
        } catch (NumberFormatException ignored) {}

        double totalPrice = PriceCalculator.calculateTotal(service.getPrice(), partsPrice, pastVisits);

        Appointment app = new Appointment(
                customer.getId(),
                vehicle.getId(),
                employee.getId(),
                service.getId(),
                date.toString(),
                time,
                "ZAKAZANO",
                notes != null ? notes.trim() : "",
                totalPrice
        );

        Response<Appointment> resp = networkClient.addAppointment(app);
        if (resp.isSuccess()) {
            AlertUtil.showInfo("Termin Zakazan", "Termin je uspešno rezervisan!\nUkupna cena: " + String.format("%.2f RSD", totalPrice));
            handleClearAppointmentForm(null);
            refreshAllData();
        } else {
            // Prikazujemo lepu Alert poruku ako je termin zauzet (Collision Alert)
            AlertUtil.showWarning("Zauzet Termin / Konflikt", resp.getMessage());
        }
    }

    @FXML
    private void handleUpdateAppointment(ActionEvent event) {
        if (selectedAppointment == null) {
            AlertUtil.showWarning("Upozorenje", "Prvo izaberite termin iz tabele koji želite da izmenite.");
            return;
        }

        Customer customer = appCustomerCombo.getValue();
        Vehicle vehicle = appVehicleCombo.getValue();
        ServiceItem service = appServiceCombo.getValue();
        Employee employee = appEmployeeCombo.getValue();
        LocalDate date = appDatePicker.getValue();
        String time = appTimeCombo.getValue();
        String notes = appNotesField.getText();

        if (customer == null) {
            AlertUtil.showError("Greška validacije", "Molimo izaberite klijenta.");
            return;
        }
        if (vehicle == null) {
            AlertUtil.showError("Greška validacije", "Molimo izaberite vozilo klijenta.");
            return;
        }
        if (service == null) {
            AlertUtil.showError("Greška validacije", "Molimo izaberite servisnu uslugu.");
            return;
        }
        if (employee == null) {
            AlertUtil.showError("Greška validacije", "Molimo izaberite servisera.");
            return;
        }
        if (date == null) {
            AlertUtil.showError("Greška validacije", "Molimo izaberite datum termina.");
            return;
        }
        if (time == null || time.trim().isEmpty()) {
            AlertUtil.showError("Greška validacije", "Molimo izaberite vreme termina.");
            return;
        }

        int pastVisits = GenericFilter.countMatching(appointmentList,
                a -> a.getCustomerId() == customer.getId() && "ZAVRŠENO".equalsIgnoreCase(a.getStatus()) && a.getId() != selectedAppointment.getId());
        double partsPrice = 0.0;
        try {
            if (ValidationUtil.isNotEmpty(appPartsPriceField.getText())) {
                partsPrice = Double.parseDouble(appPartsPriceField.getText().trim());
            }
        } catch (NumberFormatException ignored) {}

        double totalPrice = PriceCalculator.calculateTotal(service.getPrice(), partsPrice, pastVisits);

        selectedAppointment.setCustomerId(customer.getId());
        selectedAppointment.setVehicleId(vehicle.getId());
        selectedAppointment.setEmployeeId(employee.getId());
        selectedAppointment.setServiceId(service.getId());
        selectedAppointment.setAppointmentDate(date.toString());
        selectedAppointment.setAppointmentTime(time);
        selectedAppointment.setNotes(notes != null ? notes.trim() : "");
        selectedAppointment.setTotalPrice(totalPrice);

        Response<Void> resp = networkClient.updateAppointment(selectedAppointment);
        if (resp.isSuccess()) {
            AlertUtil.showInfo("Uspeh", "Podaci o terminu su uspešno izmenjeni!");
            refreshAllData();
        } else {
            AlertUtil.showWarning("Zauzet Termin / Konflikt", resp.getMessage());
        }
    }

    @FXML
    private void handleCompleteAppointment(ActionEvent event) {
        if (selectedAppointment == null) {
            AlertUtil.showWarning("Upozorenje", "Izaberite termin iz tabele.");
            return;
        }
        Response<Void> resp = networkClient.updateAppointmentStatus(selectedAppointment.getId(), "ZAVRŠENO");
        if (resp.isSuccess()) {
            AlertUtil.showInfo("Uspeh", "Termin je označen kao ZAVRŠEN!");
            refreshAllData();
        } else {
            AlertUtil.showError("Greška", resp.getMessage());
        }
    }

    @FXML
    private void handleCancelAppointment(ActionEvent event) {
        if (selectedAppointment == null) {
            AlertUtil.showWarning("Upozorenje", "Izaberite termin iz tabele.");
            return;
        }
        Response<Void> resp = networkClient.updateAppointmentStatus(selectedAppointment.getId(), "OTKAZANO");
        if (resp.isSuccess()) {
            AlertUtil.showInfo("Uspeh", "Termin je označen kao OTKAZAN.");
            refreshAllData();
        } else {
            AlertUtil.showError("Greška", resp.getMessage());
        }
    }

    @FXML
    private void handleDeleteAppointment(ActionEvent event) {
        if (selectedAppointment == null) {
            AlertUtil.showWarning("Upozorenje", "Izaberite termin za brisanje.");
            return;
        }
        boolean confirm = AlertUtil.showConfirmation("Potvrda", "Da li ste sigurni da želite da obrišete termin #" + selectedAppointment.getId() + "?");
        if (confirm) {
            Response<Void> resp = networkClient.deleteAppointment(selectedAppointment.getId());
            if (resp.isSuccess()) {
                AlertUtil.showInfo("Uspeh", "Termin je obrisan.");
                handleClearAppointmentForm(null);
                refreshAllData();
            } else {
                AlertUtil.showError("Greška", resp.getMessage());
            }
        }
    }

    @FXML
    private void handleClearAppointmentForm(ActionEvent event) {
        selectedAppointment = null;
        appCustomerCombo.getSelectionModel().clearSelection();
        appVehicleCombo.getSelectionModel().clearSelection();
        appVehicleCombo.setPromptText("Izaberite vozilo klijenta...");
        appServiceCombo.getSelectionModel().clearSelection();
        appEmployeeCombo.getSelectionModel().clearSelection();
        appDatePicker.setValue(LocalDate.now());
        appTimeCombo.getSelectionModel().clearSelection();
        appPartsPriceField.setText("0");
        appNotesField.clear();
        appCalculatedPriceLabel.setText("0.00 RSD");
        appointmentsTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleApplyAppointmentFilter(ActionEvent event) {
        LocalDate date = filterDatePicker.getValue();
        String status = filterStatusCombo.getValue();

        List<Appointment> filtered = GenericFilter.filter(appointmentList, a -> {
            boolean matchDate = (date == null) || date.toString().equals(a.getAppointmentDate());
            boolean matchStatus = (status == null || "Svi statusi".equalsIgnoreCase(status)) || status.equalsIgnoreCase(a.getStatus());
            return matchDate && matchStatus;
        });

        appointmentsTable.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    private void handleResetAppointmentFilter(ActionEvent event) {
        filterDatePicker.setValue(null);
        filterStatusCombo.getSelectionModel().selectFirst();
        appointmentsTable.setItems(appointmentList);
    }

    // ==========================================
    // TAB 8: JSOUP VESTI
    // ==========================================

    @FXML
    private void handleRefreshNews() {
        new Thread(() -> {
            Response<List<NewsArticle>> resp = networkClient.getNews();
            Platform.runLater(() -> {
                if (resp.isSuccess() && resp.getData() != null) {
                    newsList.setAll(resp.getData());
                    newsListView.setItems(newsList);
                }
            });
        }).start();
    }

    @FXML
    private void handleOpenSelectedNews(ActionEvent event) {
        NewsArticle selected = newsListView.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getUrl() == null || selected.getUrl().isEmpty()) {
            AlertUtil.showWarning("Upozorenje", "Izaberite članak iz liste.");
            return;
        }

        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(selected.getUrl()));
            } else {
                AlertUtil.showInfo("Link", "URL članka: " + selected.getUrl());
            }
        } catch (Exception e) {
            AlertUtil.showError("Greška", "Nije moguće otvoriti pretraživač: " + e.getMessage());
        }
    }

    // ==========================================
    // BRZE NAVIGACIJE
    // ==========================================

    @FXML
    private void switchToAppointments(ActionEvent event) {
        mainTabPane.getSelectionModel().select(5);
        handleNewAppointmentClick(event);
    }

    @FXML
    private void switchToCustomers(ActionEvent event) {
        mainTabPane.getSelectionModel().select(1);
    }

    @FXML
    private void switchToVehicles(ActionEvent event) {
        mainTabPane.getSelectionModel().select(2);
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        boolean confirm = AlertUtil.showConfirmation("Odjava", "Da li ste sigurni da želite da se odjavite?");
        if (confirm) {
            networkClient.setCurrentUser(null);
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) userBadgeLabel.getScene().getWindow();
                stage.setTitle("Auto Servis Management System - Prijava");
                stage.setScene(new Scene(root, 480, 520));
                stage.centerOnScreen();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
