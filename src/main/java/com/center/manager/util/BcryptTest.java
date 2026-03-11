package com.center.manager.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Test nhanh BCrypt verify.
 */
public class BcryptTest {
    public static void main(String[] args) {
        String rawPassword = "123456";
        String hashFromDB = "$2a$10$8K1p/a06VthlJ.m9BQvveuQCByByH.AGz99shSUp9.Y8J.qZunfMC";

        System.out.println("Hash tu DB  : " + hashFromDB);
        System.out.println("Mat khau thu: " + rawPassword);

        boolean matched = BCrypt.checkpw(rawPassword, hashFromDB);
        System.out.println("Ket qua     : " + (matched ? "DUNG - KHOP" : "SAI - KHONG KHOP"));

        // Tao hash moi cho 123456
        String newHash = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        System.out.println();
        System.out.println("=== TAO HASH MOI ===");
        System.out.println("Hash moi    : " + newHash);
        System.out.println("Verify lai  : " + BCrypt.checkpw(rawPassword, newHash));
        System.out.println();
        System.out.println("Chay SQL nay de cap nhat tat ca tai khoan dung mat khau 123456:");
        System.out.println("UPDATE user_accounts SET password_hash = '" + newHash + "';");
    }
}


