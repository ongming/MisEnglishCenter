package com.center.manager.service;

import com.center.manager.db.TransactionManager;
import com.center.manager.model.Attendance;
import com.center.manager.repo.AttendanceRepository;

import java.time.LocalDate;
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
        return tx.runInTransaction(em ->
                attendanceRepo.findHistoryByClass(em, classId).stream()
                        .map(r -> new Object[]{r[0], r[1], str(r[2]), r[3], r[4]})
                        .toList());
    }

    public List<Object[]> getByStudent(Long studentId) throws Exception {
        return tx.runInTransaction(em ->
                attendanceRepo.findByStudent(em, studentId).stream()
                        .map(r -> new Object[]{r[0], str(r[1]), r[2], r[3]})
                        .toList());
    }

    private String str(Object o) { return o == null ? "" : o.toString(); }

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

