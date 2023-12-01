package com.example.bananabargains;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.bananabargains.databinding.ActivityAdminLandingBinding;
import com.example.bananabargains.databinding.ActivityMainBinding;

public class AdminLanding extends AppCompatActivity {
    private static final String USER_ID_KEY = "com.example.bananabargains.userIdKey";
    private ActivityAdminLandingBinding binding;

    private TextView mAdminLanding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_landing);

        binding = ActivityAdminLandingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //TODO: Get main display widgets and display them
        mAdminLanding = binding.adminLanding;
    }

    public static Intent intentFactory(Context context, int userId){
        Intent intent = new Intent(context, AdminLanding.class);
        intent.putExtra(USER_ID_KEY, userId);

        return intent;
    }
}