package com.center.manager.ui.panel;

import com.center.manager.service.AdminService;
import com.center.manager.service.ServiceFactory;
import com.center.manager.ui.UITheme;

import javax.swing.*;
import java.awt.*;

/**
 * Tab thêm giáo viên mới.
 */
public class AdminTeacherCreatePanel extends JPanel {

    private final AdminService adminService = ServiceFactory.adminService();

    private JTextField txtFullName;
    private JTextField txtPhone;
    private JTextField txtEmail;
    private JTextField txtSpecialization;
    private JComboBox<String> cboStatus;

    public AdminTeacherCreatePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        UITheme.styleRootPanel(this);

        JLabel lblTitle = new JLabel("Thêm giáo viên mới");
        UITheme.styleSectionTitle(lblTitle);
        add(lblTitle, BorderLayout.NORTH);

        add(buildForm(), BorderLayout.CENTER);
    }

    private JPanel buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        UITheme.styleCard(form);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtFullName = new JTextField(28);
        txtPhone = new JTextField(28);
        txtEmail = new JTextField(28);
        txtSpecialization = new JTextField(28);
        cboStatus = new JComboBox<>(new String[]{"Active", "Inactive"});

        int row = 0;
        row = addRow(form, gbc, row, "Họ tên:", txtFullName);
        row = addRow(form, gbc, row, "Số điện thoại:", txtPhone);
        row = addRow(form, gbc, row, "Email:", txtEmail);
        row = addRow(form, gbc, row, "Chuyên môn:", txtSpecialization);
        row = addRow(form, gbc, row, "Trạng thái:", cboStatus);

        JButton btnCreate = new JButton("Thêm giáo viên");
        UITheme.stylePrimaryButton(btnCreate);
        btnCreate.addActionListener(e -> handleCreateTeacher());

        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.weightx = 1;
        form.add(btnCreate, gbc);

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

    private void handleCreateTeacher() {
        try {
            Long teacherId = adminService.createTeacher(
                    txtFullName.getText(),
                    txtPhone.getText(),
                    txtEmail.getText(),
                    txtSpecialization.getText(),
                    (String) cboStatus.getSelectedItem()
            );

            JOptionPane.showMessageDialog(this,
                    "Đã thêm giáo viên thành công. Teacher ID = " + teacherId,
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);

            txtFullName.setText("");
            txtPhone.setText("");
            txtEmail.setText("");
            txtSpecialization.setText("");
            cboStatus.setSelectedIndex(0);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Không thêm được giáo viên: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
