package com.example.bananabargains;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.room.Room;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.bananabargains.DB.AppDatabase;
import com.example.bananabargains.DB.BananaBargainsDAO;
import com.example.bananabargains.DB.User;
import com.example.bananabargains.databinding.ActivityAdminLandingBinding;

public class AdminLanding extends AppCompatActivity {
    private static final String USER_ID_KEY = "com.example.bananabargains.adminUserIdKey";
    private static final String PREFERENCES_KEY = "com.example.bananabargains.ADMIN_PREFERENCES_KEY";
    private ActivityAdminLandingBinding binding;
    private AppCompatButton mLogoutAdminButton;
    private AppCompatButton mAddProductButton;
    private TextView mMainAdminUsername;
    private int mUserId = -1;
    private SharedPreferences mPreferences = null;
    private User mUser;
    private BananaBargainsDAO mBananaBargainsDAO;

    private TextView mAdminLanding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_landing);

        binding = ActivityAdminLandingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getDatabase();

        //TODO: Get main display widgets and display them
        mAdminLanding = binding.adminBananaBargainsDisplay;
        mLogoutAdminButton = binding.userLogoutButton;
        mAddProductButton = binding.adminAddProductButton;
        mMainAdminUsername = binding.mainAdminUsername;

        mLogoutAdminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
        mAddProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AddBananas.intentFactory(getApplicationContext());
                startActivity(intent);
            }
        });
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

    private void addUserToPreference(int userId) {
        if (mPreferences == null) {
            getPrefs();
        }
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(USER_ID_KEY, userId);
        editor.apply();
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

    public static Intent intentFactory(Context context, int userId){
        Intent intent = new Intent(context, AdminLanding.class);
        intent.putExtra(USER_ID_KEY, userId);

        return intent;
    }
    private void clearUserFromIntent(){
        getIntent().putExtra(USER_ID_KEY, -1);
    }
    private void clearUserFromPref() {
        addUserToPreference(-1);
    }
}