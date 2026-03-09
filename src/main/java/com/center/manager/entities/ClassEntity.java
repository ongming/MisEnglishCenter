package com.center.manager.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity đại diện cho bảng "classes" trong database.
 *
 * Mỗi dòng = 1 lớp học cụ thể.
 * Ví dụ: Lớp "IELTS-F-0201" thuộc khóa "IELTS Foundation", do GV Trần Quốc Huy dạy.
 *
 * --- Tại sao đặt tên ClassEntity mà không phải Class? ---
 * Vì "Class" là từ khóa của Java (java.lang.Class), nên đặt ClassEntity để tránh xung đột.
 *
 * --- Quan hệ (Relationship) ---
 * Mỗi lớp thuộc 1 khóa học (Course)    → course_id  (Many-to-One)
 * Mỗi lớp có 1 giảng viên (Teacher)    → teacher_id (Many-to-One)
 *
 * @ManyToOne: Nhiều lớp có thể thuộc cùng 1 khóa học.
 * @JoinColumn: Chỉ định cột nào trong bảng classes dùng làm foreign key.
 */
@Entity
@Table(name = "classes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_id")
    private Long classId;

    /**
     * Tên lớp: "COM-A1-0201", "IELTS-F-0201"...
     */
    @Column(name = "class_name", nullable = false, length = 150)
    private String className;

    /**
     * Khóa học mà lớp này thuộc về.
     *
     * @ManyToOne = Nhiều lớp → 1 khóa học.
     * FetchType.LAZY = Chỉ load Course khi nào thật sự cần (tiết kiệm bộ nhớ).
     * @JoinColumn = Cột "course_id" trong bảng classes trỏ đến bảng courses.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    /**
     * Giảng viên phụ trách lớp này.
     *
     * @ManyToOne = Nhiều lớp → 1 giảng viên (1 GV có thể dạy nhiều lớp).
     * nullable: GV có thể chưa được phân công (NULL).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    /**
     * Ngày bắt đầu lớp học.
     */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /**
     * Ngày kết thúc lớp học.
     */
    @Column(name = "end_date")
    private LocalDate endDate;

    /**
     * Sĩ số tối đa cho phép.
     */
    @Column(name = "max_student", nullable = false)
    private Integer maxStudent;

    /**
     * Trạng thái lớp: Planned, Open, Ongoing, Completed, Cancelled.
     */
    @Column(name = "status", nullable = false,
            columnDefinition = "enum('Planned','Open','Ongoing','Completed','Cancelled')")
    private String status;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", updatable = false, insertable = false)
    private LocalDateTime updatedAt;
}

