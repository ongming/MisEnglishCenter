package com.center.manager.db;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Lớp tiện ích quản lý EntityManagerFactory cho JPA/Hibernate.
 * Dùng để tạo EntityManager phục vụ truy vấn database.
 * - Singleton EntityManagerFactory (tạo 1 lần, dùng suốt app)
 * - em(): tạo mới EntityManager cho mỗi lần thao tác
 * - shutdown(): đóng EntityManagerFactory khi kết thúc app
 *
 * Ví dụ sử dụng:
 * EntityManager em = Jpa.em();
 * // ... thao tác với em ...
 * em.close();
 * Jpa.shutdown();
 */
public final class Jpa {
    // EntityManagerFactory duy nhất cho toàn app
    private static final EntityManagerFactory EMF =
            Persistence.createEntityManagerFactory("MisEnglishCenterPU");

    private Jpa() {}

    /**
     * Tạo mới EntityManager để thao tác với database
     */
    public static EntityManager em() {
        return EMF.createEntityManager();
    }

    /**
     * Đóng EntityManagerFactory khi kết thúc app
     */
    public static void shutdown() {
        EMF.close();
    }
}
