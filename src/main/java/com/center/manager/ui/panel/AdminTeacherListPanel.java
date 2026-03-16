package com.center.manager.ui.panel;

import com.center.manager.service.AdminService;
import com.center.manager.service.ServiceFactory;
import com.center.manager.ui.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Tab hiển thị danh sách giáo viên + thêm GV + tạo tài khoản GV.
 */
public class AdminTeacherListPanel extends JPanel {

    private final AdminService adminService = ServiceFactory.adminService();
    private final DefaultTableModel teacherModel;

    public AdminTeacherListPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        UITheme.styleRootPanel(this);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("Danh sách giáo viên");
        UITheme.styleSectionTitle(lblTitle);
        topPanel.add(lblTitle, BorderLayout.WEST);

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnBar.setOpaque(false);

        JButton btnAddTeacher = new JButton("Thêm giáo viên");
        UITheme.stylePrimaryButton(btnAddTeacher);
        btnAddTeacher.addActionListener(e -> showAddTeacherDialog());

        JButton btnCreateAccount = new JButton("Tạo tài khoản GV");
        UITheme.stylePrimaryButton(btnCreateAccount);
        btnCreateAccount.addActionListener(e -> showCreateAccountDialog());

        JButton btnDeleteTeacher = new JButton("Xóa giáo viên");
        UITheme.stylePrimaryButton(btnDeleteTeacher);

        JButton btnRefresh = new JButton("Làm mới");
        UITheme.stylePrimaryButton(btnRefresh);
        btnRefresh.addActionListener(e -> loadTeachers());

        btnBar.add(btnAddTeacher);
        btnBar.add(btnCreateAccount);
        btnBar.add(btnDeleteTeacher);
        btnBar.add(btnRefresh);
        topPanel.add(btnBar, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        teacherModel = new DefaultTableModel(
                new String[]{"ID", "Họ tên", "SĐT", "Email", "Chuyên môn", "Ngày tuyển", "Trạng thái"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        JTable table = new JTable(teacherModel);
        UITheme.styleTable(table);
        add(new JScrollPane(table), BorderLayout.CENTER);

        btnDeleteTeacher.addActionListener(e -> handleDeleteTeacher(table));

        loadTeachers();
    }

    public void refreshData() {
        loadTeachers();
    }

    private void loadTeachers() {
        teacherModel.setRowCount(0);
        try {
            List<Object[]> teachers = adminService.getAllTeachers();
            for (Object[] row : teachers) {
                teacherModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Không tải được danh sách giáo viên: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===== Dialog thêm giáo viên =====

    private void showAddTeacherDialog() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Thêm giáo viên mới", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(480, 360);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridBagLayout());
        UITheme.styleCard(form);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtFullName = new JTextField(22);
        JTextField txtPhone = new JTextField(22);
        JTextField txtEmail = new JTextField(22);
        JTextField txtSpecialty = new JTextField(22);
        JTextField txtHireDate = new JTextField(22);
        JComboBox<String> cboStatus = new JComboBox<>(new String[]{"Active", "Inactive"});

        int row = 0;
        row = addFormRow(form, gbc, row, "Họ tên:", txtFullName);
        row = addFormRow(form, gbc, row, "Số điện thoại:", txtPhone);
        row = addFormRow(form, gbc, row, "Email:", txtEmail);
        row = addFormRow(form, gbc, row, "Chuyên môn:", txtSpecialty);
        row = addFormRow(form, gbc, row, "Ngày tuyển (yyyy-MM-dd):", txtHireDate);
        row = addFormRow(form, gbc, row, "Trạng thái:", cboStatus);

        JButton btnCreate = new JButton("Thêm giáo viên");
        UITheme.stylePrimaryButton(btnCreate);
        btnCreate.addActionListener(e -> {
            try {
                Long id = adminService.createTeacher(
                        txtFullName.getText(), txtPhone.getText(), txtEmail.getText(),
                        txtSpecialty.getText(), txtHireDate.getText(), (String) cboStatus.getSelectedItem());
                JOptionPane.showMessageDialog(dialog,
                        "Đã thêm giáo viên thành công. ID = " + id,
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadTeachers();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Không thêm được giáo viên: " + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 1;
        gbc.gridy = row;
        form.add(btnCreate, gbc);

        dialog.setContentPane(form);
        dialog.setVisible(true);
    }

    // ===== Dialog tạo tài khoản GV =====

    private void showCreateAccountDialog() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Tạo tài khoản giáo viên", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(460, 280);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridBagLayout());
        UITheme.styleCard(form);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<String> cboTeacher = new JComboBox<>();
        JTextField txtUsername = new JTextField(22);
        JPasswordField txtPassword = new JPasswordField(22);
        List<Long> teacherIds = new ArrayList<>();

        try {
            List<Object[]> rows = adminService.getTeachersWithoutAccount();
            for (Object[] r : rows) {
                teacherIds.add((Long) r[0]);
                cboTeacher.addItem((String) r[1]);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không tải được DS GV: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int row = 0;
        row = addFormRow(form, gbc, row, "Giáo viên:", cboTeacher);
        row = addFormRow(form, gbc, row, "Username:", txtUsername);
        row = addFormRow(form, gbc, row, "Password:", txtPassword);

        JButton btnCreate = new JButton("Tạo tài khoản");
        UITheme.stylePrimaryButton(btnCreate);
        btnCreate.addActionListener(e -> {
            int idx = cboTeacher.getSelectedIndex();
            if (idx < 0) {
                JOptionPane.showMessageDialog(dialog, "Không còn GV nào cần tạo tài khoản.");
                return;
            }
            try {
                adminService.createTeacherAccount(teacherIds.get(idx), txtUsername.getText(), new String(txtPassword.getPassword()));
                JOptionPane.showMessageDialog(dialog, "Tạo tài khoản GV thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
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

    private void handleDeleteTeacher(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn giáo viên cần xóa.");
            return;
        }

        Long teacherId = ((Number) teacherModel.getValueAt(row, 0)).longValue();
        String teacherName = String.valueOf(teacherModel.getValueAt(row, 1));
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Xóa giáo viên '" + teacherName + "'?\nHệ thống sẽ gỡ giáo viên khỏi lớp và xóa tài khoản liên quan.",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            adminService.deleteTeacher(teacherId);
            JOptionPane.showMessageDialog(this, "Đã xóa giáo viên thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadTeachers();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không thể xóa giáo viên: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
