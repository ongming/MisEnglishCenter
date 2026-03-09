package com.center.manager.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity đại diện cho bảng "attendances" trong database.
 *
 * Mỗi dòng = 1 bản ghi điểm danh: Học viên X ở lớp Y ngày Z → Present/Absent/Late.
 *
 * Ví dụ:
 *   Phạm Gia Bảo | lớp COM-A1-0201 | ngày 10/02/2026 | Present (có mặt)
 *
 * --- Dùng cho chức năng Giảng viên ---
 * GV chọn lớp + ngày → hiện danh sách HV → tick Present/Absent/Late → Lưu.
 */
@Entity
@Table(name = "attendances")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    private Long attendanceId;

    /**
     * Học viên được điểm danh.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    /**
     * Lớp học.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private ClassEntity classEntity;

    /**
     * Ngày điểm danh.
     */
    @Column(name = "attend_date", nullable = false)
    private LocalDate attendDate;

    /**
     * Trạng thái: Present (có mặt), Absent (vắng), Late (trễ).
     */
    @Column(name = "status", nullable = false,
            columnDefinition = "enum('Present','Absent','Late')")
    private String status;

    /**
     * Ghi chú (ví dụ: "Đến trễ 10 phút", "Xin phép nghỉ bệnh").
     */
    @Column(name = "note", length = 255)
    private String note;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;
}

