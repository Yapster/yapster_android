package co.yapster.yapster;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

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
import java.util.ArrayList;

/**
 * Created by gurkarangulati on 3/3/15.
 */
public class Player extends MediaPlayer {

    private static Player _player = null;
    private Activity activity;
    private Integer positionOfCurrentYap = null;
    private Library mLibrary = null;
    private MediaPlayer.OnCompletionListener onYapCompletionListener = null;
    private static Integer NUMBER_OF_YAPS_LOADED_ON_EACH_PAGE = 3;
    private Boolean hasLoadedAll = false;
    private User mUser;
    private String apiPlayerLoadYapsJsonResponse;
    private AsyncTask<Void,Void,Void> apiPlayerLoadYapsAsyncTask;
    private AsyncTask<Void,Void,Void> playerPlayAsyncTask;
    private Boolean hasSetup = false;
    public Boolean isPlayerVisible = false;
    private NotificationPanel nPanel;

    public Player (User user){
        mUser = user;
        if (onYapCompletionListener == null){
            onYapCompletionListener = new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    Integer yapIndex = positionOfCurrentYap + 1;
                    playYap(yapIndex);

                };
            };
        }
    }



    public static Player getInstance(User user)
    {
        if (_player == null){
            _player = new Player(user);
            setupPlayerObject();
        }

        return _player;
    }

    public static Player getInstance(){
        return _player;
    }

    private static void setupPlayerObject(){
        _player.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    public void setupVisualPlayer(){
        String className = activity.getClass().toString();
        Yap y = getCurrentYap();
        if (className.equals("class co.yapster.yapster.MainActivity") == true)
        {
            MainActivity mainActivity = (MainActivity) activity;
            MainViewHolder mvh = mainActivity.mainViewHolder;
            if (y.picturePath != null) {
                mainActivity.imageLoader.displayImage(y.picturePathURL.toString(), mvh.imageViewPlayerYapImage, mainActivity.defaultOptions);
                mvh.imageViewPlayerYapImage.setVisibility(View.VISIBLE);
                mainActivity.imageLoader.displayImage(y.picturePathURL.toString(), mvh.imageViewFullPlayerYapImage, mainActivity.defaultOptions);
            }else{
                mvh.imageViewPlayerYapImage.setVisibility(View.GONE);
                mainActivity.imageLoader.displayImage(mLibrary.picturePathURL.toString(), mvh.imageViewFullPlayerYapImage, mainActivity.defaultOptions);
            }
            mvh.textViewPlayerYapTitle.setText(y.title);
            mvh.textViewPlayerYapUserName.setText(y.userUsername);
            mvh.textViewFullPlayerYapTitle.setText(y.title);
            mvh.textViewFullPlayerYapUser.setText(y.getYapUserFullName() + " - @" + y.userUsername);
            mvh.textViewFullPlayerYapDescription.setText(y.description);
            mvh.imageButtonFullPlayerPlayAndPause.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_player_pause_black));

            if (isPlayerVisible == false){
                mvh.slidingLayout.setPanelHeight((int)mainActivity.mScreenHeight * 10 / 35);
                mvh.slidingLayout.setSlidingEnabled(true);
                isPlayerVisible = true;
            }
        }else if (className.equals("class co.yapster.yapster.LibraryDetailsScreenActivity") == true)
        {
            LibraryDetailsScreenActivity libraryDetailsScreenActivity = (LibraryDetailsScreenActivity) activity;
            LibraryDetailsScreenViewHolder l = libraryDetailsScreenActivity.libraryDetailsScreenViewHolder;
            String yapTitle = y.title;
            String yapUserName = y.userUsername;
            if (y.picturePath != null) {
                libraryDetailsScreenActivity.imageLoader.displayImage(y.picturePathURL.toString(), l.imageViewPlayerYapImage, libraryDetailsScreenActivity.options);
                l.imageViewPlayerYapImage.setVisibility(View.VISIBLE);
            }else{
                l.imageViewPlayerYapImage.setVisibility(View.GONE);
            }
            l.textViewPlayerYapTitle.setText(yapTitle);
            l.textViewPlayerYapUserName.setText(yapUserName);
            if (isPlayerVisible == false){
                l.slidingLayout.setPanelHeight((int)libraryDetailsScreenActivity.mScreenHeight * 10 / 35);
                l.slidingLayout.setSlidingEnabled(true);
                isPlayerVisible = true;
            }
        }
        if (nPanel == null){
            nPanel = NotificationPanel.getInstance(activity, mUser,y);
        }else{
            nPanel.updateView(y);
        }
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Yap getCurrentYap() {
        if (positionOfCurrentYap == null){
            return null;
        }else {
            return mLibrary.yaps.get(positionOfCurrentYap);
        }
    }

    public void playYap(Integer yapIndex) {
        System.out.println("This is the index of the position of the Current yap : " + positionOfCurrentYap);
        System.out.println("This is the index of the new yap index : " + yapIndex);
        if (hasLoadedAll == false) {
            if (yapIndex < mLibrary.yaps.size() && yapIndex >= 0) {
                positionOfCurrentYap = yapIndex;
                playPlayer();
            } else {
                if (yapIndex >= 0) {
                    loadMore();
                    if (hasLoadedAll == false && positionOfCurrentYap < mLibrary.yaps.size() && yapIndex < mLibrary.yaps.size()) {
                            positionOfCurrentYap = yapIndex;
                            playPlayer();
                    }else{
                        positionOfCurrentYap = 0;
                        playYap(positionOfCurrentYap);
                    }
                } else {

                }
            }
        }else{
            positionOfCurrentYap = 0;
        }
    }

    public void playYap(Integer yapIndex, Library library){
        this.mLibrary = library;
        if (positionOfCurrentYap != null) {
            System.out.println("This is the index of the position of the Current yap : " + positionOfCurrentYap);
        }
        System.out.println("This is the index of the new yap index : " + yapIndex);
        positionOfCurrentYap = yapIndex;
        playPlayer();
    }

    public void playPlayer(){
        if (hasSetup == false){
            setupPlayerObject();
            hasSetup = true;
        }
        if (_player.isPlaying()){
            _player.stop();
            _player.reset();
        }
        setupVisualPlayer();
        Yap yap = mLibrary.yaps.get(positionOfCurrentYap);
        playerPlayAsyncTask = new PlayerPlayAsyncTask(yap);
        playerPlayAsyncTask.execute();
    }

    public void nextYap(){
        Integer nextYapPostion = positionOfCurrentYap + 1;
        playYap(nextYapPostion);
    }

    public void previousYap(){
        Integer nextYapPostion = positionOfCurrentYap - 1;
        playYap(nextYapPostion);
    }

    public void loadMore(){
        apiPlayerLoadYapsAsyncTask = new APIPlayerLoadYapsAsyncTask(mLibrary.page + 1,NUMBER_OF_YAPS_LOADED_ON_EACH_PAGE,false);
        apiPlayerLoadYapsAsyncTask
                .execute();

    }

    @Override
    public void stop() throws IllegalStateException {
        super.stop();
        nPanel.notificationCancel();

    }

    class APIPlayerLoadYapsAsyncTask extends
            AsyncTask<Void, Void, Void> {

        Integer page;
        Integer amount;
        Boolean reload;

        APIPlayerLoadYapsAsyncTask(Integer page, Integer amount, Boolean reload){
            this.page = page;
            this.amount = amount;
            this.reload = reload;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {
            Integer libraryYapsCountBeforeLoading = mLibrary.yaps.size();
            String api_url = "http://api.yapster.co/yap/load/library/yaps/";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(api_url);
            // List<NameValuePair> nameValuePairs = null;
            String nameValuePairsJSONObjectString = null;
            try {
                JSONObject nameValuePairsJSONObject = new JSONObject();
                nameValuePairsJSONObject.put("user_id",
                        mUser.id);
                nameValuePairsJSONObject.put("session_id",
                        mUser.sessionID);
                nameValuePairsJSONObject.put("page",
                        page);
                nameValuePairsJSONObject.put("amount",amount);
                nameValuePairsJSONObject.put("library_id",mLibrary.id);
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
            apiPlayerLoadYapsJsonResponse = writer.toString();
            System.out.println("This is the response : " + apiPlayerLoadYapsJsonResponse);
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(apiPlayerLoadYapsJsonResponse);
            Boolean valid = jsonElement.getAsJsonObject().get("valid").getAsBoolean();
            if (valid == true) {
                JsonElement data_json_element = jsonElement.getAsJsonObject().get("data");
                if (data_json_element.isJsonNull() == false) {
                    JsonArray data = jsonElement.getAsJsonObject().get("data").getAsJsonArray();
                    for (Integer i = 0; i < data.size(); i++) {
                        JsonObject yap_json_element = data.get(i).getAsJsonObject();
                        Yap library_yap = new Yap(yap_json_element,null);
                        mLibrary.yaps.add(library_yap);
                    }
                    mLibrary.page = page;
                }else{
                    System.out.println("data_json_element : " + data_json_element.toString());
                }
            }
            Integer libraryYapsCountAfterLoading = mLibrary.yaps.size();
            if (libraryYapsCountBeforeLoading == libraryYapsCountAfterLoading){
                _player.hasLoadedAll = true;
                mLibrary.hasLoadedAll = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


        }

    }

    class PlayerPlayAsyncTask extends
            AsyncTask<Void, Void, Void> {

        Yap yap;

        PlayerPlayAsyncTask(Yap yap){
            this.yap = yap;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                _player.setDataSource(yap.audioPathURL.toString());
                _player.prepare();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                Log.d("IllegalArgument", e.getMessage());
                e.printStackTrace();
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            _player.start();

        }

    }

}
