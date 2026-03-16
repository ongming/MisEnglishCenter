package com.center.manager.service;

import com.center.manager.repo.jpa.JpaCourseRepository;
import com.center.manager.db.TransactionManager;
import java.util.List;

/**
 * Service xử lý logic liên quan đến khóa học.
 */
public class CourseService {
    private final JpaCourseRepository courseRepo;
    private final TransactionManager tx;

    public CourseService(JpaCourseRepository courseRepo, TransactionManager tx) {
        this.courseRepo = courseRepo;
        this.tx = tx;
    }

    /**
     * Lấy tất cả các khóa học (dùng cho admin tạo lớp).
     */
    public List<Object[]> getAllCourses() {
        try {
            return tx.runInTransaction(em -> courseRepo.getAllCourses(em));
        } catch (Exception ex) {
            throw new RuntimeException("Không lấy được danh sách khóa học", ex);
        }
    }
}
