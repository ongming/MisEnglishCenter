package com.center.manager.repositories;

import com.center.manager.entities.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository cho bảng schedules.
 *
 * Schedule = lịch học từng buổi.
 * Dùng để: GV xem lịch dạy (lấy schedule theo class_id).
 */
@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    /**
     * Tìm tất cả buổi học của 1 lớp.
     *
     * Spring tự sinh SQL: SELECT * FROM schedules WHERE class_id = ? ORDER BY study_date, start_time
     *
     * @param classId ID của lớp học
     * @return danh sách buổi học, sắp xếp theo ngày rồi giờ
     */
    List<Schedule> findByClassEntity_ClassIdOrderByStudyDateAscStartTimeAsc(Long classId);

    /**
     * Tìm tất cả buổi học của nhiều lớp cùng lúc.
     *
     * Spring tự sinh SQL: SELECT * FROM schedules WHERE class_id IN (?, ?, ...) ORDER BY ...
     *
     * Dùng khi GV dạy nhiều lớp → lấy lịch tất cả lớp 1 lần.
     *
     * @param classIds danh sách ID các lớp
     * @return tất cả buổi học của các lớp đó
     */
    List<Schedule> findByClassEntity_ClassIdInOrderByStudyDateAscStartTimeAsc(List<Long> classIds);
}

