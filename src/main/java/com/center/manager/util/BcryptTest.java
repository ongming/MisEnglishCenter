package com.center.manager.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Test nhanh BCrypt verify + tạo hash mới.
 * Chạy: mvn exec:java -Dexec.mainClass=com.center.manager.util.BcryptTest
 */
public class BcryptTest {
    public static void main(String[] args) {
        String rawPassword = "123456";

        // Tạo hash mới
        String newHash = BCrypt.hashpw(rawPassword, BCrypt.gensalt(10));

        System.out.println("===================================");
        System.out.println("Mat khau   : " + rawPassword);
        System.out.println("Hash moi   : " + newHash);
        System.out.println("Verify     : " + BCrypt.checkpw(rawPassword, newHash));
        System.out.println("===================================");
        System.out.println();
        System.out.println("Chay SQL nay trong MySQL:");
        System.out.println("UPDATE user_accounts SET password_hash = '" + newHash + "';");
    }
}


