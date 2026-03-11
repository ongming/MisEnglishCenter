package com.center.manager.ui.panel;

import com.center.manager.service.ClassService;
import com.center.manager.service.ServiceFactory;
import com.center.manager.util.UserSession;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Tab "Lớp đang dạy" — bảng lớp (trên) + bảng học viên (dưới).
 */
public class TeacherClassesPanel extends JPanel {

    private JTable tableClasses;
    private DefaultTableModel classModel;
    private JTable tableStudents;
    private DefaultTableModel studentModel;
    private JLabel lblStudentTitle;

    private final ClassService classService = ServiceFactory.classService();

    public TeacherClassesPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initComponents();
        loadMyClasses();
    }

    private void initComponents() {
        // ========== Bảng LỚP (trên) ==========
        JLabel lblTitle = new JLabel("Lớp đang dạy");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));

        classModel = new DefaultTableModel(
                new String[]{"ID", "Tên lớp", "Khóa học", "Bắt đầu", "Kết thúc", "Sĩ số max", "Trạng thái"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tableClasses = new JTable(classModel);
        tableClasses.setRowHeight(28);
        tableClasses.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Ẩn cột ID
        tableClasses.getColumnModel().getColumn(0).setMinWidth(0);
        tableClasses.getColumnModel().getColumn(0).setMaxWidth(0);
        tableClasses.getColumnModel().getColumn(0).setPreferredWidth(0);

        JScrollPane scrollClasses = new JScrollPane(tableClasses);
        scrollClasses.setPreferredSize(new Dimension(0, 200));

        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.add(lblTitle, BorderLayout.NORTH);
        topPanel.add(scrollClasses, BorderLayout.CENTER);

        // ========== Bảng HỌC VIÊN (dưới) ==========
        lblStudentTitle = new JLabel("Danh sách học viên — (chọn lớp ở trên)");
        lblStudentTitle.setFont(new Font("Arial", Font.BOLD, 14));

        studentModel = new DefaultTableModel(
                new String[]{"ID", "Họ và tên", "SĐT", "Email", "Ngày ghi danh", "Trạng thái"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tableStudents = new JTable(studentModel);
        tableStudents.setRowHeight(28);
        // Ẩn cột ID
        tableStudents.getColumnModel().getColumn(0).setMinWidth(0);
        tableStudents.getColumnModel().getColumn(0).setMaxWidth(0);
        tableStudents.getColumnModel().getColumn(0).setPreferredWidth(0);

        JScrollPane scrollStudents = new JScrollPane(tableStudents);

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.add(lblStudentTitle, BorderLayout.NORTH);
        bottomPanel.add(scrollStudents, BorderLayout.CENTER);

        // Split: trên - dưới
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, bottomPanel);
        splitPane.setDividerLocation(250);
        splitPane.setResizeWeight(0.4);
        add(splitPane, BorderLayout.CENTER);

        // === Sự kiện chọn lớp ===
        tableClasses.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tableClasses.getSelectedRow();
                if (row >= 0) {
                    Long classId = (Long) classModel.getValueAt(row, 0);
                    String className = (String) classModel.getValueAt(row, 1);
                    loadStudentsInClass(classId, className);
                }
            }
        });
    }

    private void loadMyClasses() {
        classModel.setRowCount(0);
        Long teacherId = UserSession.getInstance().getTeacherId();
        if (teacherId == null) return;

        try {
            List<Object[]> classes = classService.getClassesByTeacher(teacherId);
            for (Object[] row : classes) {
                classModel.addRow(row);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadStudentsInClass(Long classId, String className) {
        studentModel.setRowCount(0);
        lblStudentTitle.setText("Danh sách học viên — Lớp " + className);

        try {
            List<Object[]> students = classService.getStudentsInClass(classId);
            for (Object[] row : students) {
                studentModel.addRow(row);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}

