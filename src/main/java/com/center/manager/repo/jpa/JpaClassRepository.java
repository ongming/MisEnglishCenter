package com.center.manager.repo.jpa;

import com.center.manager.repo.ClassRepository;
import jakarta.persistence.EntityManager;

import java.util.List;

public class JpaClassRepository implements ClassRepository {

    @Override
    public List<Object[]> findAllClasses(EntityManager em) {
        String jpql = """
                SELECT c.classId, c.className, COALESCE(co.courseName, 'N/A'),
                       c.startDate, c.endDate, c.maxStudent, c.status
                FROM ClassEntity c LEFT JOIN Course co ON c.courseId = co.courseId
                ORDER BY c.startDate DESC
                """;
        return em.createQuery(jpql, Object[].class).getResultList();
    }

    @Override
    public List<Object[]> findClassesByTeacher(EntityManager em, Long teacherId) {
        String jpql = """
                SELECT c.classId, c.className, COALESCE(co.courseName, 'N/A'),
                       c.startDate, c.endDate, c.maxStudent, c.status
                FROM ClassEntity c LEFT JOIN Course co ON c.courseId = co.courseId
                WHERE c.teacherId = :teacherId
                ORDER BY c.startDate DESC
                """;
        return em.createQuery(jpql, Object[].class)
                .setParameter("teacherId", teacherId)
                .getResultList();
    }

    @Override
    public List<Object[]> findStudentsInClass(EntityManager em, Long classId) {
        String jpql = """
                SELECT s.studentId, s.fullName, COALESCE(s.phone, ''), COALESCE(s.email, ''),
                       e.enrollmentDate, e.status, COALESCE(e.result, 'NA')
                FROM Enrollment e JOIN Student s ON e.studentId = s.studentId
                WHERE e.classId = :classId
                ORDER BY s.fullName
                """;
        return em.createQuery(jpql, Object[].class)
                .setParameter("classId", classId)
                .getResultList();
    }

    @Override
    public List<Object[]> findClassesByStudent(EntityManager em, Long studentId) {
        String jpql = """
                SELECT c.classId, c.className, COALESCE(co.courseName, 'N/A'),
                       c.startDate, c.endDate, c.status, e.status
                FROM Enrollment e
                JOIN ClassEntity c ON e.classId = c.classId
                LEFT JOIN Course co ON c.courseId = co.courseId
                WHERE e.studentId = :studentId
                ORDER BY c.startDate DESC
                """;
        return em.createQuery(jpql, Object[].class)
                .setParameter("studentId", studentId)
                .getResultList();
    }
}

