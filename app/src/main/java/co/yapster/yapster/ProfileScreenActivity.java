package co.yapster.yapster;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.transition.Explode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

class ProfileScreenViewHolder {
	ScrollView scrollViewProfileScreen;
	RelativeLayout relativeLayoutForTopPortionOfProfile;
	RelativeLayout relativeLayoutUserInfo;
	RelativeLayout relativeLayoutForListViewUserLibrariesTableOfContents;
	RelativeLayout relativeLayoutForAreaForListViewUserLibrariesIndividuals;
	RelativeLayout relativeLayoutForListViewUserLibrariesIndividuals;
	TextView textViewUserName;
	ImageView imageViewUserProfilePicture;
	ImageView imageButtonSubscribeToUser;
	RelativeLayout relativeLayoutForProfileScreen;
	LinearLayout linearLayoutForUserLibraries;
	ListView listViewUserLibrariesTableOfContents;
	ListView listViewUserLibrariesIndividuals;
	ProgressBar progressBarForListViewUserLibrariesTableOfContents;
	ProgressBar progressBarForListViewUserLibrariesIndividuals;
    TextView textViewLocation;

}

public class ProfileScreenActivity extends Activity {

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
	private ProfileScreenViewHolder profileScreenViewHolder;
	String[] picture_urls = { "misc/drake1.jpg", "misc/twins1.jpg",
			"misc/twins8.jpg" };
	private ProfileUserScreenLibrariesListViewAdapter profileUserScreenLibrariesListViewAdapter;
	private LibraryIndividualsWithYapsLazyAdapter libraryIndividualsWithYapsLazyAdapter;
	private View view;
	private Bundle savedInstanceState;
    Activity activity;
    AsyncTask<Void, Void, Void> apiProfileLoadLibrariesAsyncTask;
    AsyncTask<Void, Void, Void> profileScreenLoadLibrariesTableOfContentsDataAsyncTask;
    AsyncTask<Void, Void, Void> profileScreenLoadLibrariesIndividualsDataAsyncTask;
    public ImageLoader imageLoader;
    public DisplayImageOptions options;
    public Drawable fallback;
    User profile_user;
    User user;
    ArrayList<Library> profile_user_libraries;
    String apiProfileLoadLibrariesJsonResponse;
    public static int NUMBER_OF_LIBRARIES_LOADED_ON_EACH_PAGE = 3;
    PopupMenu popupMenu;

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
		setContentView(R.layout.activity_profile_user);
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
		profileScreenViewHolder = new ProfileScreenViewHolder();
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
			new ProfileScreenViewAsyncTask()
					.execute().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (savedInstanceState == null) {
			ViewTreeObserver observer = profileScreenViewHolder.imageViewUserProfilePicture
					.getViewTreeObserver();
			observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

				@Override
				public boolean onPreDraw() {
					profileScreenViewHolder.imageViewUserProfilePicture
							.getViewTreeObserver()
							.removeOnPreDrawListener(this);

					// Figure out where the thumbnail and full size versions
					// are, relative
					// to the screen and each other
					int[] screenLocation = new int[2];
					profileScreenViewHolder.imageViewUserProfilePicture
							.getLocationOnScreen(screenLocation);
					mLeftDelta = thumbnailLeft - screenLocation[0];
					mTopDelta = thumbnailTop - screenLocation[1];

					// Scale factors to make the large version the same size as
					// the thumbnail
					mWidthScale = (float) thumbnailWidth
							/ profileScreenViewHolder.imageViewUserProfilePicture
									.getWidth();
					mHeightScale = (float) thumbnailHeight
							/ profileScreenViewHolder.imageViewUserProfilePicture
									.getHeight();

					runEnterAnimation();
					return true;
				}
			});
		}

	}

	/**
	 * The enter animation scales the picture in from its previous thumbnail
	 * size/location, colorizing it in parallel. In parallel, the background of
	 * the activity is fading in. When the pictue is in place, the text
	 * description drops down.
	 */
	public void runEnterAnimation() {
		final long duration = (long) (ANIM_DURATION * MainActivity.sAnimatorScale);

		// Set starting values for properties we're going to animate. These
		// values scale and position the full size version down to the thumbnail
		// size/location, from which we'll animate it back up
		profileScreenViewHolder.imageViewUserProfilePicture.setPivotX(0);
		profileScreenViewHolder.imageViewUserProfilePicture.setPivotY(0);
		profileScreenViewHolder.imageViewUserProfilePicture
				.setScaleX(mWidthScale);
		profileScreenViewHolder.imageViewUserProfilePicture
				.setScaleY(mHeightScale);
		profileScreenViewHolder.imageViewUserProfilePicture
				.setTranslationX(mLeftDelta);
		profileScreenViewHolder.imageViewUserProfilePicture
				.setTranslationY(mTopDelta);

		// We'll fade the text in later
		profileScreenViewHolder.textViewUserName.setAlpha(0);
		profileScreenViewHolder.relativeLayoutUserInfo.setAlpha(0);
		// profileScreenViewHolder.linearLayoutForRestOfScreen.setAlpha(0);
		profileScreenViewHolder.imageButtonSubscribeToUser
				.setVisibility(View.INVISIBLE);
		mBackground.setAlpha(0);

		// Animate scale and translation to go from thumbnail to full size
		profileScreenViewHolder.imageViewUserProfilePicture.animate()
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
						profileScreenViewHolder.relativeLayoutUserInfo
								.animate().setDuration(duration / 2)
								.translationY(0).alpha(1)
								.setInterpolator(sDecelerator);
						profileScreenViewHolder.textViewUserName.animate()
								.setDuration(duration / 2).translationY(0)
								.alpha(1).setInterpolator(sDecelerator);

						profileScreenViewHolder.imageButtonSubscribeToUser
								.setScaleX(0.0f);
						profileScreenViewHolder.imageButtonSubscribeToUser
								.setScaleY(0.0f);
						profileScreenViewHolder.imageButtonSubscribeToUser
								.setVisibility(View.VISIBLE);
						profileScreenViewHolder.imageButtonSubscribeToUser
								.animate().setDuration(duration / 2).scaleX(1)
								.scaleY(1).translationX(0).translationY(0)
								.setInterpolator(sDecelerator)
                                .withEndAction(new Runnable() {
                                    public void run() {
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

        if (profileScreenLoadLibrariesTableOfContentsDataAsyncTask != null) {
            if (profileScreenLoadLibrariesTableOfContentsDataAsyncTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
                profileScreenLoadLibrariesTableOfContentsDataAsyncTask.cancel(true);
            }
        }

        if (profileScreenLoadLibrariesIndividualsDataAsyncTask != null) {
            if (profileScreenLoadLibrariesIndividualsDataAsyncTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
                profileScreenLoadLibrariesTableOfContentsDataAsyncTask.cancel(true);
            }
        }
		final long duration = (long) (ANIM_DURATION * MainActivity.sAnimatorScale);

		// First, slide/fade text out of the way
		profileScreenViewHolder.relativeLayoutUserInfo.animate()
				.translationY(0).alpha(0).setDuration(duration / 4)
				.setInterpolator(sAccelerator).withEndAction(new Runnable() {
					public void run() {
						profileScreenViewHolder.imageButtonSubscribeToUser
								.animate().setDuration(duration / 4).scaleX(0)
								.scaleY(0).translationX(0).translationY(0)
								.alpha(0).withEndAction(new Runnable() {
									public void run() {
										ObjectAnimator bgAnim = ObjectAnimator
												.ofInt(mBackground, "alpha", 0);
										bgAnim.setDuration(duration);
										bgAnim.start();

										profileScreenViewHolder.linearLayoutForUserLibraries
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
										profileScreenViewHolder.imageViewUserProfilePicture
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
		// }
		// });

	}

	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = 125 + totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	public void startLoadingScreen() {

		apiProfileLoadLibrariesAsyncTask = new APIProfileLoadLibrariesAsyncTask(1,NUMBER_OF_LIBRARIES_LOADED_ON_EACH_PAGE);
        apiProfileLoadLibrariesAsyncTask.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
        if(user == profile_user){
            inflater.inflate(R.menu.activity_profile_user_user_menu, menu);
        }else{
            inflater.inflate(R.menu.activity_profile_user_menu, menu);
        }

		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * On selecting action bar icons
	 * */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Take appropriate action for each action item click
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_edit:
                return true;

            case R.id.action_report:
                return true;

            case R.id.action_help:
                return true;
//                if (popupMenu == null) {
//                    View menuItemView = findViewById(R.id.menu_overflow); // SAME ID AS MENU ID
//                    popupMenu = new PopupMenu(this, menuItemView);
//                    popupMenu.inflate(R.menu.activity_profile_user_popover_menu_with_popover);
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
//                                    View action_user_profile_view = popupMenu.getMenu().getItem(R.id.action_user_profile).getActionView().findViewById(R.id.imageViewUserProfileImage);
//                                    action_user_profile_view.setTransitionName("userProfilePicture");
//                                    Intent intent = new Intent(activity,
//                                            ProfileScreenActivity.class);
//                                    int[] screenLocation = new int[2];
//                                    action_user_profile_view.getLocationOnScreen(screenLocation);
//                                    int orientation = getResources().getConfiguration().orientation;
//
//                                    intent.putExtra("user", user);
//                                    intent.putExtra("profile_user", user);
//                                    intent.putExtra("orientation", orientation);
//                                    intent.putExtra("left", screenLocation[0]);
//                                    intent.putExtra("top", screenLocation[1]);
//                                    intent.putExtra("width", action_user_profile_view.getWidth());
//                                    intent.putExtra("height", action_user_profile_view.getHeight());
//
//                                    ActivityOptions options = ActivityOptions
//                                            .makeSceneTransitionAnimation(
//                                                    activity,
//                                                    Pair.create(
//                                                            action_user_profile_view,
//                                                            "userProfilePicture"));
//                                    startActivity(intent, options.toBundle());
//                                    overridePendingTransition(0, 0);
//                                    return true;
//                                default:
//                                    System.out.println("Testing");
//                            }
//                            return false;
//                        }
//                    });
//                }
//                popupMenu.show();
//
//
//
//
//
////                Intent openMainActivity = new Intent(
////                        "co.yapster.yapster.YAPSCREEN1ACTIVITY");
////                openMainActivity.putExtra("user", user);
////                startActivity(openMainActivity);
////                overridePendingTransition(0, 0);
//                return true;
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

    public String getLocationText(){
        if(user.country  == profile_user.country && profile_user.country == "United States" ){
            if (profile_user.usState.isEmpty()){
                if (profile_user.city.isEmpty()){
                    return profile_user.country;
                }else{
                    return profile_user.city + ", " + profile_user.country;
                }
            }else{
                if (profile_user.city.isEmpty()){
                    return profile_user.usState;
                }else{
                    return profile_user.city + ", " + profile_user.usState;
                }
            }
        }else if (profile_user.country.isEmpty()){
            return null;
        }else{
            if(profile_user.city.isEmpty()){
                return profile_user.country;
            }else{
                return profile_user.city + ", " + profile_user.country;
            }
        }
    }

    class ProfileScreenViewAsyncTask extends
            AsyncTask<Void, Void, Void> {
        // private ProfileScreenViewHolder profileScreenViewHolder;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            user = intentBundle.getParcelable("user");
            profile_user = intentBundle.getParcelable("profile_user");
            thumbnailTop = intentBundle.getInt("top");
            thumbnailLeft = intentBundle.getInt("left");
            thumbnailWidth = intentBundle.getInt("width");
            thumbnailHeight = intentBundle.getInt("height");
            mOriginalOrientation = intentBundle.getInt("orientation");
            profileScreenViewHolder.textViewUserName = (TextView) view
                    .findViewById(R.id.textViewUserName);
            profileScreenViewHolder.textViewLocation = (TextView) view.findViewById(R.id.textViewLocation);
            profileScreenViewHolder.relativeLayoutForListViewUserLibrariesTableOfContents = (RelativeLayout) view
                    .findViewById(R.id.relativeLayoutForListViewUserLibrariesTableOfContents);
            profileScreenViewHolder.relativeLayoutForAreaForListViewUserLibrariesIndividuals = (RelativeLayout) view
                    .findViewById(R.id.relativeLayoutForAreaForListViewUserLibrariesIndividuals);
            profileScreenViewHolder.relativeLayoutForListViewUserLibrariesIndividuals = (RelativeLayout) view
                    .findViewById(R.id.relativeLayoutForListViewUserLibrariesIndividuals);
            profileScreenViewHolder.relativeLayoutForAreaForListViewUserLibrariesIndividuals
                    .setElevation(4.0f);
            profileScreenViewHolder.relativeLayoutForAreaForListViewUserLibrariesIndividuals
                    .setTranslationZ(4.0f);
            profileScreenViewHolder.relativeLayoutForListViewUserLibrariesIndividuals
                    .setElevation(4.0f);
            profileScreenViewHolder.relativeLayoutForListViewUserLibrariesIndividuals
                    .setTranslationZ(4.0f);
            profileScreenViewHolder.relativeLayoutUserInfo = (RelativeLayout) view
                    .findViewById(R.id.relativeLayoutUserInfo);
            profileScreenViewHolder.listViewUserLibrariesTableOfContents = (ListView) view
                    .findViewById(R.id.listViewUserLibrariesTableOfContents);
            profileScreenViewHolder.listViewUserLibrariesIndividuals = (ListView) view
                    .findViewById(R.id.listViewUserLibrariesIndividuals);
            profileScreenViewHolder.imageButtonSubscribeToUser = (ImageView) view
                    .findViewById(R.id.imageButtonSubscribeToUser);
            profileScreenViewHolder.imageButtonSubscribeToUser
                    .setElevation(4.0f);
            profileScreenViewHolder.imageButtonSubscribeToUser
                    .setTranslationZ(4.0f);
            ViewOutlineProvider imageButtonSubscribeToUserOutlineProvider = new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    int diameter = profileScreenViewHolder.imageButtonSubscribeToUser.getHeight();
                    outline.setOval(0, 0, diameter, diameter);
                }
            };
            profileScreenViewHolder.imageButtonSubscribeToUser.setOutlineProvider(imageButtonSubscribeToUserOutlineProvider);
            profileScreenViewHolder.imageButtonSubscribeToUser.setClipToOutline(true);
            profileScreenViewHolder.scrollViewProfileScreen = (ScrollView) view
                    .findViewById(R.id.scrollViewProfileScreen);
            profileScreenViewHolder.relativeLayoutForProfileScreen = (RelativeLayout) view
                    .findViewById(R.id.relativeLayoutForProfileScreen);
            profileScreenViewHolder.linearLayoutForUserLibraries = (LinearLayout) view
                    .findViewById(R.id.linearLayoutForUserLibraries);
            mBackground = new ColorDrawable(Color.WHITE);
            profileScreenViewHolder.relativeLayoutForProfileScreen
                    .setBackground(mBackground);
            profileScreenViewHolder.imageViewUserProfilePicture = (ImageView) view
                    .findViewById(R.id.imageViewUserProfilePicture);
            profileScreenViewHolder.textViewUserName
                    .setText(profile_user.getFullName());
            profileScreenViewHolder.listViewUserLibrariesTableOfContents
                    .setVisibility(View.INVISIBLE);
            view.setTag(profileScreenViewHolder);
            return  null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            String locationText = getLocationText();
            profileScreenViewHolder.textViewLocation.setText(locationText);
            if (profile_user.profilePicturePath.isEmpty() == false){
                imageLoader.displayImage(profile_user.profilePictureURL.toString(), profileScreenViewHolder.imageViewUserProfilePicture, options);
            }

        }

    }

    class APIProfileLoadLibrariesAsyncTask extends
            AsyncTask<Void, Void, Void> {

        private Integer page;
        private Integer amount;

        APIProfileLoadLibrariesAsyncTask(Integer page, Integer amount) {
            this.page = page;
            this.amount = amount;
        }

        @Override
        protected void onPreExecute() {
            profileScreenViewHolder.progressBarForListViewUserLibrariesTableOfContents = (ProgressBar) view
                    .findViewById(R.id.progressBarForListViewUserLibrariesTableOfContents);
            profileScreenViewHolder.progressBarForListViewUserLibrariesTableOfContents
                    .setVisibility(View.VISIBLE);

        }

        @Override
        protected Void doInBackground(Void... params) {
            profile_user_libraries = new ArrayList<Library>();
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
                        profile_user.id);
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
            apiProfileLoadLibrariesJsonResponse = writer.toString();
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(apiProfileLoadLibrariesJsonResponse);
            Boolean valid = jsonElement.getAsJsonObject().get("valid").getAsBoolean();
            if (profile_user_libraries.isEmpty()) {
                if (valid == true) {
                    profile_user_libraries = new ArrayList<Library>();
                    JsonArray data = jsonElement.getAsJsonObject().get("data").getAsJsonArray();
                    for (Integer i = 0; i < data.size(); i++) {
                        JsonObject library_json_object = data.get(i).getAsJsonObject();
                        Library profile_user_library = new Library(library_json_object, i);
                        profile_user_libraries.add(profile_user_library);
                    }
                    profileUserScreenLibrariesListViewAdapter = new ProfileUserScreenLibrariesListViewAdapter(
                            activity, profile_user_libraries);
                }
            } else {
                if (valid == true) {
                    JsonArray data = jsonElement.getAsJsonObject().get("data").getAsJsonArray();
                    for (Integer i = 0; i < data.size(); i++) {
                        JsonObject library_json_object = data.get(i).getAsJsonObject();
                        Library profile_user_library = new Library(library_json_object, i);
                        profile_user_libraries.add(profile_user_library);
                    }
                    profileUserScreenLibrariesListViewAdapter.notifyDataSetChanged();

                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            profileScreenLoadLibrariesTableOfContentsDataAsyncTask = new ProfileScreenLoadLibrariesTableOfContentsDataAsyncTask();
            profileScreenLoadLibrariesTableOfContentsDataAsyncTask.execute();
        }
    }

    class ProfileScreenLoadLibrariesTableOfContentsDataAsyncTask extends
            AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            profileUserScreenLibrariesListViewAdapter = new ProfileUserScreenLibrariesListViewAdapter(
                    activity,profile_user_libraries );
            profileScreenViewHolder.listViewUserLibrariesTableOfContents
                    .setTranslationZ(4.0f);
            profileScreenViewHolder.listViewUserLibrariesTableOfContents
                    .setElevation(4.0f);
            profileScreenViewHolder.listViewUserLibrariesTableOfContents
                    .setOnItemClickListener(new OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent,
                                                View view, int position, long arg3) {
                            System.out.println("list view postion = "
                                    + position);
                        }
                    });

            view.setTag(profileScreenViewHolder);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            profileScreenViewHolder.listViewUserLibrariesTableOfContents
                    .setAdapter(profileUserScreenLibrariesListViewAdapter);
            setListViewHeightBasedOnChildren(profileScreenViewHolder.listViewUserLibrariesTableOfContents);
            profileScreenViewHolder.progressBarForListViewUserLibrariesTableOfContents
                    .setVisibility(View.GONE);
            profileScreenViewHolder.listViewUserLibrariesTableOfContents
                    .setElevation(4.0f);
            profileScreenViewHolder.listViewUserLibrariesTableOfContents
                    .setTranslationZ(0.0f);

            profileScreenViewHolder.listViewUserLibrariesTableOfContents
                    .setTranslationY(5 * -profileScreenViewHolder.listViewUserLibrariesTableOfContents
                            .getHeight());
            profileScreenViewHolder.listViewUserLibrariesTableOfContents
                    .animate()
                    .setDuration(10 * ANIM_DURATION)
                    .translationY(
                            5 * -profileScreenViewHolder.listViewUserLibrariesTableOfContents
                                    .getHeight()).translationZ(4.0f)
                    .setInterpolator(sDecelerator);
            profileScreenViewHolder.listViewUserLibrariesTableOfContents
                    .setVisibility(View.VISIBLE);
            profileScreenLoadLibrariesIndividualsDataAsyncTask = new ProfileScreenLoadLibrariesIndividualsDataAsyncTask();
            profileScreenLoadLibrariesIndividualsDataAsyncTask
                    .execute();

        }

    }

	class ProfileScreenLoadLibrariesIndividualsDataAsyncTask extends
			AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			profileScreenViewHolder.progressBarForListViewUserLibrariesIndividuals = (ProgressBar) view
					.findViewById(R.id.progressBarForListViewUserLibrariesIndividuals);
			profileScreenViewHolder.progressBarForListViewUserLibrariesIndividuals
					.setVisibility(View.VISIBLE);

		}

		@Override
		protected Void doInBackground(Void... params) {
            libraryIndividualsWithYapsLazyAdapter = new LibraryIndividualsWithYapsLazyAdapter(activity,profile_user_libraries,user);
			profileScreenViewHolder.listViewUserLibrariesIndividuals
					.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long arg3) {


						}
					});

			view.setTag(profileScreenViewHolder);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			profileScreenViewHolder.listViewUserLibrariesIndividuals
					.setAdapter(libraryIndividualsWithYapsLazyAdapter);
//			setListViewHeightBasedOnChildren(profileScreenViewHolder.listViewUserLibrariesIndividuals);
			profileScreenViewHolder.relativeLayoutForAreaForListViewUserLibrariesIndividuals
					.setElevation(4.0f);
			profileScreenViewHolder.relativeLayoutForAreaForListViewUserLibrariesIndividuals
					.setTranslationZ(4.0f);
			profileScreenViewHolder.progressBarForListViewUserLibrariesIndividuals
					.setVisibility(View.GONE);
			profileScreenViewHolder.listViewUserLibrariesIndividuals
					.setElevation(4.0f);
			profileScreenViewHolder.listViewUserLibrariesIndividuals
					.setTranslationZ(0.0f);
			profileScreenViewHolder.listViewUserLibrariesIndividuals
					.setTranslationY(5 * -profileScreenViewHolder.listViewUserLibrariesIndividuals
							.getHeight());
			profileScreenViewHolder.listViewUserLibrariesIndividuals
					.animate()
					.setDuration(10 * ANIM_DURATION)
					.translationY(
							5 * -profileScreenViewHolder.listViewUserLibrariesIndividuals
									.getHeight()).translationZ(4.0f)
					.setInterpolator(sDecelerator);
			profileScreenViewHolder.relativeLayoutForListViewUserLibrariesIndividuals
					.setVisibility(View.VISIBLE);
			profileScreenViewHolder.relativeLayoutForListViewUserLibrariesIndividuals
					.animate()
					.setDuration(10 * ANIM_DURATION)
					.translationY(
							5 * -profileScreenViewHolder.listViewUserLibrariesIndividuals
									.getHeight()).translationZ(4.0f);
			profileScreenViewHolder.listViewUserLibrariesIndividuals
					.setVisibility(View.VISIBLE);

		}

	}

}
