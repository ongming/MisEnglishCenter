package com.center.manager.ui.panel;

import com.center.manager.service.AdminService;
import com.center.manager.service.ServiceFactory;
import com.center.manager.ui.UITheme;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Tab tạo tài khoản cho học viên chưa có account.
 */
public class AdminStudentAccountPanel extends JPanel {

    private final AdminService adminService;

    private JComboBox<String> cboStudent;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private final List<Long> studentIds = new ArrayList<>();

    public AdminStudentAccountPanel() {
        this.adminService = ServiceFactory.adminService();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        UITheme.styleRootPanel(this);

        JLabel lblTitle = new JLabel("Tạo tài khoản học viên");
        UITheme.styleSectionTitle(lblTitle);
        add(lblTitle, BorderLayout.NORTH);

        add(buildForm(), BorderLayout.CENTER);
        loadStudentsWithoutAccount();
    }

    public void refreshData() {
        loadStudentsWithoutAccount();
    }

    private JPanel buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        UITheme.styleCard(form);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cboStudent = new JComboBox<>();
        txtUsername = new JTextField(26);
        txtPassword = new JPasswordField(26);

        int row = 0;
        row = addRow(form, gbc, row, "Học viên:", cboStudent);
        row = addRow(form, gbc, row, "Username:", txtUsername);
        row = addRow(form, gbc, row, "Password:", txtPassword);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JButton btnCreate = new JButton("Tạo tài khoản");
        JButton btnRefresh = new JButton("Làm mới danh sách");
        UITheme.stylePrimaryButton(btnCreate);
        UITheme.stylePrimaryButton(btnRefresh);
        btnPanel.add(btnCreate);
        btnPanel.add(btnRefresh);

        btnCreate.addActionListener(e -> handleCreateAccount());
        btnRefresh.addActionListener(e -> loadStudentsWithoutAccount());

        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.weightx = 1;
        form.add(btnPanel, gbc);

        return form;
    }

    private int addRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent component) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(component, gbc);
        return row + 1;
    }

    private void loadStudentsWithoutAccount() {
        cboStudent.removeAllItems();
        studentIds.clear();

        try {
            List<Object[]> rows = adminService.getStudentsWithoutAccount();
            for (Object[] row : rows) {
                studentIds.add((Long) row[0]);
                cboStudent.addItem((String) row[1]);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Không tải được danh sách học viên: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleCreateAccount() {
        int idx = cboStudent.getSelectedIndex();
        if (idx < 0) {
            JOptionPane.showMessageDialog(this, "Không còn học viên nào cần tạo tài khoản.");
            return;
        }

        Long studentId = studentIds.get(idx);
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        try {
            adminService.createStudentAccount(studentId, username, password);
            JOptionPane.showMessageDialog(this,
                    "Tạo tài khoản học viên thành công.",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);

            txtUsername.setText("");
            txtPassword.setText("");
            loadStudentsWithoutAccount();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Không tạo được tài khoản: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
