package com.restaurantpos.ui;

import com.restaurantpos.data.POSStore;
import com.restaurantpos.model.*;
import com.restaurantpos.util.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class ReportsPanel extends JPanel {
    private final POSStore store = POSStore.getInstance();
    private JPanel statsRow;
    private JPanel topItemsPanel;
    private JPanel waiterPanel;
    private JPanel billLogPanel;

    public ReportsPanel() {
        setBackground(Theme.BG_DARKEST);
        setLayout(new BorderLayout());
        buildUI();
        store.addListener(this::refresh);
    }

    private void buildUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(20, 24, 12, 24));

        JPanel titleArea = new JPanel(new GridLayout(2, 1));
        titleArea.setOpaque(false);
        titleArea.add(UIHelper.label("End-of-Day Report", Theme.FONT_TITLE, Theme.TEXT_PRIMARY));
        titleArea.add(UIHelper.label(java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")), Theme.FONT_SMALL, Theme.TEXT_MUTED));

        JButton resetBtn = UIHelper.dangerBtn("Reset Demo Data");
        resetBtn.setPreferredSize(new Dimension(140, 32));
        resetBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Reset all sessions, KOTs and bills?", "Confirm Reset", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) store.reset();
        });

        header.add(titleArea, BorderLayout.WEST);
        header.add(resetBtn, BorderLayout.EAST);

        // Stats row
        statsRow = new JPanel(new GridLayout(1, 4, 12, 0));
        statsRow.setOpaque(false);
        statsRow.setBorder(BorderFactory.createEmptyBorder(0, 24, 16, 24));

        // Content grid
        JPanel contentGrid = new JPanel(new GridLayout(1, 2, 14, 0));
        contentGrid.setOpaque(false);
        contentGrid.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));

        topItemsPanel = buildCard("Top Dishes Today");
        waiterPanel   = buildCard("Waiter Performance");
        contentGrid.add(topItemsPanel);
        contentGrid.add(waiterPanel);

        billLogPanel = buildCard("Bill Log");

        JPanel inner = new JPanel();
        inner.setBackground(Theme.BG_DARKEST);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.add(statsRow);
        inner.add(contentGrid);
        inner.add(Box.createVerticalStrut(14));
        JPanel billWrapper = new JPanel(new BorderLayout());
        billWrapper.setOpaque(false);
        billWrapper.setBorder(BorderFactory.createEmptyBorder(0, 24, 24, 24));
        billWrapper.add(billLogPanel, BorderLayout.CENTER);
        inner.add(billWrapper);

        add(header, BorderLayout.NORTH);
        add(UIHelper.scrollPane(inner), BorderLayout.CENTER);
    }

    private JPanel buildCard(String title) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Theme.BG_DARK);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Theme.BORDER, 1, true),
            BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));
        JLabel titleLabel = UIHelper.label(title, Theme.FONT_SUBHEAD, Theme.TEXT_SECONDARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        card.add(titleLabel, BorderLayout.NORTH);
        return card;
    }

    public void refresh() {
        SwingUtilities.invokeLater(() -> {
            List<Bill> todayBills = store.getTodayBills();
            double revenue = store.getTodayRevenue();
            double avgBill = todayBills.isEmpty() ? 0 : revenue / todayBills.size();

            // Stats
            statsRow.removeAll();
            addStat(statsRow, "Total Revenue", String.format("₹%.2f", revenue), Theme.TEAL);
            addStat(statsRow, "Bills Closed", String.valueOf(todayBills.size()), Theme.GREEN);
            addStat(statsRow, "KOTs Fired", String.valueOf(store.getKots().size()), Theme.AMBER);
            addStat(statsRow, "Avg Bill", String.format("₹%.2f", avgBill), Theme.PURPLE);

            // Top items
            refreshTopItems();
            refreshWaiterPanel();
            refreshBillLog(todayBills);

            revalidate(); repaint();
        });
    }

    private void addStat(JPanel panel, String label, String value, Color color) {
        JPanel card = new JPanel(new GridLayout(2, 1, 0, 6));
        card.setBackground(Theme.BG_DARK);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Theme.BORDER, 1, true),
            BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));
        JLabel lbl = UIHelper.label(label.toUpperCase(), new Font("Segoe UI", Font.BOLD, 9), Theme.TEXT_MUTED);
        JLabel val = UIHelper.label(value, new Font("Segoe UI", Font.BOLD, 22), color);
        card.add(lbl); card.add(val);
        panel.add(card);
    }

    private void refreshTopItems() {
        // Remove old content (keep title)
        Component title = topItemsPanel.getComponent(0);
        topItemsPanel.removeAll();
        topItemsPanel.add(title, BorderLayout.NORTH);

        Map<String, Integer> items = store.getTopItems();
        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        if (items.isEmpty()) {
            list.add(UIHelper.label("No data yet", Theme.FONT_SMALL, Theme.TEXT_MUTED));
        } else {
            int rank = 1;
            for (Map.Entry<String, Integer> e : items.entrySet()) {
                JPanel row = new JPanel(new BorderLayout(8, 0));
                row.setOpaque(false);
                row.setBorder(new MatteBorder(0, 0, 1, 0, Theme.BORDER));
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

                JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
                left.setOpaque(false);
                left.add(UIHelper.label("#" + rank++, Theme.FONT_TINY, Theme.TEXT_MUTED));
                left.add(UIHelper.label(e.getKey(), Theme.FONT_BODY, Theme.TEXT_PRIMARY));
                row.add(left, BorderLayout.WEST);
                row.add(UIHelper.label(e.getValue() + " sold", Theme.FONT_SMALL, Theme.TEAL), BorderLayout.EAST);
                list.add(row);
            }
        }
        topItemsPanel.add(list, BorderLayout.CENTER);
        topItemsPanel.revalidate(); topItemsPanel.repaint();
    }

    private void refreshWaiterPanel() {
        Component title = waiterPanel.getComponent(0);
        waiterPanel.removeAll();
        waiterPanel.add(title, BorderLayout.NORTH);

        Map<String, Double> sales = store.getWaiterSales();
        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        if (sales.isEmpty()) {
            list.add(UIHelper.label("No data yet", Theme.FONT_SMALL, Theme.TEXT_MUTED));
        } else {
            for (Map.Entry<String, Double> e : sales.entrySet()) {
                JPanel row = new JPanel(new BorderLayout(8, 0));
                row.setOpaque(false);
                row.setBorder(new MatteBorder(0, 0, 1, 0, Theme.BORDER));
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

                JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
                left.setOpaque(false);
                JLabel avatar = UIHelper.label(String.valueOf(e.getKey().charAt(0)), Theme.FONT_SMALL, Theme.TEAL);
                avatar.setOpaque(true);
                avatar.setBackground(Theme.BG_SELECTED);
                avatar.setPreferredSize(new Dimension(26, 26));
                avatar.setHorizontalAlignment(SwingConstants.CENTER);
                left.add(avatar);
                left.add(UIHelper.label(e.getKey(), Theme.FONT_BODY, Theme.TEXT_PRIMARY));
                row.add(left, BorderLayout.WEST);
                row.add(UIHelper.label(String.format("₹%.2f", e.getValue()), Theme.FONT_SMALL, Theme.GREEN), BorderLayout.EAST);
                list.add(row);
            }
        }
        waiterPanel.add(list, BorderLayout.CENTER);
        waiterPanel.revalidate(); waiterPanel.repaint();
    }

    private void refreshBillLog(List<Bill> bills) {
        Component title = billLogPanel.getComponent(0);
        billLogPanel.removeAll();
        billLogPanel.add(title, BorderLayout.NORTH);

        if (bills.isEmpty()) {
            billLogPanel.add(UIHelper.label("No bills closed today", Theme.FONT_SMALL, Theme.TEXT_MUTED), BorderLayout.CENTER);
        } else {
            String[] cols = {"Bill #", "Table", "Waiter", "Items", "GST", "Total", "Pay Mode", "Time"};
            Object[][] data = new Object[bills.size()][8];
            for (int i = 0; i < bills.size(); i++) {
                Bill b = bills.get(bills.size() - 1 - i);
                int itemCount = b.getItems().stream().mapToInt(OrderItem::getQty).sum();
                data[i][0] = b.getBillNum();
                data[i][1] = b.getTableName();
                data[i][2] = b.getWaiter();
                data[i][3] = itemCount;
                data[i][4] = String.format("₹%.2f", b.getTax());
                data[i][5] = String.format("₹%.2f", b.getTotal());
                data[i][6] = b.getPayMode();
                data[i][7] = b.getClosedAt().format(DateTimeFormatter.ofPattern("hh:mm a"));
            }
            JTable table = new JTable(data, cols) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
                @Override public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int col) {
                    Component c = super.prepareRenderer(renderer, row, col);
                    c.setBackground(row % 2 == 0 ? Theme.BG_ROW : Theme.BG_ROW_ALT);
                    c.setForeground(col == 0 ? Theme.TEAL : col == 5 ? Theme.GREEN : Theme.TEXT_SECONDARY);
                    return c;
                }
            };
            UIHelper.styleTable(table);
            table.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                    int row = table.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        Bill b = bills.get(bills.size() - 1 - row);
                        BillDialog.show(SwingUtilities.getWindowAncestor(ReportsPanel.this), b);
                    }
                }
            });
            table.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JScrollPane scroll = UIHelper.scrollPane(table);
            scroll.setPreferredSize(new Dimension(0, Math.min(280, bills.size() * 33 + 35)));
            billLogPanel.add(scroll, BorderLayout.CENTER);
        }
        billLogPanel.revalidate(); billLogPanel.repaint();
    }
}
