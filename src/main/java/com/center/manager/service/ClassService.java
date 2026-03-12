package com.center.manager.service;

import com.center.manager.db.TransactionManager;
import com.center.manager.repo.ClassRepository;
import com.center.manager.repo.ScheduleRepository;

import java.util.List;

/**
 * Service cho lớp học và lịch học.
 */
public class ClassService {
    private final ClassRepository classRepo;
    private final ScheduleRepository scheduleRepo;
    private final TransactionManager tx;

    public ClassService(ClassRepository classRepo, ScheduleRepository scheduleRepo, TransactionManager tx) {
        this.classRepo = classRepo;
        this.scheduleRepo = scheduleRepo;
        this.tx = tx;
    }

    // ===== ALL =====

    public List<Object[]> getAllClasses() throws Exception {
        return tx.runInTransaction(em ->
                classRepo.findAllClasses(em).stream()
                        .map(r -> new Object[]{r[0], r[1], r[2], str(r[3]), str(r[4]), r[5], r[6]})
                        .toList());
    }

    // ===== TEACHER =====

    public List<Object[]> getClassesByTeacher(Long teacherId) throws Exception {
        return tx.runInTransaction(em ->
                classRepo.findClassesByTeacher(em, teacherId).stream()
                        .map(r -> new Object[]{r[0], r[1], r[2], str(r[3]), str(r[4]), r[5], r[6]})
                        .toList());
    }

    public List<Object[]> getStudentsInClass(Long classId) throws Exception {
        return tx.runInTransaction(em ->
                classRepo.findStudentsInClass(em, classId).stream()
                        .map(r -> new Object[]{r[0], r[1], r[2], r[3], str(r[4]), r[5], r[6]})
                        .toList());
    }

    public List<Object[]> getScheduleByTeacher(Long teacherId) throws Exception {
        return tx.runInTransaction(em ->
                scheduleRepo.findByTeacher(em, teacherId).stream()
                        .map(r -> new Object[]{r[0], r[1], str(r[2]), str(r[3]), str(r[4])})
                        .toList());
    }

    // ===== STUDENT =====

    public List<Object[]> getClassesByStudent(Long studentId) throws Exception {
        return tx.runInTransaction(em ->
                classRepo.findClassesByStudent(em, studentId).stream()
                        .map(r -> new Object[]{r[0], r[1], r[2], str(r[3]), str(r[4]), r[5], r[6]})
                        .toList());
    }

    public List<Object[]> getScheduleByStudent(Long studentId) throws Exception {
        return tx.runInTransaction(em ->
                scheduleRepo.findByStudent(em, studentId).stream()
                        .map(r -> new Object[]{r[0], r[1], str(r[2]), str(r[3]), str(r[4])})
                        .toList());
    }

    private String str(Object o) { return o == null ? "" : o.toString(); }
}

