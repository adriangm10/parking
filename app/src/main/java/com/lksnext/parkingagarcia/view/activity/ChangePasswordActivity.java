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
import com.lksnext.parkingagarcia.databinding.ActivityChangePasswordBinding;
import com.lksnext.parkingagarcia.viewmodel.ChangePasswordViewModel;

public class ChangePasswordActivity extends AppCompatActivity {
    ActivityChangePasswordBinding binding;
    ChangePasswordViewModel changePasswordViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        changePasswordViewModel = new ViewModelProvider(this).get(ChangePasswordViewModel.class);

        binding.cancelButton.setOnClickListener(v -> finish());

        binding.confirmButton.setOnClickListener(v -> {
            String password = binding.passwordText.getText().toString();
            String confirmPassword = binding.confirmPasswordText.getText().toString();
            changePasswordViewModel.changePassword(password, confirmPassword);
        });

        changePasswordViewModel.getNewPasswordError().observe(this, binding.passwordText::setError);
        changePasswordViewModel.getConfirmPasswordError().observe(this, binding.confirmPasswordText::setError);
        changePasswordViewModel.getErrorMessage().observe(this, error ->
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        );
        changePasswordViewModel.getSuccess().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}