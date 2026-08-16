package rs.autoservice.service;

import java.util.Map;

/**
 * Servisna klasa za obradu i pripremu statističkih pokazatelja poslovanja auto-servisa.
 * (Dodatna operacija 3 za Nivo 2).
 *
 * @author Vukasin Miljkovic
 */
public class StatisticsService {

    /**
     * Izračunava procenat uspešno realizovanih termina u odnosu na ukupan broj.
     *
     * @param completedCount Broj završenih termina
     * @param totalCount     Ukupan broj termina
     * @return Procenat uspešnosti (0.0 do 100.0)
     */
    public static double calculateCompletionRate(int completedCount, int totalCount) {
        if (totalCount <= 0) {
            return 0.0;
        }
        double rate = ((double) completedCount / totalCount) * 100.0;
        return Math.round(rate * 10.0) / 10.0;
    }

    /**
     * Pronalazi najtraženiju uslugu na osnovu mape učestalosti.
     *
     * @param serviceCounts Mapa naziva usluga i broja njihovog izvršavanja
     * @return Naziv najpopularnije usluge ili "Nema podataka"
     */
    public static String getMostPopularService(Map<String, Integer> serviceCounts) {
        if (serviceCounts == null || serviceCounts.isEmpty()) {
            return "Nema podataka";
        }

        String bestService = "Nema podataka";
        int maxCount = -1;

        for (Map.Entry<String, Integer> entry : serviceCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                bestService = entry.getKey() + " (" + maxCount + "x)";
            }
        }
        return bestService;
    }
}
