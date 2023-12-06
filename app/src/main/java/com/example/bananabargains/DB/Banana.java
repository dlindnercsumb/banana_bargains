package com.example.bananabargains.DB;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = AppDatabase.BANANA_TABLE)
public class Banana {

    @PrimaryKey(autoGenerate = true)
    private int mBananaId;
    private String mBananaDescription;
    private double mBananaPrice;

    public Banana(String bananaDescription, double bananaPrice) {
        mBananaDescription = bananaDescription;
        mBananaPrice = bananaPrice;
    }

    public int getBananaId() {
        return mBananaId;
    }

    public void setBananaId(int bananaId) {
        mBananaId = bananaId;
    }

    public String getBananaDescription() {
        return mBananaDescription;
    }

    public void setBananaDescription(String bananaDescription) {
        mBananaDescription = bananaDescription;
    }

    public double getBananaPrice() {
        return mBananaPrice;
    }

    public void setBananaPrice(double bananaPrice) {
        mBananaPrice = bananaPrice;
    }
}
