package com.center.manager.service;

import com.center.manager.db.TransactionManager;
import com.center.manager.repo.ClassRepository;
import com.center.manager.repo.ScheduleRepository;

import java.util.ArrayList;
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

    // ===== TEACHER =====

    public List<Object[]> getClassesByTeacher(Long teacherId) throws Exception {
        return tx.runInTransaction(em -> {
            List<Object[]> rows = classRepo.findClassesByTeacher(em, teacherId);
            List<Object[]> result = new ArrayList<>();
            for (Object[] r : rows) {
                result.add(new Object[]{
                        r[0], r[1], r[2],
                        r[3] != null ? r[3].toString() : "",
                        r[4] != null ? r[4].toString() : "",
                        r[5], r[6]
                });
            }
            return result;
        });
    }

    public List<Object[]> getStudentsInClass(Long classId) throws Exception {
        return tx.runInTransaction(em -> {
            List<Object[]> rows = classRepo.findStudentsInClass(em, classId);
            List<Object[]> result = new ArrayList<>();
            for (Object[] r : rows) {
                result.add(new Object[]{
                        r[0], r[1], r[2], r[3],
                        r[4] != null ? r[4].toString() : "",
                        r[5]
                });
            }
            return result;
        });
    }

    public List<Object[]> getScheduleByTeacher(Long teacherId) throws Exception {
        return tx.runInTransaction(em -> {
            List<Object[]> rows = scheduleRepo.findByTeacher(em, teacherId);
            List<Object[]> result = new ArrayList<>();
            for (Object[] r : rows) {
                result.add(new Object[]{
                        r[0], r[1],
                        r[2] != null ? r[2].toString() : "",
                        r[3] != null ? r[3].toString() : "",
                        r[4] != null ? r[4].toString() : ""
                });
            }
            return result;
        });
    }

    // ===== STUDENT =====

    public List<Object[]> getClassesByStudent(Long studentId) throws Exception {
        return tx.runInTransaction(em -> {
            List<Object[]> rows = classRepo.findClassesByStudent(em, studentId);
            List<Object[]> result = new ArrayList<>();
            for (Object[] r : rows) {
                result.add(new Object[]{
                        r[0], r[1], r[2],
                        r[3] != null ? r[3].toString() : "",
                        r[4] != null ? r[4].toString() : "",
                        r[5], r[6]
                });
            }
            return result;
        });
    }

    public List<Object[]> getScheduleByStudent(Long studentId) throws Exception {
        return tx.runInTransaction(em -> {
            List<Object[]> rows = scheduleRepo.findByStudent(em, studentId);
            List<Object[]> result = new ArrayList<>();
            for (Object[] r : rows) {
                result.add(new Object[]{
                        r[0], r[1],
                        r[2] != null ? r[2].toString() : "",
                        r[3] != null ? r[3].toString() : "",
                        r[4] != null ? r[4].toString() : ""
                });
            }
            return result;
        });
    }
}

