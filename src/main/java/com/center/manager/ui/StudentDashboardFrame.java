package com.center.manager.ui;

import com.center.manager.service.PersonService;
import com.center.manager.service.ServiceFactory;
import com.center.manager.ui.panel.*;
import com.center.manager.util.UserSession;

import javax.swing.*;
import java.awt.*;

/**
 * Giao diện chính (dashboard) cho học viên.
 * Sidebar chuyển tab, content hiển thị các panel chức năng:
 * - Hồ sơ cá nhân
 * - Danh sách lớp đã đăng ký
 * - Lịch học
 * - Điểm danh
 * - Thanh toán
 */
public class StudentDashboardFrame extends JFrame {
    // Panel chứa nội dung các tab
    private JPanel contentPanel;
    // Layout chuyển tab
    private CardLayout cardLayout;
    // Các nút sidebar
    private JButton btnProfile, btnClasses, btnSchedule, btnAttendance, btnPayment, btnLogout;
    // Nút đang active
    private JButton currentActiveBtn;
    // Service lấy thông tin học viên
    private final PersonService personService = ServiceFactory.personService();

    public StudentDashboardFrame() {
        setTitle("MIS English Center - Học viên");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        // Khởi tạo giao diện
        initComponents();
    }

    /**
     * Khởi tạo các thành phần giao diện (sidebar, content, ...)
     */
    private void initComponents() {
        setLayout(new BorderLayout());

        // ========== SIDEBAR ==========
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(220, 0));
        UITheme.styleSidebar(sidebar);
        sidebar.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        // Tên sinh viên
        String displayName = UserSession.getInstance().getUsername();
        Long studentId = UserSession.getInstance().getStudentId();
        if (studentId != null) {
            try {
                String realName = personService.getStudentName(studentId);
                if (realName != null) displayName = realName;
            } catch (Exception ignored) {}
        }

        JLabel lblName = new JLabel(displayName);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblName.setForeground(Color.WHITE);
        lblName.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(lblName);

        JLabel lblRole = new JLabel("Học viên");
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRole.setForeground(UITheme.TEXT_MUTED);
        lblRole.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(lblRole);

        sidebar.add(Box.createVerticalStrut(25));

        // Nút sidebar
        btnProfile = createSidebarButton("Thông tin cá nhân");
        btnClasses = createSidebarButton("Lớp học");
        btnSchedule = createSidebarButton("Lịch học");
        btnAttendance = createSidebarButton("Điểm danh");
        btnPayment = createSidebarButton("Thanh toán");

        sidebar.add(btnProfile);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnClasses);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnSchedule);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnAttendance);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnPayment);

        sidebar.add(Box.createVerticalGlue());

        btnLogout = createSidebarButton("Đăng xuất");
        btnLogout.setForeground(UITheme.DANGER);
        sidebar.add(btnLogout);

        add(sidebar, BorderLayout.WEST);

        // ========== CONTENT AREA ==========
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        UITheme.styleRootPanel(contentPanel);

        StudentClassesPanel classesPanel = new StudentClassesPanel();
        StudentSchedulePanel schedulePanel = new StudentSchedulePanel();
        StudentAttendancePanel attendancePanel = new StudentAttendancePanel();
        StudentPaymentPanel paymentPanel = new StudentPaymentPanel();

        contentPanel.add(new StudentProfilePanel(), "profile");
        contentPanel.add(classesPanel, "classes");
        contentPanel.add(schedulePanel, "schedule");
        contentPanel.add(attendancePanel, "attendance");
        contentPanel.add(paymentPanel, "payment");

        add(contentPanel, BorderLayout.CENTER);

        // ========== SỰ KIỆN ==========
        bindNavigation(btnProfile, "profile", () -> { });
        bindNavigation(btnClasses, "classes", classesPanel::refreshData);
        bindNavigation(btnSchedule, "schedule", schedulePanel::refreshData);
        bindNavigation(btnAttendance, "attendance", attendancePanel::refreshData);
        bindNavigation(btnPayment, "payment", paymentPanel::refreshData);
        btnLogout.addActionListener(e -> handleLogout());

        // Mặc định hiện tab "Thông tin cá nhân"
        setActiveButton(btnProfile);
    }

    /**
     * Tạo nút cho sidebar
     * @param text Text hiển thị trên nút
     * @return JButton đã được cấu hình
     */
    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        UITheme.styleSidebarButton(btn);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setPreferredSize(new Dimension(200, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        return btn;
    }

    /**
     * Đặt nút đang hoạt động (được chọn)
     * @param btn Nút cần đặt làm nút hoạt động
     */
    private void setActiveButton(JButton btn) {
        if (currentActiveBtn != null) {
            UITheme.setSidebarButtonActive(currentActiveBtn, false);
        }
        UITheme.setSidebarButtonActive(btn, true);
        currentActiveBtn = btn;
    }

    /**
     * Gán lambda điều hướng tab và callback refresh nếu tab cần tải lại dữ liệu.
     */
    private void bindNavigation(JButton button, String cardName, Runnable refresher) {
        button.addActionListener(e -> {
            cardLayout.show(contentPanel, cardName);
            refresher.run();
            setActiveButton(button);
        });
    }

    /**
     * Xử lý sự kiện đăng xuất
     */
    private void handleLogout() {
        UserSession.getInstance().clear();
        dispose();
        new LoginFrame().setVisible(true);
    }
}
