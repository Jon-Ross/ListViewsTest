package com.baddesigns.android.listviewtest.models;

/**
 * Created by Jon-Ross on 13/03/2016.
 */
public class Item {

    public static final int MAX_QUANTITY = 99;

    private String mName;
    private double mPrice;
    private int mQuantity;

    public Item(String name, double price) {
        this(name, price, 1);
    }

    public Item(String name, double price, int quantity) {
        mName = name;
        mPrice = price;
        mQuantity = quantity;
    }

    public static Item copyItem(Item item) {
        return new Item(
                item.getName(),
                item.getPrice(),
                item.getQuantity());
    }

    public static Item copyOneItem(Item item) {
        return new Item(
                item.getName(),
                item.getPrice());
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Item && getName().equals(((Item) o).getName());
    }

    // ************************** GETTERS AND SETTERS **************************

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public double getPrice() {
        return mPrice;
    }

    public void setPrice(double price) {
        mPrice = price;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public void setQuantity(int quantity) {
        mQuantity = quantity;
    }
}
