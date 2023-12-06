package com.example.bananabargains;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.bananabargains.DB.BananaBargainsDAO;
import com.example.bananabargains.DB.User;
import com.example.bananabargains.databinding.ActivityMembershipBinding;

public class Membership extends AppCompatActivity {
    private static final String USER_ID_KEY = "com.example.bananabargains.userIdKey";
    private int mUserId;
    private User mUser;
    private AppCompatButton mConfirmBuyMembershipButton;
    private ActivityMembershipBinding binding;
    private BananaBargainsDAO mBananaBargainsDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership);

        binding = ActivityMembershipBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mConfirmBuyMembershipButton = binding.confirmBuyMembershipButton;

        mConfirmBuyMembershipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmMembership();
            }
        });
    }

    private void confirmMembership() {
        mUserId = getIntent().getIntExtra(USER_ID_KEY, -1);
        //Set to 1 for "True"
        User user = mBananaBargainsDAO.getUserById(mUserId);
        Double userMoney = user.getTotalMoney();
        if(userMoney < 10.00) {
            Toast.makeText(Membership.this, "Insufficient funds", Toast.LENGTH_SHORT).show();
        } else {
            mBananaBargainsDAO.updateMembership(1, mUserId);
            mBananaBargainsDAO.updateMoney(userMoney- 10.00, mUserId);
            Toast.makeText(Membership.this, "Thanks for becoming a member, " + user.getUsername(), Toast.LENGTH_SHORT).show();
        }
    }

    public static Intent intentFactory(Context context){
        Intent intent = new Intent(context, Membership.class);
        return intent;
    }
}