package com.center.manager.ui.panel;

import com.center.manager.service.AdminService;
import com.center.manager.service.ServiceFactory;
import com.center.manager.ui.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Tab hiển thị danh sách giáo viên.
 */
public class AdminTeacherListPanel extends JPanel {

    private final AdminService adminService = ServiceFactory.adminService();
    private final DefaultTableModel teacherModel;

    public AdminTeacherListPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        UITheme.styleRootPanel(this);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("Danh sách giáo viên");
        UITheme.styleSectionTitle(lblTitle);
        topPanel.add(lblTitle, BorderLayout.WEST);

        JButton btnRefresh = new JButton("Làm mới");
        UITheme.stylePrimaryButton(btnRefresh);
        btnRefresh.addActionListener(e -> loadTeachers());
        topPanel.add(btnRefresh, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        teacherModel = new DefaultTableModel(
                new String[]{"ID", "Họ tên", "SĐT", "Email", "Chuyên môn", "Trạng thái"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        JTable table = new JTable(teacherModel);
        UITheme.styleTable(table);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadTeachers();
    }

    private void loadTeachers() {
        teacherModel.setRowCount(0);
        try {
            List<Object[]> teachers = adminService.getAllTeachers();
            for (Object[] row : teachers) {
                teacherModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Không tải được danh sách giáo viên: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
