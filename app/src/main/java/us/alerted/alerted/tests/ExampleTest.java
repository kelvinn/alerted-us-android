package us.alerted.alerted.tests;

import java.util.concurrent.CountDownLatch;

import android.test.ActivityInstrumentationTestCase2;
import android.test.InstrumentationTestCase;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.mockwebserver.MockResponse;
import com.google.mockwebserver.MockWebServer;

import java.net.URL;
import java.util.concurrent.TimeUnit;

import us.alerted.alerted.LoginActivity;
import us.alerted.alerted.R;

/**
 * Created by kelvinn on 12/08/2014.
 */
public class ExampleTest extends ActivityInstrumentationTestCase2<LoginActivity> {
    private LoginActivity mActivity;
    private TextView mEmailView;
    private TextView mPasswordView;
    private String resourceString;
    private Button mEmailSignInButton;
    private Boolean mLoginResult;
    private LoginActivity.UserLoginTask mAuthTask = null;

    private MockWebServer server = new MockWebServer();


    public ExampleTest() {
        super("us.alerted.alerted", LoginActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mActivity = this.getActivity();
        mEmailView = (TextView) mActivity.findViewById
                (us.alerted.alerted.R.id.email);
        mPasswordView = (TextView) mActivity.findViewById
                (us.alerted.alerted.R.id.password);
        mEmailSignInButton = (Button) mActivity.findViewById(
                R.id.email_sign_in_button);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        //mAuthTask = new LoginActivity.UserLoginTask(email, password, false);


        // Schedule some responses.
        server.enqueue(new MockResponse().setBody("T8lj04fKrg7NZUkWN8V1a0DpQ6ZJ8FgVsCVjLBy2g6GOHuX5VPsZZaTU8wOQAawL"));

        // Start the server.
        server.play();

        // Ask the server for its URL. You'll need this to make HTTP requests.
        URL baseUrl = server.getUrl("/api-token-auth/");

    }

    public void testPreconditions() {
        assertNotNull(mEmailView);
        assertNotNull(mPasswordView);
        assertNotNull(mEmailSignInButton);
    }

    public void testExecute() throws Throwable {

        runTestOnUiThread(new Runnable() {
            public void run() {
                mEmailView.setText("alertedustestuser@alerted.us");
                mPasswordView.setText("1DSYWsCsw249k9EuNSIW");
                //mActivity.attemptLogin(false);
                //mAuthTask.execute();

                //mAuthTask.execute();

            }
        });

        assertNotNull(mActivity);
        // To wait for the AsyncTask to complete, you can safely call get() from the test thread
        //mAuthTask.get();


    }

    /*
    public void test() throws Exception {
        final int expected = 1;
        final int reality = 5;
        assertEquals(expected, reality);
    }
    */
}






