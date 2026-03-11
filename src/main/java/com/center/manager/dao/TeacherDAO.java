package com.center.manager.dao;

import com.center.manager.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * DAO cho bảng teachers.
 */
public class TeacherDAO {

    /**
     * Lấy tên giảng viên theo teacher_id.
     */
    public String getTeacherName(Long teacherId) {
        String sql = "SELECT full_name FROM teachers WHERE teacher_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, teacherId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("full_name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lấy tên sinh viên theo student_id.
     */
    public String getStudentName(Long studentId) {
        String sql = "SELECT full_name FROM students WHERE student_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("full_name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

