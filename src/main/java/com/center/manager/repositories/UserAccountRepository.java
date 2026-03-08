package com.center.manager.repositories;

import com.center.manager.entities.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository cho bảng user_accounts.
 *
 * --- JpaRepository là gì? ---
 * Spring Data JPA tự động tạo sẵn các hàm CRUD cơ bản:
 *   - findAll()      : Lấy tất cả tài khoản
 *   - findById(id)   : Tìm theo ID
 *   - save(entity)   : Lưu (thêm mới hoặc cập nhật)
 *   - delete(entity) : Xóa
 *
 * Bạn KHÔNG cần viết SQL, Spring tự sinh ra câu lệnh dựa trên tên hàm.
 *
 * --- JpaRepository<UserAccount, Long> ---
 *   - UserAccount : Entity mà repository này quản lý
 *   - Long        : Kiểu dữ liệu của Primary Key (user_id là BIGINT → Long)
 */
@Repository  // Đánh dấu: đây là 1 Repository (tầng truy cập dữ liệu)
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    /**
     * Tìm tài khoản theo username.
     *
     * Spring tự sinh câu SQL: SELECT * FROM user_accounts WHERE username = ?
     * Quy tắc: tên hàm "findByUsername" → Spring hiểu là WHERE username = ?
     *
     * Optional là gì?
     *   - Nếu tìm thấy → Optional chứa UserAccount
     *   - Nếu không tìm thấy → Optional rỗng (tránh NullPointerException)
     */
    Optional<UserAccount> findByUsername(String username);
}

