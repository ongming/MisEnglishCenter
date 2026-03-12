package com.center.manager.ui.panel;

import com.center.manager.service.PaymentService;
import com.center.manager.service.ServiceFactory;
import com.center.manager.ui.UITheme;
import com.center.manager.util.UserSession;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Tab "Thanh toán" của Sinh viên — xem lịch sử thanh toán.
 */
public class StudentPaymentPanel extends JPanel {

    private final PaymentService paymentService = ServiceFactory.paymentService();
    private final DefaultTableModel paymentModel;

    public StudentPaymentPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        UITheme.styleRootPanel(this);

        JLabel lblTitle = new JLabel("Lịch sử thanh toán");
        UITheme.styleSectionTitle(lblTitle);
        add(lblTitle, BorderLayout.NORTH);

        paymentModel = new DefaultTableModel(
                new String[]{"ID", "Học viên", "Số tiền", "Ngày thanh toán", "Phương thức", "Trạng thái", "Mã tham chiếu"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        JTable table = new JTable(paymentModel);
        UITheme.styleTable(table);
        // Ẩn cột ID
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);

        add(new JScrollPane(table), BorderLayout.CENTER);

        loadPayments();
    }

    public void refreshData() {
        loadPayments();
    }

    private void loadPayments() {
        paymentModel.setRowCount(0);
        Long studentId = UserSession.getInstance().getStudentId();
        if (studentId == null) return;

        try {
            List<Object[]> payments = paymentService.getByStudent(studentId);
            for (Object[] row : payments) {
                paymentModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Không tải được lịch sử thanh toán: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
