package rs.autoservice.model;

import java.io.Serializable;

/**
 * Predstavlja servisnu uslugu u ponudi auto-servisa (npr. Mali servis, Zamena kočnica, Dijagnostika).
 *
 * @author Vukasin Miljkovic
 */
public class ServiceItem implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Jedinstveni identifikator usluge */
    private int id;

    /** Naziv usluge */
    private String name;

    /** Detaljan opis usluge */
    private String description;

    /** Osnovna cena usluge u RSD / EUR */
    private double price;

    /** Prosečno trajanje usluge u minutima */
    private int durationMinutes;

    /**
     * Prazan podrazumevani konstruktor.
     */
    public ServiceItem() {
    }

    /**
     * Konstruktor za kreiranje nove usluge.
     *
     * @param name            Naziv usluge
     * @param description     Opis usluge
     * @param price           Cena
     * @param durationMinutes Trajanje u minutima
     */
    public ServiceItem(String name, String description, double price, int durationMinutes) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.durationMinutes = durationMinutes;
    }

    /**
     * Konstruktor sa svim poljima.
     *
     * @param id              ID usluge
     * @param name            Naziv usluge
     * @param description     Opis usluge
     * @param price           Cena
     * @param durationMinutes Trajanje u minutima
     */
    public ServiceItem(int id, String name, String description, double price, int durationMinutes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.durationMinutes = durationMinutes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    @Override
    public String toString() {
        return name + " (" + price + " RSD, ~" + durationMinutes + " min)";
    }
}
