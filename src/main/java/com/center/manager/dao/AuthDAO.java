package com.center.manager.dao;

import org.mindrot.jbcrypt.BCrypt;
import com.center.manager.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * DAO xử lý đăng nhập — truy vấn bảng user_accounts.
 * Thay thế AuthService + UserAccountRepository cũ.
 */
public class AuthDAO {

    /**
     * Kiểm tra đăng nhập.
     *
     * @return mảng Object[] chứa thông tin user nếu thành công, null nếu thất bại.
     *         [0]=userId, [1]=username, [2]=role, [3]=teacherId, [4]=studentId, [5]=staffId
     */
    public Object[] login(String username, String rawPassword) {
        String sql = "SELECT user_id, username, password_hash, role, teacher_id, student_id, staff_id, is_active "
                   + "FROM user_accounts WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                System.out.println("[DEBUG] Không tìm thấy username: " + username);
                return null;
            }

            boolean isActive = rs.getBoolean("is_active");
            if (!isActive) {
                System.out.println("[DEBUG] Tài khoản bị khóa!");
                return null;
            }

            String passwordHash = rs.getString("password_hash");
            boolean matched = BCrypt.checkpw(rawPassword, passwordHash);

            if (!matched) {
                System.out.println("[DEBUG] Mật khẩu sai!");
                return null;
            }

            // Đăng nhập thành công
            Long userId = rs.getLong("user_id");
            String role = rs.getString("role");
            Long teacherId = rs.getObject("teacher_id") != null ? rs.getLong("teacher_id") : null;
            Long studentId = rs.getObject("student_id") != null ? rs.getLong("student_id") : null;
            Long staffId = rs.getObject("staff_id") != null ? rs.getLong("staff_id") : null;

            System.out.println("[DEBUG] Đăng nhập thành công! Role: " + role);
            return new Object[]{userId, username, role, teacherId, studentId, staffId};

        } catch (Exception e) {
            System.err.println("Lỗi khi đăng nhập: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
