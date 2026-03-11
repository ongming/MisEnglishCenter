package com.center.manager.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "attendances")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    private Long attendanceId;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "class_id")
    private Long classId;

    @Column(name = "attend_date")
    private LocalDate attendDate;

    @Column(name = "status")
    private String status;

    @Column(name = "note")
    private String note;

    public Attendance() {}

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
}

