package rs.autoservice.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Generička pomoćna klasa koja demonstrira upotrebu generičkih metoda u programskom jeziku Java.
 * Omogućava univerzalno filtriranje i pretragu listi bilo kojih objekata (Customer, Vehicle, Appointment itd.).
 *
 * @author Vukasin Miljkovic
 */
public class GenericFilter {

    /**
     * Generička metoda za filtriranje bilo koje liste na osnovu prosleđenog predikata (uslova).
     *
     * @param <T>       Tip elemenata u listi
     * @param items     Izvorna lista elemenata
     * @param condition Uslov koji elementi moraju da zadovolje
     * @return Nova lista koja sadrži samo elemente koji ispunjavaju uslov
     */
    public static <T> List<T> filter(List<T> items, Predicate<T> condition) {
        List<T> result = new ArrayList<>();
        if (items == null || condition == null) {
            return result;
        }
        for (T item : items) {
            if (condition.test(item)) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * Generička metoda za tekstualnu pretragu po određenom svojstvu objekta.
     *
     * @param <T>           Tip objekta u listi
     * @param items         Izvorna lista
     * @param textExtractor Funkcija koja iz objekta izvlači String za poređenje
     * @param query         Tekst koji se traži (case-insensitive)
     * @return Lista pronađenih elemenata
     */
    public static <T> List<T> search(List<T> items, Function<T, String> textExtractor, String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>(items);
        }
        String lowerQuery = query.trim().toLowerCase();
        return filter(items, item -> {
            String text = textExtractor.apply(item);
            return text != null && text.toLowerCase().contains(lowerQuery);
        });
    }

    /**
     * Generička metoda koja broji koliko elemenata u listi zadovoljava zadati uslov.
     *
     * @param <T>       Tip elemenata
     * @param items     Izvorna lista
     * @param condition Uslov
     * @return Broj elemenata koji ispunjavaju uslov
     */
    public static <T> int countMatching(List<T> items, Predicate<T> condition) {
        if (items == null || condition == null) {
            return 0;
        }
        int count = 0;
        for (T item : items) {
            if (condition.test(item)) {
                count++;
            }
        }
        return count;
    }
}
