CREATE DATABASE IF NOT EXISTS gym_db;
USE gym_db;

CREATE TABLE IF NOT EXISTS membership_plans (
    plan_id INT AUTO_INCREMENT PRIMARY KEY,
    plan_name VARCHAR(50) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    duration_months INT NOT NULL,
    description TEXT
);

CREATE TABLE IF NOT EXISTS members (
    member_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    plan_id INT,
    join_date DATE,
    FOREIGN KEY (plan_id) REFERENCES membership_plans(plan_id)
);

CREATE TABLE IF NOT EXISTS staff (
    staff_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    role VARCHAR(50) NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(50) NOT NULL,
    salary DECIMAL(10, 2)
);

CREATE TABLE IF NOT EXISTS payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    member_id INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_date DATE NOT NULL,
    payment_method VARCHAR(50),
    FOREIGN KEY (member_id) REFERENCES members(member_id)
);

CREATE TABLE IF NOT EXISTS attendance (
    attendance_id INT AUTO_INCREMENT PRIMARY KEY,
    member_id INT NOT NULL,
    check_in_time DATETIME NOT NULL,
    FOREIGN KEY (member_id) REFERENCES members(member_id)
);

INSERT INTO membership_plans (plan_name, price, duration_months, description) VALUES
('Basic Plan', 99.00, 1, 'Basic gym access, standard equipment, locker room access.'),
('Premium Plan', 199.00, 1, 'All Basic features + group classes, pool access, personal trainer consultation.'),
('VIP Plan', 399.00, 1, 'All Premium features + private training sessions, sauna, 24/7 access, VIP lounge.');

INSERT INTO staff (name, email, phone, role, username, password, salary) VALUES
('Admin User', 'admin@gym.com', '13800000000', 'Receptionist', 'admin', 'admin123', 5000.00),
('Zhang Wei', 'zhangwei@gym.com', '13800000001', 'Trainer', 'trainer1', '123456', 8000.00);

INSERT INTO members (name, email, phone, plan_id, join_date) VALUES
('Li Ming', 'liming@email.com', '13900000001', 1, '2026-05-01'),
('Wang Fang', 'wangfang@email.com', '13900000002', 2, '2026-06-15'),
('Chen Jie', 'chenjie@email.com', '13900000003', 3, '2026-07-01');
