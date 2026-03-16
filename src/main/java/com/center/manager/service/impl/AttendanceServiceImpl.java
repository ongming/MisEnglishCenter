package com.center.manager.service.impl;

import com.center.manager.db.TransactionManager;
import com.center.manager.model.Attendance;
import com.center.manager.repo.AttendanceRepository;
import com.center.manager.service.AttendanceService;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

/**
 * Implement AttendanceService.
 */
public class AttendanceServiceImpl implements AttendanceService {
    private final AttendanceRepository attendanceRepo;
    private final TransactionManager tx;

    // Mapper lich su diem danh theo lop, co chuyen cot ngay null-safe sang String.
    private final Function<Object[], Object[]> historyRowMapper =
            r -> new Object[]{r[0], r[1], str(r[2]), r[3], r[4]};

    // Mapper diem danh theo hoc vien.
    private final Function<Object[], Object[]> studentRowMapper =
            r -> new Object[]{r[0], str(r[1]), r[2], r[3]};

    public AttendanceServiceImpl(AttendanceRepository attendanceRepo, TransactionManager tx) {
        this.attendanceRepo = attendanceRepo;
        this.tx = tx;
    }

    @Override
    public List<Object[]> getByClassAndDate(Long classId, String attendDate) throws Exception {
        return tx.runInTransaction(em -> attendanceRepo.findByClassAndDate(em, classId, LocalDate.parse(attendDate)));
    }

    @Override
    public List<Object[]> getHistoryByClass(Long classId) throws Exception {
        return tx.runInTransaction(em -> attendanceRepo.findHistoryByClass(em, classId).stream().map(historyRowMapper).toList());
    }

    @Override
    public List<Object[]> getByStudent(Long studentId) throws Exception {
        return tx.runInTransaction(em -> attendanceRepo.findByStudent(em, studentId).stream().map(studentRowMapper).toList());
    }

    /**
     * Update diem danh neu da ton tai ID; neu khong se tao ban ghi moi.
     */
    @Override
    public void saveAttendance(Long attendanceId, Long studentId, Long classId,
                               String attendDate, String status, String note) throws Exception {
        // Lambda transaction nay gom ca update va create de dam bao thay doi du lieu atomically.
        tx.runInTransaction(em -> {
            if (attendanceId != null && attendanceId > 0) {
                Attendance att = attendanceRepo.findById(em, attendanceId);
                if (att != null) {
                    att.setStatus(status);
                    att.setNote(note);
                    attendanceRepo.save(em, att);
                }
                return null;
            }

            Attendance att = new Attendance();
            att.setStudentId(studentId);
            att.setClassId(classId);
            att.setAttendDate(LocalDate.parse(attendDate));
            att.setStatus(status);
            att.setNote(note);
            attendanceRepo.save(em, att);
            return null;
        });
    }

    private String str(Object o) {
        return o == null ? "" : o.toString();
    }
}
