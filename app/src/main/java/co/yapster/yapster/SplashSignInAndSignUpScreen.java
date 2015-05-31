package co.yapster.yapster;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.json.parsers.JSONParser;
import com.json.parsers.JsonParserFactory;

import android.animation.TimeInterpolator;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SplashSignInAndSignUpScreen extends Activity implements
		View.OnClickListener {
	LinearLayout linearLayoutForTheRestOfTheScreen;
	Button buttonSignIn;
	Button buttonSignUp;
	Button buttonForgotPassword;
	EditText editTextInputtedEmailOrUsername;
	EditText editTextInputtedPassword;
	TextView textViewYapsterTitle;
    ProgressBar progressBarForSignIn;
	String apiSignInResponseJSONString;
    String apiUserInfoResponseJSONString;
	String inputtedEmailOrUsernameString;
	String inputtedEmailOrUsernameTypeString;
	String inputtedPasswordString;
	String deviceTokenString = "<>";
	Boolean signInValidFlag = false;
	Integer user_id;
	Integer session_id;
    public String device_type;
    public String identifier;
	Activity activity;
    static final String PREFS_KEY = "com.yapster.yapster";
    static final String USER_ID_KEY = "user_id";
    static final String SESSION_ID_KEY = "session_id";
    static final String DEVICE_TYPE_KEY = "device_type";
    static final String IDENTIFIER_KEY = "identifier";
	private static final TimeInterpolator sDecelerator = new DecelerateInterpolator();
	private static final TimeInterpolator sAccelerator = new AccelerateInterpolator();
	private static final int ANIM_DURATION = 500;
	ActionBar actionBar;
	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	static final String TAG = "Splash";
	GoogleCloudMessaging gcm;
	AtomicInteger msgId = new AtomicInteger();
	SharedPreferences prefs;
	Context context;
	String regid;
    Boolean signedInFlag = false;
    User user;
	/**
	 * Substitute you own sender ID here. This is the project number you got
	 * from the API Console, as described in "Getting Started."
	 */
	String SENDER_ID = "1004114511602";
    String APPLICATION_ID = "yapster-app";
    SharedPreferences preferences;


    @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
		getWindow().setAllowEnterTransitionOverlap(true);
		getWindow().setAllowReturnTransitionOverlap(true);
		getWindow().setSharedElementExitTransition(new Explode());
		getWindow().setSharedElementEnterTransition(new Explode());
		setContentView(R.layout.activity_splash_sign_in_and_sign_up_screen);
		actionBar = getActionBar();
		// Hide the action bar title
		actionBar.setDisplayShowTitleEnabled(false);
		setUpAllElements();
		if (savedInstanceState == null) {
			Thread timer = new Thread() {
				public void run() {
					try {
						sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						currentUserSignIn();
						if (signInValidFlag == true) {
                            try {
                                getUser();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (user != null){
                                Integer sInt = session_id;
                                user.setSessionID(sInt);
                                Intent openMainActivity = new Intent(
                                        "co.yapster.yapster.MAINACTIVITY");
                                openMainActivity.putExtra("user",user);
                                startActivity(openMainActivity);
//                                finish();
                            }else{

                            }

						} else {
							activity.runOnUiThread(new Runnable() {
								public void run() {
									becomeSignInScreen();
								}
							});
						}
					}
				}
			};
			timer.start();
		}
	}

    @Override
    protected void onStart() {
        super.onStart();
        AWS.getInstance(activity);
    }

    @Override
	public void onClick(View view) {
		// TODO Auto-generated method stub

		switch (view.getId()) {
		case R.id.buttonSignIn:
			if (editTextInputtedEmailOrUsername.getText().toString() == null) {

			} else if (editTextInputtedPassword.getText().toString() == null) {

			} else {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editTextInputtedEmailOrUsername.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(editTextInputtedPassword.getWindowToken(), 0);
                linearLayoutForTheRestOfTheScreen.setVisibility(View.INVISIBLE);
                progressBarForSignIn.setVisibility(View.VISIBLE);
                try {
					apiSignInCall();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                currentUserSignIn();
                if (signInValidFlag == true) {
                    try {
                        getUser();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (user != null){
                        Integer sInt = session_id;
                        user.setSessionID(sInt);
                        Intent openMainActivity = new Intent(
                                "co.yapster.yapster.MAINACTIVITY");
                        openMainActivity.putExtra("user",user);
                        startActivity(openMainActivity);
                        finish();
                    }else{

                    }

                } else {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            becomeSignInScreen();
                        }
                    });
                }
				break;
			}
		case R.id.buttonSignUp:
			Intent signUpPage1ActivityIntent = new Intent(
					"co.yapster.yapster.SIGNUPPAGE1SCREEN");
			startActivity(signUpPage1ActivityIntent);
			break;

		default:
			break;
		}
	}

	public void setUpAllElements() {
		activity = this;
		context = getApplicationContext();
		linearLayoutForTheRestOfTheScreen = (LinearLayout) findViewById(R.id.linearLayoutForTheRestOfTheScreen);
		textViewYapsterTitle = (TextView) findViewById(R.id.textViewYapsterTitle);
		buttonSignIn = (Button) findViewById(R.id.buttonSignIn);
		buttonSignUp = (Button) findViewById(R.id.buttonSignUp);
		buttonForgotPassword = (Button) findViewById(R.id.buttonForgotPassword);
		editTextInputtedEmailOrUsername = (EditText) findViewById(R.id.editTextInputtedEmailOrUsername);
		editTextInputtedPassword = (EditText) findViewById(R.id.editTextInputtedPassword);
        progressBarForSignIn = (ProgressBar) findViewById(R.id.progressBarForSignIn);
		buttonSignIn.setOnClickListener(this);
		buttonSignUp.setOnClickListener(this);
		buttonForgotPassword.setOnClickListener(this);

		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) textViewYapsterTitle
				.getLayoutParams();
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT,
				RelativeLayout.TRUE);
		textViewYapsterTitle.setLayoutParams(layoutParams);
		linearLayoutForTheRestOfTheScreen.setAlpha(0);
	}

    public void currentUserSignIn(){
        preferences = this.getSharedPreferences(
                PREFS_KEY, Context.MODE_PRIVATE);

        user_id = preferences.getInt(USER_ID_KEY,0);
        session_id = preferences.getInt(SESSION_ID_KEY,0);
        device_type = preferences.getString(DEVICE_TYPE_KEY, "android");
        identifier = preferences.getString(IDENTIFIER_KEY, "");

        if (user_id == 0){
            signedInFlag = false;
        }else{
            signedInFlag = true;
            try {
                apiAutomaticSignInCall();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

    }

	public void becomeSignInScreen() {

		textViewYapsterTitle.setPivotX(0);
		textViewYapsterTitle.setPivotY(0);
		textViewYapsterTitle.setScaleX(1);
		textViewYapsterTitle.setScaleY(1);

		// Animate scale and translation to go from thumbnail to full size
		textViewYapsterTitle.animate().setDuration(ANIM_DURATION * 4).scaleX(1)
				.scaleY(1).translationX(0).translationY(-900f)
				.setInterpolator(sDecelerator).withEndAction(new Runnable() {
					public void run() {
						// Animate the description in after the image animation
						// is done. Slide and fade the text in from underneath
						// the picture.
						linearLayoutForTheRestOfTheScreen
								.setTranslationY(linearLayoutForTheRestOfTheScreen
										.getHeight());
						linearLayoutForTheRestOfTheScreen.animate()
								.setDuration(ANIM_DURATION * 2).translationY(0)
								.alpha(1).setInterpolator(sDecelerator);

					}
				});

	}

    public Void getUser() throws ExecutionException, InterruptedException {
        AsyncTask<Void, Void, String> api_user_info = new APIUserInfo()
                .execute();
        apiUserInfoResponseJSONString = api_user_info.get().toString();
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(apiUserInfoResponseJSONString);
        Boolean validFlag = jsonElement.getAsJsonObject().get("valid").getAsBoolean();
        if (validFlag == true) {
            JsonObject data = jsonElement.getAsJsonObject().get("data").getAsJsonObject();
            user = new User(true, data);
        } else {

        }
      return null;
    }

	private boolean checkPlayServices() {
//		int resultCode = GooglePlayServicesUtil
//				.isGooglePlayServicesAvailable(this);
//		if (resultCode != ConnectionResult.SUCCESS) {
//			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
//				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
//						PLAY_SERVICES_RESOLUTION_REQUEST).show();
//			} else {
//				Log.i(TAG, "This device is not supported.");
//				finish();
//			}
//			return false;
//		}
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
		} catch (NameNotFoundException e) {
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

	public void apiSignInCall() throws InterruptedException, ExecutionException {
		inputtedEmailOrUsernameString = editTextInputtedEmailOrUsername
				.getText().toString();
		if ((inputtedEmailOrUsernameString.contains("@") == true)
				|| (inputtedEmailOrUsernameString.contains(".com") == true)
				|| (inputtedEmailOrUsernameString.contains(".net")) == true) {
			inputtedEmailOrUsernameTypeString = "email";
		} else {
			inputtedEmailOrUsernameTypeString = "username";
		}
		inputtedPasswordString = editTextInputtedPassword.getText().toString();

		if (checkPlayServices()) {
			gcm = GoogleCloudMessaging.getInstance(this);
			regid = getRegistrationId(context);

			if (regid.isEmpty()) {
				registerInBackground();

			} else {
				Log.i(TAG, "No valid Google Play Services APK found.");
			}

			AsyncTask<Void, Integer, String> apiJSONResponseString = new SignInAPICall()
					.execute();
			JsonParserFactory factory = JsonParserFactory.getInstance();
			JSONParser parser = factory.newJsonParser();
            apiSignInResponseJSONString = apiJSONResponseString.get().toString();
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(apiSignInResponseJSONString);
            Boolean valid = jsonElement.getAsJsonObject().get("valid").getAsBoolean();
			if (valid == true) {
				signInValidFlag = true;
				user_id = jsonElement.getAsJsonObject().get("user_id").getAsInt();
				session_id = jsonElement.getAsJsonObject().get("session_id").getAsInt();
                preferences = this.getSharedPreferences(
                        PREFS_KEY, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(USER_ID_KEY,user_id);
                editor.putInt(SESSION_ID_KEY,session_id);
                editor.putString(DEVICE_TYPE_KEY,device_type);
                editor.putString(IDENTIFIER_KEY,regid);
                editor.apply();
                signInValidFlag = true;
			} else {
				signInValidFlag = false;
				String error_message = jsonElement.getAsJsonObject().get("message").getAsString();
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

    public void apiAutomaticSignInCall() throws InterruptedException, ExecutionException {

        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);

            if (regid.isEmpty()) {
                registerInBackground();

            } else {
                Log.i(TAG, "No valid Google Play Services APK found.");
            }
            String api_url = "http://api.yapster.co/users/sign_in/";
            AsyncTask<Void, Integer, String> api_automatic_sign_in_async_task = new AutomaticSignInAPICall()
                    .execute();
            JsonParserFactory factory = JsonParserFactory.getInstance();
            JSONParser parser = factory.newJsonParser();
            apiSignInResponseJSONString = api_automatic_sign_in_async_task.get().toString();
            Map jsonMap = parser.parseJson(apiSignInResponseJSONString);
            String validFlag = (String) jsonMap.get("valid");
            if (validFlag.equals("true")) {
                signInValidFlag = true;
            } else {
                signedInFlag = false;
                signInValidFlag = false;
//                String error_message = (String) jsonMap.get("message");
//                // 1. Instantiate an AlertDialog.Builder with its constructor
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//                // 2. Chain together various setter methods to set the dialog
//                // characteristics
//                builder.setMessage(error_message).setTitle(
//                        R.string.sign_in_sign_up_screen_error_title);
//
//                builder.setPositiveButton(
//                        R.string.sign_in_sign_up_screen_popup_ok,
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                // User clicked OK button
//                            }
//                        });
//
//                // 3. Get the AlertDialog from create()
//                AlertDialog dialog = builder.create();
//                dialog.show();
            }
        }

    }


	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and app versionCode in the application's
	 * shared preferences.
	 */

	class SignInAPICall extends AsyncTask<Void, Integer, String> {

        @Override
        protected void onPreExecute() {

        }

		@Override
		protected String doInBackground(Void... params) {
            String api_url = "http://api.yapster.co/users/sign_in/";
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(api_url);
			// List<NameValuePair> nameValuePairs = null;
			String nameValuePairsJSONObjectString = null;
			try {
				JSONObject nameValuePairsJSONObject = new JSONObject();
				nameValuePairsJSONObject.put("option",
						inputtedEmailOrUsernameString);
				nameValuePairsJSONObject.put("option_type",
						inputtedEmailOrUsernameTypeString);
				nameValuePairsJSONObject
						.put("password", inputtedPasswordString);
				nameValuePairsJSONObject.put("device_type", "android");
				nameValuePairsJSONObject.put("identifier", regid);

				nameValuePairsJSONObjectString = nameValuePairsJSONObject
						.toString();
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
            apiSignInResponseJSONString = writer.toString();

			return apiSignInResponseJSONString;
		}
	}

    class AutomaticSignInAPICall extends AsyncTask<Void, Integer, String> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(Void... params) {
            String api_url = "http://api.yapster.co/users/automatic_sign_in/";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(api_url);
            // List<NameValuePair> nameValuePairs = null;
            String nameValuePairsJSONObjectString = null;
            try {
                JSONObject nameValuePairsJSONObject = new JSONObject();
                nameValuePairsJSONObject.put("user_id",
                        user_id);
                nameValuePairsJSONObject.put("session_id",
                        session_id);
                nameValuePairsJSONObject
                        .put("device_type", device_type);
                nameValuePairsJSONObject.put("identifier", identifier);

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
            apiSignInResponseJSONString = writer.toString();
            System.out.println(apiSignInResponseJSONString);

            return apiSignInResponseJSONString;
        }
    }

    class APIUserInfo extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(Void... params) {
            String api_url = "http://api.yapster.co/users/load/profile/info/";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(api_url);
            // List<NameValuePair> nameValuePairs = null;
            String nameValuePairsJSONObjectString = null;
            try {
                JSONObject nameValuePairsJSONObject = new JSONObject();
                nameValuePairsJSONObject.put("user_id",
                        user_id);
                nameValuePairsJSONObject.put("session_id",
                        session_id);
                nameValuePairsJSONObject.put("profile_user_id",
                        user_id);
                nameValuePairsJSONObject
                        .put("device_type", device_type);
                nameValuePairsJSONObject.put("identifier", identifier);

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
            apiUserInfoResponseJSONString = writer.toString();
            System.out.println(apiUserInfoResponseJSONString);

            return apiUserInfoResponseJSONString;
        }
    }
}