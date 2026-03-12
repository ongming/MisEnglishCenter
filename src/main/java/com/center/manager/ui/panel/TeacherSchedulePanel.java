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
 * Tab "Lịch dạy" — hiển thị tất cả buổi học của GV.
 */
public class TeacherSchedulePanel extends JPanel {

    private JTable tableSchedule;
    private DefaultTableModel scheduleModel;

    private final ClassService classService = ServiceFactory.classService();

    public TeacherSchedulePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        UITheme.styleRootPanel(this);
        initComponents();
        loadSchedule();
    }

    private void initComponents() {
        JLabel lblTitle = new JLabel("Lịch dạy");
        UITheme.styleSectionTitle(lblTitle);
        add(lblTitle, BorderLayout.NORTH);

        scheduleModel = new DefaultTableModel(
                new String[]{"ID", "Lớp", "Ngày học", "Bắt đầu", "Kết thúc"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tableSchedule = new JTable(scheduleModel);
        UITheme.styleTable(tableSchedule);
        tableSchedule.setRowHeight(28);
        // Ẩn cột ID
        tableSchedule.getColumnModel().getColumn(0).setMinWidth(0);
        tableSchedule.getColumnModel().getColumn(0).setMaxWidth(0);
        tableSchedule.getColumnModel().getColumn(0).setPreferredWidth(0);

        add(new JScrollPane(tableSchedule), BorderLayout.CENTER);
    }

    public void refreshData() {
        loadSchedule();
    }

    private void loadSchedule() {
        scheduleModel.setRowCount(0);
        Long teacherId = UserSession.getInstance().getTeacherId();
        if (teacherId == null) return;

        try {
            List<Object[]> schedules = classService.getScheduleByTeacher(teacherId);
            for (Object[] row : schedules) {
                scheduleModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Không tải được lịch dạy: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
