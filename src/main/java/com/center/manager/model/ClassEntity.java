package com.center.manager.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "classes")
public class ClassEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_id")
    private Long classId;

    @Column(name = "class_name")
    private String className;

    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "teacher_id")
    private Long teacherId;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "max_student")
    private Integer maxStudent;

    @Column(name = "status")
    private String status;

    public ClassEntity() {}

    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Integer getMaxStudent() { return maxStudent; }
    public void setMaxStudent(Integer maxStudent) { this.maxStudent = maxStudent; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

