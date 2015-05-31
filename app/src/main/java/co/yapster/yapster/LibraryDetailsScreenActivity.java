package co.yapster.yapster;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.transition.Explode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutionException;

/**
 * Created by gurkarangulati on 2/21/15.
 */

class LibraryDetailsScreenViewHolder {

    ScrollView scrollViewLibraryDetailsScreen;
    RelativeLayout relativeLayoutForLibraryDetailsScreen;
    LinearLayout linearLayoutForLibraryYaps;
    RelativeLayout relativeLayoutForYapSectionTitle;
    TextView textViewLibraryYapTitle;
    TextView textViewUserYapsNumber;
    RelativeLayout relativeLayoutForLibraryYaps;
    RelativeLayout relativeLayoutForListViewLibraryYaps;
    ListView listViewLibraryYaps;
    ProgressBar progressBarForListViewlistViewLibraryYaps;
    ImageView imageViewLibraryPicture;
    LinearLayout linearLayoutForLibraryImage;
    RelativeLayout relativeLayoutLibraryInfo;
    TextView textViewLibraryName;
    RelativeLayout relativeLayoutSubscribersNumber;
    TextView textViewSubscribers;
    ImageButton imageButtonSubscribeToLibrary;
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

}


public class LibraryDetailsScreenActivity extends Activity implements
        SlidingUpPanelLayout.PanelSlideListener {

    private ActionBar actionBar;
    private Intent intent;
    private Bundle intentBundle;
    private static final TimeInterpolator sDecelerator = new DecelerateInterpolator();
    private static final TimeInterpolator sAccelerator = new AccelerateInterpolator();
    private static final int ANIM_DURATION = 500;

    ColorDrawable mBackground;
    int mLeftDelta;
    int mTopDelta;
    float mWidthScale;
    float mHeightScale;
    int thumbnailTop;
    int thumbnailLeft;
    int thumbnailWidth;
    int thumbnailHeight;
    float mScreenHeight;
    float mScreenWidth;
    private FrameLayout mTopLevelLayout;
    private int mOriginalOrientation;
    public LibraryDetailsScreenViewHolder libraryDetailsScreenViewHolder;
    String[] picture_urls = { "misc/drake1.jpg", "misc/twins1.jpg",
            "misc/twins8.jpg" };
    private YapDetailsLazyAdapter yapDetailsLazyAdapter;
    InfiniteListViewScrollListener libraryYapsInfiniteListViewScrollListener;
    private View view;
    private Bundle savedInstanceState;
    Activity activity;
    AsyncTask<Void, Void, Void> apiLibraryDetailsLoadYapsAsyncTask;

    public ImageLoader imageLoader;
    public DisplayImageOptions options;
    public Drawable fallback;
    Library library;
    User user;
    String apiLibraryLoadYapsJsonResponse;
    public static int NUMBER_OF_YAPS_LOADED_ON_EACH_PAGE = 3;
    Integer mPage;
    float playerHeight = 0.0f;
    Menu menu;
    Player player;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // inside your activity (if you did not enable transitions in your
        // theme)
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setAllowEnterTransitionOverlap(true);
        getWindow().setAllowReturnTransitionOverlap(true);
        getWindow().setSharedElementExitTransition(new Explode());
        getWindow().setSharedElementEnterTransition(new Explode());
        // getWindow().setBackgroundDrawable(
        // new ColorDrawable(Color.parseColor("#ffffff")));
        setContentView(R.layout.activity_library_details);
        // set an exit transition
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        mScreenHeight = displayMetrics.heightPixels / displayMetrics.density;
        mScreenWidth = displayMetrics.widthPixels / displayMetrics.density;
        System.out.println("Display Metrics = height : " + mScreenHeight
                + "   width  : " + mScreenWidth);
        // View decorView = getWindow().getDecorView();
        // int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        // decorView.setSystemUiVisibility(uiOptions);
        actionBar = getActionBar();
        // Hide the action bar title
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setElevation(1.0f);
        actionBar.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.shape_action_bar_gradient));
        intent = getIntent();
        intentBundle = getIntent().getExtras();
        libraryDetailsScreenViewHolder = new LibraryDetailsScreenViewHolder();
        activity = this;
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
            new LibraryDetailsViewAsyncTask()
                    .execute().get();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (savedInstanceState == null) {
            ViewTreeObserver observer = libraryDetailsScreenViewHolder.imageViewLibraryPicture
                    .getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    libraryDetailsScreenViewHolder.imageViewLibraryPicture
                            .getViewTreeObserver()
                            .removeOnPreDrawListener(this);

                    // Figure out where the thumbnail and full size versions
                    // are, relative
                    // to the screen and each other
                    int[] screenLocation = new int[2];
                    libraryDetailsScreenViewHolder.imageViewLibraryPicture
                            .getLocationOnScreen(screenLocation);
                    mLeftDelta = thumbnailLeft - screenLocation[0];
                    mTopDelta = thumbnailTop - screenLocation[1];

                    // Scale factors to make the large version the same size as
                    // the thumbnail
                    mWidthScale = (float) thumbnailWidth
                            / libraryDetailsScreenViewHolder.imageViewLibraryPicture
                            .getWidth();
                    mHeightScale = (float) thumbnailHeight
                            / libraryDetailsScreenViewHolder.imageViewLibraryPicture
                            .getHeight();

                    runEnterAnimation();
                    return true;
                }
            });
        }

    }

    public void runEnterAnimation() {
        final long duration = (long) (ANIM_DURATION * MainActivity.sAnimatorScale);

        // Set starting values for properties we're going to animate. These
        // values scale and position the full size version down to the thumbnail
        // size/location, from which we'll animate it back up
        libraryDetailsScreenViewHolder.imageViewLibraryPicture.setPivotX(0);
        libraryDetailsScreenViewHolder.imageViewLibraryPicture.setPivotY(0);
        libraryDetailsScreenViewHolder.imageViewLibraryPicture
                .setScaleX(mWidthScale);
        libraryDetailsScreenViewHolder.imageViewLibraryPicture
                .setScaleY(mHeightScale);
        libraryDetailsScreenViewHolder.imageViewLibraryPicture
                .setTranslationX(mLeftDelta);
        libraryDetailsScreenViewHolder.imageViewLibraryPicture
                .setTranslationY(mTopDelta);

        // We'll fade the text in later
        libraryDetailsScreenViewHolder.textViewLibraryName.setAlpha(0);
        libraryDetailsScreenViewHolder.relativeLayoutLibraryInfo.setAlpha(0);
        // profileScreenViewHolder.linearLayoutForRestOfScreen.setAlpha(0);
        libraryDetailsScreenViewHolder.imageButtonSubscribeToLibrary
                .setVisibility(View.INVISIBLE);
        mBackground.setAlpha(0);

        // Animate scale and translation to go from thumbnail to full size
        libraryDetailsScreenViewHolder.imageViewLibraryPicture.animate()
                .setDuration(duration).scaleX(1).scaleY(1).translationX(0)
                .translationY(0).setInterpolator(sDecelerator)
                .withEndAction(new Runnable() {
                    public void run() {
                        // Animate the description in after the image animation
                        // is done. Slide and fade the text in from underneath
                        // the picture.
                        // profileScreenViewHolder.relativeLayoutUserInfo
                        // .setTranslationY(-profileScreenViewHolder.relativeLayoutUserInfo
                        // .getHeight());
                        libraryDetailsScreenViewHolder.relativeLayoutLibraryInfo
                                .animate().setDuration(duration / 2)
                                .translationY(0).alpha(1)
                                .setInterpolator(sDecelerator);
                        libraryDetailsScreenViewHolder.textViewLibraryName.animate()
                                .setDuration(duration / 2).translationY(0)
                                .alpha(1).setInterpolator(sDecelerator);

                        libraryDetailsScreenViewHolder.imageButtonSubscribeToLibrary
                                .setScaleX(0.0f);
                        libraryDetailsScreenViewHolder.imageButtonSubscribeToLibrary
                                .setScaleY(0.0f);
                        libraryDetailsScreenViewHolder.imageButtonSubscribeToLibrary
                                .setVisibility(View.VISIBLE);
                        libraryDetailsScreenViewHolder.imageButtonSubscribeToLibrary
                                .animate().setDuration(duration / 2).scaleX(1)
                                .scaleY(1).translationX(0).translationY(0)
                                .setInterpolator(sDecelerator)
                                .withEndAction(new Runnable() {
                                    public void run() {
                                        player = Player.getInstance(user);
                                        Yap currentYap = player.getCurrentYap();
                                        if (currentYap != null){
                                            libraryDetailsScreenViewHolder.textViewPlayerYapTitle.setText(currentYap.title);
                                            libraryDetailsScreenViewHolder.textViewPlayerYapUserName.setText(currentYap.userUsername);
                                            imageLoader.displayImage(currentYap.picturePathURL.toString(), libraryDetailsScreenViewHolder.imageViewPlayerYapImage, options);                                            libraryDetailsScreenViewHolder.slidingLayout.setPanelHeight((int) mScreenHeight * 10 / 35);
                                            libraryDetailsScreenViewHolder.slidingLayout.setSlidingEnabled(true);
                                        }

                                        startLoadingScreen();
                                    }
                                });

                    }
                });

        // Fade in the black background
        ObjectAnimator bgAnim = ObjectAnimator.ofInt(mBackground, "alpha", 0,
                255);
        bgAnim.setDuration(duration);
        bgAnim.start();

    }

    public void runExitAnimation(final Runnable endAction) {

        if (apiLibraryDetailsLoadYapsAsyncTask != null) {
            if (apiLibraryDetailsLoadYapsAsyncTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
                apiLibraryDetailsLoadYapsAsyncTask.cancel(true);
            }
        }
        final long duration = (long) (ANIM_DURATION * MainActivity.sAnimatorScale);

        // First, slide/fade text out of the way
        libraryDetailsScreenViewHolder.relativeLayoutLibraryInfo.animate()
                .translationY(0).alpha(0).setDuration(duration / 4)
                .setInterpolator(sAccelerator).withEndAction(new Runnable() {
            public void run() {
                libraryDetailsScreenViewHolder.imageButtonSubscribeToLibrary
                        .animate().setDuration(duration / 4).scaleX(0)
                        .scaleY(0).translationX(0).translationY(0)
                        .alpha(0).withEndAction(new Runnable() {
                    public void run() {
                        ObjectAnimator bgAnim = ObjectAnimator
                                .ofInt(mBackground, "alpha", 0);
                        bgAnim.setDuration(duration);
                        bgAnim.start();

                        libraryDetailsScreenViewHolder.linearLayoutForLibraryYaps
                                .animate()
                                .setDuration(duration / 4)
                                .scaleX(0).translationX(0)
                                .translationY(0).alpha(0);

                        actionBar.hide();

                        // profileScreenViewHolder.linearLayoutForRestOfScreen
                        // .animate()
                        // .setDuration(duration / 4)
                        // .scaleX(0).translationX(0)
                        // .translationY(0).alpha(0)
                        // .withEndAction(new Runnable() {
                        // public void run() {
                        libraryDetailsScreenViewHolder.imageViewLibraryPicture
                                .animate()
                                .setDuration(duration)
                                .scaleX(mWidthScale)
                                .scaleY(mHeightScale)
                                .translationX(mLeftDelta)
                                .translationY(mTopDelta)
                                .withEndAction(endAction);

                    }
                });
            }
        });

    }

    public void startLoadingScreen() {

        apiLibraryDetailsLoadYapsAsyncTask = new APILibraryDetailsLoadYapsAsyncTask(1,NUMBER_OF_YAPS_LOADED_ON_EACH_PAGE,true);
        apiLibraryDetailsLoadYapsAsyncTask.execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
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
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.action_search:
                // search action
                return true;
            case android.R.id.home:
                runExitAnimation(new Runnable() {
                    public void run() {
                        // *Now* go ahead and exit the activity
                        finish();
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        runExitAnimation(new Runnable() {
            public void run() {
                // *Now* go ahead and exit the activity
                finish();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();

        // override transitions to skip the standard window animations
        overridePendingTransition(0, 0);
    }

    private Bitmap loadImageFromStorage(String path)
    { File[] a = getCacheDir().listFiles();
        for (File b:a){
        }
        File outputDir = getCacheDir();
        Bitmap b = null;
        try {
            File f=new File(outputDir.getPath() + File.separator + path + ".png");
            b = BitmapFactory.decodeStream(new FileInputStream(f));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return b;
    }

    public void setupPlayerScreen(){
        actionBar.setDisplayShowTitleEnabled(false);
        menu.getItem(0).setVisible(false);
        menu.getItem(1).setVisible(false);
        libraryDetailsScreenViewHolder.textViewPlayerYapTitle.setVisibility(View.INVISIBLE);
        libraryDetailsScreenViewHolder.textViewPlayerYapUserName.setVisibility(View.INVISIBLE);
        libraryDetailsScreenViewHolder.imageViewPlayerYapImage.setVisibility(View.INVISIBLE);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_action_bar_gradient));
        libraryDetailsScreenViewHolder.relativeLayoutForPlayerScreen.setVisibility(View.VISIBLE);
    }

    public void setupMainViewScreen(){
        actionBar.setDisplayShowTitleEnabled(true);
        menu.getItem(0).setVisible(true);
        menu.getItem(1).setVisible(true);
        libraryDetailsScreenViewHolder.textViewPlayerYapTitle.setVisibility(View.VISIBLE);
        libraryDetailsScreenViewHolder.textViewPlayerYapUserName.setVisibility(View.VISIBLE);
        libraryDetailsScreenViewHolder.imageViewPlayerYapImage.setVisibility(View.VISIBLE);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color
                .parseColor("#ffffff")));
        libraryDetailsScreenViewHolder.relativeLayoutForPlayerScreen.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        // TODO Auto-generated method stub
        if(playerHeight == 0.0f || slideOffset == 0.0f || slideOffset == 1.0f){
            playerHeight = slideOffset;
        }else{
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

        libraryDetailsScreenViewHolder.textViewPlayerYapTitle.animate()
                .setDuration(ANIM_DURATION / 10).translationY(0).alpha(1);
        libraryDetailsScreenViewHolder.textViewPlayerYapUserName.animate()
                .setDuration(ANIM_DURATION / 10).translationY(0).alpha(1);
        libraryDetailsScreenViewHolder.imageViewPlayerYapImage.animate()
                .setDuration(ANIM_DURATION / 10).translationY(0).alpha(1);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color
                .parseColor("#ffffff")));

    }

    @Override
    public void onPanelExpanded(View view) {

        libraryDetailsScreenViewHolder.textViewPlayerYapTitle.animate()
                .setDuration(ANIM_DURATION / 10).translationY(0).alpha(0);
        libraryDetailsScreenViewHolder.textViewPlayerYapUserName.animate()
                .setDuration(ANIM_DURATION / 10).translationY(0).alpha(0);
        libraryDetailsScreenViewHolder.imageViewPlayerYapImage.animate()
                .setDuration(ANIM_DURATION / 10).translationY(0).alpha(0);
        actionBar.setBackgroundDrawable(getResources()
                .getDrawable(
                        R.drawable.shape_action_bar_gradient));

    }

    @Override
    public void onPanelAnchored(View view) {

    }

    @Override
    public void onPanelHidden(View view) {

    }

    class LibraryDetailsViewAsyncTask extends
            AsyncTask<Void, Void, Void> {
        // private ProfileScreenViewHolder profileScreenViewHolder;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            user = intentBundle.getParcelable("user");
            library = intentBundle.getParcelable("library");
            thumbnailTop = intentBundle.getInt("top");
            thumbnailLeft = intentBundle.getInt("left");
            thumbnailWidth = intentBundle.getInt("width");
            thumbnailHeight = intentBundle.getInt("height");
            mOriginalOrientation = intentBundle.getInt("orientation");
            libraryDetailsScreenViewHolder.textViewLibraryName = (TextView) view
                    .findViewById(R.id.textViewLibraryName);
            libraryDetailsScreenViewHolder.relativeLayoutForLibraryYaps = (RelativeLayout) view
                    .findViewById(R.id.relativeLayoutForLibraryYaps);
            libraryDetailsScreenViewHolder.relativeLayoutForListViewLibraryYaps = (RelativeLayout) view
                    .findViewById(R.id.relativeLayoutForListViewLibraryYaps);
            libraryDetailsScreenViewHolder.slidingLayout = (SlidingUpPanelLayout) view.findViewById(R.id.slidingLayout);
            libraryDetailsScreenViewHolder.slidingContainer = (RelativeLayout) view.findViewById(R.id.slidingContainer);
            libraryDetailsScreenViewHolder.imageViewPlayerYapImage = (ImageView) view.findViewById(R.id.imageViewPlayerYapImage);
            libraryDetailsScreenViewHolder.textViewPlayerYapTitle = (TextView) view.findViewById(R.id.textViewPlayerYapTitle);
            libraryDetailsScreenViewHolder.textViewPlayerYapUserName = (TextView) view.findViewById(R.id.textViewPlayerYapUserName);

            libraryDetailsScreenViewHolder.relativeLayoutForPlayerScreen = (RelativeLayout) view.findViewById(R.id.relativeLayoutForPlayerScreen);
            libraryDetailsScreenViewHolder.imageViewFullPlayerYapImage = (ImageView) view.findViewById(R.id.imageViewFullPlayerYapImage);
            libraryDetailsScreenViewHolder.textViewFullPlayerYapTitle = (TextView) view.findViewById(R.id.textViewFullPlayerYapTitle);
            libraryDetailsScreenViewHolder.textViewFullPlayerYapUser = (TextView) view.findViewById(R.id.textViewFullPlayerYapUser);
            libraryDetailsScreenViewHolder.textViewFullPlayerYapDescription = (TextView) view.findViewById(R.id.textViewFullPlayerYapDescription);
            libraryDetailsScreenViewHolder.imageButtonDownArrowDismissFullPlayer = (ImageButton) view.findViewById(R.id.imageButtonDownArrowDismissFullPlayer);
            libraryDetailsScreenViewHolder.linearLayoutPlayerControls = (LinearLayout) view.findViewById(R.id.linearLayoutPlayerControls);
            libraryDetailsScreenViewHolder.imageButtonFullPlayerPrevious = (ImageButton) view.findViewById(R.id.imageButtonFullPlayerPrevious);
            libraryDetailsScreenViewHolder.imageButtonFullPlayerPlayAndPause = (ImageButton) view.findViewById(R.id.imageButtonFullPlayerPlayAndPause);
            libraryDetailsScreenViewHolder.imageButtonFullPlayerNext = (ImageButton) view.findViewById(R.id.imageButtonFullPlayerNext);
            player = Player.getInstance(user);


            libraryDetailsScreenViewHolder.imageButtonDownArrowDismissFullPlayer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("Down arrow clicked");
                    libraryDetailsScreenViewHolder.slidingLayout.collapsePanel();
                }
            });

            libraryDetailsScreenViewHolder.imageButtonFullPlayerPrevious.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    player.previousYap();
                }
            });

            libraryDetailsScreenViewHolder.imageButtonFullPlayerNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    player.nextYap();
                }
            });

            libraryDetailsScreenViewHolder.imageButtonFullPlayerPlayAndPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Boolean isPlaying = player.isPlaying();
                    if(isPlaying == true){
                        libraryDetailsScreenViewHolder.imageButtonFullPlayerPlayAndPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_player_play_black));
                        player.pause();
                    }else{
                        libraryDetailsScreenViewHolder.imageButtonFullPlayerPlayAndPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_player_pause_black));
                        player.start();
                    }
                }
            });


            libraryDetailsScreenViewHolder.slidingLayout
                    .setPanelSlideListener((SlidingUpPanelLayout.PanelSlideListener) activity);
            libraryDetailsScreenViewHolder.relativeLayoutForListViewLibraryYaps
                    .setElevation(4.0f);
            libraryDetailsScreenViewHolder.relativeLayoutForListViewLibraryYaps
                    .setTranslationZ(4.0f);
            libraryDetailsScreenViewHolder.relativeLayoutLibraryInfo = (RelativeLayout) view
                    .findViewById(R.id.relativeLayoutLibraryInfo);
            libraryDetailsScreenViewHolder.listViewLibraryYaps = (ListView) view
                    .findViewById(R.id.listViewLibraryYaps);
            libraryDetailsScreenViewHolder.imageButtonSubscribeToLibrary = (ImageButton) view
                    .findViewById(R.id.imageButtonSubscribeToLibrary);
            libraryDetailsScreenViewHolder.imageButtonSubscribeToLibrary
                    .setElevation(4.0f);
            libraryDetailsScreenViewHolder.imageButtonSubscribeToLibrary
                    .setTranslationZ(4.0f);
            ViewOutlineProvider imageButtonSubscribeToLibraryOutlineProvider = new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    int diameter = libraryDetailsScreenViewHolder.imageButtonSubscribeToLibrary.getHeight();
                    outline.setOval(0, 0, diameter, diameter);
                }
            };
            libraryDetailsScreenViewHolder.imageButtonSubscribeToLibrary.setOutlineProvider(imageButtonSubscribeToLibraryOutlineProvider);
            libraryDetailsScreenViewHolder.imageButtonSubscribeToLibrary.setClipToOutline(true);
            libraryDetailsScreenViewHolder.scrollViewLibraryDetailsScreen = (ScrollView) view
                    .findViewById(R.id.scrollViewLibraryDetailsScreen);
            libraryDetailsScreenViewHolder.relativeLayoutForLibraryDetailsScreen = (RelativeLayout) view
                    .findViewById(R.id.relativeLayoutForLibraryDetailsScreen);
            libraryDetailsScreenViewHolder.linearLayoutForLibraryYaps = (LinearLayout) view
                    .findViewById(R.id.linearLayoutForLibraryYaps);
            mBackground = new ColorDrawable(Color.WHITE);
            libraryDetailsScreenViewHolder.relativeLayoutForLibraryDetailsScreen
                    .setBackground(mBackground);
            libraryDetailsScreenViewHolder.imageViewLibraryPicture = (ImageView) view
                    .findViewById(R.id.imageViewLibraryPicture);
            libraryDetailsScreenViewHolder.textViewLibraryName
                    .setText(library.title);
            libraryDetailsScreenViewHolder.listViewLibraryYaps
                    .setVisibility(View.INVISIBLE);
            libraryDetailsScreenViewHolder.progressBarForListViewlistViewLibraryYaps = (ProgressBar) view.findViewById(R.id.progressBarForListViewlistViewLibraryYaps);
            yapDetailsLazyAdapter = new YapDetailsLazyAdapter(activity,user,library);
            view.setTag(libraryDetailsScreenViewHolder);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            libraryDetailsScreenViewHolder.textViewLibraryName.setAlpha(0);
            libraryDetailsScreenViewHolder.relativeLayoutLibraryInfo.setAlpha(0);
            libraryDetailsScreenViewHolder.relativeLayoutForPlayerScreen.setVisibility(View.INVISIBLE);
            if (library.picturePathURL != null & library.picturePathURL.toString().isEmpty() == false){
                imageLoader.displayImage(library.picturePathURL.toString(), libraryDetailsScreenViewHolder.imageViewLibraryPicture, options);
            }
            libraryDetailsScreenViewHolder.listViewLibraryYaps.setAdapter(yapDetailsLazyAdapter);
            libraryYapsInfiniteListViewScrollListener =  new InfiniteListViewScrollListener(NUMBER_OF_YAPS_LOADED_ON_EACH_PAGE) {
                @Override
                public void loadMore(int page, int totalItemsCount) {
                    apiLibraryDetailsLoadYapsAsyncTask = new APILibraryDetailsLoadYapsAsyncTask(page,NUMBER_OF_YAPS_LOADED_ON_EACH_PAGE,false);
                    apiLibraryDetailsLoadYapsAsyncTask
                            .execute();
                }
            };
            libraryDetailsScreenViewHolder.listViewLibraryYaps.setOnScrollListener(libraryYapsInfiniteListViewScrollListener);
        }

    }

    class APILibraryDetailsLoadYapsAsyncTask extends
            AsyncTask<Void, Void, Void> {

        Integer page;
        Integer amount;
        Boolean reload;

        APILibraryDetailsLoadYapsAsyncTask(Integer page, Integer amount, Boolean reload){
            this.page = page;
            this.amount = amount;
            this.reload = reload;
        }

        @Override
        protected void onPreExecute() {

            if (page == 1){
                libraryDetailsScreenViewHolder.progressBarForListViewlistViewLibraryYaps.setVisibility(View.VISIBLE);
            }
//            mainActivityViewAllSubscribedUsersViewHolder.listViewAllSubscribedUsers.addFooterView(mainActivityViewAllSubscribedUsersViewHolder.listViewAllSubscribedUsersFooterView);
        }

        @Override
        protected Void doInBackground(Void... params) {
            Integer yapDetailsLazyAdapterCountBeforeLoadMore =  yapDetailsLazyAdapter.getCount();
            String api_url = "http://api.yapster.co/yap/load/library/yaps/";
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
                nameValuePairsJSONObject.put("library_id",library.id);
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
            apiLibraryLoadYapsJsonResponse = writer.toString();
            System.out.println("This is the response : " + apiLibraryLoadYapsJsonResponse);
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(apiLibraryLoadYapsJsonResponse);
            Boolean valid = jsonElement.getAsJsonObject().get("valid").getAsBoolean();
             if (valid == true) {
                JsonElement data_json_element = jsonElement.getAsJsonObject().get("data");
                if (data_json_element.isJsonNull() == false) {
                    JsonArray data = jsonElement.getAsJsonObject().get("data").getAsJsonArray();
                    for (Integer i = 0; i < data.size(); i++) {
                        JsonObject yap_json_element = data.get(i).getAsJsonObject();
                        Yap library_yap = new Yap(yap_json_element,null);
                        library.yaps.add(i + yapDetailsLazyAdapterCountBeforeLoadMore, library_yap);
                    }
                    library.page = page;
                }else{
                    System.out.println("data_json_element : " + data_json_element.toString());
                }
            }
            Integer yapDetailsLazyAdapterCountAfterLoadMore =  yapDetailsLazyAdapter.getCount();
            if (yapDetailsLazyAdapterCountBeforeLoadMore == yapDetailsLazyAdapterCountAfterLoadMore){
                libraryYapsInfiniteListViewScrollListener.hasLoadedAll = true;
                library.hasLoadedAll = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            yapDetailsLazyAdapter.notifyDataSetChanged();
            if (libraryDetailsScreenViewHolder.listViewLibraryYaps.getVisibility() == View.INVISIBLE || libraryDetailsScreenViewHolder.listViewLibraryYaps.getVisibility() == View.GONE){
                libraryDetailsScreenViewHolder.progressBarForListViewlistViewLibraryYaps.setVisibility(View.GONE);
                libraryDetailsScreenViewHolder.listViewLibraryYaps.setVisibility(View.VISIBLE);
                libraryDetailsScreenViewHolder.relativeLayoutForListViewLibraryYaps
                        .setVisibility(View.VISIBLE);
                libraryDetailsScreenViewHolder.relativeLayoutForListViewLibraryYaps
                        .animate()
                        .setDuration(ANIM_DURATION)
                        .translationY(0).translationZ(4.0f);
            }
            if (libraryDetailsScreenViewHolder.progressBarForListViewlistViewLibraryYaps.getVisibility() == View.VISIBLE){
               libraryDetailsScreenViewHolder.progressBarForListViewlistViewLibraryYaps.setVisibility(View.GONE);
            }
//            mainActivityViewAllSubscribedUsersViewHolder.listViewAllSubscribedUsers.removeFooterView(mainActivityViewAllSubscribedUsersViewHolder.listViewAllSubscribedUsersFooterView);

        }

    }





}
