package com.restaurantpos.ui;

import com.restaurantpos.data.*;
import com.restaurantpos.model.*;
import com.restaurantpos.util.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class FloorPanel extends JPanel {
    private final POSStore store = POSStore.getInstance();
    private final MainFrame mainFrame;
    private final CardLayout cardLayout = new CardLayout();

    private FloorGridPanel floorGrid;
    private OrderPanel orderPanel;

    public FloorPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(cardLayout);
        setBackground(Theme.BG_DARKEST);

        floorGrid  = new FloorGridPanel(this);
        orderPanel = new OrderPanel(this);

        add(floorGrid,  "grid");
        add(orderPanel, "order");

        store.addListener(this::refresh);
    }

    public void refresh() {
        floorGrid.refresh();
    }

    public void openOrderView(int tableId) {
        orderPanel.loadTable(tableId);
        cardLayout.show(this, "order");
    }

    public void showFloor() {
        cardLayout.show(this, "grid");
        floorGrid.refresh();
    }

    // ==============================
    // FLOOR GRID
    // ==============================
    static class FloorGridPanel extends JPanel {
        private final FloorPanel parent;
        private final POSStore store = POSStore.getInstance();

        FloorGridPanel(FloorPanel parent) {
            this.parent = parent;
            setBackground(Theme.BG_DARKEST);
            setLayout(new BorderLayout());
            build();
        }

        void build() {
            removeAll();

            // Header
            JPanel header = new JPanel(new BorderLayout());
            header.setOpaque(false);
            header.setBorder(BorderFactory.createEmptyBorder(20, 24, 12, 24));

            JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            titleRow.setOpaque(false);
            JLabel title = UIHelper.label("Floor Plan", Theme.FONT_TITLE, Theme.TEXT_PRIMARY);
            JLabel sub   = UIHelper.label("  ·  Click a table to open its folio", Theme.FONT_SMALL, Theme.TEXT_MUTED);
            titleRow.add(title); titleRow.add(sub);

            // Legend
            JPanel legend = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
            legend.setOpaque(false);
            addLegend(legend, Theme.TABLE_VACANT_BG, Theme.GREEN, "Vacant");
            addLegend(legend, Theme.TABLE_OCCUPIED_BG, Theme.TEAL, "Occupied");
            addLegend(legend, Theme.TABLE_BILL_BG, Theme.AMBER, "Bill Req.");

            header.add(titleRow, BorderLayout.WEST);
            header.add(legend, BorderLayout.EAST);

            // Sections
            JPanel sectionsPanel = new JPanel();
            sectionsPanel.setBackground(Theme.BG_DARKEST);
            sectionsPanel.setLayout(new BoxLayout(sectionsPanel, BoxLayout.Y_AXIS));
            sectionsPanel.setBorder(BorderFactory.createEmptyBorder(0, 24, 24, 24));

            for (String section : new String[]{"Indoor", "Outdoor", "Private"}) {
                sectionsPanel.add(buildSection(section));
                sectionsPanel.add(Box.createVerticalStrut(20));
            }

            add(header, BorderLayout.NORTH);
            add(UIHelper.scrollPane(sectionsPanel), BorderLayout.CENTER);
            revalidate(); repaint();
        }

        private JPanel buildSection(String section) {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setOpaque(false);

            JLabel sectionLabel = UIHelper.label(section.toUpperCase() + " SECTION",
                new Font("Segoe UI", Font.BOLD, 10), Theme.TEXT_MUTED);
            sectionLabel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, Theme.BORDER),
                BorderFactory.createEmptyBorder(0, 0, 8, 0)
            ));

            JPanel grid = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
            grid.setOpaque(false);
            grid.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

            List<Table> tables = MenuData.getTables();
            for (Table t : tables) {
                if (t.getSection().equals(section)) {
                    grid.add(buildTableCard(t));
                }
            }

            panel.add(sectionLabel, BorderLayout.NORTH);
            panel.add(grid, BorderLayout.CENTER);
            return panel;
        }

        private JPanel buildTableCard(Table table) {
            TableSession sess = store.getSession(table.getId());
            TableSession.Status status = sess != null ? sess.getStatus() : null;

            final Color bg = (sess == null) ? Theme.TABLE_VACANT_BG
                    : (status == TableSession.Status.BILL_REQUESTED) ? Theme.TABLE_BILL_BG
                    : Theme.TABLE_OCCUPIED_BG;
            final Color bd = (sess == null) ? Theme.TABLE_VACANT_BD
                    : (status == TableSession.Status.BILL_REQUESTED) ? Theme.TABLE_BILL_BD
                    : Theme.TABLE_OCCUPIED_BD;
            final Color txt = (sess == null) ? Theme.GREEN
                    : (status == TableSession.Status.BILL_REQUESTED) ? Theme.AMBER
                    : Theme.TEAL;
            final String statusLabel = (sess == null) ? "Vacant"
                    : (status == TableSession.Status.BILL_REQUESTED) ? "Bill Req."
                    : "Occupied";

            JPanel card = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    g2.setColor(bd);
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                    g2.dispose();
                }
                @Override public boolean isOpaque() { return false; }
            };
            card.setBackground(bg);
            card.setBorder(new LineBorder(bd, 1, true));
            card.setLayout(new BorderLayout(0, 4));
            card.setPreferredSize(new Dimension(148, sess != null ? 110 : 86));
            card.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
            card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            // Top row
            JPanel top = new JPanel(new BorderLayout());
            top.setOpaque(false);
            JLabel nameLabel = UIHelper.label(table.getName(), new Font("Segoe UI", Font.BOLD, 17), txt);
            JLabel statusPill = UIHelper.label(statusLabel, Theme.FONT_TINY, txt);
            statusPill.setOpaque(true);
            statusPill.setBackground(bd);
            statusPill.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
            top.add(nameLabel, BorderLayout.WEST);
            top.add(statusPill, BorderLayout.EAST);

            JLabel seatsLabel = UIHelper.label(table.getSeats() + " seats", Theme.FONT_TINY, Theme.TEXT_MUTED);

            card.add(top, BorderLayout.NORTH);
            card.add(seatsLabel, BorderLayout.CENTER);

            if (sess != null) {
                JPanel bottom = new JPanel(new GridLayout(3, 1, 0, 1));
                bottom.setOpaque(false);
                bottom.setBorder(new MatteBorder(1, 0, 0, 0, bd));
                bottom.add(UIHelper.label(sess.getWaiter(), Theme.FONT_TINY, Theme.TEXT_SECONDARY));
                bottom.add(UIHelper.label(String.format("₹%.0f", sess.getGrandTotal()), Theme.FONT_SUBHEAD, txt));
                bottom.add(UIHelper.label(sess.getItems().size() + " items · " + sess.getKotCount() + " KOTs", Theme.FONT_TINY, Theme.TEXT_MUTED));
                card.add(bottom, BorderLayout.SOUTH);
            }

            card.addMouseListener(new MouseAdapter() {
                Color originalBg = bg;
                @Override public void mouseEntered(MouseEvent e) { card.setBackground(bg.brighter()); card.repaint(); }
                @Override public void mouseExited(MouseEvent e)  { card.setBackground(originalBg); card.repaint(); }
                @Override public void mouseClicked(MouseEvent e) { parent.openOrderView(table.getId()); }
            });

            return card;
        }

        private void addLegend(JPanel panel, Color bg, Color txt, String label) {
            JPanel dot = new JPanel();
            dot.setBackground(bg);
            dot.setBorder(new LineBorder(txt, 1));
            dot.setPreferredSize(new Dimension(10, 10));
            panel.add(dot);
            panel.add(UIHelper.label(label, Theme.FONT_TINY, Theme.TEXT_MUTED));
        }

        void refresh() { build(); }
    }
}
