package com.restaurantpos.model;

import java.time.LocalDateTime;

public class OrderItem {
    private final MenuItem menuItem;
    private int qty;
    private boolean printed; // KOT printed
    private final LocalDateTime addedAt;

    public OrderItem(MenuItem menuItem, int qty) {
        this.menuItem = menuItem;
        this.qty = qty;
        this.printed = false;
        this.addedAt = LocalDateTime.now();
    }

    public MenuItem getMenuItem() { return menuItem; }
    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }
    public boolean isPrinted() { return printed; }
    public void setPrinted(boolean printed) { this.printed = printed; }
    public LocalDateTime getAddedAt() { return addedAt; }

    public double getSubtotal() { return menuItem.getPrice() * qty; }
    public double getTaxAmount() { return getSubtotal() * menuItem.getTaxPercent() / 100.0; }
    public double getTotal() { return getSubtotal() + getTaxAmount(); }
}
