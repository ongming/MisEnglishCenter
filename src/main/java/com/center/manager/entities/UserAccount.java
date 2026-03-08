package com.center.manager.entities;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity đại diện cho bảng "user_accounts" trong database.
 *
 * Mỗi dòng trong bảng = 1 tài khoản đăng nhập.
 * Cột "role" quyết định người dùng là Admin, Teacher, Student hay Staff.
 *
 * --- Ánh xạ (Mapping) ---
 * Mỗi thuộc tính (field) trong class này = 1 cột trong bảng SQL.
 * Ví dụ: field "username" <-> cột "username" trong bảng user_accounts.
 *
 * --- Lombok ---
 * @Getter / @Setter  : Tự sinh getUsername(), setUsername()... không cần viết tay.
 * @NoArgsConstructor  : Tự sinh constructor rỗng (JPA bắt buộc phải có).
 * @AllArgsConstructor : Tự sinh constructor đầy đủ tham số.
 * @Builder            : Cho phép tạo object kiểu UserAccount.builder().username("abc").build()
 */
@Entity                          // Đánh dấu: đây là 1 Entity (đại diện 1 bảng DB)
@Table(name = "user_accounts")   // Tên bảng trong MySQL là "user_accounts"
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAccount {

    /**
     * Khóa chính (Primary Key), tự tăng (AUTO_INCREMENT trong MySQL).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    /**
     * Tên đăng nhập - phải là duy nhất (UNIQUE trong DB).
     */
    @Column(name = "username", nullable = false, unique = true, length = 80)
    private String username;

    /**
     * Mật khẩu đã mã hóa bằng BCrypt.
     * KHÔNG BAO GIỜ lưu mật khẩu gốc (plain text) vào DB.
     */
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    /**
     * Vai trò: Admin, Teacher, Student, Staff.
     * Dùng để phân quyền → mở màn hình tương ứng sau khi đăng nhập.
     *
     * columnDefinition = "enum(...)" → bảo Hibernate rằng cột này là ENUM trong MySQL,
     * không phải VARCHAR. Nếu không khai báo, Hibernate sẽ validate sai kiểu.
     */
    @Column(name = "role", nullable = false,
            columnDefinition = "enum('Admin','Teacher','Student','Staff')")
    private String role;

    /**
     * Nếu role = "Teacher", trỏ đến teacher_id trong bảng teachers.
     * Nếu không phải Teacher thì = null.
     */
    @Column(name = "teacher_id")
    private Long teacherId;

    /**
     * Nếu role = "Student", trỏ đến student_id trong bảng students.
     * Nếu không phải Student thì = null.
     */
    @Column(name = "student_id")
    private Long studentId;

    /**
     * Nếu role = "Staff", trỏ đến staff_id trong bảng staffs.
     * Nếu không phải Staff thì = null.
     */
    @Column(name = "staff_id")
    private Long staffId;

    /**
     * Tài khoản có đang hoạt động không?
     * true (1) = hoạt động, false (0) = bị khóa.
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
}


