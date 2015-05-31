package co.yapster.yapster;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.transition.Explode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.concurrent.ExecutionException;

class YapScreen1ActivityViewHolder{
    RelativeLayout relativeLayoutForScreen;
    TextView textViewTimeLabel;
    ImageButton imageButtonYap;
    LinearLayout linearLayoutBottomButtonsBar;
    ImageButton imageButtonPlayOrPause;
    ImageButton imageButtonTrash;
    ImageButton imageButtonExtra;
    Boolean yapIconVisible = false;
    Boolean numberLabelVisible = false;
    Boolean bottomButtonBarVisible = false;

}

/**
 * Created by gurkarangulati on 2/11/15.
 */
public class YapScreen1Activity extends Activity {

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
    YapScreen1ActivityViewHolder yapScreen1ActivityViewHolder;
    private static final int ANIM_DURATION = 500;
    private static final TimeInterpolator sDecelerator = new DecelerateInterpolator();
    private static final TimeInterpolator sAccelerator = new AccelerateInterpolator();
    private static final String LOG_TAG = "AudioRecordTest";
    private String mFileName = null;
    private String mFileName2 = null;
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
    String animationType;

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
        setContentView(R.layout.activity_yap_screen_1);
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
        yapScreen1ActivityViewHolder = new YapScreen1ActivityViewHolder();
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
            new YapScreen1ViewAsyncTask()
                    .execute().get();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (savedInstanceState == null) {
            if (animationType.equals("slide back")) {
                runEnterAnimationSlideBack();
            } else if (animationType.equals("circular reveal")) {
                runEnterAnimation();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.yap_screen_1_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * On selecting action bar icons
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.action_next:
                runExitAnimation(new Runnable() {
                    public void run() {
                        // *Now* go ahead and exit the activity

                        Intent openMainActivity = new Intent(
                                "co.yapster.yapster.YAPSCREEN2ACTIVITY");
                        openMainActivity.putExtra("user",user);
                        openMainActivity.putExtra("mFileName", mFileName);
                        startActivity(openMainActivity);
                        overridePendingTransition(0, 0);
                    }
                });
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void runEnterAnimation() {
        yapScreen1ActivityViewHolder.imageButtonYap.setVisibility(View.INVISIBLE);
        mBackground.setAlpha(0);
        mWindowBackground.setAlpha(0);
        yapScreen1ActivityViewHolder.imageButtonYap
                .setScaleX(0.0f);
        yapScreen1ActivityViewHolder.imageButtonYap
                .setScaleY(0.0f);
        ObjectAnimator bgAnim = ObjectAnimator.ofInt(mBackground, "alpha", 0,
                255);
        bgAnim.setDuration(ANIM_DURATION);
        bgAnim.start();
        ObjectAnimator bgAnim2 = ObjectAnimator.ofInt(mWindowBackground, "alpha", 0,
                255);
        bgAnim2.setDuration(ANIM_DURATION);
        bgAnim2.start();

        yapScreen1ActivityViewHolder.relativeLayoutForScreen.animate()
                .setDuration(ANIM_DURATION).scaleX(1).scaleY(1).translationX(0)
                .translationY(-yapScreen1ActivityViewHolder.relativeLayoutForScreen.getHeight()).setInterpolator(sDecelerator).withEndAction(new Runnable() {
            public void run() {
                yapScreen1ActivityViewHolder.imageButtonYap
                        .setVisibility(View.VISIBLE);
                yapScreen1ActivityViewHolder.imageButtonYap
                        .animate().setDuration(ANIM_DURATION / 2).scaleX(1)
                        .scaleY(1).translationX(0).translationY(0)
                        .setInterpolator(sDecelerator);
            }
        });



    }

    public void runEnterAnimationSlideBack() {

        yapScreen1ActivityViewHolder.relativeLayoutForScreen
                .animate()
                .setDuration(ANIM_DURATION)
                .translationX(yapScreen1ActivityViewHolder.relativeLayoutForScreen.getWidth())
                .translationY(0).setInterpolator(sDecelerator);

    }

    public void runExitAnimation(final Runnable endAction) {

        yapScreen1ActivityViewHolder.relativeLayoutForScreen
                .animate()
                .setDuration(ANIM_DURATION)
                .translationX(-yapScreen1ActivityViewHolder.relativeLayoutForScreen.getWidth())
                .translationY(0)
                .withEndAction(endAction);

    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        if (numberOfRecordings >= 1){
            mRecorder.setOutputFile(mFileName2);
            System.out.println("This is the mFileName being recorded to : " + mFileName2);
        }else{
            mRecorder.setOutputFile(mFileName);
            System.out.println("This is the mFileName being recorded to : " + mFileName);
        }
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    public String AudioRecordTest() {
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiorecordtest.3pp";
        return mFileName;
    }

    public void startTimer(){
        startTime = SystemClock.uptimeMillis();
        myHandler.postDelayed(updateTimerMethod, 0);
    }

    public void stopTimer(){
        timeSwap += timeInMillies;
        myHandler.removeCallbacks(updateTimerMethod);
    }

    private Runnable updateTimerMethod = new Runnable() {

        public void run() {
            timeInMillies = SystemClock.uptimeMillis() - startTime;
            finalTime = timeSwap + timeInMillies;

            int seconds = (int) (finalTime / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            int milliseconds = (int) (finalTime % 1000);
            yapScreen1ActivityViewHolder.textViewTimeLabel.setText("" + minutes + ":"
                    + String.format("%02d", seconds) + ":"
                    + String.format("%03d", milliseconds));
            if(timeInMillies > 240000){
                yapScreen1ActivityViewHolder.textViewTimeLabel.setTextColor(Color.RED);
            }
            myHandler.postDelayed(this, 0);
        }

    };

    @Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
    }

    public void joinRecordedFiles() throws IOException {
        String fileName3 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/fileName" + numberOfRecordings + 2 + ".3pg";
        FileInputStream fistream1 = new FileInputStream(mFileName);  // first source file
        FileInputStream fistream2 = new FileInputStream(mFileName2);//second source file
        SequenceInputStream sistream = new SequenceInputStream(fistream1, fistream2);
        FileOutputStream fostream = new FileOutputStream(fileName3);//destinationfile

        int temp;

        while( ( temp = sistream.read() ) != -1)
        {
            // System.out.print( (char) temp ); // to print at DOS prompt
            fostream.write(temp);   // to write to file
        }
        fostream.close();
        sistream.close();
        fistream1.close();
        fistream2.close();

        File mFile = new File(mFileName);
        File mFile2 = new File(mFileName2);
        mFile.delete();
        mFile2.delete();
        mFileName = fileName3;
    }



    class YapScreen1ViewAsyncTask extends
            AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            user = intentBundle.getParcelable("user");
            thumbnailTop = intentBundle.getInt("top");
            thumbnailLeft = intentBundle.getInt("left");
            thumbnailWidth = intentBundle.getInt("width");
            thumbnailHeight = intentBundle.getInt("height");
            mOriginalOrientation = intentBundle.getInt("orientation");
            animationType = intentBundle.getString("animationType");
            yapScreen1ActivityViewHolder.relativeLayoutForScreen = (RelativeLayout) view.findViewById(R.id.relativeLayoutForScreen);
            yapScreen1ActivityViewHolder.textViewTimeLabel = (TextView) view.findViewById(R.id.textViewTimeLabel);
            yapScreen1ActivityViewHolder.imageButtonYap = (ImageButton) view.findViewById(R.id.imageButtonYap);
            yapScreen1ActivityViewHolder.linearLayoutBottomButtonsBar = (LinearLayout) view.findViewById(R.id.linearLayoutBottomButtonsBar);
            yapScreen1ActivityViewHolder.imageButtonPlayOrPause = (ImageButton) view.findViewById(R.id.imageButtonPlayOrPause);
            yapScreen1ActivityViewHolder.imageButtonTrash = (ImageButton) view.findViewById(R.id.imageButtonTrash);
            yapScreen1ActivityViewHolder.imageButtonExtra = (ImageButton) view.findViewById(R.id.imageButtonExtra);
            if (yapScreen1ActivityViewHolder.bottomButtonBarVisible == false){
                yapScreen1ActivityViewHolder.linearLayoutBottomButtonsBar.setVisibility(View.GONE);
            }else{
                yapScreen1ActivityViewHolder.linearLayoutBottomButtonsBar.setVisibility(View.VISIBLE);
            }
            if (yapScreen1ActivityViewHolder.numberLabelVisible == false){
                yapScreen1ActivityViewHolder.textViewTimeLabel.setVisibility(View.GONE);
            }else{
                yapScreen1ActivityViewHolder.textViewTimeLabel.setVisibility(View.VISIBLE);
            }
            mBackground = new ColorDrawable(Color.WHITE);
            mWindowBackground = new ColorDrawable(Color.WHITE);
            yapScreen1ActivityViewHolder.relativeLayoutForScreen.setBackground(mBackground);
            ViewOutlineProvider buttonYapOutlineProvider = new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    int diameter = yapScreen1ActivityViewHolder.imageButtonYap.getHeight();
                    outline.setOval(0, 0, diameter, diameter);
                }
            };
            yapScreen1ActivityViewHolder.imageButtonYap.setOutlineProvider(buttonYapOutlineProvider);
            yapScreen1ActivityViewHolder.imageButtonYap.setClipToOutline(true);
            mFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/fileName1.3pp";
            yapScreen1ActivityViewHolder.imageButtonYap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (numberOfRecordings > 0){
//                        mFileName2 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/fileName2.3pp";
//
//
//                    }else{
                        onRecord(mStartRecording);
                        if (mStartRecording){
                            if (timeInMillies > 0) {
                                timeInMillies = 0;
                                numberOfRecordings = 0;
                            }
                            startTimer();
                            yapScreen1ActivityViewHolder.textViewTimeLabel.setVisibility(View.VISIBLE);
                            yapScreen1ActivityViewHolder.bottomButtonBarVisible = false;
                            yapScreen1ActivityViewHolder.linearLayoutBottomButtonsBar.setVisibility(View.GONE);
                            yapScreen1ActivityViewHolder.numberLabelVisible = true;
                        }else{
                            stopTimer();
                            if (timeInMillies > 0){
                                yapScreen1ActivityViewHolder.bottomButtonBarVisible = true;
                                yapScreen1ActivityViewHolder.linearLayoutBottomButtonsBar.setVisibility(View.VISIBLE);
                                numberOfRecordings++;
                            }
//                            if (numberOfRecordings > 1){
//                                try {
//                                    joinRecordedFiles();
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                            }
                        }
                        mStartRecording = !mStartRecording;
                    }
            });
            yapScreen1ActivityViewHolder.imageButtonPlayOrPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPlay(mStartPlaying);
                    if (mStartPlaying) {
                        yapScreen1ActivityViewHolder.imageButtonPlayOrPause.setImageDrawable(getResources().getDrawable( R.drawable.ic_pause_black ));
                    } else {
                        yapScreen1ActivityViewHolder.imageButtonPlayOrPause.setImageDrawable(getResources().getDrawable( R.drawable.ic_play_black ));
                    }
                    mStartPlaying = !mStartPlaying;
                }
            });
            view.setTag(yapScreen1ActivityViewHolder);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


        }

    }
}