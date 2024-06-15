package com.lksnext.parkingagarcia.view.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.lksnext.parkingagarcia.R;
import com.lksnext.parkingagarcia.ReservationCard;
import com.lksnext.parkingagarcia.Utils;
import com.lksnext.parkingagarcia.domain.Reservation;
import com.lksnext.parkingagarcia.viewmodel.MainViewModel;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ResFragment extends Fragment {
    private View view;
    private MainViewModel mainViewModel;

    public ResFragment() {
        // need empty constructor
    }

    private Predicate<Reservation> reservationsFilter(String reservationOption) {
        switch (reservationOption) {
            case "Past reservations":
                return reservation -> reservation.getHour().getStartTime() < System.currentTimeMillis();
            case "Active reservations":
                return reservation -> reservation.getHour().getStartTime() > System.currentTimeMillis();
            default:
                return reservation -> true;
        }
    }

    private void generateReservationsCards(List<Reservation> reservationsList, LinearLayout reservations, String reservationOption) {
        reservations.removeAllViews();

        List<Reservation> filteredReservationsList = reservationsList
                .stream()
                .filter(reservationsFilter(reservationOption))
                .collect(Collectors.toList());

        for (int i = 0; i < filteredReservationsList.size(); i++) {
            ReservationCard reservationCard = new ReservationCard(getContext());
            reservationCard.setPlaceNumber(filteredReservationsList.get(i).getPlace().getId());
            reservationCard.setDateText(filteredReservationsList.get(i).getDate());
            reservationCard.setHourText(
                    Utils.formatTimeToHHmm(filteredReservationsList.get(i).getHour().getStartTime(), getResources().getConfiguration().getLocales().get(0)),
                    Utils.formatTimeToHHmm(filteredReservationsList.get(i).getHour().getEndTime(), getResources().getConfiguration().getLocales().get(0))
            );

            switch (filteredReservationsList.get(i).getPlace().getType()) {
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
            if (filteredReservationsList.get(i).getHour().getStartTime() < System.currentTimeMillis()) {
                reservationCard.setCancelBtnVisibility(View.GONE);
                reservationCard.setEditBtnVisibility(View.GONE);
            } else {
                reservationCard.setCancelBtnOnClickListener(v -> {
                    mainViewModel.cancelUserReservation(filteredReservationsList.get(finalI));
                    reservations.removeView(reservationCard);
                });

                reservationCard.setEditBtnOnClickListener(v -> {
                    EditFragment editFragment = EditFragment.newInstance(filteredReservationsList.get(finalI), mainViewModel);
                    editFragment.show(getParentFragmentManager(), "editFragment");
                });
            }

            reservations.addView(reservationCard);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_reservations, container, false);
        LinearLayout reservations = view.findViewById(R.id.reservationsList);

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getContext(), R.array.reservation_options, android.R.layout.simple_dropdown_item_1line);
        ((AutoCompleteTextView) view.findViewById(R.id.dropdown_menu_item)).setAdapter(arrayAdapter);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.loadUserReservations();

        ((AutoCompleteTextView) view.findViewById(R.id.dropdown_menu_item)).setOnItemClickListener((parent, view, position, id) -> {
            String selectedOption = parent.getItemAtPosition(position).toString();
            Log.d("ResFragment", "Selected option: " + selectedOption);
            generateReservationsCards(mainViewModel.getUserReservations().getValue(), reservations, selectedOption);
        });

        mainViewModel.getUserReservations().observe(getViewLifecycleOwner(), reservationsList -> {
            String selectedOption = ((AutoCompleteTextView) view.findViewById(R.id.dropdown_menu_item)).getText().toString();
            generateReservationsCards(reservationsList, reservations, selectedOption);
        });

        mainViewModel.getError().observe(getViewLifecycleOwner(), error -> Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show());

        mainViewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message -> Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show());

        return view;
    }
}