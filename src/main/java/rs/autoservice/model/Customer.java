package rs.autoservice.model;

import java.io.Serializable;

/**
 * Predstavlja model klijenta (vlasnika vozila) u auto-servisu.
 * Sadrži osnovne kontakt informacije klijenta.
 *
 * @author Vukasin Miljkovic
 */
public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Jedinstveni identifikator klijenta u bazi */
    private int id;
    
    /** Ime klijenta */
    private String firstName;
    
    /** Prezime klijenta */
    private String lastName;
    
    /** Broj telefona klijenta */
    private String phone;
    
    /** Email adresa klijenta */
    private String email;

    /**
     * Prazan podrazumevani konstruktor.
     */
    public Customer() {
    }

    /**
     * Konstruktor za kreiranje novog klijenta pre unosa u bazu (bez ID-a).
     *
     * @param firstName Ime klijenta
     * @param lastName  Prezime klijenta
     * @param phone     Broj telefona
     * @param email     Email adresa
     */
    public Customer(String firstName, String lastName, String phone, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
    }

    /**
     * Konstruktor sa svim poljima uključujući ID iz baze.
     *
     * @param id        Identifikator klijenta
     * @param firstName Ime klijenta
     * @param lastName  Prezime klijenta
     * @param phone     Broj telefona
     * @param email     Email adresa
     */
    public Customer(int id, String firstName, String lastName, String phone, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Vraća puno ime klijenta.
     *
     * @return Puno ime i prezime
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return getFullName() + " (" + phone + ")";
    }
}
