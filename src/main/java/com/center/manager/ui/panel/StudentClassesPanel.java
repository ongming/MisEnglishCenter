package com.center.manager.ui.panel;

import com.center.manager.service.ClassService;
import com.center.manager.service.ServiceFactory;
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
        initComponents();
        loadClasses();
    }

    private void initComponents() {
        JLabel lblTitle = new JLabel("Lớp học của tôi");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        add(lblTitle, BorderLayout.NORTH);

        classModel = new DefaultTableModel(
                new String[]{"ID", "Tên lớp", "Khóa học", "Bắt đầu", "Kết thúc", "TT lớp", "TT ghi danh"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tableClasses = new JTable(classModel);
        tableClasses.setRowHeight(28);
        // Ẩn cột ID
        tableClasses.getColumnModel().getColumn(0).setMinWidth(0);
        tableClasses.getColumnModel().getColumn(0).setMaxWidth(0);
        tableClasses.getColumnModel().getColumn(0).setPreferredWidth(0);

        add(new JScrollPane(tableClasses), BorderLayout.CENTER);
    }

    private void loadClasses() {
        classModel.setRowCount(0);
        Long studentId = UserSession.getInstance().getStudentId();
        if (studentId == null) return;

        try {
            List<Object[]> classes = classService.getClassesByStudent(studentId);
            for (Object[] row : classes) {
                classModel.addRow(row);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}

