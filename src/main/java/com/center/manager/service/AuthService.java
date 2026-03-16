package com.center.manager.service;

/**
 * Service xu ly dang nhap.
 */
public interface AuthService {

    /**
     * Xac thuc username/password va tra ve thong tin session.
     *
     * @return Object[] [userId, username, role, teacherId, studentId, staffId]
     *         hoac null neu dang nhap that bai.
     */
    Object[] login(String username, String rawPassword) throws Exception;
}
