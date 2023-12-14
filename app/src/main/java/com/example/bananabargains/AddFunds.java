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
import com.example.bananabargains.databinding.ActivityAddFundsBinding;
import com.example.bananabargains.databinding.ActivityBuyBananasBinding;

public class AddFunds extends AppCompatActivity {
    private static final String PREFERENCES_KEY = "com.example.bananabargains.PREFERENCES_KEY";
    private static final String USER_ID_KEY = "com.example.bananabargains.userIdKey";
    private SharedPreferences mPreferences = null;
    private ActivityAddFundsBinding binding;
    private AppCompatButton mAddFundsToAccountButton;
    private AppCompatButton mIncrementFundsButton;
    private TextView mFundAmount;
    private TextView mAddFundsUsername;
    private TextView mAddFundsItemCount;
    private TextView mAddFundsMoney;
    private AppCompatButton mBackToCheckoutButton;
    private BananaBargainsDAO mBananaBargainsDAO;
    private int mUserId;
    private User mUser;

    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddFundsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mBackToCheckoutButton = binding.backToCheckoutButton;
        mFundAmount = binding.fundAmount;
        mIncrementFundsButton = binding.incrementAddFundsButton;
        mAddFundsToAccountButton = binding.addFundsToAccountButton;
        mAddFundsUsername = binding.mainUsernameAddFunds;
        mAddFundsItemCount = binding.userItemsInCartAddFunds;
        mAddFundsMoney = binding.userMoneyAddFunds;

        mIncrementFundsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                mFundAmount.setText(String.valueOf(count));
            }
        });

        mAddFundsToAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFunds(count);
                refreshDisplay();
            }
        });

        mBackToCheckoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = BuyBananas.intentFactory(getApplicationContext(), mUserId);
                startActivity(intent);
            }
        });
        getDatabase();
        checkForUser();
        refreshDisplay();
    }

    private void refreshDisplay() {

        // refresh username
        mAddFundsUsername.setText(mBananaBargainsDAO.getUserById(mUserId).getUsername());

        // refresh item count
        int itemCount = mBananaBargainsDAO.findCartsByUserId(mUserId).size();
        mAddFundsItemCount.setText("Items in cart: " + itemCount);

        // refresh money amount
        String formattedMoney = String.format("$%.2f", mBananaBargainsDAO.getUserById(mUserId).getTotalMoney());
        mAddFundsMoney.setText(formattedMoney);

    }

    private void checkForUser() {
        mUserId = getIntent().getIntExtra(USER_ID_KEY, -1);
        if (mUserId != -1) {
            mUser = mBananaBargainsDAO.getUserById(mUserId);
            return;
        }
        if (mPreferences == null) {
            getPrefs();
        }
        mUserId = mPreferences.getInt(USER_ID_KEY, -1);
        mUser = mBananaBargainsDAO.getUserById(mUserId);
    }

    private void addFunds(int count) {

        mUser = mBananaBargainsDAO.getUserById(mUserId);
        Double userMoney = mUser.getTotalMoney();

        mBananaBargainsDAO.updateMoney(userMoney + count, mUserId);
        Toast.makeText(AddFunds.this, "$" + count + " has been added to your account!", Toast.LENGTH_SHORT).show();
    }

    private void getPrefs() {
        mPreferences = this.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
    }

    public static Intent intentFactory(Context context, int userId){
        Intent intent = new Intent(context, AddFunds.class);
        intent.putExtra(USER_ID_KEY, userId);
        return intent;
    }

    private void getDatabase() {
        mBananaBargainsDAO = Room.databaseBuilder(this, AppDatabase.class, AppDatabase.DATABASE_NAME)
                .allowMainThreadQueries()
                .build()
                .BananaBargainsDAO();
    }
}