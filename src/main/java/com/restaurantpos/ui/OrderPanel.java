package com.restaurantpos.ui;

import com.restaurantpos.data.MenuData;
import com.restaurantpos.data.POSStore;
import com.restaurantpos.model.Bill;
import com.restaurantpos.model.KOT;
import com.restaurantpos.model.OrderItem;
import com.restaurantpos.model.TableSession;
import com.restaurantpos.model.Table;
import com.restaurantpos.model.MenuItem;
import com.restaurantpos.util.Theme;
import com.restaurantpos.util.UIHelper;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OrderPanel extends JPanel {
    private final POSStore store = POSStore.getInstance();
    private final FloorPanel parent;

    private int currentTableId = -1;
    private Table currentTable;

    // Left - menu
    private JPanel menuCategoryBar;
    private JPanel menuItemsPanel;
    private JTextField searchField;
    private String activeCategory = "Starters";

    // Right - order
    private JLabel tableNameLabel;
    private JLabel tableInfoLabel;
    private JComboBox<String> waiterCombo;
    private JPanel orderItemsPanel;
    private JLabel subtotalLabel, taxLabel, totalLabel;
    private JButton kotBtn, billReqBtn, settleBtn;
    private JLabel sessionInfoLabel;

    public OrderPanel(FloorPanel parent) {
        this.parent = parent;
        setBackground(Theme.BG_DARKEST);
        setLayout(new BorderLayout());
        buildUI();
        store.addListener(this::refreshOrder);
    }

    private void buildUI() {
        // TOP BAR
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Theme.BG_DARK);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, Theme.BORDER),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)));

        JPanel topLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        topLeft.setOpaque(false);

        JButton backBtn = UIHelper.ghostBtn("← Back");
        backBtn.addActionListener(e -> parent.showFloor());

        tableNameLabel = UIHelper.label("—", Theme.FONT_HEADING, Theme.TEAL);
        tableInfoLabel = UIHelper.label("", Theme.FONT_SMALL, Theme.TEXT_MUTED);

        JPanel waiterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        waiterPanel.setOpaque(false);
        waiterPanel.add(UIHelper.label("Waiter:", Theme.FONT_SMALL, Theme.TEXT_MUTED));
        waiterCombo = UIHelper.comboBox(MenuData.WAITERS);
        waiterCombo.setPreferredSize(new Dimension(110, 28));
        waiterPanel.add(waiterCombo);

        topLeft.add(backBtn);
        topLeft.add(Box.createHorizontalStrut(4));
        topLeft.add(tableNameLabel);
        topLeft.add(tableInfoLabel);
        topLeft.add(Box.createHorizontalStrut(8));
        topLeft.add(waiterPanel);

        sessionInfoLabel = UIHelper.label("", Theme.FONT_SMALL, Theme.TEXT_MUTED);

        topBar.add(topLeft, BorderLayout.WEST);
        topBar.add(sessionInfoLabel, BorderLayout.EAST);

        // SPLIT: Left Menu | Right Order
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildMenuPanel(), buildOrderPanel());
        split.setDividerLocation(420);
        split.setDividerSize(1);
        split.setBackground(Theme.BORDER);
        split.setBorder(null);

        add(topBar, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
    }

    // ---- LEFT: MENU PANEL ----
    private JPanel buildMenuPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.BG_DARK);
        panel.setPreferredSize(new Dimension(420, 0));

        // Search
        JPanel searchPanel = new JPanel(new BorderLayout(0, 8));
        searchPanel.setOpaque(false);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(12, 14, 8, 14));
        searchField = UIHelper.searchField("Search menu...");
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                refreshMenuItems();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                refreshMenuItems();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
            }
        });
        searchPanel.add(searchField, BorderLayout.CENTER);

        // Category bar
        menuCategoryBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        menuCategoryBar.setBackground(Theme.BG_DARK);
        menuCategoryBar.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, Theme.BORDER),
                BorderFactory.createEmptyBorder(0, 8, 0, 8)));
        buildCategoryButtons();

        // Items
        menuItemsPanel = new JPanel();
        menuItemsPanel.setBackground(Theme.BG_DARK);
        menuItemsPanel.setLayout(new BoxLayout(menuItemsPanel, BoxLayout.Y_AXIS));
        menuItemsPanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JScrollPane scroll = UIHelper.scrollPane(menuItemsPanel);

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(menuCategoryBar, BorderLayout.CENTER);

        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(menuCategoryBar, BorderLayout.NORTH);
        centerWrapper.add(scroll, BorderLayout.CENTER);

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(centerWrapper, BorderLayout.CENTER);

        return panel;
    }

    private void buildCategoryButtons() {
        menuCategoryBar.removeAll();
        for (String cat : MenuData.getMenu().keySet()) {
            JButton btn = new JButton(cat);
            btn.setFont(Theme.FONT_TINY);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
            boolean active = cat.equals(activeCategory);
            btn.setBackground(active ? Theme.BG_SELECTED : Theme.BG_CARD);
            btn.setForeground(active ? Theme.TEAL : Theme.TEXT_MUTED);
            btn.setContentAreaFilled(false);
            btn.setOpaque(true);
            btn.addActionListener(e -> {
                activeCategory = cat;
                buildCategoryButtons();
                refreshMenuItems();
            });
            menuCategoryBar.add(btn);
        }
        menuCategoryBar.revalidate();
        menuCategoryBar.repaint();
    }

    private void refreshMenuItems() {
        menuItemsPanel.removeAll();
        String query = searchField.getText().toLowerCase().trim();
        List<MenuItem> items;
        if (!query.isEmpty()) {
            items = MenuData.getAllItems();
            items = items.stream().filter(i -> i.getName().toLowerCase().contains(query)).toList();
        } else {
            items = MenuData.getMenu().getOrDefault(activeCategory, List.of());
        }

        for (MenuItem item : items) {
            menuItemsPanel.add(buildMenuItemRow(item));
            menuItemsPanel.add(Box.createVerticalStrut(5));
        }

        menuItemsPanel.revalidate();
        menuItemsPanel.repaint();
    }

    private JPanel buildMenuItemRow(MenuItem item) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setBackground(Theme.BG_ROW);
        row.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Theme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(9, 12, 9, 12)));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel info = new JPanel(new GridLayout(2, 1, 0, 2));
        info.setOpaque(false);
        info.add(UIHelper.label(item.getName(), Theme.FONT_BODY, Theme.TEXT_PRIMARY));
        String taxText = item.getTaxPercent() > 0 ? "+" + (int) item.getTaxPercent() + "% GST" : "No Tax";
        info.add(UIHelper.label(taxText, Theme.FONT_TINY, Theme.TEXT_MUTED));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        right.add(UIHelper.label(String.format("₹%.0f", item.getPrice()), Theme.FONT_SUBHEAD, Theme.TEAL));

        JButton addBtn = new JButton("+");
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        addBtn.setPreferredSize(new Dimension(28, 28));
        addBtn.setBackground(Theme.TEAL);
        addBtn.setForeground(Color.WHITE);
        addBtn.setBorder(new LineBorder(Theme.TEAL_DARK, 1, true));
        addBtn.setFocusPainted(false);
        addBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addBtn.addActionListener(e -> {
            String waiter = (String) waiterCombo.getSelectedItem();
            store.addItemToSession(currentTableId, item, waiter);
            showToast(item.getName() + " added");
        });

        right.add(addBtn);

        row.add(info, BorderLayout.CENTER);
        row.add(right, BorderLayout.EAST);

        row.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                row.setBackground(Theme.BG_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                row.setBackground(Theme.BG_ROW);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getSource() == row) {
                    store.addItemToSession(currentTableId, item, (String) waiterCombo.getSelectedItem());
                    showToast(item.getName() + " added");
                }
            }
        });

        return row;
    }

    // ---- RIGHT: ORDER PANEL ----
    private JPanel buildOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.BG_DARKEST);

        // Order items scroll
        orderItemsPanel = new JPanel();
        orderItemsPanel.setBackground(Theme.BG_DARKEST);
        orderItemsPanel.setLayout(new BoxLayout(orderItemsPanel, BoxLayout.Y_AXIS));
        orderItemsPanel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JScrollPane scroll = UIHelper.scrollPane(orderItemsPanel);

        // Totals + actions
        JPanel footer = buildFooter();

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(footer, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(Theme.BG_DARK);
        footer.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 0, 0, 0, Theme.BORDER),
                BorderFactory.createEmptyBorder(12, 16, 14, 16)));

        // Totals
        JPanel totals = new JPanel(new GridLayout(3, 2, 0, 4));
        totals.setOpaque(false);
        totals.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        totals.add(UIHelper.label("Subtotal", Theme.FONT_SMALL, Theme.TEXT_MUTED));
        subtotalLabel = UIHelper.label("₹0.00", Theme.FONT_SMALL, Theme.TEXT_SECONDARY);
        subtotalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        totals.add(subtotalLabel);

        totals.add(UIHelper.label("GST", Theme.FONT_SMALL, Theme.TEXT_MUTED));
        taxLabel = UIHelper.label("₹0.00", Theme.FONT_SMALL, Theme.TEXT_SECONDARY);
        taxLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        totals.add(taxLabel);

        totals.add(UIHelper.label("TOTAL", Theme.FONT_HEADING, Theme.TEXT_PRIMARY));
        totalLabel = UIHelper.label("₹0.00", Theme.FONT_AMOUNT, Theme.TEAL);
        totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        totals.add(totalLabel);

        // Buttons
        JPanel btnPanel = new JPanel(new GridLayout(1, 3, 8, 0));
        btnPanel.setOpaque(false);

        kotBtn = UIHelper.warningBtn("🖨  KOT");
        billReqBtn = UIHelper.button("📄  Bill Req.", Theme.TABLE_OCCUPIED_BG, Theme.TEAL, Theme.TABLE_OCCUPIED_BD);
        settleBtn = UIHelper.successBtn("✓  Settle Bill");

        kotBtn.setPreferredSize(new Dimension(0, 40));
        billReqBtn.setPreferredSize(new Dimension(0, 40));
        settleBtn.setPreferredSize(new Dimension(0, 40));

        kotBtn.addActionListener(e -> fireKOT());
        billReqBtn.addActionListener(e -> requestBill());
        settleBtn.addActionListener(e -> settle());

        btnPanel.add(kotBtn);
        btnPanel.add(billReqBtn);
        btnPanel.add(settleBtn);

        footer.add(totals, BorderLayout.CENTER);
        footer.add(btnPanel, BorderLayout.SOUTH);
        return footer;
    }

    // ---- LOAD / REFRESH ----
    public void loadTable(int tableId) {
        currentTableId = tableId;
        currentTable = MenuData.getTables().stream().filter(t -> t.getId() == tableId).findFirst().orElse(null);
        if (currentTable != null) {
            tableNameLabel.setText(currentTable.getName());
            tableInfoLabel.setText("  ·  " + currentTable.getSection() + "  ·  " + currentTable.getSeats() + " seats");
        }
        activeCategory = "Starters";
        buildCategoryButtons();
        refreshMenuItems();
        refreshOrder();
    }

    private void refreshOrder() {
        if (currentTableId < 0)
            return;
        SwingUtilities.invokeLater(() -> {
            orderItemsPanel.removeAll();
            TableSession sess = store.getSession(currentTableId);

            if (sess == null || sess.getItems().isEmpty()) {
                JPanel empty = new JPanel(new GridBagLayout());
                empty.setOpaque(false);
                JLabel msg = UIHelper.label("No items yet. Add from the menu →", Theme.FONT_BODY, Theme.TEXT_MUTED);
                empty.add(msg);
                empty.setPreferredSize(new Dimension(0, 200));
                orderItemsPanel.add(empty);
            } else {
                // KOT'd items
                List<OrderItem> printed = sess.getItems().stream().filter(OrderItem::isPrinted).toList();
                List<OrderItem> pending = sess.getItems().stream().filter(i -> !i.isPrinted()).toList();

                if (!printed.isEmpty()) {
                    orderItemsPanel.add(sectionHeader("KOT'd Items", Theme.TEXT_MUTED));
                    for (OrderItem oi : printed)
                        orderItemsPanel.add(buildOrderRow(oi, true));
                    orderItemsPanel.add(Box.createVerticalStrut(10));
                }
                if (!pending.isEmpty()) {
                    orderItemsPanel.add(sectionHeader("▸ New — Pending KOT", Theme.AMBER));
                    for (OrderItem oi : pending)
                        orderItemsPanel.add(buildOrderRow(oi, false));
                }

                // Update totals
                subtotalLabel.setText(String.format("₹%.2f", sess.getSubtotal()));
                taxLabel.setText(String.format("₹%.2f", sess.getTaxTotal()));
                totalLabel.setText(String.format("₹%.2f", sess.getGrandTotal()));

                sessionInfoLabel.setText(
                        "Since " + sess.getStartTimeFormatted() + "  ·  " + sess.getKotCount() + " KOTs fired");

                boolean hasNew = !pending.isEmpty();
                kotBtn.setEnabled(hasNew);
                kotBtn.setForeground(hasNew ? Theme.AMBER : Theme.TEXT_MUTED);
            }

            if (sess == null || sess.getItems().isEmpty()) {
                subtotalLabel.setText("₹0.00");
                taxLabel.setText("₹0.00");
                totalLabel.setText("₹0.00");
                kotBtn.setEnabled(false);
            }

            orderItemsPanel.revalidate();
            orderItemsPanel.repaint();
        });
    }

    private JPanel sectionHeader(String title, Color color) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        JLabel l = UIHelper.label(title, new Font("Segoe UI", Font.BOLD, 10), color);
        p.add(l);
        return p;
    }

    private JPanel buildOrderRow(OrderItem oi, boolean printed) {
        JPanel row = new JPanel(new BorderLayout(6, 0));
        row.setBackground(printed ? Theme.BG_ROW : Theme.BG_ROW_ALT);
        row.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Theme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

        JPanel info = new JPanel(new GridLayout(2, 1, 0, 2));
        info.setOpaque(false);
        Color nameColor = printed ? Theme.TEXT_MUTED : Theme.TEXT_PRIMARY;
        info.add(UIHelper.label(oi.getMenuItem().getName(), Theme.FONT_BODY, nameColor));
        info.add(UIHelper.label(
                String.format("₹%.0f × %d = ₹%.2f", oi.getMenuItem().getPrice(), oi.getQty(), oi.getSubtotal()),
                Theme.FONT_TINY, Theme.TEXT_MUTED));

        row.add(info, BorderLayout.CENTER);

        if (printed) {
            JLabel badge = UIHelper.label("KOT'd", Theme.FONT_TINY, Theme.TEXT_MUTED);
            badge.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(Theme.BORDER, 1, true),
                    BorderFactory.createEmptyBorder(2, 6, 2, 6)));
            row.add(badge, BorderLayout.EAST);
        } else {
            JPanel ctrl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
            ctrl.setOpaque(false);

            JButton minus = qtyBtn("−");
            JLabel qtyLbl = UIHelper.label(String.valueOf(oi.getQty()), Theme.FONT_SUBHEAD, Theme.TEXT_PRIMARY);
            qtyLbl.setPreferredSize(new Dimension(20, 20));
            qtyLbl.setHorizontalAlignment(SwingConstants.CENTER);
            JButton plus = qtyBtn("+");
            JButton del = qtyBtn("✕");
            del.setForeground(Theme.RED);

            minus.addActionListener(e -> {
                store.updateQty(currentTableId, oi, -1);
            });
            plus.addActionListener(e -> {
                store.updateQty(currentTableId, oi, +1);
            });
            del.addActionListener(e -> {
                store.removeItemFromSession(currentTableId, oi);
            });

            ctrl.add(minus);
            ctrl.add(qtyLbl);
            ctrl.add(plus);
            ctrl.add(Box.createHorizontalStrut(4));
            ctrl.add(del);
            row.add(ctrl, BorderLayout.EAST);
        }

        row.add(Box.createVerticalStrut(5));
        return row;
    }

    private JButton qtyBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(Theme.FONT_SUBHEAD);
        btn.setPreferredSize(new Dimension(24, 24));
        btn.setBackground(Theme.BG_CARD);
        btn.setForeground(Theme.TEXT_SECONDARY);
        btn.setBorder(new LineBorder(Theme.BORDER, 1, true));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ---- ACTIONS ----
    private void fireKOT() {
        KOT kot = store.fireKOT(currentTableId);
        if (kot == null) {
            showToast("No new items to KOT");
            return;
        }
        KOTDialog.show(SwingUtilities.getWindowAncestor(this), kot);
    }

    private void requestBill() {
        store.requestBill(currentTableId);
        showToast("Bill requested for " + (currentTable != null ? currentTable.getName() : "table"));
    }

    private void settle() {
        TableSession sess = store.getSession(currentTableId);
        if (sess == null || sess.getItems().isEmpty()) {
            showToast("No items on this table");
            return;
        }

        String[] payModes = { "Cash", "UPI", "Card", "Complimentary" };
        String payMode = (String) JOptionPane.showInputDialog(
                SwingUtilities.getWindowAncestor(this),
                "Select Payment Mode:", "Settle Bill",
                JOptionPane.PLAIN_MESSAGE, null, payModes, "Cash");
        if (payMode == null)
            return;

        Bill bill = store.settleBill(currentTableId, payMode);
        if (bill != null) {
            BillDialog.show(SwingUtilities.getWindowAncestor(this), bill);
            parent.showFloor();
        }
    }

    private JWindow toastWindow;

    private void showToast(String msg) {
        if (toastWindow != null)
            toastWindow.dispose();
        Window owner = SwingUtilities.getWindowAncestor(this);
        toastWindow = new JWindow(owner);
        JLabel l = UIHelper.label("  ✓  " + msg + "  ", Theme.FONT_SMALL, Theme.GREEN);
        l.setOpaque(true);
        l.setBackground(new Color(13, 46, 26));
        l.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Theme.GREEN_DARK, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        toastWindow.add(l);
        toastWindow.pack();
        if (owner != null) {
            Point p = owner.getLocationOnScreen();
            toastWindow.setLocation(p.x + owner.getWidth() - toastWindow.getWidth() - 24,
                    p.y + owner.getHeight() - toastWindow.getHeight() - 24);
        }
        toastWindow.setVisible(true);
        javax.swing.Timer t = new javax.swing.Timer(2500, e -> {
            toastWindow.dispose();
            toastWindow = null;
        });
        t.setRepeats(false);
        t.start();
    }
}