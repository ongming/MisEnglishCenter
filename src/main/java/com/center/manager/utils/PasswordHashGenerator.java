package com.center.manager.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Công cụ tạo mật khẩu BCrypt hash.
 *
 * --- Cách dùng ---
 * 1. Chạy hàm main() trong class này (nhấn nút Run ▶ trong IntelliJ)
 * 2. Nó sẽ in ra mật khẩu đã hash
 * 3. Copy cái hash đó → paste vào cột password_hash trong bảng user_accounts
 *
 * --- Tại sao cần hash? ---
 * Không bao giờ lưu mật khẩu gốc (plain text) vào DB.
 * Ví dụ: "123456" → "$2a$10$abc..." (không thể giải ngược lại)
 * Khi đăng nhập, BCrypt so sánh mật khẩu người dùng nhập với hash trong DB.
 *
 * --- Ví dụ ---
 * Bạn muốn tạo tài khoản admin với mật khẩu "123456":
 *   - Chạy class này → nó in ra: $2a$10$xxxxx...
 *   - Chạy SQL: UPDATE user_accounts SET password_hash = '$2a$10$xxxxx...' WHERE username = 'admin';
 */
public class PasswordHashGenerator {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // ===================================
        // ★ SỬA MẬT KHẨU Ở ĐÂY ★
        // ===================================
        String rawPassword = "123456";

        // ===================================
        // ★ TEST: Kiểm tra hash từ DB có khớp không ★
        // ===================================
        String hashFromDB = "$2a$10$8K1p/a06VthlJ.m9BQvveuQCByByH.AGz99shSUp9.Y8J.qZunfMC";
        boolean dbMatch = encoder.matches(rawPassword, hashFromDB);
        System.out.println("========================================");
        System.out.println("Hash từ DB     : " + hashFromDB);
        System.out.println("Mật khẩu thử   : " + rawPassword);
        System.out.println("Kết quả khớp   : " + (dbMatch ? "✅ ĐÚNG" : "❌ SAI"));
        System.out.println("========================================");
        System.out.println();

        // Tạo hash mới
        String hashedPassword = encoder.encode(rawPassword);

        System.out.println("Hash mới tạo   : " + hashedPassword);
        System.out.println();
        System.out.println("Copy SQL để cập nhật tất cả tài khoản dùng mật khẩu '123456':");
        System.out.println();
        System.out.println("UPDATE user_accounts SET password_hash = '" + hashedPassword + "';");
        System.out.println();

        // Kiểm tra hash mới
        boolean newMatch = encoder.matches(rawPassword, hashedPassword);
        System.out.println("Kiểm tra hash mới: " + (newMatch ? "✅ ĐÚNG" : "❌ SAI"));
    }
}


