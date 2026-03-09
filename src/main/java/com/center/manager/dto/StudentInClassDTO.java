package com.center.manager.dto;

import lombok.*;

/**
 * DTO cho bảng "Danh sách học viên" trong tab Lớp đang dạy.
 *
 * Hiển thị thông tin học viên đã ghi danh vào 1 lớp cụ thể.
 *
 * Ví dụ:
 *   | Họ và tên      | SĐT         | Email             | Ngày ghi danh | Trạng thái |
 *   | Phạm Gia Bảo  | 0933000001  | baopg01@mail.com  | 01/02/2026    | Enrolled   |
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentInClassDTO {

    private Long studentId;         // ID học viên
    private String fullName;        // Họ và tên
    private String phone;           // SĐT
    private String email;           // Email
    private String enrollmentDate;  // Ngày ghi danh (String để hiển thị)
    private String enrollmentStatus; // Trạng thái ghi danh: Enrolled, Dropped, Completed
}

