package com.example.bananabargains;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.bananabargains.DB.AppDatabase;
import com.example.bananabargains.DB.BananaBargainsDAO;
import com.example.bananabargains.databinding.ActivityAddFundsBinding;
import com.example.bananabargains.databinding.ActivityBuyBananasBinding;

public class AddFunds extends AppCompatActivity {
    private static final String PREFERENCES_KEY = "com.example.bananabargains.PREFERENCES_KEY";
    private static final String USER_ID_KEY = "com.example.bananabargains.userIdKey";
    private SharedPreferences mPreferences = null;
    private ActivityAddFundsBinding binding;
    private AppCompatButton mAddFundsToUserButton;
    private AppCompatButton mBackToCheckoutButton;
    private BananaBargainsDAO mBananaBargainsDAO;
    private int mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_funds);

        binding = ActivityAddFundsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mBackToCheckoutButton = binding.backToCheckoutButton;

        mBackToCheckoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = BuyBananas.intentFactory(getApplicationContext());
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
        Intent intent = new Intent(context, AddFunds.class);

        return intent;
    }

    private void getDatabase() {
        mBananaBargainsDAO = Room.databaseBuilder(this, AppDatabase.class, AppDatabase.DATABASE_NAME)
                .allowMainThreadQueries()
                .build()
                .BananaBargainsDAO();
    }
}