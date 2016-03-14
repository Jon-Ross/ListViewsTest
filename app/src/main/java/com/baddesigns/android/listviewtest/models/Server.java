package com.baddesigns.android.listviewtest.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jon-Ross on 13/03/2016.
 */
public class Server {

    public static final int TRANSFERRED_ALL = 0;
    public static final int MAX_QUANTITY_REACHED = 1;
    public static final int OUT_OF_STOCK = 2;

    private static Server sServer;
    private List<Item> mAllItems;
    private List<Item> mUserItems;

    public static Server getInstance() {
        if(sServer == null) {
            sServer = new Server();
        }
        return sServer;
    }

    private Server() {
        // load user items
        mUserItems = new ArrayList<>();
        mUserItems.add(new Item("Steak", 565.46, 45));
        mUserItems.add(new Item("Sony Plasma TV", 435.73, 20));
        mUserItems.add(new Item("Batman vs Superman Bluray", 234.65, 30));
        mUserItems.add(new Item("PS5", 12.5, 12));
        mUserItems.add(new Item("Chicken Wings", 23.46, 83));
        mUserItems.add(new Item("Europa League Cup", 675.3, 5));
        mUserItems.add(new Item("4th Place", 8965.79, 1));

        // load all items
        mAllItems = new ArrayList<>();
        mAllItems.add(new Item("Beef", 2.99, 5));
        mAllItems.add(new Item("Chocolate", 12.5, 3));
        mAllItems.add(new Item("Bananas", 12.5, 27));
        mAllItems.add(new Item("Steak", 565.46, 50));
        mAllItems.add(new Item("Sony Plasma TV", 435.73, 34));
        mAllItems.add(new Item("Batman vs Superman Bluray", 234.65, 45));
        mAllItems.add(new Item("BMX Bike", 6346.5, 2));
        mAllItems.add(new Item("Chicken Wings", 23.46, 85));
        mAllItems.add(new Item("Europa League Cup", 675.3, 56));
        mAllItems.add(new Item("4th Place", 8965.79, 77));
        mAllItems.add(new Item("Christmas Tree", 56.7, 0));
        mAllItems.add(new Item("PS5", 12.5, 27));
        mAllItems.add(new Item("Street Fighter 7 Turbo Ultra Edition", 565.46, 50));
        mAllItems.add(new Item("Laser Eye Surgery", 435.73, 34));
        mAllItems.add(new Item("Frosted Shreddies", 234.65, 1));
        mAllItems.add(new Item("Surround Sound Speakers", 56.7, 3));
        mAllItems.add(new Item("XBoxTwo", 12.5, 27));
        mAllItems.add(new Item("Halo 9", 565.46, 50));
        mAllItems.add(new Item("Thai Green Curry", 435.73, 34));
        mAllItems.add(new Item("Monopoly", 234.65, 2));
    }

    public void copyUserItemsToList(List<Item> items) {
        if(items.size() > 0) {
            items.clear();
        }
        for(Item item : mUserItems) {
            items.add(Item.copyItem(item));
        }
    }

    public void updateSearchItems(List<Item> items, int space) {
        int size = mAllItems.size();
        items.clear();
        for(int i = space - 1; i < size; i += space) {
            items.add(mAllItems.get(i));
        }
    }

    public int getQuantityOfItemInUserList(Item item) {
        int index = mUserItems.indexOf(item);
        if(index != -1) {
            return mUserItems.get(index).getQuantity();
        }
        return -1;
    }

    /**
     *
     * @param position of item in list
     * @param increment amount to increase item by
     * @return true if all items were added
     */
    public boolean increaseQuantityOfUserListItem(int position, int increment) {
        Item item = mUserItems.get(position);
        int quantity = item.getQuantity();
        quantity += increment;
        if(quantity > Item.MAX_QUANTITY) {
            item.setQuantity(Item.MAX_QUANTITY);
            return false;
        } else {
            item.setQuantity(quantity);
            return true;
        }
    }

    /**
     *
     * @param item in list
     * @param increment amount to increase item by
     * @return true if all items were added
     */
    public boolean increaseQuantityOfUserListItem(Item item, int increment) {
        int position = mUserItems.indexOf(item);
        if(position != -1) {
            return increaseQuantityOfUserListItem(position, increment);
        } else {
            mUserItems.add(Item.copyOneItem(item));
            return true;
        }
    }

    /**
     *
     * @param position of item in list
     * @param decrement amount to decrease item by
     * @return true if item is still in list
     */
    public boolean decreaseQuantityOfUserListItem(int position, int decrement) {
        Item item = mUserItems.get(position);
        int quantity = item.getQuantity();
        quantity -= decrement;
        if(quantity < 1) {
            mUserItems.remove(item);
            return false;
        } else {
            item.setQuantity(quantity);
            return true;
        }
    }

    /**
     *
     * @param item in list
     * @param decrement amount to decrease item by
     * @return true if item is still in list
     */
    public boolean decreaseQuantityOfUserListItem(Item item, int decrement) {
        int position = mUserItems.indexOf(item);
        return decreaseQuantityOfUserListItem(position, decrement);
    }

    /**
     *
     * @param databaseItem
     * @param quantity
     * @return true if all items were added
     */
    public int transferQuantityFromAllToUserListItem(
            Item databaseItem, int quantity) {
        int dbQuantity = databaseItem.getQuantity();
        if(dbQuantity < 1) {
            return OUT_OF_STOCK;
        }
        if(dbQuantity < quantity) {
            boolean addedAll = increaseQuantityOfUserListItem(databaseItem, dbQuantity);
            if(addedAll) {
                return OUT_OF_STOCK;
            } else {
                return MAX_QUANTITY_REACHED;
            }
        }
        boolean addedAll = increaseQuantityOfUserListItem(databaseItem, quantity);
        if(addedAll) {
            return TRANSFERRED_ALL;
        } else {
            return MAX_QUANTITY_REACHED;
        }
    }

    public int transferQuantityFromUserListToAllItem(
            Item userListItem, int quantity) {
        int userListItemQuantity = userListItem.getQuantity();
        if(userListItemQuantity < 1) {
            return OUT_OF_STOCK;
        }
        boolean removedAll = !decreaseQuantityOfUserListItem(
                userListItem, quantity);
        if(removedAll) {
            return TRANSFERRED_ALL;
        } else {
            return OUT_OF_STOCK;
        }
    }


    // ************************** GETTERS AND SETTERS **************************

    public List<Item> getAllItems() {
        return mAllItems;
    }

    public List<Item> getUserItems() {
        return mUserItems;
    }

    public boolean isItemInUserList(Item item) {
        return mUserItems.contains(item);
    }

    public void addItemToUserList(Item item) {
        mUserItems.add(item);
    }

    public Item getItemFromUserList(Item item) {
        int index = mUserItems.indexOf(item);
        return index == -1 ? null : mUserItems.get(index);
    }

    public Item removeItemFromUserList(int i) {
        return mUserItems.remove(i);
    }

    public boolean removeItemFromUserList(Item item) {
        return mUserItems.remove(item);
    }
}
