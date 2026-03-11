package com.center.manager.service;

import com.center.manager.db.TransactionManager;
import com.center.manager.model.Attendance;
import com.center.manager.repo.AttendanceRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service cho điểm danh.
 */
public class AttendanceService {
    private final AttendanceRepository attendanceRepo;
    private final TransactionManager tx;

    public AttendanceService(AttendanceRepository attendanceRepo, TransactionManager tx) {
        this.attendanceRepo = attendanceRepo;
        this.tx = tx;
    }

    public List<Object[]> getByClassAndDate(Long classId, String attendDate) throws Exception {
        return tx.runInTransaction(em ->
                attendanceRepo.findByClassAndDate(em, classId, LocalDate.parse(attendDate)));
    }

    public List<Object[]> getHistoryByClass(Long classId) throws Exception {
        return tx.runInTransaction(em -> {
            List<Object[]> rows = attendanceRepo.findHistoryByClass(em, classId);
            List<Object[]> result = new ArrayList<>();
            for (Object[] r : rows) {
                result.add(new Object[]{
                        r[0], r[1],
                        r[2] != null ? r[2].toString() : "",
                        r[3], r[4]
                });
            }
            return result;
        });
    }

    public List<Object[]> getByStudent(Long studentId) throws Exception {
        return tx.runInTransaction(em -> {
            List<Object[]> rows = attendanceRepo.findByStudent(em, studentId);
            List<Object[]> result = new ArrayList<>();
            for (Object[] r : rows) {
                result.add(new Object[]{
                        r[0],
                        r[1] != null ? r[1].toString() : "",
                        r[2], r[3]
                });
            }
            return result;
        });
    }

    public void saveAttendance(Long attendanceId, Long studentId, Long classId,
                               String attendDate, String status, String note) throws Exception {
        tx.runInTransaction(em -> {
            if (attendanceId != null && attendanceId > 0) {
                Attendance att = attendanceRepo.findById(em, attendanceId);
                if (att != null) {
                    att.setStatus(status);
                    att.setNote(note);
                    attendanceRepo.save(em, att);
                }
            } else {
                Attendance att = new Attendance();
                att.setStudentId(studentId);
                att.setClassId(classId);
                att.setAttendDate(LocalDate.parse(attendDate));
                att.setStatus(status);
                att.setNote(note);
                attendanceRepo.save(em, att);
            }
            return null;
        });
    }
}

