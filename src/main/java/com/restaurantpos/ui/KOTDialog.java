package com.restaurantpos.ui;

import com.restaurantpos.model.*;
import com.restaurantpos.util.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class KOTDialog extends JDialog {

    private KOTDialog(Window owner, KOT kot) {
        super(owner, "Kitchen Order Ticket", ModalityType.APPLICATION_MODAL);
        setBackground(Theme.BG_DARK);
        setResizable(false);
        buildUI(kot);
        pack();
        setLocationRelativeTo(owner);
    }

    public static void show(Window owner, KOT kot) {
        new KOTDialog(owner, kot).setVisible(true);
    }

    private void buildUI(KOT kot) {
        JPanel main = new JPanel();
        main.setBackground(Theme.BG_DARK);
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        main.setPreferredSize(new Dimension(320, 0));

        // Header
        JPanel header = new JPanel(new GridLayout(3, 1, 0, 4));
        header.setBackground(new Color(10, 22, 40));
        header.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, Theme.BORDER),
            BorderFactory.createEmptyBorder(14, 18, 14, 18)
        ));

        JLabel typeLbl = UIHelper.label("KITCHEN ORDER TICKET", new Font("Consolas", Font.BOLD, 10), Theme.AMBER);
        typeLbl.setAlignmentX(CENTER_ALIGNMENT);
        typeLbl.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel kotNumLbl = UIHelper.label(kot.getKotNum(), new Font("Consolas", Font.BOLD, 22), Theme.AMBER);
        kotNumLbl.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel timeLbl = UIHelper.label(kot.getTimeFormatted(), Theme.FONT_TINY, Theme.TEXT_MUTED);
        timeLbl.setHorizontalAlignment(SwingConstants.CENTER);

        header.add(typeLbl); header.add(kotNumLbl); header.add(timeLbl);

        // Info row
        JPanel infoRow = new JPanel(new GridLayout(1, 2, 0, 0));
        infoRow.setBackground(Theme.BG_DARK);
        infoRow.setBorder(BorderFactory.createEmptyBorder(10, 18, 8, 18));

        JLabel tableInfo = UIHelper.label("Table:  " + kot.getTableName(), Theme.FONT_SMALL, Theme.TEXT_SECONDARY);
        JLabel waiterInfo = UIHelper.label("Waiter:  " + kot.getWaiter(), Theme.FONT_SMALL, Theme.TEXT_SECONDARY);
        waiterInfo.setHorizontalAlignment(SwingConstants.RIGHT);
        infoRow.add(tableInfo); infoRow.add(waiterInfo);

        // Items
        JPanel itemsPanel = new JPanel();
        itemsPanel.setBackground(Theme.BG_DARK);
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(1, 0, 1, 0, new Color(45, 63, 107, 80)),
            BorderFactory.createEmptyBorder(8, 18, 8, 18)
        ));

        for (OrderItem oi : kot.getItems()) {
            JPanel row = new JPanel(new BorderLayout(8, 0));
            row.setBackground(Theme.BG_DARK);
            row.setBorder(new MatteBorder(0, 0, 1, 0, new Color(30, 42, 74, 60)));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

            JLabel nameLabel = UIHelper.label(oi.getMenuItem().getName(), Theme.FONT_BODY, Theme.TEXT_PRIMARY);
            JLabel qtyLabel  = UIHelper.label("×  " + oi.getQty(), new Font("Consolas", Font.BOLD, 16), Theme.AMBER);
            qtyLabel.setHorizontalAlignment(SwingConstants.RIGHT);

            row.add(nameLabel, BorderLayout.CENTER);
            row.add(qtyLabel, BorderLayout.EAST);
            row.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, Theme.BORDER),
                BorderFactory.createEmptyBorder(6, 0, 6, 0)
            ));
            itemsPanel.add(row);
        }

        // Close button
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(Theme.BG_DARK);
        JButton closeBtn = UIHelper.primaryBtn("Close");
        closeBtn.setPreferredSize(new Dimension(180, 38));
        closeBtn.addActionListener(e -> dispose());
        btnPanel.add(closeBtn);

        main.add(header);
        main.add(infoRow);
        main.add(itemsPanel);
        main.add(btnPanel);

        setContentPane(main);
    }
}
