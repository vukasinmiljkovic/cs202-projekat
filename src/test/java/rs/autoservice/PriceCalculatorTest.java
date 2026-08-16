package rs.autoservice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rs.autoservice.service.PriceCalculator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 testovi za kalkulaciju cena servisa i primenu lojalti popusta.
 *
 * @author Vukasin Miljkovic
 */
public class PriceCalculatorTest {

    @Test
    @DisplayName("Test 4: Kalkulacija samo osnovne cene bez popusta")
    void testBasePriceOnly() {
        double total = PriceCalculator.calculateTotal(9500.0);
        assertEquals(9500.0, total, 0.001, "Cena mora biti jednaka osnovnoj ceni");
    }

    @Test
    @DisplayName("Test 5: Kalkulacija cene rada i ugrađenih delova bez popusta (manje od 3 posete)")
    void testPriceWithPartsAndNoDiscount() {
        double base = 10000.0;
        double parts = 5000.0;
        int pastVisits = 2; // Nema popust jer je < 3

        double total = PriceCalculator.calculateTotal(base, parts, pastVisits);
        assertEquals(15000.0, total, 0.001, "Ukupna cena treba da bude zbir rada i delova");
    }

    @Test
    @DisplayName("Test 6: Primena popusta za lojalne klijente (10% na cenu rada)")
    void testPriceWithLoyaltyDiscount() {
        double base = 10000.0;
        double parts = 5000.0;
        int pastVisits = 4; // Ima popust (10% na 10.000 = 1.000 popust) -> 9.000 + 5.000 = 14.000

        double total = PriceCalculator.calculateTotal(base, parts, pastVisits);
        assertEquals(14000.0, total, 0.001, "Ukupna cena treba da uključi 10% popusta na rad");
    }

    @Test
    @DisplayName("Test 7: Provera izuzetka za negativnu cenu")
    void testNegativePriceException() {
        assertThrows(IllegalArgumentException.class, () -> {
            PriceCalculator.calculateTotal(-500.0, 1000.0, 0);
        }, "Negativna cena mora baciti IllegalArgumentException");
    }
}
