package com.center.manager.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Entity đại diện cho bảng "schedules" trong database.
 *
 * Mỗi dòng = 1 buổi học cụ thể (ngày nào, giờ nào, lớp nào).
 *
 * Ví dụ:
 *   Lớp "COM-A1-0201" có buổi học ngày 10/02/2026 từ 18:30 đến 20:30 tại phòng P101.
 *
 * --- Dùng cho chức năng Giảng viên ---
 * GV xem lịch dạy → lấy tất cả schedule của các lớp GV đang dạy.
 */
@Entity
@Table(name = "schedules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long scheduleId;

    /**
     * Buổi học này thuộc lớp nào.
     * @ManyToOne = Nhiều buổi học → 1 lớp.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private ClassEntity classEntity;

    /**
     * Ngày học.
     */
    @Column(name = "study_date", nullable = false)
    private LocalDate studyDate;

    /**
     * Giờ bắt đầu (ví dụ: 18:30).
     */
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    /**
     * Giờ kết thúc (ví dụ: 20:30).
     */
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;
}

