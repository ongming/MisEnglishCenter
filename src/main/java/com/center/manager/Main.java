package com.center.manager;

import com.center.manager.ui.LoginFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Điểm khởi chạy chính của ứng dụng.
 * Không dùng Spring Boot — chỉ dùng Java Swing thuần.
 */
public class Main {
    public static void main(String[] args) {
        // Set Look and Feel cho đẹp hơn
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        // Khởi chạy giao diện Swing trên EDT (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}

