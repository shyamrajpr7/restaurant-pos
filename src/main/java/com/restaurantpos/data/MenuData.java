package com.restaurantpos.data;

import com.restaurantpos.model.MenuItem;
import com.restaurantpos.model.Table;

import java.util.*;

public class MenuData {

    public static final String[] WAITERS = {"Rajan", "Priya", "Suresh", "Meena", "Arjun"};

    public static List<Table> getTables() {
        return Arrays.asList(
            new Table(1, "T-01", 2, "Indoor"),
            new Table(2, "T-02", 4, "Indoor"),
            new Table(3, "T-03", 4, "Indoor"),
            new Table(4, "T-04", 6, "Indoor"),
            new Table(5, "T-05", 2, "Indoor"),
            new Table(6, "T-06", 4, "Indoor"),
            new Table(7, "T-07", 8, "Outdoor"),
            new Table(8, "T-08", 4, "Outdoor"),
            new Table(9, "T-09", 2, "Outdoor"),
            new Table(10, "T-10", 4, "Outdoor"),
            new Table(11, "T-11", 6, "Private"),
            new Table(12, "T-12", 10, "Private")
        );
    }

    public static LinkedHashMap<String, List<MenuItem>> getMenu() {
        LinkedHashMap<String, List<MenuItem>> menu = new LinkedHashMap<>();

        menu.put("Starters", Arrays.asList(
            new MenuItem("s1", "Veg Spring Rolls", 120, 5, "Starters"),
            new MenuItem("s2", "Chicken 65", 220, 5, "Starters"),
            new MenuItem("s3", "Paneer Tikka", 180, 5, "Starters"),
            new MenuItem("s4", "Fish Fry", 260, 5, "Starters"),
            new MenuItem("s5", "Onion Rings", 90, 5, "Starters")
        ));

        menu.put("Main Course", Arrays.asList(
            new MenuItem("m1", "Butter Chicken", 320, 5, "Main Course"),
            new MenuItem("m2", "Palak Paneer", 240, 5, "Main Course"),
            new MenuItem("m3", "Biryani (Chicken)", 280, 5, "Main Course"),
            new MenuItem("m4", "Kerala Fish Curry", 340, 5, "Main Course"),
            new MenuItem("m5", "Dal Makhani", 200, 5, "Main Course"),
            new MenuItem("m6", "Mutton Rogan Josh", 380, 5, "Main Course")
        ));

        menu.put("Breads & Rice", Arrays.asList(
            new MenuItem("b1", "Butter Naan", 40, 5, "Breads & Rice"),
            new MenuItem("b2", "Tandoori Roti", 30, 5, "Breads & Rice"),
            new MenuItem("b3", "Jeera Rice", 120, 5, "Breads & Rice"),
            new MenuItem("b4", "Parotta", 25, 5, "Breads & Rice")
        ));

        menu.put("Beverages", Arrays.asList(
            new MenuItem("bv1", "Masala Chai", 30, 0, "Beverages"),
            new MenuItem("bv2", "Fresh Lime Soda", 60, 0, "Beverages"),
            new MenuItem("bv3", "Mango Lassi", 80, 0, "Beverages"),
            new MenuItem("bv4", "Cold Coffee", 90, 0, "Beverages"),
            new MenuItem("bv5", "Mineral Water", 25, 0, "Beverages")
        ));

        menu.put("Desserts", Arrays.asList(
            new MenuItem("d1", "Gulab Jamun", 80, 0, "Desserts"),
            new MenuItem("d2", "Ice Cream (2 Scoop)", 120, 0, "Desserts"),
            new MenuItem("d3", "Payasam", 60, 0, "Desserts")
        ));

        return menu;
    }

    public static List<MenuItem> getAllItems() {
        List<MenuItem> all = new ArrayList<>();
        getMenu().values().forEach(all::addAll);
        return all;
    }
}
