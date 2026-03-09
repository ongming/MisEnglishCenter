package com.center.manager.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity đại diện cho bảng "teachers" trong database.
 *
 * Mỗi dòng trong bảng = 1 giảng viên.
 * Lưu thông tin cá nhân: họ tên, SĐT, email, chuyên môn...
 *
 * --- Ánh xạ (Mapping) ---
 * Mỗi field trong class này tương ứng 1 cột trong bảng SQL.
 * Ví dụ: field "fullName" <-> cột "full_name" trong bảng teachers.
 *
 * --- Lombok ---
 * @Getter/@Setter: Tự sinh getter/setter cho tất cả field.
 * @NoArgsConstructor: Constructor rỗng (JPA bắt buộc).
 * @AllArgsConstructor: Constructor đầy đủ tham số.
 * @Builder: Cho phép tạo object kiểu Teacher.builder().fullName("abc").build()
 */
@Entity
@Table(name = "teachers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Teacher {

    /**
     * Khóa chính — tự tăng (AUTO_INCREMENT trong MySQL).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "teacher_id")
    private Long teacherId;

    /**
     * Họ và tên giảng viên.
     */
    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    /**
     * Số điện thoại.
     */
    @Column(name = "phone", length = 20)
    private String phone;

    /**
     * Email liên lạc.
     */
    @Column(name = "email", length = 150)
    private String email;

    /**
     * Chuyên môn: IELTS, TOEIC, GiaoTiep...
     */
    @Column(name = "specialty", length = 100)
    private String specialty;

    /**
     * Ngày bắt đầu làm việc.
     */
    @Column(name = "hire_date")
    private LocalDate hireDate;

    /**
     * Trạng thái: Active (đang dạy) / Inactive (đã nghỉ).
     * Dùng columnDefinition để khớp với ENUM trong MySQL.
     */
    @Column(name = "status", nullable = false,
            columnDefinition = "enum('Active','Inactive')")
    private String status;

    /**
     * Thời điểm tạo bản ghi — MySQL tự điền.
     */
    @Column(name = "created_at", updatable = false,
            insertable = false)
    private LocalDateTime createdAt;

    /**
     * Thời điểm cập nhật gần nhất — MySQL tự điền.
     */
    @Column(name = "updated_at", updatable = false,
            insertable = false)
    private LocalDateTime updatedAt;
}

