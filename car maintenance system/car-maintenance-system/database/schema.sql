-- ============================================
-- Smart Car Maintenance Analytics System
-- Database Schema
-- ============================================

-- Create Database
CREATE DATABASE IF NOT EXISTS car_maintenance_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE car_maintenance_db;

-- ============================================
-- CARS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS cars (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    plate_number VARCHAR(20) NOT NULL UNIQUE,
    brand VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    year INT NOT NULL,
    color VARCHAR(30),
    vin VARCHAR(17) UNIQUE,
    current_mileage INT DEFAULT 0,
    engine_type ENUM('petrol', 'diesel', 'electric', 'hybrid') DEFAULT 'petrol',
    transmission ENUM('manual', 'automatic', 'cvt') DEFAULT 'automatic',
    fuel_capacity DECIMAL(10,2),
    owner_name VARCHAR(100),
    owner_phone VARCHAR(20),
    owner_email VARCHAR(100),
    purchase_date DATE,
    warranty_expiry DATE,
    image_url VARCHAR(500),
    status ENUM('active', 'inactive', 'sold') DEFAULT 'active',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_plate (plate_number),
    INDEX idx_brand_model (brand, model),
    INDEX idx_status (status)
) ENGINE=InnoDB;

-- ============================================
-- MAINTENANCE TYPES TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS maintenance_types (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    name_ar VARCHAR(100),
    category ENUM('routine', 'preventive', 'corrective', 'emergency') DEFAULT 'routine',
    description TEXT,
    recommended_interval_km INT,
    recommended_interval_months INT,
    estimated_duration_hours DECIMAL(5,2),
    priority ENUM('low', 'medium', 'high', 'critical') DEFAULT 'medium',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Insert default maintenance types
INSERT INTO maintenance_types (name, name_ar, category, recommended_interval_km, recommended_interval_months, priority) VALUES
('Oil Change', 'تغيير الزيت', 'routine', 5000, 6, 'high'),
('Oil Filter Replacement', 'تغيير فلتر الزيت', 'routine', 5000, 6, 'medium'),
('Air Filter Replacement', 'تغيير فلتر الهواء', 'routine', 15000, 12, 'medium'),
('Cabin Air Filter', 'تغيير فلتر المكيف', 'routine', 20000, 12, 'low'),
('Spark Plugs Replacement', 'تغيير شمعات الإشعال', 'preventive', 30000, 24, 'medium'),
('Brake Pads Replacement', 'تغيير تيل الفرامل', 'preventive', 40000, 24, 'high'),
('Brake Fluid Change', 'تغيير زيت الفرامل', 'routine', 40000, 24, 'high'),
('Coolant Change', 'تغيير سائل التبريد', 'routine', 50000, 36, 'medium'),
('Transmission Fluid', 'تغيير زيت القير', 'routine', 60000, 36, 'high'),
('Timing Belt Replacement', 'تغيير سير التايمينج', 'preventive', 100000, 60, 'critical'),
('Tire Rotation', 'تدوير الإطارات', 'routine', 10000, 6, 'low'),
('Tire Replacement', 'تغيير الإطارات', 'preventive', 50000, 36, 'high'),
('Battery Replacement', 'تغيير البطارية', 'preventive', 0, 36, 'medium'),
('Wheel Alignment', 'ضبط الزوايا', 'routine', 20000, 12, 'medium'),
('AC Service', 'صيانة المكيف', 'routine', 0, 12, 'low');

-- ============================================
-- MAINTENANCE RECORDS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS maintenance_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    car_id BIGINT NOT NULL,
    maintenance_type_id INT NOT NULL,
    service_date DATE NOT NULL,
    mileage_at_service INT NOT NULL,
    cost DECIMAL(10,2) NOT NULL,
    labor_cost DECIMAL(10,2) DEFAULT 0,
    parts_cost DECIMAL(10,2) DEFAULT 0,
    workshop_name VARCHAR(100),
    workshop_location VARCHAR(200),
    technician_name VARCHAR(100),
    description TEXT,
    parts_used TEXT,
    next_service_mileage INT,
    next_service_date DATE,
    warranty_months INT,
    invoice_number VARCHAR(50),
    receipt_image_url VARCHAR(500),
    status ENUM('scheduled', 'in_progress', 'completed', 'cancelled') DEFAULT 'completed',
    rating INT CHECK (rating >= 1 AND rating <= 5),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (car_id) REFERENCES cars(id) ON DELETE CASCADE,
    FOREIGN KEY (maintenance_type_id) REFERENCES maintenance_types(id),
    
    INDEX idx_car_date (car_id, service_date),
    INDEX idx_service_date (service_date),
    INDEX idx_status (status)
) ENGINE=InnoDB;

-- ============================================
-- FAILURE TYPES TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS failure_types (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    name_ar VARCHAR(100),
    category VARCHAR(50),
    description TEXT,
    common_causes TEXT,
    prevention_tips TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Insert default failure types
INSERT INTO failure_types (name, name_ar, category, description) VALUES
('Engine Overheating', 'سخونة المحرك', 'engine', 'Engine temperature exceeds normal operating range'),
('Engine Misfire', 'احتراق غير منتظم', 'engine', 'Engine cylinders not firing properly'),
('Oil Leak', 'تسريب زيت', 'engine', 'Oil leaking from engine components'),
('Brake Failure', 'تعطل الفرامل', 'brakes', 'Braking system not functioning properly'),
('Brake Noise', 'صوت الفرامل', 'brakes', 'Unusual noise when applying brakes'),
('Battery Dead', 'نفاد البطارية', 'electrical', 'Battery unable to hold charge'),
('Alternator Failure', 'تعطل الدينامو', 'electrical', 'Alternator not charging battery'),
('Starter Motor Failure', 'تعطل موتور التشغيل', 'electrical', 'Starter motor not engaging'),
('Transmission Slip', 'انزلاق القير', 'transmission', 'Transmission slipping between gears'),
('Clutch Failure', 'تعطل الدبرياج', 'transmission', 'Clutch not engaging or disengaging'),
('Tire Blowout', 'انفجار الإطار', 'tires', 'Sudden tire failure'),
('Tire Wear', 'تآكل الإطارات', 'tires', 'Excessive tire wear patterns'),
('AC Failure', 'تعطل المكيف', 'hvac', 'Air conditioning not cooling'),
('Suspension Noise', 'صوت المساعدات', 'suspension', 'Unusual noise from suspension'),
('Steering Issues', 'مشاكل التوجيه', 'steering', 'Difficulty in steering control');

-- ============================================
-- FAILURES TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS failures (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    car_id BIGINT NOT NULL,
    failure_type_id INT NOT NULL,
    failure_date DATE NOT NULL,
    mileage_at_failure INT NOT NULL,
    severity ENUM('minor', 'moderate', 'major', 'critical') DEFAULT 'moderate',
    description TEXT,
    symptoms TEXT,
    root_cause TEXT,
    weather_conditions VARCHAR(50),
    driving_conditions VARCHAR(50),
    is_recurring BOOLEAN DEFAULT FALSE,
    parent_failure_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (car_id) REFERENCES cars(id) ON DELETE CASCADE,
    FOREIGN KEY (failure_type_id) REFERENCES failure_types(id),
    FOREIGN KEY (parent_failure_id) REFERENCES failures(id) ON DELETE SET NULL,
    
    INDEX idx_car_failure (car_id, failure_date),
    INDEX idx_severity (severity),
    INDEX idx_recurring (is_recurring)
) ENGINE=InnoDB;

-- ============================================
-- REPAIRS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS repairs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    failure_id BIGINT NOT NULL,
    car_id BIGINT NOT NULL,
    repair_date DATE NOT NULL,
    mileage_at_repair INT NOT NULL,
    cost DECIMAL(10,2) NOT NULL,
    labor_cost DECIMAL(10,2) DEFAULT 0,
    parts_cost DECIMAL(10,2) DEFAULT 0,
    workshop_name VARCHAR(100),
    technician_name VARCHAR(100),
    description TEXT,
    parts_replaced TEXT,
    repair_method TEXT,
    warranty_months INT,
    invoice_number VARCHAR(50),
    status ENUM('pending', 'in_progress', 'completed', 'partially_completed') DEFAULT 'completed',
    is_successful BOOLEAN DEFAULT TRUE,
    follow_up_required BOOLEAN DEFAULT FALSE,
    follow_up_date DATE,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (failure_id) REFERENCES failures(id) ON DELETE CASCADE,
    FOREIGN KEY (car_id) REFERENCES cars(id) ON DELETE CASCADE,
    
    INDEX idx_repair_date (repair_date),
    INDEX idx_status (status)
) ENGINE=InnoDB;

-- ============================================
-- PREDICTIONS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS predictions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    car_id BIGINT NOT NULL,
    prediction_type ENUM('maintenance', 'failure', 'cost') NOT NULL,
    prediction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    predicted_event VARCHAR(100) NOT NULL,
    predicted_date DATE,
    predicted_mileage INT,
    probability DECIMAL(5,4),
    confidence_level ENUM('low', 'medium', 'high') DEFAULT 'medium',
    contributing_factors TEXT,
    recommendations TEXT,
    model_version VARCHAR(20),
    is_accurate BOOLEAN,
    actual_outcome TEXT,
    feedback_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (car_id) REFERENCES cars(id) ON DELETE CASCADE,
    
    INDEX idx_car_prediction (car_id, prediction_date),
    INDEX idx_type (prediction_type)
) ENGINE=InnoDB;

-- ============================================
-- SENSORS DATA TABLE (For IoT integration)
-- ============================================
CREATE TABLE IF NOT EXISTS sensor_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    car_id BIGINT NOT NULL,
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    mileage INT,
    engine_temperature DECIMAL(6,2),
    oil_pressure DECIMAL(6,2),
    battery_voltage DECIMAL(6,2),
    fuel_level DECIMAL(5,2),
    engine_rpm INT,
    speed INT,
    engine_hours INT,
    error_codes TEXT,
    gps_latitude DECIMAL(10,8),
    gps_longitude DECIMAL(11,8),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (car_id) REFERENCES cars(id) ON DELETE CASCADE,
    
    INDEX idx_car_time (car_id, recorded_at)
) ENGINE=InnoDB;

-- ============================================
-- ANALYTICS SUMMARY TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS analytics_summary (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    car_id BIGINT NOT NULL,
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    total_maintenance_cost DECIMAL(12,2),
    total_repair_cost DECIMAL(12,2),
    total_cost DECIMAL(12,2),
    maintenance_count INT,
    failure_count INT,
    average_mileage_per_month DECIMAL(10,2),
    cost_per_km DECIMAL(10,4),
    most_common_failure VARCHAR(100),
    maintenance_compliance_rate DECIMAL(5,2),
    health_score INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (car_id) REFERENCES cars(id) ON DELETE CASCADE,
    
    INDEX idx_car_period (car_id, period_start, period_end)
) ENGINE=InnoDB;

-- ============================================
-- NOTIFICATIONS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    car_id BIGINT,
    notification_type ENUM('maintenance_due', 'maintenance_overdue', 'prediction_alert', 'failure_warning', 'cost_alert') NOT NULL,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    priority ENUM('low', 'medium', 'high', 'urgent') DEFAULT 'medium',
    is_read BOOLEAN DEFAULT FALSE,
    is_sent BOOLEAN DEFAULT FALSE,
    sent_via ENUM('email', 'sms', 'push', 'in_app') DEFAULT 'in_app',
    scheduled_date TIMESTAMP,
    sent_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (car_id) REFERENCES cars(id) ON DELETE CASCADE,
    
    INDEX idx_unread (is_read, created_at),
    INDEX idx_scheduled (scheduled_date, is_sent)
) ENGINE=InnoDB;

-- ============================================
-- USERS TABLE (For multi-user support)
-- ============================================
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    phone VARCHAR(20),
    role ENUM('admin', 'manager', 'technician', 'viewer') DEFAULT 'viewer',
    is_active BOOLEAN DEFAULT TRUE,
    last_login TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB;

-- ============================================
-- USER_CARS TABLE (Many-to-Many relationship)
-- ============================================
CREATE TABLE IF NOT EXISTS user_cars (
    user_id BIGINT NOT NULL,
    car_id BIGINT NOT NULL,
    permission ENUM('owner', 'editor', 'viewer') DEFAULT 'viewer',
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    PRIMARY KEY (user_id, car_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (car_id) REFERENCES cars(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================
-- VIEWS FOR ANALYTICS
-- ============================================

-- View: Car Maintenance Summary
CREATE OR REPLACE VIEW v_car_maintenance_summary AS
SELECT 
    c.id AS car_id,
    c.plate_number,
    c.brand,
    c.model,
    c.year,
    c.current_mileage,
    COUNT(DISTINCT mr.id) AS total_maintenance_count,
    COALESCE(SUM(mr.cost), 0) AS total_maintenance_cost,
    MAX(mr.service_date) AS last_maintenance_date,
    COUNT(DISTINCT f.id) AS total_failures_count,
    COALESCE(SUM(r.cost), 0) AS total_repair_cost,
    COUNT(DISTINCT CASE WHEN f.severity = 'critical' THEN f.id END) AS critical_failures
FROM cars c
LEFT JOIN maintenance_records mr ON c.id = mr.car_id
LEFT JOIN failures f ON c.id = f.car_id
LEFT JOIN repairs r ON f.id = r.failure_id
WHERE c.status = 'active'
GROUP BY c.id;

-- View: Most Common Failures
CREATE OR REPLACE VIEW v_common_failures AS
SELECT 
    ft.id,
    ft.name,
    ft.name_ar,
    ft.category,
    COUNT(f.id) AS occurrence_count,
    AVG(CASE f.severity WHEN 'minor' THEN 1 WHEN 'moderate' THEN 2 WHEN 'major' THEN 3 WHEN 'critical' THEN 4 END) AS avg_severity_score,
    COALESCE(SUM(r.cost), 0) AS total_repair_cost,
    AVG(r.cost) AS avg_repair_cost
FROM failure_types ft
LEFT JOIN failures f ON ft.id = f.failure_type_id
LEFT JOIN repairs r ON f.id = r.failure_id
GROUP BY ft.id
ORDER BY occurrence_count DESC;

-- View: Maintenance Cost by Month
CREATE OR REPLACE VIEW v_monthly_costs AS
SELECT 
    YEAR(service_date) AS year,
    MONTH(service_date) AS month,
    COUNT(*) AS maintenance_count,
    SUM(cost) AS total_cost,
    AVG(cost) AS avg_cost
FROM maintenance_records
WHERE status = 'completed'
GROUP BY YEAR(service_date), MONTH(service_date)
ORDER BY year DESC, month DESC;

-- ============================================
-- STORED PROCEDURES
-- ============================================

DELIMITER //

-- Procedure: Get Next Maintenance Due
CREATE PROCEDURE sp_get_next_maintenance(IN p_car_id BIGINT)
BEGIN
    SELECT 
        mt.id,
        mt.name,
        mt.name_ar,
        mt.recommended_interval_km,
        mt.recommended_interval_months,
        MAX(mr.service_date) AS last_service_date,
        MAX(mr.mileage_at_service) AS last_service_mileage,
        c.current_mileage,
        CASE 
            WHEN mt.recommended_interval_km > 0 THEN 
                MAX(mr.mileage_at_service) + mt.recommended_interval_km - c.current_mileage
            ELSE NULL 
        END AS km_until_due,
        CASE 
            WHEN mt.recommended_interval_months > 0 THEN 
                DATEDIFF(DATE_ADD(MAX(mr.service_date), INTERVAL mt.recommended_interval_months MONTH), CURDATE())
            ELSE NULL 
        END AS days_until_due
    FROM maintenance_types mt
    CROSS JOIN cars c
    LEFT JOIN maintenance_records mr ON mt.id = mr.maintenance_type_id AND mr.car_id = p_car_id
    WHERE c.id = p_car_id AND mt.is_active = TRUE
    GROUP BY mt.id
    HAVING 
        (km_until_due IS NOT NULL AND km_until_due <= 1000) OR
        (days_until_due IS NOT NULL AND days_until_due <= 30) OR
        MAX(mr.service_date) IS NULL;
END //

-- Procedure: Calculate Car Health Score
CREATE PROCEDURE sp_calculate_health_score(IN p_car_id BIGINT)
BEGIN
    DECLARE v_score INT DEFAULT 100;
    DECLARE v_maintenance_score INT;
    DECLARE v_failure_score INT;
    DECLARE v_age_score INT;
    DECLARE v_mileage_score INT;
    
    -- Maintenance compliance score (0-25 points)
    SELECT COALESCE(25 - (COUNT(*) * 5), 25) INTO v_maintenance_score
    FROM (
        SELECT mt.id
        FROM maintenance_types mt
        LEFT JOIN maintenance_records mr ON mt.id = mr.maintenance_type_id AND mr.car_id = p_car_id
        WHERE mt.is_active = TRUE
        GROUP BY mt.id
        HAVING 
            (MAX(mr.service_date) IS NULL) OR
            (DATEDIFF(CURDATE(), MAX(mr.service_date)) > mt.recommended_interval_months * 30)
    ) overdue;
    SET v_maintenance_score = GREATEST(0, v_maintenance_score);
    
    -- Failure score (0-25 points)
    SELECT COALESCE(25 - (COUNT(*) * 
        CASE severity 
            WHEN 'minor' THEN 2 
            WHEN 'moderate' THEN 5 
            WHEN 'major' THEN 10 
            WHEN 'critical' THEN 20 
        END), 25) INTO v_failure_score
    FROM failures
    WHERE car_id = p_car_id AND failure_date >= DATE_SUB(CURDATE(), INTERVAL 1 YEAR);
    SET v_failure_score = GREATEST(0, v_failure_score);
    
    -- Age score (0-25 points)
    SELECT COALESCE(25 - FLOOR((YEAR(CURDATE()) - year) * 2), 25) INTO v_age_score
    FROM cars WHERE id = p_car_id;
    SET v_age_score = GREATEST(0, LEAST(25, v_age_score));
    
    -- Mileage score (0-25 points)
    SELECT COALESCE(25 - FLOOR(current_mileage / 50000), 25) INTO v_mileage_score
    FROM cars WHERE id = p_car_id;
    SET v_mileage_score = GREATEST(0, LEAST(25, v_mileage_score));
    
    SET v_score = v_maintenance_score + v_failure_score + v_age_score + v_mileage_score;
    
    -- Update analytics summary
    INSERT INTO analytics_summary (car_id, period_start, period_end, health_score)
    VALUES (p_car_id, DATE_SUB(CURDATE(), INTERVAL 1 YEAR), CURDATE(), v_score)
    ON DUPLICATE KEY UPDATE health_score = v_score;
    
    SELECT v_score AS health_score, v_maintenance_score, v_failure_score, v_age_score, v_mileage_score;
END //

DELIMITER ;

-- ============================================
-- TRIGGERS
-- ============================================

DELIMITER //

-- Trigger: Update car mileage after maintenance
CREATE TRIGGER tr_update_mileage_after_maintenance
AFTER INSERT ON maintenance_records
FOR EACH ROW
BEGIN
    UPDATE cars 
    SET current_mileage = GREATEST(current_mileage, NEW.mileage_at_service),
        updated_at = CURRENT_TIMESTAMP
    WHERE id = NEW.car_id;
END //

-- Trigger: Create notification for overdue maintenance
CREATE TRIGGER tr_check_overdue_maintenance
AFTER UPDATE ON cars
FOR EACH ROW
BEGIN
    DECLARE overdue_count INT;
    
    SELECT COUNT(*) INTO overdue_count
    FROM maintenance_types mt
    LEFT JOIN maintenance_records mr ON mt.id = mr.maintenance_type_id AND mr.car_id = NEW.id
    WHERE mt.is_active = TRUE
    GROUP BY mt.id
    HAVING DATEDIFF(CURDATE(), MAX(mr.service_date)) > mt.recommended_interval_months * 30 * 1.5;
    
    IF overdue_count > 0 THEN
        INSERT INTO notifications (car_id, notification_type, title, message, priority)
        VALUES (NEW.id, 'maintenance_overdue', 'Maintenance Overdue', 
                CONCAT('Car ', NEW.plate_number, ' has overdue maintenance items'), 'high');
    END IF;
END //

DELIMITER ;

-- ============================================
-- SAMPLE DATA FOR TESTING
-- ============================================

-- Insert sample cars
INSERT INTO cars (plate_number, brand, model, year, color, current_mileage, engine_type, transmission, owner_name, owner_phone) VALUES
('ABC-1234', 'Toyota', 'Camry', 2020, 'White', 45000, 'petrol', 'automatic', 'Ahmed Mohamed', '+201234567890'),
('XYZ-5678', 'Honda', 'Civic', 2019, 'Black', 62000, 'petrol', 'automatic', 'Sara Ali', '+201234567891'),
('DEF-9012', 'BMW', '320i', 2021, 'Blue', 28000, 'petrol', 'automatic', 'Mohamed Hassan', '+201234567892');

-- Insert sample maintenance records
INSERT INTO maintenance_records (car_id, maintenance_type_id, service_date, mileage_at_service, cost, workshop_name, status) VALUES
(1, 1, '2024-01-15', 40000, 350.00, 'Auto Service Center', 'completed'),
(1, 2, '2024-01-15', 40000, 50.00, 'Auto Service Center', 'completed'),
(1, 6, '2023-08-20', 35000, 800.00, 'Brake Masters', 'completed'),
(2, 1, '2024-02-10', 58000, 380.00, 'Honda Dealer', 'completed'),
(2, 3, '2024-02-10', 58000, 80.00, 'Honda Dealer', 'completed'),
(3, 1, '2024-03-01', 25000, 450.00, 'BMW Service', 'completed');

-- Insert sample failures
INSERT INTO failures (car_id, failure_type_id, failure_date, mileage_at_failure, severity, description) VALUES
(1, 3, '2023-12-10', 38000, 'minor', 'Small oil leak detected near oil filter'),
(2, 5, '2023-11-15', 55000, 'moderate', 'Battery not holding charge, needs replacement'),
(1, 8, '2023-06-20', 30000, 'moderate', 'Brake squealing noise');

-- Insert sample repairs
INSERT INTO repairs (failure_id, car_id, repair_date, mileage_at_repair, cost, description, status) VALUES
(1, 1, '2023-12-15', 38000, 150.00, 'Replaced oil filter and gasket', 'completed'),
(2, 2, '2023-11-18', 55000, 300.00, 'Replaced battery with new one', 'completed'),
(3, 1, '2023-06-25', 30000, 250.00, 'Cleaned brake components', 'completed');

-- ============================================
-- END OF SCHEMA
-- ============================================
