package rs.autoservice.service;

/**
 * Servisna klasa za izračunavanje ukupne cene servisa.
 * Podržava dodavanje cene ugrađenih delova i primenu popusta za lojalne klijente.
 * (Dodatna operacija 2 za Nivo 2).
 *
 * @author Vukasin Miljkovic
 */
public class PriceCalculator {

    /** Procenat popusta za stalne klijente (10%) */
    public static final double LOYALTY_DISCOUNT_PERCENT = 0.10;

    /** Minimalan broj prethodnih poseta za ostvarivanje popusta */
    public static final int LOYALTY_THRESHOLD_VISITS = 3;

    /**
     * Računa ukupnu cenu servisa.
     *
     * @param baseServicePrice     Osnovna cena rada/usluge
     * @param additionalPartsPrice Cena ugrađenih delova i materijala
     * @param pastVisitsCount      Broj prethodnih završenih poseta klijenta
     * @return Ukupan iznos za naplatu (zaokružen na 2 decimale)
     */
    public static double calculateTotal(double baseServicePrice, double additionalPartsPrice, int pastVisitsCount) {
        if (baseServicePrice < 0) {
            throw new IllegalArgumentException("Osnovna cena usluge ne može biti negativna!");
        }
        if (additionalPartsPrice < 0) {
            additionalPartsPrice = 0;
        }

        double subtotal = baseServicePrice + additionalPartsPrice;

        // Ako klijent ima 3 ili više poseta, odobrava se popust na rad
        if (pastVisitsCount >= LOYALTY_THRESHOLD_VISITS) {
            double discount = baseServicePrice * LOYALTY_DISCOUNT_PERCENT;
            subtotal -= discount;
        }

        return Math.round(subtotal * 100.0) / 100.0;
    }

    /**
     * Pomoćna preopterećena metoda bez uračunavanja delova i istorije.
     *
     * @param baseServicePrice Osnovna cena usluge
     * @return Iznos
     */
    public static double calculateTotal(double baseServicePrice) {
        return calculateTotal(baseServicePrice, 0.0, 0);
    }
}
