package com.lksnext.parkingagarcia;

import static org.junit.Assert.assertEquals;

import static java.lang.Thread.sleep;

import android.util.Log;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.lksnext.parkingagarcia.data.DataRepository;
import com.lksnext.parkingagarcia.domain.Callback;
import com.lksnext.parkingagarcia.domain.Place;
import com.lksnext.parkingagarcia.domain.Reservation;
import com.lksnext.parkingagarcia.viewmodel.MainViewModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@RunWith(AndroidJUnit4.class)
public class MainViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    MainViewModel viewModel = new MainViewModel();
    private static final String TAG = "MainViewModelTest";
    private FirebaseAuth auth;

    @Before
    public void setUp() throws InterruptedException {
        FirebaseApp.initializeApp(InstrumentationRegistry.getInstrumentation().getTargetContext());
        auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword("test@test.com", "test1234").addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "User created");
            } else {
                Log.d(TAG, "User not created");
                throw new RuntimeException("User not created");
            }
        });
        sleep(2000);
        assertEquals(auth.getCurrentUser().getEmail(), "test@test.com");
    }

    @After
    public void tearDown() throws InterruptedException {
        for (Reservation r : LiveDataTestUtil.getValue(viewModel.getUserReservations())) {
            DataRepository.getInstance().cancelUserReservation(r, new Callback() {
                @Override
                public void onSuccess(Object ... args) {
                    Log.d(TAG, "Reservation cancelled");
                }

                @Override
                public void onFailure(String error, String errorCode) {
                    Log.d(TAG, "Error cancelling reservation");
                }
            });
        }

        FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "User deleted");
            } else {
                Log.d(TAG, "User not deleted");
                throw new RuntimeException("User not deleted");
            }
        });
    }

    @Test
    public void reserveTest() throws Exception {
        List<Reservation> userReservations = LiveDataTestUtil.getValue(viewModel.getUserReservations());
        assertEquals(0, userReservations.size());

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date startDate = df.parse("2021-06-01 10:00:00");
        Date endDate = df.parse("2021-06-01 11:00:00");
        viewModel.reserve("01/06/2021", "1", new Place(22, Place.Type.NORMAL), startDate, endDate);
        viewModel.loadUserReservations();

        userReservations = LiveDataTestUtil.getValue(viewModel.getUserReservations());
        assertEquals(1, userReservations.size());
        Reservation r = userReservations.get(0);
        assertEquals("01/06/2021", r.getDate());
        assertEquals(auth.getCurrentUser().getUid(), r.getUser());
        assertEquals(22, r.getPlace().getId());
        assertEquals(Place.Type.NORMAL, r.getPlace().getType());
        assertEquals(startDate.getTime(), r.getHour().getStartTime());
        assertEquals(endDate.getTime(), r.getHour().getEndTime());
        assertEquals("1", r.getId());
    }
}
