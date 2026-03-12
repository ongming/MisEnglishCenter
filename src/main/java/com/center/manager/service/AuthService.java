package com.center.manager.service;

import com.center.manager.db.TransactionManager;
import com.center.manager.model.UserAccount;
import com.center.manager.repo.AuthRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

/**
 * Service xử lý đăng nhập.
 */
public class AuthService {
    private final AuthRepository authRepo;
    private final TransactionManager tx;

    public AuthService(AuthRepository authRepo, TransactionManager tx) {
        this.authRepo = authRepo;
        this.tx = tx;
    }

    /**
     * Kiểm tra đăng nhập.
     * @return Object[] [userId, username, role, teacherId, studentId, staffId] hoặc null nếu thất bại.
     */
    public Object[] login(String username, String rawPassword) throws Exception {
        return tx.runInTransaction(em ->
                Optional.ofNullable(authRepo.findByUsername(em, username))
                        .filter(u -> Boolean.TRUE.equals(u.getIsActive()))
                        .filter(u -> BCrypt.checkpw(rawPassword, u.getPasswordHash()))
                        .map(u -> new Object[]{
                                u.getUserId(), u.getUsername(), u.getRole(),
                                u.getTeacherId(), u.getStudentId(), u.getStaffId()
                        }).orElse(null));
    }
}

