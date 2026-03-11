package com.center.manager.repo.jpa;

import com.center.manager.repo.ScheduleRepository;
import jakarta.persistence.EntityManager;

import java.util.List;

public class JpaScheduleRepository implements ScheduleRepository {

    @Override
    public List<Object[]> findByTeacher(EntityManager em, Long teacherId) {
        String jpql = """
                SELECT sc.scheduleId, c.className, sc.studyDate, sc.startTime, sc.endTime
                FROM Schedule sc JOIN ClassEntity c ON sc.classId = c.classId
                WHERE c.teacherId = :teacherId
                ORDER BY sc.studyDate ASC, sc.startTime ASC
                """;
        return em.createQuery(jpql, Object[].class)
                .setParameter("teacherId", teacherId)
                .getResultList();
    }

    @Override
    public List<Object[]> findByStudent(EntityManager em, Long studentId) {
        String jpql = """
                SELECT sc.scheduleId, c.className, sc.studyDate, sc.startTime, sc.endTime
                FROM Schedule sc
                JOIN ClassEntity c ON sc.classId = c.classId
                JOIN Enrollment e ON e.classId = c.classId
                WHERE e.studentId = :studentId
                ORDER BY sc.studyDate ASC, sc.startTime ASC
                """;
        return em.createQuery(jpql, Object[].class)
                .setParameter("studentId", studentId)
                .getResultList();
    }
}

