package com.center.manager.util;

/**
 * Singleton lưu thông tin người dùng đang đăng nhập.
 */
public class UserSession {

    private static final UserSession instance = new UserSession();

    private Long userId;
    private String username;
    private String role;        // Admin, Teacher, Student, Staff
    private Long teacherId;
    private Long studentId;
    private Long staffId;

    private UserSession() {}

    public static UserSession getInstance() {
        return instance;
    }

    public void set(Long userId, String username, String role,
                    Long teacherId, Long studentId, Long staffId) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.teacherId = teacherId;
        this.studentId = studentId;
        this.staffId = staffId;
    }

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

