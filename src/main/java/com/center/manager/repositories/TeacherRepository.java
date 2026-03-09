package com.center.manager.repositories;

import com.center.manager.entities.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository cho bảng teachers.
 *
 * Kế thừa JpaRepository → tự có sẵn: findAll(), findById(), save(), delete()...
 * Không cần viết SQL thủ công.
 */
@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    // Hiện tại chưa cần thêm hàm custom nào.
    // Các hàm có sẵn từ JpaRepository đã đủ dùng:
    //   - findById(Long id)  → tìm GV theo teacher_id
    //   - findAll()          → lấy tất cả GV
}

