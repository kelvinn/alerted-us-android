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
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getInstrumentation().getTargetContext());
    }

    public void testPopulateAlert() {

        onView(withId(R.id.email)).perform(typeText("test@alerted.us")).check(ViewAssertions.matches(withText("test@alerted.us")));
        onView(withId(R.id.password)).perform(typeText("password")).check(ViewAssertions.matches(withText("password")));
        onView(withId(R.id.email_sign_in_button)).perform(click());

        // Test that a token was received
        String result = sharedPref.getString("AlertedToken", "");
        assertEquals(result, "abcdefgh"); // This value is from Apiary

        // Test that the GCM was submitted OK
        Boolean gcm_result = sharedPref.getBoolean(String.valueOf(R.string.post_new_gmc_token), true);
        assertFalse(gcm_result);

        //Bundle extras = new Bundle();
        //extras.putString("message", "Test single notification");

        //NotificationService.saveToDB(extras);
        //NotificationService.sendToApp("NEW_CARD");

        List<AlertGson> alertGson = LocationService.getAlertFromApi("0.0", "1.0");
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

        // Click the item.
        onView(withText("Logout")).perform(click());
        onView(withId(R.id.email)).check(ViewAssertions.matches(isDisplayed()));
    }

    public void testReceiveGCM() {

        onView(withId(R.id.email)).perform(typeText("test@alerted.us")).check(ViewAssertions.matches(withText("test@alerted.us")));
        onView(withId(R.id.password)).perform(typeText("password")).check(ViewAssertions.matches(withText("password")));
        onView(withId(R.id.email_sign_in_button)).perform(click());
        Intent gcmIntent = new Intent();
        gcmIntent.putExtra("message", "Test single notification");

        ExternalReceiver r = new ExternalReceiver();
        // TODO put extras
        //gcmIntent.addFlags()
        r.onReceive(getInstrumentation().getTargetContext(), gcmIntent);

        // Check that the list of alerts comes up
        //onView(withId(R.id.myListImg)).check(ViewAssertions.matches(isDisplayed()));

        // Click on a card and see if the detail page comes up
        //onView(withId(R.id.card)).perform(click());
        //onView(withId(R.id.alert_summary)).check(ViewAssertions.matches(isDisplayed()));

    }
}