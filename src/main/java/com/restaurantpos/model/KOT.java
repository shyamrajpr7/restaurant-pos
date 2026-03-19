package com.restaurantpos.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class KOT {
    private final String kotNum;
    private final int tableId;
    private final String tableName;
    private final String waiter;
    private final List<OrderItem> items;
    private final LocalDateTime time;

    public KOT(String kotNum, int tableId, String tableName, String waiter, List<OrderItem> items) {
        this.kotNum = kotNum;
        this.tableId = tableId;
        this.tableName = tableName;
        this.waiter = waiter;
        this.items = items;
        this.time = LocalDateTime.now();
    }

    public String getKotNum() { return kotNum; }
    public int getTableId() { return tableId; }
    public String getTableName() { return tableName; }
    public String getWaiter() { return waiter; }
    public List<OrderItem> getItems() { return items; }
    public String getTimeFormatted() {
        return time.format(DateTimeFormatter.ofPattern("dd-MMM hh:mm a"));
    }
}
