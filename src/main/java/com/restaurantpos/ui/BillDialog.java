package com.restaurantpos.ui;

import com.restaurantpos.model.*;
import com.restaurantpos.util.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class BillDialog extends JDialog {

    private BillDialog(Window owner, Bill bill) {
        super(owner, "GST Tax Invoice", ModalityType.APPLICATION_MODAL);
        setBackground(Theme.BG_DARK);
        setResizable(false);
        buildUI(bill);
        pack();
        setLocationRelativeTo(owner);
    }

    public static void show(Window owner, Bill bill) {
        new BillDialog(owner, bill).setVisible(true);
    }

    private void buildUI(Bill bill) {
        JPanel main = new JPanel();
        main.setBackground(Theme.BG_DARK);
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setPreferredSize(new Dimension(360, 0));
        main.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        // Header
        JPanel header = new JPanel(new GridLayout(4, 1, 0, 3));
        header.setBackground(new Color(10, 22, 20));
        header.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, Theme.BORDER),
            BorderFactory.createEmptyBorder(12, 18, 12, 18)
        ));

        JLabel typeLbl = UIHelper.label("GST TAX INVOICE", new Font("Consolas", Font.BOLD, 10), Theme.GREEN);
        typeLbl.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel billNum = UIHelper.label(bill.getBillNum(), new Font("Consolas", Font.BOLD, 20), Theme.GREEN);
        billNum.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel gstin   = UIHelper.label("GSTIN: 32ABCDE1234F1Z5", Theme.FONT_TINY, Theme.TEXT_MUTED);
        gstin.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel dateInfo = UIHelper.label(bill.getClosedAtFormatted(), Theme.FONT_TINY, Theme.TEXT_MUTED);
        dateInfo.setHorizontalAlignment(SwingConstants.CENTER);

        header.add(typeLbl); header.add(billNum); header.add(gstin); header.add(dateInfo);

        // Table / waiter info
        JPanel infoRow = new JPanel(new GridLayout(1, 2));
        infoRow.setBackground(Theme.BG_DARK);
        infoRow.setBorder(BorderFactory.createEmptyBorder(10, 18, 6, 18));
        JLabel tableInfo  = UIHelper.label("Table:  " + bill.getTableName(), Theme.FONT_SMALL, Theme.TEXT_SECONDARY);
        JLabel waiterInfo = UIHelper.label("Waiter:  " + bill.getWaiter(), Theme.FONT_SMALL, Theme.TEXT_SECONDARY);
        waiterInfo.setHorizontalAlignment(SwingConstants.RIGHT);
        infoRow.add(tableInfo); infoRow.add(waiterInfo);

        // Items table
        String[] cols = {"Item", "Qty", "Amount"};
        Object[][] data = new Object[bill.getItems().size()][3];
        for (int i = 0; i < bill.getItems().size(); i++) {
            OrderItem oi = bill.getItems().get(i);
            data[i][0] = oi.getMenuItem().getName();
            data[i][1] = oi.getQty();
            data[i][2] = String.format("₹%.2f", oi.getSubtotal());
        }
        JTable table = new JTable(data, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        UIHelper.styleTable(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(180);
        table.getColumnModel().getColumn(1).setPreferredWidth(40);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        JScrollPane scroll = UIHelper.scrollPane(table);
        scroll.setPreferredSize(new Dimension(0, Math.min(200, bill.getItems().size() * 33 + 30)));

        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setBackground(Theme.BG_DARK);
        tableWrapper.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(1, 0, 1, 0, Theme.BORDER),
            BorderFactory.createEmptyBorder(0, 18, 0, 18)
        ));
        tableWrapper.add(scroll, BorderLayout.CENTER);

        // Totals
        JPanel totals = new JPanel(new GridLayout(3, 2, 0, 4));
        totals.setBackground(Theme.BG_DARK);
        totals.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));

        JLabel subLbl  = UIHelper.label("Subtotal", Theme.FONT_SMALL, Theme.TEXT_MUTED);
        JLabel subVal  = UIHelper.label(String.format("₹%.2f", bill.getSubtotal()), Theme.FONT_SMALL, Theme.TEXT_SECONDARY);
        subVal.setHorizontalAlignment(SwingConstants.RIGHT);

        JLabel taxLbl  = UIHelper.label("GST", Theme.FONT_SMALL, Theme.TEXT_MUTED);
        JLabel taxVal  = UIHelper.label(String.format("₹%.2f", bill.getTax()), Theme.FONT_SMALL, Theme.TEXT_SECONDARY);
        taxVal.setHorizontalAlignment(SwingConstants.RIGHT);

        JLabel totLbl  = UIHelper.label("TOTAL", Theme.FONT_HEADING, Theme.TEXT_PRIMARY);
        JLabel totVal  = UIHelper.label(String.format("₹%.2f", bill.getTotal()), Theme.FONT_AMOUNT, Theme.GREEN);
        totVal.setHorizontalAlignment(SwingConstants.RIGHT);

        totals.add(subLbl); totals.add(subVal);
        totals.add(taxLbl); totals.add(taxVal);
        totals.add(totLbl); totals.add(totVal);

        // Pay mode
        JPanel payRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        payRow.setBackground(Theme.BG_DARK);
        JLabel payLbl = UIHelper.label("Payment: " + bill.getPayMode(), Theme.FONT_SMALL, Theme.TEXT_MUTED);
        payRow.add(payLbl);

        // Button
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(Theme.BG_DARK);
        JButton closeBtn = UIHelper.successBtn("Close");
        closeBtn.setPreferredSize(new Dimension(180, 38));
        closeBtn.addActionListener(e -> dispose());
        btnPanel.add(closeBtn);

        main.add(header);
        main.add(infoRow);
        main.add(tableWrapper);
        main.add(totals);
        main.add(payRow);
        main.add(btnPanel);

        setContentPane(main);
    }
}
