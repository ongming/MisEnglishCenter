package com.center.manager.ui.panel;

import com.center.manager.service.AdminService;
import com.center.manager.service.ServiceFactory;
import com.center.manager.ui.UITheme;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Tab tạo tài khoản cho giáo viên chưa có account.
 */
public class AdminTeacherAccountPanel extends JPanel {

    private final AdminService adminService = ServiceFactory.adminService();

    private JComboBox<String> cboTeacher;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private final List<Long> teacherIds = new ArrayList<>();

    public AdminTeacherAccountPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        UITheme.styleRootPanel(this);

        JLabel lblTitle = new JLabel("Tạo tài khoản giáo viên");
        UITheme.styleSectionTitle(lblTitle);
        add(lblTitle, BorderLayout.NORTH);

        add(buildForm(), BorderLayout.CENTER);
        loadTeachersWithoutAccount();
    }

    public void refreshData() {
        loadTeachersWithoutAccount();
    }

    private JPanel buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        UITheme.styleCard(form);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cboTeacher = new JComboBox<>();
        txtUsername = new JTextField(26);
        txtPassword = new JPasswordField(26);

        int row = 0;
        row = addRow(form, gbc, row, "Giáo viên:", cboTeacher);
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
        btnRefresh.addActionListener(e -> loadTeachersWithoutAccount());

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

    private void loadTeachersWithoutAccount() {
        cboTeacher.removeAllItems();
        teacherIds.clear();

        try {
            List<Object[]> rows = adminService.getTeachersWithoutAccount();
            for (Object[] row : rows) {
                teacherIds.add((Long) row[0]);
                cboTeacher.addItem((String) row[1]);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Không tải được danh sách giáo viên: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleCreateAccount() {
        int idx = cboTeacher.getSelectedIndex();
        if (idx < 0) {
            JOptionPane.showMessageDialog(this, "Không còn giáo viên nào cần tạo tài khoản.");
            return;
        }

        Long teacherId = teacherIds.get(idx);
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        try {
            adminService.createTeacherAccount(teacherId, username, password);
            JOptionPane.showMessageDialog(this,
                    "Tạo tài khoản giáo viên thành công.",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);

            txtUsername.setText("");
            txtPassword.setText("");
            loadTeachersWithoutAccount();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Không tạo được tài khoản: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
