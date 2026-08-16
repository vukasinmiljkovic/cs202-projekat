-- =====================================================================
-- AUTO SERVIS MANAGEMENT SYSTEM (CS202)
-- MySQL BAZA PODATAKA ZA XAMPP (phpMyAdmin)
-- =====================================================================

CREATE DATABASE IF NOT EXISTS `autoservice` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `autoservice`;

-- 1. Tabela korisničkih naloga za Login
CREATE TABLE IF NOT EXISTS `users` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `role` VARCHAR(20) NOT NULL,
    `full_name` VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Tabela klijenata
CREATE TABLE IF NOT EXISTS `customers` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `first_name` VARCHAR(50) NOT NULL,
    `last_name` VARCHAR(50) NOT NULL,
    `phone` VARCHAR(30) NOT NULL,
    `email` VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Tabela vozila
CREATE TABLE IF NOT EXISTS `vehicles` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `customer_id` INT NOT NULL,
    `brand` VARCHAR(50) NOT NULL,
    `model` VARCHAR(50) NOT NULL,
    `year` INT NOT NULL,
    `license_plate` VARCHAR(20) NOT NULL,
    CONSTRAINT `fk_vehicles_customer` FOREIGN KEY (`customer_id`) 
        REFERENCES `customers` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Tabela zaposlenih
CREATE TABLE IF NOT EXISTS `employees` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `first_name` VARCHAR(50) NOT NULL,
    `last_name` VARCHAR(50) NOT NULL,
    `position` VARCHAR(100) NOT NULL,
    `phone` VARCHAR(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Tabela servisnih usluga
CREATE TABLE IF NOT EXISTS `services` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL,
    `description` TEXT,
    `price` DOUBLE NOT NULL,
    `duration_minutes` INT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. Tabela zakazanih termina
CREATE TABLE IF NOT EXISTS `appointments` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `customer_id` INT NOT NULL,
    `vehicle_id` INT NOT NULL,
    `employee_id` INT NOT NULL,
    `service_id` INT NOT NULL,
    `appointment_date` VARCHAR(20) NOT NULL,
    `appointment_time` VARCHAR(10) NOT NULL,
    `status` VARCHAR(20) NOT NULL DEFAULT 'ZAKAZANO',
    `notes` TEXT,
    `total_price` DOUBLE NOT NULL,
    CONSTRAINT `fk_appointments_customer` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`),
    CONSTRAINT `fk_appointments_vehicle` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles` (`id`),
    CONSTRAINT `fk_appointments_employee` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`id`),
    CONSTRAINT `fk_appointments_service` FOREIGN KEY (`service_id`) REFERENCES `services` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================================
-- TESTNI PODACI (SEED DATA)
-- =====================================================================

-- 1. Korisnici
INSERT INTO `users` (`username`, `password`, `role`, `full_name`) VALUES
('admin', 'admin123', 'ADMIN', 'Administrator Sistema'),
('radnik', 'radnik123', 'EMPLOYEE', 'Petar Marković')
ON DUPLICATE KEY UPDATE `username`=`username`;

-- 2. Klijenti
INSERT INTO `customers` (`id`, `first_name`, `last_name`, `phone`, `email`) VALUES
(1, 'Marko', 'Petrović', '+381641112233', 'marko.petrovic@gmail.com'),
(2, 'Nikola', 'Jovanović', '+381652223344', 'nikola.jovanovic@yahoo.com'),
(3, 'Ana', 'Ilić', '+381633334455', 'ana.ilic@gmail.com'),
(4, 'Stefan', 'Popović', '+381604445566', 'stefan.popovic@outlook.com'),
(5, 'Jelena', 'Nikolić', '+381615556677', 'jelena.nikolic@gmail.com')
ON DUPLICATE KEY UPDATE `id`=`id`;

-- 3. Vozila
INSERT INTO `vehicles` (`id`, `customer_id`, `brand`, `model`, `year`, `license_plate`) VALUES
(1, 1, 'Volkswagen', 'Golf 7 2.0 TDI', 2017, 'BG-123-AA'),
(2, 2, 'BMW', '320d xDrive', 2019, 'NS-456-BB'),
(3, 3, 'Audi', 'A4 2.0 TFSI', 2018, 'NI-789-CC'),
(4, 4, 'Škoda', 'Octavia 1.6 TDI', 2020, 'KG-321-DD'),
(5, 5, 'Renault', 'Megane 1.5 dCi', 2016, 'BG-654-EE')
ON DUPLICATE KEY UPDATE `id`=`id`;

-- 4. Zaposleni
INSERT INTO `employees` (`id`, `first_name`, `last_name`, `position`, `phone`) VALUES
(1, 'Petar', 'Marković', 'Glavni automehaničar', '+381648881122'),
(2, 'Milan', 'Đorđević', 'Auto-električar', '+381658882233'),
(3, 'Dejan', 'Stanković', 'Dijagnostičar', '+381638883344'),
(4, 'Nemanja', 'Simić', 'Vulkanizer i trap', '+381628884455')
ON DUPLICATE KEY UPDATE `id`=`id`;

-- 5. Usluge
INSERT INTO `services` (`id`, `name`, `description`, `price`, `duration_minutes`) VALUES
(1, 'Mali servis', 'Zamena motornog ulja, filtera ulja, vazduha, goriva i kabine', 9500.0, 60),
(2, 'Veliki servis', 'Zamena zupčastog kaiša, španera, rolera i vodene pumpe', 38000.0, 180),
(3, 'Kompjuterska dijagnostika', 'Kompletno očitavanje grešaka svih elektronskih modula', 3000.0, 30),
(4, 'Servis kočionog sistema', 'Zamena prednjih/zadnjih kočionih pločica i diskova sa proverom ulja', 14000.0, 90),
(5, 'Montaža i balansiranje guma', 'Demontaža, montaža i balansiranje seta od 4 pneumatika', 4000.0, 45)
ON DUPLICATE KEY UPDATE `id`=`id`;

-- 6. Termini
INSERT INTO `appointments` (`id`, `customer_id`, `vehicle_id`, `employee_id`, `service_id`, `appointment_date`, `appointment_time`, `status`, `notes`, `total_price`) VALUES
(1, 1, 1, 1, 1, '2026-08-10', '09:00', 'ZAVRŠENO', 'Redovan servis na 150.000 km, sipano Castrol 5W30', 9500.0),
(2, 2, 2, 3, 3, '2026-08-12', '11:30', 'ZAVRŠENO', 'Otklonjena greška na EGR ventilu', 3000.0),
(3, 3, 3, 2, 2, '2026-08-20', '10:00', 'ZAKAZANO', 'Zameniti i PK kaiš i remenicu alternatora', 38000.0),
(4, 4, 4, 1, 4, '2026-08-22', '14:00', 'ZAKAZANO', 'Klijent prijavio škripanje pri kočenju', 14000.0),
(5, 5, 5, 4, 5, '2026-08-15', '16:00', 'OTKAZANO', 'Klijent otkazao zbog službenog puta', 4000.0)
ON DUPLICATE KEY UPDATE `id`=`id`;
