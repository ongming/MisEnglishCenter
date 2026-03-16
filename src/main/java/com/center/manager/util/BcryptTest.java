package com.center.manager.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * File test nhanh BCrypt để tạo hash mới và kiểm tra verify hash.
 * Chạy: mvn exec:java -Dexec.mainClass=com.center.manager.util.BcryptTest
 * Dùng để sinh hash lưu vào DB và kiểm tra verify mật khẩu nhập vào.
 */
public class BcryptTest {
    public static void main(String[] args) {
        String rawPassword = "123456";

        // Tạo hash mới từ mật khẩu gốc
        String newHash = BCrypt.hashpw(rawPassword, BCrypt.gensalt(10));

        System.out.println("===================================");
        System.out.println("Mat khau   : " + rawPassword);
        System.out.println("Hash moi   : " + newHash);
        // Kiểm tra verify: true nếu đúng, false nếu sai
        System.out.println("Verify     : " + BCrypt.checkpw(rawPassword, newHash));
        System.out.println("===================================");
        System.out.println();
        System.out.println("Chay SQL nay trong MySQL:");
        // Gợi ý update hash vào DB
        System.out.println("UPDATE user_accounts SET password_hash = '" + newHash + "';");
    }
}
