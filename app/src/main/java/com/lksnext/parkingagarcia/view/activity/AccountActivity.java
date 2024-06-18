package com.lksnext.parkingagarcia.view.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.lksnext.parkingagarcia.R;
import com.lksnext.parkingagarcia.databinding.ActivityAccountBinding;
import com.lksnext.parkingagarcia.databinding.ActivityForgotPasswordBinding;

public class AccountActivity extends AppCompatActivity {
    ActivityAccountBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(v -> finish());
    }
}