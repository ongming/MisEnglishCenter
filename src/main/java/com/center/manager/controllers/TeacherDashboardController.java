package com.center.manager.controllers;

import com.center.manager.config.UserSession;
import com.center.manager.entities.UserAccount;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Controller cho Dashboard Giảng viên (teacher-dashboard-view.fxml).
 *
 * Chức năng:
 *   - Hiển thị sidebar với các tab: Lớp đang dạy, Lịch dạy, Điểm danh
 *   - Khi nhấn tab → load file FXML tương ứng vào vùng contentArea
 *   - Đăng xuất → quay về màn hình Login
 */
@Component
public class TeacherDashboardController {

    // ========== FXML Controls ==========
    @FXML private Label lblSidebarName;    // Tên giảng viên trên sidebar
    @FXML private Label lblSidebarRole;    // Badge "Teacher"
    @FXML private StackPane contentArea;   // Vùng hiển thị nội dung bên phải

    // Các nút sidebar — để đổi style khi active
    @FXML private Button btnMyClasses;
    @FXML private Button btnSchedule;
    @FXML private Button btnAttendance;

    // Spring context — dùng để load Controller từ Spring khi chuyển tab
    private final ApplicationContext springContext;

    // Nút đang được chọn (active) — để đổi style
    private Button currentActiveBtn;

    public TeacherDashboardController(ApplicationContext springContext) {
        this.springContext = springContext;
    }

    /**
     * Khởi tạo sau khi FXML load xong.
     * Hiển thị tên giảng viên và load tab mặc định.
     */
    @FXML
    private void initialize() {
        // Lấy thông tin user đang đăng nhập từ session
        UserAccount user = UserSession.getInstance().getCurrentUser();
        if (user != null) {
            lblSidebarName.setText(user.getUsername());
            lblSidebarRole.setText("Giảng viên");
        }

        // Load tab mặc định: "Lớp đang dạy"
        handleMyClassesTab();
    }

    // ========== XỬ LÝ CHUYỂN TAB ==========

    /** Nhấn "Lớp đang dạy" */
    @FXML
    private void handleMyClassesTab() {
        loadTab("/fxml/teacher/my-classes-tab.fxml");
        setActiveButton(btnMyClasses);
    }

    /** Nhấn "Lịch dạy" */
    @FXML
    private void handleScheduleTab() {
        loadTab("/fxml/teacher/schedule-tab.fxml");
        setActiveButton(btnSchedule);
    }

    /** Nhấn "Điểm danh" */
    @FXML
    private void handleAttendanceTab() {
        loadTab("/fxml/teacher/attendance-tab.fxml");
        setActiveButton(btnAttendance);
    }

    /**
     * Load file FXML vào vùng contentArea.
     *
     * Mỗi tab là 1 file FXML riêng → khi nhấn tab, ta load file đó
     * và thay thế nội dung trong contentArea.
     *
     * @param fxmlPath đường dẫn file FXML (ví dụ: "/fxml/teacher/my-classes-tab.fxml")
     */
    private void loadTab(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            // Lấy Controller từ Spring (để sau này inject Service, Repository...)
            loader.setControllerFactory(springContext::getBean);
            Parent tabContent = loader.load();

            // Xóa nội dung cũ, thay bằng nội dung tab mới
            contentArea.getChildren().clear();
            contentArea.getChildren().add(tabContent);
        } catch (IOException e) {
            System.err.println("Không thể load tab: " + fxmlPath);
            e.printStackTrace();
        }
    }

    /**
     * Đổi style nút sidebar: nút được chọn sẽ có class "sidebar-btn-active".
     * Nút cũ sẽ trở về style bình thường.
     */
    private void setActiveButton(Button btn) {
        // Bỏ active style của nút cũ
        if (currentActiveBtn != null) {
            currentActiveBtn.getStyleClass().remove("sidebar-btn-active");
        }
        // Thêm active style cho nút mới
        btn.getStyleClass().add("sidebar-btn-active");
        currentActiveBtn = btn;
    }

    /**
     * Đăng xuất: Xóa session → quay về màn hình Login.
     */
    @FXML
    private void handleLogout() {
        // Xóa thông tin phiên đăng nhập
        UserSession.getInstance().clear();

        try {
            // Load lại màn hình Login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login-view.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent loginRoot = loader.load();

            // Lấy Stage hiện tại và đổi sang màn hình Login
            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setScene(new Scene(loginRoot));
            stage.setTitle("MIS English Center - Đăng nhập");
            stage.setResizable(false);
        } catch (IOException e) {
            System.err.println("Không thể quay về màn hình Login");
            e.printStackTrace();
        }
    }
}

