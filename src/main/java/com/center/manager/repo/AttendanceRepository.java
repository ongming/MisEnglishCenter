package com.center.manager.repo;

import com.center.manager.model.Attendance;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository {
    List<Object[]> findByClassAndDate(EntityManager em, Long classId, LocalDate attendDate);
    List<Object[]> findHistoryByClass(EntityManager em, Long classId);
    List<Object[]> findByStudent(EntityManager em, Long studentId);
    void save(EntityManager em, Attendance attendance);
    Attendance findById(EntityManager em, Long attendanceId);
}

