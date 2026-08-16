package rs.autoservice.model;

import java.io.Serializable;

/**
 * Predstavlja zakazani termin servisa.
 * Povezuje klijenta, vozilo, zaposlenog i uslugu.
 *
 * @author Vukasin Miljkovic
 */
public class Appointment implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Jedinstveni identifikator termina */
    private int id;

    /** ID klijenta (FK) */
    private int customerId;

    /** ID vozila (FK) */
    private int vehicleId;

    /** ID zaposlenog servisera (FK) */
    private int employeeId;

    /** ID usluge (FK) */
    private int serviceId;

    /** Datum servisa u formatu YYYY-MM-DD */
    private String appointmentDate;

    /** Vreme servisa u formatu HH:mm (npr. 10:00) */
    private String appointmentTime;

    /** Status termina: ZAKAZANO, ZAVRŠENO, OTKAZANO */
    private String status;

    /** Dodatne napomene ili primedbe klijenta */
    private String notes;

    /** Ukupna cena servisa */
    private double totalPrice;

    // --- Pomoćna polja za lepši prikaz u tabelama korisničkog interfejsa ---
    private String customerName;
    private String vehicleDetails;
    private String employeeName;
    private String serviceName;

    /**
     * Prazan podrazumevani konstruktor.
     */
    public Appointment() {
        this.status = "ZAKAZANO";
    }

    /**
     * Konstruktor za kreiranje novog termina pre unosa u bazu.
     */
    public Appointment(int customerId, int vehicleId, int employeeId, int serviceId,
                       String appointmentDate, String appointmentTime, String status,
                       String notes, double totalPrice) {
        this.customerId = customerId;
        this.vehicleId = vehicleId;
        this.employeeId = employeeId;
        this.serviceId = serviceId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = (status != null && !status.isEmpty()) ? status : "ZAKAZANO";
        this.notes = notes;
        this.totalPrice = totalPrice;
    }

    /**
     * Konstruktor sa svim poljima iz baze.
     */
    public Appointment(int id, int customerId, int vehicleId, int employeeId, int serviceId,
                       String appointmentDate, String appointmentTime, String status,
                       String notes, double totalPrice) {
        this.id = id;
        this.customerId = customerId;
        this.vehicleId = vehicleId;
        this.employeeId = employeeId;
        this.serviceId = serviceId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.notes = notes;
        this.totalPrice = totalPrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getVehicleDetails() {
        return vehicleDetails;
    }

    public void setVehicleDetails(String vehicleDetails) {
        this.vehicleDetails = vehicleDetails;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public String toString() {
        return "Termin #" + id + " [" + appointmentDate + " " + appointmentTime + "] - " + status;
    }
}
