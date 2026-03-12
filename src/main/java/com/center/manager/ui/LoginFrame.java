package com.center.manager.ui;

import com.center.manager.service.AuthService;
import com.center.manager.service.ServiceFactory;
import com.center.manager.util.UserSession;

import javax.swing.*;
import java.awt.*;

/**
 * Màn hình đăng nhập.
 */
public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblError;

    private final AuthService authService = ServiceFactory.authService();

    public LoginFrame() {
        setTitle("MIS English Center - Đăng nhập");
        setSize(420, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        // Nền tổng thể
        JPanel page = new JPanel(new GridBagLayout());
        UITheme.styleRootPanel(page);
        setContentPane(page);

        // Card đăng nhập
        JPanel mainPanel = new JPanel(new GridBagLayout());
        UITheme.styleCard(mainPanel);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(9, 8, 9, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Tiêu đề
        JLabel lblTitle = new JLabel("MIS English Center", SwingConstants.CENTER);
        UITheme.styleTitle(lblTitle);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(lblTitle, gbc);

        // Label + TextField Username
        gbc.gridwidth = 1; gbc.gridy = 1; gbc.gridx = 0;
        mainPanel.add(new JLabel("Tên đăng nhập:"), gbc);
        txtUsername = new JTextField(18);
        gbc.gridx = 1;
        mainPanel.add(txtUsername, gbc);

        // Label + PasswordField
        gbc.gridy = 2; gbc.gridx = 0;
        mainPanel.add(new JLabel("Mật khẩu:"), gbc);
        txtPassword = new JPasswordField(18);
        gbc.gridx = 1;
        mainPanel.add(txtPassword, gbc);

        // Error label
        lblError = new JLabel(" ");
        lblError.setForeground(UITheme.DANGER);
        lblError.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        mainPanel.add(lblError, gbc);

        // Nút đăng nhập
        btnLogin = new JButton("ĐĂNG NHẬP");
        UITheme.stylePrimaryButton(btnLogin);
        gbc.gridy = 4;
        mainPanel.add(btnLogin, gbc);

        // Label hướng dẫn liên hệ nếu chưa có tài khoản
        JLabel lblContact = new JLabel("Bạn chưa có tài khoản? Hãy liên hệ với Giảng viên hoặc Admin", SwingConstants.CENTER);
        lblContact.setFont(new Font("Segoe UI", Font.ITALIC, 11)); // Font chữ nhỏ hơn và in nghiêng cho tinh tế
        lblContact.setForeground(Color.GRAY); // Màu xám để không làm xao nhãng nút chính

        gbc.gridy = 5; // Hàng tiếp theo dưới nút Đăng nhập
        gbc.gridx = 0;
        gbc.gridwidth = 2; // Trải dài qua cả 2 cột (label và input)
        gbc.insets = new Insets(7, 8, 5, 8); // Tăng khoảng cách phía trên (15) để tách rời khỏi nút
        mainPanel.add(lblContact, gbc);

        page.add(mainPanel);

        // === Sự kiện ===
        btnLogin.addActionListener(e -> handleLogin());

        // Enter trong ô mật khẩu → đăng nhập
        txtPassword.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Vui lòng nhập tên đăng nhập và mật khẩu.");
            return;
        }

        lblError.setText(" ");
        btnLogin.setEnabled(false);
        btnLogin.setText("Đang đăng nhập...");

        // Chạy trên thread riêng để không đơ giao diện
        new Thread(() -> {
            try {
                Object[] result = authService.login(username, password);

                SwingUtilities.invokeLater(() -> {
                    btnLogin.setEnabled(true);
                    btnLogin.setText("ĐĂNG NHẬP");

                    if (result != null) {
                        // Lưu session
                        UserSession.getInstance().set(
                                (Long) result[0],
                                (String) result[1],
                                (String) result[2],
                                (Long) result[3],
                                (Long) result[4],
                                (Long) result[5]
                        );

                        String role = (String) result[2];
                        navigateToDashboard(role);
                    } else {
                        lblError.setText("Sai tên đăng nhập hoặc mật khẩu.");
                    }
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    btnLogin.setEnabled(true);
                    btnLogin.setText("ĐĂNG NHẬP");
                    lblError.setText("Lỗi hệ thống: " + ex.getMessage());
                });
            }
        }).start();
    }

    private void navigateToDashboard(String role) {
        dispose(); // Đóng cửa sổ Login

        switch (role) {
            case "Admin":
                new AdminDashboardFrame().setVisible(true);
                break;
            case "Teacher":
                new TeacherDashboardFrame().setVisible(true);
                break;
            case "Student":
                new StudentDashboardFrame().setVisible(true);
                break;
            default:
                JOptionPane.showMessageDialog(null,
                        "Role '" + role + "' chưa có giao diện Dashboard.",
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                // Quay lại Login
                new LoginFrame().setVisible(true);
                break;
        }
    }
}
