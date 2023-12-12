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
import android.widget.Toast;

import com.example.bananabargains.DB.AppDatabase;
import com.example.bananabargains.DB.Banana;
import com.example.bananabargains.DB.BananaBargainsDAO;
import com.example.bananabargains.DB.Cart;
import com.example.bananabargains.databinding.ActivityBuyBananasBinding;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private TextView mCartTotal;
    private TextView mCartTotalAmount;
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
        mCartTotal = binding.BuyBananasTotal;
        mCartTotalAmount = binding.BuyBananasTotalCost;

        getUserId();

        Log.d("BuyBananas", "mUserId: " + mUserId);

        getDatabase();

        refreshDisplay();

        setupRecyclerView();

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

                // initialize cart total
                Double cartTotal = 0.00;
                List<Integer> bananaIdList = mBananaBargainsDAO.getBananaIdsByUserId(mUserId);
                for(Integer i : bananaIdList) {
                    // turn id into a price and add it to total
                    cartTotal += mBananaBargainsDAO.getBananaById(i).getBananaPrice();
                }
                // 50% off if user is a member
                if (mBananaBargainsDAO.getUserById(mUserId).getHasMembership() == 1) {
                    cartTotal = cartTotal/2;
                }
                // round the cart to 2 decimals (same rounding used in %.2f for string formatting)
                BigDecimal cartTotalRounded = new BigDecimal(cartTotal).setScale(2, RoundingMode.HALF_EVEN);
                cartTotal = cartTotalRounded.doubleValue();

                // if cart total is greater than user total, notify user
                if (cartTotal > mBananaBargainsDAO.getUserById(mUserId).getTotalMoney()) {
                    // TODO: THIS DOESN'T HAVE TO BE A TOAST
                    Toast.makeText(getApplicationContext(), "\t\t\tNot enough funds\nDon't PEEL disappointed", Toast.LENGTH_LONG).show();
                    return;
                }

                // subtract from user total
                //Log.d("BuyBananas", "userTotal Before: " + mBananaBargainsDAO.getUserById(mUserId).getTotalMoney());
                Double userTotal = mBananaBargainsDAO.getUserById(mUserId).getTotalMoney() - cartTotal;
                //Log.d("BuyBananas", "userTotal After: " + userTotal);
                // set user total to new amount
                mBananaBargainsDAO.updateMoney(userTotal, mUserId);
                // remove user's carts from database
                mBananaBargainsDAO.deleteAllCartsFromUser(mUserId);

                Intent intent = CheckoutScreen.intentFactory(getApplicationContext());
                intent.putExtra(USER_ID_KEY,mUserId);
                startActivity(intent);
            }
        });

        getDatabase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshDisplay();
        setupRecyclerView();
    }

    private void getUserId() {
        mUserId = getIntent().getIntExtra(USER_ID_KEY, -1);
        //Log.d("BuyBananas", "getUserId: mUserId " + mUserId);
        if(mUserId != -1) {
            return;
        }
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
        Log.d("BuyBananas", "refreshDisplay: mUserId" + mUserId);
        if(mUserId == -1) {
            return;
        }

        //Log.d("BuyBananas", "refreshDisplay: REFRESH DISPLAY");

        // refresh username
        mBuyUsername.setText(mBananaBargainsDAO.getUserById(mUserId).getUsername());

        // refresh item count
        int itemCount = mBananaBargainsDAO.findCartsByUserId(mUserId).size();
        mBuyItemCount.setText("" + itemCount);

        // refresh money amount
        String formattedMoney = String.format("$%.2f", mBananaBargainsDAO.getUserById(mUserId).getTotalMoney());
        mBuyMoney.setText(formattedMoney);

        // refresh total text
        if (mBananaBargainsDAO.getUserById(mUserId).getHasMembership() == 1) {
            // set text to: Member Total
            mCartTotal.setText("Member Total");
        } else {
            // set text to: Total
            mCartTotal.setText("Total");
        }

        // refresh total cost
        double totalCost = 0;
        // get cart total
        List<Cart> cartList = mBananaBargainsDAO.findCartsByUserId(mUserId);
        for (Cart c : cartList) {
            double bananaPrice = mBananaBargainsDAO.getBananaById(c.getBananaId()).getBananaPrice();
            totalCost += bananaPrice;
        }
        // if member
        if(mBananaBargainsDAO.getUserById(mUserId).getHasMembership() == 1) {
            // 50% off cart total
            totalCost = totalCost/2;
        }
        String formattedTotal = String.format("$%.2f", totalCost);
        mCartTotalAmount.setText(formattedTotal);

    }

    /**
     * Sets up RecyclerView
     * Also includes what happens when recyclerview is clicked (refreshes display)
     */
    private void setupRecyclerView() {
        // -- refresh banana list
        // get Integer list of unique banana id from the user
        List<Integer> mUniqueBananaIdsUser = mBananaBargainsDAO.getBananaIdByUserId(mUserId);
        // turn that Integer list into a banana list
        mBananaList = new ArrayList<>();
        for (Integer i : mUniqueBananaIdsUser) {
            // add integer to new list
            mBananaList.add(mBananaBargainsDAO.getBananaById(i));
        }

        // refresh recycler view
        BuyBananasAdapter buttonPanelAdapter = new BuyBananasAdapter(this, mBananaList,mUserId);
        mMainDisplay.setAdapter(buttonPanelAdapter);
        mMainDisplay.setLayoutManager(new LinearLayoutManager(this));

        // Set the click listener for the adapter
        buttonPanelAdapter.setOnItemClickListener(new BuyBananasAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                refreshDisplay();
            }
        });
    }
}