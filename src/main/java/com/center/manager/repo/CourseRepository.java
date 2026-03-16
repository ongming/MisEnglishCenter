package com.center.manager.repo;

import jakarta.persistence.EntityManager;

import java.util.List;

/**
 * Repository cho truy van khoa hoc.
 */
public interface CourseRepository {
    List<Object[]> getAllCourses(EntityManager em);
}
