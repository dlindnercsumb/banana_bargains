package com.example.bananabargains.DB;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = AppDatabase.USER_TABLE)
public class User {
    @PrimaryKey(autoGenerate = true)
    private int mUserId;
    private String mUsername;
    private String mPassword;
    private int mIsAdmin;
    private double mTotalMoney;
    private int mHasMembership;

    public User(String username, String password, int isAdmin, double totalMoney, int hasMembership) {
        mUsername = username;
        mPassword = password;
        mIsAdmin = isAdmin;
        mTotalMoney = totalMoney;
        mHasMembership = hasMembership;
    }

    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int userId) {
        mUserId = userId;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public int getIsAdmin() {
        return mIsAdmin;
    }

    public void setIsAdmin(Integer isAdmin) {
        mIsAdmin = isAdmin;
    }

    public double getTotalMoney() {
        return mTotalMoney;
    }

    public void setTotalMoney(Integer totalMoney) {
        mTotalMoney = totalMoney;
    }

    public int getHasMembership() {
        return mHasMembership;
    }

    public void setHasMembership(Integer hasMembership) {
        mHasMembership = hasMembership;
    }
}
