package co.yapster.yapster;

import android.os.AsyncTask;
import android.util.Log;

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

/**
 * Created by gurkarangulati on 5/12/15.
 */
public class APIUserInfoAsyncTask extends AsyncTask<Void, Void, String> {

    Integer userID;
    Integer profileUserID;
    Integer sessionID;
    String identifier;
    String apiUserInfoResponseJSONString;

    APIUserInfoAsyncTask (Integer userID, Integer profileUserID, Integer sessionID, String identifier){

        this.userID = userID;
        this.profileUserID = profileUserID;
        this.sessionID = sessionID;
        this.identifier = identifier;


    }

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
                    userID);
            nameValuePairsJSONObject.put("session_id",
                    sessionID);
            nameValuePairsJSONObject.put("profile_user_id",
                    profileUserID);
            nameValuePairsJSONObject
                    .put("device_type", "android");
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

        return apiUserInfoResponseJSONString;
    }
}