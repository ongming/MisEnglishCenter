package com.center.manager.controllers.teacher;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import org.springframework.stereotype.Component;

/**
 * Controller cho tab "Điểm danh" của Giảng viên.
 * Liên kết với file: teacher/attendance-tab.fxml
 *
 * Chức năng: Xem và quản lý điểm danh học viên trong các lớp đang dạy.
 * (Chưa kết nối DB — chỉ là khung giao diện)
 */
@Component("teacherAttendanceTabController")  // Đặt tên riêng để không trùng với student
public class AttendanceTabController {

    @FXML private TableView<?> tableAttendance;

    @FXML
    private void initialize() {
        // TODO: Load dữ liệu điểm danh từ DB
    }
}


