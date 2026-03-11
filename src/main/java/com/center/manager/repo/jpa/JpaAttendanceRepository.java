package com.center.manager.repo.jpa;

import com.center.manager.model.Attendance;
import com.center.manager.repo.AttendanceRepository;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.List;

public class JpaAttendanceRepository implements AttendanceRepository {

    @Override
    public List<Object[]> findByClassAndDate(EntityManager em, Long classId, LocalDate attendDate) {
        String jpql = """
                SELECT a.attendanceId, a.studentId, s.fullName, a.status, COALESCE(a.note, '')
                FROM Attendance a JOIN Student s ON a.studentId = s.studentId
                WHERE a.classId = :classId AND a.attendDate = :attendDate
                ORDER BY s.fullName
                """;
        return em.createQuery(jpql, Object[].class)
                .setParameter("classId", classId)
                .setParameter("attendDate", attendDate)
                .getResultList();
    }

    @Override
    public List<Object[]> findHistoryByClass(EntityManager em, Long classId) {
        String jpql = """
                SELECT a.attendanceId, s.fullName, a.attendDate, a.status, COALESCE(a.note, '')
                FROM Attendance a JOIN Student s ON a.studentId = s.studentId
                WHERE a.classId = :classId
                ORDER BY a.attendDate DESC, s.fullName
                """;
        return em.createQuery(jpql, Object[].class)
                .setParameter("classId", classId)
                .getResultList();
    }

    @Override
    public List<Object[]> findByStudent(EntityManager em, Long studentId) {
        String jpql = """
                SELECT c.className, a.attendDate, a.status, COALESCE(a.note, '')
                FROM Attendance a
                JOIN ClassEntity c ON a.classId = c.classId
                WHERE a.studentId = :studentId
                ORDER BY a.attendDate DESC
                """;
        return em.createQuery(jpql, Object[].class)
                .setParameter("studentId", studentId)
                .getResultList();
    }

    @Override
    public void save(EntityManager em, Attendance attendance) {
        if (attendance.getAttendanceId() != null && attendance.getAttendanceId() > 0) {
            em.merge(attendance);
        } else {
            em.persist(attendance);
        }
    }

    @Override
    public Attendance findById(EntityManager em, Long attendanceId) {
        return em.find(Attendance.class, attendanceId);
    }
}

