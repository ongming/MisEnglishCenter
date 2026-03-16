package com.center.manager.util;

/**
 * Singleton lưu thông tin người dùng đang đăng nhập.
 * Giúp truy xuất thông tin user hiện tại ở mọi nơi trong app.
 * Dùng cho phân quyền, hiển thị tên, role, id liên quan.
 */
public class UserSession {
    // Instance duy nhất (singleton)
    private static final UserSession instance = new UserSession();

    // ID tài khoản user (user_accounts)
    private Long userId;
    // Tên đăng nhập
    private String username;
    // Vai trò: Admin, Teacher, Student, Staff
    private String role;
    // ID giáo viên nếu là giáo viên
    private Long teacherId;
    // ID học viên nếu là học viên
    private Long studentId;
    // ID nhân viên nếu là staff
    private Long staffId;

    // Constructor private để chỉ có 1 instance
    private UserSession() {}

    // Lấy instance duy nhất
    public static UserSession getInstance() {
        return instance;
    }

    /**
     * Set thông tin user khi đăng nhập thành công
     */
    public void set(Long userId, String username, String role,
                    Long teacherId, Long studentId, Long staffId) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.teacherId = teacherId;
        this.studentId = studentId;
        this.staffId = staffId;
    }

    /**
     * Xóa thông tin user khi logout
     */
    public void clear() {
        userId = null;
        username = null;
        role = null;
        teacherId = null;
        studentId = null;
        staffId = null;
    }

    public Long getUserId()    { return userId; }
    public String getUsername(){ return username; }
    public String getRole()   { return role; }
    public Long getTeacherId(){ return teacherId; }
    public Long getStudentId(){ return studentId; }
    public Long getStaffId()  { return staffId; }
}
