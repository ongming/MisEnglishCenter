package com.center.manager.service;

import java.util.List;

/**
 * Service cho diem danh.
 */
public interface AttendanceService {

    List<Object[]> getByClassAndDate(Long classId, String attendDate) throws Exception;

    List<Object[]> getHistoryByClass(Long classId) throws Exception;

    List<Object[]> getByStudent(Long studentId) throws Exception;

    /**
     * Luu diem danh theo che do update neu co attendanceId, nguoc lai la create moi.
     */
    void saveAttendance(Long attendanceId, Long studentId, Long classId,
                        String attendDate, String status, String note) throws Exception;
}
