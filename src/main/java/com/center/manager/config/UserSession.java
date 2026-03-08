package com.center.manager.config;

import com.center.manager.entities.UserAccount;

/**
 * Lưu thông tin người dùng đang đăng nhập (phiên làm việc).
 *
 * --- Tại sao cần UserSession? ---
 * Sau khi đăng nhập thành công, các màn hình khác cần biết:
 *   - Ai đang đăng nhập? (username)
 *   - Vai trò gì? (Admin/Teacher/Student/Staff)
 *   - ID liên kết là gì? (teacherId, studentId...)
 *
 * UserSession lưu lại thông tin này để dùng xuyên suốt ứng dụng.
 *
 * --- Singleton Pattern ---
 * Chỉ có DUY NHẤT 1 instance UserSession trong toàn bộ app.
 * Gọi UserSession.getInstance() ở bất kỳ đâu đều ra cùng 1 object.
 */
public class UserSession {

    // Biến static duy nhất — chỉ tạo 1 lần
    private static UserSession instance;

    // Thông tin người dùng đang đăng nhập
    private UserAccount currentUser;

    // Constructor private → không ai tạo được bằng "new UserSession()"
    private UserSession() {}

    /**
     * Lấy instance duy nhất của UserSession.
     * Nếu chưa tồn tại → tạo mới.
     * Nếu đã tồn tại → trả về cái cũ.
     */
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    /**
     * Lưu thông tin người dùng sau khi đăng nhập thành công.
     */
    public void setCurrentUser(UserAccount user) {
        this.currentUser = user;
    }

    /**
     * Lấy thông tin người dùng đang đăng nhập.
     */
    public UserAccount getCurrentUser() {
        return this.currentUser;
    }

    /**
     * Đăng xuất — xóa thông tin phiên làm việc.
     */
    public void clear() {
        this.currentUser = null;
    }
}

