package com.center.manager.service;

/**
 * Service lay thong tin giang vien / hoc vien.
 */
public interface PersonService {

    /** Lay ho ten giang vien theo ID. */
    String getTeacherName(Long teacherId) throws Exception;

    /** Lay ho ten hoc vien theo ID. */
    String getStudentName(Long studentId) throws Exception;

    /**
     * Lay thong tin profile hoc vien de hien thi len UI.
     */
    Object[] getStudentProfile(Long studentId) throws Exception;
}
