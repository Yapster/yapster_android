package co.yapster.yapster;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
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
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by gurkarangulati on 2/17/15.
 */

class YapScreen2ActivityViewHolder{
    LinearLayout linearLayoutForScreen;
    LinearLayout linearLayoutForEditText;
    EditText editTextYapTitle;
    EditText editTextYapDescription;
    LinearLayout linearLayoutForImageButtons;
    ImageButton imageButtonAlbums;
    ImageButton imageButtonYapPicture;
    LinearLayout linearLayoutForBottomLayout;
    RelativeLayout relativeLayoutForBottomLayout;
    ProgressBar progressBarForBottomLayout;
    ImageView imageViewBottomLayoutPicture;
    ListView listViewLibraries;
}

public class YapScreen2Activity extends Activity {
    Activity activity;
    View view;
    Drawable fallback;
    public DisplayImageOptions defaultOptions;
    public ImageLoaderConfiguration config;
    AsyncTask<Void, Void, Void> apiLoadDashboardSubscribedUsersAsyncTask;
    User user;
    Window window;
    Intent intent;
    Bundle intentBundle;
    private ActionBar actionBar;
    public ImageLoader imageLoader;
    ColorDrawable mBackground;
    ColorDrawable mWindowBackground;
    int mLeftDelta;
    int mTopDelta;
    float mWidthScale;
    float mHeightScale;
    int mOriginalOrientation;
    int thumbnailTop;
    int thumbnailLeft;
    int thumbnailWidth;
    int thumbnailHeight;
    float mScreenHeight;
    float mScreenWidth;
    YapScreen2ActivityViewHolder yapScreen2ActivityViewHolder;
    private static final int ANIM_DURATION = 500;
    private static final TimeInterpolator sDecelerator = new DecelerateInterpolator();
    private static final TimeInterpolator sAccelerator = new AccelerateInterpolator();
    private static final String LOG_TAG = "AudioRecordTest";
    private static String mFileName = null;
    private static String mFileName2 = null;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    boolean mStartRecording = true;
    boolean mStartPlaying = true;
    private long startTime = 0L;
    private Handler myHandler = new Handler();
    long timeInMillies = 0L;
    long timeSwap = 0L;
    long finalTime = 0L;
    int numberOfRecordings = 0;
    LibraryListLazyAdapter libraryListLazyAdapter;
    ArrayList<Library> user_libraries;
    String apiLoadUserLibrariesJsonResponse;
    AsyncTask<Void, Void, Void> apiLoadUserLibrariesAsyncTask;
    private static int NUMBER_OF_LIBRARIES_PER_PAGE = 7;
    InfiniteListViewScrollListener userLibrariesInfiniteListViewScrollListener;
    private static final int SELECT_PICTURE = 1;
    Bitmap yapPictureSelected;
    Bitmap yapPictureSelectedCropped;
    Uri yapPictureFileUri;
    Boolean imageTakenFlag;
    Boolean imageCapturedFlag;
    private static final int PICK_IMAGE = 1;
    AWS awsInstance;
    AmazonS3 amazonS3Client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // inside your activity (if you did not enable transitions in your
        // theme)
        window = getWindow();
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        // set an exit transition
        window.setAllowEnterTransitionOverlap(true);
        window.setAllowReturnTransitionOverlap(true);
        window.setSharedElementExitTransition(new Explode());
        window.setSharedElementEnterTransition(new Explode());
        setContentView(R.layout.activity_yap_screen_2);
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        System.out.println("Display Metrics = height : " + dpHeight
                + "   width  : " + dpWidth);
        System.out.println("Displayed Metrics");
        actionBar = getActionBar();
        // Hide the action bar title
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Yap");
        actionBar.setElevation(1.0f);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color
                .parseColor("#ffffff")));
        intent = getIntent();
        intentBundle = getIntent().getExtras();
        view = this.findViewById(android.R.id.content);
        activity = this;
        yapScreen2ActivityViewHolder = new YapScreen2ActivityViewHolder();
        fallback = getDrawable(R.drawable.default1);
        user = intentBundle.getParcelable("user");
        // UNIVERSAL IMAGE LOADER SETUP
        defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .showImageForEmptyUri(fallback)
                .showImageOnFail(fallback)
                .showImageOnLoading(fallback)
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
        try {
            new YapScreen2ViewAsyncTask()
                    .execute().get();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (savedInstanceState == null) {
            ViewTreeObserver observer = yapScreen2ActivityViewHolder.linearLayoutForScreen
                    .getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    yapScreen2ActivityViewHolder.linearLayoutForScreen
                            .getViewTreeObserver()
                            .removeOnPreDrawListener(this);

                    runEnterAnimation();
                    return true;
                }
            });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.yap_screen_2_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * On selecting action bar icons
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case android.R.id.home:
                runExitAnimationSlideBack(new Runnable() {
                    @Override
                    public void run() {
                        Intent openMainActivity = new Intent("co.yapster.yapster.YAPSCREEN1ACTIVITY");
                        openMainActivity.putExtra("user", user);
                        openMainActivity.putExtra("animationType","slide back");
                        startActivity(openMainActivity);
                        overridePendingTransition(0, 0);
                    }
                });
                return true;
            case R.id.action_post:
                try {
                    new APIYapAsyncTask()
                            .execute().get();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void runEnterAnimation() {

        Integer translationForLinearLayoutForScreen = yapScreen2ActivityViewHolder.linearLayoutForScreen.getWidth();
        yapScreen2ActivityViewHolder.linearLayoutForScreen
                .setTranslationX(translationForLinearLayoutForScreen);

        yapScreen2ActivityViewHolder.linearLayoutForScreen.animate()
                .setDuration(ANIM_DURATION).scaleX(1).scaleY(1).translationX(0)
                .translationY(0).setInterpolator(sDecelerator).withEndAction(new Runnable() {
            public void run() {
                startLoadingScreen();
            }
        });



    }

    public void runExitAnimationSlideBack(final Runnable endAction) {
        yapScreen2ActivityViewHolder.linearLayoutForScreen
                .animate()
                .setDuration(ANIM_DURATION)
                .translationX(yapScreen2ActivityViewHolder.linearLayoutForScreen.getWidth())
                .translationY(0)
                .withEndAction(endAction);

    }

    public void runExitAnimation(final Runnable endAction) {


    }

    public void startLoadingScreen(){

        try {
            new APILoadUserLibrariesAsyncTask(1,NUMBER_OF_LIBRARIES_PER_PAGE)
                    .execute().get();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    private void openImageIntent() {

// Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "MyDir" + File.separator);
        root.mkdirs();
        final String fname = "yap_image.png";
        final File sdImageMainDirectory = new File(root, fname);
        yapPictureFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, yapPictureFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));

        startActivityForResult(chooserIntent, SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode == RESULT_OK)
        {
            if(requestCode == SELECT_PICTURE)
            {
                final boolean isCamera;
                if(data == null)
                {
                    isCamera = true;
                }
                else
                {
                    final String action = data.getAction();
                    if(action == null)
                    {
                        isCamera = false;
                    }
                    else
                    {
                        isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }

                Uri selectedImageUri;
                if(isCamera)
                {
                    selectedImageUri = yapPictureFileUri;
                }
                else
                {
                    selectedImageUri = data == null ? null : data.getData();
                }
                try {
                    yapPictureSelected = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    int widthOfPicture = yapScreen2ActivityViewHolder.imageViewBottomLayoutPicture.getWidth();
                    int height = (widthOfPicture * 9 / 16);
                    yapPictureSelectedCropped = ThumbnailUtils
                            .extractThumbnail(
                                    yapPictureSelected,
                                    yapScreen2ActivityViewHolder.imageViewBottomLayoutPicture.getWidth(), height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (yapPictureSelected == null) {
                    Bitmap defaultBitmapForYapPicture = BitmapFactory.decodeResource(getResources(), R.drawable.default1);
                    yapScreen2ActivityViewHolder.imageViewBottomLayoutPicture.setImageBitmap(defaultBitmapForYapPicture);

                }else {

                    yapScreen2ActivityViewHolder.imageViewBottomLayoutPicture.setImageBitmap(yapPictureSelectedCropped);
                }
            }

        }
    }

    class YapScreen2ViewAsyncTask extends
            AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            user = intentBundle.getParcelable("user");
            mFileName = intentBundle.getString("mFileName");
            yapScreen2ActivityViewHolder.linearLayoutForScreen = (LinearLayout) view.findViewById(R.id.linearLayoutForScreen);
            yapScreen2ActivityViewHolder.linearLayoutForEditText =  (LinearLayout) view.findViewById(R.id.linearLayoutForEditText);
            yapScreen2ActivityViewHolder.editTextYapTitle = (EditText) view.findViewById(R.id.editTextYapTitle);
            yapScreen2ActivityViewHolder.editTextYapDescription = (EditText) view.findViewById(R.id.editTextYapDescription);
            yapScreen2ActivityViewHolder.linearLayoutForImageButtons = (LinearLayout) view.findViewById(R.id.linearLayoutForImageButtons);
            yapScreen2ActivityViewHolder.imageButtonAlbums = (ImageButton) view.findViewById(R.id.imageButtonAlbums);
            yapScreen2ActivityViewHolder.imageButtonYapPicture = (ImageButton) view.findViewById(R.id.imageButtonYapPicture);
            yapScreen2ActivityViewHolder.linearLayoutForBottomLayout = (LinearLayout) view.findViewById(R.id.linearLayoutForBottomLayout);
            yapScreen2ActivityViewHolder.relativeLayoutForBottomLayout = (RelativeLayout) view.findViewById(R.id.relativeLayoutForBottomLayout);
            yapScreen2ActivityViewHolder.progressBarForBottomLayout = (ProgressBar) view.findViewById(R.id.progressBarForBottomLayout);
            yapScreen2ActivityViewHolder.imageViewBottomLayoutPicture = (ImageView) view.findViewById(R.id.imageViewBottomLayoutPicture);
            yapScreen2ActivityViewHolder.listViewLibraries = (ListView) view.findViewById(R.id.listViewLibraries);
            mBackground = new ColorDrawable(Color.WHITE);
            mWindowBackground = new ColorDrawable(Color.WHITE);
            yapScreen2ActivityViewHolder.linearLayoutForScreen.setBackground(mBackground);
            user_libraries = new ArrayList<Library>();
            libraryListLazyAdapter = new LibraryListLazyAdapter(activity, user_libraries);
            yapScreen2ActivityViewHolder.imageButtonAlbums.setImageDrawable(getResources().getDrawable(R.drawable.library_active));
            Bitmap defaultBitmapForYapPicture = BitmapFactory.decodeResource(getResources(),R.drawable.default1);
            yapScreen2ActivityViewHolder.imageViewBottomLayoutPicture.setImageBitmap(defaultBitmapForYapPicture);
            yapScreen2ActivityViewHolder.imageButtonAlbums.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    yapScreen2ActivityViewHolder.imageButtonAlbums.setImageDrawable(getResources().getDrawable(R.drawable.library_active));
                    yapScreen2ActivityViewHolder.imageButtonYapPicture.setImageDrawable(getResources().getDrawable(R.drawable.photo_inactive));
                    yapScreen2ActivityViewHolder.listViewLibraries.setVisibility(View.VISIBLE);
                    yapScreen2ActivityViewHolder.imageViewBottomLayoutPicture.setVisibility(View.INVISIBLE);

                }
            });
            yapScreen2ActivityViewHolder.imageButtonYapPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    yapScreen2ActivityViewHolder.imageButtonYapPicture.setImageDrawable(getResources().getDrawable(R.drawable.photo_active));
                    yapScreen2ActivityViewHolder.imageButtonAlbums.setImageDrawable(getResources().getDrawable(R.drawable.library_inactive));
                    yapScreen2ActivityViewHolder.listViewLibraries.setVisibility(View.INVISIBLE);
                    yapScreen2ActivityViewHolder.imageViewBottomLayoutPicture.setVisibility(View.VISIBLE);
                }
            });

            yapScreen2ActivityViewHolder.imageViewBottomLayoutPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openImageIntent();
                }
            });

            yapScreen2ActivityViewHolder.listViewLibraries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    view.setSelected(true);
                }
            });

            awsInstance = AWS.getInstance();
            amazonS3Client = awsInstance.getAmazonS3Client();

            view.setTag(yapScreen2ActivityViewHolder);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            yapScreen2ActivityViewHolder.listViewLibraries.setAdapter(libraryListLazyAdapter);
            userLibrariesInfiniteListViewScrollListener =  new InfiniteListViewScrollListener(NUMBER_OF_LIBRARIES_PER_PAGE/2) {
                @Override
                public void loadMore(int page, int totalItemsCount) {
                    apiLoadUserLibrariesAsyncTask = new APILoadUserLibrariesAsyncTask(page,NUMBER_OF_LIBRARIES_PER_PAGE);
                    apiLoadUserLibrariesAsyncTask
                            .execute();
                }
            };
            yapScreen2ActivityViewHolder.listViewLibraries.setOnScrollListener(userLibrariesInfiniteListViewScrollListener);
            yapScreen2ActivityViewHolder.progressBarForBottomLayout
                    .setVisibility(View.VISIBLE);
            apiLoadUserLibrariesAsyncTask = new APILoadUserLibrariesAsyncTask(1,NUMBER_OF_LIBRARIES_PER_PAGE);
            apiLoadUserLibrariesAsyncTask
                    .execute();
            yapScreen2ActivityViewHolder.listViewLibraries.setVisibility(View.VISIBLE);

        }

    }

    class APILoadUserLibrariesAsyncTask extends
            AsyncTask<Void, Void, Void> {

        private Integer page;
        private Integer amount;

        APILoadUserLibrariesAsyncTask(Integer page, Integer amount) {
            this.page = page;
            this.amount = amount;
            System.out.println("Page : " + page);
        }

        @Override
        protected void onPreExecute() {


        }

        @Override
        protected Void doInBackground(Void... params) {
            Integer libraryListLazyAdapterCountBeforeLoadMore =  libraryListLazyAdapter.getCount();
            String api_url = "http://api.yapster.co/users/load/profile/libraries/";
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
                nameValuePairsJSONObject.put("profile_user_id",
                        user.id);
                nameValuePairsJSONObject.put("page",
                        page);
                nameValuePairsJSONObject.put("amount",
                        amount);
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
            apiLoadUserLibrariesJsonResponse = writer.toString();
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(apiLoadUserLibrariesJsonResponse);
            Boolean valid = jsonElement.getAsJsonObject().get("valid").getAsBoolean();
            if (valid == true) {
                JsonElement data_json_element = jsonElement.getAsJsonObject().get("data");
                if (data_json_element.isJsonNull() == false) {
                    JsonArray data = jsonElement.getAsJsonObject().get("data").getAsJsonArray();
                    for (Integer i = 0; i < data.size(); i++) {
                        JsonObject library_json_object = data.get(i).getAsJsonObject();
                        Library librarySubscribedReceived = new Library(library_json_object,null);
                        user_libraries.add(i + libraryListLazyAdapterCountBeforeLoadMore, librarySubscribedReceived);
                    }
                }else{
                    System.out.println("data_json_element : " + data_json_element.toString());
                }
            }
            Integer userListLazyAdapterCountAfterLoadMore =  libraryListLazyAdapter.getCount();
            if (libraryListLazyAdapterCountBeforeLoadMore == userListLazyAdapterCountAfterLoadMore){
                userLibrariesInfiniteListViewScrollListener.hasLoadedAll = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (yapScreen2ActivityViewHolder.progressBarForBottomLayout.getVisibility() == View.VISIBLE){
                yapScreen2ActivityViewHolder.progressBarForBottomLayout
                        .setVisibility(View.GONE);
            }
            if (userLibrariesInfiniteListViewScrollListener.hasLoadedAll != true){
                libraryListLazyAdapter.notifyDataSetChanged();
            }


        }
    }

    class APIYapAsyncTask extends
            AsyncTask<Void, Void, Void> {

        APIYapAsyncTask() {

        }

        @Override
        protected void onPreExecute() {


        }

        @Override
        protected Void doInBackground(Void... params) {
            File audioFile = new File(mFileName);
            String mFilePath = "/yapsterusers/uid/" + user.id + "/yaps/" + user.lastYapUserYapID;
            PutObjectResult yapPutObjectResult = amazonS3Client.putObject("yapster", mFilePath, audioFile);
            if (amazonS3Client.getObject("yapster",mFilePath) != null){
                System.out.println("Success uploading to Amazon S3");
            }
//
//
//
//            Integer libraryListLazyAdapterCountBeforeLoadMore =  libraryListLazyAdapter.getCount();
//            String api_url = "http://api.yapster.co/users/load/profile/libraries/";
//            HttpClient httpclient = new DefaultHttpClient();
//            HttpPost httppost = new HttpPost(api_url);
//            // List<NameValuePair> nameValuePairs = null;
//            String nameValuePairsJSONObjectString = null;
//            try {
//                JSONObject nameValuePairsJSONObject = new JSONObject();
//                nameValuePairsJSONObject.put("user_id",
//                        user.id);
//                nameValuePairsJSONObject.put("session_id",
//                        user.sessionID);
//                nameValuePairsJSONObject.put("profile_user_id",
//                        user.id);
//                nameValuePairsJSONObject.put("page",
//                        page);
//                nameValuePairsJSONObject.put("amount",
//                        amount);
//                nameValuePairsJSONObjectString = nameValuePairsJSONObject
//                        .toString();
//            } catch (Exception e) {
//                Log.e("Error: ", e.getMessage());
//            }
//            try {
//                StringEntity nameValuePairsStringEntity = new StringEntity(
//                        nameValuePairsJSONObjectString);
//                nameValuePairsStringEntity.setContentType("application/json");
//                httppost.setEntity(nameValuePairsStringEntity);
//            } catch (UnsupportedEncodingException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//            HttpResponse response = null;
//            try {
//                response = httpclient.execute(httppost);
//            } catch (ClientProtocolException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//            InputStream responseInputStream = null;
//            try {
//                responseInputStream = response.getEntity().getContent();
//            } catch (IllegalStateException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//            StringWriter writer = new StringWriter();
//            try {
//                IOUtils.copy(responseInputStream, writer, "UTF-8");
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//            apiLoadUserLibrariesJsonResponse = writer.toString();
//            JsonParser jsonParser = new JsonParser();
//            JsonElement jsonElement = jsonParser.parse(apiLoadUserLibrariesJsonResponse);
//            Boolean valid = jsonElement.getAsJsonObject().get("valid").getAsBoolean();
//            if (valid == true) {
//                JsonElement data_json_element = jsonElement.getAsJsonObject().get("data");
//                if (data_json_element.isJsonNull() == false) {
//                    JsonArray data = jsonElement.getAsJsonObject().get("data").getAsJsonArray();
//                    for (Integer i = 0; i < data.size(); i++) {
//                        JsonObject library_json_object = data.get(i).getAsJsonObject();
//                        Library librarySubscribedReceived = new Library(library_json_object,null);
//                        user_libraries.add(i + libraryListLazyAdapterCountBeforeLoadMore, librarySubscribedReceived);
//                    }
//                }else{
//                    System.out.println("data_json_element : " + data_json_element.toString());
//                }
//            }
//            Integer userListLazyAdapterCountAfterLoadMore =  libraryListLazyAdapter.getCount();
//            if (libraryListLazyAdapterCountBeforeLoadMore == userListLazyAdapterCountAfterLoadMore){
//                userLibrariesInfiniteListViewScrollListener.hasLoadedAll = true;
//            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
//            if (yapScreen2ActivityViewHolder.progressBarForBottomLayout.getVisibility() == View.VISIBLE){
//                yapScreen2ActivityViewHolder.progressBarForBottomLayout
//                        .setVisibility(View.GONE);
//            }
//            if (userLibrariesInfiniteListViewScrollListener.hasLoadedAll != true){
//                libraryListLazyAdapter.notifyDataSetChanged();
//            }


        }
    }



}
