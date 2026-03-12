package com.center.manager.ui.panel;

import com.center.manager.service.AttendanceService;
import com.center.manager.service.ClassService;
import com.center.manager.service.ServiceFactory;
import com.center.manager.util.UserSession;
import com.center.manager.ui.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Tab "Điểm danh" — GV chọn lớp + ngày, điểm danh cho HV.
 */
public class TeacherAttendancePanel extends JPanel {

    private JComboBox<String> cboClass;
    private JTextField txtDate;
    private JTable tableAttendance;
    private DefaultTableModel attModel;
    private JLabel lblTitle;

    private final ClassService classService = ServiceFactory.classService();
    private final AttendanceService attendanceService = ServiceFactory.attendanceService();

    // Lưu classId tương ứng với ComboBox
    private java.util.List<Long> classIds = new java.util.ArrayList<>();

    public TeacherAttendancePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        UITheme.styleRootPanel(this);
        initComponents();
        loadClassComboBox();
    }

    private void initComponents() {
        // ========== Panel trên: chọn lớp + ngày ==========
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        UITheme.styleCard(topPanel);

        topPanel.add(new JLabel("Lớp:"));
        cboClass = new JComboBox<>();
        cboClass.setPreferredSize(new Dimension(200, 30));
        topPanel.add(cboClass);

        topPanel.add(new JLabel("Ngày (yyyy-MM-dd):"));
        txtDate = new JTextField(LocalDate.now().toString(), 12);
        topPanel.add(txtDate);

        JButton btnLoad = new JButton("Tải danh sách");
        UITheme.stylePrimaryButton(btnLoad);
        btnLoad.addActionListener(e -> handleLoadAttendance());
        topPanel.add(btnLoad);

        JButton btnSave = new JButton("Lưu điểm danh");
        UITheme.stylePrimaryButton(btnSave);
        btnSave.addActionListener(e -> handleSaveAttendance());
        topPanel.add(btnSave);

        JButton btnRefresh = new JButton("Làm mới dữ liệu");
        UITheme.stylePrimaryButton(btnRefresh);
        btnRefresh.addActionListener(e -> refreshData());
        topPanel.add(btnRefresh);

        add(topPanel, BorderLayout.NORTH);

        // ========== Bảng điểm danh ==========
        lblTitle = new JLabel("Điểm danh");
        UITheme.styleSectionTitle(lblTitle);

        // Cột: attendanceId (ẩn), studentId (ẩn), Họ tên, Trạng thái (editable combobox), Ghi chú (editable)
        attModel = new DefaultTableModel(
                new String[]{"AttID", "StudentID", "Họ và tên", "Trạng thái", "Ghi chú"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 3 || col == 4; // Chỉ cho sửa Trạng thái và Ghi chú
            }
        };
        tableAttendance = new JTable(attModel);
        UITheme.styleTable(tableAttendance);

        // Ẩn cột AttID, StudentID
        tableAttendance.getColumnModel().getColumn(0).setMinWidth(0);
        tableAttendance.getColumnModel().getColumn(0).setMaxWidth(0);
        tableAttendance.getColumnModel().getColumn(0).setPreferredWidth(0);
        tableAttendance.getColumnModel().getColumn(1).setMinWidth(0);
        tableAttendance.getColumnModel().getColumn(1).setMaxWidth(0);
        tableAttendance.getColumnModel().getColumn(1).setPreferredWidth(0);

        // Cột Trạng thái: ComboBox
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Present", "Absent", "Late"});
        tableAttendance.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(statusCombo));

        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        tablePanel.add(lblTitle, BorderLayout.NORTH);
        tablePanel.add(new JScrollPane(tableAttendance), BorderLayout.CENTER);
        add(tablePanel, BorderLayout.CENTER);
    }

    public void refreshData() {
        loadClassComboBox();
        attModel.setRowCount(0);
        lblTitle.setText("Điểm danh");
    }

    private void loadClassComboBox() {
        cboClass.removeAllItems();
        classIds.clear();

        Long teacherId = UserSession.getInstance().getTeacherId();
        if (teacherId == null) return;

        try {
            List<Object[]> classes = classService.getClassesByTeacher(teacherId);
            for (Object[] row : classes) {
                classIds.add((Long) row[0]);
                cboClass.addItem((String) row[1]); // className
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Không tải được danh sách lớp: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleLoadAttendance() {
        int idx = cboClass.getSelectedIndex();
        if (idx < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp.");
            return;
        }

        String dateStr = txtDate.getText().trim();
        if (dateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập ngày.");
            return;
        }

        try {
            LocalDate.parse(dateStr);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Ngày không hợp lệ. Vui lòng nhập đúng định dạng yyyy-MM-dd.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Long classId = classIds.get(idx);
        String className = (String) cboClass.getSelectedItem();
        lblTitle.setText("Điểm danh — " + className + " — " + dateStr);

        attModel.setRowCount(0);

        try {
            // Thử lấy điểm danh đã có
            List<Object[]> existing = attendanceService.getByClassAndDate(classId, dateStr);

            if (!existing.isEmpty()) {
                for (Object[] row : existing) {
                    attModel.addRow(row);
                }
            } else {
                // Chưa có → tạo danh sách mới từ enrollment
                List<Object[]> students = classService.getStudentsInClass(classId);
                for (Object[] s : students) {
                    attModel.addRow(new Object[]{
                            0L,        // attendanceId = 0 (chưa lưu)
                            s[0],      // studentId
                            s[1],      // fullName
                            "Present", // mặc định
                            ""         // ghi chú
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Không tải được dữ liệu điểm danh: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSaveAttendance() {
        if (attModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Chưa có dữ liệu điểm danh để lưu.");
            return;
        }

        // Dừng editing nếu đang sửa ô
        if (tableAttendance.isEditing()) {
            tableAttendance.getCellEditor().stopCellEditing();
        }

        int idx = cboClass.getSelectedIndex();
        if (idx < 0) return;
        Long classId = classIds.get(idx);
        String dateStr = txtDate.getText().trim();

        try {
            for (int i = 0; i < attModel.getRowCount(); i++) {
                Long attId = toLong(attModel.getValueAt(i, 0));
                Long studentId = toLong(attModel.getValueAt(i, 1));
                String status = (String) attModel.getValueAt(i, 3);
                String note = (String) attModel.getValueAt(i, 4);

                // attId == 0 → tạo mới
                attendanceService.saveAttendance(
                        attId != null && attId > 0 ? attId : null,
                        studentId, classId, dateStr, status, note
                );
            }

            JOptionPane.showMessageDialog(this, "✅ Đã lưu điểm danh thành công!");
            // Reload
            handleLoadAttendance();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Lỗi khi lưu: " + e.getMessage());
        }
    }

    private Long toLong(Object val) {
        if (val == null) return null;
        if (val instanceof Long) return (Long) val;
        if (val instanceof Number) return ((Number) val).longValue();
        try { return Long.parseLong(val.toString()); } catch (Exception e) { return null; }
    }
}

