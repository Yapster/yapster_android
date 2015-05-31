package co.yapster.yapster;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonElement;
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
import java.util.Date;

/**
 * Created by gurkarangulati on 5/9/15.
 */
public class APISignUpAsyncTask extends AsyncTask<Void,Void,String> {

    Activity activity;
    String email;
    String firstName;
    String lastName;
    String username;
    String gender;
    String dateOfBirth;
    Integer facebookAccountID;
    String facebookAccessToken;
    String identifier;
    String apiJsonString;
    Boolean validSignUp;
    Integer userID;
    Integer sessionID;
    String message;
    String password;


    APISignUpAsyncTask(Activity activity, String email, String firstName, String lastName, String username, String gender, String dateOfBirth, Integer facebookAccountID, String facebookAccessToken, String identifier, String password){

        this.activity = activity;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.facebookAccountID = facebookAccountID;
        this.facebookAccessToken = facebookAccessToken;
        this.identifier = identifier;
        this.password = password;

    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(Void... params) {
        String api_url = "http://api.yapster.co/users/sign_up/";
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(api_url);
        String nameValuePairsJSONObjectString = null;
        try {
            JSONObject nameValuePairsJSONObject = new JSONObject();
            nameValuePairsJSONObject.put("email",
                    email);
            nameValuePairsJSONObject.put("first_name",
                    firstName);
            nameValuePairsJSONObject.put("last_name",
                    lastName);
            nameValuePairsJSONObject.put("username",
                    username);
            nameValuePairsJSONObject.put("email",
                    email);
            nameValuePairsJSONObject.put("gender",
                    gender);
            if (dateOfBirth != null){
                nameValuePairsJSONObject.put("date_of_birth",
                        dateOfBirth);
            }
            if (facebookAccountID != null){
                nameValuePairsJSONObject.put("facebook_connection_flag", true);
                nameValuePairsJSONObject.put("facebook_account_id", facebookAccountID);
                nameValuePairsJSONObject.put("facebook_access_token", facebookAccessToken);
            }
            nameValuePairsJSONObject.put("device_type","android");
            nameValuePairsJSONObject.put("identifier",identifier);
            nameValuePairsJSONObject.put("password", password);

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
        apiJsonString = writer.toString();
        System.out.println(apiJsonString);
//        JsonParser jsonParser = new JsonParser();
//        JsonElement jsonElement = jsonParser.parse(apiJsonString);
//        Boolean valid = jsonElement.getAsJsonObject().get("valid").getAsBoolean();
//        if (valid == true) {
//            validSignUp = true;
//            userID = jsonElement.getAsJsonObject().get("user_id").getAsInt();
//            sessionID = jsonElement.getAsJsonObject().get("session_id").getAsInt();
//            return
//        }else{
//            validSignUp = false;
//
//        }
        return apiJsonString;
    }
}
