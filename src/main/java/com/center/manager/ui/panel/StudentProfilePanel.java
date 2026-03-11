package com.center.manager.ui.panel;

import com.center.manager.service.PersonService;
import com.center.manager.service.ServiceFactory;
import com.center.manager.util.UserSession;

import javax.swing.*;
import java.awt.*;

/**
 * Tab "Thông tin cá nhân" của Sinh viên.
 */
public class StudentProfilePanel extends JPanel {

    private final PersonService personService = ServiceFactory.personService();

    public StudentProfilePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initComponents();
    }

    private void initComponents() {
        JLabel lblTitle = new JLabel("Thông tin cá nhân");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblTitle, BorderLayout.NORTH);

        Long studentId = UserSession.getInstance().getStudentId();
        if (studentId == null) {
            add(new JLabel("Không tìm thấy thông tin sinh viên."), BorderLayout.CENTER);
            return;
        }

        Object[] profile = null;
        try {
            profile = personService.getStudentProfile(studentId);
        } catch (Exception e) { e.printStackTrace(); }
        if (profile == null) {
            add(new JLabel("Không tìm thấy thông tin sinh viên trong database."), BorderLayout.CENTER);
            return;
        }

        // profile: [studentId, fullName, dateOfBirth, gender, phone, email, address, registrationDate, status]
        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[][] fields = {
                {"Mã sinh viên:", profile[0].toString()},
                {"Họ và tên:", (String) profile[1]},
                {"Ngày sinh:", (String) profile[2]},
                {"Giới tính:", (String) profile[3]},
                {"Số điện thoại:", (String) profile[4]},
                {"Email:", (String) profile[5]},
                {"Địa chỉ:", (String) profile[6]},
                {"Ngày đăng ký:", (String) profile[7]},
                {"Trạng thái:", (String) profile[8]}
        };

        for (int i = 0; i < fields.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0;
            JLabel lbl = new JLabel(fields[i][0]);
            lbl.setFont(new Font("Arial", Font.BOLD, 14));
            infoPanel.add(lbl, gbc);

            gbc.gridx = 1;
            gbc.weightx = 1;
            JLabel val = new JLabel(fields[i][1]);
            val.setFont(new Font("Arial", Font.PLAIN, 14));
            infoPanel.add(val, gbc);
        }

        // Đẩy lên trên
        gbc.gridx = 0;
        gbc.gridy = fields.length;
        gbc.weighty = 1;
        infoPanel.add(Box.createVerticalGlue(), gbc);

        add(new JScrollPane(infoPanel), BorderLayout.CENTER);
    }
}

