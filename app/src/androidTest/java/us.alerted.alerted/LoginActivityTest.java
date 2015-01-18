package us.alerted.alerted;


import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;

//import com.squareup.okhttp.mockwebserver.MockResponse;
//import com.squareup.okhttp.mockwebserver.MockWebServer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.Result;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import static org.junit.Assert.assertThat;

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


    public void testLoginAbility() {
        /*
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody("{'token': 'aa173cb1dcfabdb7af7c611c5f048f56d8975d09'}"));

        try {
            server.play();

        } catch (IOException e) {
            e.printStackTrace();
        }

        URL baseUrl = server.getUrl("/api-token-auth/");


        stubFor(post(urlEqualTo("/api-token-auth/"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{'token': 'aa173cc1dcfabdb7af7c311a5f028f86d8975d09'}")));
*/

        onView(withId(R.id.email)).perform(typeText("test@alerted.us")).check(ViewAssertions.matches(withText("test@alerted.us")));
        onView(withId(R.id.password)).perform(typeText("password")).check(ViewAssertions.matches(withText("password")));
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