package com.lksnext.parkingagarcia.view.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.lksnext.parkingagarcia.databinding.ActivityPasswordUpdatedBinding;

public class PasswordUpdatedActivity extends AppCompatActivity  {
    private ActivityPasswordUpdatedBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPasswordUpdatedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(PasswordUpdatedActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}
