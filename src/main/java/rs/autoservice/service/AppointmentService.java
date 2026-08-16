package rs.autoservice.service;

import rs.autoservice.model.Appointment;

import java.util.List;

/**
 * Poslovna logika za proveru dostupnosti termina i sprečavanje preklapanja.
 * (Dodatna operacija 1 za Nivo 2).
 *
 * @author Vukasin Miljkovic
 */
public class AppointmentService {

    /**
     * Proverava da li postoji vremensko preklapanje za izabranog zaposlenog na zadati datum i vreme.
     *
     * @param employeeId            ID izabranog zaposlenog
     * @param date                  Datum servisa (YYYY-MM-DD)
     * @param timeStr               Vreme početka (HH:mm)
     * @param durationMinutes       Trajanje nove usluge u minutima
     * @param currentAppointmentId  ID termina koji se menja (ili 0/negativan za novi termin)
     * @param existingAppointments  Lista postojećih termina zaposlenog za taj dan
     * @return true ako je zaposleni slobodan (nema preklapanja), false ako postoji konflikt
     */
    public static boolean isEmployeeAvailable(int employeeId, String date, String timeStr,
                                              int durationMinutes, int currentAppointmentId,
                                              List<Appointment> existingAppointments) {
        int newStart = parseTimeToMinutes(timeStr);
        int newEnd = newStart + durationMinutes;

        if (newStart < 0) {
            return false;
        }

        for (Appointment existing : existingAppointments) {
            // Ignorišemo otkazane termine i termin koji trenutno ažuriramo
            if ("OTKAZANO".equalsIgnoreCase(existing.getStatus()) || existing.getId() == currentAppointmentId) {
                continue;
            }

            if (existing.getEmployeeId() == employeeId && date.equals(existing.getAppointmentDate())) {
                int existingStart = parseTimeToMinutes(existing.getAppointmentTime());
                // Podrazumevano trajanje ako nije drugačije definisano je 60 minuta
                int existingDuration = 60;
                int existingEnd = existingStart + existingDuration;

                // Provera preklapanja intervala: [newStart, newEnd) i [existingStart, existingEnd)
                if (newStart < existingEnd && existingStart < newEnd) {
                    return false; // Postoji preklapanje!
                }
            }
        }
        return true;
    }

    /**
     * Pomoćna metoda za konverziju vremena "HH:mm" u ukupan broj minuta od početka dana.
     *
     * @param timeStr Vreme u formatu "HH:mm"
     * @return Broj minuta od ponoći, ili -1 ako je format neispravan
     */
    public static int parseTimeToMinutes(String timeStr) {
        if (timeStr == null || !timeStr.contains(":")) {
            return -1;
        }
        try {
            String[] parts = timeStr.trim().split(":");
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            if (hours < 0 || hours > 23 || minutes < 0 || minutes > 59) {
                return -1;
            }
            return hours * 60 + minutes;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
