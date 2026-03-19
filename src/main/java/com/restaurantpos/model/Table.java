package com.restaurantpos.model;

public class Table {
    private final int id;
    private final String name;
    private final int seats;
    private final String section;

    public Table(int id, String name, int seats, String section) {
        this.id = id;
        this.name = name;
        this.seats = seats;
        this.section = section;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getSeats() { return seats; }
    public String getSection() { return section; }
}
