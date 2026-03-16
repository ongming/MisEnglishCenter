package com.center.manager.repo.jpa;

import jakarta.persistence.EntityManager;
import java.util.List;

/**
 * Repository lấy dữ liệu khóa học từ database.
 */
public class JpaCourseRepository {
    /**
     * Lấy tất cả các khóa học (id, tên).
     */
    public List<Object[]> getAllCourses(EntityManager em) {
        return em.createQuery("SELECT c.courseId, c.courseName FROM Course c", Object[].class)
                .getResultList();
    }
}
