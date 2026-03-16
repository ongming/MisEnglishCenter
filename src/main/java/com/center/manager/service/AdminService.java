package com.center.manager.service;

import java.util.List;

/**
 * Service xu ly nghiep vu Admin: quan ly giao vien, tai khoan va lop hoc.
 */
public interface AdminService {

    List<Object[]> getAllTeachers() throws Exception;

    List<Object[]> getAllStudents() throws Exception;

    Long createStudent(String fullName, String dateOfBirth, String gender,
                       String phone, String email, String address,
                       String registrationDate, String status) throws Exception;

    void updateStudent(Long studentId, String fullName, String dateOfBirth, String gender,
                       String phone, String email, String address,
                       String registrationDate, String status) throws Exception;

    void deleteStudent(Long studentId) throws Exception;

    Long createTeacher(String fullName, String phone, String email, String specialty,
                       String hireDate, String status) throws Exception;

    void deleteTeacher(Long teacherId) throws Exception;

    List<Object[]> getTeachersWithoutAccount() throws Exception;

    List<Object[]> getStudentsWithoutAccount() throws Exception;

    List<Object[]> getStudentsNotInClass(Long classId) throws Exception;

    void addStudentToClass(Long classId, Long studentId) throws Exception;

    void removeStudentFromClass(Long classId, Long studentId) throws Exception;

    void createTeacherAccount(Long teacherId, String username, String rawPassword) throws Exception;

    void createStudentAccount(Long studentId, String username, String rawPassword) throws Exception;

    Long createClass(String className, Long courseId, Long teacherId, String startDate,
                     String endDate, Integer maxStudent, Long roomId) throws Exception;
}
