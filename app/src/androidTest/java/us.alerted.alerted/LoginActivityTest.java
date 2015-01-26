package us.alerted.alerted;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;

import android.support.test.espresso.assertion.ViewAssertions;

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

    public void testLoginEmptyUsernameDenied() {
        onView(withId(R.id.email_sign_in_button)).perform(click());
        onView(withId(R.id.email)).check(ViewAssertions.matches(isDisplayed()));
    }

    public void testLoginInvalidUsernameDenied() {
        onView(withId(R.id.email)).perform(typeText("test")).check(ViewAssertions.matches(withText("test")));
        onView(withId(R.id.email_sign_in_button)).perform(click());
        onView(withId(R.id.email)).check(ViewAssertions.matches(isDisplayed()));
    }

    public void testLoginInvalidPasswordDenied() {
        onView(withId(R.id.email)).perform(typeText("test@alerted.us")).check(ViewAssertions.matches(withText("test@alerted.us")));
        onView(withId(R.id.password)).perform(typeText("invalid")).check(ViewAssertions.matches(withText("invalid")));
        onView(withId(R.id.email_sign_in_button)).perform(click());
        onView(withId(R.id.email)).check(ViewAssertions.matches(isDisplayed()));
    }

    public void testLoginInvalidPasswordDeniedTooShort() {
        onView(withId(R.id.email)).perform(typeText("test@alerted.us")).check(ViewAssertions.matches(withText("test@alerted.us")));
        onView(withId(R.id.password)).perform(typeText("abc")).check(ViewAssertions.matches(withText("abc")));
        onView(withId(R.id.email_sign_in_button)).perform(click());
        onView(withId(R.id.email)).check(ViewAssertions.matches(isDisplayed()));
    }

    public void testLoginEmptyPasswordDenied() {
        onView(withId(R.id.email)).perform(typeText("test@alerted.us")).check(ViewAssertions.matches(withText("test@alerted.us")));
        onView(withId(R.id.email_sign_in_button)).perform(click());
        onView(withId(R.id.email)).check(ViewAssertions.matches(isDisplayed()));
    }

    public void testCreateAccount() {

        onView(withId(R.id.email)).perform(typeText("test@alerted.us")).check(ViewAssertions.matches(withText("test@alerted.us")));
        onView(withId(R.id.password)).perform(typeText("password")).check(ViewAssertions.matches(withText("password")));
        onView(withId(R.id.email_sign_up_button)).perform(click());

        // Open the overflow menu OR open the options menu,
        // depending on if the device has a hardware or software overflow menu button.
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        // Click the item.
        onView(withText("Logout")).perform(click());
        onView(withId(R.id.email)).check(ViewAssertions.matches(isDisplayed()));
    }

    public void testLoginEmptyUsernameSignUpDenied() {
        onView(withId(R.id.email_sign_up_button)).perform(click());
        onView(withId(R.id.email)).check(ViewAssertions.matches(isDisplayed()));
    }

    public void testLoginInvalidUsernameSignUpDenied() {
        onView(withId(R.id.email)).perform(typeText("test")).check(ViewAssertions.matches(withText("test")));
        onView(withId(R.id.email_sign_up_button)).perform(click());
        onView(withId(R.id.email)).check(ViewAssertions.matches(isDisplayed()));
    }

    public void testLoginEmptyPasswordSignUpDenied() {
        onView(withId(R.id.email)).perform(typeText("test@alerted.us")).check(ViewAssertions.matches(withText("test@alerted.us")));
        onView(withId(R.id.email_sign_up_button)).perform(click());
        onView(withId(R.id.email)).check(ViewAssertions.matches(isDisplayed()));
    }

}