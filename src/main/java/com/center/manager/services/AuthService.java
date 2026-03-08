package com.center.manager.services;

import com.center.manager.entities.UserAccount;
import com.center.manager.repositories.UserAccountRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service xử lý logic đăng nhập (Authentication).
 *
 * --- Tại sao cần Service? (Nguyên lý Single Responsibility - SOLID) ---
 * Controller chỉ lo việc nhận sự kiện từ giao diện (nhấn nút).
 * Service lo việc xử lý logic (kiểm tra username, so sánh mật khẩu...).
 * Repository lo việc lấy dữ liệu từ DB.
 *
 * Luồng: Controller → Service → Repository → Database
 */
@Service  // Đánh dấu: đây là 1 Service (tầng xử lý logic)
public class AuthService {

    // Repository để truy vấn bảng user_accounts
    private final UserAccountRepository userAccountRepository;

    // Công cụ mã hóa BCrypt — dùng để so sánh mật khẩu
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Constructor Injection (khuyến khích hơn @Autowired trên field).
     * Spring tự động truyền UserAccountRepository vào khi tạo AuthService.
     */
    public AuthService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Xử lý đăng nhập.
     *
     * Bước 1: Tìm tài khoản theo username trong DB.
     * Bước 2: Nếu không tìm thấy → trả về Optional rỗng (đăng nhập thất bại).
     * Bước 3: Nếu tìm thấy → so sánh mật khẩu người dùng nhập với mật khẩu đã mã hóa trong DB.
     * Bước 4: Nếu mật khẩu đúng VÀ tài khoản đang hoạt động → trả về UserAccount.
     *
     * @param username Tên đăng nhập người dùng nhập
     * @param rawPassword Mật khẩu gốc (chưa mã hóa) người dùng nhập
     * @return Optional<UserAccount> — có giá trị nếu đăng nhập thành công, rỗng nếu thất bại
     */
    public Optional<UserAccount> login(String username, String rawPassword) {

        // Bước 1: Tìm tài khoản theo username
        Optional<UserAccount> optionalUser = userAccountRepository.findByUsername(username);

        // Bước 2: Nếu không tìm thấy username → thất bại
        if (optionalUser.isEmpty()) {
            System.out.println("[DEBUG] Không tìm thấy username: " + username);
            return Optional.empty();
        }

        // Bước 3: Lấy tài khoản ra để kiểm tra
        UserAccount user = optionalUser.get();

        // [DEBUG] In ra để kiểm tra dữ liệu từ DB
        System.out.println("[DEBUG] Tìm thấy user: " + user.getUsername());
        System.out.println("[DEBUG] Role: " + user.getRole());
        System.out.println("[DEBUG] isActive: " + user.getIsActive());
        System.out.println("[DEBUG] Hash từ DB: [" + user.getPasswordHash() + "]");
        System.out.println("[DEBUG] Độ dài hash: " + (user.getPasswordHash() != null ? user.getPasswordHash().length() : "null"));

        // Bước 4: Kiểm tra tài khoản có bị khóa không
        if (!user.getIsActive()) {
            System.out.println("[DEBUG] Tài khoản bị khóa!");
            return Optional.empty();
        }

        // Bước 5: So sánh mật khẩu gốc với mật khẩu đã mã hóa trong DB
        // BCrypt.matches("123456", "$2a$10$xxxxx...") → true nếu khớp
        boolean matched = passwordEncoder.matches(rawPassword, user.getPasswordHash());
        System.out.println("[DEBUG] Kết quả so sánh mật khẩu: " + matched);

        if (matched) {
            return Optional.of(user);  // Đăng nhập thành công
        }

        // Mật khẩu sai → thất bại
        return Optional.empty();
    }

    /**
     * Hàm tiện ích: Mã hóa mật khẩu gốc thành BCrypt hash.
     * Dùng khi tạo tài khoản mới hoặc đổi mật khẩu.
     *
     * Ví dụ: encodePassword("123456") → "$2a$10$abc..."
     */
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}


