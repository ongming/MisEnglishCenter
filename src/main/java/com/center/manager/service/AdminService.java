package com.center.manager.service;

import java.util.List;

/**
 * Service xu ly nghiep vu Admin: quan ly giao vien va tao tai khoan.
 */
public interface AdminService {

    List<Object[]> getAllTeachers() throws Exception;

    /** Tao giao vien moi va tra ve teacherId duoc tao. */
    Long createTeacher(String fullName, String phone, String email, String specialty,
                       String hireDate, String status) throws Exception;

    List<Object[]> getTeachersWithoutAccount() throws Exception;

    List<Object[]> getStudentsWithoutAccount() throws Exception;

    void createTeacherAccount(Long teacherId, String username, String rawPassword) throws Exception;

    void createStudentAccount(Long studentId, String username, String rawPassword) throws Exception;
}
