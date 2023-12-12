package com.example.bananabargains;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bananabargains.DB.AppDatabase;
import com.example.bananabargains.DB.BananaBargainsDAO;
import com.example.bananabargains.DB.User;
import com.example.bananabargains.databinding.ActivityMembershipBinding;

public class Membership extends AppCompatActivity {
    private static final String USER_ID_KEY = "com.example.bananabargains.userIdKey";
    private static final String PREFERENCES_KEY = "com.example.bananabargains.PREFERENCES_KEY";
    private BananaBargainsDAO mBananaBargainsDAO;
    private int mUserId;
    private User mUser;
    private AppCompatButton mConfirmBuyMembershipButton;
    private ActivityMembershipBinding binding;
    private SharedPreferences mPreferences = null;

    private TextView mMembershipUsername;
    private TextView mMembershipItemCount;
    private TextView mMembershipMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership);

        binding = ActivityMembershipBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mConfirmBuyMembershipButton = binding.confirmBuyMembershipButton;
        mMembershipUsername = binding.mainUsernameMembership;
        mMembershipItemCount = binding.userItemsInCartMembership;
        mMembershipMoney = binding.userMoneyMembership;

        mConfirmBuyMembershipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmMembership();
                Intent intent = MainActivity.intentFactory(getApplicationContext(), mUser.getUserId());
                startActivity(intent);
            }
        });

        getDatabase();

        refreshDisplay();
    }

    private void getDatabase() {
        mBananaBargainsDAO= Room.databaseBuilder(this, AppDatabase.class, AppDatabase.DATABASE_NAME)
                .allowMainThreadQueries()
                .build()
                .BananaBargainsDAO();
    }

    private void refreshDisplay() {
        Log.d("BuyBananas", "refreshDisplay: REFRESH DISPLAY");

        mUserId = getIntent().getIntExtra(USER_ID_KEY, -1);
        if (mPreferences == null) {
            getPrefs();
        }
        mUserId = mPreferences.getInt(USER_ID_KEY, -1);
        mUser = mBananaBargainsDAO.getUserById(mUserId);

        // refresh username
        mMembershipUsername.setText(mBananaBargainsDAO.getUserById(mUserId).getUsername());

        // refresh item count
        int itemCount = mBananaBargainsDAO.findCartsByUserId(mUserId).size();
        mMembershipItemCount.setText("Items in cart: " + itemCount);

        // refresh money amount
        String formattedMoney = String.format("$%.2f", mBananaBargainsDAO.getUserById(mUserId).getTotalMoney());
        mMembershipMoney.setText(formattedMoney);

    }

    private void getPrefs() {
        mPreferences = this.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
    }

    private void confirmMembership() {
        mUserId = getIntent().getIntExtra(USER_ID_KEY, -1);
        if (mPreferences == null) {
            getPrefs();
        }
        mUserId = mPreferences.getInt(USER_ID_KEY, -1);
        mUser = mBananaBargainsDAO.getUserById(mUserId);
        Double userMoney = mUser.getTotalMoney();
        if(userMoney < 10.00) {
            Toast.makeText(Membership.this, "Insufficient funds", Toast.LENGTH_SHORT).show();
        } else {
            //Set to 1 for "True"
            mBananaBargainsDAO.updateMembership(1, mUserId);
            mBananaBargainsDAO.updateMoney(userMoney- 10.00, mUserId);
            Toast.makeText(Membership.this, "Thanks for becoming a member, " + mUser.getUsername(), Toast.LENGTH_SHORT).show();
        }
    }

    public static Intent intentFactory(Context context){
        Intent intent = new Intent(context, Membership.class);
        return intent;
    }
}