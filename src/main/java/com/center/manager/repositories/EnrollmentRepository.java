package com.center.manager.repositories;

import com.center.manager.entities.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository cho bảng enrollments.
 *
 * Enrollment = ghi danh (nối Student với ClassEntity).
 * Dùng để: lấy danh sách học viên trong 1 lớp.
 */
@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    /**
     * Tìm tất cả enrollment (ghi danh) của 1 lớp cụ thể.
     *
     * Spring tự sinh SQL: SELECT * FROM enrollments WHERE class_id = ?
     *
     * @param classId ID của lớp học
     * @return danh sách enrollment (mỗi enrollment chứa thông tin student)
     */
    List<Enrollment> findByClassEntity_ClassId(Long classId);
}

