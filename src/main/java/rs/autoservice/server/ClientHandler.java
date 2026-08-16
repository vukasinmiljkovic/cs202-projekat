package rs.autoservice.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import rs.autoservice.dao.*;
import rs.autoservice.model.*;
import rs.autoservice.service.AppointmentService;
import rs.autoservice.service.ScraperService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Obradnik pojedinačne klijentske konekcije na serveru.
 * Pokreće se u zasebnoj niti (Thread) i obrađuje zahteve klijenta.
 *
 * @author Vukasin Miljkovic
 */
public class ClientHandler implements Runnable {

    private final Socket socket;
    private final Gson gson = new Gson();

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final ServiceDAO serviceDAO = new ServiceDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final UserDAO userDAO = new UserDAO();

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println("[Server] Novi klijent povezan sa adrese: " + socket.getRemoteSocketAddress());
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                Request request = gson.fromJson(line, Request.class);
                Response<?> response = processRequest(request);

                String jsonResponse = gson.toJson(response);
                writer.println(jsonResponse);
            }

        } catch (IOException e) {
            System.out.println("[Server] Klijent se diskonektovao: " + socket.getRemoteSocketAddress());
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * Obrađuje primljeni mrežni zahtev i poziva odgovarajući DAO ili servis.
     */
    private Response<?> processRequest(Request request) {
        String action = request.getAction();
        String payload = request.getPayload();

        try {
            switch (action) {
                // --- Autentifikacija ---
                case "LOGIN": {
                    User creds = gson.fromJson(payload, User.class);
                    User user = userDAO.login(creds.getUsername(), creds.getPassword());
                    if (user != null) {
                        return Response.ok("Uspešna prijava!", user);
                    } else {
                        return Response.error("Pogrešno korisničko ime ili lozinka.");
                    }
                }

                // --- Customers CRUD ---
                case "GET_CUSTOMERS": {
                    List<Customer> customers = customerDAO.getAll();
                    return Response.ok(customers);
                }
                case "ADD_CUSTOMER": {
                    Customer c = gson.fromJson(payload, Customer.class);
                    int id = customerDAO.insert(c);
                    return id > 0 ? Response.ok("Klijent uspešno dodat!", c) : Response.error("Greška pri unosu klijenta.");
                }
                case "UPDATE_CUSTOMER": {
                    Customer c = gson.fromJson(payload, Customer.class);
                    boolean ok = customerDAO.update(c);
                    return ok ? Response.ok("Podaci klijenta ažurirani.", null) : Response.error("Greška pri ažuriranju klijenta.");
                }
                case "DELETE_CUSTOMER": {
                    int id = Integer.parseInt(payload);
                    boolean ok = customerDAO.delete(id);
                    return ok ? Response.ok("Klijent uspešno obrisan.", null) : Response.error("Greška pri brisanju klijenta.");
                }

                // --- Vehicles CRUD ---
                case "GET_VEHICLES": {
                    List<Vehicle> vehicles = vehicleDAO.getAll();
                    return Response.ok(vehicles);
                }
                case "ADD_VEHICLE": {
                    Vehicle v = gson.fromJson(payload, Vehicle.class);
                    int id = vehicleDAO.insert(v);
                    return id > 0 ? Response.ok("Vozilo uspešno dodato!", v) : Response.error("Greška pri unosu vozila.");
                }
                case "UPDATE_VEHICLE": {
                    Vehicle v = gson.fromJson(payload, Vehicle.class);
                    boolean ok = vehicleDAO.update(v);
                    return ok ? Response.ok("Podaci o vozilu ažurirani.", null) : Response.error("Greška pri ažuriranju vozila.");
                }
                case "DELETE_VEHICLE": {
                    int id = Integer.parseInt(payload);
                    boolean ok = vehicleDAO.delete(id);
                    return ok ? Response.ok("Vozilo uspešno obrisano.", null) : Response.error("Greška pri brisanju vozila.");
                }

                // --- Employees CRUD ---
                case "GET_EMPLOYEES": {
                    List<Employee> employees = employeeDAO.getAll();
                    return Response.ok(employees);
                }
                case "ADD_EMPLOYEE": {
                    Employee emp = gson.fromJson(payload, Employee.class);
                    int id = employeeDAO.insert(emp);
                    return id > 0 ? Response.ok("Zaposleni uspešno dodat!", emp) : Response.error("Greška pri unosu zaposlenog.");
                }
                case "UPDATE_EMPLOYEE": {
                    Employee emp = gson.fromJson(payload, Employee.class);
                    boolean ok = employeeDAO.update(emp);
                    return ok ? Response.ok("Podaci o zaposlenom ažurirani.", null) : Response.error("Greška pri ažuriranju zaposlenog.");
                }
                case "DELETE_EMPLOYEE": {
                    int id = Integer.parseInt(payload);
                    boolean ok = employeeDAO.delete(id);
                    return ok ? Response.ok("Zaposleni uspešno obrisan.", null) : Response.error("Greška pri brisanju zaposlenog.");
                }

                // --- Services CRUD ---
                case "GET_SERVICES": {
                    List<ServiceItem> services = serviceDAO.getAll();
                    return Response.ok(services);
                }
                case "ADD_SERVICE": {
                    ServiceItem s = gson.fromJson(payload, ServiceItem.class);
                    int id = serviceDAO.insert(s);
                    return id > 0 ? Response.ok("Usluga uspešno dodata!", s) : Response.error("Greška pri unosu usluge.");
                }
                case "UPDATE_SERVICE": {
                    ServiceItem s = gson.fromJson(payload, ServiceItem.class);
                    boolean ok = serviceDAO.update(s);
                    return ok ? Response.ok("Podaci o usluzi ažurirani.", null) : Response.error("Greška pri ažuriranju usluge.");
                }
                case "DELETE_SERVICE": {
                    int id = Integer.parseInt(payload);
                    boolean ok = serviceDAO.delete(id);
                    return ok ? Response.ok("Usluga uspešno obrisana.", null) : Response.error("Greška pri brisanju usluge.");
                }

                // --- Appointments CRUD & Collision Check ---
                case "GET_APPOINTMENTS": {
                    List<Appointment> appointments = appointmentDAO.getAll();
                    return Response.ok(appointments);
                }
                case "ADD_APPOINTMENT": {
                    Appointment app = gson.fromJson(payload, Appointment.class);

                    // Provera preklapanja termina za zaposlenog radnika
                    List<Appointment> existing = appointmentDAO.getByEmployeeAndDate(app.getEmployeeId(), app.getAppointmentDate());
                    ServiceItem serviceItem = serviceDAO.getById(app.getServiceId());
                    int duration = (serviceItem != null) ? serviceItem.getDurationMinutes() : 60;

                    boolean available = AppointmentService.isEmployeeAvailable(
                            app.getEmployeeId(),
                            app.getAppointmentDate(),
                            app.getAppointmentTime(),
                            duration,
                            0,
                            existing
                    );

                    if (!available) {
                        return Response.error("Izabrani serviser već ima zakazan termin u navedeno vreme! Izaberite drugi termin ili drugog servisera.");
                    }

                    int id = appointmentDAO.insert(app);
                    return id > 0 ? Response.ok("Termin uspešno zakazan!", app) : Response.error("Greška pri zakazivanju termina.");
                }
                case "UPDATE_APPOINTMENT": {
                    Appointment app = gson.fromJson(payload, Appointment.class);

                    // Provera preklapanja termina pri izmeni
                    List<Appointment> existing = appointmentDAO.getByEmployeeAndDate(app.getEmployeeId(), app.getAppointmentDate());
                    ServiceItem serviceItem = serviceDAO.getById(app.getServiceId());
                    int duration = (serviceItem != null) ? serviceItem.getDurationMinutes() : 60;

                    boolean available = AppointmentService.isEmployeeAvailable(
                            app.getEmployeeId(),
                            app.getAppointmentDate(),
                            app.getAppointmentTime(),
                            duration,
                            app.getId(),
                            existing
                    );

                    if (!available) {
                        return Response.error("Izabrani serviser već ima zakazan termin u navedeno vreme!");
                    }

                    boolean ok = appointmentDAO.update(app);
                    return ok ? Response.ok("Termin uspešno ažuriran.", null) : Response.error("Greška pri ažuriranju termina.");
                }
                case "UPDATE_APPOINTMENT_STATUS": {
                    // payload format: "id:STATUS"
                    String[] parts = payload.split(":");
                    int id = Integer.parseInt(parts[0]);
                    String newStatus = parts[1];
                    boolean ok = appointmentDAO.updateStatus(id, newStatus);
                    return ok ? Response.ok("Status termina ažuriran.", null) : Response.error("Greška pri ažuriranju statusa.");
                }
                case "DELETE_APPOINTMENT": {
                    int id = Integer.parseInt(payload);
                    boolean ok = appointmentDAO.delete(id);
                    return ok ? Response.ok("Termin uspešno obrisan.", null) : Response.error("Greška pri brisanju termina.");
                }

                // --- Statistics ---
                case "GET_STATISTICS": {
                    Map<String, Object> stats = appointmentDAO.getStatistics();
                    return Response.ok(stats);
                }

                // --- Jsoup Scraped News ---
                case "GET_NEWS": {
                    List<NewsArticle> articles = ScraperService.fetchArticles();
                    return Response.ok(articles);
                }

                default:
                    return Response.error("Nepoznata serverska akcija: " + action);
            }

        } catch (SQLException e) {
            System.err.println("[Server] SQL Greška za akciju " + action + ": " + e.getMessage());
            return Response.error("Greška baze podataka: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("[Server] Sistemska greška za akciju " + action + ": " + e.getMessage());
            return Response.error("Greška na serveru: " + e.getMessage());
        }
    }
}
