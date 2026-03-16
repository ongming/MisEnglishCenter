package com.center.manager.ui.panel;

import com.center.manager.service.ClassService;
import com.center.manager.service.ServiceFactory;
import com.center.manager.ui.UITheme;
import com.center.manager.util.UserSession;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Tab "Lớp học" của Sinh viên — hiển thị các lớp đang theo học.
 */
public class StudentClassesPanel extends JPanel {

    private JTable tableClasses;
    private DefaultTableModel classModel;

    private final ClassService classService = ServiceFactory.classService();

    public StudentClassesPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        UITheme.styleRootPanel(this);
        initComponents();
        loadClasses();
    }

    private void initComponents() {
        JLabel lblTitle = new JLabel("Lớp học của tôi");
        UITheme.styleSectionTitle(lblTitle);
        add(lblTitle, BorderLayout.NORTH);

        classModel = new DefaultTableModel(
                new String[]{"ID", "Tên lớp", "Khóa học", "Bắt đầu", "Kết thúc", "Phòng", "TT lớp", "TT ghi danh"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tableClasses = new JTable(classModel);
        UITheme.styleTable(tableClasses);
        // Ẩn cột ID
        tableClasses.getColumnModel().getColumn(0).setMinWidth(0);
        tableClasses.getColumnModel().getColumn(0).setMaxWidth(0);
        tableClasses.getColumnModel().getColumn(0).setPreferredWidth(0);

        add(new JScrollPane(tableClasses), BorderLayout.CENTER);
    }

    public void refreshData() {
        loadClasses();
    }

    private void loadClasses() {
        classModel.setRowCount(0);
        Long studentId = UserSession.getInstance().getStudentId();
        if (studentId == null) return;

        try {
            List<Object[]> classes = classService.getClassesByStudent(studentId);
            for (Object[] row : classes) {
                // row = [id, name, course, start, end, status, enrollStatus, ...]
                // Thêm phòng học vào row (giả sử row[5] là roomName)
                Object[] newRow = new Object[row.length + 1];
                System.arraycopy(row, 0, newRow, 0, 5);
                newRow[5] = row[5]; // roomName
                System.arraycopy(row, 5, newRow, 6, row.length - 5);
                classModel.addRow(newRow);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Không tải được danh sách lớp: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
