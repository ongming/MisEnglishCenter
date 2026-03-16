package com.center.manager.repo;

import com.center.manager.model.Enrollment;
import com.center.manager.model.Student;
import com.center.manager.model.Teacher;
import com.center.manager.model.UserAccount;
import jakarta.persistence.EntityManager;

import java.util.List;

/**
 * Repository cho cac nghiep vu quan tri (Admin).
 */
public interface AdminRepository {
    List<Object[]> findAllTeachers(EntityManager em);

    List<Object[]> findAllStudents(EntityManager em);

    Student findStudentById(EntityManager em, Long studentId);

    void saveStudent(EntityManager em, Student student);

    boolean existsStudentEmail(EntityManager em, String email, Long excludeStudentId);

    boolean existsStudentPhone(EntityManager em, String phone, Long excludeStudentId);

    void saveTeacher(EntityManager em, Teacher teacher);

    boolean existsUsername(EntityManager em, String username);

    boolean existsTeacherAccount(EntityManager em, Long teacherId);

    boolean existsStudentAccount(EntityManager em, Long studentId);

    List<Object[]> findTeachersWithoutAccount(EntityManager em);

    List<Object[]> findStudentsWithoutAccount(EntityManager em);

    List<Object[]> findStudentsNotInClass(EntityManager em, Long classId);

    boolean existsEnrollment(EntityManager em, Long studentId, Long classId);

    long countStudentsInClass(EntityManager em, Long classId);

    Integer findClassMaxStudent(EntityManager em, Long classId);

    void saveEnrollment(EntityManager em, Enrollment enrollment);

    int deleteEnrollment(EntityManager em, Long studentId, Long classId);

    int deletePaymentsByStudent(EntityManager em, Long studentId);

    int deleteAttendancesByStudent(EntityManager em, Long studentId);

    int deleteEnrollmentsByStudent(EntityManager em, Long studentId);

    int deleteStudentAccounts(EntityManager em, Long studentId);

    int deleteStudentById(EntityManager em, Long studentId);

    void clearTeacherFromClasses(EntityManager em, Long teacherId);

    void deleteTeacherAccounts(EntityManager em, Long teacherId);

    int deleteTeacherById(EntityManager em, Long teacherId);

    void saveUserAccount(EntityManager em, UserAccount userAccount);
}
