package com.center.manager;

import com.center.manager.db.Jpa;
import com.center.manager.ui.LoginFrame;

import javax.swing.*;

/**
 * Điểm khởi chạy chính của ứng dụng.
 * Không dùng Spring Boot — chỉ dùng Java Swing thuần + JPA (Hibernate).
 */
public class Main {
    public static void main(String[] args) {
        // Đóng JPA khi thoát ứng dụng
        Runtime.getRuntime().addShutdownHook(new Thread(Jpa::shutdown));

        // Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Khởi chạy giao diện Swing trên EDT
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
