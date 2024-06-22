package com.lksnext.parkingagarcia.view.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.lksnext.parkingagarcia.R;
import com.lksnext.parkingagarcia.databinding.ActivityAccountBinding;
import com.lksnext.parkingagarcia.databinding.ActivityForgotPasswordBinding;
import com.lksnext.parkingagarcia.viewmodel.AccountViewModel;
import com.lksnext.parkingagarcia.viewmodel.MainViewModel;

public class AccountActivity extends AppCompatActivity {
    ActivityAccountBinding binding;
    AccountViewModel accountViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        accountViewModel.retrieveUser();

        binding.backBtn.setOnClickListener(v -> finish());

        accountViewModel.getUser().observe(this, user -> {
            if (user != null) {
                binding.nameText.setText(user.getDisplayName());
                binding.emailText.setText(user.getEmail());
            }
        });

        binding.okBtn.setOnClickListener(v -> {
            String name = binding.nameText.getText().toString();
            accountViewModel.updateUser(name);
        });

        accountViewModel.getSuccess().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Your profile has been updated", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        accountViewModel.getNameError().observe(this, binding.nameText::setError);

        accountViewModel.getErrorMessage().observe(this, error -> Toast.makeText(this, error, Toast.LENGTH_SHORT).show());
    }
}