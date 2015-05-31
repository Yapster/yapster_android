package co.yapster.yapster;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.transition.Explode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
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
import java.util.concurrent.ExecutionException;

/**
 * Created by gurkarangulati on 2/5/15.
 */

class MainActivityViewAllSubscribedUsersViewHolder {

    TextView usersNumberLabel;
    ListView listViewAllSubscribedUsers;
    ProgressBar progressBarForLoading;
    View listViewAllSubscribedUsersFooterView;

}

public class MainActivityViewAllSubscribedUsers extends Activity {

    float mScreenHeight;
    float mScreenWidth;
    ActionBar actionBar;
    Intent intent;
    Bundle intentBundle;
    Activity activity;
    View view;
    Bundle savedInstanceState;
    DisplayImageOptions defaultOptions;
    ImageLoaderConfiguration config;
    ImageLoader imageLoader;
    DisplayImageOptions options;
    Drawable fallback;
    MainActivityViewAllSubscribedUsersViewHolder mainActivityViewAllSubscribedUsersViewHolder;
    User user;
    String apiViewAllSubscribedUsersJsonResponse;
    String apiViewAllSubscribedUsersJsonResponse2;
    ArrayList<User> users;
    ArrayList<User> newUsers;
    UserListLazyAdapter userListLazyAdapter;
    Boolean reloaded = false;
    AsyncTask<Void, Void, Void> apiViewAllSubscribedUsersAsyncTask;
    public static int NUMBER_OF_USERS_PER_PAGE = 10;
    Window window;
    InfiniteListViewScrollListener usersSubscribedInfiniteListViewScrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // inside your activity (if you did not enable transitions in your
        // theme)
        window = getWindow();
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        window.setAllowEnterTransitionOverlap(true);
        window.setAllowReturnTransitionOverlap(true);
        window.setSharedElementExitTransition(new Explode());
        window.setSharedElementEnterTransition(new Explode());
        window.setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#ffffff")));
        setContentView(R.layout.activity_main_view_all_subscribed_users);
        // set an exit transition
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        mScreenHeight = displayMetrics.heightPixels / displayMetrics.density;
        mScreenWidth = displayMetrics.widthPixels / displayMetrics.density;
        System.out.println("Display Metrics = height : " + mScreenHeight
                + "   width  : " + mScreenWidth);
        actionBar = getActionBar();
        // Hide the action bar title
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setElevation(1.0f);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color
                .parseColor("#ffffff")));
        actionBar.setTitle("All Subscribed Users");
        intent = getIntent();
        intentBundle = getIntent().getExtras();
        activity = this;
        this.savedInstanceState = savedInstanceState;
        view = this.findViewById(android.R.id.content);
        fallback = getResources().getDrawable( R.drawable.default1);
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(fallback)
                .showImageOnFail(fallback)
                .showImageOnLoading(fallback).build();
        // END - UNIVERSAL IMAGE LOADER SETUP
        mainActivityViewAllSubscribedUsersViewHolder = new MainActivityViewAllSubscribedUsersViewHolder();
        user = intentBundle.getParcelable("user");
        try {
            new MainActivityViewAllSubscribedUsersViewAsyncTask()
                    .execute().get();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_profile_user_popover_menu_with_popover, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * On selecting action bar icons
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.action_search:
                // search action
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class MainActivityViewAllSubscribedUsersViewAsyncTask extends
            AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            mainActivityViewAllSubscribedUsersViewHolder.usersNumberLabel = (TextView) view.findViewById(R.id.usersNumberLabel);
            mainActivityViewAllSubscribedUsersViewHolder.usersNumberLabel.setText("Users: (" + user.subscribingUsersCount.toString() + ")");
            mainActivityViewAllSubscribedUsersViewHolder.listViewAllSubscribedUsers = (ListView) view.findViewById(R.id.listViewAllSubscribedUsers);
            AdapterView.OnItemClickListener listViewAllSubscribedUsersOnClickListener = new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View listViewAllSubscribedUsersView, int position, long id) {
                    UserListLazyAdapterViewHolder userListLazyAdapterViewHolder = (UserListLazyAdapterViewHolder) listViewAllSubscribedUsersView.getTag();
                    User listViewAllSubscribedUsersProfileUser = users.get(position);
                    userListLazyAdapterViewHolder.profile_user = listViewAllSubscribedUsersProfileUser;
                    userListLazyAdapterViewHolder.textViewUserName = (TextView) listViewAllSubscribedUsersView
                            .findViewById(R.id.textViewUserName); // title
                    userListLazyAdapterViewHolder.textViewUserDescription = (TextView) listViewAllSubscribedUsersView
                            .findViewById(R.id.textViewUserDescription);
                    userListLazyAdapterViewHolder.imageViewUserPicture = (ImageView) listViewAllSubscribedUsersView
                            .findViewById(R.id.imageViewUserPicture);
                    userListLazyAdapterViewHolder.imageViewUserPicture
                            .setTransitionName("userProfilePicture");
                    Intent intent = new Intent(activity,
                            ProfileScreenActivity.class);
                    int[] screenLocation = new int[2];
                    userListLazyAdapterViewHolder.imageViewUserPicture.getLocationOnScreen(screenLocation);
                    int orientation = activity.getResources().getConfiguration().orientation;

                    intent.putExtra("user", user);
                    intent.putExtra("profile_user", userListLazyAdapterViewHolder.profile_user);
                    intent.putExtra("orientation", orientation);
                    intent.putExtra("left", screenLocation[0]);
                    intent.putExtra("top", screenLocation[1]);
                    intent.putExtra("width", userListLazyAdapterViewHolder.imageViewUserPicture.getWidth());
                    intent.putExtra("height", userListLazyAdapterViewHolder.imageViewUserPicture.getHeight());

                    ActivityOptions options = ActivityOptions
                            .makeSceneTransitionAnimation(
                                    activity,
                                    Pair.create(
                                            (View) userListLazyAdapterViewHolder.imageViewUserPicture,
                                            "userProfilePicture"),
                                    Pair.create(
                                            (View) userListLazyAdapterViewHolder.textViewUserName,
                                            "userName"));
                    activity.startActivity(intent, options.toBundle());
                    activity.overridePendingTransition(0, 0);
                }
            };
            mainActivityViewAllSubscribedUsersViewHolder.listViewAllSubscribedUsers.setOnItemClickListener(listViewAllSubscribedUsersOnClickListener);
            mainActivityViewAllSubscribedUsersViewHolder.listViewAllSubscribedUsersFooterView = ((LayoutInflater) activity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_view_loading, null, false);
            mainActivityViewAllSubscribedUsersViewHolder.listViewAllSubscribedUsers.setElevation(4.0f);
            mainActivityViewAllSubscribedUsersViewHolder.listViewAllSubscribedUsers.setTranslationZ(4.0f);
            mainActivityViewAllSubscribedUsersViewHolder.progressBarForLoading = (ProgressBar) view.findViewById(R.id.progressBarForLoading);
            mainActivityViewAllSubscribedUsersViewHolder.progressBarForLoading.setVisibility(View.VISIBLE);
            users = new ArrayList<User>();
            userListLazyAdapter = new UserListLazyAdapter(activity, users,user);
            view.setTag(mainActivityViewAllSubscribedUsersViewHolder);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mainActivityViewAllSubscribedUsersViewHolder.listViewAllSubscribedUsers.setAdapter(userListLazyAdapter);
            usersSubscribedInfiniteListViewScrollListener =  new InfiniteListViewScrollListener(NUMBER_OF_USERS_PER_PAGE/2) {
                @Override
                public void loadMore(int page, int totalItemsCount) {
                    apiViewAllSubscribedUsersAsyncTask = new APIViewAllSubscribedUsersAsyncTask(page,NUMBER_OF_USERS_PER_PAGE,false);
                    apiViewAllSubscribedUsersAsyncTask
                            .execute();
                }
            };
            mainActivityViewAllSubscribedUsersViewHolder.listViewAllSubscribedUsers.setOnScrollListener(usersSubscribedInfiniteListViewScrollListener);
            apiViewAllSubscribedUsersAsyncTask = new APIViewAllSubscribedUsersAsyncTask(1,NUMBER_OF_USERS_PER_PAGE,false);
            apiViewAllSubscribedUsersAsyncTask
                    .execute();
        }

    }

    class APIViewAllSubscribedUsersAsyncTask extends
            AsyncTask<Void, Void, Void> {

        Integer page;
        Integer amount;
        Boolean reload;

        APIViewAllSubscribedUsersAsyncTask(Integer page, Integer amount, Boolean reload){
            this.page = page;
            this.amount = amount;
            this.reload = reload;
        }

        @Override
        protected void onPreExecute() {
//            mainActivityViewAllSubscribedUsersViewHolder.listViewAllSubscribedUsers.addFooterView(mainActivityViewAllSubscribedUsersViewHolder.listViewAllSubscribedUsersFooterView);
        }

        @Override
        protected Void doInBackground(Void... params) {
            Integer userListLazyAdapterCountBeforeLoadMore =  userListLazyAdapter.getCount();
            String api_url = "http://api.yapster.co/users/load/dashboard/subscribed/users/";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(api_url);
            // List<NameValuePair> nameValuePairs = null;
            String nameValuePairsJSONObjectString = null;
            try {
                JSONObject nameValuePairsJSONObject = new JSONObject();
                nameValuePairsJSONObject.put("user_id",
                        user.id);
                nameValuePairsJSONObject.put("session_id",
                        user.sessionID);
                nameValuePairsJSONObject.put("page",
                        page);
                nameValuePairsJSONObject.put("amount",amount);

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
            apiViewAllSubscribedUsersJsonResponse = writer.toString();
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(apiViewAllSubscribedUsersJsonResponse);
            Boolean valid = jsonElement.getAsJsonObject().get("valid").getAsBoolean();
            if (valid == true) {
                JsonElement data_json_element = jsonElement.getAsJsonObject().get("data");
                if (data_json_element.isJsonNull() == false) {
                    JsonArray data = jsonElement.getAsJsonObject().get("data").getAsJsonArray();
                    for (Integer i = 0; i < data.size(); i++) {
                        JsonObject user_json_object = data.get(i).getAsJsonObject();
                        User userSubscribedReceived = new User(false, user_json_object);
                        users.add(i + userListLazyAdapterCountBeforeLoadMore, userSubscribedReceived);
                    }
                }else{
                    System.out.println("data_json_element : " + data_json_element.toString());
                }
            }
            Integer userListLazyAdapterCountAfterLoadMore =  userListLazyAdapter.getCount();
            if (userListLazyAdapterCountBeforeLoadMore == userListLazyAdapterCountAfterLoadMore){
                usersSubscribedInfiniteListViewScrollListener.hasLoadedAll = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            userListLazyAdapter.notifyDataSetChanged();
            if (mainActivityViewAllSubscribedUsersViewHolder.progressBarForLoading.getVisibility() == View.VISIBLE){
                mainActivityViewAllSubscribedUsersViewHolder.progressBarForLoading.setVisibility(View.GONE);
            }
//            mainActivityViewAllSubscribedUsersViewHolder.listViewAllSubscribedUsers.removeFooterView(mainActivityViewAllSubscribedUsersViewHolder.listViewAllSubscribedUsersFooterView);

        }

    }
}
