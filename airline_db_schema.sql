CREATE DATABASE IF NOT EXISTS airline_db;
USE airline_db;

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('User', 'Staff', 'Admin') DEFAULT 'User',
    status ENUM('Active', 'Inactive') DEFAULT 'Active',
    created_at DATETIME DEFAULT NOW()
);

CREATE TABLE employees (
    employee_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    role ENUM('Admin', 'Staff') NOT NULL,
    department VARCHAR(100),
    status ENUM('Active', 'Inactive') DEFAULT 'Active',
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE flights (
    flight_id INT AUTO_INCREMENT PRIMARY KEY,
    flight_number VARCHAR(20) UNIQUE NOT NULL,
    source VARCHAR(100) NOT NULL,
    destination VARCHAR(100) NOT NULL,
    date DATE NOT NULL,
    economy_price DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    business_price DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    first_price DECIMAL(10,2) NOT NULL DEFAULT 0.00
);

CREATE TABLE seats (
    seat_id INT AUTO_INCREMENT PRIMARY KEY,
    flight_id INT NOT NULL,
    seat_number VARCHAR(10) NOT NULL,
    seat_class ENUM('Economy', 'Business', 'First') NOT NULL,
    status ENUM('Available', 'Booked') DEFAULT 'Available',
    FOREIGN KEY (flight_id) REFERENCES flights(flight_id) ON DELETE CASCADE
);

CREATE TABLE bookings (
    booking_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    flight_id INT NOT NULL,
    seat_id INT NOT NULL,
    seat_class ENUM('Economy', 'Business', 'First') NOT NULL,
    booking_date DATETIME DEFAULT NOW(),
    status ENUM('Confirmed', 'Cancelled') DEFAULT 'Confirmed',
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (flight_id) REFERENCES flights(flight_id),
    FOREIGN KEY (seat_id) REFERENCES seats(seat_id)
);

CREATE TABLE payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id INT NOT NULL,
    payment_method ENUM('BankAccount', 'Card', 'Bkash') NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_status ENUM('Completed', 'Failed') DEFAULT 'Completed',
    payment_date DATETIME DEFAULT NOW(),
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id)
);

CREATE TABLE payment_accounts (
    account_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    payment_type ENUM('BankAccount', 'Card', 'Bkash') NOT NULL,
    account_number VARCHAR(100) NOT NULL,
    pin VARCHAR(10) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE coupons (
    coupon_id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    discount_percent INT NOT NULL CHECK (discount_percent BETWEEN 1 AND 100),
    expiry_date DATE,
    status ENUM('Active', 'Inactive') DEFAULT 'Active'
);

INSERT INTO users (name, username, password, role) VALUES
('System Admin', 'admin', 'admin123', 'Admin');

INSERT INTO employees (user_id, role, department) VALUES
(1, 'Admin', 'Management');

INSERT INTO users (name, username, password, role) VALUES
('Jane Staff', 'staff1', 'staff123', 'Staff');

INSERT INTO employees (user_id, role, department) VALUES
(2, 'Staff', 'Ticketing');

INSERT INTO users (name, username, password, role) VALUES
('John Doe', 'johndoe', 'user123', 'User');

INSERT INTO flights (flight_number, source, destination, date, economy_price, business_price, first_price) VALUES
('BS101', 'Dhaka',      'Chittagong',  CURDATE() + INTERVAL 2 DAY,  3500.00, 7000.00,  12000.00),
('BS102', 'Dhaka',      'Sylhet',      CURDATE() + INTERVAL 4 DAY,  4000.00, 8000.00,  14000.00),
('BS103', 'Chittagong', 'Cox\'s Bazar',CURDATE() + INTERVAL 6 DAY,  2500.00, 5000.00,  9000.00),
('BS104', 'Dhaka',      'Rajshahi',    CURDATE() + INTERVAL 8 DAY,  3200.00, 6500.00,  11000.00),
('BS105', 'Sylhet',     'Dhaka',       CURDATE() + INTERVAL 10 DAY, 4000.00, 8000.00,  14000.00);

INSERT INTO seats (flight_id, seat_number, seat_class) VALUES
(1, '1A', 'First'),   (1, '1B', 'First'),
(1, '2A', 'Business'),(1, '2B', 'Business'),(1, '2C', 'Business'),
(1, '3A', 'Economy'), (1, '3B', 'Economy'), (1, '3C', 'Economy'),
(1, '4A', 'Economy'), (1, '4B', 'Economy'), (1, '4C', 'Economy'),
(1, '5A', 'Economy'), (1, '5B', 'Economy'), (1, '5C', 'Economy');

INSERT INTO coupons (code, discount_percent, expiry_date) VALUES
('WELCOME10', 10, CURDATE() + INTERVAL 30 DAY),
('SALE20',    20, CURDATE() + INTERVAL 15 DAY);