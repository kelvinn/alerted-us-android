package us.alerted.alerted;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.test.espresso.assertion.ViewAssertions;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;

@LargeTest
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public static SharedPreferences sharedPref;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        getActivity();
        Alert.deleteAll(Alert.class);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getInstrumentation().getTargetContext());
    }

    public void testPopulateAlert() {

        List<AlertGson> alertGson = LocationService.getAlertFromApi("0.0", "1.0", "2015-02-17T08:03:21.156421");
        Boolean saveResult = LocationService.saveAlertToDB(alertGson.get(0));
        assertTrue(saveResult);

        LocationService.sendToApp("NEW_CARD");

        // Check that the list of alerts comes up
        onView(withId(R.id.myListImg)).check(ViewAssertions.matches(isDisplayed()));

        // Check that the status is displayed (it should be inactive)
        onView(withId(R.id.status)).check(ViewAssertions.matches(withText("Inactive")));

        // Click on a card and see if the detail page comes up
        onView(withId(R.id.card)).perform(click());
        onView(withId(R.id.alert_summary)).check(ViewAssertions.matches(isDisplayed()));

        // Open the overflow menu OR open the options menu,
        // depending on if the device has a hardware or software overflow menu button.
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

    }

}