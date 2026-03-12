package com.center.manager.service;

import com.center.manager.db.TransactionManager;
import com.center.manager.model.Teacher;
import com.center.manager.model.Student;
import com.center.manager.repo.TeacherRepository;
import com.center.manager.repo.StudentRepository;

import java.util.Optional;

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
        return tx.runInTransaction(em ->
                Optional.ofNullable(teacherRepo.findById(em, teacherId))
                        .map(Teacher::getFullName).orElse(null));
    }

    public String getStudentName(Long studentId) throws Exception {
        return tx.runInTransaction(em ->
                Optional.ofNullable(studentRepo.findById(em, studentId))
                        .map(Student::getFullName).orElse(null));
    }

    public Object[] getStudentProfile(Long studentId) throws Exception {
        return tx.runInTransaction(em ->
                Optional.ofNullable(studentRepo.findById(em, studentId))
                        .map(s -> new Object[]{
                                s.getStudentId(), s.getFullName(),
                                str(s.getDateOfBirth()), str(s.getGender()),
                                str(s.getPhone()), str(s.getEmail()),
                                str(s.getAddress()), str(s.getRegistrationDate()),
                                s.getStatus()
                        }).orElse(null));
    }

    private String str(Object o) { return o == null ? "" : o.toString(); }
}
