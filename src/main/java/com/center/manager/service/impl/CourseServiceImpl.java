package com.center.manager.service.impl;

import com.center.manager.db.TransactionManager;
import com.center.manager.repo.CourseRepository;
import com.center.manager.service.CourseService;

import java.util.List;

/**
 * Implement CourseService de lay du lieu khoa hoc cho UI admin.
 */
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepo;
    private final TransactionManager tx;

    public CourseServiceImpl(CourseRepository courseRepo, TransactionManager tx) {
        this.courseRepo = courseRepo;
        this.tx = tx;
    }

    @Override
    public List<Object[]> getAllCourses() throws Exception {
        return tx.runInTransaction(courseRepo::getAllCourses);
    }
}
