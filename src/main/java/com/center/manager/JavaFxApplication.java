package com.center.manager;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * JavaFX Application — Lớp khởi chạy giao diện.
 *
 * --- Tại sao cần class này? ---
 * Project này dùng 2 framework cùng lúc:
 *   1. Spring Boot: Quản lý database, service, repository...
 *   2. JavaFX: Quản lý giao diện (cửa sổ, nút bấm, bảng...)
 *
 * Class này "kết nối" 2 framework lại với nhau:
 *   - init()  : Khởi động Spring Boot
 *   - start() : Mở cửa sổ JavaFX (màn hình Login)
 *   - stop()  : Tắt Spring Boot khi đóng app
 *
 * --- FXMLLoader + Spring ---
 * Bình thường JavaFX tự tạo Controller bằng "new LoginController()".
 * Nhưng LoginController cần AuthService (inject từ Spring).
 * Nên ta phải bảo FXMLLoader: "Đừng tự tạo, hãy lấy từ Spring!"
 * → Dòng: fxmlLoader.setControllerFactory(springContext::getBean)
 */
public class JavaFxApplication extends Application {

    // Spring context — chứa tất cả bean (Service, Repository, Controller...)
    private ConfigurableApplicationContext springContext;

    /**
     * Bước 1: Khởi động Spring Boot (chạy trước start).
     * Spring sẽ kết nối database, tạo các bean (AuthService, Repository...).
     */
    @Override
    public void init() {
        springContext = new SpringApplicationBuilder(Main.class).run();
    }

    /**
     * Bước 2: Mở cửa sổ JavaFX — hiển thị màn hình Login.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        // Load file FXML (giao diện login)
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/fxml/login-view.fxml")
        );

        // ★ QUAN TRỌNG: Bảo FXMLLoader lấy Controller từ Spring
        // Thay vì tự tạo "new LoginController()", nó sẽ lấy bean từ Spring context
        // → Nhờ vậy LoginController mới inject được AuthService
        fxmlLoader.setControllerFactory(springContext::getBean);

        // Load giao diện từ FXML → trả về cây Node (Parent)
        Parent root = fxmlLoader.load();

        // Tạo Scene (khung chứa giao diện) và gắn vào Stage (cửa sổ)
        Scene scene = new Scene(root);
        primaryStage.setTitle("MIS English Center - Đăng nhập");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);  // Không cho kéo thay đổi kích thước
        primaryStage.show();
    }

    /**
     * Bước 3: Khi đóng cửa sổ → tắt Spring Boot và thoát app.
     */
    @Override
    public void stop() {
        springContext.close();     // Tắt Spring (đóng kết nối DB...)
        Platform.exit();          // Tắt JavaFX
    }
}

