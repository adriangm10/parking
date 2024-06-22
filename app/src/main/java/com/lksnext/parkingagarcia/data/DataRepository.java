package com.lksnext.parkingagarcia.data;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.lksnext.parkingagarcia.domain.Callback;
import com.lksnext.parkingagarcia.domain.Reservation;

import java.util.ArrayList;

public class DataRepository {

    private static final String TAG = "DataRepository";
    private static DataRepository instance;
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private DataRepository() {

    }

    public static synchronized DataRepository getInstance() {
        if (instance == null) {
            instance = new DataRepository();
        }
        return instance;
    }

    public void login(String email, String pass, Callback callback) {
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
    public void register(String name, String email, String pass, Callback callback) {
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

    public void reserve(Reservation reservation, Callback callback) {
        callback.onSuccess();
        reservation.setUser(auth.getCurrentUser().getDisplayName());

        db.collection("reservations")
                .document()
                .set(reservation)
                .addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Reserve added");
                callback.onSuccess();
            } else {
                Log.w(TAG, "Reserve failure", task.getException());
                callback.onFailure(task.getException().getMessage(), "");
            }
        });
    }

    public void getReservationsForDate(String date, Callback callback) {
        db.collection("reservations")
                .whereEqualTo("date", date)
                .get()
                .addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<Reservation> reservations = new ArrayList<>();
                for (QueryDocumentSnapshot i : task.getResult()) {
                    Log.d(TAG, "Got reservation " + i.getId());
                    reservations.add(i.toObject(Reservation.class));
                }
                callback.onSuccess(reservations);
            } else {
                Log.e(TAG, "Could not get reservations for date: " + date, task.getException());
                callback.onFailure(task.getException().getMessage(), "");
            }
        });
    }

    public void getUserReservations(Callback callback) {
        db.collection("reservations")
                .whereEqualTo("user", auth.getCurrentUser().getDisplayName())
                .get()
                .addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<Reservation> reservations = new ArrayList<>();
                for (QueryDocumentSnapshot i : task.getResult()) {
                    reservations.add(i.toObject(Reservation.class));
                }
                callback.onSuccess(reservations);
            } else {
                Log.e(TAG, "Could not get user reservations", task.getException());
                callback.onFailure(task.getException().getMessage(), "");
            }
        });
    }

    public void cancelUserReservation(Reservation reservation, Callback callback) {
        db.collection("reservations")
                .whereEqualTo("id", reservation.getId())
                .whereEqualTo("user", auth.getCurrentUser().getDisplayName())
                .get()
                .addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().size() != 1) {
                    callback.onFailure("Something gone wrong finding reservation", "");
                    return;
                }

                for (QueryDocumentSnapshot i : task.getResult()) {
                    i.getReference().delete();
                }

                Log.d(TAG, "Reservation deleted");
                callback.onSuccess();
            } else {
                Log.e(TAG, "Could not delete reservation", task.getException());
                callback.onFailure(task.getException().getMessage(), "");
            }
        });
    }

    public void editUserReservation(Reservation newReservation, String origReservation, Callback callback) {
        db.collection("reservations")
                .whereEqualTo("id", origReservation)
                .whereEqualTo("user", auth.getCurrentUser().getDisplayName())
                .get()
                .addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().size() != 1) {
                    callback.onFailure("Something gone wrong finding reservation", "");
                    return;
                }

                for (QueryDocumentSnapshot i : task.getResult()) {
                    i.getReference().set(newReservation);
                }

                Log.d(TAG, "Reservation edited");
                callback.onSuccess();
            } else {
                Log.e(TAG, "Could not edit reservation", task.getException());
                callback.onFailure(task.getException().getMessage(), "");
            }
        });
    }

    public void sendEmail(String email, Callback callback) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Email sent");
                callback.onSuccess();
            } else {
                Log.w(TAG, "Email failure", task.getException());
                if (task.getException() instanceof FirebaseAuthException) {
                    callback.onFailure(task.getException().getMessage(), ((FirebaseAuthException) task.getException()).getErrorCode());
                } else {
                    callback.onFailure(task.getException().getMessage(), "");
                }
            }
        });
    }

    public void logout(Callback callback) {
        auth.signOut();
        callback.onSuccess();
    }

    public void getUser(Callback callback) {
        callback.onSuccess(auth.getCurrentUser());
    }

    public void updateUser(String username, String email, Callback callback) {
        FirebaseUser user = auth.getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build();

        user.updateProfile(profileUpdates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "User name updated.");
                user.verifyBeforeUpdateEmail(email).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        Log.d(TAG, "User email updated.");
                        callback.onSuccess();
                    } else {
                        Log.w(TAG, "Update email failure", task1.getException());
                        if (task1.getException() instanceof FirebaseAuthException) {
                            callback.onFailure(task1.getException().getMessage(), ((FirebaseAuthException) task1.getException()).getErrorCode());
                        } else {
                            callback.onFailure(task1.getException().getMessage(), "");
                        }
                    }
                });
            } else {
                Log.w(TAG, "Update profile failure", task.getException());
                if (task.getException() instanceof FirebaseAuthException) {
                    callback.onFailure(task.getException().getMessage(), ((FirebaseAuthException) task.getException()).getErrorCode());
                } else {
                    callback.onFailure(task.getException().getMessage(), "");
                }
            }
        });
    }

    public void changePassword(String password, Callback callback) {
        FirebaseUser user = auth.getCurrentUser();
        user.updatePassword(password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "User password updated.");
                callback.onSuccess();
            } else {
                Log.w(TAG, "Update password failure", task.getException());
                if (task.getException() instanceof FirebaseAuthException) {
                    callback.onFailure(task.getException().getMessage(), ((FirebaseAuthException) task.getException()).getErrorCode());
                } else {
                    callback.onFailure(task.getException().getMessage(), "");
                }
            }
        });
    }
}
