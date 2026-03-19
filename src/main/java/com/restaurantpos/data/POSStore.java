package com.restaurantpos.data;

import com.restaurantpos.model.*;

import java.util.*;

public class POSStore {
    private static POSStore instance;

    private final Map<Integer, TableSession> sessions = new HashMap<>();
    private final List<KOT> kots = new ArrayList<>();
    private final List<Bill> bills = new ArrayList<>();
    private int kotCounter = 1;
    private int billCounter = 1;

    private final List<Runnable> listeners = new ArrayList<>();

    private POSStore() {}

    public static POSStore getInstance() {
        if (instance == null) {
            instance = new POSStore();
            Database.init(); // ← connects and creates tables
        }
        return instance;
    }

    public void addListener(Runnable r) {
        listeners.add(r);
    }

    private void notifyListeners() {
        listeners.forEach(Runnable::run);
    }

    // --- Session Management ---
    public TableSession getSession(int tableId) {
        return sessions.get(tableId);
    }

    public TableSession startSession(int tableId, String waiter) {
        TableSession sess = new TableSession(tableId, waiter);
        sessions.put(tableId, sess);
        notifyListeners();
        return sess;
    }

    public void addItemToSession(int tableId, MenuItem item, String waiter) {
        TableSession sess = sessions.computeIfAbsent(tableId, id -> new TableSession(id, waiter));
        sess.addItem(item);
        notifyListeners();
    }

    public boolean removeItemFromSession(int tableId, OrderItem oi) {
        TableSession sess = sessions.get(tableId);
        if (sess == null)
            return false;
        boolean removed = sess.removeItem(oi);
        notifyListeners();
        return removed;
    }

    public boolean updateQty(int tableId, OrderItem oi, int delta) {
        TableSession sess = sessions.get(tableId);
        if (sess == null || oi.isPrinted())
            return false;
        int newQty = oi.getQty() + delta;
        if (newQty < 1) {
            sess.removeItem(oi);
        } else {
            oi.setQty(newQty);
        }
        notifyListeners();
        return true;
    }

    public KOT fireKOT(int tableId) {
        TableSession sess = sessions.get(tableId);
        if (sess == null)
            return null;
        List<OrderItem> newItems = sess.getNewItems();
        if (newItems.isEmpty())
            return null;

        String kotNum = String.format("KOT-%04d", kotCounter++);
        Table table = MenuData.getTables().stream().filter(t -> t.getId() == tableId).findFirst().orElse(null);
        String tableName = table != null ? table.getName() : "T-" + tableId;

        List<OrderItem> snapshot = new ArrayList<>(newItems);
        KOT kot = new KOT(kotNum, tableId, tableName, sess.getWaiter(), snapshot);
        kots.add(kot);
        Database.saveKOT(kot); // ← saves KOT to database
        sess.markAllPrinted();
        notifyListeners();
        return kot;
    }

    public void requestBill(int tableId) {
        TableSession sess = sessions.get(tableId);
        if (sess != null) {
            sess.setStatus(TableSession.Status.BILL_REQUESTED);
            notifyListeners();
        }
    }

    public Bill settleBill(int tableId, String payMode) {
        TableSession sess = sessions.get(tableId);
        if (sess == null)
            return null;

        Table table = MenuData.getTables().stream().filter(t -> t.getId() == tableId).findFirst().orElse(null);
        String tableName = table != null ? table.getName() : "T-" + tableId;
        String billNum = String.format("BILL-%05d", billCounter++);

        Bill bill = new Bill(billNum, tableId, tableName, sess.getWaiter(),
                new ArrayList<>(sess.getItems()),
                sess.getSubtotal(), sess.getTaxTotal(), sess.getGrandTotal(), payMode);
        bills.add(bill);
        Database.saveBill(bill); // ← saves Bill to database
        sessions.remove(tableId);
        notifyListeners();
        return bill;
    }

    // --- Getters ---
    public Map<Integer, TableSession> getSessions() {
        return sessions;
    }

    public List<KOT> getKots() {
        return kots;
    }

    public List<Bill> getBills() {
        return bills;
    }

    public List<Bill> getTodayBills() {
        String today = java.time.LocalDate.now().toString();
        List<Bill> result = new ArrayList<>();
        for (Bill b : bills) {
            if (b.getClosedAt().toLocalDate().toString().equals(today))
                result.add(b);
        }
        return result;
    }

    public double getTodayRevenue() {
        return getTodayBills().stream().mapToDouble(Bill::getTotal).sum();
    }

    public Map<String, Integer> getTopItems() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (Bill b : getTodayBills()) {
            for (OrderItem oi : b.getItems()) {
                counts.merge(oi.getMenuItem().getName(), oi.getQty(), Integer::sum);
            }
        }
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());
        entries.sort((a, b) -> b.getValue() - a.getValue());
        LinkedHashMap<String, Integer> sorted = new LinkedHashMap<>();
        entries.stream().limit(5).forEach(e -> sorted.put(e.getKey(), e.getValue()));
        return sorted;
    }

    public Map<String, Double> getWaiterSales() {
        Map<String, Double> sales = new LinkedHashMap<>();
        for (Bill b : getTodayBills()) {
            sales.merge(b.getWaiter(), b.getTotal(), Double::sum);
        }
        List<Map.Entry<String, Double>> entries = new ArrayList<>(sales.entrySet());
        entries.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        LinkedHashMap<String, Double> sorted = new LinkedHashMap<>();
        entries.forEach(e -> sorted.put(e.getKey(), e.getValue()));
        return sorted;
    }

    public void reset() {
        sessions.clear();
        kots.clear();
        bills.clear();
        kotCounter = 1;
        billCounter = 1;
        notifyListeners();
    }
}