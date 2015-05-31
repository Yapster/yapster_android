package co.yapster.yapster;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import 	android.content.Intent;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.json.parsers.JSONParser;
import com.json.parsers.JsonParserFactory;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class SignUpPage2Screen extends Activity implements
        View.OnClickListener {

	TextView textTitleWhatShouldTheUserDo;
	TextView textTitleWhyShouldTheUserDo;
	TextView textViewInputtedUsernameDetails;
	TextView textViewInputtedPassword1Details;
	TextView textViewInputtedPassword2Details;
	EditText editTextInputtedPassword1;
	EditText editTextInputtedPassword2;
	Button buttonCompleteSignUp;
	Button buttonSignIn;
    Intent intent;
    Bundle intentBundle;
    String first_name;
    String last_name;
    String email;
    String gender;
    String username;
    String inputtedPassword1;
    String inputtedPassword2;
    String password;
    String signUpResponseJSONString;
    Boolean signUpValidFlag;
    String user_id;
    String session_id;
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    static final String TAG = "SignUpPage2";
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;
    String regid;

    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    String SENDER_ID = "1004114511602";
    String APPLICATION_ID = "yapster-app";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up_page_2_screen);
        intent = getIntent();
        intentBundle = getIntent().getExtras();
		setUpAllElements();
	}

	private void setUpAllElements() {
		// TODO Auto-generated method stub
		editTextInputtedPassword1 = (EditText) findViewById(R.id.editTextInputtedPassword1);
		editTextInputtedPassword2 = (EditText) findViewById(R.id.editTextInputtedPassword2);
		buttonCompleteSignUp = (Button) findViewById(R.id.buttonCompleteSignUp);
		buttonSignIn = (Button) findViewById(R.id.buttonSignIn);

        first_name = intentBundle.getString("first_name");
        last_name = intentBundle.getString("last_name");
        username = intentBundle.getString("username");
        email = intentBundle.getString("email");
        gender = intentBundle.getString("gender");

	}

    @Override
    public void onClick(View view) {
        // TODO Auto-generated method stub

        switch (view.getId()) {
            case R.id.buttonCompleteSignUp:
                inputtedPassword1 = editTextInputtedPassword1.getText().toString();
                inputtedPassword2 = editTextInputtedPassword2.getText().toString();
                if (username.isEmpty() == false && inputtedPassword1.isEmpty() == false && inputtedPassword2.isEmpty() == false) {
                    if (inputtedPassword1.equals(inputtedPassword2)) {
                        apiSignUpCall();
                    } else {

                    }

                }
                break;

            case R.id.buttonSignIn:

                break;

            default:
                break;
        }
    }

    public void apiSignUpCall() {
        String api_url = "http://api.yapster.co/users/sign_up/";
        password = inputtedPassword1;
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);

            if (regid.isEmpty()) {
                registerInBackground();

            } else {
                Log.i(TAG, "No valid Google Play Services APK found.");
            }

            AsyncTask<String, Integer, String> api_sign_in_async_task = new SignUpAPICall()
                    .execute(api_url);
            JsonParserFactory factory = JsonParserFactory.getInstance();
            JSONParser parser = factory.newJsonParser();
            Map jsonMap = parser.parseJson(signUpResponseJSONString);
            String validFlag = (String) jsonMap.get("valid");
            if (validFlag.equals("true")) {
                signUpValidFlag = true;
                user_id = (String) jsonMap.get("user_id");
                session_id = (String) jsonMap.get("session_id");
                Intent mainActivityIntent = new Intent(
                        "co.yapster.yapster_android.MAINACTIVITY");
                mainActivityIntent.putExtra("user_id", user_id);
                mainActivityIntent.putExtra("session_id", session_id);
                startActivity(mainActivityIntent);
            } else {
                signUpValidFlag = false;
                String error_message = (String) jsonMap.get("message");
                // 1. Instantiate an AlertDialog.Builder with its constructor
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                // 2. Chain together various setter methods to set the dialog
                // characteristics
                builder.setMessage(error_message).setTitle(
                        R.string.sign_in_sign_up_screen_error_title);

                builder.setPositiveButton(
                        R.string.sign_in_sign_up_screen_popup_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked OK button
                            }
                        });

                // 3. Get the AlertDialog from create()
                AlertDialog dialog = builder.create();
                dialog.show();
            }

        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
                Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences,
        // but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(
                SplashSignInAndSignUpScreen.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private void sendRegistrationIdToBackend() {
        // Your implementation here.
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context
     *            application's context.
     * @param regId
     *            registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over
                    // HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your
                    // app.
                    // The request to your server should be authenticated if
                    // your app
                    // is using accounts.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the
                    // device
                    // will send upstream messages to a server that echo back
                    // the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }
        }.execute(null, null, null);
    }

    class SignUpAPICall extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... api_url) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(api_url[0]);
            // List<NameValuePair> nameValuePairs = null;
            String nameValuePairsJSONObjectString = null;
            try {
                JSONObject nameValuePairsJSONObject = new JSONObject();
                nameValuePairsJSONObject.put("first_name",
                        first_name);
                nameValuePairsJSONObject.put("last_name",
                        last_name);
                nameValuePairsJSONObject
                        .put("password", password);
                nameValuePairsJSONObject.put("username", username);
                nameValuePairsJSONObject.put("email", email);
                nameValuePairsJSONObject.put("device_type", "android");
                nameValuePairsJSONObject.put("identifier", regid);

                nameValuePairsJSONObjectString = nameValuePairsJSONObject
                        .toString();
                System.out.println(nameValuePairsJSONObjectString);
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
            try {
                StringEntity nameValuePairsStringEntity = new StringEntity(
                        nameValuePairsJSONObjectString);
                nameValuePairsStringEntity.setContentType("application/json");
                httppost.setEntity(nameValuePairsStringEntity);
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            HttpResponse response = null;
            try {
                response = httpclient.execute(httppost);
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            InputStream responseInputStream = null;
            try {
                responseInputStream = response.getEntity().getContent();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            StringWriter writer = new StringWriter();
            try {
                IOUtils.copy(responseInputStream, writer, "UTF-8");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            signUpResponseJSONString = writer.toString();
            System.out.println(signUpResponseJSONString);

            return signUpResponseJSONString;
        }
    }
}




