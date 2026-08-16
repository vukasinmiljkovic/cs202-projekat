package rs.autoservice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rs.autoservice.model.Customer;
import rs.autoservice.util.GenericFilter;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 testovi za generičke metode klase {@link GenericFilter}.
 *
 * @author Vukasin Miljkovic
 */
public class GenericFilterTest {

    @Test
    @DisplayName("Test 14: Generičko filtriranje liste po predikatu")
    void testFilterByPredicate() {
        List<Integer> numbers = Arrays.asList(5, 12, 8, 20, 3, 18);
        List<Integer> greaterThanTen = GenericFilter.filter(numbers, n -> n > 10);

        assertEquals(3, greaterThanTen.size());
        assertTrue(greaterThanTen.contains(12));
        assertTrue(greaterThanTen.contains(20));
        assertTrue(greaterThanTen.contains(18));
    }

    @Test
    @DisplayName("Test 15: Generička tekstualna pretraga (search)")
    void testSearchByProperty() {
        List<Customer> customers = Arrays.asList(
                new Customer(1, "Marko", "Petrović", "064111", "marko@gmail.com"),
                new Customer(2, "Nikola", "Jovanović", "065222", "nikola@yahoo.com"),
                new Customer(3, "Ana", "Petrović", "063333", "ana@gmail.com")
        );

        // Tražimo sve klijente sa prezimenom 'Petrović'
        List<Customer> found = GenericFilter.search(customers, Customer::getLastName, "petrović");
        assertEquals(2, found.size());

        // Tražimo po email domenu
        List<Customer> yahooUsers = GenericFilter.search(customers, Customer::getEmail, "yahoo");
        assertEquals(1, yahooUsers.size());
        assertEquals("Nikola", yahooUsers.get(0).getFirstName());
    }

    @Test
    @DisplayName("Test 16: Generičko brojanje poklapanja")
    void testCountMatching() {
        List<String> statuses = Arrays.asList("ZAKAZANO", "ZAVRŠENO", "ZAKAZANO", "OTKAZANO", "ZAKAZANO");
        int scheduledCount = GenericFilter.countMatching(statuses, s -> "ZAKAZANO".equals(s));

        assertEquals(3, scheduledCount, "Broj zakazanih termina mora biti 3");
    }
}
