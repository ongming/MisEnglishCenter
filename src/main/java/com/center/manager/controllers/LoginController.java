package com.center.manager.controllers;

import com.center.manager.config.UserSession;
import com.center.manager.entities.UserAccount;
import com.center.manager.services.AuthService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

/**
 * Controller cho màn hình Đăng nhập (login-view.fxml).
 *
 * --- Mỗi file .fxml có 1 Controller tương ứng ---
 * login-view.fxml → LoginController.java
 *
 * --- fx:id trong FXML liên kết với @FXML trong Java ---
 * Ví dụ: <TextField fx:id="txtUsername" .../> → liên kết với field txtUsername bên dưới.
 *
 * --- @Component ---
 * Đánh dấu để Spring quản lý class này (tạo bean).
 * Cần thiết vì Controller này cần inject AuthService từ Spring.
 */
@Component  // Spring quản lý class này
public class LoginController {

    // ========== FXML Controls ==========
    // Các field này tự động liên kết với fx:id trong login-view.fxml

    @FXML private TextField txtUsername;        // Ô nhập tên đăng nhập
    @FXML private PasswordField txtPassword;    // Ô nhập mật khẩu
    @FXML private Button btnLogin;              // Nút "ĐĂNG NHẬP"
    @FXML private Label lblError;               // Label hiển thị lỗi
    @FXML private ProgressIndicator loadingSpinner;  // Vòng xoay loading
    @FXML private CheckBox chkRemember;         // Checkbox "Ghi nhớ đăng nhập"

    // ========== Service ==========
    private final AuthService authService;

    // Spring context — cần để khi chuyển trang, FXMLLoader lấy Controller từ Spring
    private final ApplicationContext springContext;

    /**
     * Constructor Injection — Spring tự truyền AuthService và ApplicationContext vào.
     */
    public LoginController(AuthService authService, ApplicationContext springContext) {
        this.authService = authService;
        this.springContext = springContext;
    }

    /**
     * Hàm initialize() được JavaFX tự gọi SAU KHI load xong file FXML.
     * Dùng để thiết lập ban đầu cho giao diện.
     */
    @FXML
    private void initialize() {
        // Ẩn lỗi khi mới mở màn hình
        lblError.setVisible(false);

        // Cho phép nhấn Enter trong ô mật khẩu để đăng nhập
        // (Lambda: khi nhấn phím trong txtPassword → nếu là Enter → gọi handleLogin)
        txtPassword.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) {
                handleLogin();
            }
        });
    }

    /**
     * Xử lý khi nhấn nút "ĐĂNG NHẬP".
     * Hàm này được gọi từ FXML: onAction="#handleLogin"
     */
    @FXML
    private void handleLogin() {
        // Lấy dữ liệu từ giao diện
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        // --- Validate: kiểm tra input trước khi gửi lên server ---
        if (username.isEmpty() || password.isEmpty()) {
            showError("Vui lòng nhập tên đăng nhập và mật khẩu.");
            return;  // Dừng lại, không xử lý tiếp
        }

        // Hiện loading, ẩn lỗi cũ
        lblError.setVisible(false);
        loadingSpinner.setVisible(true);
        btnLogin.setDisable(true);  // Khóa nút để tránh nhấn nhiều lần

        // --- Chạy trên thread riêng để giao diện không bị đơ ---
        // (Vì truy vấn DB có thể mất thời gian)
        new Thread(() -> {

            // Gọi AuthService để kiểm tra đăng nhập
            Optional<UserAccount> result = authService.login(username, password);

            // --- Quay lại UI thread để cập nhật giao diện ---
            // (JavaFX bắt buộc: chỉ được thay đổi UI trên JavaFX Application Thread)
            Platform.runLater(() -> {

                // Tắt loading
                loadingSpinner.setVisible(false);
                btnLogin.setDisable(false);

                if (result.isPresent()) {
                    // ✅ Đăng nhập thành công
                    UserAccount user = result.get();

                    // Lưu thông tin user vào session
                    UserSession.getInstance().setCurrentUser(user);

                    System.out.println("Đăng nhập thành công! Role: " + user.getRole());

                    // Chuyển sang màn hình tương ứng với role
                    navigateToDashboard(user.getRole());

                } else {
                    // ❌ Đăng nhập thất bại
                    showError("Sai tên đăng nhập hoặc mật khẩu.");
                }
            });

        }).start();
    }

    /**
     * Chuyển sang màn hình Dashboard tương ứng với role của user.
     *
     * Dựa vào role trong user_accounts:
     *   - "Student" → student-dashboard-view.fxml
     *   - "Teacher" → teacher-dashboard-view.fxml
     *   - "Admin" / "Staff" → (tạm thời in ra console, sẽ làm sau)
     *
     * @param role vai trò của user (Admin, Teacher, Student, Staff)
     */
    private void navigateToDashboard(String role) {
        // Xác định file FXML và tiêu đề cửa sổ dựa theo role
        String fxmlPath;
        String title;

        switch (role) {
            case "Student":
                fxmlPath = "/fxml/student-dashboard-view.fxml";
                title = "MIS English Center - Học viên";
                break;
            case "Teacher":
                fxmlPath = "/fxml/teacher-dashboard-view.fxml";
                title = "MIS English Center - Giảng viên";
                break;
            default:
                // Admin, Staff — chưa có giao diện, tạm in ra console
                System.out.println("Role '" + role + "' chưa có giao diện Dashboard.");
                return;
        }

        try {
            // Load file FXML của Dashboard
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

            // ★ Lấy Controller từ Spring (để inject được Service, Repository...)
            loader.setControllerFactory(springContext::getBean);

            Parent dashboardRoot = loader.load();

            // Lấy Stage (cửa sổ) hiện tại từ bất kỳ control nào trên màn hình
            Stage stage = (Stage) btnLogin.getScene().getWindow();

            // Đổi Scene sang Dashboard, cho phép resize
            stage.setScene(new Scene(dashboardRoot));
            stage.setTitle(title);
            stage.setResizable(true);
            stage.centerOnScreen();  // Canh giữa màn hình

        } catch (IOException e) {
            System.err.println("Không thể mở Dashboard cho role: " + role);
            e.printStackTrace();
            showError("Lỗi khi chuyển trang. Vui lòng thử lại.");
        }
    }

    /**
     * Hiển thị thông báo lỗi trên giao diện.
     */
    private void showError(String message) {
        lblError.setText(message);
        lblError.setVisible(true);
    }
}





