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

        JButton btnRefresh = new JButton("Làm mới");
        UITheme.stylePrimaryButton(btnRefresh);
        btnRefresh.addActionListener(e -> loadClasses());

        btnBar.add(btnCreateStudentAcc);
        btnBar.add(btnRefresh);
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
                new String[]{"ID", "Tên lớp", "Khóa học", "Ngày bắt đầu", "Ngày kết thúc", "Sĩ số tối đa", "Trạng thái"}, 0) {
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

        splitPane.setBottomComponent(studentPanel);
        add(splitPane, BorderLayout.CENTER);

        loadClasses();
    }

    public void refreshData() {
        loadClasses();
        studentModel.setRowCount(0);
        lblStudentTitle.setText("Chọn một lớp để xem danh sách học viên");
    }

    private void loadClasses() {
        classModel.setRowCount(0);
        classIds.clear();
        try {
            List<Object[]> rows = classService.getAllClasses();
            for (Object[] r : rows) {
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
