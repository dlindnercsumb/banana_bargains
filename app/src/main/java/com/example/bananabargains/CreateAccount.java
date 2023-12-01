package com.example.bananabargains;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bananabargains.DB.AppDatabase;
import com.example.bananabargains.DB.BananaBargainsDAO;
import com.example.bananabargains.DB.User;
import com.example.bananabargains.databinding.ActivityCreateAccountBinding;
import com.example.bananabargains.databinding.ActivityLoginBinding;

public class CreateAccount extends AppCompatActivity {

    private EditText mUsernameField;
    private EditText mPasswordField;
    private AppCompatButton mButton;
    private AppCompatButton mLoginButton;
    private BananaBargainsDAO mBananaBargainsDao;
    private String mUsername;
    private String mPassword;
    private User mUser;
    private ActivityCreateAccountBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        binding = ActivityCreateAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        wireupDisplay();

        getDatabase();
    }

    private void wireupDisplay() {
        mUsernameField = findViewById(R.id.editTextCreateUserName);
        mPasswordField = findViewById(R.id.editTextCreatePassword);

        mButton = findViewById(R.id.buttonCreateAccount);
        mLoginButton = findViewById(R.id.backToLogin);

        mLoginButton.setOnClickListener(view -> {
            Intent intent = LoginActivity.intentFactory(getApplicationContext());
            startActivity(intent);
        });

        mButton.setOnClickListener(view -> {
            getValuesFromDisplay();
            if (!validatePassword()) {
                Toast.makeText(CreateAccount.this, "Invalid password. Please enter a longer password", Toast.LENGTH_SHORT).show();
            } else {
                // If user is admin AdminLanding page
                if (checkForUserInDatabase()) {
                    Toast.makeText(CreateAccount.this, "User already exists, please login", Toast.LENGTH_SHORT).show();
                } else {
                    createUser();
                    Intent intent = LoginActivity.intentFactory(getApplicationContext());
                    startActivity(intent);
                    Toast.makeText(CreateAccount.this, "Account created! Please login", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createUser() {
        String username = mUsernameField.getText().toString();
        String password = mPasswordField.getText().toString();
        //New users will automatically get $5 w/ no admin and no membership
        User newUser = new User(username, password, 0, 5.00, 0);
        mBananaBargainsDao.insert(newUser);

    }

    private boolean validatePassword () {
        //If password entered is greater or equal to length of 6 return true.
        //We'll consider this long enough
        return mPassword.length() >= 6;
    }

    private void getValuesFromDisplay () {
        mUsername = mUsernameField.getText().toString();
        mPassword = mPasswordField.getText().toString();
    }

    private boolean checkForUserInDatabase () {
        mUser = mBananaBargainsDao.getUserByUsername(mUsername);
        if (mUser == null) {
            return false;
        }
        return true;
    }

    private void getDatabase () {
        mBananaBargainsDao = Room.databaseBuilder(this, AppDatabase.class, AppDatabase.DATABASE_NAME)
                .allowMainThreadQueries()
                .build()
                .BananaBargainsDAO();
    }

    //Intent factories allow you to navigate between screens
    public static Intent intentFactory (Context context){
        Intent intent = new Intent(context, CreateAccount.class);

        return intent;
    }
}