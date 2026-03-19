package com.restaurantpos.ui;

import com.restaurantpos.data.POSStore;
import com.restaurantpos.model.*;
import com.restaurantpos.util.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

public class KitchenPanel extends JPanel {
    private final POSStore store = POSStore.getInstance();
    private JPanel kotGrid;

    public KitchenPanel() {
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
        header.add(UIHelper.label("Kitchen Display System", Theme.FONT_TITLE, Theme.TEXT_PRIMARY), BorderLayout.WEST);
        header.add(UIHelper.label("All KOTs — real-time order tracking", Theme.FONT_SMALL, Theme.TEXT_MUTED), BorderLayout.SOUTH);

        // KOT Grid
        kotGrid = new JPanel(new WrapLayout(FlowLayout.LEFT, 14, 14));
        kotGrid.setBackground(Theme.BG_DARKEST);
        kotGrid.setBorder(BorderFactory.createEmptyBorder(0, 24, 24, 24));

        JScrollPane scroll = UIHelper.scrollPane(kotGrid);
        add(header, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    public void refresh() {
        SwingUtilities.invokeLater(() -> {
            kotGrid.removeAll();
            List<KOT> kots = store.getKots();
            if (kots.isEmpty()) {
                JPanel empty = new JPanel(new GridBagLayout());
                empty.setOpaque(false);
                JPanel inner = new JPanel(new GridLayout(2, 1, 0, 8));
                inner.setOpaque(false);
                JLabel icon = UIHelper.label("🧑‍🍳", new Font("Segoe UI Emoji", Font.PLAIN, 36), Theme.TEXT_MUTED);
                icon.setHorizontalAlignment(SwingConstants.CENTER);
                JLabel msg  = UIHelper.label("No KOTs yet today", Theme.FONT_BODY, Theme.TEXT_MUTED);
                msg.setHorizontalAlignment(SwingConstants.CENTER);
                inner.add(icon); inner.add(msg);
                empty.add(inner);
                empty.setPreferredSize(new Dimension(400, 200));
                kotGrid.add(empty);
            } else {
                // newest first
                for (int i = kots.size() - 1; i >= 0; i--) kotGrid.add(buildKOTCard(kots.get(i)));
            }
            kotGrid.revalidate(); kotGrid.repaint();
        });
    }

    private JPanel buildKOTCard(KOT kot) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Theme.BG_DARK);
        card.setBorder(new LineBorder(Theme.BORDER, 1, true));
        card.setPreferredSize(new Dimension(260, 0));

        // KOT Header
        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setBackground(new Color(10, 22, 40));
        hdr.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, Theme.BORDER),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        JPanel hdrLeft = new JPanel(new GridLayout(2, 1, 0, 2));
        hdrLeft.setOpaque(false);
        hdrLeft.add(UIHelper.label(kot.getKotNum(), Theme.FONT_SUBHEAD, Theme.AMBER));
        hdrLeft.add(UIHelper.label(kot.getTableName() + "  ·  " + kot.getWaiter(), Theme.FONT_TINY, Theme.TEXT_MUTED));
        hdr.add(hdrLeft, BorderLayout.WEST);
        hdr.add(UIHelper.label(kot.getTimeFormatted(), Theme.FONT_TINY, Theme.TEXT_MUTED), BorderLayout.EAST);

        // Items
        JPanel items = new JPanel();
        items.setBackground(Theme.BG_DARK);
        items.setLayout(new BoxLayout(items, BoxLayout.Y_AXIS));
        items.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        for (int i = 0; i < kot.getItems().size(); i++) {
            OrderItem oi = kot.getItems().get(i);
            JPanel row = new JPanel(new BorderLayout(6, 0));
            row.setOpaque(false);
            row.setBorder(i < kot.getItems().size() - 1
                ? BorderFactory.createCompoundBorder(new MatteBorder(0, 0, 1, 0, Theme.BORDER), BorderFactory.createEmptyBorder(5, 0, 5, 0))
                : BorderFactory.createEmptyBorder(5, 0, 5, 0));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            row.add(UIHelper.label(oi.getMenuItem().getName(), Theme.FONT_BODY, Theme.TEXT_PRIMARY), BorderLayout.CENTER);
            JLabel qty = UIHelper.label("×" + oi.getQty(), new Font("Consolas", Font.BOLD, 15), Theme.AMBER);
            qty.setPreferredSize(new Dimension(32, 20));
            qty.setHorizontalAlignment(SwingConstants.RIGHT);
            row.add(qty, BorderLayout.EAST);
            items.add(row);
        }

        card.add(hdr, BorderLayout.NORTH);
        card.add(items, BorderLayout.CENTER);
        return card;
    }

    // FlowLayout that wraps
    static class WrapLayout extends FlowLayout {
        WrapLayout(int align, int hgap, int vgap) { super(align, hgap, vgap); }
        @Override public Dimension preferredLayoutSize(Container target) {
            return layoutSize(target, true);
        }
        @Override public Dimension minimumLayoutSize(Container target) {
            return layoutSize(target, false);
        }
        private Dimension layoutSize(Container target, boolean preferred) {
            synchronized (target.getTreeLock()) {
                int targetWidth = target.getSize().width;
                if (targetWidth == 0) targetWidth = Integer.MAX_VALUE;
                int hgap = getHgap(), vgap = getVgap();
                Insets insets = target.getInsets();
                int maxWidth = targetWidth - (insets.left + insets.right + hgap * 2);
                int width = 0, height = 0, rowHeight = 0, rowWidth = 0;
                for (int i = 0; i < target.getComponentCount(); i++) {
                    Component c = target.getComponent(i);
                    if (c.isVisible()) {
                        Dimension d = preferred ? c.getPreferredSize() : c.getMinimumSize();
                        if (rowWidth + d.width > maxWidth) {
                            width = Math.max(width, rowWidth);
                            height += rowHeight + vgap;
                            rowWidth = 0; rowHeight = 0;
                        }
                        rowWidth += d.width + hgap;
                        rowHeight = Math.max(rowHeight, d.height);
                    }
                }
                width = Math.max(width, rowWidth);
                height += rowHeight + insets.top + insets.bottom + vgap * 2;
                return new Dimension(width, height);
            }
        }
    }
}
