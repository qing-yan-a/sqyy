CREATE DATABASE IF NOT EXISTS community_hospital DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE community_hospital;

DROP TABLE IF EXISTS prescription_record;
DROP TABLE IF EXISTS drug_inventory_log;
DROP TABLE IF EXISTS diagnosis_record;
DROP TABLE IF EXISTS visit_record;
DROP TABLE IF EXISTS drug_info;
DROP TABLE IF EXISTS patient;
DROP TABLE IF EXISTS sys_user_role;
DROP TABLE IF EXISTS sys_role;
DROP TABLE IF EXISTS sys_user;

CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    real_name VARCHAR(64) NOT NULL,
    phone VARCHAR(32),
    id_card VARCHAR(18),
    avatar VARCHAR(255),
    department VARCHAR(64),
    pharmacy VARCHAR(64),
    reception_desk VARCHAR(64),
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_code VARCHAR(64) NOT NULL UNIQUE,
    role_name VARCHAR(64) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE sys_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_role (user_id, role_id)
);

CREATE TABLE patient (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    patient_no VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(64) NOT NULL,
    gender VARCHAR(16) NOT NULL,
    age INT NOT NULL,
    phone VARCHAR(32),
    id_card VARCHAR(32),
    address VARCHAR(255),
    allergy_history VARCHAR(255),
    medical_history TEXT,
    avatar VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE visit_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    patient_id BIGINT NOT NULL,
    visit_no VARCHAR(64) NOT NULL UNIQUE,
    doctor_name VARCHAR(64) NOT NULL,
    department VARCHAR(64) NOT NULL,
    chief_complaint VARCHAR(255),
    visit_time DATETIME NOT NULL,
    notes TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED',
    queue_number INT DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE diagnosis_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    visit_id BIGINT NOT NULL,
    diagnosis_name VARCHAR(128) NOT NULL,
    diagnosis_type VARCHAR(32) NOT NULL DEFAULT 'PRIMARY',
    description TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE drug_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    drug_code VARCHAR(64) NOT NULL UNIQUE,
    drug_name VARCHAR(128) NOT NULL,
    specification VARCHAR(128) NOT NULL,
    manufacturer VARCHAR(128),
    unit VARCHAR(32) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    reserved_stock INT NOT NULL DEFAULT 0,
    warning_stock INT NOT NULL DEFAULT 10,
    unit_price DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    drug_type VARCHAR(20) NOT NULL DEFAULT 'ORAL',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE drug_inventory_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    drug_id BIGINT NOT NULL,
    change_type VARCHAR(32) NOT NULL,
    quantity INT NOT NULL,
    before_stock INT NOT NULL,
    after_stock INT NOT NULL,
    operator_name VARCHAR(64) NOT NULL,
    remark VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE prescription_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    visit_id BIGINT NOT NULL,
    drug_id BIGINT NOT NULL,
    drug_name VARCHAR(128) NOT NULL,
    dosage VARCHAR(64),
    frequency VARCHAR(64),
    days INT DEFAULT 1,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    line_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    drug_type VARCHAR(20) NOT NULL DEFAULT 'ORAL',
    payment_status VARCHAR(20) NOT NULL DEFAULT 'UNPAID',
    pickup_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    injection_status VARCHAR(20) NOT NULL DEFAULT 'NOT_REQUIRED',
    paid_at DATETIME NULL,
    picked_up_at DATETIME NULL,
    injection_completed_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO sys_role (id, role_code, role_name) VALUES
(1, 'ADMIN', '系统管理员'),
(2, 'DOCTOR', '医生'),
(3, 'PHARMACIST', '药师'),
(4, 'RECEPTION', '导诊/前台');

INSERT INTO sys_user (id, username, password, real_name, phone, id_card, department, pharmacy, reception_desk, status) VALUES
(1, 'admin', '123456', '系统管理员', '13800000001', '110101199001010011', NULL, NULL, NULL, 1),
(2, 'doctor', '123456', '张医生', '13800000002', '110101198502150022', '全科诊室', NULL, NULL, 1),
(3, 'pharmacist', '123456', '李药师', '13800000003', '110101199203200033', NULL, '西药房', NULL, 1),
(4, 'reception', '123456', '王前台', '13800000004', '110101199505250044', NULL, NULL, '一号前台', 1);

INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 4);

INSERT INTO patient (id, patient_no, name, gender, age, phone, id_card, address, allergy_history, medical_history) VALUES
(1, 'P2026001', '刘建国', '男', 58, '13900000001', '110101196801010011', '朝阳区社区路 1 号', '青霉素过敏', '高血压 5 年'),
(2, 'P2026002', '陈丽华', '女', 42, '13900000002', '110101198402020022', '海淀区健康街 2 号', '无', '胃炎病史');

INSERT INTO visit_record (id, patient_id, visit_no, doctor_name, department, chief_complaint, visit_time, notes) VALUES
(1, 1, 'V2026001', '张医生', '全科门诊', '头晕、血压波动', '2026-03-10 09:30:00', '建议规律监测血压'),
(2, 2, 'V2026002', '张医生', '消化内科', '胃部不适', '2026-03-11 14:00:00', '饮食清淡');

INSERT INTO diagnosis_record (visit_id, diagnosis_name, diagnosis_type, description) VALUES
(1, '高血压', 'PRIMARY', '继续控制血压'),
(2, '慢性胃炎', 'PRIMARY', '注意饮食规律');

INSERT INTO drug_info (id, drug_code, drug_name, specification, manufacturer, unit, stock, warning_stock, unit_price, drug_type) VALUES
(1, 'D2026001', '氨氯地平片', '5mg*28片', '社区制药厂', '盒', 118, 20, 26.50, 'ORAL'),
(2, 'D2026002', '奥美拉唑肠溶胶囊', '20mg*14粒', '健康药业', '盒', 80, 15, 32.00, 'ORAL'),
(3, 'D2026003', '注射用头孢曲松钠', '1.0g/支', '华北制药', '支', 50, 10, 48.00, 'INJECTION'),
(4, 'D2026004', '维生素C注射液', '5ml:1g/支', '石药集团', '支', 80, 15, 15.00, 'INJECTION'),
(5, 'D2026005', '地塞米松磷酸钠注射液', '1ml:5mg/支', '天津药业', '支', 60, 10, 12.00, 'INJECTION');

INSERT INTO drug_inventory_log (drug_id, change_type, quantity, before_stock, after_stock, operator_name, remark) VALUES
(1, 'IN', 120, 0, 120, '李药师', '期初入库'),
(2, 'IN', 80, 0, 80, '李药师', '期初入库'),
(3, 'IN', 50, 0, 50, '李药师', '注射药物期初入库'),
(4, 'IN', 80, 0, 80, '李药师', '注射药物期初入库'),
(5, 'IN', 60, 0, 60, '李药师', '注射药物期初入库'),
(1, 'OUT', 2, 120, 118, '李药师', '门诊发药');

INSERT INTO prescription_record (
    visit_id, drug_id, drug_name, dosage, frequency, days, quantity,
    unit_price, line_amount, drug_type, payment_status, pickup_status, injection_status, paid_at, picked_up_at
) VALUES
(1, 1, '氨氯地平片', '5mg', '每日一次', 14, 2, 26.50, 53.00, 'ORAL', 'PAID', 'DISPENSED', 'NOT_REQUIRED', '2026-03-10 09:50:00', '2026-03-10 09:52:00'),
(2, 2, '奥美拉唑肠溶胶囊', '20mg', '每日一次', 7, 1, 32.00, 32.00, 'ORAL', 'PAID', 'DISPENSED', 'NOT_REQUIRED', '2026-03-11 14:20:00', '2026-03-11 14:22:00');
