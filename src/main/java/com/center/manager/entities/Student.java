package com.center.manager.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity đại diện cho bảng "students" trong database.
 *
 * Mỗi dòng = 1 học viên đăng ký tại trung tâm.
 * Lưu thông tin: họ tên, ngày sinh, giới tính, SĐT, email, địa chỉ...
 *
 * --- Tại sao cần Student entity cho chức năng Giảng viên? ---
 * Vì khi giảng viên xem danh sách lớp → cần hiện danh sách HỌC VIÊN trong lớp.
 * Dữ liệu học viên nằm trong bảng students.
 */
@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Long studentId;

    /**
     * Họ và tên học viên.
     */
    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    /**
     * Ngày sinh.
     */
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    /**
     * Giới tính: Male / Female / Other.
     */
    @Column(name = "gender",
            columnDefinition = "enum('Male','Female','Other')")
    private String gender;

    /**
     * Số điện thoại.
     */
    @Column(name = "phone", length = 20)
    private String phone;

    /**
     * Email.
     */
    @Column(name = "email", length = 150)
    private String email;

    /**
     * Địa chỉ.
     */
    @Column(name = "address", length = 255)
    private String address;

    /**
     * Ngày đăng ký học tại trung tâm.
     */
    @Column(name = "registration_date", nullable = false)
    private LocalDate registrationDate;

    /**
     * Trạng thái: Active / Inactive.
     */
    @Column(name = "status", nullable = false,
            columnDefinition = "enum('Active','Inactive')")
    private String status;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", updatable = false, insertable = false)
    private LocalDateTime updatedAt;
}

