package co.yapster.yapster;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class SignUpPage1Screen extends Activity {

    LinearLayout linearLayoutForView;
    LinearLayout linearLayoutForInputtedInfo;
	EditText editTextInputtedEmailAddress;
	EditText editTextInputtedFirstName;
	EditText editTextInputtedLastName;
	EditText editTextInputtedUsername;
	RadioGroup radioGroupWithGenderChoices;
	RadioButton radioOptionMale;
	RadioButton radioOptionFemale;
	RadioButton radioOptionOther;
	CheckBox checkBoxTermsOfServicePrivacyPolicyAge13;
	Button buttonContinueToSignUpPage2;
	LoginButton loginButtonFacebook;
	String genderSelected;
	Boolean readTermsOfServicePrivacyPolicyAge13Confirmation;
    String apiUserEmailValidationJSONString;
    String apiUserUsernameValidationJSONString;
    Boolean validEmail;
    Boolean validUsername;
    CallbackManager callbackManager;
    AccessToken facebookAccessToken;
    Activity activity;
    Boolean facebookAutofill = false;
    FBUser fbUser;
    SharedPreferences preferences;
    Integer user_id;
    Integer session_id;
    String identifier;
    String message;
    String apiUserInfoResponseJSONString;
    User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
		setContentView(R.layout.activity_sign_up_page_1_screen_);
        activity = this;
		setUpAllElements();
	}

	private void setUpAllElements() {
		// TODO Auto-generated method stub
		editTextInputtedEmailAddress = (EditText) findViewById(R.id.editTextInputtedEmailAddress);
		editTextInputtedFirstName = (EditText) findViewById(R.id.editTextInputtedFirstName);
		editTextInputtedLastName = (EditText) findViewById(R.id.editTextInputtedLastName);
        editTextInputtedUsername = (EditText) findViewById(R.id.editTextInputtedUsername);
		radioGroupWithGenderChoices = (RadioGroup) findViewById(R.id.radioGroupWithGenderChoices);
		radioOptionMale = (RadioButton) findViewById(R.id.radioOptionMale);
		radioOptionFemale = (RadioButton) findViewById(R.id.radioOptionFemale);
		radioOptionOther = (RadioButton) findViewById(R.id.radioOptionOther);
		checkBoxTermsOfServicePrivacyPolicyAge13 = (CheckBox) findViewById(R.id.checkBoxTermsOfServicePrivacyPolicyAge13);
		buttonContinueToSignUpPage2 = (Button) findViewById(R.id.buttonContinueToSignUpPage2);
        loginButtonFacebook = (LoginButton) findViewById(R.id.loginButtonFacebook);
        callbackManager = CallbackManager.Factory.create();

        loginButtonFacebook.setReadPermissions("user_friends","user_birthday","email","public_profile","user_location","user_likes");
//        loginButtonFacebook.setPublishPermissions("publish_actions","publish_pages");

        // Callback registration
        loginButtonFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        facebookAccessToken = loginResult.getAccessToken();
                        fbUser = new FBUser();

                        GraphRequestAsyncTask request = GraphRequest.newMeRequest(facebookAccessToken, new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject user, GraphResponse graphResponse) {
                                fbUser.setId(Integer.parseInt(user.optString("id")));
                                fbUser.setEmail(user.optString("email"));
                                fbUser.setName(user.optString("name"));
                                fbUser.setDateOfBirth(user.optString("birthday"));
                                fbUser.setGender(user.optString("gender"));
                                autocompleteScreen();
                            }


                            public void autocompleteScreen(){
                                editTextInputtedEmailAddress.requestFocus();
                                editTextInputtedEmailAddress.setText(fbUser.getEmail());
                                editTextInputtedFirstName.requestFocus();
                                editTextInputtedFirstName.setText(fbUser.getFirstName());
                                editTextInputtedLastName.requestFocus();
                                editTextInputtedLastName.setText(fbUser.getLastName());
                                editTextInputtedUsername.requestFocus();
                                editTextInputtedUsername.setText((fbUser.getFirstName() + fbUser.getLastName()).toLowerCase());
                                editTextInputtedUsername.clearFocus();
                                if (fbUser.getGender().equals("male")){
                                    radioOptionMale.setChecked(true);
                                }else if (fbUser.getGender().equals("female")){
                                    radioOptionFemale.setChecked(true);
                                }else{

                                }

                                SimpleDateFormat facebookBirthDateFormatter = new SimpleDateFormat("mm/dd/yyyy");
                                try {
                                    Date birthDate = facebookBirthDateFormatter.parse(fbUser.getDateOfBirth());
                                    Date todaysDate = facebookBirthDateFormatter.parse(facebookBirthDateFormatter.format(new Date()));
                                    Integer difference = getDiffYears(birthDate,todaysDate);
                                    if (difference > 13){
                                        checkBoxTermsOfServicePrivacyPolicyAge13.setChecked(true);
                                    }
                                    SimpleDateFormat dateOfBirthFormatter = new SimpleDateFormat("yyyy-mm-dd");
                                    String formattedDateOfBirth = dateOfBirthFormatter.format(birthDate);
                                    fbUser.setDateOfBirth(formattedDateOfBirth);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }


                                buttonContinueToSignUpPage2.setText("Complete");
                                facebookAutofill = true;

                            }

                            public  int getDiffYears(Date first, Date last) {
                                Calendar a = getCalendar(first);
                                Calendar b = getCalendar(last);
                                int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
                                if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) ||
                                        (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE))) {
                                    diff--;
                                }
                                return diff;
                            }

                            public Calendar getCalendar(Date date) {
                                Calendar cal = Calendar.getInstance(Locale.US);
                                cal.setTime(date);
                                return cal;
                            }
                        }).executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Log.v("SignUpPage1", "cancel");
                    }

                    @Override
                    public void onError(FacebookException e) {
                        Log.v("SignUpPage1", e.getCause().toString());
                        Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG);


                    }
                });

            radioGroupWithGenderChoices
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        // TODO Auto-generated method stub

                        switch (checkedId) {
                            case -1:
                                System.out
                                        .println("checkedId : "
                                                + checkedId
                                                + "name : radio button pressed has been cleared.");
                                genderSelected = null;
                                break;
                            case R.id.radioOptionMale:
                                System.out.println("checkedId : " + checkedId
                                        + " name : male");
                                genderSelected = "M";
                                break;
                            case R.id.radioOptionFemale:
                                System.out.println("checkedId : " + checkedId
                                        + " name : female");
                                genderSelected = "F";
                                break;
                            case R.id.radioOptionOther:
                                System.out.println("checkedId : " + checkedId
                                        + " name : other");
                                genderSelected = "O";
                                break;

                        }

                    }
                });

        editTextInputtedUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                final Editable sf = s;
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {

                                try {
                                    validateUsername(sf.toString());
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        500
                );

            }
        });

        editTextInputtedUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            /* When focus is lost check that the text field
            * has valid values.
            */
                if (!hasFocus) {
                    EditText eT = (EditText) v;
                    try {
                        validateUsername(eT.getText().toString());
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        editTextInputtedEmailAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                final Editable sf = s;
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {

                                try {
                                    validateEmail(sf.toString());
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        500
                );

            }
        });


        editTextInputtedEmailAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            /* When focus is lost check that the text field
            * has valid values.
            */
                if (!hasFocus) {
                    EditText eT = (EditText) v;
                    try {
                        validateEmail(eT.getText().toString());
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });



		checkBoxTermsOfServicePrivacyPolicyAge13
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked == true) {
							readTermsOfServicePrivacyPolicyAge13Confirmation = true;
						} else {
							readTermsOfServicePrivacyPolicyAge13Confirmation = false;
						}

					}

				});

		buttonContinueToSignUpPage2
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Integer checkedId = radioGroupWithGenderChoices
								.getCheckedRadioButtonId();
						if (checkedId == -1) {
							AlertDialog.Builder builder = new AlertDialog.Builder(
									SignUpPage1Screen.this);

							// 2. Chain together various setter methods to set
							// the dialog
							// characteristics
							builder.setMessage(
									"Please select a gender before continuing.")
									.setTitle("Error");

							builder.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											// User clicked OK button
										}
									});

							// 3. Get the AlertDialog from create()
							AlertDialog dialog = builder.create();
							dialog.show();
						} else {
							if (checkedId == radioOptionFemale.getId()) {
								checkGenderValue("F");
							} else if (checkedId == radioOptionMale.getId()) {
								checkGenderValue("M");
							} else if (checkedId == radioOptionOther.getId()) {
								checkGenderValue("O");
							}

							String userEmailAddressInputted = editTextInputtedEmailAddress
									.getText().toString();
							String userFirstNameInputted = editTextInputtedFirstName
									.getText().toString();
							String userLastNameInputted = editTextInputtedLastName
									.getText().toString();
                            String userUsernameInputted = editTextInputtedUsername.getText().toString();

                            //Checking if email is empty or not accurate.

                            if (userEmailAddressInputted.isEmpty() == true || validEmail == false){
                                AlertDialog.Builder builder = new AlertDialog.Builder(
                                        SignUpPage1Screen.this);

                                // 2. Chain together various setter methods to set
                                // the dialog
                                // characteristics
                                builder.setMessage(
                                        "Please enter a valid email before continuing.")
                                        .setTitle("Error");

                                builder.setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog, int id) {
                                                // User clicked OK button
                                            }
                                        });

                                // 3. Get the AlertDialog from create()
                            } else {

                                if (userFirstNameInputted.isEmpty() == true){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(
                                            SignUpPage1Screen.this);

                                    // 2. Chain together various setter methods to set
                                    // the dialog
                                    // characteristics
                                    builder.setMessage(
                                            "Please enter a valid first name before continuing.")
                                            .setTitle("Error");

                                    builder.setPositiveButton("OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface dialog, int id) {
                                                    // User clicked OK button
                                                }
                                            });

                                    // 3. Get the AlertDialog from create()
                                } else {

                                    if (userLastNameInputted.isEmpty() == true){
                                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                                SignUpPage1Screen.this);

                                        // 2. Chain together various setter methods to set
                                        // the dialog
                                        // characteristics
                                        builder.setMessage(
                                                "Please enter a valid last name before continuing.")
                                                .setTitle("Error");

                                        builder.setPositiveButton("OK",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(
                                                            DialogInterface dialog, int id) {
                                                        // User clicked OK button
                                                    }
                                                });

                                        // 3. Get the AlertDialog from create()
                                    } else {

                                        if (userUsernameInputted.isEmpty() == true || validUsername == false){
                                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                                    SignUpPage1Screen.this);

                                            // 2. Chain together various setter methods to set
                                            // the dialog
                                            // characteristics
                                            builder.setMessage(
                                                    "Please enter a valid username before continuing.")
                                                    .setTitle("Error");

                                            builder.setPositiveButton("OK",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(
                                                                DialogInterface dialog, int id) {
                                                            // User clicked OK button
                                                        }
                                                    });

                                            // 3. Get the AlertDialog from create()
                                        } else {

                                            if(facebookAutofill == true){

                                                String editTextEmail = editTextInputtedEmailAddress.getText().toString();
                                                String editTextFirstName = editTextInputtedFirstName.getText().toString();
                                                String editTextLastName = editTextInputtedLastName.getText().toString();
                                                String editTextUsername = editTextInputtedUsername.getText().toString();
                                                String dateOfBirth;
                                                if (fbUser != null){
                                                    dateOfBirth = fbUser.getDateOfBirth();
                                                }else{
                                                    Calendar c = Calendar.getInstance();
                                                    c.add(Calendar.YEAR,-13);
                                                    SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd");
                                                    dateOfBirth = df.format(c.getTime());
                                                }
                                                Integer facebookAccountID;
                                                if (fbUser != null){
                                                    facebookAccountID = fbUser.getId();
                                                }else{
                                                    facebookAccountID = null;
                                                }
                                                preferences = activity.getSharedPreferences(
                                                        SplashSignInAndSignUpScreen.PREFS_KEY, Context.MODE_PRIVATE);
                                                identifier = preferences.getString(SplashSignInAndSignUpScreen.PREFS_KEY,"<>");
                                                String apiSignUpResponse = null;
                                                String password = fbUser.getLastName() + fbUser.getDateOfBirth().substring(5,7) + fbUser.getDateOfBirth().substring(8,10);
                                                try {

                                                    apiSignUpResponse = new APISignUpAsyncTask(activity, editTextEmail, editTextFirstName, editTextLastName, editTextUsername, genderSelected, dateOfBirth, facebookAccountID, facebookAccessToken.getToken(), identifier, password).execute().get();
                                                } catch (InterruptedException e) {
                                                    // TODO Auto-generated catch block
                                                    e.printStackTrace();
                                                } catch (ExecutionException e) {
                                                    // TODO Auto-generated catch block
                                                    e.printStackTrace();
                                                }
                                                JsonParser jsonParser = new JsonParser();
                                                JsonElement jsonElement = jsonParser.parse(apiSignUpResponse);
                                                Boolean valid = jsonElement.getAsJsonObject().get("valid").getAsBoolean();
                                                if (valid == true) {
                                                    JsonObject data = jsonElement.getAsJsonObject().get("data").getAsJsonObject();
                                                    user_id = data.get("user_id").getAsInt();
                                                    session_id = data.get("session_id").getAsInt();
                                                    String device_type = "android";
                                                    SharedPreferences.Editor editor = preferences.edit();
                                                    editor.putString(SplashSignInAndSignUpScreen.USER_ID_KEY,user_id.toString());
                                                    editor.putString(SplashSignInAndSignUpScreen.SESSION_ID_KEY,session_id.toString());
                                                    editor.putString(SplashSignInAndSignUpScreen.DEVICE_TYPE_KEY,device_type);
                                                    editor.putString(SplashSignInAndSignUpScreen.IDENTIFIER_KEY,identifier);
                                                    editor.apply();
                                                    try {
                                                        getUser();
                                                    } catch (ExecutionException e) {
                                                        e.printStackTrace();
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                    }
                                                    if (user != null) {
                                                        Integer sInt = session_id;
                                                        user.setSessionID(sInt);
                                                        Intent openMainActivity = new Intent(
                                                                "co.yapster.yapster.MAINACTIVITY");
                                                        openMainActivity.putExtra("user", user);
                                                        startActivity(openMainActivity);
                                                        finish();
                                                    }else{

                                                    }
                                                }else{
                                                    message = jsonElement.getAsJsonObject().get("message").getAsString();
                                                }


                                            }else{

                                                Intent openSignUpPage2Screen = new Intent(
                                                        "com.yapster.yapster_android.SIGNUPPAGE2SCREEN");

                                                openSignUpPage2Screen.putExtra("email", userEmailAddressInputted);
                                                openSignUpPage2Screen.putExtra("first_name", userFirstNameInputted);
                                                openSignUpPage2Screen.putExtra("last_name", userLastNameInputted);
                                                openSignUpPage2Screen.putExtra("username", userUsernameInputted);
                                                openSignUpPage2Screen.putExtra("gender", genderSelected);
                                                startActivity(openSignUpPage2Screen);

                                            }

                                        }

                                    }

                                }
                            }
						}

					}

				});

	}

    public Void getUser() throws ExecutionException, InterruptedException {
        AsyncTask<Void, Void, String> api_user_info = new APIUserInfoAsyncTask(user_id, user_id, session_id, identifier)
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void validateEmail(String userEmailInputted) throws ExecutionException, InterruptedException {
        if (userEmailInputted.isEmpty() == false && userEmailInputted.length() > 1){

            validEmail = new APIValidateEmail().execute(userEmailInputted).get();
            if (validEmail == true){

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        editTextInputtedEmailAddress.setTextColor(getResources().getColor(R.color.yapster_green));


                    }
                });

            }else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        editTextInputtedEmailAddress.setTextColor(Color.RED);

                    }
                });
            }

        }

    }

    private void validateUsername(String userUsernameInputted) throws ExecutionException, InterruptedException {
        if(userUsernameInputted.isEmpty() == false && userUsernameInputted.length() > 1){

            validUsername = new APIValidateUsername().execute(userUsernameInputted).get();
            if (validUsername == true){

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        editTextInputtedUsername.setTextColor(getResources().getColor(R.color.yapster_green));

                    }
                });

            }else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        editTextInputtedUsername.setTextColor(Color.RED);

                    }
                });
            }

        }

    }


	private void checkGenderValue(String whatGenderShouldBe) {
		if (genderSelected.equals(whatGenderShouldBe) == true) {

		} else {
			genderSelected = whatGenderShouldBe;
		}
	}

    class APIValidateEmail extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(String... params) {
            String email = params[0];
            String api_url = "http://api.yapster.co/users/sign_up/verify_email/";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(api_url);
            // List<NameValuePair> nameValuePairs = null;
            String nameValuePairsJSONObjectString = null;
            try {
                JSONObject nameValuePairsJSONObject = new JSONObject();
                nameValuePairsJSONObject.put("email",
                        email);
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
            apiUserEmailValidationJSONString = writer.toString();
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(apiUserEmailValidationJSONString);
            Boolean valid = jsonElement.getAsJsonObject().get("valid").getAsBoolean();
            if (valid == true) {
                validEmail = true;
            }else{
                validEmail = false;
            }

            return validEmail;
        }
    }

    class APIValidateUsername extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(String... params) {
            String username = params[0];
            String api_url = "http://api.yapster.co/users/sign_up/verify_username/";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(api_url);
            // List<NameValuePair> nameValuePairs = null;
            String nameValuePairsJSONObjectString = null;
            try {
                JSONObject nameValuePairsJSONObject = new JSONObject();
                nameValuePairsJSONObject.put("username",
                        username);
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
            apiUserUsernameValidationJSONString = writer.toString();
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(apiUserUsernameValidationJSONString);
            Boolean valid = jsonElement.getAsJsonObject().get("valid").getAsBoolean();
            if (valid == true) {
                validUsername = true;
            }else{
                 validUsername = false;
            }

            return validUsername;
        }
    }

}
