package com.center.manager.controllers.teacher;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import org.springframework.stereotype.Component;

/**
 * Controller cho tab "Lịch dạy" của Giảng viên.
 * Liên kết với file: teacher/schedule-tab.fxml
 *
 * Chức năng: Hiển thị lịch dạy (ngày, giờ, lớp) của giảng viên.
 * (Chưa kết nối DB — chỉ là khung giao diện)
 */
@Component("teacherScheduleTabController")  // Đặt tên riêng để không trùng với student
public class ScheduleTabController {

    @FXML private TableView<?> tableSchedule;

    @FXML
    private void initialize() {
        // TODO: Load lịch dạy từ DB theo teacher_id
    }
}


