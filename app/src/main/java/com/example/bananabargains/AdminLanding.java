package com.example.bananabargains;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.bananabargains.DB.AppDatabase;
import com.example.bananabargains.DB.Banana;
import com.example.bananabargains.DB.BananaBargainsDAO;
import com.example.bananabargains.DB.User;
import com.example.bananabargains.databinding.ActivityAdminLandingBinding;

import java.util.List;

public class AdminLanding extends AppCompatActivity {
    private static final String USER_ID_KEY = "com.example.bananabargains.userIdKey";
    private static final String PREFERENCES_KEY = "com.example.bananabargains.ADMIN_PREFERENCES_KEY";
    private ActivityAdminLandingBinding binding;
    private AppCompatButton mLogoutAdminButton;
    private AppCompatButton mAddProductButton;
    private AppCompatButton mAdminCheckoutButton;
    private TextView mMainAdminUsername;
    private TextView mMainAdminItemsInCart;
    private TextView mMainAdminMoney;
    private int mUserId = -1;
    private SharedPreferences mPreferences = null;
    private User mUser;
    private List<Banana> mBananaList;
    private BananaBargainsDAO mBananaBargainsDAO;

    private RecyclerView mAdminLanding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_landing);

        binding = ActivityAdminLandingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getDatabase();

        checkForUser();

        //TODO: Get main display widgets and display them
        mAdminLanding = binding.adminMainDisplay;
        mLogoutAdminButton = binding.adminLogoutButton;
        mAddProductButton = binding.adminAddProductButton;
        mAdminCheckoutButton = binding.adminCheckoutButton;

        mMainAdminUsername = binding.mainAdminUsername;
        mMainAdminItemsInCart = binding.adminItemsInCartCount;
        mMainAdminMoney = binding.adminMoneyAmount;

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
        mAdminCheckoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = BuyBananas.intentFactory(getApplicationContext());
                intent.putExtra(USER_ID_KEY,mUserId);
                startActivity(intent);
            }
        });

        refreshDisplay();

        setupRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshDisplay();
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


    private void refreshDisplay() {
        //Log.d("AdminLanding", "refreshDisplay: " + mBananaBargainsDAO.getUserById(mUserId).getUsername());
        // refresh username text
        mMainAdminUsername.setText(mBananaBargainsDAO.getUserById(mUserId).getUsername());

        // refresh item count
        int itemCount = mBananaBargainsDAO.findCartsByUserId(mUserId).size();
        //Log.d("ITEM_COUNT", "" + itemCount);
        mMainAdminItemsInCart.setText("" + itemCount);

        // refresh money amount
        String formattedMoney = String.format("$%.2f", mBananaBargainsDAO.getUserById(mUserId).getTotalMoney());
        mMainAdminMoney.setText(formattedMoney);
    }

    private void setupRecyclerView() {

        // refresh recycler view
        mBananaList = mBananaBargainsDAO.getAllBananas();
        MainActivityBananaListAdapter buttonPanelAdapter = new MainActivityBananaListAdapter(this,mBananaList, mUserId);
        mAdminLanding.setAdapter(buttonPanelAdapter);
        mAdminLanding.setLayoutManager(new LinearLayoutManager(this));

        // Set the click listener for the adapter
        buttonPanelAdapter.setOnItemClickListener(new MainActivityBananaListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                refreshDisplay();
            }
        });


    }


}