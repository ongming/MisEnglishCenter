package com.center.manager.repositories;

import com.center.manager.entities.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository cho bảng attendances.
 *
 * Attendance = điểm danh.
 * Dùng để: GV xem/lưu điểm danh theo lớp + ngày.
 */
@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    /**
     * Lấy danh sách điểm danh của 1 lớp trong 1 ngày cụ thể.
     *
     * Spring tự sinh SQL:
     *   SELECT * FROM attendances WHERE class_id = ? AND attend_date = ?
     *
     * @param classId    ID lớp học
     * @param attendDate ngày điểm danh
     * @return danh sách bản ghi điểm danh
     */
    List<Attendance> findByClassEntity_ClassIdAndAttendDate(Long classId, LocalDate attendDate);

    /**
     * Lấy toàn bộ điểm danh của 1 lớp (tất cả ngày).
     *
     * Dùng khi GV muốn xem lịch sử điểm danh cả lớp.
     */
    List<Attendance> findByClassEntity_ClassIdOrderByAttendDateDesc(Long classId);
}

