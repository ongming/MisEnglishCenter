-- =====================================================
-- SQL Script tao Database cho MIS English Center (MySQL 8.x)
-- =====================================================

CREATE DATABASE IF NOT EXISTS mis_language_center
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE mis_language_center;

-- =====================================================
-- TABLES
-- =====================================================

CREATE TABLE IF NOT EXISTS teachers (
    teacher_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name    VARCHAR(100) NOT NULL,
    phone        VARCHAR(20),
    email        VARCHAR(100),
    specialty    VARCHAR(200),
    hire_date    DATE,
    status       VARCHAR(20) DEFAULT 'Active',
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS students (
    student_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name         VARCHAR(100) NOT NULL,
    date_of_birth     DATE,
    gender            VARCHAR(10),
    phone             VARCHAR(20),
    email             VARCHAR(100),
    address           VARCHAR(255),
    registration_date DATE DEFAULT (CURRENT_DATE),
    status            VARCHAR(20) DEFAULT 'Active',
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS courses (
    course_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_name    VARCHAR(150) NOT NULL,
    description    TEXT,
    level          VARCHAR(50),
    duration       INT,
    duration_unit  VARCHAR(20) DEFAULT 'Week',
    fee            DECIMAL(15,2) DEFAULT 0.00,
    status         VARCHAR(20) DEFAULT 'Active',
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS rooms (
    room_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_name   VARCHAR(100) NOT NULL,
    capacity    INT DEFAULT 30,
    location    VARCHAR(255),
    status      VARCHAR(20) DEFAULT 'Active',
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    branch_id   BIGINT
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS classes (
    class_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    class_name  VARCHAR(100) NOT NULL,
    course_id   BIGINT NOT NULL,
    teacher_id  BIGINT,
    start_date  DATE NOT NULL,
    end_date    DATE,
    max_student INT DEFAULT 30,
    room_id     BIGINT,
    status      VARCHAR(20) DEFAULT 'Planned',
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(course_id),
    FOREIGN KEY (teacher_id) REFERENCES teachers(teacher_id),
    FOREIGN KEY (room_id) REFERENCES rooms(room_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS enrollments (
    enrollment_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id      BIGINT NOT NULL,
    class_id        BIGINT NOT NULL,
    enrollment_date DATE DEFAULT (CURRENT_DATE),
    status          VARCHAR(20) DEFAULT 'Enrolled',
    result          VARCHAR(20) DEFAULT 'NA',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_enrollment_student_class UNIQUE (student_id, class_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (class_id) REFERENCES classes(class_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS payments (
    payment_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id     BIGINT NOT NULL,
    enrollment_id  BIGINT,
    amount         DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    payment_date   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    payment_method VARCHAR(50) DEFAULT 'Cash',
    status         VARCHAR(20) DEFAULT 'Completed',
    reference_code VARCHAR(100),
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (enrollment_id) REFERENCES enrollments(enrollment_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS schedules (
    schedule_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    class_id    BIGINT NOT NULL,
    study_date  DATE NOT NULL,
    start_time  TIME NOT NULL,
    end_time    TIME NOT NULL,
    room_id     BIGINT,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (class_id) REFERENCES classes(class_id),
    FOREIGN KEY (room_id) REFERENCES rooms(room_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS attendances (
    attendance_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id    BIGINT NOT NULL,
    class_id      BIGINT NOT NULL,
    attend_date   DATE NOT NULL,
    status        VARCHAR(20) DEFAULT 'Present',
    note          VARCHAR(255),
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (class_id) REFERENCES classes(class_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS user_accounts (
    user_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(20) NOT NULL,
    teacher_id    BIGINT,
    student_id    BIGINT,
    staff_id      BIGINT,
    is_active     TINYINT(1) DEFAULT 1,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES teachers(teacher_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id)
) ENGINE=InnoDB;

-- =====================================================
-- RESET DATA FOR RE-RUN
-- =====================================================
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE attendances;
TRUNCATE TABLE schedules;
TRUNCATE TABLE payments;
TRUNCATE TABLE user_accounts;
TRUNCATE TABLE enrollments;
TRUNCATE TABLE classes;
TRUNCATE TABLE rooms;
TRUNCATE TABLE courses;
TRUNCATE TABLE students;
TRUNCATE TABLE teachers;
SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================
-- SEED DATA (NO DUPLICATE)
-- =====================================================

INSERT INTO teachers (full_name, phone, email, specialty, hire_date, status) VALUES
('Nguyen Van An', '0901000001', 'an.nguyen@mis.edu.vn', 'IELTS, TOEFL', '2022-01-10', 'Active'),
('Tran Thi Bich', '0901000002', 'bich.tran@mis.edu.vn', 'Communication', '2022-03-20', 'Active'),
('Le Hoang Cuong', '0901000003', 'cuong.le@mis.edu.vn', 'TOEIC', '2022-05-15', 'Active'),
('Pham Gia Duy', '0901000004', 'duy.pham@mis.edu.vn', 'Grammar', '2022-08-01', 'Active'),
('Hoang Quynh Nhu', '0901000005', 'nhu.hoang@mis.edu.vn', 'Kids English', '2023-02-12', 'Active'),
('Do Minh Khang', '0901000006', 'khang.do@mis.edu.vn', 'Business English', '2023-07-05', 'Active'),
('Bui Thanh Long', '0901000007', 'long.bui@mis.edu.vn', 'Pronunciation', '2023-09-18', 'Active'),
('Ngo Yen Nhi', '0901000008', 'nhi.ngo@mis.edu.vn', 'Public Speaking', '2024-01-08', 'Active');

INSERT INTO students (full_name, date_of_birth, gender, phone, email, address, registration_date, status) VALUES
('Pham Minh Duc', '2002-05-15', 'Nam', '0912000001', 'duc.pham@student.mis.vn', 'Quan 10, TP HCM', '2025-01-10', 'Active'),
('Hoang Thi Emma', '2003-08-22', 'Nu', '0912000002', 'emma.hoang@student.mis.vn', 'Quan 5, TP HCM', '2025-01-12', 'Active'),
('Vo Thanh Phong', '2001-12-01', 'Nam', '0912000003', 'phong.vo@student.mis.vn', 'Quan 3, TP HCM', '2025-01-15', 'Active'),
('Ngo Ngoc Quynh', '2002-03-30', 'Nu', '0912000004', 'quynh.ngo@student.mis.vn', 'Quan 1, TP HCM', '2025-01-16', 'Active'),
('Dang Tuan Rang', '2003-11-11', 'Nam', '0912000005', 'rang.dang@student.mis.vn', 'Tan Binh, TP HCM', '2025-01-20', 'Active'),
('Le Gia Han', '2004-04-09', 'Nu', '0912000006', 'han.le@student.mis.vn', 'Thu Duc, TP HCM', '2025-01-25', 'Active'),
('Tran Quoc Bao', '2001-09-07', 'Nam', '0912000007', 'bao.tran@student.mis.vn', 'Go Vap, TP HCM', '2025-01-28', 'Active'),
('Phan Thuy Linh', '2002-07-17', 'Nu', '0912000008', 'linh.phan@student.mis.vn', 'Phu Nhuan, TP HCM', '2025-02-01', 'Active'),
('Mai Gia Huy', '2003-10-21', 'Nam', '0912000009', 'huy.mai@student.mis.vn', 'Quan 7, TP HCM', '2025-02-05', 'Active'),
('Nguyen Khanh Vy', '2004-06-03', 'Nu', '0912000010', 'vy.nguyen@student.mis.vn', 'Quan 4, TP HCM', '2025-02-08', 'Active'),
('Doan Minh Tri', '2001-01-19', 'Nam', '0912000011', 'tri.doan@student.mis.vn', 'Binh Thanh, TP HCM', '2025-02-12', 'Active'),
('Bui Anh Thu', '2002-02-26', 'Nu', '0912000012', 'thu.bui@student.mis.vn', 'Tan Phu, TP HCM', '2025-02-15', 'Active'),
('Tran Hoai Nam', '2003-03-14', 'Nam', '0912000013', 'nam.tran@student.mis.vn', 'Hoc Mon, TP HCM', '2025-02-20', 'Active'),
('Vu Thanh Truc', '2004-12-30', 'Nu', '0912000014', 'truc.vu@student.mis.vn', 'Nha Be, TP HCM', '2025-02-22', 'Active'),
('Ly Tuan Kiet', '2002-10-10', 'Nam', '0912000015', 'kiet.ly@student.mis.vn', 'Quan 8, TP HCM', '2025-02-25', 'Active'),
('Nguyen My Tien', '2003-05-05', 'Nu', '0912000016', 'tien.nguyen@student.mis.vn', 'Quan 6, TP HCM', '2025-02-27', 'Active');

INSERT INTO courses (course_name, description, level, duration, duration_unit, fee, status) VALUES
('IELTS Foundation', 'Target 5.0-5.5', 'Beginner', 12, 'Week', 5000000.00, 'Active'),
('IELTS Advanced', 'Target 6.5-7.5', 'Advanced', 16, 'Week', 7500000.00, 'Active'),
('TOEIC 450+', 'Target 450+', 'Intermediate', 10, 'Week', 3500000.00, 'Active'),
('TOEIC 700+', 'Target 700+', 'Upper-Intermediate', 12, 'Week', 4500000.00, 'Active'),
('English Communication', 'Daily speaking', 'Beginner', 8, 'Week', 3000000.00, 'Active'),
('Business English', 'Workplace communication', 'Intermediate', 10, 'Week', 4200000.00, 'Active');

INSERT INTO rooms (room_name, capacity, location, status, branch_id) VALUES
('A101', 30, 'Floor 1 - Campus A', 'Active', 1),
('A102', 25, 'Floor 1 - Campus A', 'Active', 1),
('A201', 35, 'Floor 2 - Campus A', 'Active', 1),
('A202', 20, 'Floor 2 - Campus A', 'Active', 1),
('B101', 30, 'Floor 1 - Campus B', 'Active', 2),
('B102', 28, 'Floor 1 - Campus B', 'Active', 2),
('B201', 32, 'Floor 2 - Campus B', 'Active', 2),
('B202', 24, 'Floor 2 - Campus B', 'Active', 2);

INSERT INTO classes (class_name, course_id, teacher_id, start_date, end_date, max_student, room_id, status) VALUES
('IELTS-F-01', 1, 1, '2025-03-01', '2025-05-31', 25, 1, 'Ongoing'),
('IELTS-F-02', 1, 4, '2025-03-10', '2025-06-10', 25, 2, 'Ongoing'),
('IELTS-A-01', 2, 1, '2025-04-01', '2025-07-31', 20, 3, 'Ongoing'),
('TOEIC-450-01', 3, 3, '2025-03-15', '2025-05-20', 30, 5, 'Ongoing'),
('TOEIC-700-01', 4, 3, '2025-04-05', '2025-06-30', 28, 6, 'Ongoing'),
('COMM-01', 5, 2, '2025-03-12', '2025-05-12', 22, 4, 'Ongoing'),
('COMM-02', 5, 8, '2025-04-08', '2025-06-08', 24, 8, 'Ongoing'),
('BUS-EN-01', 6, 6, '2025-03-20', '2025-06-01', 20, 7, 'Ongoing'),
('IELTS-F-03', 1, 7, '2025-05-01', '2025-07-31', 26, 1, 'Planned'),
('TOEIC-450-02', 3, 5, '2025-05-05', '2025-07-05', 30, 5, 'Planned');

INSERT INTO enrollments (student_id, class_id, enrollment_date, status, result) VALUES
(1, 1, '2025-02-20', 'Enrolled', 'NA'),
(2, 1, '2025-02-22', 'Enrolled', 'NA'),
(3, 1, '2025-02-23', 'Enrolled', 'NA'),
(4, 2, '2025-02-24', 'Enrolled', 'NA'),
(5, 2, '2025-02-25', 'Enrolled', 'NA'),
(6, 2, '2025-02-26', 'Enrolled', 'NA'),
(3, 3, '2025-03-20', 'Enrolled', 'NA'),
(7, 3, '2025-03-21', 'Enrolled', 'NA'),
(8, 3, '2025-03-22', 'Enrolled', 'NA'),
(1, 4, '2025-03-10', 'Enrolled', 'NA'),
(9, 4, '2025-03-11', 'Enrolled', 'NA'),
(10, 4, '2025-03-12', 'Enrolled', 'NA'),
(11, 5, '2025-03-30', 'Enrolled', 'NA'),
(12, 5, '2025-03-31', 'Enrolled', 'NA'),
(13, 5, '2025-04-01', 'Enrolled', 'NA'),
(2, 6, '2025-03-05', 'Enrolled', 'NA'),
(4, 6, '2025-03-06', 'Enrolled', 'NA'),
(14, 6, '2025-03-07', 'Enrolled', 'NA'),
(15, 7, '2025-03-25', 'Enrolled', 'NA'),
(16, 7, '2025-03-26', 'Enrolled', 'NA'),
(5, 7, '2025-03-27', 'Enrolled', 'NA'),
(6, 8, '2025-03-15', 'Enrolled', 'NA'),
(7, 8, '2025-03-16', 'Enrolled', 'NA'),
(8, 8, '2025-03-17', 'Enrolled', 'NA');

INSERT INTO schedules (class_id, study_date, start_time, end_time, room_id) VALUES
(1, '2025-03-03', '18:00:00', '20:00:00', 1),
(1, '2025-03-05', '18:00:00', '20:00:00', 1),
(1, '2025-03-07', '18:00:00', '20:00:00', 1),
(2, '2025-03-10', '18:00:00', '20:00:00', 2),
(2, '2025-03-12', '18:00:00', '20:00:00', 2),
(3, '2025-04-01', '19:00:00', '21:00:00', 3),
(3, '2025-04-03', '19:00:00', '21:00:00', 3),
(4, '2025-03-15', '08:00:00', '10:30:00', 5),
(4, '2025-03-16', '08:00:00', '10:30:00', 5),
(5, '2025-04-05', '08:00:00', '10:30:00', 6),
(5, '2025-04-06', '08:00:00', '10:30:00', 6),
(6, '2025-03-12', '17:30:00', '19:30:00', 4),
(6, '2025-03-14', '17:30:00', '19:30:00', 4),
(7, '2025-04-08', '17:30:00', '19:30:00', 8),
(7, '2025-04-10', '17:30:00', '19:30:00', 8),
(8, '2025-03-20', '19:15:00', '21:15:00', 7),
(8, '2025-03-22', '19:15:00', '21:15:00', 7);

INSERT INTO attendances (student_id, class_id, attend_date, status, note) VALUES
(1, 1, '2025-03-03', 'Present', NULL),
(2, 1, '2025-03-03', 'Present', NULL),
(3, 1, '2025-03-03', 'Late', 'Den muon 10 phut'),
(4, 2, '2025-03-10', 'Present', NULL),
(5, 2, '2025-03-10', 'Absent', 'Nghi co phep'),
(6, 2, '2025-03-10', 'Present', NULL),
(1, 4, '2025-03-15', 'Present', NULL),
(9, 4, '2025-03-15', 'Present', NULL),
(10, 4, '2025-03-15', 'Present', NULL),
(2, 6, '2025-03-12', 'Present', NULL),
(4, 6, '2025-03-12', 'Present', NULL),
(14, 6, '2025-03-12', 'Absent', 'Nghi khong phep');

-- Mat khau cho tat ca tai khoan mau: 123456
INSERT INTO user_accounts (username, password_hash, role, teacher_id, student_id, staff_id, is_active) VALUES
('teacher1', '$2a$10$Xkkhj2vgYZXZS6Xu7AmgKeTFcHyQrAnXT3HKO83HBDxLKXDKH/f1G', 'Teacher', 1, NULL, NULL, 1),
('teacher2', '$2a$10$Xkkhj2vgYZXZS6Xu7AmgKeTFcHyQrAnXT3HKO83HBDxLKXDKH/f1G', 'Teacher', 2, NULL, NULL, 1),
('teacher3', '$2a$10$Xkkhj2vgYZXZS6Xu7AmgKeTFcHyQrAnXT3HKO83HBDxLKXDKH/f1G', 'Teacher', 3, NULL, NULL, 1),
('teacher4', '$2a$10$Xkkhj2vgYZXZS6Xu7AmgKeTFcHyQrAnXT3HKO83HBDxLKXDKH/f1G', 'Teacher', 4, NULL, NULL, 1),
('student1', '$2a$10$Xkkhj2vgYZXZS6Xu7AmgKeTFcHyQrAnXT3HKO83HBDxLKXDKH/f1G', 'Student', NULL, 1, NULL, 1),
('student2', '$2a$10$Xkkhj2vgYZXZS6Xu7AmgKeTFcHyQrAnXT3HKO83HBDxLKXDKH/f1G', 'Student', NULL, 2, NULL, 1),
('student3', '$2a$10$Xkkhj2vgYZXZS6Xu7AmgKeTFcHyQrAnXT3HKO83HBDxLKXDKH/f1G', 'Student', NULL, 3, NULL, 1),
('student4', '$2a$10$Xkkhj2vgYZXZS6Xu7AmgKeTFcHyQrAnXT3HKO83HBDxLKXDKH/f1G', 'Student', NULL, 4, NULL, 1),
('student5', '$2a$10$Xkkhj2vgYZXZS6Xu7AmgKeTFcHyQrAnXT3HKO83HBDxLKXDKH/f1G', 'Student', NULL, 5, NULL, 1),
('student6', '$2a$10$Xkkhj2vgYZXZS6Xu7AmgKeTFcHyQrAnXT3HKO83HBDxLKXDKH/f1G', 'Student', NULL, 6, NULL, 1),
('admin', '$2a$10$Xkkhj2vgYZXZS6Xu7AmgKeTFcHyQrAnXT3HKO83HBDxLKXDKH/f1G', 'Admin', NULL, NULL, NULL, 1);

