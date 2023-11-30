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
import androidx.room.Room;

import android.view.MenuInflater;
import android.view.View;

import com.example.bananabargains.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private static final String USER_ID_KEY = "com.example.bananabargains.userIdKey";
    private static final String PREFERENCES_KEY = "com.example.bananabargains.PREFERENCES_KEY";
    private BananaBargainsDAO mBananaBargainsDAO;
    private TextView mMainDisplay;

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

        checkForUser();

        loginUser(mUserId);

        //TODO: Get main display widgets and display them
        mMainDisplay = binding.mainBananaBargainsDisplay;

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
    //Intent factories allow you to navigate between screens
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mUser != null) {
            MenuItem item = menu.findItem(R.id.logoutMenu);
            item.setTitle(mUser.getUsername());
        }
        return super.onPrepareOptionsMenu(menu);
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
            //hasMembership & isAdmin are 1 for true since Room doesn't store booleans
            User defaultUser = new User("Admin1", "admin123", 1, 100.0, 1);
            User altUser = new User("Average Consumer", "ac123", 0, 50.00, 0);
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

    private void refreshDisplay(){
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
    }

    //Creating the logout option
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_login, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.logoutMenu) {
            logoutUser();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

}