package com.center.manager.ui.panel;

import com.center.manager.service.PaymentService;
import com.center.manager.service.ServiceFactory;
import com.center.manager.ui.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Tab quản lý thanh toán (Admin) — xem tất cả thanh toán.
 */
public class AdminPaymentPanel extends JPanel {

    private final PaymentService paymentService = ServiceFactory.paymentService();
    private final DefaultTableModel paymentModel;

    public AdminPaymentPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        UITheme.styleRootPanel(this);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("Danh sách thanh toán");
        UITheme.styleSectionTitle(lblTitle);
        topPanel.add(lblTitle, BorderLayout.WEST);

        JButton btnRefresh = new JButton("Làm mới");
        UITheme.stylePrimaryButton(btnRefresh);
        btnRefresh.addActionListener(e -> loadPayments());
        topPanel.add(btnRefresh, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

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
        try {
            List<Object[]> payments = paymentService.getAll();
            for (Object[] row : payments) {
                paymentModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Không tải được danh sách thanh toán: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
