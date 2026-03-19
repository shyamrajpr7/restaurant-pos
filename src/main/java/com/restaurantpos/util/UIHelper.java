package com.restaurantpos.util;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class UIHelper {

    public static JPanel darkPanel() {
        JPanel p = new JPanel();
        p.setBackground(Theme.BG_PANEL);
        p.setOpaque(true);
        return p;
    }

    public static JLabel label(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }

    public static JButton button(String text, Color bg, Color fg, Color border) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? bg.darker() :
                           getModel().isRollover() ? bg.brighter() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(Theme.FONT_SUBHEAD);
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setBorder(new LineBorder(border, 1, true));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JButton primaryBtn(String text) {
        return button(text, Theme.TEAL, Color.WHITE, Theme.TEAL_DARK);
    }

    public static JButton successBtn(String text) {
        return button(text, Theme.GREEN_DARK, Color.WHITE, Theme.GREEN);
    }

    public static JButton warningBtn(String text) {
        return button(text, Theme.TABLE_BILL_BG, Theme.AMBER, Theme.AMBER_DARK);
    }

    public static JButton dangerBtn(String text) {
        return button(text, new Color(63, 26, 26), Theme.RED, Theme.RED);
    }

    public static JButton ghostBtn(String text) {
        return button(text, Theme.BG_CARD, Theme.TEXT_SECONDARY, Theme.BORDER);
    }

    public static JTextField searchField(String placeholder) {
        JTextField field = new JTextField(placeholder) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(Theme.TEXT_MUTED);
                    g2.setFont(Theme.FONT_BODY);
                    g2.drawString(placeholder, 10, getHeight() / 2 + 5);
                }
            }
        };
        field.setBackground(Theme.BG_INPUT);
        field.setForeground(Theme.TEXT_PRIMARY);
        field.setCaretColor(Theme.TEAL);
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Theme.BORDER, 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        field.setFont(Theme.FONT_BODY);
        return field;
    }

    public static Border cardBorder() {
        return BorderFactory.createCompoundBorder(
            new LineBorder(Theme.BORDER, 1, true),
            BorderFactory.createEmptyBorder(12, 14, 12, 14)
        );
    }

    public static Border sectionBorder(String title) {
        TitledBorder tb = BorderFactory.createTitledBorder(
            new LineBorder(Theme.BORDER, 1, true), " " + title + " ",
            TitledBorder.LEFT, TitledBorder.TOP, Theme.FONT_TINY, Theme.TEXT_MUTED
        );
        return BorderFactory.createCompoundBorder(tb, BorderFactory.createEmptyBorder(4, 8, 8, 8));
    }

    public static JSeparator separator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(Theme.BORDER);
        sep.setBackground(Theme.BG_PANEL);
        return sep;
    }

    public static JScrollPane scrollPane(Component c) {
        JScrollPane sp = new JScrollPane(c);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(Theme.BG_PANEL);
        sp.setBackground(Theme.BG_PANEL);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.getVerticalScrollBar().setBackground(Theme.BG_DARK);
        sp.getHorizontalScrollBar().setBackground(Theme.BG_DARK);
        return sp;
    }

    public static JComboBox<String> comboBox(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setBackground(Theme.BG_CARD);
        cb.setForeground(Theme.TEXT_PRIMARY);
        cb.setFont(Theme.FONT_BODY);
        cb.setBorder(new LineBorder(Theme.BORDER, 1, true));
        cb.setFocusable(false);
        return cb;
    }

    public static void styleTable(JTable table) {
        table.setBackground(Theme.BG_PANEL);
        table.setForeground(Theme.TEXT_PRIMARY);
        table.setFont(Theme.FONT_BODY);
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.getTableHeader().setBackground(Theme.BG_DARK);
        table.getTableHeader().setForeground(Theme.TEXT_MUTED);
        table.getTableHeader().setFont(Theme.FONT_SMALL);
        table.getTableHeader().setBorder(new MatteBorder(0, 0, 1, 0, Theme.BORDER));
        table.setSelectionBackground(Theme.BG_SELECTED);
        table.setSelectionForeground(Theme.TEXT_PRIMARY);
        table.setGridColor(Theme.BORDER);
    }
}
