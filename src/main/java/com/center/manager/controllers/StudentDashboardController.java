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
 * Controller cho Dashboard Sinh viên (student-dashboard-view.fxml).
 *
 * Chức năng:
 *   - Hiển thị sidebar với các tab: Tổng quan, Thông tin, Lớp học, Lịch học, Điểm danh, Học phí...
 *   - Khi nhấn tab → load file FXML tương ứng vào vùng contentArea
 *   - Đăng xuất → quay về màn hình Login
 */
@Component
public class StudentDashboardController {

    // ========== FXML Controls ==========
    @FXML private Label lblSidebarName;    // Tên sinh viên trên sidebar
    @FXML private Label lblSidebarRole;    // Badge "Student"
    @FXML private StackPane contentArea;   // Vùng hiển thị nội dung bên phải

    // Các nút sidebar
    @FXML private Button btnOverview;
    @FXML private Button btnProfile;
    @FXML private Button btnClasses;
    @FXML private Button btnSchedule;
    @FXML private Button btnAttendance;
    @FXML private Button btnResults;
    @FXML private Button btnFinance;
    @FXML private Button btnCertificates;
    @FXML private Button btnNotifications;

    // Spring context — dùng để load Controller từ Spring khi chuyển tab
    private final ApplicationContext springContext;

    // Nút đang được chọn (active)
    private Button currentActiveBtn;

    public StudentDashboardController(ApplicationContext springContext) {
        this.springContext = springContext;
    }

    @FXML
    private void initialize() {
        // Hiển thị tên user đang đăng nhập
        UserAccount user = UserSession.getInstance().getCurrentUser();
        if (user != null) {
            lblSidebarName.setText(user.getUsername());
            lblSidebarRole.setText("Học viên");
        }

        // Load tab mặc định: "Tổng quan"
        handleOverviewTab();
    }

    // ========== XỬ LÝ CHUYỂN TAB ==========

    @FXML
    private void handleOverviewTab() {
        loadTab("/fxml/student/overview-tab.fxml");
        setActiveButton(btnOverview);
    }

    @FXML
    private void handleProfileTab() {
        loadTab("/fxml/student/profile-tab.fxml");
        setActiveButton(btnProfile);
    }

    @FXML
    private void handleClassesTab() {
        loadTab("/fxml/student/classes-tab.fxml");
        setActiveButton(btnClasses);
    }

    @FXML
    private void handleScheduleTab() {
        loadTab("/fxml/student/schedule-tab.fxml");
        setActiveButton(btnSchedule);
    }

    @FXML
    private void handleAttendanceTab() {
        loadTab("/fxml/student/attendance-tab.fxml");
        setActiveButton(btnAttendance);
    }

    @FXML
    private void handleResultsTab() {
        loadTab("/fxml/student/results-tab.fxml");
        setActiveButton(btnResults);
    }

    @FXML
    private void handleFinanceTab() {
        loadTab("/fxml/student/finance-tab.fxml");
        setActiveButton(btnFinance);
    }

    @FXML
    private void handleCertificatesTab() {
        loadTab("/fxml/student/certificates-tab.fxml");
        setActiveButton(btnCertificates);
    }

    @FXML
    private void handleNotificationsTab() {
        loadTab("/fxml/student/notifications-tab.fxml");
        setActiveButton(btnNotifications);
    }

    /**
     * Load file FXML vào vùng contentArea (giống TeacherDashboardController).
     */
    private void loadTab(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(springContext::getBean);
            Parent tabContent = loader.load();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(tabContent);
        } catch (IOException e) {
            System.err.println("Không thể load tab: " + fxmlPath);
            e.printStackTrace();
        }
    }

    /**
     * Đổi style nút sidebar khi chuyển tab.
     */
    private void setActiveButton(Button btn) {
        if (currentActiveBtn != null) {
            currentActiveBtn.getStyleClass().remove("sidebar-btn-active");
        }
        btn.getStyleClass().add("sidebar-btn-active");
        currentActiveBtn = btn;
    }

    /**
     * Đăng xuất: Xóa session → quay về Login.
     */
    @FXML
    private void handleLogout() {
        UserSession.getInstance().clear();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login-view.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent loginRoot = loader.load();

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

