package com.center.manager.repo;

import com.center.manager.model.Teacher;
import com.center.manager.model.UserAccount;
import jakarta.persistence.EntityManager;

import java.util.List;

/**
 * Repository cho các nghiệp vụ quản trị (Admin).
 */
public interface AdminRepository {
    List<Object[]> findAllTeachers(EntityManager em);

    void saveTeacher(EntityManager em, Teacher teacher);

    boolean existsUsername(EntityManager em, String username);

    boolean existsTeacherAccount(EntityManager em, Long teacherId);

    boolean existsStudentAccount(EntityManager em, Long studentId);

    List<Object[]> findTeachersWithoutAccount(EntityManager em);

    List<Object[]> findStudentsWithoutAccount(EntityManager em);

    void saveUserAccount(EntityManager em, UserAccount userAccount);
}

