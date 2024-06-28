package com.lksnext.parkingagarcia.view.fragment;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.CompositeDateValidator;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.lksnext.parkingagarcia.Utils;
import com.lksnext.parkingagarcia.R;
import com.lksnext.parkingagarcia.domain.Hour;
import com.lksnext.parkingagarcia.domain.Place;
import com.lksnext.parkingagarcia.domain.Reservation;
import com.lksnext.parkingagarcia.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditFragment extends DialogFragment {

    private Reservation reservation;
    private View view;
    private MaterialButton selectedButton;
    private Place selectedPlace;
    private MainViewModel mainViewModel;
    private Integer startHour, startMinute, endHour, endMinute, year, month, dayOfMonth;
    private static final String TAG = "EditFragment";

    public EditFragment() {
        // Required empty public constructor
    }

    public static EditFragment newInstance(Reservation reservation) {
        EditFragment fragment = new EditFragment();
        fragment.reservation = reservation;
        fragment.selectedPlace = reservation.getPlace();
        fragment.dayOfMonth = Integer.valueOf(reservation.getDate().split("/")[0]);
        fragment.month = Integer.valueOf(reservation.getDate().split("/")[1]);
        fragment.year = Integer.valueOf(reservation.getDate().split("/")[2]);
        return fragment;
    }

    private void disablePlaces(List<Reservation> reservations) {
        Pair<Date, Date> dates = getDates();
        for (Reservation r : reservations) {
            Log.d(TAG, "Checking reservation " + r.getId() + " startTime: " + r.getHour().getStartTime() + " endTime: " + r.getHour().getEndTime() + " startDate: " + dates.first.getTime() + " endDate: " + dates.second.getTime());
            if (r.getHour().isOverlapping(new Hour(dates.first, dates.second)) && !reservation.getId().equals(r.getId())) {
                Log.d(TAG, "Disabling place " + (int) r.getPlace().getId());
                MaterialButton btn = view.findViewById((int) r.getPlace().getId());
                Log.d(TAG, "Button: " + btn.getText());
                btn.setEnabled(false);
                if (selectedButton != null && btn.getId() == selectedButton.getId()) {
                    selectedButton.setStrokeColor(ColorStateList.valueOf(getResources().getColor(com.google.android.material.R.color.material_grey_300, null)));
                    selectedButton = null;
                    selectedPlace = null;
                }
            } else {
                view.findViewById((int) r.getPlace().getId()).setEnabled(true);
            }
        }
    }

    private Pair<Date, Date> getDates() {
        if (year == null || month == null || dayOfMonth == null || startHour == null || endHour == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, dayOfMonth, startHour, startMinute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date startDate = cal.getTime();

        if (endHour < startHour) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        cal.set(Calendar.HOUR_OF_DAY, endHour);
        cal.set(Calendar.MINUTE, endMinute);
        Date endDate = cal.getTime();

        return new Pair<>(startDate, endDate);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_edit, container, false);
        mainViewModel = new ViewModelProvider(requireParentFragment()).get(MainViewModel.class);

        Locale l = getResources().getConfiguration().getLocales().get(0);
        startHour = Integer.valueOf(Utils.formatTimeToHHmm(reservation.getHour().getStartTime(), l).substring(0, 2));
        startMinute = Integer.valueOf(Utils.formatTimeToHHmm(reservation.getHour().getStartTime(), l).substring(4));
        endHour = Integer.valueOf(Utils.formatTimeToHHmm(reservation.getHour().getEndTime(), l).substring(0, 2));
        endMinute = Integer.valueOf(Utils.formatTimeToHHmm(reservation.getHour().getEndTime(), l).substring(4));

        GridLayout parkingContainer = view.findViewById(R.id.glParking);
        parkingContainer.setRowCount(Utils.getParking().length);
        parkingContainer.setColumnCount(Utils.getParking()[0].length);

        Utils.chargeParking(parkingContainer, v -> {
            MaterialButton btn = (MaterialButton) v;
            if (selectedButton != null) {
                selectedButton.setStrokeColor(ColorStateList.valueOf(getResources().getColor(com.google.android.material.R.color.material_grey_300, null)));
            }
            btn.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.orange, null)));
            selectedButton = btn;
            selectedPlace = Utils.getParking()[(btn.getId() - 1) / 10][(btn.getId() - 1) % 10];
        }, this);

        selectedButton = parkingContainer.findViewById((int) selectedPlace.getId());
        selectedButton.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.orange, null)));

        EditText startTimeText = view.findViewById(R.id.startTimeText);
        EditText endTimeText = view.findViewById(R.id.endTimeText);
        EditText dateText = view.findViewById(R.id.dateText);

        dateText.setText(reservation.getDate());
        startTimeText.setText(Utils.formatTimeToHHmm(reservation.getHour().getStartTime(), getResources().getConfiguration().getLocales().get(0)));
        endTimeText.setText(Utils.formatTimeToHHmm(reservation.getHour().getEndTime(), getResources().getConfiguration().getLocales().get(0)));

        view.findViewById(R.id.dateText).setOnClickListener(v -> {
            CalendarConstraints.DateValidator min = DateValidatorPointForward.from(MaterialDatePicker.todayInUtcMilliseconds());
            CalendarConstraints.DateValidator max = DateValidatorPointBackward.before(MaterialDatePicker.todayInUtcMilliseconds() + 1000 * 60 * 60 * 24 * 7);
            ArrayList<CalendarConstraints.DateValidator> listValidators = new ArrayList<>();
            listValidators.add(min);
            listValidators.add(max);
            CalendarConstraints.DateValidator validators = CompositeDateValidator.allOf(listValidators);

            MaterialDatePicker<Long> picker = MaterialDatePicker.Builder
                    .datePicker()
                    .setTitleText("Select a Date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .setCalendarConstraints(new CalendarConstraints.Builder().setValidator(validators).build())
                    .build();

            picker.addOnPositiveButtonClickListener(selection -> {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(selection);
                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH) + 1;
                dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
                dateText.setText(dayOfMonth + "/" + month + "/" + year);
                mainViewModel.loadReservationsForDate(dayOfMonth + "/" + month + "/" + year);
            });

            picker.show(getParentFragmentManager(), picker.toString());
        });

        startTimeText.setOnClickListener(v -> {
            MaterialTimePicker picker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setTitleText("Select Start Time")
                    .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
                    .build();

            picker.addOnPositiveButtonClickListener(view1 -> {
                startHour = picker.getHour();
                startMinute = picker.getMinute();

                startTimeText.setText(String.format("%02d:%02d", startHour, startMinute));
            });

            picker.show(getParentFragmentManager(), picker.toString());
        });

        endTimeText.setOnClickListener(v -> {
            MaterialTimePicker picker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setTitleText("Select End Time")
                    .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
                    .build();

            picker.addOnPositiveButtonClickListener(view1 -> {
                int newEndHour = picker.getHour();
                int newEndMinute = picker.getMinute();

                if (newEndHour == startHour && newEndMinute <= startMinute) {
                    endTimeText.setError("End time must be after start time");
                    Toast.makeText(getContext(), "End time must be after start time", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Utils.hourDiff(startHour, newEndHour) > 9 || (newEndHour - startHour == 9 && newEndMinute > startMinute)) {
                    endTimeText.setError("The maximum duration is 8 hours");
                    Toast.makeText(getContext(), "The maximum duration is 8 hours", Toast.LENGTH_SHORT).show();
                    return;
                }

                mainViewModel.loadReservationsForDate(String.valueOf(dateText.getText()));

                endHour = newEndHour;
                endMinute = newEndMinute;
                endTimeText.setError(null);
                endTimeText.setText(String.format("%02d:%02d", endHour, endMinute));
            });

            picker.show(getParentFragmentManager(), picker.toString());
        });

        view.findViewById(R.id.btnConfirm).setOnClickListener(v -> {
            if (selectedPlace == null) {
                Toast.makeText(getContext(), "Select a place", Toast.LENGTH_SHORT).show();
                return;
            }
            reservation.setPlace(selectedPlace);
            reservation.setDate(((EditText) view.findViewById(R.id.dateText)).getText().toString());
            Pair<Date, Date> dates = getDates();
            reservation.getHour().setStartTime(dates.first.getTime());
            reservation.getHour().setEndTime(dates.second.getTime());
            String prevId = reservation.getId();
            reservation.setId(selectedPlace.getId() + "-" + reservation.getDate() + "-" + reservation.getHour().getStartTime() + "-" + reservation.getHour().getEndTime());
            mainViewModel.editUserReservation(reservation, prevId);
            dismiss();
        });

        view.findViewById(R.id.btnCancel).setOnClickListener(v -> dismiss());

        mainViewModel.getReservationsForDate().observe(getViewLifecycleOwner(), this::disablePlaces);
        mainViewModel.loadReservationsForDate(reservation.getDate());

        return view;
    }
}