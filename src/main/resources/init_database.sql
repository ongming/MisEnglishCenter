-- =====================================================
-- SQL Script tạo Database cho MIS English Center
-- Sử dụng MySQL 8.x
-- =====================================================

-- Tạo database
CREATE DATABASE IF NOT EXISTS mis_language_center
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE mis_language_center;

-- =====================================================
-- 1. Bảng TEACHERS (Giảng viên)
-- =====================================================
CREATE TABLE IF NOT EXISTS teachers (
    teacher_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name    VARCHAR(100) NOT NULL,
    phone        VARCHAR(20),
    email        VARCHAR(100),
    specialization VARCHAR(200),
    status       VARCHAR(20) DEFAULT 'Active'
) ENGINE=InnoDB;

-- =====================================================
-- 2. Bảng STUDENTS (Sinh viên)
-- =====================================================
CREATE TABLE IF NOT EXISTS students (
    student_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name         VARCHAR(100) NOT NULL,
    date_of_birth     DATE,
    gender            VARCHAR(10),
    phone             VARCHAR(20),
    email             VARCHAR(100),
    address           VARCHAR(255),
    registration_date DATE DEFAULT (CURRENT_DATE),
    status            VARCHAR(20) DEFAULT 'Active'
) ENGINE=InnoDB;

-- =====================================================
-- 3. Bảng COURSES (Khóa học)
-- =====================================================
CREATE TABLE IF NOT EXISTS courses (
    course_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_name    VARCHAR(150) NOT NULL,
    description    TEXT,
    duration_hours INT,
    tuition_fee    DOUBLE,
    status         VARCHAR(20) DEFAULT 'Active'
) ENGINE=InnoDB;

-- =====================================================
-- 4. Bảng CLASSES (Lớp học)
-- =====================================================
CREATE TABLE IF NOT EXISTS classes (
    class_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    class_name  VARCHAR(100) NOT NULL,
    course_id   BIGINT,
    teacher_id  BIGINT,
    start_date  DATE,
    end_date    DATE,
    max_student INT DEFAULT 30,
    status      VARCHAR(20) DEFAULT 'Active',
    FOREIGN KEY (course_id) REFERENCES courses(course_id),
    FOREIGN KEY (teacher_id) REFERENCES teachers(teacher_id)
) ENGINE=InnoDB;

-- =====================================================
-- 5. Bảng ENROLLMENTS (Ghi danh)
-- =====================================================
CREATE TABLE IF NOT EXISTS enrollments (
    enrollment_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id      BIGINT NOT NULL,
    class_id        BIGINT NOT NULL,
    enrollment_date DATE DEFAULT (CURRENT_DATE),
    status          VARCHAR(20) DEFAULT 'Active',
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (class_id) REFERENCES classes(class_id)
) ENGINE=InnoDB;

-- =====================================================
-- 6. Bảng SCHEDULES (Lịch học)
-- =====================================================
CREATE TABLE IF NOT EXISTS schedules (
    schedule_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    class_id    BIGINT NOT NULL,
    study_date  DATE NOT NULL,
    start_time  TIME,
    end_time    TIME,
    FOREIGN KEY (class_id) REFERENCES classes(class_id)
) ENGINE=InnoDB;

-- =====================================================
-- 7. Bảng ATTENDANCES (Điểm danh)
-- =====================================================
CREATE TABLE IF NOT EXISTS attendances (
    attendance_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id    BIGINT NOT NULL,
    class_id      BIGINT NOT NULL,
    attend_date   DATE NOT NULL,
    status        VARCHAR(20) DEFAULT 'Present',
    note          VARCHAR(255),
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (class_id) REFERENCES classes(class_id)
) ENGINE=InnoDB;

-- =====================================================
-- 8. Bảng USER_ACCOUNTS (Tài khoản đăng nhập)
-- =====================================================
CREATE TABLE IF NOT EXISTS user_accounts (
    user_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(20) NOT NULL COMMENT 'Admin, Teacher, Student, Staff',
    teacher_id    BIGINT,
    student_id    BIGINT,
    staff_id      BIGINT,
    is_active     TINYINT(1) DEFAULT 1,
    FOREIGN KEY (teacher_id) REFERENCES teachers(teacher_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id)
) ENGINE=InnoDB;


-- =====================================================
-- DỮ LIỆU MẪU
-- =====================================================

-- -------------------------------------------------------
-- Giảng viên
-- -------------------------------------------------------
INSERT INTO teachers (full_name, phone, email, specialization, status) VALUES
('Nguyễn Văn An',    '0901111111', 'an.nguyen@email.com',    'IELTS, TOEFL',     'Active'),
('Trần Thị Bích',    '0902222222', 'bich.tran@email.com',    'Giao tiếp',        'Active'),
('Lê Hoàng Cường',   '0903333333', 'cuong.le@email.com',     'Ngữ pháp, TOEIC',  'Active');

-- -------------------------------------------------------
-- Sinh viên
-- -------------------------------------------------------
INSERT INTO students (full_name, date_of_birth, gender, phone, email, address, registration_date, status) VALUES
('Phạm Minh Đức',    '2002-05-15', 'Nam',  '0911111111', 'duc.pham@email.com',    '123 Lý Thường Kiệt, Q.10, TP.HCM',  '2025-01-10', 'Active'),
('Hoàng Thị Ema',    '2003-08-22', 'Nữ',   '0912222222', 'ema.hoang@email.com',   '456 Nguyễn Trãi, Q.5, TP.HCM',       '2025-01-12', 'Active'),
('Võ Thanh Phong',   '2001-12-01', 'Nam',  '0913333333', 'phong.vo@email.com',    '789 Điện Biên Phủ, Q.3, TP.HCM',     '2025-02-01', 'Active'),
('Ngô Ngọc Quỳnh',  '2002-03-30', 'Nữ',   '0914444444', 'quynh.ngo@email.com',   '12 Pasteur, Q.1, TP.HCM',            '2025-02-15', 'Active'),
('Đặng Tuấn Rạng',  '2003-11-11', 'Nam',  '0915555555', 'rang.dang@email.com',   '34 CMT8, Q. Tân Bình, TP.HCM',       '2025-03-01', 'Active');

-- -------------------------------------------------------
-- Khóa học
-- -------------------------------------------------------
INSERT INTO courses (course_name, description, duration_hours, tuition_fee, status) VALUES
('IELTS Foundation',     'Khóa luyện thi IELTS cơ bản (target 5.0-5.5)',  60, 5000000, 'Active'),
('IELTS Advanced',       'Khóa luyện thi IELTS nâng cao (target 6.5-7.0)', 80, 7000000, 'Active'),
('TOEIC 450+',           'Luyện thi TOEIC mục tiêu 450+',                  48, 3500000, 'Active'),
('English Communication','Tiếng Anh giao tiếp hàng ngày',                  40, 3000000, 'Active');

-- -------------------------------------------------------
-- Lớp học
-- -------------------------------------------------------
INSERT INTO classes (class_name, course_id, teacher_id, start_date, end_date, max_student, status) VALUES
('IELTS-F-01',  1, 1, '2025-03-01', '2025-05-31', 25, 'Active'),
('IELTS-A-01',  2, 1, '2025-04-01', '2025-07-31', 20, 'Active'),
('TOEIC-01',    3, 3, '2025-03-15', '2025-05-15', 30, 'Active'),
('COMM-01',     4, 2, '2025-03-10', '2025-05-10', 20, 'Active');

-- -------------------------------------------------------
-- Ghi danh (enrollments)
-- -------------------------------------------------------
INSERT INTO enrollments (student_id, class_id, enrollment_date, status) VALUES
-- Lớp IELTS-F-01
(1, 1, '2025-02-20', 'Active'),
(2, 1, '2025-02-22', 'Active'),
(3, 1, '2025-02-25', 'Active'),
-- Lớp IELTS-A-01
(3, 2, '2025-03-20', 'Active'),
(4, 2, '2025-03-22', 'Active'),
-- Lớp TOEIC-01
(1, 3, '2025-03-10', 'Active'),
(5, 3, '2025-03-12', 'Active'),
-- Lớp COMM-01
(2, 4, '2025-03-05', 'Active'),
(4, 4, '2025-03-07', 'Active'),
(5, 4, '2025-03-08', 'Active');

-- -------------------------------------------------------
-- Lịch học (schedules) — một số buổi mẫu
-- -------------------------------------------------------
INSERT INTO schedules (class_id, study_date, start_time, end_time) VALUES
-- IELTS-F-01 (T2, T4, T6)
(1, '2025-03-03', '18:00:00', '20:00:00'),
(1, '2025-03-05', '18:00:00', '20:00:00'),
(1, '2025-03-07', '18:00:00', '20:00:00'),
(1, '2025-03-10', '18:00:00', '20:00:00'),
(1, '2025-03-12', '18:00:00', '20:00:00'),
-- IELTS-A-01 (T3, T5)
(2, '2025-04-01', '19:00:00', '21:00:00'),
(2, '2025-04-03', '19:00:00', '21:00:00'),
(2, '2025-04-08', '19:00:00', '21:00:00'),
-- TOEIC-01 (T7, CN)
(3, '2025-03-15', '08:00:00', '10:30:00'),
(3, '2025-03-16', '08:00:00', '10:30:00'),
(3, '2025-03-22', '08:00:00', '10:30:00'),
(3, '2025-03-23', '08:00:00', '10:30:00'),
-- COMM-01 (T2, T4)
(4, '2025-03-10', '17:30:00', '19:30:00'),
(4, '2025-03-12', '17:30:00', '19:30:00'),
(4, '2025-03-17', '17:30:00', '19:30:00'),
(4, '2025-03-19', '17:30:00', '19:30:00');

-- -------------------------------------------------------
-- Điểm danh (attendances) — dữ liệu mẫu
-- -------------------------------------------------------
INSERT INTO attendances (student_id, class_id, attend_date, status, note) VALUES
-- IELTS-F-01 ngày 03/03
(1, 1, '2025-03-03', 'Present', NULL),
(2, 1, '2025-03-03', 'Present', NULL),
(3, 1, '2025-03-03', 'Late',    'Đến muộn 10 phút'),
-- IELTS-F-01 ngày 05/03
(1, 1, '2025-03-05', 'Present', NULL),
(2, 1, '2025-03-05', 'Absent',  'Nghỉ có phép'),
(3, 1, '2025-03-05', 'Present', NULL),
-- TOEIC-01 ngày 15/03
(1, 3, '2025-03-15', 'Present', NULL),
(5, 3, '2025-03-15', 'Present', NULL),
-- COMM-01 ngày 10/03
(2, 4, '2025-03-10', 'Present', NULL),
(4, 4, '2025-03-10', 'Present', NULL),
(5, 4, '2025-03-10', 'Absent',  'Nghỉ không phép');

-- -------------------------------------------------------
-- Tài khoản đăng nhập
-- Mật khẩu: 123456 (BCrypt hash)
-- -------------------------------------------------------
INSERT INTO user_accounts (username, password_hash, role, teacher_id, student_id, staff_id, is_active) VALUES
-- Tài khoản giảng viên
('teacher1', '$2a$10$Xkkhj2vgYZXZS6Xu7AmgKeTFcHyQrAnXT3HKO83HBDxLKXDKH/f1G', 'Teacher', 1, NULL, NULL, 1),
('teacher2', '$2a$10$Xkkhj2vgYZXZS6Xu7AmgKeTFcHyQrAnXT3HKO83HBDxLKXDKH/f1G', 'Teacher', 2, NULL, NULL, 1),
('teacher3', '$2a$10$Xkkhj2vgYZXZS6Xu7AmgKeTFcHyQrAnXT3HKO83HBDxLKXDKH/f1G', 'Teacher', 3, NULL, NULL, 1),
-- Tài khoản sinh viên
('student1', '$2a$10$Xkkhj2vgYZXZS6Xu7AmgKeTFcHyQrAnXT3HKO83HBDxLKXDKH/f1G', 'Student', NULL, 1, NULL, 1),
('student2', '$2a$10$Xkkhj2vgYZXZS6Xu7AmgKeTFcHyQrAnXT3HKO83HBDxLKXDKH/f1G', 'Student', NULL, 2, NULL, 1),
('student3', '$2a$10$Xkkhj2vgYZXZS6Xu7AmgKeTFcHyQrAnXT3HKO83HBDxLKXDKH/f1G', 'Student', NULL, 3, NULL, 1),
('student4', '$2a$10$Xkkhj2vgYZXZS6Xu7AmgKeTFcHyQrAnXT3HKO83HBDxLKXDKH/f1G', 'Student', NULL, 4, NULL, 1),
('student5', '$2a$10$Xkkhj2vgYZXZS6Xu7AmgKeTFcHyQrAnXT3HKO83HBDxLKXDKH/f1G', 'Student', NULL, 5, NULL, 1),
-- Tài khoản admin
('admin',    '$2a$10$Xkkhj2vgYZXZS6Xu7AmgKeTFcHyQrAnXT3HKO83HBDxLKXDKH/f1G', 'Admin',   NULL, NULL, NULL, 1);

-- =====================================================
-- XONG! Tất cả mật khẩu đều là: 123456
-- =====================================================

