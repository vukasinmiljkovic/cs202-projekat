package rs.autoservice.model;

import java.io.Serializable;

/**
 * Predstavlja zaposlenog radnika u auto-servisu (mehaničar, električar, itd.).
 *
 * @author Vukasin Miljkovic
 */
public class Employee implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Jedinstveni identifikator zaposlenog */
    private int id;

    /** Ime zaposlenog */
    private String firstName;

    /** Prezime zaposlenog */
    private String lastName;

    /** Pozicija/radno mesto (npr. Glavni mehaničar, Auto-električar, Dijagnostičar) */
    private String position;

    /** Kontakt telefon zaposlenog */
    private String phone;

    /**
     * Prazan podrazumevani konstruktor.
     */
    public Employee() {
    }

    /**
     * Konstruktor za novog zaposlenog pre unosa u bazu.
     *
     * @param firstName Ime
     * @param lastName  Prezime
     * @param position  Radno mesto
     * @param phone     Telefon
     */
    public Employee(String firstName, String lastName, String position, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.phone = phone;
    }

    /**
     * Konstruktor sa svim poljima.
     *
     * @param id        ID zaposlenog
     * @param firstName Ime
     * @param lastName  Prezime
     * @param position  Radno mesto
     * @param phone     Telefon
     */
    public Employee(int id, String firstName, String lastName, String position, String phone) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Vraća puno ime i prezime zaposlenog.
     *
     * @return String sa imenom i prezimenom
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return getFullName() + " - " + position;
    }
}
