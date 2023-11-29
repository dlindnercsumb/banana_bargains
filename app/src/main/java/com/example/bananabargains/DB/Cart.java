package com.example.bananabargains.DB;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = AppDatabase.CART_TABLE)
public class Cart {
    @PrimaryKey(autoGenerate = true)
    private int mCartId;
    private int mUserId;
    private int mBananaId;
    private double mTotalPrice;
    private int mProductCount;

    public Cart(int cartId, double totalPrice, int productCount) {
        //Should get userId and bananaId from joining other tables
        this.mCartId = cartId;
        this.mTotalPrice = totalPrice;
        this.mProductCount = productCount;
    }

    public int getCartId() {
        return mCartId;
    }

    public void setCartId(int cartId) {
        mCartId = cartId;
    }

    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int userId) {
        mUserId = userId;
    }

    public int getBananaId() {
        return mBananaId;
    }

    public void setBananaId(int bananaId) {
        mBananaId = bananaId;
    }

    public double getTotalPrice() {
        return mTotalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        mTotalPrice = totalPrice;
    }

    public int getProductCount() {
        return mProductCount;
    }

    public void setProductCount(int productCount) {
        mProductCount = productCount;
    }
}
