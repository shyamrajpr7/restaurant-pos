package com.restaurantpos.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TableSession {
    public enum Status { VACANT, OCCUPIED, BILL_REQUESTED }

    private final int tableId;
    private String waiter;
    private final List<OrderItem> items;
    private Status status;
    private final LocalDateTime startTime;
    private int kotCount;
    private int pax;

    public TableSession(int tableId, String waiter) {
        this.tableId = tableId;
        this.waiter = waiter;
        this.items = new ArrayList<>();
        this.status = Status.OCCUPIED;
        this.startTime = LocalDateTime.now();
        this.kotCount = 0;
        this.pax = 1;
    }

    public void addItem(MenuItem menuItem) {
        for (OrderItem oi : items) {
            if (oi.getMenuItem().getId().equals(menuItem.getId()) && !oi.isPrinted()) {
                oi.setQty(oi.getQty() + 1);
                return;
            }
        }
        items.add(new OrderItem(menuItem, 1));
    }

    public boolean removeItem(OrderItem oi) {
        if (oi.isPrinted()) return false;
        items.remove(oi);
        return true;
    }

    public List<OrderItem> getNewItems() {
        List<OrderItem> newItems = new ArrayList<>();
        for (OrderItem oi : items) if (!oi.isPrinted()) newItems.add(oi);
        return newItems;
    }

    public void markAllPrinted() {
        for (OrderItem oi : items) oi.setPrinted(true);
        kotCount++;
    }

    public double getSubtotal() { return items.stream().mapToDouble(OrderItem::getSubtotal).sum(); }
    public double getTaxTotal() { return items.stream().mapToDouble(OrderItem::getTaxAmount).sum(); }
    public double getGrandTotal() { return getSubtotal() + getTaxTotal(); }

    public int getTableId() { return tableId; }
    public String getWaiter() { return waiter; }
    public void setWaiter(String waiter) { this.waiter = waiter; }
    public List<OrderItem> getItems() { return items; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public int getKotCount() { return kotCount; }
    public int getPax() { return pax; }
    public void setPax(int pax) { this.pax = pax; }
    public String getStartTimeFormatted() {
        return startTime.format(DateTimeFormatter.ofPattern("hh:mm a"));
    }
}
