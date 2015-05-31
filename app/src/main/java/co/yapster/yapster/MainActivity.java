package co.yapster.yapster;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.google.android.gms.gcm.GoogleCloudMessaging;
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
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.SearchManager;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;

class MainViewHolder {
    TextView textViewUserName1;
    ImageView imageViewUserProfilePicture1;
    String stringUserProfileName1;
    String stringUserProfilePictureURL1;
    TextView textViewUserName2;
    ImageView imageViewUserProfilePicture2;
    String stringUserProfileName2;
    String stringUserProfilePictureURL2;
    TextView textViewUserName3;
    ImageView imageViewUserProfilePicture3;
    String stringUserProfileName3;
    String stringUserProfilePictureURL3;
    LinearLayout linearLayoutUserProfilePicture1;
    RelativeLayout relativeLayoutForScreen;
    RelativeLayout relativeLayoutForUserProfilePicture1;
    RelativeLayout relativeLayoutForUserProfilePicture2;
    RelativeLayout relativeLayoutForUserProfilePicture3;
    Button buttonViewAllUsers;
    Button buttonViewAllLibraries;
    LinearLayout linearLayoutForLibraries;
    LinearLayout linearLayoutForListOfLibraries;
    TextView textViewCardTitleUsers;
    TextView textViewCardTitleLibraries;
    LinearLayout linearLayoutLineInUsersTitle;
    RelativeLayout relativeLayoutCardTitleLibraries;
    LinearLayout linearLayoutLineInLibrariesTitle;


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

    RecyclerView recyclerView;

    //    //SlidingNavigationObjects
//    View headerViewOfDrawerListView;
//    RelativeLayout relativeLayoutDrawerTop;
//    ImageView imageViewDrawerUserProfilePicture;
//    LinearLayout linearLayoutDrawerUserText;
//    TextView textViewDrawerUserFullName;
//    TextView textViewDrawerUserUsername;
//    ListView listViewDrawerNavigationOptions;
    DrawerLayout drawerLayout;

}

public class MainActivity extends ActionBarActivity implements
        SlidingUpPanelLayout.PanelSlideListener {

    // action bar
    private android.support.v7.app.ActionBar actionBar;
    public ImageLoader imageLoader;
    static final String KEY_PICTURE_URL = "picture_url";
    static final String KEY_NAME = "name";
    static final String KEY_DESCRIPTION = "description";
    static final String KEY_ID = "id";
    static final String KEY_LIBRARY_INFO = "library_info";
    static final String KEY_LIBRARY_TITLE = "library_title";
    static final String KEY_LIBRARY_YAPS = "library_yaps";
    static final String KEY_LIBRARY_1_YAPS = "library_1_yaps";
    static final String KEY_LIBRARY_2_YAPS = "library_2_yaps";
    static final String KEY_LIBRARY_3_YAPS = "library_3_yaps";
    static final String KEY_YAP_ID = "yap_id";
    static final String KEY_YAP_USER_NAME = "yap_user_name";
    static final String KEY_YAP_TITLE = "yap_title";
    static final String KEY_YAP_DESCRIPTION = "yap_description";
    static final String KEY_YAP_DATE = "yap_date";
    static final String KEY_YAP_LENGTH = "yap_length";
    static final String KEY_YAP_AUDIO_PATH = "yap_audio_path";
    static final String PREFS_KEY = "com.yapster.yapster";
    static final String USER_ID_KEY = "user_id";
    static final String SESSION_ID_KEY = "session_id";
    static final String DEVICE_TYPE_KEY = "device_type";
    static final String IDENTIFIER_KEY = "identifier";
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    static final String TAG = "MainActivity";
    static final String USERS_CARD_TITLE_SUBSCRIBED = "Subscribed Users";
    static final String USERS_CARD_TITLE_EXPLORE = "Top Users";
    static final String LIBRARIES_CARD_TITLE_SUBSCRIBED = "Subscribed Libraries";
    static final String LIBRARIES_CARD_TITLE_EXPLORE = "Top Libraries";
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    ArrayList<HashMap<String, String>> libraryList;
    ArrayList<HashMap<String, String>> stringArrayOfSongsInLibrary;
    HashMap<String, ArrayList<HashMap<String, String>>> libraryData;
    LibraryIndividualsWithYapsLazyAdapter adapter;
    OnClickListener onClickListenerToGoToProfileScreen1;
    OnClickListener onClickListenerToGoToProfileScreen2;
    OnClickListener onClickListenerToGoToProfileScreen3;
    static float sAnimatorScale = 1;
    public static final int ANIM_DURATION = 500;
    MainViewHolder mainViewHolder;
    Intent intent;
    Bundle intentBundle;
    String apiDashboardSubscribedUsersJsonResponse;
    String apiDashboardSubscribedLibrariesJsonResponse;
    String apiDashboardExploreUsersJsonResponse = null;
    String apiDashboardExploreLibrariesJsonResponse = null;
    ArrayList<User> subscribedUsersList;
    ArrayList<Library> subscribedLibrariesList;
    ArrayList<User> exploreUsersList;
    ArrayList<Library> exploreLibrariesList;
    Activity activity;
    View view;
    Drawable fallback;
    Drawable fallback2;
    AsyncTask<Void, Void, Void> apiLoadDashboardSubscribedUsersAsyncTask;
    AsyncTask<Void, Void, Void> apiLoadDashboardSubscribedLibrariesAsyncTask;
    AsyncTask<MainViewHolder, Bitmap, View> mainViewUsersAsyncTask;
    LayoutInflater layoutInflater;
    public static final int NUMBER_OF_LIBRARIES = 3;
    Date start;
    Date stop;
    public DisplayImageOptions defaultOptions;
    DisplayImageOptions defaultOptions2;
    public ImageLoaderConfiguration config;
    User user;
    Window window;
    float mScreenHeight;
    float mScreenWidth;
    float playerHeight = 0.0f;
    Menu menu;
    Player player;
    DisplayMetrics displayMetrics;
    Integer usersSize;
    Integer librariesSize;
    Integer currentDrawerTitle = 1;
    ListAdapter exploreLibraryIndividualsWithYapsLazyAdapter;
    ListAdapter subscribedLibraryIndividualsWithYapsLazyAdapter;
    String drawer_titles[] = {"Subscribed", "Explore", "Yap", "Logout"};
    int drawer_icons[] = {R.drawable.ic_subscribed_black, R.drawable.ic_explore_black, R.drawable.ic_yap_black, R.drawable.ic_logout_black};
    Toolbar toolbar;
    MainActivityDrawerRecyclerViewAdapter navigationDrawerRecyclerViewAdapter;
    RecyclerView.LayoutManager navigationDrawerRecyclerViewManager;
    RecyclerItemClickListener mainActivityDrawerRecyclerItemClickListener;
    MainActivityDrawerListener mainActivityDrawerListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // inside your activity (if you did not enable transitions in your
        // theme)
        window = getWindow();
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        // set an exit transition
        window.setAllowEnterTransitionOverlap(true);
        window.setAllowReturnTransitionOverlap(true);
        window.setSharedElementExitTransition(new Explode());
        window.setSharedElementEnterTransition(new Explode());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        // Hide the action bar title
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color
                .parseColor("#ffffff")));
        actionBar.setElevation(1.0f);
        actionBar.setTitle("Subscribed");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        displayMetrics = this.getResources().getDisplayMetrics();

        mScreenHeight = displayMetrics.heightPixels / displayMetrics.density;
        mScreenWidth = displayMetrics.widthPixels / displayMetrics.density;
        System.out.println("Display Metrics = height : " + mScreenHeight
                + "   width  : " + mScreenWidth);
        intent = getIntent();
        intentBundle = getIntent().getExtras();
        view = this.findViewById(android.R.id.content);
        user = intentBundle.getParcelable("user");
        activity = this;
        fallback = getDrawable(R.drawable.default1);
        fallback2 = getDrawable(R.drawable.default1);
        // UNIVERSAL IMAGE LOADER SETUP
        defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .showImageForEmptyUri(fallback)
                .showImageOnFail(fallback)
                .showImageOnLoading(fallback)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        defaultOptions2 = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .showImageForEmptyUri(fallback2)
                .showImageOnFail(fallback2)
                .showImageOnLoading(fallback2)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        config = new ImageLoaderConfiguration.Builder(
                activity.getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);

        imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(fallback)
                .showImageOnFail(fallback)
                .showImageOnLoading(fallback).build();
        // END - UNIVERSAL IMAGE LOADER SETUP
        apiLoadDashboardSubscribedUsersAsyncTask = new APILoadDashboardSubscribedUsersAsyncTask();
        apiLoadDashboardSubscribedUsersAsyncTask
                .execute();
        if (apiLoadDashboardSubscribedUsersAsyncTask != null) {
            if (apiLoadDashboardSubscribedUsersAsyncTask.getStatus().equals(AsyncTask.Status.FINISHED)) {
                mainViewHolder = new MainViewHolder();
                mainViewUsersAsyncTask = new MainViewUsersAsyncTask(activity, view);
                mainViewUsersAsyncTask.execute(mainViewHolder);
            }
        }
        mainViewHolder = new MainViewHolder();
        mainViewUsersAsyncTask = new MainViewUsersAsyncTask(activity, view);
        mainViewUsersAsyncTask.execute(mainViewHolder);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_menu, menu);
//
//        // Associate searchable configuration with the SearchView
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        android.support.v7.widget.SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
//                .getActionView();
//        searchView.setSearchableInfo(searchManager
//                .getSearchableInfo(getComponentName()));

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);

        android.support.v7.widget.SearchView searchView = null;
        if (searchItem != null) {
            searchView = (android.support.v7.widget.SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));
        }


//        MenuItem searchViewMenuItem  = menu.findItem(R.id.action_search);
//        int searchImgId = getResources().getIdentifier("android:id/search_button", null, null);
//        ImageView v = (ImageView) searchView.findViewById(searchImgId);
//        v.setImageDrawable(getResources().getDrawable(R.drawable.ic_search_yapster_green));
//        SubMenu moreSubMenu = menu.addSubMenu(searchViewMenuItem.getGroupId(), 2, 1, "More").setIcon(getResources().getDrawable(R.drawable.ic_action_more_green));
//        moreSubMenu.add(0, 1, 1, "Yap").setIcon(getResources().getDrawable(R.drawable.ic_microphone_yapster_green));
//        BitmapDrawable profilePictureDrawable = null;
//        try {
//            profilePictureDrawable = new DownloadImageAsyncTask(user.profilePictureURL).execute().get();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//        if (profilePictureDrawable == null) {
//            moreSubMenu.add(0, 0, 0, user.getFullName()).setIcon(getResources().getDrawable(R.drawable.default1));
//        } else {
//            moreSubMenu.add(0, 0, 0, user.getFullName()).setIcon(profilePictureDrawable);
//
//        }
        return super.onCreateOptionsMenu(menu);
    }

//        MenuItem action_user_profile = menu.findItem(R.id.action_user_profile);
//        LayoutInflater actionBarUserProfileViewInflator = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View actionBarUserProfileView = actionBarUserProfileViewInflator.inflate(R.layout.action_bar_user_view, null);
//        TextView textViewDrawerUserFullName = (TextView) actionBarUserProfileView.findViewById(R.id.textViewDrawerUserFullName);
//        TextView textViewDrawerUserUsername = (TextView) actionBarUserProfileView.findViewById(R.id.textViewDrawerUserUsername);
//        ImageView imageViewUserProfileImage = (ImageView) actionBarUserProfileView.findViewById(R.id.imageViewUserProfileImage);
//        textViewDrawerUserFullName.setText(user.getFullName());
//        textViewDrawerUserUsername.setText(user.username);
//        imageLoader.displayImage(user.profilePictureURL.toString(), imageViewUserProfileImage, defaultOptions);
//        action_user_profile.setActionView(actionBarUserProfileView);

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mainActivityDrawerListener.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.action_search:
                onSearchRequested();
                return true;

            case R.id.action_report_bug:
                return true;

            case R.id.action_help:
                return true;
//                if (popupMenu == null) {
//                    View menuItemView = findViewById(R.id.menu_overflow); // SAME ID AS MENU ID
//                    popupMenu = new PopupMenu(this, menuItemView);
//                    popupMenu.inflate(R.menu.activity_main_popover_menu_overflow);
//                    MenuItem action_user_profile = popupMenu.getMenu().findItem(R.id.action_user_profile);
//                    action_user_profile.setTitle(user.getFullName());
//                    if (user.profilePictureURL != null) {
//                        BitmapDrawable profilePictureDrawable = null;
//                        try {
//                            profilePictureDrawable = new DownloadImageAsyncTask(user.profilePictureURL).execute().get();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        } catch (ExecutionException e) {
//                            e.printStackTrace();
//                        }
//                        if (profilePictureDrawable == null) {
//                            action_user_profile.setIcon(getResources().getDrawable(R.drawable.default1));
//                        } else {
//                            action_user_profile.setIcon(profilePictureDrawable);
//
//                        }
//
//                    } else {
//                        action_user_profile.setIcon(getResources().getDrawable(R.drawable.default1));
//                    }
//
//                    try {
//                        Field[] fields = popupMenu.getClass().getDeclaredFields();
//                        for (Field field : fields) {
//                            if ("mPopup".equals(field.getName())) {
//                                field.setAccessible(true);
//                                Object menuPopupHelper = field.get(popupMenu);
//                                Class<?> classPopupHelper = Class.forName(menuPopupHelper
//                                        .getClass().getName());
//                                Method setForceIcons = classPopupHelper.getMethod(
//                                        "setForceShowIcon", boolean.class);
//                                setForceIcons.invoke(menuPopupHelper, true);
//                                break;
//                            }
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                        @Override
//                        public boolean onMenuItemClick(MenuItem item2) {
//                            switch (item2.getItemId()) {
//                                case R.id.action_user_profile:
//                                    MenuItem action_user_profile = popupMenu.getMenu().findItem(R.id.action_user_profile);
//                                    Drawable action_user_profile_drawable = action_user_profile.getIcon();
//                                    Rect action_user_profile_drawable_bounds = action_user_profile_drawable.getBounds();
//                                    Intent intent = new Intent(activity,
//                                            ProfileScreenActivity.class);
//                                    int[] screenLocation = new int[2];
//                                    int orientation = getResources().getConfiguration().orientation;
//
//                                    intent.putExtra("user", user);
//                                    intent.putExtra("profile_user", user);
//                                    intent.putExtra("orientation", orientation);
//                                    intent.putExtra("left", displayMetrics.widthPixels);
//                                    intent.putExtra("top", action_user_profile_drawable_bounds.top);
//                                    intent.putExtra("width", action_user_profile_drawable_bounds.width());
//                                    intent.putExtra("height", action_user_profile_drawable_bounds.height());
//
//                                    ActivityOptions options = ActivityOptions
//                                            .makeSceneTransitionAnimation(
//                                                    activity);
//                                    startActivity(intent, options.toBundle());
//                                    overridePendingTransition(0, 0);
//                                    return true;
//                                case R.id.action_yap:
//                                    Intent openMainActivity = new Intent("co.yapster.yapster.YAPSCREEN1ACTIVITY");
//                                    openMainActivity.putExtra("user", user);
//                                    openMainActivity.putExtra("animationType","circular reveal");
//                                    startActivity(openMainActivity);
//                                    overridePendingTransition(0, 0);
//                                default:
//                                    System.out.println("Testing");
//                            }
//                            return false;
//                        }
//                    });
//                }
//                popupMenu.show();
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mainActivityDrawerListener.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStart() {
        super.onStart();
        AWS.getInstance(activity);
    }

    @Override
    public void startActivity(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            intent.putExtra("user", user);
            intent.putExtra("origin_screen", "dashboard_subscribed");
        }
        super.startActivity(intent);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mainActivityDrawerListener != null) {
            mainActivityDrawerListener.syncState();
        }
    }

    /**
     * On selecting action bar icons
     */

    public void setupPlayerScreen() {
        actionBar.setDisplayShowTitleEnabled(false);
        menu.getItem(0).setVisible(false);
        menu.getItem(1).setVisible(false);
        mainViewHolder.textViewPlayerYapTitle.setVisibility(View.INVISIBLE);
        mainViewHolder.textViewPlayerYapUserName.setVisibility(View.INVISIBLE);
        mainViewHolder.imageViewPlayerYapImage.setVisibility(View.INVISIBLE);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_action_bar_gradient));
        actionBar.setElevation(0.0f);
        mainViewHolder.relativeLayoutForPlayerScreen.setVisibility(View.VISIBLE);
    }

    public void setupMainViewScreen() {
        actionBar.setDisplayShowTitleEnabled(true);
        menu.getItem(0).setVisible(true);
        menu.getItem(1).setVisible(true);
        mainViewHolder.textViewPlayerYapTitle.setVisibility(View.VISIBLE);
        mainViewHolder.textViewPlayerYapUserName.setVisibility(View.VISIBLE);
        mainViewHolder.imageViewPlayerYapImage.setVisibility(View.VISIBLE);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color
                .parseColor("#ffffff")));
        actionBar.setElevation(1.0f);
        mainViewHolder.relativeLayoutForPlayerScreen.setVisibility(View.INVISIBLE);
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

    public void storeImage(Bitmap image, String imageName, Boolean screenReloaded) throws IOException {
        String TAG = "storeImage";
        File outputDir = getCacheDir();
        File outputFile = new File(outputDir, imageName + ".png");
        if (screenReloaded == true) {
            File[] a = getCacheDir().listFiles();
            String[] c = getCacheDir().list();
            for (File b : a) {
                System.out.println(b.getName());
                b.delete();
            }
        }

        if (outputFile.exists() == false) {
            try {
                FileOutputStream fos = new FileOutputStream(outputFile);
                image.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.close();
                System.out.println("File has been saved");
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    }

    class MainActivityDrawerListener extends ActionBarDrawerToggle {

        public MainActivityDrawerListener(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
            super(activity, drawerLayout, toolbar, openDrawerContentDescRes, closeDrawerContentDescRes);
        }

        public MainActivityDrawerListener(Activity activity, DrawerLayout drawerLayout, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
            super(activity, drawerLayout, openDrawerContentDescRes, closeDrawerContentDescRes);
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);

            navigationDrawerRecyclerViewAdapter.drawerIsOpen();

        }

        @Override
        public void onDrawerClosed(View drawerView) {
            super.onDrawerClosed(drawerView);

            navigationDrawerRecyclerViewAdapter.drawerIsClosed();

        }

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            super.onDrawerSlide(drawerView, slideOffset);
        }
    }


//    TextView textViewSelected = (TextView) view;
//
//    String optionSelected = textViewSelected.getText().toString();
//    if (currentOptionSelected.equals(optionSelected)) {
//
//    } else {
//        currentOptionSelected = optionSelected;
//        actionBar.setTitle(optionSelected);
//        if (optionSelected.equals("Subscribed")) {
//            setupDashboardSubscribed();
//        } else if (optionSelected.equals("Explore")) {
//            setupDashboardExplore();
//        } else if (optionSelected.equals("Yap")) {
//
//            Intent openMainActivity = new Intent("co.yapster.yapster.YAPSCREEN1ACTIVITY");
//            openMainActivity.putExtra("user", user);
//            openMainActivity.putExtra("animationType", "circular reveal");
//            startActivity(openMainActivity);
//            overridePendingTransition(0, 0);
//
//
//        } else if (optionSelected.equals("Logout")) {
//            mainViewHolder.drawerLayout.closeDrawer(mainViewHolder.recyclerView);
//            prefs = activity.getSharedPreferences(
//                    PREFS_KEY, Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = prefs.edit();
//            editor.remove(USER_ID_KEY).commit();
//            editor.remove(SESSION_ID_KEY).commit();
//            editor.remove(DEVICE_TYPE_KEY).commit();
//            editor.remove(IDENTIFIER_KEY).commit();
//
//            Intent openMainActivity = new Intent(activity, SplashSignInAndSignUpScreen.class);
//            startActivity(openMainActivity);
//            overridePendingTransition(0, 0);
//            finish();
//        }
//
//
//    }
//
//
//}


//
//
//
//    class MainActivityDrawerItemClickListener implements ListView.OnItemClickListener {
//
//        @Override
//        public void onItemClick(AdapterView parent, View view, int position, long id) {
//            selectItem(position);
//        }
//    }
//
//    /**
//     * Swaps fragments in the main content view
//     */
//    private void selectItem(int position) {
//        // Create a new fragment and specify the planet to show based on position
//
//        // Highlight the selected item, update the title, and close the drawer
////        mainViewHolder.listViewDrawerNavigationOptions.setItemChecked(position, true);
////        mainViewHolder.drawerLayout.closeDrawer(mainViewHolder.listViewDrawerNavigationOptions);
//        String optionSelected = mDrawerListStringArray[position - 1];
//        if (currentOptionSelected.equals(optionSelected)) {
//
//        } else {
//            currentOptionSelected = optionSelected;
//            actionBar.setTitle(optionSelected);
//            if (optionSelected.equals("Subscribed")) {
//                setupDashboardSubscribed();
//            } else if (optionSelected.equals("Explore")) {
//                setupDashboardExplore();
//            } else if (optionSelected.equals("Yap")) {
//
//                Intent openMainActivity = new Intent("co.yapster.yapster.YAPSCREEN1ACTIVITY");
//                openMainActivity.putExtra("user", user);
//                openMainActivity.putExtra("animationType", "circular reveal");
//                startActivity(openMainActivity);
//                overridePendingTransition(0, 0);
//
//
//            } else if (optionSelected.equals("Logout")) {
//                mainViewHolder.drawerLayout.closeDrawer(mainViewHolder.recyclerView);
//                prefs = this.getSharedPreferences(
//                        PREFS_KEY, Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = prefs.edit();
//                editor.remove(USER_ID_KEY).commit();
//                editor.remove(SESSION_ID_KEY).commit();
//                editor.remove(DEVICE_TYPE_KEY).commit();
//                editor.remove(IDENTIFIER_KEY).commit();
//
//                Intent openMainActivity = new Intent(this, SplashSignInAndSignUpScreen.class);
//                startActivity(openMainActivity);
//                overridePendingTransition(0, 0);
//                finish();
//            }
//
//
//        }
//    }


    @Override
    public void setTitle(CharSequence title) {
        getActionBar().setTitle(title);
    }

    public void setupDashboardExplore() {

        mainViewHolder.textViewCardTitleUsers.setText(USERS_CARD_TITLE_EXPLORE);
        mainViewHolder.textViewCardTitleLibraries.setText(LIBRARIES_CARD_TITLE_EXPLORE);

        if (exploreUsersList != null) {


        } else {

            try {
                new APILoadDashboardExploreUsersAsyncTask()
                        .execute().get();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        User user1 = exploreUsersList.get(0);
        User user2 = exploreUsersList.get(1);
        User user3 = exploreUsersList.get(2);

        mainViewHolder.textViewUserName1.setText(user1.getFullName());
        mainViewHolder.textViewUserName2.setText(user2.getFullName());
        mainViewHolder.textViewUserName3.setText(user3.getFullName());

        if (user1.profilePictureURL != null)
            imageLoader.displayImage(user1.profilePictureURL.toString(), mainViewHolder.imageViewUserProfilePicture1, defaultOptions);
        else {
            mainViewHolder.imageViewUserProfilePicture1.setImageDrawable(getResources().getDrawable(R.drawable.default1));
        }
        if (user2.profilePictureURL != null)
            imageLoader.displayImage(user2.profilePictureURL.toString(), mainViewHolder.imageViewUserProfilePicture2, defaultOptions);
        else {
            mainViewHolder.imageViewUserProfilePicture1.setImageDrawable(getResources().getDrawable(R.drawable.default1));
        }
        if (user3.profilePictureURL != null)
            imageLoader.displayImage(user3.profilePictureURL.toString(), mainViewHolder.imageViewUserProfilePicture3, defaultOptions);
        else {
            mainViewHolder.imageViewUserProfilePicture1.setImageDrawable(getResources().getDrawable(R.drawable.default1));
        }


        if (exploreLibrariesList != null) {


        } else {

            try {
                new APILoadDashboardExploreLibrariesAsyncTask(activity, view)
                        .execute().get();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        Integer exploreLibrariesSize = exploreLibrariesList.size();
        if (exploreLibrariesSize == 0 || exploreLibrariesSize == null) {


        } else {
            mainViewHolder.linearLayoutForListOfLibraries.removeAllViews();
            for (Integer i = 0; i < NUMBER_OF_LIBRARIES; i++) {
                View libraryView = exploreLibraryIndividualsWithYapsLazyAdapter.getView(i, null, null);
                libraryView.setId(i);
                mainViewHolder.linearLayoutForListOfLibraries.addView(libraryView, i);
            }
            mainViewHolder.buttonViewAllLibraries.setClickable(true);

        }


    }

    public void setupDashboardSubscribed() {

        mainViewHolder.textViewCardTitleUsers.setText(USERS_CARD_TITLE_SUBSCRIBED);
        mainViewHolder.textViewCardTitleLibraries.setText(LIBRARIES_CARD_TITLE_SUBSCRIBED);

        if (subscribedUsersList != null) {


        } else {

            try {
                new APILoadDashboardSubscribedUsersAsyncTask()
                        .execute().get();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        User user1 = subscribedUsersList.get(0);
        User user2 = subscribedUsersList.get(1);
        User user3 = subscribedUsersList.get(2);

        mainViewHolder.textViewUserName1.setText(user1.getFullName());
        mainViewHolder.textViewUserName2.setText(user2.getFullName());
        mainViewHolder.textViewUserName3.setText(user3.getFullName());

        if (user1.profilePictureURL != null)
            imageLoader.displayImage(user1.profilePictureURL.toString(), mainViewHolder.imageViewUserProfilePicture1, defaultOptions);
        else {
            mainViewHolder.imageViewUserProfilePicture1.setImageDrawable(getResources().getDrawable(R.drawable.default1));
        }
        if (user2.profilePictureURL != null)
            imageLoader.displayImage(user2.profilePictureURL.toString(), mainViewHolder.imageViewUserProfilePicture2, defaultOptions);
        else {
            mainViewHolder.imageViewUserProfilePicture1.setImageDrawable(getResources().getDrawable(R.drawable.default1));
        }
        if (user3.profilePictureURL != null)
            imageLoader.displayImage(user3.profilePictureURL.toString(), mainViewHolder.imageViewUserProfilePicture3, defaultOptions);
        else {
            mainViewHolder.imageViewUserProfilePicture1.setImageDrawable(getResources().getDrawable(R.drawable.default1));
        }


        if (subscribedLibrariesList != null) {


        } else {

            try {
                new APILoadDashboardSubscribedLibrariesAsyncTask(activity, view)
                        .execute().get();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        Integer subscribedLibrariesSize = subscribedLibrariesList.size();
        if (subscribedLibrariesSize == 0 || subscribedLibrariesSize == null) {


        } else {
            mainViewHolder.linearLayoutForListOfLibraries.removeAllViews();
            for (Integer i = 0; i < NUMBER_OF_LIBRARIES; i++) {
                View libraryView = subscribedLibraryIndividualsWithYapsLazyAdapter.getView(i, null, null);
                libraryView.setId(i);
                mainViewHolder.linearLayoutForListOfLibraries.addView(libraryView, i);
            }
            mainViewHolder.buttonViewAllLibraries.setClickable(true);

        }


    }

    class MainViewUsersAsyncTask extends
            AsyncTask<MainViewHolder, Bitmap, View> {
        private MainViewHolder mainViewHolder;
        private Activity asyncActivity;
        private View asyncView;
        User user1;
        User user2;
        User user3;

        MainViewUsersAsyncTask(Activity activity, View view) {
            asyncView = view;
            asyncActivity = activity;
        }

        @Override
        protected View doInBackground(MainViewHolder... params) {
            layoutInflater = LayoutInflater.from(asyncActivity);
            LinearLayout linearLayoutForUsersGrid = (LinearLayout) asyncView
                    .findViewById(R.id.linearLayoutForUsersGrid);
            linearLayoutForUsersGrid.setElevation(3.0f);
            linearLayoutForUsersGrid.setTranslationZ(2.0f);
            LinearLayout linearLayoutForUsers = (LinearLayout) asyncView
                    .findViewById(R.id.linearLayoutForUsers);
            linearLayoutForUsers.setElevation(3.0f);
            linearLayoutForUsers.setTranslationZ(2.0f);
            mainViewHolder = params[0];
            mainViewHolder.slidingLayout = (SlidingUpPanelLayout) findViewById(R.id.slidingLayout);
            mainViewHolder.slidingLayout
                    .setEnableDragViewTouchEvents(false);
            mainViewHolder.slidingLayout
                    .setPanelSlideListener((PanelSlideListener) asyncActivity);
//            mainViewHolder.mSlidingUpPanelLayout.setPanelHeight(0);
            mainViewHolder.slidingLayout.setSlidingEnabled(false);
            mainViewHolder.relativeLayoutForScreen = (RelativeLayout) asyncView.findViewById(R.id.relativeLayoutForScreen);
            mainViewHolder.textViewPlayerYapTitle = (TextView) asyncView
                    .findViewById(R.id.textViewPlayerYapTitle);
            mainViewHolder.textViewPlayerYapUserName = (TextView) asyncView
                    .findViewById(R.id.textViewPlayerYapUserName);
            mainViewHolder.imageViewPlayerYapImage = (ImageView) asyncView
                    .findViewById(R.id.imageViewPlayerYapImage);
            mainViewHolder.textViewUserName1 = (TextView) asyncView
                    .findViewById(R.id.textForSquareImageButtonUser1);
            mainViewHolder.textViewUserName2 = (TextView) asyncView
                    .findViewById(R.id.textForSquareImageButtonUser2);
            mainViewHolder.textViewUserName3 = (TextView) asyncView
                    .findViewById(R.id.textForSquareImageButtonUser3);
            mainViewHolder.linearLayoutUserProfilePicture1 = (LinearLayout) asyncView
                    .findViewById(R.id.linearLayoutUserProfilePicture1);
            mainViewHolder.relativeLayoutForUserProfilePicture1 = (RelativeLayout) asyncView
                    .findViewById(R.id.relativeLayoutForUserProfilePicture1);
            mainViewHolder.relativeLayoutForUserProfilePicture2 = (RelativeLayout) asyncView
                    .findViewById(R.id.relativeLayoutUserProfilePicture2);
            mainViewHolder.relativeLayoutForUserProfilePicture3 = (RelativeLayout) asyncView
                    .findViewById(R.id.relativeLayoutUserProfilePicture3);
            mainViewHolder.textViewCardTitleUsers = (TextView) asyncView.findViewById(R.id.textViewCardTitleUsers);
            mainViewHolder.textViewCardTitleLibraries = (TextView) asyncView.findViewById(R.id.textViewCardTitleLibraries);
            mainViewHolder.linearLayoutLineInUsersTitle = (LinearLayout) asyncView.findViewById(R.id.linearLayoutLineInUsersTitle);
            mainViewHolder.relativeLayoutCardTitleLibraries = (RelativeLayout) asyncView.findViewById(R.id.relativeLayoutCardTitleLibraries);
            mainViewHolder.linearLayoutLineInLibrariesTitle = (LinearLayout) asyncView.findViewById(R.id.linearLayoutLineInLibrariesTitle);

            mainViewHolder.relativeLayoutForPlayerScreen = (RelativeLayout) asyncView.findViewById(R.id.relativeLayoutForPlayerScreen);
            mainViewHolder.linearLayoutForLibraries = (LinearLayout) asyncView.findViewById(R.id.linearLayoutForLibraries);
            mainViewHolder.linearLayoutForListOfLibraries = (LinearLayout) asyncView.findViewById(R.id.linearLayoutForListOfLibraries);
            mainViewHolder.linearLayoutFullPlayerPlayingYapInfo = (LinearLayout) asyncView.findViewById(R.id.linearLayoutFullPlayerPlayingYapInfo);
            mainViewHolder.imageViewFullPlayerYapImage = (ImageView) asyncView.findViewById(R.id.imageViewFullPlayerYapImage);
            mainViewHolder.textViewFullPlayerYapTitle = (TextView) asyncView.findViewById(R.id.textViewFullPlayerYapTitle);
            mainViewHolder.textViewFullPlayerYapUser = (TextView) asyncView.findViewById(R.id.textViewFullPlayerYapUser);
            mainViewHolder.textViewFullPlayerYapDescription = (TextView) asyncView.findViewById(R.id.textViewFullPlayerYapDescription);
            mainViewHolder.imageButtonDownArrowDismissFullPlayer = (ImageButton) asyncView.findViewById(R.id.imageButtonDownArrowDismissFullPlayer);
            mainViewHolder.linearLayoutPlayerControls = (LinearLayout) asyncView.findViewById(R.id.linearLayoutPlayerControls);
            mainViewHolder.imageButtonFullPlayerPrevious = (ImageButton) asyncView.findViewById(R.id.imageButtonFullPlayerPrevious);
            mainViewHolder.imageButtonFullPlayerPlayAndPause = (ImageButton) asyncView.findViewById(R.id.imageButtonFullPlayerPlayAndPause);
            mainViewHolder.imageButtonFullPlayerNext = (ImageButton) asyncView.findViewById(R.id.imageButtonFullPlayerNext);
            player = Player.getInstance(user);

/// /            mainViewHolder.headerViewOfDrawerListView = activity.getLayoutInflater().inflate(R.layout.activity_main_drawer_header_view, null);
//            mainViewHolder.relativeLayoutDrawerTop = (RelativeLayout) mainViewHolder.headerViewOfDrawerListView.findViewById(R.id.relativeLayoutDrawerTop);
//            mainViewHolder.imageViewDrawerUserProfilePicture = (ImageView) mainViewHolder.headerViewOfDrawerListView.findViewById(R.id.imageViewDrawerUserProfilePicture);
//            mainViewHolder.linearLayoutDrawerUserText = (LinearLayout) mainViewHolder.headerViewOfDrawerListView.findViewById(R.id.linearLayoutDrawerUserText);
//            mainViewHolder.textViewDrawerUserFullName = (TextView) mainViewHolder.headerViewOfDrawerListView.findViewById(R.id.textViewDrawerUserFullName);
//            mainViewHolder.textViewDrawerUserUsername = (TextView) mainViewHolder.headerViewOfDrawerListView.findViewById(R.id.textViewDrawerUserUsername);
//            mainViewHolder.listViewDrawerNavigationOptions = (ListView) asyncView.findViewById(R.id.listViewDrawerNavigationOptions);
            mainViewHolder.drawerLayout = (DrawerLayout) asyncView.findViewById(R.id.drawerLayout);
            mainViewHolder.recyclerView = (RecyclerView) asyncView.findViewById(R.id.recyclerView);
            navigationDrawerRecyclerViewManager = new LinearLayoutManager(activity);
            mainActivityDrawerListener = new MainActivityDrawerListener(activity, mainViewHolder.drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
            navigationDrawerRecyclerViewAdapter = new MainActivityDrawerRecyclerViewAdapter(activity, user, drawer_titles, drawer_icons, activity);

//            mainViewHolder.listViewDrawerNavigationOptions.addHeaderView(mainViewHolder.headerViewOfDrawerListView, null, true);


//            mainViewHolder.imageViewDrawerUserProfilePicture.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    mainViewHolder.imageViewDrawerUserProfilePicture
//                            .setTransitionName("userProfilePicture");
////                    mainViewHolder.linearLayoutUserProfilePicture1
////                            .setTransitionName("linearLayoutForUserProfilePicture");
//                    mainViewHolder.relativeLayoutDrawerTop
//                            .setTransitionName("relativeLayoutForUserProfilePicture");
//                    Intent intent = new Intent(asyncActivity,
//                            ProfileScreenActivity.class);
//                    int[] screenLocation = new int[2];
//                    mainViewHolder.imageViewDrawerUserProfilePicture.getLocationOnScreen(screenLocation);
//                    int orientation = getResources().getConfiguration().orientation;
//
//                    intent.putExtra("user", user);
//                    intent.putExtra("profile_user", user);
//                    intent.putExtra("orientation", orientation);
//                    intent.putExtra("left", screenLocation[0]);
//                    intent.putExtra("top", screenLocation[1]);
//                    intent.putExtra("width", mainViewHolder.imageViewDrawerUserProfilePicture.getWidth());
//                    intent.putExtra("height", mainViewHolder.imageViewDrawerUserProfilePicture.getHeight());
//
//                    ActivityOptions options = ActivityOptions
//                            .makeSceneTransitionAnimation(
//                                    asyncActivity,
//                                    Pair.create(
//                                            (View) mainViewHolder.imageViewDrawerUserProfilePicture,
//                                            "userProfilePicture"),
////                                    Pair.create(
////                                            (View) mainViewHolder.textViewUserName1,
////                                            "userName"),
////                                    Pair.create(
////                                            (View) mainViewHolder.linearLayoutUserProfilePicture1,
////                                            "linearLayoutForUserProfilePicture"),
//                                    Pair.create(
//                                            (View) mainViewHolder.relativeLayoutDrawerTop,
//                                            "relativeLayoutForUserProfilePicture"));
//                    startActivity(intent, options.toBundle());
//                    overridePendingTransition(0, 0);
//
//                }
//            });

            mainViewHolder.imageButtonDownArrowDismissFullPlayer.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("Down arrow clicked");
                    mainViewHolder.slidingLayout.collapsePanel();
                }
            });

            mainViewHolder.imageButtonFullPlayerPrevious.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    player.previousYap();
                }
            });

            mainViewHolder.imageButtonFullPlayerNext.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    player.nextYap();
                }
            });

            mainViewHolder.imageButtonFullPlayerPlayAndPause.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Boolean isPlaying = player.isPlaying();
                    if (isPlaying == true) {
                        mainViewHolder.imageButtonFullPlayerPlayAndPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_player_play_black));
                        player.pause();
                    } else {
                        mainViewHolder.imageButtonFullPlayerPlayAndPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_player_pause_black));
                        player.start();
                    }
                }
            });
            mainViewHolder.linearLayoutForLibraries.setElevation(4.0f);
            mainViewHolder.linearLayoutForLibraries.setTranslationZ(4.0f);
            usersSize = subscribedUsersList.size();
            if (usersSize == 0 || usersSize < 3) {

            } else {

                user1 = subscribedUsersList.get(0);
                user2 = subscribedUsersList.get(1);
                user3 = subscribedUsersList.get(2);

                mainViewHolder.stringUserProfileName1 = user1.getFullName();
                mainViewHolder.stringUserProfileName2 = user2.getFullName();
                mainViewHolder.stringUserProfileName3 = user3.getFullName();
                mainViewHolder.imageViewUserProfilePicture1 = (ImageView) asyncView
                        .findViewById(R.id.squareImageButtonUser1);
                mainViewHolder.imageViewUserProfilePicture2 = (ImageView) asyncView
                        .findViewById(R.id.squareImageButtonUser2);
                mainViewHolder.imageViewUserProfilePicture3 = (ImageView) asyncView
                        .findViewById(R.id.squareImageButtonUser3);

                onClickListenerToGoToProfileScreen1 = new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        User profileUser1 = subscribedUsersList.get(0);
                        mainViewHolder.imageViewUserProfilePicture1
                                .setTransitionName("userProfilePicture");
                        mainViewHolder.linearLayoutUserProfilePicture1
                                .setTransitionName("linearLayoutForUserProfilePicture");
                        mainViewHolder.relativeLayoutForUserProfilePicture1
                                .setTransitionName("relativeLayoutForUserProfilePicture");
                        Intent intent = new Intent(asyncActivity,
                                ProfileScreenActivity.class);
                        int[] screenLocation = new int[2];
                        v.getLocationOnScreen(screenLocation);
                        int orientation = getResources().getConfiguration().orientation;

                        intent.putExtra("user", user);
                        intent.putExtra("profile_user", profileUser1);
                        intent.putExtra("orientation", orientation);
                        intent.putExtra("left", screenLocation[0]);
                        intent.putExtra("top", screenLocation[1]);
                        intent.putExtra("width", v.getWidth());
                        intent.putExtra("height", v.getHeight());

                        ActivityOptions options = ActivityOptions
                                .makeSceneTransitionAnimation(
                                        asyncActivity,
                                        Pair.create(
                                                (View) mainViewHolder.imageViewUserProfilePicture1,
                                                "userProfilePicture"),
//                                    Pair.create(
//                                            (View) mainViewHolder.textViewUserName1,
//                                            "userName"),
                                        Pair.create(
                                                (View) mainViewHolder.linearLayoutUserProfilePicture1,
                                                "linearLayoutForUserProfilePicture"),
                                        Pair.create(
                                                (View) mainViewHolder.relativeLayoutForUserProfilePicture1,
                                                "relativeLayoutForUserProfilePicture"));
                        startActivity(intent, options.toBundle());
                        overridePendingTransition(0, 0);

                    }
                };

                mainViewHolder.linearLayoutUserProfilePicture1
                        .setOnClickListener(onClickListenerToGoToProfileScreen1);


                onClickListenerToGoToProfileScreen2 = new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        User profileUser2 = subscribedUsersList.get(1);
                        mainViewHolder.imageViewUserProfilePicture2
                                .setTransitionName("userProfilePicture");
                        mainViewHolder.relativeLayoutForUserProfilePicture2
                                .setTransitionName("relativeLayoutForUserProfilePicture");
                        Intent intent = new Intent(asyncActivity,
                                ProfileScreenActivity.class);
                        int[] screenLocation = new int[2];
                        v.getLocationOnScreen(screenLocation);
                        int orientation = getResources().getConfiguration().orientation;

                        intent.putExtra("user", user);
                        intent.putExtra("profile_user", profileUser2);
                        intent.putExtra("orientation", orientation);
                        intent.putExtra("left", screenLocation[0]);
                        intent.putExtra("top", screenLocation[1]);
                        intent.putExtra("width", v.getWidth());
                        intent.putExtra("height", v.getHeight());

                        ActivityOptions options = ActivityOptions
                                .makeSceneTransitionAnimation(
                                        asyncActivity,
                                        Pair.create(
                                                (View) mainViewHolder.imageViewUserProfilePicture2,
                                                "userProfilePicture"),
//                                    Pair.create(
//                                            (View) mainViewHolder.textViewUserName2,
//                                            "userName"),
                                        Pair.create(
                                                (View) mainViewHolder.relativeLayoutForUserProfilePicture2,
                                                "relativeLayoutForUserProfilePicture"));
                        startActivity(intent, options.toBundle());
                        overridePendingTransition(0, 0);

                    }
                };

                mainViewHolder.relativeLayoutForUserProfilePicture2
                        .setOnClickListener(onClickListenerToGoToProfileScreen2);

                onClickListenerToGoToProfileScreen3 = new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        User profileUser3 = subscribedUsersList.get(2);
                        mainViewHolder.imageViewUserProfilePicture3
                                .setTransitionName("userProfilePicture");
                        mainViewHolder.relativeLayoutForUserProfilePicture3
                                .setTransitionName("relativeLayoutForUserProfilePicture");
                        Intent intent = new Intent(asyncActivity,
                                ProfileScreenActivity.class);
                        int[] screenLocation = new int[2];
                        v.getLocationOnScreen(screenLocation);
                        int orientation = getResources().getConfiguration().orientation;

                        intent.putExtra("user", user);
                        intent.putExtra("profile_user", profileUser3);
                        intent.putExtra("orientation", orientation);
                        intent.putExtra("left", screenLocation[0]);
                        intent.putExtra("top", screenLocation[1]);
                        intent.putExtra("width", v.getWidth());
                        intent.putExtra("height", v.getHeight());

                        ActivityOptions options = ActivityOptions
                                .makeSceneTransitionAnimation(
                                        asyncActivity,
                                        Pair.create(
                                                (View) mainViewHolder.imageViewUserProfilePicture3,
                                                "userProfilePicture"),
//                                    Pair.create(
//                                            (View) mainViewHolder.textViewUserName3,
//                                            "userName"),
                                        Pair.create(
                                                (View) mainViewHolder.relativeLayoutForUserProfilePicture3,
                                                "relativeLayoutForUserProfilePicture"));
                        startActivity(intent, options.toBundle());
                        overridePendingTransition(0, 0);

                    }
                };

                mainViewHolder.relativeLayoutForUserProfilePicture3
                        .setOnClickListener(onClickListenerToGoToProfileScreen3);

                mainViewHolder.buttonViewAllUsers = (Button) asyncView
                        .findViewById(R.id.buttonViewAllUsers);

                View.OnClickListener buttonViewAllUsersOnClickListener = new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent openActivity1 = new Intent(
                                "co.yapster.yapster.MAINACTIVITYVIEWALLSUBSCRIBEDUSERS");
                        openActivity1.putExtra("user", user);
                        startActivity(openActivity1);
                    }
                };
                mainViewHolder.buttonViewAllUsers.setOnClickListener(buttonViewAllUsersOnClickListener);


            }

            mainViewHolder.buttonViewAllLibraries = (Button) asyncView.findViewById(R.id.buttonViewAllLibraries);
            View.OnClickListener buttonViewAllLibrariesOnClickListener = new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent openActivity1 = new Intent(
                            "co.yapster.yapster.MAINACTIVITYVIEWALLSUBSCRIBEDLIBRARIES");
                    openActivity1.putExtra("user", user);
                    startActivity(openActivity1);
                }
            };
            mainViewHolder.buttonViewAllLibraries.setOnClickListener(buttonViewAllLibrariesOnClickListener);
            mainViewHolder.buttonViewAllLibraries.setClickable(false);

            asyncView.setTag(mainViewHolder);
            return asyncView;
        }

        @Override
        protected void onPostExecute(View result) {
            super.onPostExecute(result);
            mainActivityDrawerRecyclerItemClickListener = new RecyclerItemClickListener(activity, new RecyclerItemClickListener.OnItemClickListener() {
                @Override public void onItemClick(View child, int position) {

                    mainViewHolder.drawerLayout.closeDrawers();
                    if (currentDrawerTitle == position) {

                    } else {
                        currentDrawerTitle = position;
                        actionBar.setTitle(drawer_titles[position]);
                        if (position == 0) {
                            child.setTransitionName("userProfilePicture");
//                    mainViewHolder.linearLayoutUserProfilePicture1
//                            .setTransitionName("linearLayoutForUserProfilePicture");
                            mainViewHolder.recyclerView
                                    .setTransitionName("relativeLayoutForUserProfilePicture");
                            Intent intent = new Intent(activity,
                                    ProfileScreenActivity.class);
                            int[] screenLocation = new int[2];
                            child.getLocationOnScreen(screenLocation);
                            int orientation = getResources().getConfiguration().orientation;

                            intent.putExtra("user", user);
                            intent.putExtra("profile_user", user);
                            intent.putExtra("orientation", orientation);
                            intent.putExtra("left", screenLocation[0]);
                            intent.putExtra("top", screenLocation[1]);
                            intent.putExtra("width", child.getWidth());
                            intent.putExtra("height", child.getHeight());

                            ActivityOptions options = ActivityOptions
                                    .makeSceneTransitionAnimation(
                                            activity,
                                            Pair.create(
                                                    (View) child,
                                                    "userProfilePicture"),
//                                    Pair.create(
//                                            (View) mainViewHolder.textViewUserName1,
//                                            "userName"),
//                                    Pair.create(
//                                            (View) mainViewHolder.linearLayoutUserProfilePicture1,
//                                            "linearLayoutForUserProfilePicture"),
                                            Pair.create(
                                                    (View) mainViewHolder.recyclerView,
                                                    "relativeLayoutForUserProfilePicture"));
                            startActivity(intent, options.toBundle());
                            overridePendingTransition(0, 0);

                        } else if (position == 1) {
                            setupDashboardSubscribed();
                        } else if (position == 2) {
                            setupDashboardExplore();
                        } else if (position == 3) {

                            Intent openMainActivity = new Intent("co.yapster.yapster.YAPSCREEN1ACTIVITY");
                            openMainActivity.putExtra("user", user);
                            openMainActivity.putExtra("animationType", "circular reveal");
                            startActivity(openMainActivity);
                            overridePendingTransition(0, 0);


                        } else if (position == 4) {
                            mainViewHolder.drawerLayout.closeDrawer(mainViewHolder.recyclerView);
                            prefs = activity.getSharedPreferences(
                                    PREFS_KEY, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.remove(USER_ID_KEY).commit();
                            editor.remove(SESSION_ID_KEY).commit();
                            editor.remove(DEVICE_TYPE_KEY).commit();
                            editor.remove(IDENTIFIER_KEY).commit();

                            Intent openMainActivity = new Intent(activity, SplashSignInAndSignUpScreen.class);
                            startActivity(openMainActivity);
                            overridePendingTransition(0, 0);
                            finish();
                        }


                    }
                }
            });
            mainViewHolder.relativeLayoutForPlayerScreen.setVisibility(View.INVISIBLE);
            // Set the adapter for the list view
            mainViewHolder.drawerLayout.setDrawerListener(mainActivityDrawerListener);
            mainActivityDrawerListener.syncState();
//            mainViewHolder.recyclerView.addOnItemTouchListener(mainActivityDrawerRecyclerItemClickListener);
            mainViewHolder.recyclerView.setAdapter(navigationDrawerRecyclerViewAdapter);
            mainViewHolder.recyclerView.setLayoutManager(navigationDrawerRecyclerViewManager);
            mainViewHolder.recyclerView.setFocusable(true);
            mainViewHolder.recyclerView.setFocusableInTouchMode(true);
            mainViewHolder.recyclerView.setClickable(true);
            if (mainActivityDrawerListener != null) {
                mainActivityDrawerListener.syncState();
            }

            if (usersSize == 0 || usersSize < 3) {

            } else {
                mainViewHolder.textViewUserName1
                        .setText(mainViewHolder.stringUserProfileName1);
                mainViewHolder.textViewUserName2
                        .setText(mainViewHolder.stringUserProfileName2);
                mainViewHolder.textViewUserName3
                        .setText(mainViewHolder.stringUserProfileName3);
                if (user1.profilePictureURL != null)
                    imageLoader.displayImage(user1.profilePictureURL.toString(), mainViewHolder.imageViewUserProfilePicture1, defaultOptions);
                if (user2.profilePictureURL != null)
                    imageLoader.displayImage(user2.profilePictureURL.toString(), mainViewHolder.imageViewUserProfilePicture2, defaultOptions);
                if (user3.profilePictureURL != null)
                    imageLoader.displayImage(user3.profilePictureURL.toString(), mainViewHolder.imageViewUserProfilePicture3, defaultOptions);
            }

            apiLoadDashboardSubscribedLibrariesAsyncTask = new APILoadDashboardSubscribedLibrariesAsyncTask(
                    asyncActivity, asyncView);
            apiLoadDashboardSubscribedLibrariesAsyncTask
                    .execute();


        }

    }

    class APILoadDashboardSubscribedUsersAsyncTask extends
            AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            subscribedUsersList = new ArrayList<User>();
            String api_url = "http://api.yapster.co/users/load/dashboard/subscribed/users/";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(api_url);
            String nameValuePairsJSONObjectString = null;
            try {
                JSONObject nameValuePairsJSONObject = new JSONObject();
                nameValuePairsJSONObject.put("user_id",
                        user.id);
                nameValuePairsJSONObject.put("session_id",
                        user.sessionID);

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
            apiDashboardSubscribedUsersJsonResponse = writer.toString();
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(apiDashboardSubscribedUsersJsonResponse);
            Boolean valid = jsonElement.getAsJsonObject().get("valid").getAsBoolean();
            if (valid == true) {
                JsonArray data = jsonElement.getAsJsonObject().get("data").getAsJsonArray();
                for (Integer i = 0; i < data.size(); i++) {
                    JsonObject user_json_object = data.get(i).getAsJsonObject();
                    User subscribed_user = new User(false, user_json_object);
                    subscribedUsersList.add(subscribed_user);
                }
            }

            return null;


        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }

    }

    class APILoadDashboardSubscribedLibrariesAsyncTask extends
            AsyncTask<Void, Void, Void> {
        private Activity asyncActivity;
        private View asyncView;
        private ListAdapter yapLazyAdapter;
        private ListAdapter libraryIndividualsWithYapsLazyAdapter;
        private View libraryView;

        APILoadDashboardSubscribedLibrariesAsyncTask(Activity activity, View view) {
            asyncView = view;
            asyncActivity = activity;
        }

        @Override
        protected Void doInBackground(Void... params) {
            subscribedLibrariesList = new ArrayList<Library>();
            String api_url = "http://api.yapster.co/users/load/dashboard/subscribed/libraries/";
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
            apiDashboardSubscribedLibrariesJsonResponse = writer.toString();
            JsonParser jsonParser = new JsonParser();
            apiDashboardSubscribedLibrariesJsonResponse = apiDashboardSubscribedLibrariesJsonResponse.trim();
            System.out.println(apiDashboardSubscribedLibrariesJsonResponse);
            JsonElement jsonElement = jsonParser.parse(apiDashboardSubscribedLibrariesJsonResponse);
            Boolean valid = jsonElement.getAsJsonObject().get("valid").getAsBoolean();
            if (valid == true) {
                JsonArray data = jsonElement.getAsJsonObject().get("data").getAsJsonArray();
                for (Integer i = 0; i < data.size(); i++) {
                    JsonObject library_json_object = data.get(i).getAsJsonObject();
                    Library subscribed_library = new Library(library_json_object, null);
                    subscribedLibrariesList.add(subscribed_library);
                }
                subscribedLibraryIndividualsWithYapsLazyAdapter = new LibraryIndividualsWithYapsLazyAdapter(activity, subscribedLibrariesList, user);
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Integer librariesSize = subscribedLibrariesList.size();
            if (librariesSize == 0 || librariesSize < 3) {

                if (usersSize == 0 || usersSize < 3) {
                    setupDashboardExplore();
                } else {
                    mainViewHolder.linearLayoutForLibraries.setVisibility(View.GONE);
                }


            } else {

                for (Integer i = 0; i < NUMBER_OF_LIBRARIES; i++) {
                    libraryView = subscribedLibraryIndividualsWithYapsLazyAdapter.getView(i, null, null);
                    libraryView.setId(i);
                    mainViewHolder.linearLayoutForListOfLibraries.addView(libraryView, i);
                }
                mainViewHolder.buttonViewAllLibraries.setClickable(true);

            }
        }

    }

    class APILoadDashboardExploreUsersAsyncTask extends
            AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {
            exploreUsersList = new ArrayList<User>();
            String api_url = "http://api.yapster.co/users/load/dashboard/explore/users/";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(api_url);
            String nameValuePairsJSONObjectString = null;
            try {
                JSONObject nameValuePairsJSONObject = new JSONObject();
                if (user == null) {

                } else {
                    nameValuePairsJSONObject.put("user_id",
                            user.id);
                    nameValuePairsJSONObject.put("session_id",
                            user.sessionID);
                }

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
            apiDashboardExploreUsersJsonResponse = writer.toString();
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(apiDashboardExploreUsersJsonResponse);
            Boolean valid = jsonElement.getAsJsonObject().get("valid").getAsBoolean();
            if (valid == true) {
                JsonArray data = jsonElement.getAsJsonObject().get("data").getAsJsonArray();
                for (Integer i = 0; i < data.size(); i++) {
                    JsonObject user_json_object = data.get(i).getAsJsonObject();
                    User explore_user = new User(false, user_json_object);
                    exploreUsersList.add(explore_user);
                }
            }

            return null;


        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


        }

    }

    class APILoadDashboardExploreLibrariesAsyncTask extends
            AsyncTask<Void, Void, Void> {
        private Activity asyncActivity;
        private View asyncView;
        private ListAdapter yapLazyAdapter;
        private ListAdapter libraryIndividualsWithYapsLazyAdapter;
        private View libraryView;

        APILoadDashboardExploreLibrariesAsyncTask(Activity activity, View view) {
            asyncView = view;
            asyncActivity = activity;
        }

        @Override
        protected Void doInBackground(Void... params) {
            exploreLibrariesList = new ArrayList<Library>();
            String api_url = "http://api.yapster.co/users/load/dashboard/explore/libraries/";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(api_url);
            // List<NameValuePair> nameValuePairs = null;
            String nameValuePairsJSONObjectString = null;
            try {
                JSONObject nameValuePairsJSONObject = new JSONObject();
                if (user == null) {

                } else {

                    nameValuePairsJSONObject.put("user_id",
                            user.id);
                    nameValuePairsJSONObject.put("session_id",
                            user.sessionID);

                }

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
            apiDashboardExploreLibrariesJsonResponse = writer.toString();
            JsonParser jsonParser = new JsonParser();
            apiDashboardExploreLibrariesJsonResponse = apiDashboardExploreLibrariesJsonResponse.trim();
            System.out.println(apiDashboardExploreLibrariesJsonResponse);
            JsonElement jsonElement = jsonParser.parse(apiDashboardExploreLibrariesJsonResponse);
            Boolean valid = jsonElement.getAsJsonObject().get("valid").getAsBoolean();
            if (valid == true) {
                JsonArray data = jsonElement.getAsJsonObject().get("data").getAsJsonArray();
                for (Integer i = 0; i < data.size(); i++) {
                    JsonObject library_json_object = data.get(i).getAsJsonObject();
                    Library subscribed_library = new Library(library_json_object, null);
                    exploreLibrariesList.add(subscribed_library);
                }
                exploreLibraryIndividualsWithYapsLazyAdapter = new LibraryIndividualsWithYapsLazyAdapter(activity, exploreLibrariesList, user);
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


        }

    }
}
