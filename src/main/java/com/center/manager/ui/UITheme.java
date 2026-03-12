package com.center.manager.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Theme chung cho toàn bộ giao diện Swing.
 * Chủ đạo xanh lá đậm theo phong cách tối giản hiện đại.
 */
public final class UITheme {

    public static final Color APP_BG = new Color(240, 247, 243);
    public static final Color PANEL_BG = Color.WHITE;

    public static final Color SIDEBAR_BG = new Color(11, 61, 44);
    public static final Color SIDEBAR_ITEM_BG = new Color(11, 61, 44);
    public static final Color SIDEBAR_ITEM_ACTIVE_BG = new Color(19, 94, 68);

    public static final Color PRIMARY = new Color(16, 122, 84);
    public static final Color PRIMARY_DARK = new Color(11, 94, 64);
    public static final Color TEXT_DARK = new Color(28, 37, 34);
    public static final Color TEXT_MUTED = new Color(198, 225, 214);
    public static final Color DANGER = new Color(220, 68, 55);

    private UITheme() {
    }

    public static void installGlobalDefaults() {
        UIManager.put("Panel.background", APP_BG);
        UIManager.put("OptionPane.background", APP_BG);
        UIManager.put("OptionPane.messageForeground", TEXT_DARK);
        UIManager.put("Label.foreground", TEXT_DARK);
        UIManager.put("TextField.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("PasswordField.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("ComboBox.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("Table.font", new Font("Segoe UI", Font.PLAIN, 13));
    }

    public static void styleRootPanel(JComponent panel) {
        panel.setBackground(APP_BG);
    }

    public static void styleCard(JComponent panel) {
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 232, 226)),
                new EmptyBorder(14, 14, 14, 14)
        ));
    }

    public static void styleTitle(JLabel label) {
        label.setFont(new Font("Segoe UI", Font.BOLD, 20));
        label.setForeground(PRIMARY_DARK);
    }

    public static void styleSectionTitle(JLabel label) {
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(PRIMARY_DARK);
    }

    public static void styleSidebar(JPanel sidebar) {
        sidebar.setBackground(SIDEBAR_BG);
    }

    public static void styleSidebarButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(SIDEBAR_ITEM_BG);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
    }

    public static void setSidebarButtonActive(JButton btn, boolean active) {
        btn.setBackground(active ? SIDEBAR_ITEM_ACTIVE_BG : SIDEBAR_ITEM_BG);
    }

    public static void stylePrimaryButton(JButton btn) {
        btn.setBackground(PRIMARY);
        btn.setForeground(UITheme.PRIMARY);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void styleDangerButton(JButton btn) {
        btn.setBackground(DANGER);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(218, 242, 231));
        table.setSelectionForeground(TEXT_DARK);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(223, 239, 232));
        header.setForeground(PRIMARY_DARK);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
    }
}

