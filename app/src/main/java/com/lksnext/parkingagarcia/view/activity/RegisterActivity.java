package com.lksnext.parkingagarcia.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.lksnext.parkingagarcia.databinding.ActivityRegisterBinding;
import com.lksnext.parkingagarcia.viewmodel.RegisterViewModel;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private RegisterViewModel registerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        binding.registerBtn.setOnClickListener(v -> {
            String name = binding.nameText.getText().toString();
            String email = binding.emailText.getText().toString();
            String password = binding.passwordText.getText().toString();
            String confirmPassword = binding.confirmPasswordText.getText().toString();
            registerViewModel.registerUser(name, email, password, confirmPassword);
        });

        binding.loginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        registerViewModel.getErrorMessage().observe(this, error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        );

        registerViewModel.getSuccessMessage().observe(this, success ->
            Toast.makeText(this, success, Toast.LENGTH_SHORT).show()
        );

        registerViewModel.getNameError().observe(this, binding.name::setError);

        registerViewModel.getEmailError().observe(this, binding.email::setError);

        registerViewModel.getPasswordError().observe(this, binding.password::setError);

        registerViewModel.getConfirmPasswordError().observe(this, binding.confirmPassword::setError);

        registerViewModel.getRegistered().observe(this, registered -> {
            if (registered != null && registered) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}