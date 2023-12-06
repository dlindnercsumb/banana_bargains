package com.example.bananabargains.DB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BananaBargainsDAO {
    //* --- CRUD for Users ---*//
    @Insert
    void insert(User... user);

    @Update
    void update(User... users);

    @Delete
    void delete(User user);

    //* --- Queries for Users ---*//
    @Query("SELECT * FROM " + AppDatabase.USER_TABLE + " WHERE mUsername = :username")
    User getUserByUsername(String username);

    @Query("SELECT * FROM " + AppDatabase.USER_TABLE)
    List<User> getAllUsers();

    @Query("SELECT * FROM " + AppDatabase.USER_TABLE + " WHERE mUserId =:userId")
    User getUserById(int userId);

    @Query("UPDATE " + AppDatabase.USER_TABLE + " SET mHasMembership = :isMember WHERE mUserId =:userId")
    void updateMembership(int isMember, int userId);
    @Query("UPDATE " + AppDatabase.USER_TABLE + " SET mTotalMoney = :totalMoney WHERE mUserId =:userId")
    void updateMoney(double totalMoney, int userId);

    //* --- CRUD for Cart ---*//
    @Insert
    void insert(Cart... cart);

    @Update
    void update(Cart... carts);

    @Delete
    void delete(Cart cart);

    //* --- Queries for Cart ---*//

    //* --- CRUD for Banana ---*//
    @Insert
    void insert(Banana... Banana);

    @Update
    void update(Banana... bananas);

    @Delete
    void delete(Banana banana);

    //* --- Queries for Banana ---*//
    @Query("SELECT * FROM " + AppDatabase.BANANA_TABLE)
    List<Banana> getAllBananas();
}
