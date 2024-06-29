package com.lksnext.parkingagarcia;

import android.util.Pair;
import android.view.View;
import android.widget.GridLayout;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.CompositeDateValidator;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.lksnext.parkingagarcia.domain.Place;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utils {

    private Utils() {
        throw new IllegalStateException("Utility class");
    }

    static Place[][] parking = {
            {new Place(1, Place.Type.ELECTRIC), new Place(2, Place.Type.ELECTRIC), new Place(3, Place.Type.ELECTRIC), new Place(4, Place.Type.ELECTRIC), new Place(5, Place.Type.ELECTRIC), new Place(6, Place.Type.ELECTRIC), new Place(7, Place.Type.ELECTRIC), new Place(8, Place.Type.ELECTRIC), new Place(9, Place.Type.ELECTRIC), new Place(10, Place.Type.ELECTRIC)},
            {new Place(11, Place.Type.DISABLED), new Place(12, Place.Type.NORMAL), new Place(13, Place.Type.NORMAL), new Place(14, Place.Type.NORMAL), new Place(15, Place.Type.NORMAL), new Place(16, Place.Type.NORMAL), new Place(17, Place.Type.NORMAL), new Place(18, Place.Type.NORMAL), new Place(19, Place.Type.NORMAL), new Place(20, Place.Type.DISABLED)},
            {new Place(21, Place.Type.DISABLED), new Place(22, Place.Type.NORMAL), new Place(23, Place.Type.NORMAL), new Place(24, Place.Type.NORMAL), new Place(25, Place.Type.NORMAL), new Place(26, Place.Type.NORMAL), new Place(27, Place.Type.NORMAL), new Place(28, Place.Type.NORMAL), new Place(29, Place.Type.NORMAL), new Place(30, Place.Type.DISABLED)},
            {new Place(31, Place.Type.DISABLED), new Place(32, Place.Type.NORMAL), new Place(33, Place.Type.NORMAL), new Place(34, Place.Type.NORMAL), new Place(35, Place.Type.NORMAL), new Place(36, Place.Type.NORMAL), new Place(37, Place.Type.NORMAL), new Place(38, Place.Type.NORMAL), new Place(39, Place.Type.NORMAL), new Place(40, Place.Type.DISABLED)},
            {new Place(41, Place.Type.DISABLED), new Place(42, Place.Type.NORMAL), new Place(43, Place.Type.NORMAL), new Place(44, Place.Type.NORMAL), new Place(45, Place.Type.NORMAL), new Place(46, Place.Type.NORMAL), new Place(47, Place.Type.NORMAL), new Place(48, Place.Type.NORMAL), new Place(49, Place.Type.NORMAL), new Place(50, Place.Type.DISABLED)},
            {new Place(51, Place.Type.DISABLED), new Place(52, Place.Type.NORMAL), new Place(53, Place.Type.NORMAL), new Place(54, Place.Type.NORMAL), new Place(55, Place.Type.NORMAL), new Place(56, Place.Type.NORMAL), new Place(57, Place.Type.NORMAL), new Place(58, Place.Type.NORMAL), new Place(59, Place.Type.NORMAL), new Place(60, Place.Type.DISABLED)},
            {new Place(61, Place.Type.DISABLED), new Place(62, Place.Type.NORMAL), new Place(63, Place.Type.NORMAL), new Place(64, Place.Type.NORMAL), new Place(65, Place.Type.NORMAL), new Place(66, Place.Type.NORMAL), new Place(67, Place.Type.NORMAL), new Place(68, Place.Type.NORMAL), new Place(69, Place.Type.NORMAL), new Place(70, Place.Type.DISABLED)},
            {new Place(71, Place.Type.DISABLED), new Place(72, Place.Type.NORMAL), new Place(73, Place.Type.NORMAL), new Place(74, Place.Type.NORMAL), new Place(75, Place.Type.NORMAL), new Place(76, Place.Type.NORMAL), new Place(77, Place.Type.NORMAL), new Place(78, Place.Type.NORMAL), new Place(79, Place.Type.NORMAL), new Place(80, Place.Type.DISABLED)},
            {new Place(81, Place.Type.DISABLED), new Place(82, Place.Type.NORMAL), new Place(83, Place.Type.NORMAL), new Place(84, Place.Type.NORMAL), new Place(85, Place.Type.NORMAL), new Place(86, Place.Type.NORMAL), new Place(87, Place.Type.NORMAL), new Place(88, Place.Type.NORMAL), new Place(89, Place.Type.NORMAL), new Place(90, Place.Type.DISABLED)},
            {new Place(91, Place.Type.MOTORCYCLE), new Place(92, Place.Type.MOTORCYCLE), new Place(93, Place.Type.MOTORCYCLE), new Place(94, Place.Type.MOTORCYCLE), new Place(95, Place.Type.MOTORCYCLE), new Place(96, Place.Type.MOTORCYCLE), new Place(97, Place.Type.MOTORCYCLE), new Place(98, Place.Type.MOTORCYCLE), new Place(99, Place.Type.MOTORCYCLE), new Place(100, Place.Type.MOTORCYCLE)},
    };

    public static Place[][] getParking() {
        return parking;
    }

    public static void chargeParking(GridLayout container, View.OnClickListener listener, Fragment fragment) {
        for (Place[] row : parking) {
            for (Place place : row) {
                MaterialButton btn = new MaterialButton(fragment.requireContext(), null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
                btn.setText(String.valueOf(place.getId()));
                switch (place.getType()) {
                    case ELECTRIC:
                        btn.setIconResource(R.drawable.ic_electric);
                        break;
                    case NORMAL:
                        btn.setIconResource(R.drawable.ic_normal);
                        break;
                    case DISABLED:
                        btn.setIconResource(R.drawable.ic_disabled);
                        break;
                    case MOTORCYCLE:
                        btn.setIconResource(R.drawable.ic_motorcycle);
                        break;
                }

                btn.setIconGravity(MaterialButton.ICON_GRAVITY_TEXT_TOP);
                btn.setIconSize(70);
                btn.setTextSize(18);
                btn.setId((int) place.getId());
                btn.setOnClickListener(listener);
                container.addView(btn);
            }
        }
    }

    public static String formatTimeToHHmm(long millis, Locale l) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", l);
        return formatter.format(new Date(millis));
    }

    public static int hourDiff(int startHour, int endHour) {
        if (endHour < startHour) {
            return 24 - startHour + endHour;
        } else {
            return endHour - startHour;
        }
    }

    public static void setEmptyError(MutableLiveData<String> fieldError, String value, String errorMsg) {
        if (value.isEmpty()) {
            fieldError.setValue(errorMsg);
        } else {
            fieldError.setValue(null);
        }
    }

    public static MaterialDatePicker<Long> datePickerDialog() {
        CalendarConstraints.DateValidator min = DateValidatorPointForward.from(MaterialDatePicker.todayInUtcMilliseconds());
        CalendarConstraints.DateValidator max = DateValidatorPointBackward.before(MaterialDatePicker.todayInUtcMilliseconds() + 1000 * 60 * 60 * 24 * 7);
        ArrayList<CalendarConstraints.DateValidator> listValidators = new ArrayList<>();
        listValidators.add(min);
        listValidators.add(max);
        CalendarConstraints.DateValidator validators = CompositeDateValidator.allOf(listValidators);

        return MaterialDatePicker.Builder
                .datePicker()
                .setTitleText("Select a Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(new CalendarConstraints.Builder().setValidator(validators).build())
                .build();
    }

    public static Pair<Date, Date> getDates(Integer year, Integer month, Integer dayOfMonth, Integer startHour, Integer startMinute, Integer endHour, Integer endMinute) {
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

}
