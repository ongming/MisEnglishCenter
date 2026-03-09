package com.center.manager.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity đại diện cho bảng "courses" trong database.
 *
 * Mỗi dòng = 1 khóa học (ví dụ: IELTS Foundation, TOEIC 650+...).
 * Lớp học (ClassEntity) sẽ thuộc về 1 Course.
 *
 * Ví dụ:
 *   Course "IELTS Foundation" → có nhiều lớp: IELTS-F-0201, IELTS-F-0301...
 */
@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long courseId;

    /**
     * Tên khóa học: "IELTS Foundation", "TOEIC 650+", ...
     */
    @Column(name = "course_name", nullable = false, length = 200)
    private String courseName;

    /**
     * Mô tả chi tiết khóa học.
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Trình độ: Beginner / Intermediate / Advanced.
     */
    @Column(name = "level",
            columnDefinition = "enum('Beginner','Intermediate','Advanced')")
    private String level;

    /**
     * Thời lượng khóa học (số giờ hoặc số tuần).
     */
    @Column(name = "duration")
    private Integer duration;

    /**
     * Đơn vị thời lượng: Hour hoặc Week.
     */
    @Column(name = "duration_unit",
            columnDefinition = "enum('Hour','Week')")
    private String durationUnit;

    /**
     * Học phí khóa học (VND).
     * Dùng BigDecimal để tính tiền chính xác (không bị lỗi làm tròn).
     */
    @Column(name = "fee", nullable = false, precision = 15, scale = 2)
    private BigDecimal fee;

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

