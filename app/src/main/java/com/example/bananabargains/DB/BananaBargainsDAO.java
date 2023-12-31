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
    @Query("SELECT * FROM " + AppDatabase.CART_TABLE)
    List<Cart> getAllCarts();

    @Query("SELECT * FROM " + AppDatabase.CART_TABLE + " WHERE mUserId = :mUserId")
    List<Cart> getCartByUserId(int mUserId);

    @Query("SELECT * FROM CART_TABLE " +
            "INNER JOIN BANANA_TABLE ON BANANA_TABLE.mBananaId = CART_TABLE.mBananaId " +
            "INNER JOIN USER_TABLE ON USER_TABLE.mUserId = CART_TABLE.mUserId " +
            "WHERE BANANA_TABLE.mBananaId = :mBananaId AND USER_TABLE.mUserId = :mUserId")
    List<Cart> findCartsByBananaIdAndUserId(int mBananaId, int mUserId);

    @Query("SELECT * FROM CART_TABLE " +
            "INNER JOIN BANANA_TABLE ON BANANA_TABLE.mBananaId = CART_TABLE.mBananaId " +
            "INNER JOIN USER_TABLE ON USER_TABLE.mUserId = CART_TABLE.mUserId " +
            "WHERE USER_TABLE.mUserId LIKE :mUserId")
    List<Cart> findCartsByUserId(int mUserId);

    @Query("DELETE FROM CART_TABLE WHERE mBananaId = :mBananaId AND mUserId = :mUserId")
    void deleteCartFromUser(int mUserId, int mBananaId);

    @Query("DELETE FROM CART_TABLE WHERE mUserId = :mUserId")
    void deleteAllCartsFromUser(int mUserId);

    @Query("DELETE FROM CART_TABLE WHERE mBananaId = :mBananaId")
    void deleteCartsByBananaId(int mBananaId);

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

    @Query("SELECT * FROM " + AppDatabase.BANANA_TABLE + " WHERE mBananaId = :mBananaId")
    Banana getBananaById(int mBananaId);

    @Query("SELECT DISTINCT BANANA_TABLE.mBananaId FROM BANANA_TABLE " +
            "INNER JOIN CART_TABLE ON BANANA_TABLE.mBananaId = CART_TABLE.mBananaId " +
            "INNER JOIN USER_TABLE ON CART_TABLE.mUserId = USER_TABLE.mUserId " +
            "WHERE USER_TABLE.mUserId LIKE :mUserId")
    List<Integer> getBananaIdByUserId(int mUserId);

    @Query("SELECT BANANA_TABLE.mBananaId FROM BANANA_TABLE " +
            "INNER JOIN CART_TABLE ON BANANA_TABLE.mBananaId = CART_TABLE.mBananaId " +
            "INNER JOIN USER_TABLE ON CART_TABLE.mUserId = USER_TABLE.mUserId " +
            "WHERE USER_TABLE.mUserId LIKE :mUserId")
    List<Integer> getBananaIdsByUserId(int mUserId);

    @Query("SELECT * FROM BANANA_TABLE " +
            "INNER JOIN CART_TABLE ON BANANA_TABLE.mBananaId = CART_TABLE.mBananaId " +
            "INNER JOIN USER_TABLE ON CART_TABLE.mUserId = USER_TABLE.mUserId " +
            "WHERE USER_TABLE.mUserId LIKE :mUserId")
    List<Banana> getBananasByUserId(int mUserId);
}
