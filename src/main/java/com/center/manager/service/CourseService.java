package com.center.manager.service;

import java.util.List;

/**
 * Service logic lien quan den khoa hoc.
 */
public interface CourseService {

    /**
     * Lay tat ca khoa hoc cho man hinh tao lop.
     */
    List<Object[]> getAllCourses() throws Exception;
}
