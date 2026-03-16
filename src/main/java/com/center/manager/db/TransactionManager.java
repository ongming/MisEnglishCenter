package com.center.manager.db;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

/**
 * Quản lý transaction (giao dịch) cho các thao tác JPA/Hibernate.
 * Giúp thực thi một tác vụ (JpaWork) trong một transaction an toàn:
 * - Tự động bắt đầu, commit hoặc rollback transaction.
 * - Đảm bảo đóng EntityManager sau khi xong.
 *
 * Ví dụ sử dụng:
 * TransactionManager tm = new TransactionManager();
 * tm.runInTransaction(em -> { ... });
 */
public class TransactionManager {

    /**
     * Thực thi một tác vụ trong transaction.
     * @param work Lambda hoặc method thao tác với EntityManager
     * @param <T> Kiểu dữ liệu trả về
     * @return Kết quả trả về từ tác vụ
     * @throws Exception nếu có lỗi khi thực thi
     */
    public <T> T runInTransaction(JpaWork<T> work) throws Exception {
        EntityManager em = Jpa.em();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin(); // Bắt đầu transaction
            T result = work.execute(em); // Thực thi tác vụ
            tx.commit(); // Commit nếu thành công
            return result;
        } catch (Exception ex) {
            if (tx.isActive()) tx.rollback(); // Rollback nếu lỗi
            throw ex;
        } finally {
            em.close(); // Đảm bảo đóng EntityManager
        }
    }
}
