package com.center.manager.repo.jpa;

import com.center.manager.model.Teacher;
import com.center.manager.model.UserAccount;
import com.center.manager.repo.AdminRepository;
import jakarta.persistence.EntityManager;

import java.util.List;

public class JpaAdminRepository implements AdminRepository {

    @Override
    public List<Object[]> findAllTeachers(EntityManager em) {
        String jpql = """
                SELECT t.teacherId,
                       t.fullName,
                       COALESCE(t.phone, ''),
                       COALESCE(t.email, ''),
                       COALESCE(t.specialization, ''),
                       COALESCE(t.status, '')
                FROM Teacher t
                ORDER BY t.teacherId DESC
                """;
        return em.createQuery(jpql, Object[].class).getResultList();
    }

    @Override
    public void saveTeacher(EntityManager em, Teacher teacher) {
        em.persist(teacher);
        // flush để lấy teacherId ngay sau khi insert
        em.flush();
    }

    @Override
    public boolean existsUsername(EntityManager em, String username) {
        String jpql = "SELECT COUNT(u) FROM UserAccount u WHERE LOWER(u.username) = LOWER(:username)";
        Long count = em.createQuery(jpql, Long.class)
                .setParameter("username", username)
                .getSingleResult();
        return count != null && count > 0;
    }

    @Override
    public boolean existsTeacherAccount(EntityManager em, Long teacherId) {
        String jpql = "SELECT COUNT(u) FROM UserAccount u WHERE u.teacherId = :teacherId AND u.role = 'Teacher'";
        Long count = em.createQuery(jpql, Long.class)
                .setParameter("teacherId", teacherId)
                .getSingleResult();
        return count != null && count > 0;
    }

    @Override
    public boolean existsStudentAccount(EntityManager em, Long studentId) {
        String jpql = "SELECT COUNT(u) FROM UserAccount u WHERE u.studentId = :studentId AND u.role = 'Student'";
        Long count = em.createQuery(jpql, Long.class)
                .setParameter("studentId", studentId)
                .getSingleResult();
        return count != null && count > 0;
    }

    @Override
    public List<Object[]> findTeachersWithoutAccount(EntityManager em) {
        String jpql = """
                SELECT t.teacherId, t.fullName
                FROM Teacher t
                WHERE NOT EXISTS (
                    SELECT u.userId
                    FROM UserAccount u
                    WHERE u.teacherId = t.teacherId AND u.role = 'Teacher'
                )
                ORDER BY t.fullName
                """;
        return em.createQuery(jpql, Object[].class).getResultList();
    }

    @Override
    public List<Object[]> findStudentsWithoutAccount(EntityManager em) {
        String jpql = """
                SELECT s.studentId, s.fullName
                FROM Student s
                WHERE NOT EXISTS (
                    SELECT u.userId
                    FROM UserAccount u
                    WHERE u.studentId = s.studentId AND u.role = 'Student'
                )
                ORDER BY s.fullName
                """;
        return em.createQuery(jpql, Object[].class).getResultList();
    }

    @Override
    public void saveUserAccount(EntityManager em, UserAccount userAccount) {
        em.persist(userAccount);
    }
}

