package com.lksnext.parkingagarcia.view.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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
import com.lksnext.parkingagarcia.domain.EndReservationReminderReceiver;
import com.lksnext.parkingagarcia.domain.Hour;
import com.lksnext.parkingagarcia.domain.Place;
import com.lksnext.parkingagarcia.domain.Reservation;
import com.lksnext.parkingagarcia.domain.StartReservationReminderReceiver;
import com.lksnext.parkingagarcia.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainFragment extends Fragment {
    Integer year, month, dayOfMonth, startHour, endHour, startMinute, endMinute;

    MainViewModel mainViewModel;

    Place selectedPlace;
    MaterialButton selectedButton;
    View view;

    public MainFragment() {
        // Es necesario un constructor vacio
    }

    private boolean checkNulls() {
        boolean ret = false;
        if (selectedPlace == null) {
            Toast.makeText(getContext(), "Select a place", Toast.LENGTH_SHORT).show();
            ret = true;
        }
        if (year == null || month == null || dayOfMonth == null) {
            ((EditText) view.findViewById(R.id.dateText)).setError("Select a date");
            ret = true;
        } else {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month - 1, dayOfMonth);
            if (cal.before(Calendar.getInstance())) {
                ((EditText) view.findViewById(R.id.dateText)).setError("Select a future date");
                ret = true;
            } else {
                ((EditText) view.findViewById(R.id.dateText)).setError(null);
            }
        }
        if (startHour == null) {
            ((EditText) view.findViewById(R.id.startTimeText)).setError("Select a start time");
            ret = true;
        } else {
            ((EditText) view.findViewById(R.id.startTimeText)).setError(null);
        }
        if (endHour == null) {
            ((EditText) view.findViewById(R.id.endTimeText)).setError("Select an end time");
            ret = true;
        } else {
            ((EditText) view.findViewById(R.id.endTimeText)).setError(null);
        }
        return ret;
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

    private void resetFields() {
        selectedButton.setStrokeColor(ColorStateList.valueOf(getResources().getColor(com.google.android.material.R.color.material_grey_300, null)));
        selectedPlace = null;
        selectedButton = null;
        year = null;
        month = null;
        dayOfMonth = null;
        startHour = null;
        endHour = null;
        startMinute = null;
        endMinute = null;
        EditText t = view.findViewById(R.id.dateText);
        t.setText(null);
        t = view.findViewById(R.id.startTimeText);
        t.setText(null);
        t = view.findViewById(R.id.endTimeText);
        t.setText(null);
    }

    private void scheduleStartReservationReminder(long startTimeInMillis, String id) {
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), StartReservationReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), id.hashCode(), intent, PendingIntent.FLAG_IMMUTABLE);

        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, startTimeInMillis - 1000 * 60 * 30, pendingIntent);
    }

    private void scheduleEndReservationReminder(long endTimeInMillis, String id) {
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), EndReservationReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), id.hashCode(), intent, PendingIntent.FLAG_IMMUTABLE);

        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, endTimeInMillis - 1000 * 60 * 15, pendingIntent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        GridLayout glParking = view.findViewById(R.id.glParking);
        glParking.setRowCount(Utils.getParking().length);
        glParking.setColumnCount(Utils.getParking()[0].length);

        Utils.chargeParking(glParking, v -> {
            MaterialButton btn = (MaterialButton) v;
            if (selectedButton != null) {
                selectedButton.setStrokeColor(ColorStateList.valueOf(getResources().getColor(com.google.android.material.R.color.material_grey_300, null)));
            }
            btn.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.orange, null)));
            selectedButton = btn;
            selectedPlace = Utils.getParking()[(btn.getId() - 1) / 10][(btn.getId() - 1) % 10];
        }, this);

        EditText dateText = view.findViewById(R.id.dateText);
        dateText.setOnClickListener(v -> {
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
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH) + 1;
                int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

                if (startHour != null) {
                    cal.set(year, month - 1, dayOfMonth, startHour, startMinute);
                    if (cal.before(Calendar.getInstance())) {
                        dateText.setError("Select a future date");
                        Toast.makeText(getContext(), "Select a future date", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                this.year = year;
                this.month = month;
                this.dayOfMonth = dayOfMonth;
                dateText.setText(dayOfMonth + "/" + month + "/" + year);
                dateText.setError(null);
                if (startHour != null && endHour != null) {
                    mainViewModel.loadReservationsForDate(dayOfMonth + "/" + month + "/" + year);
                }
            });

            picker.show(getParentFragmentManager(), picker.toString());
        });

        EditText startTimeText = view.findViewById(R.id.startTimeText);
        EditText endTimeText = view.findViewById(R.id.endTimeText);

        startTimeText.setOnClickListener(v -> {
            MaterialTimePicker picker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(12)
                    .setMinute(0)
                    .setTitleText("Select Start Time")
                    .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
                    .build();

            picker.addOnPositiveButtonClickListener(view1 -> {
                int startHour = picker.getHour();
                int startMinute = picker.getMinute();

                if (year != null && month != null && dayOfMonth != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.set(year, month - 1, dayOfMonth, startHour, startMinute);
                    if (cal.before(Calendar.getInstance())) {
                        startTimeText.setError("Select a future time");
                        Toast.makeText(getContext(), "Select a future time", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        startTimeText.setError(null);
                    }
                }

                this.startHour = startHour;
                this.startMinute = startMinute;

                startTimeText.setText(String.format("%02d:%02d", startHour, startMinute));
                endTimeText.setEnabled(true);
            });

            picker.show(getParentFragmentManager(), picker.toString());
        });

        endTimeText.setOnClickListener(v -> {
            MaterialTimePicker picker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(startHour)
                    .setMinute(startMinute)
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

                if (month != null && dayOfMonth != null && year != null) {
                    mainViewModel.loadReservationsForDate(dayOfMonth + "/" + month + "/" + year);
                }

                endHour = newEndHour;
                endMinute = newEndMinute;
                endTimeText.setError(null);
                endTimeText.setText(String.format("%02d:%02d", endHour, endMinute));
            });

            picker.show(getParentFragmentManager(), picker.toString());
        });

        MaterialButton reserveButton = view.findViewById(R.id.btnReserve);
        reserveButton.setOnClickListener(v -> {
            if (checkNulls()) return;

            String date = dayOfMonth + "/" + month + "/" + year;
            String id = selectedPlace.getId() + "-" + date + "-" + startTimeText.getText() + "-" + endTimeText.getText();
            Pair<Date, Date> dates = getDates();

            mainViewModel.reserve(date, id, selectedPlace, dates.first, dates.second);
            scheduleStartReservationReminder(dates.first.getTime(), id);
            scheduleEndReservationReminder(dates.second.getTime(), id);
            resetFields();
        });

        mainViewModel.getError().observe(getViewLifecycleOwner(), error ->
            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show()
        );

        mainViewModel.getSuccessMessage().observe(getViewLifecycleOwner(), success ->
            Toast.makeText(getContext(), success, Toast.LENGTH_SHORT).show()
        );

        mainViewModel.getReservationsForDate().observe(getViewLifecycleOwner(), reservations -> {
            Pair<Date, Date> dates = getDates();
            for (Reservation r : reservations) {
                Log.d("MainFragment", "Checking reservation " + r.getId() + " startTime: " + r.getHour().getStartTime() + " endTime: " + r.getHour().getEndTime() + " startDate: " + dates.first.getTime() + " endDate: " + dates.second.getTime());
                if (r.getHour().isOverlapping(new Hour(dates.first, dates.second))) {
                    Log.d("MainFragment", "Disabling place " + (int) r.getPlace().getId());
                    MaterialButton btn = view.findViewById((int) r.getPlace().getId());
                    Log.d("MainFragment", "Button: " + btn.getText());
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
        });

        return view;
    }
}
