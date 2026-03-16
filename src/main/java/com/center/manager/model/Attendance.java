package com.center.manager.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity đại diện cho bảng attendances (điểm danh) trong database.
 * Lưu thông tin điểm danh của từng học viên theo từng buổi học của lớp.
 * - studentId: ID học viên
 * - classId: ID lớp học
 * - attendDate: Ngày điểm danh
 * - status: Trạng thái (Present, Absent, ...)
 * - note: Ghi chú
 * - createdAt: Thời điểm tạo bản ghi
 */
@Entity
@Table(name = "attendances")
public class Attendance {
    // Khóa chính (tự tăng)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    private Long attendanceId;

    // ID học viên được điểm danh
    @Column(name = "student_id", nullable = false)
    private Long studentId;

    // ID lớp học
    @Column(name = "class_id", nullable = false)
    private Long classId;

    // Ngày điểm danh
    @Column(name = "attend_date", nullable = false)
    private LocalDate attendDate;

    // Trạng thái điểm danh (Present, Absent, ...)
    @Column(name = "status", nullable = false)
    private String status;

    // Ghi chú thêm
    @Column(name = "note")
    private String note;

    // Thời điểm tạo bản ghi
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Attendance() {}

    /**
     * Hàm callback tự động set createdAt và status mặc định khi insert mới
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = "Present";
    }

    // Getter/setter cho các trường dữ liệu
    public Long getAttendanceId() { return attendanceId; }
    public void setAttendanceId(Long attendanceId) { this.attendanceId = attendanceId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }

    public LocalDate getAttendDate() { return attendDate; }
    public void setAttendDate(LocalDate attendDate) { this.attendDate = attendDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
