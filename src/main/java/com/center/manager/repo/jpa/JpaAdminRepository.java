package com.center.manager.repo.jpa;

import com.center.manager.model.Enrollment;
import com.center.manager.model.Student;
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
                       COALESCE(t.specialty, ''),
                       t.hireDate,
                       COALESCE(t.status, '')
                FROM Teacher t
                ORDER BY t.teacherId DESC
                """;
        return em.createQuery(jpql, Object[].class).getResultList();
    }

    @Override
    public List<Object[]> findAllStudents(EntityManager em) {
        String jpql = """
                SELECT s.studentId,
                       s.fullName,
                       COALESCE(s.phone, ''),
                       COALESCE(s.email, ''),
                       COALESCE(s.gender, ''),
                       s.registrationDate,
                       COALESCE(s.status, '')
                FROM Student s
                ORDER BY s.studentId DESC
                """;
        return em.createQuery(jpql, Object[].class).getResultList();
    }

    @Override
    public Student findStudentById(EntityManager em, Long studentId) {
        return em.find(Student.class, studentId);
    }

    @Override
    public void saveStudent(EntityManager em, Student student) {
        em.persist(student);
        em.flush();
    }

    @Override
    public boolean existsStudentEmail(EntityManager em, String email, Long excludeStudentId) {
        String jpql = """
                SELECT COUNT(s)
                FROM Student s
                WHERE LOWER(s.email) = LOWER(:email)
                  AND (:excludeId IS NULL OR s.studentId <> :excludeId)
                """;
        Long count = em.createQuery(jpql, Long.class)
                .setParameter("email", email)
                .setParameter("excludeId", excludeStudentId)
                .getSingleResult();
        return count != null && count > 0;
    }

    @Override
    public boolean existsStudentPhone(EntityManager em, String phone, Long excludeStudentId) {
        String jpql = """
                SELECT COUNT(s)
                FROM Student s
                WHERE s.phone = :phone
                  AND (:excludeId IS NULL OR s.studentId <> :excludeId)
                """;
        Long count = em.createQuery(jpql, Long.class)
                .setParameter("phone", phone)
                .setParameter("excludeId", excludeStudentId)
                .getSingleResult();
        return count != null && count > 0;
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
    public List<Object[]> findStudentsNotInClass(EntityManager em, Long classId) {
        String jpql = """
                SELECT s.studentId,
                       s.fullName,
                       COALESCE(s.phone, ''),
                       COALESCE(s.email, '')
                FROM Student s
                WHERE COALESCE(s.status, 'Active') = 'Active'
                  AND NOT EXISTS (
                    SELECT e.enrollmentId
                    FROM Enrollment e
                    WHERE e.classId = :classId
                      AND e.studentId = s.studentId
                  )
                ORDER BY s.fullName
                """;
        return em.createQuery(jpql, Object[].class)
                .setParameter("classId", classId)
                .getResultList();
    }

    @Override
    public boolean existsEnrollment(EntityManager em, Long studentId, Long classId) {
        String jpql = "SELECT COUNT(e) FROM Enrollment e WHERE e.studentId = :studentId AND e.classId = :classId";
        Long count = em.createQuery(jpql, Long.class)
                .setParameter("studentId", studentId)
                .setParameter("classId", classId)
                .getSingleResult();
        return count != null && count > 0;
    }

    @Override
    public long countStudentsInClass(EntityManager em, Long classId) {
        String jpql = "SELECT COUNT(e) FROM Enrollment e WHERE e.classId = :classId";
        Long count = em.createQuery(jpql, Long.class)
                .setParameter("classId", classId)
                .getSingleResult();
        return count == null ? 0L : count;
    }

    @Override
    public Integer findClassMaxStudent(EntityManager em, Long classId) {
        String jpql = "SELECT c.maxStudent FROM ClassEntity c WHERE c.classId = :classId";
        return em.createQuery(jpql, Integer.class)
                .setParameter("classId", classId)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public void saveEnrollment(EntityManager em, Enrollment enrollment) {
        em.persist(enrollment);
    }

    @Override
    public int deleteEnrollment(EntityManager em, Long studentId, Long classId) {
        String jpql = "DELETE FROM Enrollment e WHERE e.studentId = :studentId AND e.classId = :classId";
        return em.createQuery(jpql)
                .setParameter("studentId", studentId)
                .setParameter("classId", classId)
                .executeUpdate();
    }

    @Override
    public int deletePaymentsByStudent(EntityManager em, Long studentId) {
        return em.createQuery("DELETE FROM Payment p WHERE p.studentId = :studentId")
                .setParameter("studentId", studentId)
                .executeUpdate();
    }

    @Override
    public int deleteAttendancesByStudent(EntityManager em, Long studentId) {
        return em.createQuery("DELETE FROM Attendance a WHERE a.studentId = :studentId")
                .setParameter("studentId", studentId)
                .executeUpdate();
    }

    @Override
    public int deleteEnrollmentsByStudent(EntityManager em, Long studentId) {
        return em.createQuery("DELETE FROM Enrollment e WHERE e.studentId = :studentId")
                .setParameter("studentId", studentId)
                .executeUpdate();
    }

    @Override
    public int deleteStudentAccounts(EntityManager em, Long studentId) {
        return em.createQuery("DELETE FROM UserAccount u WHERE u.studentId = :studentId")
                .setParameter("studentId", studentId)
                .executeUpdate();
    }

    @Override
    public int deleteStudentById(EntityManager em, Long studentId) {
        return em.createQuery("DELETE FROM Student s WHERE s.studentId = :studentId")
                .setParameter("studentId", studentId)
                .executeUpdate();
    }

    @Override
    public void clearTeacherFromClasses(EntityManager em, Long teacherId) {
        String jpql = "UPDATE ClassEntity c SET c.teacherId = null WHERE c.teacherId = :teacherId";
        em.createQuery(jpql)
                .setParameter("teacherId", teacherId)
                .executeUpdate();
    }

    @Override
    public void deleteTeacherAccounts(EntityManager em, Long teacherId) {
        String jpql = "DELETE FROM UserAccount u WHERE u.teacherId = :teacherId";
        em.createQuery(jpql)
                .setParameter("teacherId", teacherId)
                .executeUpdate();
    }

    @Override
    public int deleteTeacherById(EntityManager em, Long teacherId) {
        String jpql = "DELETE FROM Teacher t WHERE t.teacherId = :teacherId";
        return em.createQuery(jpql)
                .setParameter("teacherId", teacherId)
                .executeUpdate();
    }

    @Override
    public void saveUserAccount(EntityManager em, UserAccount userAccount) {
        em.persist(userAccount);
    }
}
