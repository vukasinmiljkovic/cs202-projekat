# Auto Servis Management System – CS202 Projekat

Desktop aplikacija za rezervisanje termina i upravljanje poslovanjem auto-servisa, razvijena u programskom jeziku **Java** sa **JavaFX** grafičkim korisničkim interfejsom, **SQLite** relacionom bazom podataka i višenitnom **Klijent-Server** Socket arhitekturom.

---

## 📋 Sadržaj
1. [O Projektu](#-o-projektu)
2. [Korišćene Tehnologije](#-korišćene-tehnologije)
3. [Arhitektura Sistema](#-arhitektura-sistema)
4. [Baza Podataka](#-baza-podataka)
5. [Struktura Projekta](#-struktura-projekta)
6. [Uputstvo za Pokretanje](#-uputstvo-za-pokretanje)
7. [Kredencijali za Prijavu](#-kredencijali-za-prijavu)
8. [Dodatne Funkcionalnosti (Nivo 2)](#-dodatne-funkcionalnosti-nivo-2)
9. [Generički Koncepti](#-generički-koncepti-generics)
10. [Jsoup Web Scraping](#-jsoup-web-scraping)
11. [JUnit 5 Testiranje](#-junit-5-testiranje)
12. [JavaDoc Dokumentacija](#-javadoc-dokumentacija)

---

## 🚗 O Projektu
Aplikacija rešava realan poslovni problem vođenja auto-servisa:
- **Upravljanje klijentima i vozilima:** Evidencija vlasnika, brendova, modela, godišta i tablica.
- **Upravljanje zaposlenima i uslugama:** Cenovnik sa normativima rada i evidencija servisera po strukama.
- **Pametno zakazivanje termina:** Validacija slobodnih termina za servisere i vozila kako bi se sprečila preklapanja.
- **Automatski proračun troškova:** Kalkulacija cene rada, ugrađenih delova i odobravanje 10% popusta za lojalne klijente.
- **Poslovna analitika i grafikoni:** Vizuelni pregled realizacije kroz `PieChart` i `BarChart`.
- **Servisni saveti sa interneta:** Integracija `Jsoup` parsera za preuzimanje aktuelnih saveta o održavanju vozila.

---

## 🛠️ Korišćene Tehnologije
- **Programski jezik:** Java 21 (LTS)
- **Korisnički interfejs:** JavaFX 21 (FXML + SceneBuilder podrška + CSS stilizacija)
- **Baza podataka:** SQLite (JDBC Driver `sqlite-jdbc 3.45.1.0`)
- **Mrežni protokol:** TCP Java Sockets (Višenitni Client-Server model)
- **Serijalizacija:** Google Gson 2.10.1 (JSON format komunikacije)
- **Web Scraping:** Jsoup 1.17.2
- **Testiranje:** JUnit 5 (Jupiter API & Engine)
- **Build alat:** Apache Maven (ili samostalne `.bat` skripte)

---

## 🏛️ Arhitektura Sistema
Sistem koristi čistu dvoslojnu klijent-server arhitekturu:
1. **Server (`rs.autoservice.server`):**
   - Prihvata konekcije klijenata na TCP portu `8888`.
   - Svakog klijenta obrađuje u zasebnoj niti (`ClientHandler`).
   - Jedini komunicira sa SQLite bazom podataka preko DAO sloja (`CustomerDAO`, `VehicleDAO`, `EmployeeDAO`, `ServiceDAO`, `AppointmentDAO`, `UserDAO`).
   - Obrađuje poslovnu logiku i vraća strukturirane generičke JSON odgovore (`Response<T>`).
2. **Klijent (`rs.autoservice.client` / `rs.autoservice.controller`):**
   - JavaFX aplikacija koja nikada ne pristupa bazi direktno.
   - Šalje zahteve (`Request`) i reaguje na odgovore (`Response<T>`) osvežavanjem grafičkog prikaza.

---

## 💾 Baza Podataka (XAMPP MySQL & SQLite)
Aplikacija ima **punu podršku za XAMPP MySQL** kao i rezervnu podršku za SQLite.

### Rad sa XAMPP-om (MySQL & phpMyAdmin):
1. Otvorite **XAMPP Control Panel**.
2. Kliknite **Start** pored **MySQL** (i Apache-a ukoliko želite phpMyAdmin).
3. Pokrenite server aplikacije (`run-server.bat`).
4. Server će **automatski kreirati bazu `autoservice`**, sve tabele i uneti početne podatke!
5. Možete otvoriti svoj pretraživač na adresi `http://localhost/phpmyadmin` i videti kreiranu bazu `autoservice` sa svim tabelama i podacima.
6. SQL skripta se nalazi i u fajlu: [`database/autoservice.sql`](file:///c:/java%202%20projekat/database/autoservice.sql).

*(Napomena: Ukoliko XAMPP MySQL nije pokrenut, sistem automatski prelazi na lokalni SQLite fajl `database/autoservice.db` kako aplikacija nikada ne bi prestala sa radom!)*

Sadrži 6 povezanih tabela sa primarnim i stranim ključevima:
1. `users` (id, username, password, role, full_name)
2. `customers` (id, first_name, last_name, phone, email)
3. `vehicles` (id, customer_id FK, brand, model, year, license_plate)
4. `employees` (id, first_name, last_name, position, phone)
5. `services` (id, name, description, price, duration_minutes)
6. `appointments` (id, customer_id FK, vehicle_id FK, employee_id FK, service_id FK, appointment_date, appointment_time, status, notes, total_price)

---

## 🚀 Uputstvo za Pokretanje

### Način 1: Pomoću pripremljenih Windows `.bat` skripti (Najjednostavnije)
1. **Pokrenite Server:**
   - Dvoklikom pokrenite `run-server.bat`.
   - Server će inicijalizovati SQLite bazu i početi da sluša na portu `8888`.
2. **Pokrenite Klijent:**
   - Dvoklikom pokrenite `run-client.bat`.
   - Otvoriće se JavaFX Login ekran.
3. **Pokrenite JUnit Testove:**
   - Dvoklikom pokrenite `run-tests.bat` (izvršava svih 16 testova i prikazuje izveštaj).
4. **Generišite JavaDoc dokumentaciju:**
   - Dvoklikom pokrenite `generate-javadoc.bat` (dokumentacija se generiše u `javadoc/` folderu).

### Način 2: Preko Mavena / Razvojnog okruženja (IntelliJ IDEA / Eclipse / VS Code)
- Otvorite projekat kao Maven projekat (`pom.xml`).
- Za pokretanje servera: pokrenite klasu `rs.autoservice.server.AppServer`.
- Za pokretanje klijenta: pokrenite klasu `rs.autoservice.Launcher` ili `mvn javafx:run`.
- Za testove: `mvn test`.

---

## 🔐 Kredencijali za Prijavu
Prilikom pokretanja baze uneti su sledeći testni nalozi:
| Korisničko ime | Lozinka | Uloga | Opis |
|---|---|---|---|
| `admin` | `admin123` | **ADMIN** | Puni administratorski pristup sistemu |
| `radnik` | `radnik123` | **EMPLOYEE** | Serviser / Operater u radionici |

*(Na login ekranu postoje i dugmad za brzu prijavu jednim klikom)*

---

## ⭐ Dodatne Funkcionalnosti (Nivo 2)
1. **Prevencija preklapanja termina (`AppointmentService`):**
   - Prilikom zakazivanja proverava se da li je izabrani serviser slobodan u tom intervalu (uzimajući u obzir trajanje usluge).
   - Ukoliko postoji preklapanje, sistem prikazuje `Alert` upozorenje i sprečava unos.
2. **Kalkulator ukupne cene i lojalni popust (`PriceCalculator`):**
   - Računa osnovnu cenu usluge + cenu ugrađenih delova.
   - Klijentima koji imaju 3 ili više završenih poseta automatski obračunava 10% popusta na cenu rada.
3. **Statistika i analitika poslovanja (`StatisticsService`):**
   - Automatski računa ukupan prihod, broj realizovanih, zakazanih i otkazanih servisa, kao i procenat uspešnosti.
   - Prikazuje `PieChart` raspodelu statusa i `BarChart` popularnosti usluga.

---

## 🧬 Generički Koncepti (Generics)
1. **`Response<T>`:** Generička klasa koja uniformno prenosi podatke bilo kog tipa (`Customer`, `List<Appointment>`, `Map<String, Object>`) sa statusom operacije i porukom.
2. **`GenericFilter`:** Generičke pomoćne metode za univerzalno filtriranje (`filter`), tekstualnu pretragu (`search`) i prebrojavanje (`countMatching`) lista bilo kog modela.

---

## 🌐 Jsoup Web Scraping
Klasa `ScraperService` koristi **Jsoup** biblioteku za preuzimanje aktuelnih servisnih vesti i saveta sa javno dostupnog auto-portala.
- Prikazuje naslov, sažetak, kategoriju i link.
- Klikom na dugme otvara se originalni članak u pretraživaču računara.
- Sadrži ugrađeni *offline fallback* kako bi demonstracija radila čak i bez interneta na fakultetu.

---

## 🧪 JUnit 5 Testiranje
Implementirano je **16 JUnit 5 test metoda** u 5 test klasa:
- `ValidationUtilTest`: Validacija email-a, registarskih tablica i godišta vozila.
- `PriceCalculatorTest`: Testiranje osnovne cene, ugradnje delova, lojalnog popusta i provere izuzetaka.
- `AppointmentServiceTest`: Testiranje slobodnih termina, detekcije kolizija i ignorisanja otkazanih termina.
- `ResponseGenericTest`: Testiranje generičkog prenosa pojedinačnih objekata, kolekcija i grešaka.
- `GenericFilterTest`: Testiranje generičkog filtriranja, pretrage i brojanja.

---

## 📖 JavaDoc Dokumentacija
Sve klase, metode i konstruktori poseduju detaljne JavaDoc komentare.
Kompletna HTML dokumentacija generisana je u folderu:
📂 `javadoc/index.html`
