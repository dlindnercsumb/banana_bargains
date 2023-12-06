package com.example.bananabargains;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.bananabargains.DB.AppDatabase;
import com.example.bananabargains.DB.Banana;
import com.example.bananabargains.DB.BananaBargainsDAO;
import com.example.bananabargains.DB.User;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.MenuInflater;
import android.view.View;

import com.example.bananabargains.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private static final String USER_ID_KEY = "com.example.bananabargains.userIdKey";
    private static final String PREFERENCES_KEY = "com.example.bananabargains.PREFERENCES_KEY";
    private BananaBargainsDAO mBananaBargainsDAO;
    private RecyclerView mMainDisplay;
    private TextView mMainUsername;
    private AppCompatButton mLogoutButton;
    private AppCompatButton mBuyMembershipButton;

    //Info to login user
    private int mUserId = -1;
    private SharedPreferences mPreferences = null;
    private User mUser;
    private List<Banana> mBananaList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getDatabase();

        insertDefaultBananas();

        checkForUser();

        loginUser(mUserId);

        // Get main display widgets and display them
        mMainDisplay = binding.mainBananaBargainsDisplay;
        mLogoutButton = binding.userLogoutButton;
        mBuyMembershipButton = binding.userBuyMembershipButton;
        mMainUsername = binding.mainUsername;

        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        mBuyMembershipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Membership.intentFactory(getApplicationContext());
                startActivity(intent);
            }
        });

        refreshDisplay();

        //Later method to add banana to cart
//        mAddBananaToCart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                addBananaToCart();
//                refreshDisplay();
//            }
//        });
    }
    public static Intent intentFactory(Context context, int userId){
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(USER_ID_KEY, userId);

        return intent;
    }

    private void getDatabase() {
        mBananaBargainsDAO= Room.databaseBuilder(this, AppDatabase.class, AppDatabase.DATABASE_NAME)
                .allowMainThreadQueries()
                .build()
                .BananaBargainsDAO();
    }

    private void getPrefs() {
        mPreferences = this.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
    }

    private void checkForUser() {
        mUserId = getIntent().getIntExtra(USER_ID_KEY, -1);

        //do we have a user in the preferences?
        if (mUserId != -1) {
            return;
        }


        if (mPreferences == null) {
            getPrefs();
        }

        mUserId = mPreferences.getInt(USER_ID_KEY, -1);

        if (mUserId != -1) {
            return;
        }

        //do we have any users at all?
        List<User> users = mBananaBargainsDAO.getAllUsers();
        if (users.size() <= 0) {
            //hasMembrship & isAdmin are 1 for true since Room doesn't store booleans
            User defaultUser = new User("Admin1", "admin123", 1, 100.0, 1);
            User altUser = new User("averageConsumer", "ac1234", 0, 50.00, 0);
            mBananaBargainsDAO.insert(defaultUser,altUser);
        }

        Intent intent = LoginActivity.intentFactory(this);
        startActivity(intent);
    }

    private void addUserToPreference(int userId) {
        if (mPreferences == null) {
            getPrefs();
        }
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(USER_ID_KEY, userId);
        editor.apply();
    }

    private void loginUser(int userId) {
        mUser = mBananaBargainsDAO.getUserById(userId);
        addUserToPreference(userId);
        invalidateOptionsMenu();
    }

    /**
     * Sets up display for recyclerview
     */
    private void refreshDisplay(){
        mBananaList = mBananaBargainsDAO.getAllBananas();
        BananaListAdapter buttonPanelAdapter = new BananaListAdapter(this,mBananaList);
        mMainDisplay.setAdapter(buttonPanelAdapter);
        mMainDisplay.setLayoutManager(new LinearLayoutManager(this));
        /*
        mBananaList = mBananaBargainsDAO.getAllBananas();
        if(!mBananaList.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for(Banana banana:  mBananaList) {
                sb.append(banana.toString());
            }
            mMainDisplay.setText(sb.toString());
        } else {
            mMainDisplay.setText(R.string.no_bananas_message);
        }
        */
    }

    /**
     * Inserts 6 default banana items into database if no records in database
     */
    private void insertDefaultBananas() {
        mBananaList = mBananaBargainsDAO.getAllBananas();
        if (mBananaList.size() <= 0) {
            mBananaBargainsDAO.insert(new Banana(1,"Ripe Banana",1.00));
            mBananaBargainsDAO.insert(new Banana(2,"Green Banana",1.00));
            mBananaBargainsDAO.insert(new Banana(3,"Moldy Banana",1.00));
            mBananaBargainsDAO.insert(new Banana(4,"Banana Bread",1.00));
            mBananaBargainsDAO.insert(new Banana(5,"Banana Pudding",1.00));
            mBananaBargainsDAO.insert(new Banana(6,"Banana-Shaped USB Drive",999.99));
        }
    }

    private void logoutUser() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage(R.string.logout);

        alertBuilder.setPositiveButton(getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        clearUserFromIntent();
                        clearUserFromPref();
                        mUserId = -1;
                        checkForUser();
                        Intent intent = LoginActivity.intentFactory(getApplicationContext());
                        startActivity(intent);
                    }
                });
        alertBuilder.setNegativeButton(getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        alertBuilder.create().show();
    }
    private void clearUserFromIntent(){
        getIntent().putExtra(USER_ID_KEY, -1);
    }
    private void clearUserFromPref() {
        addUserToPreference(-1);
    }
}