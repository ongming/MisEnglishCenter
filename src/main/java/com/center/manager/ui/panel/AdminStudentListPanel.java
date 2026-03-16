package com.center.manager.ui.panel;

import com.center.manager.service.AdminService;
import com.center.manager.service.ServiceFactory;
import com.center.manager.ui.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Tab hien thi toan bo hoc vien trong trung tam.
 */
public class AdminStudentListPanel extends JPanel {

    private final AdminService adminService = ServiceFactory.adminService();
    private final DefaultTableModel studentModel;
    private final JTable studentTable;

    public AdminStudentListPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        UITheme.styleRootPanel(this);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Danh sach hoc vien trung tam");
        UITheme.styleSectionTitle(lblTitle);
        topPanel.add(lblTitle, BorderLayout.WEST);

        JButton btnRefresh = new JButton("Lam moi");
        UITheme.stylePrimaryButton(btnRefresh);
        btnRefresh.addActionListener(e -> loadStudents());

        JButton btnAddStudent = new JButton("Them hoc vien");
        UITheme.stylePrimaryButton(btnAddStudent);
        btnAddStudent.addActionListener(e -> showAddStudentDialog());

        JButton btnEditStudent = new JButton("Sua hoc vien");
        UITheme.stylePrimaryButton(btnEditStudent);
        btnEditStudent.addActionListener(e -> showEditStudentDialog());

        JButton btnDeleteStudent = new JButton("Xoa hoc vien");
        UITheme.stylePrimaryButton(btnDeleteStudent);
        btnDeleteStudent.addActionListener(e -> handleDeleteStudent());

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionPanel.setOpaque(false);
        actionPanel.add(btnAddStudent);
        actionPanel.add(btnEditStudent);
        actionPanel.add(btnDeleteStudent);
        actionPanel.add(btnRefresh);
        topPanel.add(actionPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        studentModel = new DefaultTableModel(
                new String[]{"ID", "Ho ten", "SDT", "Email", "Gioi tinh", "Ngay dang ky", "Trang thai"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        studentTable = new JTable(studentModel);
        UITheme.styleTable(studentTable);
        add(new JScrollPane(studentTable), BorderLayout.CENTER);

        loadStudents();
    }

    public void refreshData() {
        loadStudents();
    }

    private void loadStudents() {
        studentModel.setRowCount(0);
        try {
            List<Object[]> rows = adminService.getAllStudents();
            rows.forEach(studentModel::addRow);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Khong tai duoc danh sach hoc vien: " + ex.getMessage(),
                    "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddStudentDialog() {
        showStudentDialog("Them hoc vien", null);
    }

    private void showEditStudentDialog() {
        int row = studentTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui long chon hoc vien can sua.");
            return;
        }

        Long studentId = ((Number) studentModel.getValueAt(row, 0)).longValue();
        StudentFormData data = new StudentFormData(
                studentId,
                valueAt(row, 1),
                "",
                valueAt(row, 4),
                valueAt(row, 2),
                valueAt(row, 3),
                "",
                valueAt(row, 5),
                valueAt(row, 6)
        );
        showStudentDialog("Sua hoc vien", data);
    }

    private void showStudentDialog(String title, StudentFormData existing) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(520, 420);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridBagLayout());
        UITheme.styleCard(form);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtFullName = new JTextField(24);
        JTextField txtDob = new JTextField(24);
        JComboBox<String> cboGender = new JComboBox<>(new String[]{"", "Nam", "Nu"});
        JTextField txtPhone = new JTextField(24);
        JTextField txtEmail = new JTextField(24);
        JTextField txtAddress = new JTextField(24);
        JTextField txtRegDate = new JTextField(24);
        JComboBox<String> cboStatus = new JComboBox<>(new String[]{"Active", "Inactive"});

        if (existing == null) {
            txtRegDate.setText(LocalDate.now().toString());
            cboStatus.setSelectedItem("Active");
        } else {
            txtFullName.setText(existing.fullName());
            txtDob.setText(existing.dateOfBirth());
            cboGender.setSelectedItem(existing.gender() == null ? "" : existing.gender());
            txtPhone.setText(existing.phone());
            txtEmail.setText(existing.email());
            txtAddress.setText(existing.address());
            txtRegDate.setText(existing.registrationDate());
            cboStatus.setSelectedItem(existing.status() == null || existing.status().isBlank() ? "Active" : existing.status());
        }

        int row = 0;
        row = addFormRow(form, gbc, row, "Ho ten:", txtFullName);
        row = addFormRow(form, gbc, row, "Ngay sinh (yyyy-MM-dd):", txtDob);
        row = addFormRow(form, gbc, row, "Gioi tinh:", cboGender);
        row = addFormRow(form, gbc, row, "SDT:", txtPhone);
        row = addFormRow(form, gbc, row, "Email:", txtEmail);
        row = addFormRow(form, gbc, row, "Dia chi:", txtAddress);
        row = addFormRow(form, gbc, row, "Ngay dang ky (yyyy-MM-dd):", txtRegDate);
        row = addFormRow(form, gbc, row, "Trang thai:", cboStatus);

        JButton btnSave = new JButton(existing == null ? "Luu hoc vien" : "Cap nhat hoc vien");
        UITheme.stylePrimaryButton(btnSave);
        btnSave.addActionListener(e -> {
            try {
                if (existing == null) {
                    Long studentId = adminService.createStudent(
                            txtFullName.getText(),
                            txtDob.getText(),
                            (String) cboGender.getSelectedItem(),
                            txtPhone.getText(),
                            txtEmail.getText(),
                            txtAddress.getText(),
                            txtRegDate.getText(),
                            (String) cboStatus.getSelectedItem());
                    JOptionPane.showMessageDialog(dialog,
                            "Da them hoc vien thanh cong. ID = " + studentId,
                            "Thanh cong", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    adminService.updateStudent(
                            existing.studentId(),
                            txtFullName.getText(),
                            txtDob.getText(),
                            (String) cboGender.getSelectedItem(),
                            txtPhone.getText(),
                            txtEmail.getText(),
                            txtAddress.getText(),
                            txtRegDate.getText(),
                            (String) cboStatus.getSelectedItem());
                    JOptionPane.showMessageDialog(dialog,
                            "Cap nhat hoc vien thanh cong.",
                            "Thanh cong", JOptionPane.INFORMATION_MESSAGE);
                }

                dialog.dispose();
                loadStudents();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Khong luu duoc hoc vien: " + ex.getMessage(),
                        "Loi", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 1;
        gbc.gridy = row;
        form.add(btnSave, gbc);

        dialog.setContentPane(form);
        dialog.setVisible(true);
    }

    private void handleDeleteStudent() {
        int row = studentTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui long chon hoc vien can xoa.");
            return;
        }

        Long studentId = ((Number) studentModel.getValueAt(row, 0)).longValue();
        String studentName = valueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Xoa hoc vien '" + studentName + "'?\nCac du lieu lien quan (tai khoan, ghi danh, diem danh, thanh toan) se bi xoa.",
                "Xac nhan xoa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            adminService.deleteStudent(studentId);
            JOptionPane.showMessageDialog(this, "Da xoa hoc vien thanh cong.", "Thanh cong", JOptionPane.INFORMATION_MESSAGE);
            loadStudents();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Khong xoa duoc hoc vien: " + ex.getMessage(),
                    "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String valueAt(int row, int col) {
        Object value = studentModel.getValueAt(row, col);
        return value == null ? "" : value.toString();
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

    private record StudentFormData(Long studentId, String fullName, String dateOfBirth, String gender,
                                   String phone, String email, String address,
                                   String registrationDate, String status) {
    }
}
