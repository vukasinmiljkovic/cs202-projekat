package rs.autoservice.model;

import java.io.Serializable;

/**
 * Predstavlja model vozila u sistemu auto-servisa.
 * Povezan je sa klijentom preko polja customerId (strani ključ).
 *
 * @author Vukasin Miljkovic
 */
public class Vehicle implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Jedinstveni identifikator vozila */
    private int id;

    /** ID klijenta koji je vlasnik vozila (FK) */
    private int customerId;

    /** Marka vozila (npr. Volkswagen, BMW, Audi) */
    private String brand;

    /** Model vozila (npr. Golf 7, 320d, A4) */
    private String model;

    /** Godina proizvodnje vozila */
    private int year;

    /** Registarska oznaka vozila (npr. BG-123-AB) */
    private String licensePlate;

    /** Pomoćno polje za prikaz imena vlasnika u tabeli */
    private String customerName;

    /**
     * Prazan podrazumevani konstruktor.
     */
    public Vehicle() {
    }

    /**
     * Konstruktor za novo vozilo pre unosa u bazu.
     *
     * @param customerId   ID vlasnika
     * @param brand        Marka
     * @param model        Model
     * @param year         Godina proizvodnje
     * @param licensePlate Registarska oznaka
     */
    public Vehicle(int customerId, String brand, String model, int year, String licensePlate) {
        this.customerId = customerId;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.licensePlate = licensePlate;
    }

    /**
     * Konstruktor sa svim parametrima.
     *
     * @param id           ID vozila
     * @param customerId   ID vlasnika
     * @param brand        Marka
     * @param model        Model
     * @param year         Godina proizvodnje
     * @param licensePlate Registarska oznaka
     */
    public Vehicle(int id, int customerId, String brand, String model, int year, String licensePlate) {
        this.id = id;
        this.customerId = customerId;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.licensePlate = licensePlate;
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

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * Vraća formatirani naziv vozila (marka + model + registracija).
     *
     * @return String opis vozila
     */
    public String getDisplayName() {
        return brand + " " + model + " [" + licensePlate + "]";
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
