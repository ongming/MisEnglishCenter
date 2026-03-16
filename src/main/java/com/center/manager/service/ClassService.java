package com.center.manager.service;

import java.util.List;

/**
 * Service cho lop hoc va lich hoc.
 */
public interface ClassService {

    List<Object[]> getAllClasses() throws Exception;

    List<Object[]> getClassesByTeacher(Long teacherId) throws Exception;

    List<Object[]> getStudentsInClass(Long classId) throws Exception;

    List<Object[]> getScheduleByTeacher(Long teacherId) throws Exception;

    List<Object[]> getClassesByStudent(Long studentId) throws Exception;

    List<Object[]> getScheduleByStudent(Long studentId) throws Exception;
}
