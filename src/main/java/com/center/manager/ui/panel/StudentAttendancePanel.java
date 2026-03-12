package com.center.manager.ui.panel;

import com.center.manager.service.AttendanceService;
import com.center.manager.service.ServiceFactory;
import com.center.manager.ui.UITheme;
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

    private final AttendanceService attendanceService = ServiceFactory.attendanceService();

    public StudentAttendancePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        UITheme.styleRootPanel(this);
        initComponents();
        loadAttendance();
    }

    private void initComponents() {
        JLabel lblTitle = new JLabel("Lịch sử điểm danh");
        UITheme.styleSectionTitle(lblTitle);
        add(lblTitle, BorderLayout.NORTH);

        attModel = new DefaultTableModel(
                new String[]{"Lớp", "Ngày", "Trạng thái", "Ghi chú"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tableAttendance = new JTable(attModel);
        UITheme.styleTable(tableAttendance);

        add(new JScrollPane(tableAttendance), BorderLayout.CENTER);
    }

    private void loadAttendance() {
        attModel.setRowCount(0);
        Long studentId = UserSession.getInstance().getStudentId();
        if (studentId == null) return;

        try {
            List<Object[]> attendances = attendanceService.getByStudent(studentId);
            for (Object[] row : attendances) {
                attModel.addRow(row);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}
