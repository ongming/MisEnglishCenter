package com.center.manager.ui.panel;

import com.center.manager.dao.ClassDAO;
import com.center.manager.util.UserSession;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Tab "Điểm danh" của Sinh viên — xem lịch sử điểm danh.
 */
public class StudentAttendancePanel extends JPanel {

    private JTable tableAttendance;
    private DefaultTableModel attModel;

    private final ClassDAO classDAO = new ClassDAO();

    public StudentAttendancePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initComponents();
        loadAttendance();
    }

    private void initComponents() {
        JLabel lblTitle = new JLabel("Lịch sử điểm danh");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        add(lblTitle, BorderLayout.NORTH);

        attModel = new DefaultTableModel(
                new String[]{"Lớp", "Ngày", "Trạng thái", "Ghi chú"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tableAttendance = new JTable(attModel);
        tableAttendance.setRowHeight(28);

        add(new JScrollPane(tableAttendance), BorderLayout.CENTER);
    }

    private void loadAttendance() {
        attModel.setRowCount(0);
        Long studentId = UserSession.getInstance().getStudentId();
        if (studentId == null) return;

        List<Object[]> attendances = classDAO.getAttendanceByStudent(studentId);
        for (Object[] row : attendances) {
            attModel.addRow(row);
        }
    }
}

