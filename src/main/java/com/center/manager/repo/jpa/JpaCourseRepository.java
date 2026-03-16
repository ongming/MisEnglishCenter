package com.center.manager.repo.jpa;

import com.center.manager.repo.CourseRepository;
import jakarta.persistence.EntityManager;

import java.util.List;

/**
 * Repository lấy dữ liệu khóa học từ database.
 */
public class JpaCourseRepository implements CourseRepository {
    /**
     * Lấy tất cả các khóa học (id, tên).
     */
    @Override
    public List<Object[]> getAllCourses(EntityManager em) {
        return em.createQuery("SELECT c.courseId, c.courseName FROM Course c", Object[].class)
                .getResultList();
    }
}
