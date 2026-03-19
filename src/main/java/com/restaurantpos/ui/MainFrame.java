package com.restaurantpos.ui;

import com.restaurantpos.data.POSStore;
import com.restaurantpos.util.Theme;
import com.restaurantpos.util.UIHelper;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainFrame extends JFrame {
    private final POSStore store = POSStore.getInstance();
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);

    private FloorPanel floorPanel;
    private KitchenPanel kitchenPanel;
    private ReportsPanel reportsPanel;

    private JLabel occupancyLabel;
    private JLabel billsLabel;
    private JLabel clockLabel;

    private JButton btnFloor, btnKitchen, btnReports;

    public MainFrame() {
        setTitle("FolioDesk POS — Restaurant Suite");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BG_DARKEST);
        setLayout(new BorderLayout());

        add(buildNavBar(), BorderLayout.NORTH);
        buildContent();
        add(contentPanel, BorderLayout.CENTER);

        store.addListener(this::refreshStats);
        startClock();
        refreshStats();
    }

    private JPanel buildNavBar() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(Theme.BG_DARK);
        nav.setBorder(new MatteBorder(0, 0, 1, 0, Theme.BORDER));
        nav.setPreferredSize(new Dimension(0, 52));

        // LEFT: Logo + Nav buttons
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        left.setOpaque(false);

        // Logo
        JPanel logo = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        logo.setOpaque(false);
        JLabel iconLabel = new JLabel("🍽") {{ setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20)); }};
        JPanel logoText = new JPanel(new GridLayout(2, 1, 0, 0));
        logoText.setOpaque(false);
        logoText.add(UIHelper.label("FolioDesk POS", Theme.FONT_SUBHEAD, Theme.TEXT_PRIMARY));
        logoText.add(UIHelper.label("FOOD HOTEL SUITE", new Font("Segoe UI", Font.BOLD, 9), Theme.TEXT_MUTED));
        logo.add(iconLabel);
        logo.add(logoText);

        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setForeground(Theme.BORDER);
        sep.setPreferredSize(new Dimension(1, 28));

        btnFloor   = navBtn("🪑  Floor Plan");
        btnKitchen = navBtn("🧑‍🍳  Kitchen");
        btnReports = navBtn("📊  Reports");

        btnFloor.addActionListener(e -> showView("floor"));
        btnKitchen.addActionListener(e -> showView("kitchen"));
        btnReports.addActionListener(e -> showView("reports"));

        left.add(logo);
        left.add(sep);
        left.add(btnFloor);
        left.add(btnKitchen);
        left.add(btnReports);

        // RIGHT: Stats + Clock
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 14));
        right.setOpaque(false);

        occupancyLabel = UIHelper.label("Tables: 0/12", Theme.FONT_SMALL, Theme.TEXT_MUTED);
        billsLabel     = UIHelper.label("Bills: 0", Theme.FONT_SMALL, Theme.TEXT_MUTED);
        clockLabel     = UIHelper.label("", Theme.FONT_SMALL, Theme.TEXT_MUTED);

        right.add(occupancyLabel);
        right.add(UIHelper.label("·", Theme.FONT_SMALL, Theme.BORDER));
        right.add(billsLabel);
        right.add(UIHelper.label("·", Theme.FONT_SMALL, Theme.BORDER));
        right.add(clockLabel);

        nav.add(left, BorderLayout.WEST);
        nav.add(right, BorderLayout.EAST);
        return nav;
    }

    private JButton navBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(Theme.FONT_SMALL);
        btn.setForeground(Theme.TEXT_SECONDARY);
        btn.setBackground(Theme.BG_DARK);
        btn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(0,0,0,0), 1, true),
            BorderFactory.createEmptyBorder(4, 12, 4, 12)
        ));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void setActiveNav(JButton active) {
        for (JButton btn : new JButton[]{btnFloor, btnKitchen, btnReports}) {
            btn.setForeground(btn == active ? Theme.TEAL : Theme.TEXT_SECONDARY);
            btn.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, btn == active ? 2 : 0, 0, Theme.TEAL),
                BorderFactory.createEmptyBorder(4, 12, btn == active ? 2 : 4, 12)
            ));
        }
    }

    private void buildContent() {
        contentPanel.setBackground(Theme.BG_DARKEST);

        floorPanel   = new FloorPanel(this);
        kitchenPanel = new KitchenPanel();
        reportsPanel = new ReportsPanel();

        contentPanel.add(floorPanel, "floor");
        contentPanel.add(kitchenPanel, "kitchen");
        contentPanel.add(reportsPanel, "reports");

        showView("floor");
    }

    public void showView(String view) {
        cardLayout.show(contentPanel, view);
        switch (view) {
            case "floor"   -> { setActiveNav(btnFloor);   floorPanel.refresh(); }
            case "kitchen" -> { setActiveNav(btnKitchen); kitchenPanel.refresh(); }
            case "reports" -> { setActiveNav(btnReports); reportsPanel.refresh(); }
        }
    }

    public void openOrderView(int tableId) {
        // Show order panel inside floor
        floorPanel.openOrderView(tableId);
    }

    private void refreshStats() {
        int occupied = store.getSessions().size();
        int total = 12;
        occupancyLabel.setText("Tables: " + occupied + "/" + total);
        billsLabel.setText("Bills Today: " + store.getTodayBills().size());
    }

    private void startClock() {
        Timer timer = new Timer(1000, e -> {
            clockLabel.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM  hh:mm a")));
        });
        timer.start();
        timer.getActionListeners()[0].actionPerformed(null);
    }
}
