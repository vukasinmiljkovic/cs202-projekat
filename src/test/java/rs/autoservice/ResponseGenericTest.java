package rs.autoservice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rs.autoservice.model.Customer;
import rs.autoservice.model.Response;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 testovi za proveru generičke klase {@link Response}.
 *
 * @author Vukasin Miljkovic
 */
public class ResponseGenericTest {

    @Test
    @DisplayName("Test 11: Generički Response sa pojedinačnim objektom (Customer)")
    void testGenericResponseWithSingleObject() {
        Customer customer = new Customer(1, "Marko", "Petrović", "+381641112233", "marko@gmail.com");
        Response<Customer> response = Response.ok("Klijent pronađen", customer);

        assertTrue(response.isSuccess(), "Response mora biti uspešan");
        assertEquals("Klijent pronađen", response.getMessage());
        assertNotNull(response.getData());
        assertEquals("Marko", response.getData().getFirstName());
    }

    @Test
    @DisplayName("Test 12: Generički Response sa kolekcijom (List<String>)")
    void testGenericResponseWithList() {
        List<String> services = Arrays.asList("Mali servis", "Veliki servis", "Dijagnostika");
        Response<List<String>> response = Response.ok(services);

        assertTrue(response.isSuccess());
        assertEquals(3, response.getData().size());
        assertEquals("Veliki servis", response.getData().get(1));
    }

    @Test
    @DisplayName("Test 13: Generički Response sa greškom")
    void testGenericResponseError() {
        Response<Customer> errorResp = Response.error("Klijent sa ID 99 nije pronađen.");

        assertFalse(errorResp.isSuccess(), "Greška mora imati success = false");
        assertEquals("Klijent sa ID 99 nije pronađen.", errorResp.getMessage());
        assertNull(errorResp.getData(), "Podatak kod greške treba da bude null");
    }
}
