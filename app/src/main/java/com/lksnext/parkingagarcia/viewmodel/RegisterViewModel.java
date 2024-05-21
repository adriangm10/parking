package com.lksnext.parkingagarcia.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lksnext.parkingagarcia.data.DataRepository;
import com.lksnext.parkingagarcia.domain.Callback;

public class RegisterViewModel extends ViewModel {
    MutableLiveData<String> errorMessage = new MutableLiveData<>();
    MutableLiveData<String> successMessage = new MutableLiveData<>();
    MutableLiveData<String> nameError = new MutableLiveData<>();
    MutableLiveData<String> emailError = new MutableLiveData<>();
    MutableLiveData<String> passwordError = new MutableLiveData<>();
    MutableLiveData<String> confirmPasswordError = new MutableLiveData<>();
    MutableLiveData<Boolean> registered = new MutableLiveData<>();

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public LiveData<String> getNameError() {
        return nameError;
    }

    public LiveData<String> getEmailError() {
        return emailError;
    }

    public LiveData<String> getPasswordError() {
        return passwordError;
    }

    public LiveData<String> getConfirmPasswordError() {
        return confirmPasswordError;
    }

    public LiveData<Boolean> getRegistered() {
        return registered;
    }

    private void setErrorMessage(MutableLiveData<String> fieldError, String value, String errorMsg) {
        if (value.isEmpty()) {
            fieldError.setValue(errorMsg);
        } else {
            fieldError.setValue(null);
        }
    }

    public void registerUser(String name, String email, String pass, String confirmPass) {
        setErrorMessage(nameError, name, "Name is required");
        setErrorMessage(emailError, email, "Email is required");
        setErrorMessage(passwordError, pass, "Password is required");
        setErrorMessage(confirmPasswordError, confirmPass, "Confirm password is required");
        if (!pass.isEmpty() && !confirmPass.isEmpty() && !pass.equals(confirmPass)) {
            confirmPasswordError.setValue("Passwords do not match");
            return;
        }
        if (nameError.getValue() != null || emailError.getValue() != null || passwordError.getValue() != null || confirmPasswordError.getValue() != null) {
            return;
        }
        DataRepository.getInstance().register(name, email, pass, new Callback() {
            @Override
            public void onSuccess() {
                successMessage.setValue("User registered");
                registered.setValue(true);
            }

            @Override
            public void onFailure(String error, String code) {
                registered.setValue(false);
                switch (code) {
                    case "ERROR_INVALID_EMAIL":
                    case "ERROR_EMAIL_ALREADY_IN_USE":
                        emailError.setValue(error);
                        break;
                    case "ERROR_WEAK_PASSWORD":
                        passwordError.setValue(error);
                        break;
                    default:
                        errorMessage.setValue(error);
                        break;
                }
            }
        });
    }
}
