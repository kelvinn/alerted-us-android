package us.alerted.alerted;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * A login screen that offers login via email/password and via Google+ sign in.
 * <p/>
 * ************ IMPORTANT SETUP NOTES: ************
 * In order for Google+ sign in to work with your app, you must first go to:
 * https://developers.google.com/+/mobile/android/getting-started#step_1_enable_the_google_api
 * and follow the steps in "Step 1" to create an OAuth 2.0 client for your package.
 */
public class LoginActivity extends Activity implements LoaderCallbacks<Cursor>{

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private String TAG = this.getClass().getSimpleName();

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private Boolean mLoginResult;
    private View mProgressView;
    //private View mTextViewLoading;
    private TextView mTextViewLoading;
    private View mEmailLoginFormView;
    private SignInButton mPlusSignInButton;
    private View mSignOutButtons;
    private View mLoginFormView;
    private Boolean create_user;
    private Boolean cancel;
    private Bundle data;
    private Boolean success = false;

    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        // This 'data' object is able to query the meta data from the Manifest
        try {
            data = getApplicationContext().getPackageManager().getApplicationInfo(
                    getApplicationContext().getPackageName(),
                    PackageManager.GET_META_DATA).metaData;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        //populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);

        // I don't think this does anything
        /*
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.email_sign_in_button || id == R.id.email_sign_up_button || id == EditorInfo.IME_NULL) {
                    Boolean result = verifyDetails();
                    return result;
                }
                return false;
            }
        });
        */

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mTextViewLoading = (TextView)findViewById(R.id.textViewLoading);
        mEmailLoginFormView = findViewById(R.id.email_login_form);

        // This sets the Terms and Privacy to allow clickable links
        TextView tv = (TextView) findViewById(R.id.termsView);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        tv.setText(Html.fromHtml(getString(R.string.terms_and_conditions)));

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel = verifyDetails();
                mTextViewLoading.setText("Logging in...");
                if (!cancel) {
                    attemptLogin(false);
                }

            }
        });

        Button mEmailSignUpButton = (Button) findViewById(R.id.email_sign_up_button);
        mEmailSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel = verifyDetails();
                mTextViewLoading.setText("Creating account...");
                if (!cancel) {
                    attemptLogin(true);
                }
            }
        });

    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public boolean verifyDetails() {
        if (mAuthTask != null) {
            return false;
        }


        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (email.isEmpty() || password.isEmpty()){
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            if (focusView != null) {
                focusView.requestFocus();
            }
        }
        return cancel;

    }

    public void attemptLogin(Boolean create_user) {
        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.

        showProgress(true);
        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        mAuthTask = new UserLoginTask(email, password, create_user);
        mAuthTask.execute((Void) null);
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mTextViewLoading.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mTextViewLoading.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    /*
    @Override
    protected void onPlusClientBlockingUI(boolean show) {
        showProgress(show);
    }


    @Override
    protected void updateConnectButtonState() {
        //TODO: Update this logic to also handle the user logged in by email.
        //boolean connected = getPlusClient().isConnected();

        //mSignOutButtons.setVisibility(connected ? View.VISIBLE : View.GONE);
        //mPlusSignInButton.setVisibility(connected ? View.GONE : View.VISIBLE);
        //mEmailLoginFormView.setVisibility(connected ? View.GONE : View.VISIBLE);
    }
    */


    /**
     * Check if the device supports Google Play Services.  It's best
     * practice to check first rather than handling this as an error case.
     *
     * @return whether the device supports Google Play Services
     */
    private boolean supportsGooglePlayServices() {
        return GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) ==
                ConnectionResult.SUCCESS;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                                                                     .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    public boolean getLoginResult(){
        return mLoginResult;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final Boolean mCreateUser;
        private URL mUrl;
        private String apiUrl;

        //SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences();

        UserLoginTask(String email, String password, Boolean create_user) {
            mEmail = email;
            mPassword = password;
            mCreateUser = create_user;
        }



        private String getResponseText(InputStream inStream) {
            // very nice trick from
            // http://weblogs.java.net/blog/pat/archive/2004/10/stupid_scanner_1.html
            return new Scanner(inStream).useDelimiter("\\A").next();
        }


        protected void createAccount(){
            List<NameValuePair> urlParams = new ArrayList<NameValuePair>();
            urlParams.add(new BasicNameValuePair("username", mEmail));
            urlParams.add(new BasicNameValuePair("password", mPassword));
            urlParams.add(new BasicNameValuePair("email", mEmail));

            JSONObject postData = new JSONObject();

            try {
                postData.put("username",mEmail);
                postData.put("password",mPassword);
                postData.put("email", mEmail);
            }  catch(JSONException e) {
                Log.e(TAG, e.toString());
            }


            try {
                String apiUrl;

                if (BuildConfig.DEBUG) {
                    apiUrl = data.getString("api.url.test.users");
                } else {
                    apiUrl = data.getString("api.url.prod.users");
                }

                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                //writer.write(getQuery(urlParams));
                Log.i("us.alerted.us", postData.toString());
                writer.write(postData.toString());
                writer.flush();
                writer.close();
                os.close();

                conn.connect();

                // create JSON object from content
                InputStream in = new BufferedInputStream(
                        conn.getInputStream());

                /*
                try {
                    JSONObject jsonToken = new JSONObject(getResponseText(in));
                } catch(JSONException e) {
                    Log.e(TAG, e.toString());
                }
                */


            } catch (MalformedURLException e) {
                Log.e(TAG, e.toString());
            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
        }
        protected boolean getToken(){
            List<NameValuePair> urlParams = new ArrayList<NameValuePair>();
            urlParams.add(new BasicNameValuePair("username", mEmail));
            urlParams.add(new BasicNameValuePair("password", mPassword));

            JSONObject postData = new JSONObject();

            try {
                postData.put("username",mEmail);
                postData.put("password",mPassword);
            }  catch(JSONException e) {
                Log.e(TAG, e.toString());
            }

            try {

                if (BuildConfig.DEBUG) {
                    apiUrl = data.getString("api.url.test.token");
                } else {
                    apiUrl = data.getString("api.url.prod.token");
                }

                mUrl = new URL(apiUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            try {
                HttpURLConnection conn = (HttpURLConnection) mUrl.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                //writer.write(getQuery(urlParams));
                Log.i("us.alerted.us", postData.toString());
                writer.write(postData.toString());
                writer.flush();
                writer.close();
                os.close();

                conn.connect();

                // create JSON object from content
                InputStream in = new BufferedInputStream(
                        conn.getInputStream());
                int code = conn.getResponseCode();
                Log.i(TAG, String.valueOf(code));
                if (code == 200){
                    try {
                        JSONObject jsonToken = new JSONObject(getResponseText(in));
                        SharedPreferences.Editor editor = sharedPref.edit();
                        String httpAuthToken = jsonToken.get("token").toString();
                        editor.putString("AlertedToken", httpAuthToken);
                        editor.putBoolean("userLoggedInState", true);
                        editor.apply();
                        return true;
                    } catch(JSONException e) {
                        Log.e(TAG, e.toString());
                    }
                }


            } catch (MalformedURLException e) {
                Log.e(TAG, e.toString());

            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
            return false;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean result;
            if (!mCreateUser){
                result = getToken();

                if (result) {
                    // Because an account already exists we do not need to create a new SNS token
                    // setting this to 'false' means the SNS token will just be updated
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean(getString(R.string.post_new_gmc_token), false);
                    editor.apply();
                }

            } else {
                createAccount();
                result = getToken();
            }
            return result;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                mLoginResult = true; // This is for unit tests
                startService(new Intent(getApplicationContext(), NotificationService.class));
                startService(new Intent(getApplicationContext(), LocationService.class));

                Intent i = new Intent(getBaseContext(), MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);

            } else {
                mLoginResult = false; // This is for unit tests
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }


        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}



