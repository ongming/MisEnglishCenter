package com.center.manager.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity đại diện cho bảng "enrollments" trong database.
 *
 * Enrollment = Ghi danh — nối 1 Học viên (Student) với 1 Lớp học (ClassEntity).
 * Khi học viên đăng ký vào lớp → tạo 1 dòng trong bảng enrollments.
 *
 * Ví dụ:
 *   Student "Phạm Gia Bảo" ghi danh vào lớp "COM-A1-0201" ngày 01/02/2026.
 *
 * --- Quan hệ ---
 * Mỗi enrollment liên kết 1 Student + 1 ClassEntity.
 */
@Entity
@Table(name = "enrollments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_id")
    private Long enrollmentId;

    /**
     * Học viên đăng ký.
     * @ManyToOne = Nhiều enrollment → 1 student (1 HV có thể đăng ký nhiều lớp).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    /**
     * Lớp học được đăng ký.
     * @ManyToOne = Nhiều enrollment → 1 class (1 lớp có nhiều HV).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private ClassEntity classEntity;

    /**
     * Ngày ghi danh.
     */
    @Column(name = "enrollment_date", nullable = false)
    private LocalDate enrollmentDate;

    /**
     * Trạng thái ghi danh: Enrolled (đang học), Dropped (bỏ), Completed (hoàn thành).
     */
    @Column(name = "status", nullable = false,
            columnDefinition = "enum('Enrolled','Dropped','Completed')")
    private String status;

    /**
     * Kết quả: Pass (đậu), Fail (rớt), NA (chưa có).
     */
    @Column(name = "result", nullable = false,
            columnDefinition = "enum('Pass','Fail','NA')")
    private String result;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", updatable = false, insertable = false)
    private LocalDateTime updatedAt;
}

