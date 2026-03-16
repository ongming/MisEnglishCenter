package com.center.manager.service.impl;

import com.center.manager.db.TransactionManager;
import com.center.manager.model.Student;
import com.center.manager.model.Teacher;
import com.center.manager.repo.StudentRepository;
import com.center.manager.repo.TeacherRepository;
import com.center.manager.service.PersonService;

import java.util.Optional;

/**
 * Implement PersonService de lay du lieu profile cho UI.
 */
public class PersonServiceImpl implements PersonService {
    private final TeacherRepository teacherRepo;
    private final StudentRepository studentRepo;
    private final TransactionManager tx;

    public PersonServiceImpl(TeacherRepository teacherRepo, StudentRepository studentRepo, TransactionManager tx) {
        this.teacherRepo = teacherRepo;
        this.studentRepo = studentRepo;
        this.tx = tx;
    }

    @Override
    public String getTeacherName(Long teacherId) throws Exception {
        return tx.runInTransaction(em ->
                Optional.ofNullable(teacherRepo.findById(em, teacherId))
                        .map(Teacher::getFullName)
                        .orElse(null));
    }

    @Override
    public String getStudentName(Long studentId) throws Exception {
        return tx.runInTransaction(em ->
                Optional.ofNullable(studentRepo.findById(em, studentId))
                        .map(Student::getFullName)
                        .orElse(null));
    }

    /**
     * Chuan hoa gia tri null thanh chuoi rong de bang Swing hien thi on dinh.
     */
    @Override
    public Object[] getStudentProfile(Long studentId) throws Exception {
        return tx.runInTransaction(em ->
                Optional.ofNullable(studentRepo.findById(em, studentId))
                        .map(s -> new Object[]{
                                s.getStudentId(), s.getFullName(),
                                str(s.getDateOfBirth()), str(s.getGender()),
                                str(s.getPhone()), str(s.getEmail()),
                                str(s.getAddress()), str(s.getRegistrationDate()),
                                s.getStatus()
                        })
                        .orElse(null));
    }

    private String str(Object o) {
        return o == null ? "" : o.toString();
    }
}

