package com.lksnext.parkingagarcia.data;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.lksnext.parkingagarcia.domain.Callback;

public class DataRepository {

    private static DataRepository instance;
    private FirebaseAuth auth;
    private DataRepository(){
        auth = FirebaseAuth.getInstance();
    }
    private static final String TAG = "DataRepository";

    public static synchronized DataRepository getInstance(){
        if (instance==null){
            instance = new DataRepository();
        }
        return instance;
    }

    public void login(String email, String pass, Callback callback){
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "User logged in");
                callback.onSuccess();
            } else {
                Log.w(TAG, "Login failure", task.getException());
                if (task.getException() instanceof FirebaseAuthException) {
                    callback.onFailure(task.getException().getMessage(), ((FirebaseAuthException) task.getException()).getErrorCode());
                } else {
                    callback.onFailure(task.getException().getMessage(), "");
                }
            }
        });
    }

    // pre: email, pass, confirmPass are not empty
    public void register(String name, String email, String pass, Callback callback){
        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "User registered");
                FirebaseUser user = auth.getCurrentUser();

                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build();

                user.updateProfile(profileUpdates).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        Log.d(TAG, "User profile updated.");
                    }
                });

                callback.onSuccess();
            } else {
                Log.w(TAG, "Create user failure", task.getException());
                if (task.getException() instanceof FirebaseAuthException) {
                    callback.onFailure(task.getException().getMessage(), ((FirebaseAuthException) task.getException()).getErrorCode());
                } else {
                    callback.onFailure(task.getException().getMessage(), "");
                }
            }
        });
    }
}
