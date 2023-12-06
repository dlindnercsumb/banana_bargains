package com.example.bananabargains;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bananabargains.DB.AppDatabase;
import com.example.bananabargains.DB.Banana;
import com.example.bananabargains.DB.BananaBargainsDAO;
import com.example.bananabargains.databinding.ActivityAddBananasBinding;

public class AddBananas extends AppCompatActivity {
    private static final String PREFERENCES_KEY = "com.example.bananabargains.PREFERENCES_KEY";
    private static final String USER_ID_KEY = "com.example.bananabargains.userIdKey";
    private SharedPreferences mPreferences = null;
    private ActivityAddBananasBinding binding;
    private EditText mEnterProductName;
    private EditText mEnterPrice;
    private AppCompatButton mAddProductToDatabaseButton;
    private BananaBargainsDAO mBananaBargainsDAO;
    private int mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bananas);

        binding = ActivityAddBananasBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mEnterProductName = binding.enterProductName;
        mEnterPrice = binding.enterPrice;
        mAddProductToDatabaseButton = binding.addProductToDatabaseButton;

        mAddProductToDatabaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitNewProduct();
            }
        });

        getDatabase();
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

    private void getUserId() {
        mUserId = getIntent().getIntExtra(USER_ID_KEY, -1);
        if (mPreferences == null) {
            getPrefs();
        }
        mUserId = mPreferences.getInt(USER_ID_KEY, -1);
    }

    private void submitNewProduct() {
        String productName = mEnterProductName.getText().toString();
        Double price = Double.parseDouble(mEnterPrice.getText().toString());

        if(productName.length() == 0) {
            Toast.makeText(AddBananas.this, "No product name entered.", Toast.LENGTH_SHORT).show();
        } else if(price.equals(0.0)) {
            Toast.makeText(AddBananas.this, "No price entered.", Toast.LENGTH_SHORT).show();
        } else {
            Banana banana = new Banana(productName, price);
            mBananaBargainsDAO.insert(banana);

            Toast.makeText(AddBananas.this, productName + " added to inventory.", Toast.LENGTH_SHORT).show();

            getUserId();
            Intent intent = AdminLanding.intentFactory(getApplicationContext(), mUserId);
            startActivity(intent);
        }
    }

    public static Intent intentFactory(Context context){
        Intent intent = new Intent(context, AddBananas.class);
        return intent;
    }
}