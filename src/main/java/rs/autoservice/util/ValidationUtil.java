package rs.autoservice.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Pomoćna klasa za validaciju korisničkih unosa (email, telefon, tablice, cene, datumi).
 *
 * @author Vukasin Miljkovic
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^[+]?[0-9/\\s-]{6,20}$");

    private static final Pattern LICENSE_PLATE_PATTERN =
            Pattern.compile("^[A-ZŠĐČĆŽ]{2}-[0-9]{3,5}-[A-ZŠĐČĆŽ]{2}$", Pattern.CASE_INSENSITIVE);

    /**
     * Proverava da li je prosleđeni string neprazan (nije null i nije samo whitespace).
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    /**
     * Proverava ispravnost email adrese.
     */
    public static boolean isValidEmail(String email) {
        if (!isNotEmpty(email)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Proverava ispravnost broja telefona.
     */
    public static boolean isValidPhone(String phone) {
        if (!isNotEmpty(phone)) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * Proverava format registarske tablice (npr. BG-123-AA ili NS-1234-XY).
     */
    public static boolean isValidLicensePlate(String plate) {
        if (!isNotEmpty(plate)) {
            return false;
        }
        return LICENSE_PLATE_PATTERN.matcher(plate.trim().toUpperCase()).matches();
    }

    /**
     * Proverava godinu proizvodnje vozila (od 1900. do sledeće godine).
     */
    public static boolean isValidYear(int year) {
        int currentYear = LocalDate.now().getYear();
        return year >= 1900 && year <= (currentYear + 1);
    }

    /**
     * Proverava da li je broj pozitivan.
     */
    public static boolean isPositiveNumber(double number) {
        return number > 0;
    }

    /**
     * Proverava da li je datum u validnom formatu (YYYY-MM-DD) i da nije u prošlosti.
     */
    public static boolean isValidFutureOrPresentDate(String dateStr) {
        if (!isNotEmpty(dateStr)) {
            return false;
        }
        try {
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            return !date.isBefore(LocalDate.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
