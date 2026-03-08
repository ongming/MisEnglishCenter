package com.center.manager.controllers.teacher;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import org.springframework.stereotype.Component;

/**
 * Controller cho tab "Lớp đang dạy" của Giảng viên.
 * Liên kết với file: teacher/my-classes-tab.fxml
 *
 * Chức năng:
 *   - Hiển thị danh sách lớp mà giảng viên đang phụ trách
 *   - Khi chọn 1 lớp → hiển thị danh sách học viên trong lớp đó
 *
 * (Chưa kết nối DB — chỉ là khung giao diện)
 */
@Component
public class MyClassesTabController {

    @FXML private TableView<?> tableClasses;    // Bảng danh sách lớp
    @FXML private TableView<?> tableStudents;   // Bảng danh sách học viên
    @FXML private Label lblStudentListTitle;     // Tiêu đề bảng học viên

    @FXML
    private void initialize() {
        // TODO: Load danh sách lớp từ DB theo teacher_id
    }
}

