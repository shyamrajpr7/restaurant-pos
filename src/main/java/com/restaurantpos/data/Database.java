package com.restaurantpos.data;

import com.restaurantpos.model.Bill;
import com.restaurantpos.model.KOT;
import java.sql.*;

public class Database {

    private static final String URL = "jdbc:sqlite:pos.db";
    private static Connection conn;

    public static Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(URL);
        }
        return conn;
    }

    public static void init() {
        try (Statement stmt = getConnection().createStatement()) {

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS bills (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            bill_num TEXT,
                            table_name TEXT,
                            waiter TEXT,
                            subtotal REAL,
                            tax REAL,
                            total REAL,
                            pay_mode TEXT,
                            closed_at TEXT
                        )
                    """);

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS bill_items (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            bill_num TEXT,
                            item_name TEXT,
                            price REAL,
                            qty INTEGER,
                            tax_percent REAL
                        )
                    """);

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS kots (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            kot_num TEXT,
                            table_name TEXT,
                            waiter TEXT,
                            created_at TEXT
                        )
                    """);

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS kot_items (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            kot_num TEXT,
                            item_name TEXT,
                            qty INTEGER
                        )
                    """);

            System.out.println("Database ready ✓");

        } catch (SQLException e) {
            System.err.println("DB init error: " + e.getMessage());
        }
    }

    public static void saveBill(Bill bill) {
        String sql = "INSERT INTO bills (bill_num, table_name, waiter, subtotal, tax, total, pay_mode, closed_at) VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, bill.getBillNum());
            ps.setString(2, bill.getTableName());
            ps.setString(3, bill.getWaiter());
            ps.setDouble(4, bill.getSubtotal());
            ps.setDouble(5, bill.getTax());
            ps.setDouble(6, bill.getTotal());
            ps.setString(7, bill.getPayMode());
            ps.setString(8, bill.getClosedAtFormatted());
            ps.executeUpdate();

            // Save bill items
            String itemSql = "INSERT INTO bill_items (bill_num, item_name, price, qty, tax_percent) VALUES (?,?,?,?,?)";
            try (PreparedStatement ps2 = getConnection().prepareStatement(itemSql)) {
                for (var item : bill.getItems()) {
                    ps2.setString(1, bill.getBillNum());
                    ps2.setString(2, item.getMenuItem().getName());
                    ps2.setDouble(3, item.getMenuItem().getPrice());
                    ps2.setInt(4, item.getQty());
                    ps2.setDouble(5, item.getMenuItem().getTaxPercent());
                    ps2.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.err.println("Save bill error: " + e.getMessage());
        }
    }

    public static void saveKOT(KOT kot) {
        String sql = "INSERT INTO kots (kot_num, table_name, waiter, created_at) VALUES (?,?,?,?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, kot.getKotNum());
            ps.setString(2, kot.getTableName());
            ps.setString(3, kot.getWaiter());
            ps.setString(4, kot.getTimeFormatted());
            ps.executeUpdate();

            String itemSql = "INSERT INTO kot_items (kot_num, item_name, qty) VALUES (?,?,?)";
            try (PreparedStatement ps2 = getConnection().prepareStatement(itemSql)) {
                for (var item : kot.getItems()) {
                    ps2.setString(1, kot.getKotNum());
                    ps2.setString(2, item.getMenuItem().getName());
                    ps2.setInt(3, item.getQty());
                    ps2.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.err.println("Save KOT error: " + e.getMessage());
        }
    }
}