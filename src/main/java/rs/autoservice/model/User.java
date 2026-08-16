package rs.autoservice.model;

import java.io.Serializable;

/**
 * Predstavlja korisnički nalog za prijavu na sistem (ADMIN ili EMPLOYEE).
 *
 * @author Vukasin Miljkovic
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    /** ID korisnika */
    private int id;

    /** Korisničko ime */
    private String username;

    /** Lozinka */
    private String password;

    /** Uloga korisnika: ADMIN ili EMPLOYEE */
    private String role;

    /** Puno ime i prezime vlasnika naloga */
    private String fullName;

    public User() {
    }

    public User(String username, String password, String role, String fullName) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
    }

    public User(int id, String username, String password, String role, String fullName) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return fullName + " (" + role + ")";
    }
}
