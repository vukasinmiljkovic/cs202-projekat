package rs.autoservice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rs.autoservice.util.ValidationUtil;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 testovi za proveru validacija unosa korisnika (email, tablice, godine).
 *
 * @author Vukasin Miljkovic
 */
public class ValidationUtilTest {

    @Test
    @DisplayName("Test 1: Validacija ispravnih i neispravnih email adresa")
    void testEmailValidation() {
        assertTrue(ValidationUtil.isValidEmail("marko.petrovic@gmail.com"), "Ispravan email treba da prođe validaciju");
        assertTrue(ValidationUtil.isValidEmail("korisnik123@yahoo.co.uk"), "Email sa poddomenom treba da prođe");
        
        assertFalse(ValidationUtil.isValidEmail("neispravan-email"), "Email bez @ mora biti odbijen");
        assertFalse(ValidationUtil.isValidEmail("korisnik@"), "Email bez domena mora biti odbijen");
        assertFalse(ValidationUtil.isValidEmail(""), "Prazan email mora biti odbijen");
        assertFalse(ValidationUtil.isValidEmail(null), "Null vrednost mora biti odbijena");
    }

    @Test
    @DisplayName("Test 2: Validacija formata registarskih tablica")
    void testLicensePlateValidation() {
        assertTrue(ValidationUtil.isValidLicensePlate("BG-123-AA"), "Format BG-123-AA mora biti ispravan");
        assertTrue(ValidationUtil.isValidLicensePlate("NS-4567-XY"), "Format sa 4 cifre mora biti ispravan");
        assertTrue(ValidationUtil.isValidLicensePlate("ni-789-cc"), "Mala slova treba da budu prihvaćena");

        assertFalse(ValidationUtil.isValidLicensePlate("123-BG-AA"), "Pogrešan redosled mora biti odbijen");
        assertFalse(ValidationUtil.isValidLicensePlate("BEOGRAD-1"), "Nestandardne oznake moraju biti odbijene");
        assertFalse(ValidationUtil.isValidLicensePlate(""), "Prazna tablica mora biti odbijena");
    }

    @Test
    @DisplayName("Test 3: Validacija godine proizvodnje vozila")
    void testYearValidation() {
        assertTrue(ValidationUtil.isValidYear(2018), "Godina 2018 je validna");
        assertTrue(ValidationUtil.isValidYear(2000), "Godina 2000 je validna");
        
        assertFalse(ValidationUtil.isValidYear(1850), "Godina pre pojave automobila (1850) mora biti nevalidna");
        assertFalse(ValidationUtil.isValidYear(2099), "Godina u dalekoj budućnosti mora biti nevalidna");
    }
}
