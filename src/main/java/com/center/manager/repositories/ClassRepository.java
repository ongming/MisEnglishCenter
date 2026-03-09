package com.center.manager.repositories;

import com.center.manager.entities.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository cho bảng classes.
 *
 * --- Hàm custom ---
 * findByTeacher_TeacherId(Long teacherId):
 *   Spring tự sinh SQL: SELECT * FROM classes WHERE teacher_id = ?
 *
 * Quy tắc đặt tên:
 *   findBy + [tên field trong Entity] + _ + [field con]
 *   ClassEntity có field "teacher" (kiểu Teacher) → teacher.teacherId
 *   → findByTeacher_TeacherId
 */
@Repository
public interface ClassRepository extends JpaRepository<ClassEntity, Long> {

    /**
     * Tìm tất cả lớp học mà 1 giảng viên đang phụ trách.
     *
     * @param teacherId ID của giảng viên
     * @return danh sách lớp (có thể rỗng nếu GV chưa được phân lớp)
     */
    List<ClassEntity> findByTeacher_TeacherId(Long teacherId);
}

