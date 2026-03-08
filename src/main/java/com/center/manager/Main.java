package com.center.manager;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Điểm khởi chạy chính của ứng dụng.
 *
 * --- @SpringBootApplication ---
 * Đánh dấu đây là ứng dụng Spring Boot.
 * Spring sẽ tự quét (scan) tất cả class trong package com.center.manager
 * để tìm @Entity, @Repository, @Service, @Component... và tạo bean.
 *
 * --- Tại sao gọi JavaFxApplication? ---
 * Main.java là nơi JVM gọi đầu tiên (hàm main).
 * Từ đây ta khởi chạy JavaFX → JavaFX sẽ khởi chạy Spring Boot bên trong.
 */
@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        // Khởi chạy ứng dụng JavaFX (JavaFxApplication.init → start)
        Application.launch(JavaFxApplication.class, args);
    }
}
