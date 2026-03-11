package com.center.manager.service;

import com.center.manager.db.TransactionManager;
import com.center.manager.model.Teacher;
import com.center.manager.model.Student;
import com.center.manager.repo.TeacherRepository;
import com.center.manager.repo.StudentRepository;

/**
 * Service lấy thông tin giảng viên / sinh viên.
 */
public class PersonService {
    private final TeacherRepository teacherRepo;
    private final StudentRepository studentRepo;
    private final TransactionManager tx;

    public PersonService(TeacherRepository teacherRepo, StudentRepository studentRepo, TransactionManager tx) {
        this.teacherRepo = teacherRepo;
        this.studentRepo = studentRepo;
        this.tx = tx;
    }

    public String getTeacherName(Long teacherId) throws Exception {
        return tx.runInTransaction(em -> {
            Teacher t = teacherRepo.findById(em, teacherId);
            return t != null ? t.getFullName() : null;
        });
    }

    public String getStudentName(Long studentId) throws Exception {
        return tx.runInTransaction(em -> {
            Student s = studentRepo.findById(em, studentId);
            return s != null ? s.getFullName() : null;
        });
    }

    public Object[] getStudentProfile(Long studentId) throws Exception {
        return tx.runInTransaction(em -> {
            Student s = studentRepo.findById(em, studentId);
            if (s == null) return null;
            return new Object[]{
                    s.getStudentId(),
                    s.getFullName(),
                    s.getDateOfBirth() != null ? s.getDateOfBirth().toString() : "",
                    s.getGender() != null ? s.getGender() : "",
                    s.getPhone() != null ? s.getPhone() : "",
                    s.getEmail() != null ? s.getEmail() : "",
                    s.getAddress() != null ? s.getAddress() : "",
                    s.getRegistrationDate() != null ? s.getRegistrationDate().toString() : "",
                    s.getStatus()
            };
        });
    }
}
