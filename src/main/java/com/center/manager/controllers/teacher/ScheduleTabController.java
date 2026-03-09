package com.center.manager.controllers.teacher;

import com.center.manager.config.UserSession;
import com.center.manager.dto.ScheduleDTO;
import com.center.manager.services.TeacherService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Controller cho tab "Lịch dạy" của Giảng viên.
 * Liên kết với file: teacher/schedule-tab.fxml
 *
 * Chức năng:
 *   - Hiển thị toàn bộ lịch dạy (các buổi học) của GV.
 *   - Dữ liệu lấy từ bảng schedules → lọc theo các lớp GV đang dạy.
 *
 * --- Luồng dữ liệu ---
 * initialize() → TeacherService.getScheduleByTeacher(teacherId) → hiện bảng lịch
 */
@Component("teacherScheduleTabController")  // Đặt tên riêng để không trùng với student
public class ScheduleTabController {

    // ========== FXML Controls ==========
    @FXML private TableView<ScheduleDTO> tableSchedule;
    @FXML private TableColumn<ScheduleDTO, String> colSchClass;
    @FXML private TableColumn<ScheduleDTO, String> colSchDate;
    @FXML private TableColumn<ScheduleDTO, String> colSchStart;
    @FXML private TableColumn<ScheduleDTO, String> colSchEnd;

    // ========== Service ==========
    private final TeacherService teacherService;

    public ScheduleTabController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    /**
     * Khởi tạo: cấu hình cột + load dữ liệu lịch dạy.
     */
    @FXML
    private void initialize() {
        // --- Cấu hình cột ---
        colSchClass.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getClassName()));
        colSchDate.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getStudyDate()));
        colSchStart.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getStartTime()));
        colSchEnd.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getEndTime()));

        // --- Load dữ liệu ---
        loadSchedule();
    }

    /**
     * Load lịch dạy của GV từ DB → hiển thị lên tableSchedule.
     */
    private void loadSchedule() {
        Long teacherId = UserSession.getInstance().getCurrentUser().getTeacherId();

        if (teacherId == null) {
            System.out.println("[WARN] teacherId = null, GV chưa liên kết teacher.");
            return;
        }

        // Gọi Service: lấy tất cả buổi học của tất cả lớp GV đang dạy
        List<ScheduleDTO> schedules = teacherService.getScheduleByTeacher(teacherId);

        // Hiển thị lên bảng
        ObservableList<ScheduleDTO> data = FXCollections.observableArrayList(schedules);
        tableSchedule.setItems(data);
    }
}


