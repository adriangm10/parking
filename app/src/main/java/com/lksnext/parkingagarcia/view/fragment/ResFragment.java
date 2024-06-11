package com.lksnext.parkingagarcia.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.lksnext.parkingagarcia.R;
import com.lksnext.parkingagarcia.ReservationCard;
import com.lksnext.parkingagarcia.viewmodel.MainViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ResFragment extends Fragment {
    private View view;
    private MainViewModel mainViewModel;

    public ResFragment() {
        // need empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_reservations, container, false);
        LinearLayout reservations = view.findViewById(R.id.reservationsList);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.loadUserReservations();

        mainViewModel.getUserReservations().observe(getViewLifecycleOwner(), reservationsList -> {
            reservations.removeAllViews();
            for (int i = 0; i < reservationsList.size(); i++) {
                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", getResources().getConfiguration().getLocales().get(0));
                ReservationCard reservationCard = new ReservationCard(getContext());
                reservationCard.setPlaceNumber(reservationsList.get(i).getPlace().getId());
                reservationCard.setDateText(reservationsList.get(i).getDate());
                reservationCard.setHourText(
                        formatter.format(new Date(reservationsList.get(i).getHour().getStartTime())),
                        formatter.format(new Date(reservationsList.get(i).getHour().getEndTime()))
                );
                switch (reservationsList.get(i).getPlace().getType()) {
                    case NORMAL:
                        reservationCard.setPlaceImage(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_normal, null));
                        break;
                    case ELECTRIC:
                        reservationCard.setPlaceImage(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_electric, null));
                        break;
                    case DISABLED:
                        reservationCard.setPlaceImage(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_disabled, null));
                        break;
                    case MOTORCYCLE:
                        reservationCard.setPlaceImage(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_motorcycle, null));
                        break;
                }

                int finalI = i;
                reservationCard.setCancelBtnOnClickListener(v -> {
                    mainViewModel.cancelUserReservation(reservationsList.get(finalI));
                    reservations.removeView(reservationCard);
                });

                reservations.addView(reservationCard);
            }
        });

        mainViewModel.getError().observe(getViewLifecycleOwner(), error -> Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show());

        mainViewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message -> Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show());

        return view;
    }
}