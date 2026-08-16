package rs.autoservice.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import rs.autoservice.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Klijentska mrežna komponenta zadužena za Socket komunikaciju sa backend serverom.
 * Šalje {@link Request} zahteve i prima tipizirane {@link Response} odgovore.
 *
 * @author Vukasin Miljkovic
 */
public class NetworkClient {

    private static NetworkClient instance;

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private final Gson gson = new Gson();

    private User currentUser;

    private NetworkClient() {
    }

    public static synchronized NetworkClient getInstance() {
        if (instance == null) {
            instance = new NetworkClient();
        }
        return instance;
    }

    /**
     * Otvara vezu sa serverom.
     */
    public synchronized boolean connect(String host, int port) {
        try {
            if (socket != null && !socket.isClosed()) {
                return true;
            }
            socket = new Socket(host, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            return true;
        } catch (IOException e) {
            System.err.println("[NetworkClient] Nije moguće uspostaviti konekciju sa serverom: " + e.getMessage());
            return false;
        }
    }

    /**
     * Proverava da li je klijent trenutno povezan na server.
     */
    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    /**
     * Zatvara konekciju sa serverom.
     */
    public synchronized void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ignored) {
        } finally {
            socket = null;
            reader = null;
            writer = null;
        }
    }

    /**
     * Šalje generički zahtev serveru i čeka odgovor.
     */
    private synchronized String sendRequest(Request request) throws IOException {
        if (!isConnected()) {
            if (!connect("localhost", 8888)) {
                throw new IOException("Nema veze sa serverom. Proverite da li je server pokrenut!");
            }
        }
        String jsonOut = gson.toJson(request);
        writer.println(jsonOut);

        String jsonIn = reader.readLine();
        if (jsonIn == null) {
            throw new IOException("Server je prekinuo vezu.");
        }
        return jsonIn;
    }

    // ==========================================
    //   AUTENTIFIKACIJA
    // ==========================================

    public Response<User> login(String username, String password) {
        try {
            User credentials = new User(username, password, "", "");
            Request req = new Request("LOGIN", gson.toJson(credentials));
            String rawJson = sendRequest(req);

            Type type = new TypeToken<Response<User>>() {}.getType();
            Response<User> resp = gson.fromJson(rawJson, type);
            if (resp.isSuccess()) {
                this.currentUser = resp.getData();
            }
            return resp;
        } catch (Exception e) {
            return Response.error("Greška pri povezivanju sa serverom: " + e.getMessage());
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    // ==========================================
    //   CUSTOMERS
    // ==========================================

    public Response<List<Customer>> getCustomers() {
        try {
            Request req = new Request("GET_CUSTOMERS");
            String rawJson = sendRequest(req);
            Type type = new TypeToken<Response<List<Customer>>>() {}.getType();
            return gson.fromJson(rawJson, type);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response<Customer> addCustomer(Customer c) {
        try {
            Request req = new Request("ADD_CUSTOMER", gson.toJson(c));
            String rawJson = sendRequest(req);
            Type type = new TypeToken<Response<Customer>>() {}.getType();
            return gson.fromJson(rawJson, type);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response<Void> updateCustomer(Customer c) {
        try {
            Request req = new Request("UPDATE_CUSTOMER", gson.toJson(c));
            String rawJson = sendRequest(req);
            Type type = new TypeToken<Response<Void>>() {}.getType();
            return gson.fromJson(rawJson, type);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response<Void> deleteCustomer(int id) {
        try {
            Request req = new Request("DELETE_CUSTOMER", String.valueOf(id));
            String rawJson = sendRequest(req);
            Type type = new TypeToken<Response<Void>>() {}.getType();
            return gson.fromJson(rawJson, type);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    // ==========================================
    //   VEHICLES
    // ==========================================

    public Response<List<Vehicle>> getVehicles() {
        try {
            Request req = new Request("GET_VEHICLES");
            String rawJson = sendRequest(req);
            Type type = new TypeToken<Response<List<Vehicle>>>() {}.getType();
            return gson.fromJson(rawJson, type);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response<Vehicle> addVehicle(Vehicle v) {
        try {
            Request req = new Request("ADD_VEHICLE", gson.toJson(v));
            String rawJson = sendRequest(req);
            Type type = new TypeToken<Response<Vehicle>>() {}.getType();
            return gson.fromJson(rawJson, type);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response<Void> updateVehicle(Vehicle v) {
        try {
            Request req = new Request("UPDATE_VEHICLE", gson.toJson(v));
            String rawJson = sendRequest(req);
            Type type = new TypeToken<Response<Void>>() {}.getType();
            return gson.fromJson(rawJson, type);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response<Void> deleteVehicle(int id) {
        try {
            Request req = new Request("DELETE_VEHICLE", String.valueOf(id));
            String rawJson = sendRequest(req);
            Type type = new TypeToken<Response<Void>>() {}.getType();
            return gson.fromJson(rawJson, type);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    // ==========================================
    //   EMPLOYEES
    // ==========================================

    public Response<List<Employee>> getEmployees() {
        try {
            Request req = new Request("GET_EMPLOYEES");
            String rawJson = sendRequest(req);
            Type type = new TypeToken<Response<List<Employee>>>() {}.getType();
            return gson.fromJson(rawJson, type);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response<Employee> addEmployee(Employee emp) {
        try {
            Request req = new Request("ADD_EMPLOYEE", gson.toJson(emp));
            String rawJson = sendRequest(req);
            Type type = new TypeToken<Response<Employee>>() {}.getType();
            return gson.fromJson(rawJson, type);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response<Void> updateEmployee(Employee emp) {
        try {
            Request req = new Request("UPDATE_EMPLOYEE", gson.toJson(emp));
            String rawJson = sendRequest(req);
            Type type = new TypeToken<Response<Void>>() {}.getType();
            return gson.fromJson(rawJson, type);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response<Void> deleteEmployee(int id) {
        try {
            Request req = new Request("DELETE_EMPLOYEE", String.valueOf(id));
            String rawJson = sendRequest(req);
            Type type = new TypeToken<Response<Void>>() {}.getType();
            return gson.fromJson(rawJson, type);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    // ==========================================
    //   SERVICES
    // ==========================================

    public Response<List<ServiceItem>> getServices() {
        try {
            Request req = new Request("GET_SERVICES");
            String rawJson = sendRequest(req);
            Type type = new TypeToken<Response<List<ServiceItem>>>() {}.getType();
            return gson.fromJson(rawJson, type);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response<ServiceItem> addService(ServiceItem s) {
        try {
            Request req = new Request("ADD_SERVICE", gson.toJson(s));
            String rawJson = sendRequest(req);
            Type type = new TypeToken<Response<ServiceItem>>() {}.getType();
            return gson.fromJson(rawJson, type);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response<Void> updateService(ServiceItem s) {
        try {
            Request req = new Request("UPDATE_SERVICE", gson.toJson(s));
            String rawJson = sendRequest(req);
            Type type = new TypeToken<Response<Void>>() {}.getType();
            return gson.fromJson(rawJson, type);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response<Void> deleteService(int id) {
        try {
            Request req = new Request("DELETE_SERVICE", String.valueOf(id));
            String rawJson = sendRequest(req);
            Type type = new TypeToken<Response<Void>>() {}.getType();
            return gson.fromJson(rawJson, type);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    // ==========================================
    //   APPOINTMENTS
    // ==========================================

    public Response<List<Appointment>> getAppointments() {
        try {
            Request req = new Request("GET_APPOINTMENTS");
            String rawJson = sendRequest(req);
            Type type = new TypeToken<Response<List<Appointment>>>() {}.getType();
            return gson.fromJson(rawJson, type);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response<Appointment> addAppointment(Appointment app) {
        try {
            Request req = new Request("ADD_APPOINTMENT", gson.toJson(app));
            String rawJson = sendRequest(req);
            Type type = new TypeToken<Response<Appointment>>() {}.getType();
            return gson.fromJson(rawJson, type);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response<Void> updateAppointment(Appointment app) {
        try {
            Request req = new Request("UPDATE_APPOINTMENT", gson.toJson(app));
            String rawJson = sendRequest(req);
            Type type = new TypeToken<Response<Void>>() {}.getType();
            return gson.fromJson(rawJson, type);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response<Void> updateAppointmentStatus(int id, String status) {
        try {
            Request req = new Request("UPDATE_APPOINTMENT_STATUS", id + ":" + status);
            String rawJson = sendRequest(req);
            Type type = new TypeToken<Response<Void>>() {}.getType();
            return gson.fromJson(rawJson, type);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response<Void> deleteAppointment(int id) {
        try {
            Request req = new Request("DELETE_APPOINTMENT", String.valueOf(id));
            String rawJson = sendRequest(req);
            Type type = new TypeToken<Response<Void>>() {}.getType();
            return gson.fromJson(rawJson, type);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    // ==========================================
    //   STATISTICS & NEWS
    // ==========================================

    public Response<Map<String, Object>> getStatistics() {
        try {
            Request req = new Request("GET_STATISTICS");
            String rawJson = sendRequest(req);
            Type type = new TypeToken<Response<Map<String, Object>>>() {}.getType();
            return gson.fromJson(rawJson, type);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    public Response<List<NewsArticle>> getNews() {
        try {
            Request req = new Request("GET_NEWS");
            String rawJson = sendRequest(req);
            Type type = new TypeToken<Response<List<NewsArticle>>>() {}.getType();
            return gson.fromJson(rawJson, type);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }
}
