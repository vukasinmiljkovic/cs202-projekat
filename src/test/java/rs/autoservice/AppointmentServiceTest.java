package rs.autoservice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rs.autoservice.model.Appointment;
import rs.autoservice.service.AppointmentService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 testovi za poslovnu logiku detekcije preklapanja termina (Collision Prevention).
 *
 * @author Vukasin Miljkovic
 */
public class AppointmentServiceTest {

    @Test
    @DisplayName("Test 8: Zaposleni je slobodan kada nema zakazanih termina u to vreme")
    void testEmployeeAvailableWhenNoConflict() {
        List<Appointment> existing = new ArrayList<>();
        // Postojeći termin u 09:00 (traje do 10:00)
        existing.add(new Appointment(1, 1, 1, 1, 1, "2026-08-25", "09:00", "ZAKAZANO", "", 5000.0));

        // Pokušavamo da zakažemo novi termin u 11:00 za 60 minuta (nema konflikta)
        boolean available = AppointmentService.isEmployeeAvailable(1, "2026-08-25", "11:00", 60, 0, existing);
        assertTrue(available, "Zaposleni bi trebalo da bude slobodan u 11:00");
    }

    @Test
    @DisplayName("Test 9: Detekcija preklapanja kada je termin u isto vreme")
    void testConflictDetectedSameTime() {
        List<Appointment> existing = new ArrayList<>();
        // Postojeći termin za radnika 1 od 10:00 do 11:00
        existing.add(new Appointment(1, 1, 1, 1, 1, "2026-08-25", "10:00", "ZAKAZANO", "", 5000.0));

        // Pokušaj zakazivanja u 10:30 za 60 minuta za istog radnika
        boolean available = AppointmentService.isEmployeeAvailable(1, "2026-08-25", "10:30", 60, 0, existing);
        assertFalse(available, "Sistem mora da detektuje preklapanje u 10:30 jer termin traje od 10:00 do 11:00");
    }

    @Test
    @DisplayName("Test 10: Otkazani termini ne smeju blokirati nove termine")
    void testCancelledAppointmentDoesNotBlock() {
        List<Appointment> existing = new ArrayList<>();
        // Otkazani termin u 14:00
        existing.add(new Appointment(2, 1, 1, 1, 1, "2026-08-25", "14:00", "OTKAZANO", "Otkazano", 5000.0));

        // Pokušaj zakazivanja u 14:00
        boolean available = AppointmentService.isEmployeeAvailable(1, "2026-08-25", "14:00", 60, 0, existing);
        assertTrue(available, "Otkazani termin ne sme sprečiti novi unos u isto vreme");
    }
}
