package com.center.manager.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * Quản lý kết nối JDBC tới MySQL.
 * Đọc cấu hình từ file db.properties trong classpath.
 */
public class DatabaseConnection {

    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) throw new RuntimeException("Không tìm thấy file db.properties trong classpath!");
            PROPS.load(in);
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khởi tạo kết nối DB: " + e.getMessage(), e);
        }
    }

    /**
     * Tạo và trả về 1 Connection mới tới MySQL.
     * Caller phải tự đóng Connection sau khi dùng xong (try-with-resources).
     */
    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(
                PROPS.getProperty("db.url"),
                PROPS.getProperty("db.username"),
                PROPS.getProperty("db.password")
        );
    }
}

