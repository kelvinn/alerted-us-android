package us.alerted.alerted;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.espresso.assertion.ViewAssertions;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;

@LargeTest
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public static SharedPreferences sharedPref;
    private LocationService mReceiver = new LocationService();

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


    public void testPostNotification() {
        List<AlertGson> result = mReceiver.getAlertFromApi("0.0", "1.0", "2014-02-17T08:03:21.156421");
        Boolean saveResult = mReceiver.saveAlertToDB(result.get(0));
        assertTrue(saveResult);

    }

    public void testSaveAlert() {
        Alert.deleteAll(Alert.class);
        List<AlertGson> alertGson = mReceiver.getAlertFromApi("0.0", "1.0", "2014-02-17T08:03:21.156421");
        assertNotNull(alertGson);

        Boolean saveResult = mReceiver.saveAlertToDB(alertGson.get(0)); // we just show one for now
        assertTrue(saveResult);

        mReceiver.sendToApp("NEW_CARD");

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