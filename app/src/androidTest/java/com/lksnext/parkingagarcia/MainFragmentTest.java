package com.lksnext.parkingagarcia;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.google.common.base.CharMatcher.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;

import static java.lang.Thread.sleep;

import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;

import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lksnext.parkingagarcia.data.DataRepository;
import com.lksnext.parkingagarcia.domain.Callback;
import com.lksnext.parkingagarcia.view.activity.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainFragmentTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);
    private MainActivity activity;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private static final String TAG = "MainFragmentTest";

    @Before
    public void setUp() {
        activityRule.getScenario().onActivity(activity -> this.activity = activity);
        auth.createUserWithEmailAndPassword("test@test.com", "test1234").addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "User created");
            } else {
                Log.d(TAG, "User not created");
                throw new RuntimeException("User not created");
            }
        });
    }

    @After
    public void tearDown() {
        auth.getCurrentUser().delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "User deleted");
            } else {
                Log.d(TAG, "User not deleted");
                throw new RuntimeException("User not deleted");
            }
        });
    }

    @Test
    public void testReserve() throws Exception {
        onView(withId(R.id.dateText)).perform(click());
        onView(allOf(
                isDescendantOfA(withTagValue(equalTo("MONTHS_VIEW_GROUP_TAG"))),
                withContentDescription("Thursday, June 27")
        )).perform(click());
        onView(withId(com.google.android.material.R.id.confirm_button)).perform(click());
        onView(withId(R.id.dateText)).check(matches(withText("27/6/2024")));

        onView(withId(R.id.startTimeText)).perform(click());
        onView(withId(com.google.android.material.R.id.material_timepicker_ok_button)).perform(click());
        onView(withId(R.id.startTimeText)).check(matches(withText("12:00")));

        onView(withId(R.id.endTimeText)).perform(click());
        onView(withId(com.google.android.material.R.id.material_hour_text_input)).perform(click());
        onView(allOf(
                withText("12"),
                isDisplayed()
        )).perform(replaceText("14"));
        onView(withId(com.google.android.material.R.id.material_timepicker_ok_button)).perform(click());
        onView(withId(R.id.endTimeText)).check(matches(withText("14:00")));

        onView(withId(1)).perform(click());
        onView(withId(R.id.btnReserve)).perform(click());

        onView(withId(R.id.reservations)).perform(click());
        onView(withId(R.id.dropdown_menu_item)).perform(click());
        onView(withText("Future")).inRoot(RootMatchers.isPlatformPopup()).perform(click());
        onView(withText("Cancel")).perform(click());
        sleep(1000);
    }
}
