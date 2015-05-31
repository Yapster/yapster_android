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
 * Created by gurkarangulati on 3/28/15.
 */

class SearchResultsViewAllLibrariesViewHolder {

    TextView usersNumberLabel;
    ListView listViewAllSearchedLibraries;
    ProgressBar progressBarForLoading;
    View listViewAllSearchedLibrariesFooterView;

}

public class SearchResultsViewAllLibraries extends Activity {

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
    SearchResultsViewAllLibrariesViewHolder searchResultsViewAllLibrariesViewHolder;
    User user;
    String apiViewAllSearchedLibrariesJsonResponse;
    ArrayList<Library> libraries;
    ArrayList<Library> newLibraries;
    LibraryListLazyAdapter libraryListLazyAdapter;
    Boolean reloaded = false;
    AsyncTask<Void, Void, Void> apiViewAllSearchedLibrariesAsyncTask;
    public static int NUMBER_OF_LIBRARIES_PER_PAGE = 10;
    Window window;
    InfiniteListViewScrollListener librariesSearchedInfiniteListViewScrollListener;
    Integer searchID;

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
        actionBar.setTitle("All Searched Users");
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
        searchResultsViewAllLibrariesViewHolder = new SearchResultsViewAllLibrariesViewHolder();
        user = intentBundle.getParcelable("user");
        try {
            new SearchResultsViewAllUsersViewAsyncTask()
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

    class SearchResultsViewAllUsersViewAsyncTask extends
            AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            searchID = intent.getIntExtra("searchID", 0);
            searchResultsViewAllLibrariesViewHolder.usersNumberLabel = (TextView) view.findViewById(R.id.usersNumberLabel);
            searchResultsViewAllLibrariesViewHolder.usersNumberLabel.setText("");
            searchResultsViewAllLibrariesViewHolder.listViewAllSearchedLibraries = (ListView) view.findViewById(R.id.listViewAllSearchedLibraries);
            AdapterView.OnItemClickListener listViewAllSearchedUsersOnClickListener = new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View listViewAllSearchedLibrariesView, int position, long id) {
                    LibraryListLazyAdapterViewHolder libraryListLazyAdapterViewHolder = (LibraryListLazyAdapterViewHolder) listViewAllSearchedLibrariesView.getTag();
                    Library listViewAllSearchedLibrariesLibrary = libraries.get(position);
                    libraryListLazyAdapterViewHolder.library = listViewAllSearchedLibrariesLibrary;
                    libraryListLazyAdapterViewHolder.textViewLibraryName = (TextView) listViewAllSearchedLibrariesView.findViewById(R.id.textViewLibraryName); // title
                    libraryListLazyAdapterViewHolder.textViewLibraryDescription = (TextView) listViewAllSearchedLibrariesView
                            .findViewById(R.id.textViewLibraryDescription);
                    libraryListLazyAdapterViewHolder.imageViewLibraryPicture = (ImageView) listViewAllSearchedLibrariesView
                            .findViewById(R.id.imageViewLibraryPicture);
                    libraryListLazyAdapterViewHolder.imageViewLibraryPicture
                            .setTransitionName("libraryPicture");
                    Intent intent = new Intent(activity,
                            LibraryDetailsScreenActivity.class);
                    int[] screenLocation = new int[2];
                    libraryListLazyAdapterViewHolder.imageViewLibraryPicture.getLocationOnScreen(screenLocation);
                    int orientation = activity.getResources().getConfiguration().orientation;

                    intent.putExtra("user",user);
                    intent.putExtra("library",libraries.get(position));
                    intent.putExtra("orientation", orientation);
                    intent.putExtra("left", screenLocation[0]);
                    intent.putExtra("top", screenLocation[1]);
                    intent.putExtra("width", libraryListLazyAdapterViewHolder.imageViewLibraryPicture.getWidth());
                    intent.putExtra("height", libraryListLazyAdapterViewHolder.imageViewLibraryPicture.getHeight());

                    ActivityOptions options = ActivityOptions
                            .makeSceneTransitionAnimation(
                                    activity,
                                    Pair.create(
                                            (View) libraryListLazyAdapterViewHolder.imageViewLibraryPicture,
                                            "libraryPicture"),
                                    Pair.create(
                                            (View) libraryListLazyAdapterViewHolder.textViewLibraryName,
                                            "libraryName"));
                    activity.startActivity(intent, options.toBundle());
                    activity.overridePendingTransition(0, 0);
                }
            };
            searchResultsViewAllLibrariesViewHolder.listViewAllSearchedLibraries.setOnItemClickListener(listViewAllSearchedUsersOnClickListener);
            searchResultsViewAllLibrariesViewHolder.listViewAllSearchedLibrariesFooterView = ((LayoutInflater) activity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_view_loading, null, false);
            searchResultsViewAllLibrariesViewHolder.listViewAllSearchedLibraries.setElevation(4.0f);
            searchResultsViewAllLibrariesViewHolder.listViewAllSearchedLibraries.setTranslationZ(4.0f);
            searchResultsViewAllLibrariesViewHolder.progressBarForLoading = (ProgressBar) view.findViewById(R.id.progressBarForLoading);
            searchResultsViewAllLibrariesViewHolder.progressBarForLoading.setVisibility(View.VISIBLE);
            libraries = new ArrayList<Library>();
            libraryListLazyAdapter = new LibraryListLazyAdapter(activity, libraries);
            view.setTag(searchResultsViewAllLibrariesViewHolder);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            searchResultsViewAllLibrariesViewHolder.listViewAllSearchedLibraries.setAdapter(libraryListLazyAdapter);
            librariesSearchedInfiniteListViewScrollListener =  new InfiniteListViewScrollListener(NUMBER_OF_LIBRARIES_PER_PAGE/2) {
                @Override
                public void loadMore(int page, int totalItemsCount) {
                    apiViewAllSearchedLibrariesAsyncTask = new APIViewAllSearchedLibrariesAsyncTask(page,NUMBER_OF_LIBRARIES_PER_PAGE,false);
                    apiViewAllSearchedLibrariesAsyncTask
                            .execute();
                }
            };
            searchResultsViewAllLibrariesViewHolder.listViewAllSearchedLibraries.setOnScrollListener(librariesSearchedInfiniteListViewScrollListener);
            apiViewAllSearchedLibrariesAsyncTask = new APIViewAllSearchedLibrariesAsyncTask(1,NUMBER_OF_LIBRARIES_PER_PAGE,false);
            apiViewAllSearchedLibrariesAsyncTask
                    .execute();
        }

    }

    class APIViewAllSearchedLibrariesAsyncTask extends
            AsyncTask<Void, Void, Void> {

        Integer page;
        Integer amount;
        Boolean reload;

        APIViewAllSearchedLibrariesAsyncTask(Integer page, Integer amount, Boolean reload){
            this.page = page;
            this.amount = amount;
            this.reload = reload;
        }

        @Override
        protected void onPreExecute() {
//            searchResultsViewAllLibrariesViewHolder.listViewAllSearchedUsers.addFooterView(searchResultsViewAllLibrariesViewHolder.listViewAllSearchedUsersFooterView);
        }

        @Override
        protected Void doInBackground(Void... params) {
            Integer userListLazyAdapterCountBeforeLoadMore =  libraryListLazyAdapter.getCount();
            String api_url = "http://api.yapster.co/search/default/";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(api_url);
            // List<NameValuePair> nameValuePairs = null;
            String nameValuePairsJSONObjectString = null;
            try {
                JSONObject nameValuePairsJSONObject = new JSONObject();
                if (user != null){
                    nameValuePairsJSONObject.put("user_id",
                            user.id);
                    nameValuePairsJSONObject.put("session_id",
                            user.sessionID);
                }
                nameValuePairsJSONObject.put("search_id",
                        searchID);
                nameValuePairsJSONObject.put("search_type",
                        "libraries");
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
            apiViewAllSearchedLibrariesJsonResponse = writer.toString();
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(apiViewAllSearchedLibrariesJsonResponse);
            Boolean valid = jsonElement.getAsJsonObject().get("valid").getAsBoolean();
            if (valid == true) {
                JsonElement data_json_element = jsonElement.getAsJsonObject().get("data");
                if (data_json_element.isJsonNull() == false) {
                    JsonArray data = jsonElement.getAsJsonObject().get("data").getAsJsonArray();
                    for (Integer i = 0; i < data.size(); i++) {
                        JsonObject library_json_object = data.get(i).getAsJsonObject();
                        Library librarySearchedReceived = new Library(library_json_object,0);
                        libraries.add(i + userListLazyAdapterCountBeforeLoadMore, librarySearchedReceived);
                    }
                }else{
                    System.out.println("data_json_element : " + data_json_element.toString());
                }
            }
            Integer userListLazyAdapterCountAfterLoadMore =  libraryListLazyAdapter.getCount();
            if (userListLazyAdapterCountBeforeLoadMore == userListLazyAdapterCountAfterLoadMore){
                librariesSearchedInfiniteListViewScrollListener.hasLoadedAll = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            libraryListLazyAdapter.notifyDataSetChanged();
            if (searchResultsViewAllLibrariesViewHolder.progressBarForLoading.getVisibility() == View.VISIBLE){
                searchResultsViewAllLibrariesViewHolder.progressBarForLoading.setVisibility(View.GONE);
            }
//            searchResultsViewAllLibrariesViewHolder.listViewAllSearchedUsers.removeFooterView(searchResultsViewAllLibrariesViewHolder.listViewAllSearchedUsersFooterView);

        }

    }
}
