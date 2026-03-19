package com.restaurantpos.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Bill {
    private final String billNum;
    private final int tableId;
    private final String tableName;
    private final String waiter;
    private final List<OrderItem> items;
    private final double subtotal;
    private final double tax;
    private final double total;
    private final String payMode;
    private final LocalDateTime closedAt;

    public Bill(String billNum, int tableId, String tableName, String waiter,
                List<OrderItem> items, double subtotal, double tax, double total, String payMode) {
        this.billNum = billNum;
        this.tableId = tableId;
        this.tableName = tableName;
        this.waiter = waiter;
        this.items = items;
        this.subtotal = subtotal;
        this.tax = tax;
        this.total = total;
        this.payMode = payMode;
        this.closedAt = LocalDateTime.now();
    }

    public String getBillNum() { return billNum; }
    public int getTableId() { return tableId; }
    public String getTableName() { return tableName; }
    public String getWaiter() { return waiter; }
    public List<OrderItem> getItems() { return items; }
    public double getSubtotal() { return subtotal; }
    public double getTax() { return tax; }
    public double getTotal() { return total; }
    public String getPayMode() { return payMode; }
    public LocalDateTime getClosedAt() { return closedAt; }
    public String getClosedAtFormatted() {
        return closedAt.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a"));
    }
}
