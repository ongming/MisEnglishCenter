package com.center.manager.service;

import com.center.manager.db.TransactionManager;
import com.center.manager.model.UserAccount;
import com.center.manager.repo.AuthRepository;
import org.mindrot.jbcrypt.BCrypt;

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
        return tx.runInTransaction(em -> {
            UserAccount user = authRepo.findByUsername(em, username);

            if (user == null) {
                System.out.println("[DEBUG] Không tìm thấy username: " + username);
                return null;
            }

            if (user.getIsActive() == null || !user.getIsActive()) {
                System.out.println("[DEBUG] Tài khoản bị khóa!");
                return null;
            }

            if (!BCrypt.checkpw(rawPassword, user.getPasswordHash())) {
                System.out.println("[DEBUG] Mật khẩu sai!");
                return null;
            }

            System.out.println("[DEBUG] Đăng nhập thành công! Role: " + user.getRole());
            return new Object[]{
                    user.getUserId(),
                    user.getUsername(),
                    user.getRole(),
                    user.getTeacherId(),
                    user.getStudentId(),
                    user.getStaffId()
            };
        });
    }
}

