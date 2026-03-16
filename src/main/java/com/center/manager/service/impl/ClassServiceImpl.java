package com.center.manager.service.impl;

import com.center.manager.db.TransactionManager;
import com.center.manager.repo.ClassRepository;
import com.center.manager.repo.ScheduleRepository;
import com.center.manager.service.ClassService;

import java.util.List;
import java.util.function.Function;

/**
 * Implement ClassService, bao gom truy van lop hoc va lich hoc.
 */
public class ClassServiceImpl implements ClassService {
    private final ClassRepository classRepo;
    private final ScheduleRepository scheduleRepo;
    private final TransactionManager tx;

    // Mapper cho query lop hoc, chuan hoa cot nullable ve chuoi rong de UI render on dinh.
    private final Function<Object[], Object[]> classRowMapper =
            r -> new Object[]{r[0], r[1], r[2], str(r[3]), str(r[4]), r[5], r[6]};

    // Mapper cho query lich hoc (teacher/student) voi cac cot ngay-gio co the null.
    private final Function<Object[], Object[]> scheduleRowMapper =
            r -> new Object[]{r[0], r[1], str(r[2]), str(r[3]), str(r[4])};

    // Mapper danh sach hoc vien trong lop.
    private final Function<Object[], Object[]> studentInClassRowMapper =
            r -> new Object[]{r[0], r[1], r[2], r[3], str(r[4]), r[5], r[6]};

    public ClassServiceImpl(ClassRepository classRepo, ScheduleRepository scheduleRepo, TransactionManager tx) {
        this.classRepo = classRepo;
        this.scheduleRepo = scheduleRepo;
        this.tx = tx;
    }

    @Override
    public List<Object[]> getAllClasses() throws Exception {
        // Lambda transaction + stream mapper giup giu nghiep vu gon, khong lap lai convert Object[].
        return tx.runInTransaction(em -> classRepo.findAllClasses(em).stream().map(classRowMapper).toList());
    }

    @Override
    public List<Object[]> getClassesByTeacher(Long teacherId) throws Exception {
        return tx.runInTransaction(em -> classRepo.findClassesByTeacher(em, teacherId).stream().map(classRowMapper).toList());
    }

    @Override
    public List<Object[]> getStudentsInClass(Long classId) throws Exception {
        return tx.runInTransaction(em -> classRepo.findStudentsInClass(em, classId).stream().map(studentInClassRowMapper).toList());
    }

    @Override
    public List<Object[]> getScheduleByTeacher(Long teacherId) throws Exception {
        return tx.runInTransaction(em -> scheduleRepo.findByTeacher(em, teacherId).stream().map(scheduleRowMapper).toList());
    }

    @Override
    public List<Object[]> getClassesByStudent(Long studentId) throws Exception {
        return tx.runInTransaction(em -> classRepo.findClassesByStudent(em, studentId).stream().map(classRowMapper).toList());
    }

    @Override
    public List<Object[]> getScheduleByStudent(Long studentId) throws Exception {
        return tx.runInTransaction(em -> scheduleRepo.findByStudent(em, studentId).stream().map(scheduleRowMapper).toList());
    }

    private String str(Object o) {
        return o == null ? "" : o.toString();
    }
}
