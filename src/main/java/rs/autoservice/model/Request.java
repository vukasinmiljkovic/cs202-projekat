package rs.autoservice.model;

import java.io.Serializable;

/**
 * Predstavlja mrežni zahtev koji klijent šalje serveru preko Socket konekcije.
 * Sadrži naziv akcije (komande) i opcioni payload u JSON formatu.
 *
 * @author Vukasin Miljkovic
 */
public class Request implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Naziv komande (npr. GET_CUSTOMERS, ADD_APPOINTMENT, LOGIN, itd.) */
    private String action;

    /** Podaci uz zahtev serijalizovani u JSON string */
    private String payload;

    public Request() {
    }

    public Request(String action) {
        this.action = action;
        this.payload = "";
    }

    public Request(String action, String payload) {
        this.action = action;
        this.payload = payload;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "Request{action='" + action + "', payload='" + payload + "'}";
    }
}
