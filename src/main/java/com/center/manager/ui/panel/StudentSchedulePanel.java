package com.center.manager.ui.panel;

import com.center.manager.service.ClassService;
import com.center.manager.service.ServiceFactory;
import com.center.manager.util.UserSession;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Tab "Lịch học" của Sinh viên.
 */
public class StudentSchedulePanel extends JPanel {

    private JTable tableSchedule;
    private DefaultTableModel scheduleModel;

    private final ClassService classService = ServiceFactory.classService();

    public StudentSchedulePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initComponents();
        loadSchedule();
    }

    private void initComponents() {
        JLabel lblTitle = new JLabel("Lịch học");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        add(lblTitle, BorderLayout.NORTH);

        scheduleModel = new DefaultTableModel(
                new String[]{"ID", "Lớp", "Ngày học", "Bắt đầu", "Kết thúc"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tableSchedule = new JTable(scheduleModel);
        tableSchedule.setRowHeight(28);
        // Ẩn cột ID
        tableSchedule.getColumnModel().getColumn(0).setMinWidth(0);
        tableSchedule.getColumnModel().getColumn(0).setMaxWidth(0);
        tableSchedule.getColumnModel().getColumn(0).setPreferredWidth(0);

        add(new JScrollPane(tableSchedule), BorderLayout.CENTER);
    }

    private void loadSchedule() {
        scheduleModel.setRowCount(0);
        Long studentId = UserSession.getInstance().getStudentId();
        if (studentId == null) return;

        try {
            List<Object[]> schedules = classService.getScheduleByStudent(studentId);
            for (Object[] row : schedules) {
                scheduleModel.addRow(row);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}

