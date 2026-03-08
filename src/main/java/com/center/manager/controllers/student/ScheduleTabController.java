package com.center.manager.controllers.student;

import javafx.fxml.FXML;
import org.springframework.stereotype.Component;

/** Controller tab "Lịch học" cho Sinh viên. (Chưa kết nối DB) */
@Component("studentScheduleTabController")  // Đặt tên riêng để không trùng với teacher
public class ScheduleTabController {
    @FXML
    private void initialize() {
        // TODO: Load lịch học từ DB
    }
}


