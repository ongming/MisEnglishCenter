package com.center.manager.ui;

import com.center.manager.service.PersonService;
import com.center.manager.service.ServiceFactory;
import com.center.manager.ui.panel.*;
import com.center.manager.util.UserSession;

import javax.swing.*;
import java.awt.*;

/**
 * Dashboard Sinh viên.
 */
public class StudentDashboardFrame extends JFrame {

    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JButton btnProfile, btnClasses, btnSchedule, btnAttendance, btnLogout;
    private JButton currentActiveBtn;

    private final PersonService personService = ServiceFactory.personService();

    public StudentDashboardFrame() {
        setTitle("MIS English Center - Học viên");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // ========== SIDEBAR ==========
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(new Color(44, 62, 80));
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
        lblName.setFont(new Font("Arial", Font.BOLD, 16));
        lblName.setForeground(Color.WHITE);
        lblName.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(lblName);

        JLabel lblRole = new JLabel("Học viên");
        lblRole.setFont(new Font("Arial", Font.PLAIN, 12));
        lblRole.setForeground(new Color(149, 165, 166));
        lblRole.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(lblRole);

        sidebar.add(Box.createVerticalStrut(25));

        // Nút sidebar
        btnProfile = createSidebarButton("Thông tin cá nhân");
        btnClasses = createSidebarButton("Lớp học");
        btnSchedule = createSidebarButton("Lịch học");
        btnAttendance = createSidebarButton("Điểm danh");

        sidebar.add(btnProfile);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnClasses);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnSchedule);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnAttendance);

        sidebar.add(Box.createVerticalGlue());

        btnLogout = createSidebarButton("Đăng xuất");
        btnLogout.setForeground(new Color(231, 76, 60));
        sidebar.add(btnLogout);

        add(sidebar, BorderLayout.WEST);

        // ========== CONTENT AREA ==========
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        contentPanel.add(new StudentProfilePanel(), "profile");
        contentPanel.add(new StudentClassesPanel(), "classes");
        contentPanel.add(new StudentSchedulePanel(), "schedule");
        contentPanel.add(new StudentAttendancePanel(), "attendance");

        add(contentPanel, BorderLayout.CENTER);

        // ========== SỰ KIỆN ==========
        btnProfile.addActionListener(e -> { cardLayout.show(contentPanel, "profile"); setActiveButton(btnProfile); });
        btnClasses.addActionListener(e -> { cardLayout.show(contentPanel, "classes"); setActiveButton(btnClasses); });
        btnSchedule.addActionListener(e -> { cardLayout.show(contentPanel, "schedule"); setActiveButton(btnSchedule); });
        btnAttendance.addActionListener(e -> { cardLayout.show(contentPanel, "attendance"); setActiveButton(btnAttendance); });
        btnLogout.addActionListener(e -> handleLogout());

        // Mặc định hiện tab "Thông tin cá nhân"
        setActiveButton(btnProfile);
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(44, 62, 80));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setPreferredSize(new Dimension(200, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        return btn;
    }

    private void setActiveButton(JButton btn) {
        if (currentActiveBtn != null) {
            currentActiveBtn.setBackground(new Color(44, 62, 80));
        }
        btn.setBackground(new Color(52, 73, 94));
        currentActiveBtn = btn;
    }

    private void handleLogout() {
        UserSession.getInstance().clear();
        dispose();
        new LoginFrame().setVisible(true);
    }
}

