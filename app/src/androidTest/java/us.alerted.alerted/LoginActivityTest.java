package us.alerted.alerted;

import android.support.test.espresso.assertion.ViewAssertions;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;

@LargeTest
public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    public void testLoginAbility() {
        onView(withId(R.id.email)).perform(typeText("alertedus@gmail.com")).check(ViewAssertions.matches(withText("alertedus@gmail.com")));
        onView(withId(R.id.password)).perform(typeText("wBQIyzylJ")).check(ViewAssertions.matches(withText("wBQIyzylJ")));
        onView(withId(R.id.email_sign_in_button)).perform(click());
        //onView(withId(R.id.myListImg)).check(ViewAssertions.matches(isDisplayed()));

        // Open the overflow menu OR open the options menu,
        // depending on if the device has a hardware or software overflow menu button.
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        // Click the item.
        onView(withText("Logout")).perform(click());
        onView(withId(R.id.email)).check(ViewAssertions.matches(isDisplayed()));
        //onView(withText("Hello world!")).check(ViewAssertions.matches(isDisplayed()));
    }
}