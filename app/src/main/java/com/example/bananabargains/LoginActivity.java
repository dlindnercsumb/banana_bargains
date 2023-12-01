package com.example.bananabargains;

import androidx.appcompat.app.AppCompatActivity;
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

public class LoginActivity extends AppCompatActivity {
    private EditText mUsernameField;
    private EditText mPasswordField;
    private Button mButton;
    private BananaBargainsDAO mBananaBargainsDao;
    private String mUsername;
    private String mPassword;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        wireupDisplay();

        getDatabase();
    }

    private void wireupDisplay(){
        mUsernameField = findViewById(R.id.editTextLoginUserName);
        mPasswordField = findViewById(R.id.editTextLoginPassword);

        mButton = findViewById(R.id.buttonLogin);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getValuesFromDisplay();
                if(checkForUserInDatabase()) {
                    if(!validatePassword()){
                        Toast.makeText(LoginActivity.this, "Invalid password", Toast.LENGTH_SHORT).show();
                    } else {
                        // If user is admin AdminLanding page
                        if(mUser.getIsAdmin() == 1) {
                            Intent intent = AdminLanding.intentFactory(getApplicationContext(), mUser.getUserId());
                            startActivity(intent);
                        } else {
                            Intent intent = MainActivity.intentFactory(getApplicationContext(), mUser.getUserId());
                            startActivity(intent);
                        }
                    }
                }
            }
        });
    }

    private boolean validatePassword() {
        //if password enters matched user password in database then returns true
        return mUser.getPassword().equals(mPassword);
    }

    private void getValuesFromDisplay() {
        mUsername = mUsernameField.getText().toString();
        mPassword = mPasswordField.getText().toString();
    }

    private boolean checkForUserInDatabase() {
        mUser = mBananaBargainsDao.getUserByUsername(mUsername);
        if(mUser == null) {
            Toast.makeText(this, "User: " + mUsername + "  not found", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void getDatabase() {
        mBananaBargainsDao = Room.databaseBuilder(this, AppDatabase.class, AppDatabase.DATABASE_NAME)
                .allowMainThreadQueries()
                .build()
                .BananaBargainsDAO();
    }

    //Intent factories allow you to navigate between screens
    public static Intent intentFactory(Context context){
        Intent intent = new Intent(context, LoginActivity.class);

        return intent;
    }
}