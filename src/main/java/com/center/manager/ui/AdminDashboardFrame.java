package com.center.manager.ui;

import com.center.manager.ui.panel.AdminStudentAccountPanel;
import com.center.manager.ui.panel.AdminTeacherAccountPanel;
import com.center.manager.ui.panel.AdminTeacherCreatePanel;
import com.center.manager.ui.panel.AdminTeacherListPanel;
import com.center.manager.util.UserSession;

import javax.swing.*;
import java.awt.*;

/**
 * Dashboard cho Admin.
 */
public class AdminDashboardFrame extends JFrame {

    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JButton currentActiveBtn;

    public AdminDashboardFrame() {
        setTitle("MIS English Center - Admin");
        setSize(1100, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(260, 0));
        UITheme.styleSidebar(sidebar);
        sidebar.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        JLabel lblName = new JLabel(UserSession.getInstance().getUsername());
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblName.setForeground(Color.WHITE);
        lblName.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(lblName);

        JLabel lblRole = new JLabel("Admin");
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRole.setForeground(UITheme.TEXT_MUTED);
        lblRole.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(lblRole);

        sidebar.add(Box.createVerticalStrut(25));

        JButton btnTeacherList = createSidebarButton("Danh sách giáo viên");
        JButton btnAddTeacher = createSidebarButton("Thêm giáo viên");
        JButton btnCreateTeacherAcc = createSidebarButton("Tạo tài khoản giáo viên");
        JButton btnCreateStudentAcc = createSidebarButton("Tạo tài khoản học viên");

        sidebar.add(btnTeacherList);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnAddTeacher);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnCreateTeacherAcc);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnCreateStudentAcc);

        sidebar.add(Box.createVerticalGlue());

        JButton btnLogout = createSidebarButton("Đăng xuất");
        btnLogout.setForeground(UITheme.DANGER);
        sidebar.add(btnLogout);

        add(sidebar, BorderLayout.WEST);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        UITheme.styleRootPanel(contentPanel);

        contentPanel.add(new AdminTeacherListPanel(), "teacher-list");
        contentPanel.add(new AdminTeacherCreatePanel(), "teacher-create");
        contentPanel.add(new AdminTeacherAccountPanel(), "teacher-account");
        contentPanel.add(new AdminStudentAccountPanel(), "student-account");

        add(contentPanel, BorderLayout.CENTER);

        btnTeacherList.addActionListener(e -> {
            cardLayout.show(contentPanel, "teacher-list");
            setActiveButton(btnTeacherList);
        });
        btnAddTeacher.addActionListener(e -> {
            cardLayout.show(contentPanel, "teacher-create");
            setActiveButton(btnAddTeacher);
        });
        btnCreateTeacherAcc.addActionListener(e -> {
            cardLayout.show(contentPanel, "teacher-account");
            setActiveButton(btnCreateTeacherAcc);
        });
        btnCreateStudentAcc.addActionListener(e -> {
            cardLayout.show(contentPanel, "student-account");
            setActiveButton(btnCreateStudentAcc);
        });

        btnLogout.addActionListener(e -> {
            UserSession.getInstance().clear();
            dispose();
            new LoginFrame().setVisible(true);
        });

        setActiveButton(btnTeacherList);
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        UITheme.styleSidebarButton(btn);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(230, 40));
        btn.setPreferredSize(new Dimension(230, 40));
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
}
