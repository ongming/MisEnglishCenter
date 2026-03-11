package com.center.manager.dao;

import com.center.manager.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO cho các chức năng giảng viên: lớp đang dạy, lịch dạy, điểm danh.
 * Thay thế TeacherService + ClassRepository + EnrollmentRepository + ScheduleRepository + AttendanceRepository.
 */
public class ClassDAO {

    // =====================================================
    // 1. LỚP ĐANG DẠY
    // =====================================================

    /**
     * Lấy danh sách lớp mà giảng viên đang dạy.
     * Trả về List<Object[]>, mỗi phần tử: [classId, className, courseName, startDate, endDate, maxStudent, status]
     */
    public List<Object[]> getClassesByTeacher(Long teacherId) {
        List<Object[]> result = new ArrayList<>();
        String sql = """
            SELECT c.class_id, c.class_name, co.course_name,
                   c.start_date, c.end_date, c.max_student, c.status
            FROM classes c
            LEFT JOIN courses co ON c.course_id = co.course_id
            WHERE c.teacher_id = ?
            ORDER BY c.start_date DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, teacherId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new Object[]{
                        rs.getLong("class_id"),
                        rs.getString("class_name"),
                        rs.getString("course_name") != null ? rs.getString("course_name") : "N/A",
                        rs.getDate("start_date") != null ? rs.getDate("start_date").toString() : "",
                        rs.getDate("end_date") != null ? rs.getDate("end_date").toString() : "",
                        rs.getInt("max_student"),
                        rs.getString("status")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Lấy danh sách học viên trong 1 lớp.
     * Trả về List<Object[]>: [studentId, fullName, phone, email, enrollmentDate, enrollmentStatus]
     */
    public List<Object[]> getStudentsInClass(Long classId) {
        List<Object[]> result = new ArrayList<>();
        String sql = """
            SELECT s.student_id, s.full_name, s.phone, s.email,
                   e.enrollment_date, e.status
            FROM enrollments e
            JOIN students s ON e.student_id = s.student_id
            WHERE e.class_id = ?
            ORDER BY s.full_name
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, classId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new Object[]{
                        rs.getLong("student_id"),
                        rs.getString("full_name"),
                        rs.getString("phone") != null ? rs.getString("phone") : "",
                        rs.getString("email") != null ? rs.getString("email") : "",
                        rs.getDate("enrollment_date") != null ? rs.getDate("enrollment_date").toString() : "",
                        rs.getString("status")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // =====================================================
    // 2. LỊCH DẠY
    // =====================================================

    /**
     * Lấy lịch dạy của giảng viên (tất cả buổi học).
     * Trả về List<Object[]>: [scheduleId, className, studyDate, startTime, endTime]
     */
    public List<Object[]> getScheduleByTeacher(Long teacherId) {
        List<Object[]> result = new ArrayList<>();
        String sql = """
            SELECT sc.schedule_id, c.class_name, sc.study_date, sc.start_time, sc.end_time
            FROM schedules sc
            JOIN classes c ON sc.class_id = c.class_id
            WHERE c.teacher_id = ?
            ORDER BY sc.study_date ASC, sc.start_time ASC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, teacherId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new Object[]{
                        rs.getLong("schedule_id"),
                        rs.getString("class_name"),
                        rs.getDate("study_date") != null ? rs.getDate("study_date").toString() : "",
                        rs.getTime("start_time") != null ? rs.getTime("start_time").toString() : "",
                        rs.getTime("end_time") != null ? rs.getTime("end_time").toString() : ""
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // =====================================================
    // 3. ĐIỂM DANH
    // =====================================================

    /**
     * Lấy điểm danh theo lớp + ngày.
     * Trả về List<Object[]>: [attendanceId, studentId, studentName, status, note]
     */
    public List<Object[]> getAttendanceByClassAndDate(Long classId, String attendDate) {
        List<Object[]> result = new ArrayList<>();
        String sql = """
            SELECT a.attendance_id, a.student_id, s.full_name, a.status, a.note
            FROM attendances a
            JOIN students s ON a.student_id = s.student_id
            WHERE a.class_id = ? AND a.attend_date = ?
            ORDER BY s.full_name
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, classId);
            ps.setString(2, attendDate);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new Object[]{
                        rs.getLong("attendance_id"),
                        rs.getLong("student_id"),
                        rs.getString("full_name"),
                        rs.getString("status"),
                        rs.getString("note") != null ? rs.getString("note") : ""
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Lấy toàn bộ lịch sử điểm danh của 1 lớp.
     * Trả về List<Object[]>: [attendanceId, studentName, attendDate, status, note]
     */
    public List<Object[]> getAttendanceHistoryByClass(Long classId) {
        List<Object[]> result = new ArrayList<>();
        String sql = """
            SELECT a.attendance_id, s.full_name, a.attend_date, a.status, a.note
            FROM attendances a
            JOIN students s ON a.student_id = s.student_id
            WHERE a.class_id = ?
            ORDER BY a.attend_date DESC, s.full_name
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, classId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new Object[]{
                        rs.getLong("attendance_id"),
                        rs.getString("full_name"),
                        rs.getDate("attend_date") != null ? rs.getDate("attend_date").toString() : "",
                        rs.getString("status"),
                        rs.getString("note") != null ? rs.getString("note") : ""
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Lưu/cập nhật điểm danh cho 1 học viên.
     *
     * @param attendanceId null nếu tạo mới
     * @param studentId    ID học viên
     * @param classId      ID lớp
     * @param attendDate   ngày điểm danh (yyyy-MM-dd)
     * @param status       Present / Absent / Late
     * @param note         ghi chú
     */
    public void saveAttendance(Long attendanceId, Long studentId, Long classId,
                               String attendDate, String status, String note) {
        if (attendanceId != null && attendanceId > 0) {
            // UPDATE
            String sql = "UPDATE attendances SET status = ?, note = ? WHERE attendance_id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, status);
                ps.setString(2, note);
                ps.setLong(3, attendanceId);
                ps.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // INSERT
            String sql = "INSERT INTO attendances (student_id, class_id, attend_date, status, note) VALUES (?,?,?,?,?)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, studentId);
                ps.setLong(2, classId);
                ps.setString(3, attendDate);
                ps.setString(4, status);
                ps.setString(5, note);
                ps.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // =====================================================
    // 4. LỊCH HỌC CỦA SINH VIÊN
    // =====================================================

    /**
     * Lấy lịch học của sinh viên.
     */
    public List<Object[]> getScheduleByStudent(Long studentId) {
        List<Object[]> result = new ArrayList<>();
        String sql = """
            SELECT sc.schedule_id, c.class_name, sc.study_date, sc.start_time, sc.end_time
            FROM schedules sc
            JOIN classes c ON sc.class_id = c.class_id
            JOIN enrollments e ON e.class_id = c.class_id
            WHERE e.student_id = ?
            ORDER BY sc.study_date ASC, sc.start_time ASC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new Object[]{
                        rs.getLong("schedule_id"),
                        rs.getString("class_name"),
                        rs.getDate("study_date") != null ? rs.getDate("study_date").toString() : "",
                        rs.getTime("start_time") != null ? rs.getTime("start_time").toString() : "",
                        rs.getTime("end_time") != null ? rs.getTime("end_time").toString() : ""
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Lấy danh sách lớp sinh viên đang học.
     */
    public List<Object[]> getClassesByStudent(Long studentId) {
        List<Object[]> result = new ArrayList<>();
        String sql = """
            SELECT c.class_id, c.class_name, co.course_name,
                   c.start_date, c.end_date, c.status, e.status AS enroll_status
            FROM enrollments e
            JOIN classes c ON e.class_id = c.class_id
            LEFT JOIN courses co ON c.course_id = co.course_id
            WHERE e.student_id = ?
            ORDER BY c.start_date DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new Object[]{
                        rs.getLong("class_id"),
                        rs.getString("class_name"),
                        rs.getString("course_name") != null ? rs.getString("course_name") : "N/A",
                        rs.getDate("start_date") != null ? rs.getDate("start_date").toString() : "",
                        rs.getDate("end_date") != null ? rs.getDate("end_date").toString() : "",
                        rs.getString("status"),
                        rs.getString("enroll_status")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Lấy lịch sử điểm danh của sinh viên.
     */
    public List<Object[]> getAttendanceByStudent(Long studentId) {
        List<Object[]> result = new ArrayList<>();
        String sql = """
            SELECT c.class_name, a.attend_date, a.status, a.note
            FROM attendances a
            JOIN classes c ON a.class_id = c.class_id
            WHERE a.student_id = ?
            ORDER BY a.attend_date DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new Object[]{
                        rs.getString("class_name"),
                        rs.getDate("attend_date") != null ? rs.getDate("attend_date").toString() : "",
                        rs.getString("status"),
                        rs.getString("note") != null ? rs.getString("note") : ""
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Lấy thông tin profile sinh viên.
     */
    public Object[] getStudentProfile(Long studentId) {
        String sql = """
            SELECT student_id, full_name, date_of_birth, gender, phone, email, address,
                   registration_date, status
            FROM students WHERE student_id = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Object[]{
                        rs.getLong("student_id"),
                        rs.getString("full_name"),
                        rs.getDate("date_of_birth") != null ? rs.getDate("date_of_birth").toString() : "",
                        rs.getString("gender") != null ? rs.getString("gender") : "",
                        rs.getString("phone") != null ? rs.getString("phone") : "",
                        rs.getString("email") != null ? rs.getString("email") : "",
                        rs.getString("address") != null ? rs.getString("address") : "",
                        rs.getDate("registration_date") != null ? rs.getDate("registration_date").toString() : "",
                        rs.getString("status")
                };
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

