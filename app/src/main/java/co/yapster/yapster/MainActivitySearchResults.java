package co.yapster.yapster;

import android.animation.TimeInterpolator;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.transition.Explode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

class MainActivitySearchResultsViewHolder{

    RelativeLayout relativeLayoutForScreen;
    LinearLayout linearlayoutView;

    ProgressBar progressBarForScreenLoaded;

    LinearLayout linearLayoutForUsers;
    RelativeLayout relativeLayoutForUsers;
    TextView textViewUsers;
    Button buttonViewAllUsers;
    LinearLayout linearLayoutLineInUsersTitle;
    ListView listViewUsers;

    LinearLayout linearLayoutForLibraries;
    RelativeLayout relativeLayoutForLibraries;
    TextView textViewLibraries;
    Button buttonViewAllLibraries;
    LinearLayout linearLayoutLineInLibrariesTitle;
    ListView listViewLibraries;

    LinearLayout linearLayoutForYaps;
    RelativeLayout relativeLayoutForYaps;
    TextView textViewYaps;
    Button buttonViewAllYaps;
    LinearLayout linearLayoutLineInYapsTitle;
    ListView listViewYaps;

    SlidingUpPanelLayout slidingLayout;
    RelativeLayout slidingContainer;
    ImageView imageViewPlayerYapImage;
    TextView textViewPlayerYapTitle;
    TextView textViewPlayerYapUserName;
    RelativeLayout relativeLayoutForPlayerScreen;
    ImageButton imageButtonDownArrowDismissFullPlayer;
    LinearLayout linearLayoutPlayerControls;
    ImageButton imageButtonFullPlayerPrevious;
    ImageButton imageButtonFullPlayerPlayAndPause;
    ImageButton imageButtonFullPlayerNext;
    LinearLayout linearLayoutFullPlayerPlayingYapInfo;
    ImageView imageViewFullPlayerYapImage;
    TextView textViewFullPlayerYapTitle;
    TextView textViewFullPlayerYapUser;
    TextView textViewFullPlayerYapDescription;

    SearchView searchView;

}

public class MainActivitySearchResults extends Activity implements
        SlidingUpPanelLayout.PanelSlideListener {

    private ActionBar actionBar;
    private Intent intent;
    private Bundle intentBundle;
    private static final TimeInterpolator sDecelerator = new DecelerateInterpolator();
    private static final TimeInterpolator sAccelerator = new AccelerateInterpolator();
    private static final int ANIM_DURATION = 500;
    float mScreenHeight;
    float mScreenWidth;
    public MainActivitySearchResultsViewHolder mainActivitySearchResultsViewHolder;
    private View view;
    private Bundle savedInstanceState;
    Activity activity;
    AsyncTask<Void, Void, Void> apiDefaultSearch;

    public ImageLoader imageLoader;
    public DisplayImageOptions options;
    public Drawable fallback;
    Library library;
    User user;
    String apiDefaultSearchJsonResponse;
    public static int NUMBER_OF_YAPS_LOADED_ON_EACH_PAGE = 3;
    Integer mPage;
    float playerHeight = 0.0f;
    Menu menu;
    Player player;
    String query;
    LayoutInflater layoutInflater;
    View actionBarCustomView;
    String originScreen;
    ArrayList<User> users;
    ArrayList<Library> libraries;
    ArrayList<Yap> yaps;
    UserListLazyAdapter userLazyAdapter;
    LibraryListLazyAdapter libraryLazyAdapter;
    YapLazyAdapter yapLazyAdapter;
    Integer searchID;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// http://www.androidhive.info/2013/11/android-working-with-action-bar/
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setAllowEnterTransitionOverlap(true);
        getWindow().setAllowReturnTransitionOverlap(true);
        getWindow().setSharedElementExitTransition(new Explode());
        getWindow().setSharedElementEnterTransition(new Explode());
        // getWindow().setBackgroundDrawable(
        // new ColorDrawable(Color.parseColor("#ffffff")));
        setContentView(R.layout.activity_main_search_results);
        activity = this;
        // set an exit transition
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        mScreenHeight = displayMetrics.heightPixels / displayMetrics.density;
        mScreenWidth = displayMetrics.widthPixels / displayMetrics.density;
        System.out.println("Display Metrics = height : " + mScreenHeight
                + "   width  : " + mScreenWidth);
        // View decorView = getWindow().getDecorView();
        // int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        // decorView.setSystemUiVisibility(uiOptions);
        layoutInflater = LayoutInflater.from(this);
        actionBar = getActionBar();
        // Hide the action bar title
        actionBar.setTitle("");
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setElevation(1.0f);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color
                .parseColor("#ffffff")));
        actionBar.setDisplayHomeAsUpEnabled(false);
//        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        actionBarCustomView = layoutInflater.inflate(R.layout.action_bar_full_bar_search_view, null);
//        actionBar.setCustomView(actionBarCustomView);
        intent = getIntent();
        intentBundle = getIntent().getExtras();
        mainActivitySearchResultsViewHolder = new MainActivitySearchResultsViewHolder();
        this.savedInstanceState = savedInstanceState;
        view = this.findViewById(android.R.id.content);
        fallback = activity.getResources().getDrawable( R.drawable.default1 );
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).showImageForEmptyUri(fallback)
                .showImageOnFail(fallback)
                .showImageOnLoading(fallback).build();
        // END - UNIVERSAL IMAGE LOADER SETUP

        try {
            new SearchResultsViewAsyncTask()
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
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_search_results_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setIconified(false);
        searchView.setFocusable(false);
        searchView.setQuery(query, false);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                onSearchRequested();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setupPlayerScreen() {
        actionBar.setDisplayShowTitleEnabled(false);
        menu.getItem(0).setVisible(false);
        mainActivitySearchResultsViewHolder.textViewPlayerYapTitle.setVisibility(View.INVISIBLE);
        mainActivitySearchResultsViewHolder.textViewPlayerYapUserName.setVisibility(View.INVISIBLE);
        mainActivitySearchResultsViewHolder.imageViewPlayerYapImage.setVisibility(View.INVISIBLE);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_action_bar_gradient));
        actionBar.setElevation(0.0f);
        mainActivitySearchResultsViewHolder.relativeLayoutForPlayerScreen.setVisibility(View.VISIBLE);
    }

    public void setupMainViewScreen() {
        actionBar.setDisplayShowTitleEnabled(true);
        menu.getItem(0).setVisible(true);
        mainActivitySearchResultsViewHolder.textViewPlayerYapTitle.setVisibility(View.VISIBLE);
        mainActivitySearchResultsViewHolder.textViewPlayerYapUserName.setVisibility(View.VISIBLE);
        mainActivitySearchResultsViewHolder.imageViewPlayerYapImage.setVisibility(View.VISIBLE);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color
                .parseColor("#ffffff")));
        actionBar.setElevation(1.0f);
        mainActivitySearchResultsViewHolder.relativeLayoutForPlayerScreen.setVisibility(View.INVISIBLE);
    }

    public void onPanelSlide(View panel, float slideOffset) {
        // TODO Auto-generated method stub
        if (playerHeight == 0.0f || slideOffset == 0.0f || slideOffset == 1.0f) {
            playerHeight = slideOffset;
        } else {
            if (slideOffset > playerHeight) {
                setupPlayerScreen();
            }
            // If the panel is not fully expanded set the whole
            // panel as dragview
            else if (slideOffset < playerHeight) {
                setupMainViewScreen();

            }
        }
        playerHeight = slideOffset;
    }

    @Override
    public void onPanelCollapsed(View view) {
        setupMainViewScreen();
    }

    @Override
    public void onPanelExpanded(View view) {
        setupPlayerScreen();
    }

    @Override
    public void onPanelAnchored(View view) {

    }

    @Override
    public void onPanelHidden(View view) {

    }

    public static void getTotalHeightofListView(ListView listView) {

        ListAdapter mAdapter = listView.getAdapter();

        int totalHeight = 0;

        for (int i = 0; i < mAdapter.getCount(); i++) {
            View mView = mAdapter.getView(i, null, listView);

            mView.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),

                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

            totalHeight += mView.getMeasuredHeight();
            Log.w("HEIGHT" + i, String.valueOf(totalHeight));

        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (mAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();

    }


    class SearchResultsViewAsyncTask extends
            AsyncTask<Void, Void, Void> {
        // private ProfileScreenViewHolder profileScreenViewHolder;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
                query = intent.getStringExtra(SearchManager.QUERY);
                SearchRecentSuggestions suggestions = new SearchRecentSuggestions(
                        activity,
                        MainActivitySearchSuggestionsRecentlySearched.AUTHORITY,
                        MainActivitySearchSuggestionsRecentlySearched.MODE);
                suggestions.saveRecentQuery(query, null);
            }
            user = intentBundle.getParcelable("user");
            originScreen = intent.getStringExtra("origin_screen");
            mainActivitySearchResultsViewHolder.relativeLayoutForScreen = (RelativeLayout) view.findViewById(R.id.relativeLayoutForScreen);
            mainActivitySearchResultsViewHolder.progressBarForScreenLoaded = (ProgressBar) view.findViewById(R.id.progressBarForScreenLoaded);
            mainActivitySearchResultsViewHolder.slidingLayout = (SlidingUpPanelLayout) view.findViewById(R.id.slidingLayout);
            mainActivitySearchResultsViewHolder.linearlayoutView = (LinearLayout) view.findViewById(R.id.linearlayoutView);
            mainActivitySearchResultsViewHolder.linearLayoutForUsers = (LinearLayout) view.findViewById(R.id.linearLayoutForUsers);
            mainActivitySearchResultsViewHolder.relativeLayoutForUsers = (RelativeLayout) view.findViewById(R.id.relativeLayoutForUsers);
            mainActivitySearchResultsViewHolder.textViewUsers = (TextView) view.findViewById(R.id.textViewUsers);
            mainActivitySearchResultsViewHolder.buttonViewAllUsers = (Button) view.findViewById(R.id.buttonViewAllUsers);
            mainActivitySearchResultsViewHolder.linearLayoutLineInUsersTitle = (LinearLayout) view.findViewById(R.id.linearLayoutLineInUsersTitle);
            mainActivitySearchResultsViewHolder.listViewUsers = (ListView) view.findViewById(R.id.listViewUsers);

            mainActivitySearchResultsViewHolder.linearLayoutForLibraries = (LinearLayout) view.findViewById(R.id.linearLayoutForLibraries);
            mainActivitySearchResultsViewHolder.relativeLayoutForLibraries = (RelativeLayout) view.findViewById(R.id.relativeLayoutForLibraries);
            mainActivitySearchResultsViewHolder.textViewLibraries = (TextView) view.findViewById(R.id.textViewLibraries);
            mainActivitySearchResultsViewHolder.buttonViewAllLibraries = (Button) view.findViewById(R.id.buttonViewAllLibraries);
            mainActivitySearchResultsViewHolder.linearLayoutLineInLibrariesTitle = (LinearLayout) view.findViewById(R.id.linearLayoutLineInLibrariesTitle);
            mainActivitySearchResultsViewHolder.listViewLibraries = (ListView) view.findViewById(R.id.listViewLibraries);

            mainActivitySearchResultsViewHolder.linearLayoutForYaps = (LinearLayout) view.findViewById(R.id.linearLayoutForYaps);
            mainActivitySearchResultsViewHolder.relativeLayoutForYaps = (RelativeLayout) view.findViewById(R.id.relativeLayoutForYaps);
            mainActivitySearchResultsViewHolder.textViewYaps = (TextView) view.findViewById(R.id.textViewYaps);
            mainActivitySearchResultsViewHolder.buttonViewAllYaps = (Button) view.findViewById(R.id.buttonViewAllYaps);
            mainActivitySearchResultsViewHolder.linearLayoutLineInYapsTitle = (LinearLayout) view.findViewById(R.id.linearLayoutLineInYapsTitle);
            mainActivitySearchResultsViewHolder.listViewYaps = (ListView) view.findViewById(R.id.listViewYaps);

            mainActivitySearchResultsViewHolder.slidingLayout = (SlidingUpPanelLayout) view.findViewById(R.id.slidingLayout);
            mainActivitySearchResultsViewHolder.slidingContainer = (RelativeLayout) view.findViewById(R.id.slidingContainer);
            mainActivitySearchResultsViewHolder.imageViewPlayerYapImage = (ImageView) view.findViewById(R.id.imageViewPlayerYapImage);
            mainActivitySearchResultsViewHolder.textViewPlayerYapTitle = (TextView) view.findViewById(R.id.textViewPlayerYapTitle);
            mainActivitySearchResultsViewHolder.textViewPlayerYapUserName = (TextView) view.findViewById(R.id.textViewPlayerYapUserName);

            mainActivitySearchResultsViewHolder.relativeLayoutForPlayerScreen = (RelativeLayout) view.findViewById(R.id.relativeLayoutForPlayerScreen);
            mainActivitySearchResultsViewHolder.imageViewFullPlayerYapImage = (ImageView) view.findViewById(R.id.imageViewFullPlayerYapImage);
            mainActivitySearchResultsViewHolder.textViewFullPlayerYapTitle = (TextView) view.findViewById(R.id.textViewFullPlayerYapTitle);
            mainActivitySearchResultsViewHolder.textViewFullPlayerYapUser = (TextView) view.findViewById(R.id.textViewFullPlayerYapUser);
            mainActivitySearchResultsViewHolder.textViewFullPlayerYapDescription = (TextView) view.findViewById(R.id.textViewFullPlayerYapDescription);
            mainActivitySearchResultsViewHolder.imageButtonDownArrowDismissFullPlayer = (ImageButton) view.findViewById(R.id.imageButtonDownArrowDismissFullPlayer);
            mainActivitySearchResultsViewHolder.linearLayoutPlayerControls = (LinearLayout) view.findViewById(R.id.linearLayoutPlayerControls);
            mainActivitySearchResultsViewHolder.imageButtonFullPlayerPrevious = (ImageButton) view.findViewById(R.id.imageButtonFullPlayerPrevious);
            mainActivitySearchResultsViewHolder.imageButtonFullPlayerPlayAndPause = (ImageButton) view.findViewById(R.id.imageButtonFullPlayerPlayAndPause);
            mainActivitySearchResultsViewHolder.imageButtonFullPlayerNext = (ImageButton) view.findViewById(R.id.imageButtonFullPlayerNext);

//            mainActivitySearchResultsViewHolder.searchView = (SearchView) actionBarCustomView.findViewById(R.id.searchView);
//            mainActivitySearchResultsViewHolder.searchView.setQuery(query, false);
            player = Player.getInstance(user);

            mainActivitySearchResultsViewHolder.linearLayoutForUsers.setVisibility(View.INVISIBLE);
            mainActivitySearchResultsViewHolder.linearLayoutForLibraries.setVisibility(View.INVISIBLE);
            mainActivitySearchResultsViewHolder.linearLayoutForYaps.setVisibility(View.INVISIBLE);

            mainActivitySearchResultsViewHolder.imageButtonDownArrowDismissFullPlayer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainActivitySearchResultsViewHolder.slidingLayout.collapsePanel();
                }
            });

            mainActivitySearchResultsViewHolder.imageButtonFullPlayerPrevious.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    player.previousYap();
                }
            });

            mainActivitySearchResultsViewHolder.imageButtonFullPlayerNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    player.nextYap();
                }
            });

            mainActivitySearchResultsViewHolder.imageButtonFullPlayerPlayAndPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Boolean isPlaying = player.isPlaying();
                    if(isPlaying == true){
                        mainActivitySearchResultsViewHolder.imageButtonFullPlayerPlayAndPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_player_play_black));
                        player.pause();
                    }else{
                        mainActivitySearchResultsViewHolder.imageButtonFullPlayerPlayAndPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_player_pause_black));
                        player.start();
                    }
                }
            });

            mainActivitySearchResultsViewHolder.slidingLayout
                    .setPanelSlideListener((SlidingUpPanelLayout.PanelSlideListener) activity);

            users = new ArrayList<User>();
            libraries = new ArrayList<Library>();
            yaps = new ArrayList<Yap>();

            userLazyAdapter = new UserListLazyAdapter(activity,users, user);
            libraryLazyAdapter = new LibraryListLazyAdapter(activity, libraries);
            yapLazyAdapter = new YapLazyAdapter(activity, user, yaps);

            mainActivitySearchResultsViewHolder.listViewUsers.setAdapter(userLazyAdapter);
            mainActivitySearchResultsViewHolder.listViewLibraries.setAdapter(libraryLazyAdapter);
            mainActivitySearchResultsViewHolder.listViewYaps.setAdapter(yapLazyAdapter);

            mainActivitySearchResultsViewHolder.buttonViewAllUsers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent openActivity1 = new Intent(
                            "co.yapster.yapster.SEARCHRESULTSVIEWALLUSERS");
                    openActivity1.putExtra("user", user);
                    openActivity1.putExtra("searchID", searchID);
                    startActivity(openActivity1);
                }
            });

            mainActivitySearchResultsViewHolder.buttonViewAllLibraries.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent openActivity1 = new Intent(
                            "co.yapster.yapster.SEARCHRESULTSVIEWALLLIBRARIES");
                    openActivity1.putExtra("user", user);
                    openActivity1.putExtra("searchID", searchID);
                    startActivity(openActivity1);
                }
            });

            mainActivitySearchResultsViewHolder.buttonViewAllYaps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent openActivity1 = new Intent(
                            "co.yapster.yapster.SEARCHRESULTSVIEWALLYAPS");
                    openActivity1.putExtra("user", user);
                    openActivity1.putExtra("searchID", searchID);
                    startActivity(openActivity1);
                }
            });

            view.setTag(mainActivitySearchResultsViewHolder);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mainActivitySearchResultsViewHolder.progressBarForScreenLoaded.setVisibility(View.VISIBLE);
            if (query.startsWith("#")){

            }else if(query.startsWith("@")){

            }else{
                apiDefaultSearch = new APIDefaultSearch();
                apiDefaultSearch.execute();
            }

        }

    }

    class APIDefaultSearch extends
            AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            String api_url = "http://api.yapster.co/search/default/";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(api_url);
            // List<NameValuePair> nameValuePairs = null;
            String nameValuePairsJSONObjectString = null;
            try {
                JSONObject nameValuePairsJSONObject = new JSONObject();
                if (user != null) {
                    nameValuePairsJSONObject.put("user_id",
                            user.id);
                    nameValuePairsJSONObject.put("session_id",
                            user.sessionID);
                }
                nameValuePairsJSONObject.put("text",
                        query);
                nameValuePairsJSONObject.put("screen",
                        originScreen);
                nameValuePairsJSONObject.put("search_type",
                        "all");
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
            apiDefaultSearchJsonResponse = writer.toString();
            JsonParser jsonParser = new JsonParser();
            apiDefaultSearchJsonResponse = apiDefaultSearchJsonResponse.trim();
            System.out.println(apiDefaultSearchJsonResponse);
            JsonElement jsonElement = jsonParser.parse(apiDefaultSearchJsonResponse);
            Boolean valid = jsonElement.getAsJsonObject().get("valid").getAsBoolean();
            if (valid == true) {
                searchID = jsonElement.getAsJsonObject().get("search_id").getAsInt();
                JsonObject data = jsonElement.getAsJsonObject().get("data").getAsJsonObject();
                JsonElement usersJsonElement = data.get("users");
                if (usersJsonElement != null){
                    JsonArray users_data_array = usersJsonElement.getAsJsonArray();
                    for (Integer i = 0; i < users_data_array.size(); i++) {
                        JsonObject user_json_object = users_data_array.get(i).getAsJsonObject();
                        User user_searched = new User(false,user_json_object);
                        users.add(user_searched);
                    }

                }
                JsonElement librariesJsonElement = data.get("libraries");
                if (librariesJsonElement != null){
                    JsonArray libraries_data_array = librariesJsonElement.getAsJsonArray();
                    for (Integer i = 0; i < libraries_data_array.size(); i++) {
                        JsonObject library_json_object = libraries_data_array.get(i).getAsJsonObject();
                        Library library_searched = new Library(library_json_object, null);
                        libraries.add(library_searched);
                    }

                }
                JsonElement yapsJsonElement = data.get("yaps");
                if (yapsJsonElement != null){
                    JsonArray yaps_data_array = yapsJsonElement.getAsJsonArray();
                    for (Integer i = 0; i < yaps_data_array.size(); i++) {
                        JsonObject yap_json_object = yaps_data_array.get(i).getAsJsonObject();
                        Yap yap_searched = new Yap(yap_json_object,0);
                        yaps.add(yap_searched);
                    }
                }
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mainActivitySearchResultsViewHolder.progressBarForScreenLoaded.setVisibility(View.GONE);
            if(users.size() > 0){
                userLazyAdapter.notifyDataSetChanged();
                getTotalHeightofListView(mainActivitySearchResultsViewHolder.listViewUsers);
                mainActivitySearchResultsViewHolder.linearLayoutForUsers.setVisibility(View.VISIBLE);
            }else{
                mainActivitySearchResultsViewHolder.linearLayoutForUsers.setVisibility(View.GONE);
            }
            if (libraries.size() > 0){
                libraryLazyAdapter.notifyDataSetChanged();
                getTotalHeightofListView(mainActivitySearchResultsViewHolder.listViewLibraries);
                mainActivitySearchResultsViewHolder.linearLayoutForLibraries.setVisibility(View.VISIBLE);
            }else{
                mainActivitySearchResultsViewHolder.linearLayoutForLibraries.setVisibility(View.GONE);
            }
            if (yaps.size() > 0){
                yapLazyAdapter.notifyDataSetChanged();
                getTotalHeightofListView(mainActivitySearchResultsViewHolder.listViewYaps);
                mainActivitySearchResultsViewHolder.linearLayoutForYaps.setVisibility(View.VISIBLE);
            }else{
                mainActivitySearchResultsViewHolder.linearLayoutForYaps.setVisibility(View.GONE);
            }

            if(users.size() == 0 && libraries.size() == 0 && yaps.size() == 0){
                System.out.println("There were no results.");
            }
        }

    }
}