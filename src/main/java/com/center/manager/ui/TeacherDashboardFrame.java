package com.center.manager.ui;

import com.center.manager.service.PersonService;
import com.center.manager.service.ServiceFactory;
import com.center.manager.ui.panel.TeacherClassesPanel;
import com.center.manager.ui.panel.TeacherSchedulePanel;
import com.center.manager.ui.panel.TeacherAttendancePanel;
import com.center.manager.util.UserSession;

import javax.swing.*;
import java.awt.*;

/**
 * Giao diện chính (dashboard) cho giáo viên.
 * Sidebar chuyển tab, content hiển thị các panel chức năng:
 * - Xem danh sách lớp đang dạy
 * - Xem lịch dạy
 * - Quản lý điểm danh
 */
public class TeacherDashboardFrame extends JFrame {
    // Panel chứa nội dung các tab
    private JPanel contentPanel;
    // Layout chuyển tab
    private CardLayout cardLayout;
    // Các nút sidebar
    private JButton btnMyClasses, btnSchedule, btnAttendance, btnLogout;
    // Nút đang active
    private JButton currentActiveBtn;
    // Service lấy thông tin giáo viên
    private final PersonService personService = ServiceFactory.personService();

    public TeacherDashboardFrame() {
        setTitle("MIS English Center - Giảng viên");
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

        // Tên giảng viên
        String displayName = UserSession.getInstance().getUsername();
        Long teacherId = UserSession.getInstance().getTeacherId();
        if (teacherId != null) {
            try {
                String realName = personService.getTeacherName(teacherId);
                if (realName != null) displayName = realName;
            } catch (Exception ignored) {}
        }

        JLabel lblName = new JLabel(displayName);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblName.setForeground(Color.WHITE);
        lblName.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(lblName);

        JLabel lblRole = new JLabel("Giảng viên");
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRole.setForeground(UITheme.TEXT_MUTED);
        lblRole.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(lblRole);

        sidebar.add(Box.createVerticalStrut(25));

        // Nút sidebar
        btnMyClasses = createSidebarButton("Lớp đang dạy");
        btnSchedule = createSidebarButton("Lịch dạy");
        btnAttendance = createSidebarButton("Điểm danh");

        sidebar.add(btnMyClasses);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnSchedule);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnAttendance);

        sidebar.add(Box.createVerticalGlue());

        btnLogout = createSidebarButton("Đăng xuất");
        btnLogout.setForeground(UITheme.DANGER);
        sidebar.add(btnLogout);

        add(sidebar, BorderLayout.WEST);

        // ========== CONTENT AREA ==========
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        UITheme.styleRootPanel(contentPanel);

        TeacherClassesPanel classesPanel = new TeacherClassesPanel();
        TeacherSchedulePanel schedulePanel = new TeacherSchedulePanel();
        TeacherAttendancePanel attendancePanel = new TeacherAttendancePanel();

        contentPanel.add(classesPanel, "classes");
        contentPanel.add(schedulePanel, "schedule");
        contentPanel.add(attendancePanel, "attendance");

        add(contentPanel, BorderLayout.CENTER);

        // ========== SỰ KIỆN ==========
        bindNavigation(btnMyClasses, "classes", classesPanel::refreshData);
        bindNavigation(btnSchedule, "schedule", schedulePanel::refreshData);
        bindNavigation(btnAttendance, "attendance", attendancePanel::refreshData);
        btnLogout.addActionListener(e -> handleLogout());

        // Mặc định hiện tab "Lớp đang dạy"
        setActiveButton(btnMyClasses);
    }

    /**
     * Đăng ký 1 lambda xử lý điều hướng tab + refresh dữ liệu + active button.
     */
    private void bindNavigation(JButton button, String cardName, Runnable refresher) {
        button.addActionListener(e -> {
            cardLayout.show(contentPanel, cardName);
            refresher.run();
            setActiveButton(button);
        });
    }

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

    private void setActiveButton(JButton btn) {
        if (currentActiveBtn != null) {
            UITheme.setSidebarButtonActive(currentActiveBtn, false);
        }
        UITheme.setSidebarButtonActive(btn, true);
        currentActiveBtn = btn;
    }

    private void handleLogout() {
        UserSession.getInstance().clear();
        dispose();
        new LoginFrame().setVisible(true);
    }
}
