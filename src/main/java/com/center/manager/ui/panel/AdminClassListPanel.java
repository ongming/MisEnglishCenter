package com.center.manager.ui.panel;

import com.center.manager.service.AdminService;
import com.center.manager.service.ClassService;
import com.center.manager.service.ServiceFactory;
import com.center.manager.ui.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Tab danh sách lớp + danh sách học viên trong lớp + tạo tài khoản HV.
 */
public class AdminClassListPanel extends JPanel {

    private final ClassService classService = ServiceFactory.classService();
    private final AdminService adminService = ServiceFactory.adminService();

    private final DefaultTableModel classModel;
    private final DefaultTableModel studentModel;
    private final JLabel lblStudentTitle;
    private final List<Long> classIds = new ArrayList<>();
    private Long selectedClassId;
    private String selectedClassName;

    public AdminClassListPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        UITheme.styleRootPanel(this);

        // ===== Top: title + buttons =====
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("Danh sách lớp học");
        UITheme.styleSectionTitle(lblTitle);
        topPanel.add(lblTitle, BorderLayout.WEST);

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnBar.setOpaque(false);

        JButton btnCreateStudentAcc = new JButton("Tạo tài khoản học viên");
        UITheme.stylePrimaryButton(btnCreateStudentAcc);
        btnCreateStudentAcc.addActionListener(e -> showCreateStudentAccountDialog());

        JButton btnAddStudentToClass = new JButton("Thêm HV vào lớp");
        UITheme.stylePrimaryButton(btnAddStudentToClass);
        btnAddStudentToClass.addActionListener(e -> showAddStudentToClassDialog());

        JButton btnRemoveStudent = new JButton("Xóa HV khỏi lớp");
        UITheme.stylePrimaryButton(btnRemoveStudent);

        JButton btnRefresh = new JButton("Làm mới");
        UITheme.stylePrimaryButton(btnRefresh);
        btnRefresh.addActionListener(e -> loadClasses());

        JButton btnCreateClass = new JButton("Tạo lớp học");
        UITheme.stylePrimaryButton(btnCreateClass);
        btnCreateClass.addActionListener(e -> showCreateClassDialog());

        btnBar.add(btnCreateStudentAcc);
        btnBar.add(btnAddStudentToClass);
        btnBar.add(btnRemoveStudent);
        btnBar.add(btnRefresh);
        btnBar.add(btnCreateClass);
        topPanel.add(btnBar, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // ===== Split: class table (top) + student table (bottom) =====
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.45);
        splitPane.setDividerSize(6);
        splitPane.setBorder(null);
        splitPane.setBackground(UITheme.APP_BG);

        // --- Class table ---
        classModel = new DefaultTableModel(
                new String[]{"ID", "Tên lớp", "Khóa học", "Ngày bắt đầu", "Ngày kết thúc", "Sĩ số tối đa", "Trạng thái", "Phòng"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable classTable = new JTable(classModel);
        UITheme.styleTable(classTable);
        classTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = classTable.getSelectedRow();
                if (row >= 0 && row < classIds.size()) {
                    loadStudentsInClass(classIds.get(row), (String) classModel.getValueAt(row, 1));
                }
            }
        });
        splitPane.setTopComponent(new JScrollPane(classTable));

        // --- Student table ---
        JPanel studentPanel = new JPanel(new BorderLayout(5, 5));
        studentPanel.setOpaque(false);

        lblStudentTitle = new JLabel("Chọn một lớp để xem danh sách học viên");
        UITheme.styleSectionTitle(lblStudentTitle);
        studentPanel.add(lblStudentTitle, BorderLayout.NORTH);

        studentModel = new DefaultTableModel(
                new String[]{"ID", "Họ tên", "SĐT", "Email", "Ngày đăng ký", "Trạng thái", "Kết quả"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable studentTable = new JTable(studentModel);
        UITheme.styleTable(studentTable);
        studentPanel.add(new JScrollPane(studentTable), BorderLayout.CENTER);

        btnRemoveStudent.addActionListener(e -> handleRemoveStudentFromClass(studentTable));

        splitPane.setBottomComponent(studentPanel);
        add(splitPane, BorderLayout.CENTER);

        loadClasses();
    }

    public void refreshData() {
        loadClasses();
        studentModel.setRowCount(0);
        selectedClassId = null;
        selectedClassName = null;
        lblStudentTitle.setText("Chọn một lớp để xem danh sách học viên");
    }

    private void loadClasses() {
        classModel.setRowCount(0);
        classIds.clear();
        try {
            List<Object[]> rows = classService.getAllClasses();
            for (Object[] r : rows) {
                // r = [id, name, course, start, end, max, status, roomName]
                classIds.add(((Number) r[0]).longValue());
                classModel.addRow(r);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Không tải được danh sách lớp: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadStudentsInClass(Long classId, String className) {
        studentModel.setRowCount(0);
        selectedClassId = classId;
        selectedClassName = className;
        lblStudentTitle.setText("Học viên lớp: " + className);
        try {
            List<Object[]> rows = classService.getStudentsInClass(classId);
            for (Object[] r : rows) {
                studentModel.addRow(r);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Không tải được danh sách học viên: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddStudentToClassDialog() {
        if (selectedClassId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp học trước.");
            return;
        }

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Thêm học viên vào lớp", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridBagLayout());
        UITheme.styleCard(form);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<String> cboStudent = new JComboBox<>();
        List<Long> studentIds = new ArrayList<>();

        try {
            List<Object[]> rows = adminService.getStudentsNotInClass(selectedClassId);
            rows.forEach(r -> {
                studentIds.add(((Number) r[0]).longValue());
                cboStudent.addItem(r[1] + " - " + r[2] + " - " + r[3]);
            });
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không tải được danh sách học viên: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (studentIds.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không còn học viên phù hợp để thêm vào lớp này.");
            return;
        }

        int row = 0;
        row = addFormRow(form, gbc, row, "Lớp:", new JLabel(selectedClassName == null ? String.valueOf(selectedClassId) : selectedClassName));
        row = addFormRow(form, gbc, row, "Học viên:", cboStudent);

        JButton btnAdd = new JButton("Thêm vào lớp");
        UITheme.stylePrimaryButton(btnAdd);
        btnAdd.addActionListener(e -> {
            int idx = cboStudent.getSelectedIndex();
            if (idx < 0) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng chọn học viên.");
                return;
            }
            try {
                adminService.addStudentToClass(selectedClassId, studentIds.get(idx));
                JOptionPane.showMessageDialog(dialog, "Thêm học viên vào lớp thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadStudentsInClass(selectedClassId, selectedClassName);
                loadClasses();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Không thể thêm học viên: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 1;
        gbc.gridy = row;
        form.add(btnAdd, gbc);

        dialog.setContentPane(form);
        dialog.setVisible(true);
    }

    private void handleRemoveStudentFromClass(JTable studentTable) {
        if (selectedClassId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp học trước.");
            return;
        }

        int row = studentTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn học viên cần xóa khỏi lớp.");
            return;
        }

        Long studentId = ((Number) studentModel.getValueAt(row, 0)).longValue();
        String studentName = String.valueOf(studentModel.getValueAt(row, 1));
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Xóa học viên '" + studentName + "' khỏi lớp '" + selectedClassName + "'?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            adminService.removeStudentFromClass(selectedClassId, studentId);
            JOptionPane.showMessageDialog(this, "Đã xóa học viên khỏi lớp.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadStudentsInClass(selectedClassId, selectedClassName);
            loadClasses();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không thể xóa học viên: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===== Dialog tạo tài khoản học viên =====

    private void showCreateStudentAccountDialog() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Tạo tài khoản học viên", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(460, 280);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridBagLayout());
        UITheme.styleCard(form);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<String> cboStudent = new JComboBox<>();
        JTextField txtUsername = new JTextField(22);
        JPasswordField txtPassword = new JPasswordField(22);
        List<Long> studentIds = new ArrayList<>();

        try {
            List<Object[]> rows = adminService.getStudentsWithoutAccount();
            for (Object[] r : rows) {
                studentIds.add((Long) r[0]);
                cboStudent.addItem((String) r[1]);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không tải được DS HV: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int row = 0;
        row = addFormRow(form, gbc, row, "Học viên:", cboStudent);
        row = addFormRow(form, gbc, row, "Username:", txtUsername);
        row = addFormRow(form, gbc, row, "Password:", txtPassword);

        JButton btnCreate = new JButton("Tạo tài khoản");
        UITheme.stylePrimaryButton(btnCreate);
        btnCreate.addActionListener(e -> {
            int idx = cboStudent.getSelectedIndex();
            if (idx < 0) {
                JOptionPane.showMessageDialog(dialog, "Không còn HV nào cần tạo tài khoản.");
                return;
            }
            try {
                adminService.createStudentAccount(studentIds.get(idx), txtUsername.getText(), new String(txtPassword.getPassword()));
                JOptionPane.showMessageDialog(dialog, "Tạo tài khoản HV thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Không tạo được: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 1;
        gbc.gridy = row;
        form.add(btnCreate, gbc);

        dialog.setContentPane(form);
        dialog.setVisible(true);
    }

    // ===== Dialog tạo lớp học =====
    private void showCreateClassDialog() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Tạo lớp học mới", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(500, 420);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridBagLayout());
        UITheme.styleCard(form);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtClassName = new JTextField(22);
        JComboBox<String> cboCourse = new JComboBox<>();
        JComboBox<String> cboTeacher = new JComboBox<>();
        JTextField txtStartDate = new JTextField(22); // yyyy-MM-dd
        JTextField txtEndDate = new JTextField(22);   // yyyy-MM-dd
        JTextField txtMaxStudent = new JTextField(22);
        JComboBox<String> cboRoom = new JComboBox<>();
        List<Long> courseIds = new ArrayList<>();
        List<Long> teacherIds = new ArrayList<>();
        List<Long> roomIds = new ArrayList<>();

        // Load danh sách khóa học
        try {
            List<Object[]> courses = ServiceFactory.courseService().getAllCourses();
            // Giu cung thu tu giua courseIds va cboCourse de lay dung ID theo selectedIndex.
            courses.forEach(c -> {
                courseIds.add(((Number) c[0]).longValue());
                cboCourse.addItem((String) c[1]);
            });
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không tải được DS khóa học: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Load danh sách giáo viên
        try {
            List<Object[]> teachers = adminService.getAllTeachers();
            cboTeacher.addItem("(Không chọn)");
            teacherIds.add(null);
            teachers.forEach(t -> {
                teacherIds.add(((Number) t[0]).longValue());
                cboTeacher.addItem((String) t[1]);
            });
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không tải được DS giáo viên: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Load danh sách phòng học
        try {
            List<com.center.manager.model.Room> rooms = ServiceFactory.roomService().getAllRooms();
            rooms.forEach(r -> {
                roomIds.add(r.getRoomId());
                cboRoom.addItem(r.getRoomName() + " (" + r.getLocation() + ")");
            });
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không tải được DS phòng học: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int row = 0;
        row = addFormRow(form, gbc, row, "Tên lớp:", txtClassName);
        row = addFormRow(form, gbc, row, "Khóa học:", cboCourse);
        row = addFormRow(form, gbc, row, "Giáo viên:", cboTeacher);
        row = addFormRow(form, gbc, row, "Ngày bắt đầu (yyyy-MM-dd):", txtStartDate);
        row = addFormRow(form, gbc, row, "Ngày kết thúc (yyyy-MM-dd):", txtEndDate);
        row = addFormRow(form, gbc, row, "Sĩ số tối đa:", txtMaxStudent);
        row = addFormRow(form, gbc, row, "Phòng học:", cboRoom);

        JButton btnCreate = new JButton("Tạo lớp học");
        UITheme.stylePrimaryButton(btnCreate);
        btnCreate.addActionListener(e -> {
            try {
                String className = txtClassName.getText();
                int courseIdx = cboCourse.getSelectedIndex();
                int teacherIdx = cboTeacher.getSelectedIndex();
                int roomIdx = cboRoom.getSelectedIndex();
                Long courseId = courseIdx >= 0 ? courseIds.get(courseIdx) : null;
                Long teacherId = teacherIdx > 0 ? teacherIds.get(teacherIdx) : null;
                String startDate = txtStartDate.getText();
                String endDate = txtEndDate.getText();
                Integer maxStudent = Integer.parseInt(txtMaxStudent.getText());
                Long roomId = roomIdx >= 0 ? roomIds.get(roomIdx) : null;
                adminService.createClass(className, courseId, teacherId, startDate, endDate, maxStudent, roomId);
                JOptionPane.showMessageDialog(dialog, "Tạo lớp học thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadClasses();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Không tạo được lớp: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        gbc.gridx = 1;
        gbc.gridy = row;
        form.add(btnCreate, gbc);
        dialog.setContentPane(form);
        dialog.setVisible(true);
    }

    private int addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent comp) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(comp, gbc);
        return row + 1;
    }
}
