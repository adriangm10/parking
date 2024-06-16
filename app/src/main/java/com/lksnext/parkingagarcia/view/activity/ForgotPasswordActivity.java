package com.lksnext.parkingagarcia.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.lksnext.parkingagarcia.databinding.ActivityForgotPasswordBinding;
import com.lksnext.parkingagarcia.viewmodel.ForgotPasswordViewModel;

public class ForgotPasswordActivity extends AppCompatActivity {
    private ActivityForgotPasswordBinding binding;
    private ForgotPasswordViewModel forgotPasswordViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        forgotPasswordViewModel = new ViewModelProvider(this).get(ForgotPasswordViewModel.class);

        binding.sendEmailBtn.setOnClickListener(v -> {
            String email = binding.emailText.getText().toString();
            forgotPasswordViewModel.sendEmail(email);
        });

        binding.backToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        forgotPasswordViewModel.getErrorMessage().observe(this, error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        );

        forgotPasswordViewModel.getEmailError().observe(this, binding.emailText::setError);

        forgotPasswordViewModel.getEmailSent().observe(this, emailSent -> {
            if (emailSent) {
                Intent newIntent = new Intent(ForgotPasswordActivity.this, PasswordUpdatedActivity.class);
                startActivity(newIntent);
            }
        });
    }
}
