package com.lksnext.parkingagarcia.view.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.lksnext.parkingagarcia.R;
import com.lksnext.parkingagarcia.Utils;
import com.lksnext.parkingagarcia.domain.EndReservationReminderReceiver;
import com.lksnext.parkingagarcia.domain.Reservation;
import com.lksnext.parkingagarcia.domain.StartReservationReminderReceiver;
import com.lksnext.parkingagarcia.view.ReservationCard;
import com.lksnext.parkingagarcia.viewmodel.MainViewModel;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ResFragment extends Fragment {
    View view;
    private MainViewModel mainViewModel;

    public ResFragment() {
        // need empty constructor
    }

    private Predicate<Reservation> reservationsFilter(String reservationOption) {
        switch (reservationOption) {
            case "Last month":
                return reservation -> reservation.getHour().getStartTime() > System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000 &&
                        reservation.getHour().getEndTime() < System.currentTimeMillis();
            case "Future":
                return reservation -> reservation.getHour().getStartTime() > System.currentTimeMillis();
            case "Active":
                return reservation -> reservation.getHour().getStartTime() < System.currentTimeMillis() && reservation.getHour().getEndTime() > System.currentTimeMillis();
            default:
                return reservation -> true;
        }
    }

    private void cancelReservationReminder(Reservation reservation) {
        getContext();
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), EndReservationReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), reservation.getId().hashCode(), intent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(pendingIntent);

        intent = new Intent(getContext(), StartReservationReminderReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getContext(), reservation.getId().hashCode(), intent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(pendingIntent);
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
                    cancelReservationReminder(filteredReservationsList.get(finalI));
                });

                reservationCard.setEditBtnOnClickListener(v -> {
                    EditFragment editFragment = EditFragment.newInstance(filteredReservationsList.get(finalI));
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

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getContext(), R.array.reservation_options, android.R.layout.simple_dropdown_item_1line);
        AutoCompleteTextView autoCompleteTextView = view.findViewById(R.id.dropdown_menu_item);
        if(mainViewModel.getFilter().getValue() != null) {
            autoCompleteTextView.setText(mainViewModel.getFilter().getValue());
        } else {
            autoCompleteTextView.setText(arrayAdapter.getItem(0));
        }
        autoCompleteTextView.setAdapter(arrayAdapter);


        ((AutoCompleteTextView) view.findViewById(R.id.dropdown_menu_item)).setOnItemClickListener((parent, view, position, id) -> {
            String selectedOption = parent.getItemAtPosition(position).toString();
            Log.d("ResFragment", "Selected option: " + selectedOption);
            generateReservationsCards(mainViewModel.getUserReservations().getValue(), reservations, selectedOption);
        });

        mainViewModel.getUserReservations().observe(getViewLifecycleOwner(), reservationsList -> {
            String selectedOption = ((AutoCompleteTextView) view.findViewById(R.id.dropdown_menu_item)).getText().toString();
            generateReservationsCards(reservationsList, reservations, selectedOption);
        });

        mainViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                mainViewModel.getError().setValue(null);
            }
        });

        mainViewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                mainViewModel.getSuccessMessage().setValue(null);
            }
        });

        ((SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh)).setOnRefreshListener(() -> {
            mainViewModel.loadUserReservations();
            ((SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh)).setRefreshing(false);
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mainViewModel.setFilter(((AutoCompleteTextView) view.findViewById(R.id.dropdown_menu_item)).getText().toString());
    }
}