package com.center.manager.controllers.student;

import javafx.fxml.FXML;
import org.springframework.stereotype.Component;

/** Controller tab "Điểm danh" cho Sinh viên. (Chưa kết nối DB) */
@Component("studentAttendanceTabController")  // Đặt tên riêng để không trùng với teacher
public class AttendanceTabController {
    @FXML
    private void initialize() {
        // TODO: Load dữ liệu điểm danh từ DB
    }
}


