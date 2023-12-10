package com.example.bananabargains;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.bananabargains.DB.AppDatabase;
import com.example.bananabargains.DB.Banana;
import com.example.bananabargains.DB.BananaBargainsDAO;
import com.example.bananabargains.databinding.ActivityAddBananasBinding;
import com.example.bananabargains.databinding.ActivityBuyBananasBinding;
import com.example.bananabargains.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class BuyBananas extends AppCompatActivity {
    private static final String PREFERENCES_KEY = "com.example.bananabargains.PREFERENCES_KEY";
    private static final String USER_ID_KEY = "com.example.bananabargains.userIdKey";
    private SharedPreferences mPreferences = null;
    private ActivityBuyBananasBinding binding;
    private AppCompatButton mAddFundsButton;
    private AppCompatButton mCartCheckoutButton;
    private TextView mBuyUsername;
    private TextView mBuyItemCount;
    private TextView mBuyMoney;
    private RecyclerView mMainDisplay;
    private BananaBargainsDAO mBananaBargainsDAO;
    private int mUserId;

    private List<Banana> mBananaList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_bananas);

        binding = ActivityBuyBananasBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAddFundsButton = binding.addFundsToUserButton;
        mCartCheckoutButton = binding.cartCheckoutButton;
        mBuyUsername = binding.buyBananasUsername;
        mBuyItemCount = binding.buyBananasItemsInCartCount;
        mBuyMoney = binding.buyBananasMoneyAmount;
        mMainDisplay = binding.buyBananasDisplay;

        getUserId();

        getDatabase();

        refreshDisplay();

        mAddFundsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AddFunds.intentFactory(getApplicationContext());
                startActivity(intent);
            }
        });

        mCartCheckoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = CheckoutScreen.intentFactory(getApplicationContext());
                startActivity(intent);
            }
        });

        getDatabase();
    }

    private void getUserId() {
        mUserId = getIntent().getIntExtra(USER_ID_KEY, -1);
        if (mPreferences == null) {
            getPrefs();
        }
        mUserId = mPreferences.getInt(USER_ID_KEY, -1);
    }

    private void getPrefs() {
        mPreferences = this.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
    }

    public static Intent intentFactory(Context context){
        Intent intent = new Intent(context, BuyBananas.class);

        return intent;
    }

    private void getDatabase() {
        mBananaBargainsDAO = Room.databaseBuilder(this, AppDatabase.class, AppDatabase.DATABASE_NAME)
                .allowMainThreadQueries()
                .build()
                .BananaBargainsDAO();
    }

    private void refreshDisplay() {

        // refresh username
        mBuyUsername.setText(mBananaBargainsDAO.getUserById(mUserId).getUsername());

        // refresh item count
        int itemCount = mBananaBargainsDAO.findCartsByUserId(mUserId).size();
        mBuyItemCount.setText("" + itemCount);

        // refresh money amount
        String formattedMoney =  String.format("$%.2f",mBananaBargainsDAO.getUserById(mUserId).getTotalMoney());
        mBuyMoney.setText(formattedMoney);

        // -- refresh banana list
        // get Integer list of unique banana id from the user
        List<Integer> mUniqueBananaIdsUser = mBananaBargainsDAO.getBananaIdsByUserId(mUserId);
        // turn that Integer list into a banana list
        mBananaList = new ArrayList<>();
        for (Integer i : mUniqueBananaIdsUser) {
            // add integer to new list
            mBananaList.add(mBananaBargainsDAO.getBananaById(i));
        }
        // === DEBUG, IGNORE ===
        StringBuilder sb = new StringBuilder();
        for (Banana b : mBananaList) {
            Log.d("BuyBananas", "" + b.getBananaDescription());
            sb.append("Description: " + b.getBananaDescription() + "\n" +
                    "Price: " + b.getBananaPrice() + "\n" +
                    "Id: " + b.getBananaId() + "\n\n");
        }
        Log.d("BuyBananas", sb.toString());
        // === ===== ===

        // refresh recycler view
        BuyBananasAdapter buttonPanelAdapter = new BuyBananasAdapter(this, mBananaList,mUserId);
        mMainDisplay.setAdapter(buttonPanelAdapter);
        mMainDisplay.setLayoutManager(new LinearLayoutManager(this));
    }
}