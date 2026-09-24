-- Car Sharing Application - MySQL Database Schema
-- Defines 10 tables, structural constraints, foreign key relationships, and optimization indexes.

CREATE DATABASE IF NOT EXISTS carsharing_db;
USE carsharing_db;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER',
    phone VARCHAR(15) NOT NULL,
    city VARCHAR(100) NOT NULL,
    status ENUM('ACTIVE', 'SUSPENDED') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_user_email UNIQUE (email)
) ENGINE=InnoDB;

-- 2. Vehicles Table
CREATE TABLE IF NOT EXISTS vehicles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    owner_id INT NOT NULL,
    make VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    year INT NOT NULL,
    license_plate VARCHAR(30) NOT NULL,
    type VARCHAR(30) NOT NULL,
    fuel_type VARCHAR(30) NOT NULL,
    transmission VARCHAR(30) NOT NULL,
    color VARCHAR(30) NOT NULL,
    seating_capacity INT NOT NULL,
    status ENUM('AVAILABLE', 'UNAVAILABLE', 'REMOVED') NOT NULL DEFAULT 'AVAILABLE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_vehicle_plate UNIQUE (license_plate),
    CONSTRAINT fk_vehicle_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 3. Rides Table
CREATE TABLE IF NOT EXISTS rides (
    id INT AUTO_INCREMENT PRIMARY KEY,
    driver_id INT NOT NULL,
    vehicle_id INT NOT NULL,
    source VARCHAR(150) NOT NULL,
    destination VARCHAR(150) NOT NULL,
    ride_date DATE NOT NULL,
    ride_time TIME NOT NULL,
    total_seats INT NOT NULL,
    available_seats INT NOT NULL,
    price_per_seat DECIMAL(10, 2) NOT NULL,
    description TEXT,
    status ENUM('ACTIVE', 'FULL', 'CANCELLED', 'COMPLETED') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ride_driver FOREIGN KEY (driver_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_ride_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicles(id) ON DELETE RESTRICT,
    CONSTRAINT chk_ride_seats CHECK (available_seats >= 0 AND available_seats <= total_seats),
    CONSTRAINT chk_price_seat CHECK (price_per_seat >= 0.00)
) ENGINE=InnoDB;

-- 4. Ride Bookings Table
CREATE TABLE IF NOT EXISTS ride_bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ride_id INT NOT NULL,
    passenger_id INT NOT NULL,
    seats_booked INT NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    status ENUM('CONFIRMED', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'CONFIRMED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_booking_ride FOREIGN KEY (ride_id) REFERENCES rides(id) ON DELETE RESTRICT,
    CONSTRAINT fk_booking_passenger FOREIGN KEY (passenger_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT chk_booking_seats CHECK (seats_booked > 0)
) ENGINE=InnoDB;

-- 5. Rental Listings Table
CREATE TABLE IF NOT EXISTS rental_listings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    owner_id INT NOT NULL,
    vehicle_id INT NOT NULL,
    location VARCHAR(150) NOT NULL,
    available_from DATE NOT NULL,
    available_until DATE NOT NULL,
    price_per_day DECIMAL(10, 2) NOT NULL,
    security_deposit DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    description TEXT,
    status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rental_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_rental_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicles(id) ON DELETE RESTRICT,
    CONSTRAINT chk_rental_dates CHECK (available_until >= available_from),
    CONSTRAINT chk_price_day CHECK (price_per_day >= 0.00),
    CONSTRAINT chk_security CHECK (security_deposit >= 0.00)
) ENGINE=InnoDB;

-- 6. Rental Bookings Table
CREATE TABLE IF NOT EXISTS rental_bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    rental_listing_id INT NOT NULL,
    renter_id INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    status ENUM('CONFIRMED', 'ONGOING', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'CONFIRMED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_booking_rental FOREIGN KEY (rental_listing_id) REFERENCES rental_listings(id) ON DELETE RESTRICT,
    CONSTRAINT fk_booking_renter FOREIGN KEY (renter_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT chk_rental_booking_dates CHECK (end_date >= start_date)
) ENGINE=InnoDB;

-- 7. Payments Table
CREATE TABLE IF NOT EXISTS payments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    booking_type ENUM('RIDE', 'RENTAL') NOT NULL,
    ride_booking_id INT DEFAULT NULL,
    rental_booking_id INT DEFAULT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    status ENUM('PENDING', 'COMPLETED', 'REFUNDED') NOT NULL DEFAULT 'PENDING',
    transaction_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_payment_txn UNIQUE (transaction_id),
    CONSTRAINT fk_payment_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_payment_ride FOREIGN KEY (ride_booking_id) REFERENCES ride_bookings(id) ON DELETE SET NULL,
    CONSTRAINT fk_payment_rental FOREIGN KEY (rental_booking_id) REFERENCES rental_bookings(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- 8. Ratings Table
CREATE TABLE IF NOT EXISTS ratings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    rater_id INT NOT NULL,
    ratee_id INT NOT NULL,
    booking_type ENUM('RIDE', 'RENTAL') NOT NULL,
    ride_booking_id INT DEFAULT NULL,
    rental_booking_id INT DEFAULT NULL,
    rating_value INT NOT NULL,
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rating_rater FOREIGN KEY (rater_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_rating_ratee FOREIGN KEY (ratee_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_rating_ride FOREIGN KEY (ride_booking_id) REFERENCES ride_bookings(id) ON DELETE CASCADE,
    CONSTRAINT fk_rating_rental FOREIGN KEY (rental_booking_id) REFERENCES rental_bookings(id) ON DELETE CASCADE,
    CONSTRAINT chk_rating_val CHECK (rating_value >= 1 AND rating_value <= 5)
) ENGINE=InnoDB;

-- 9. Notifications Table
CREATE TABLE IF NOT EXISTS notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 10. Complaints Table
CREATE TABLE IF NOT EXISTS complaints (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    subject VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    booking_type ENUM('RIDE', 'RENTAL', 'GENERAL') NOT NULL DEFAULT 'GENERAL',
    ride_booking_id INT DEFAULT NULL,
    rental_booking_id INT DEFAULT NULL,
    status ENUM('PENDING', 'RESOLVED') NOT NULL DEFAULT 'PENDING',
    admin_remarks TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP NULL DEFAULT NULL,
    CONSTRAINT fk_complaint_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_complaint_ride FOREIGN KEY (ride_booking_id) REFERENCES ride_bookings(id) ON DELETE SET NULL,
    CONSTRAINT fk_complaint_rental FOREIGN KEY (rental_booking_id) REFERENCES rental_bookings(id) ON DELETE SET NULL
) ENGINE=InnoDB;


-- Indexes for Performance Tuning
-- Indexing keys that will be heavily filtered in searches or join filters
CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_vehicle_owner ON vehicles(owner_id);
CREATE INDEX idx_ride_route ON rides(source, destination, ride_date);
CREATE INDEX idx_rental_search ON rental_listings(location, available_from, available_until);
CREATE INDEX idx_ride_bookings_ride ON ride_bookings(ride_id);
CREATE INDEX idx_rental_bookings_listing ON rental_bookings(rental_listing_id);
CREATE INDEX idx_payments_txn ON payments(transaction_id);
CREATE INDEX idx_ratings_ratee ON ratings(ratee_id);
CREATE INDEX idx_notifications_user ON notifications(user_id, is_read);
CREATE INDEX idx_complaints_status ON complaints(status);
