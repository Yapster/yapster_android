package co.yapster.yapster;

import java.util.ArrayList;
import java.util.HashMap;

import android.animation.TimeInterpolator;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.transition.Explode;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import co.yapster.yapster.LibraryIndividualsWithYapsLazyAdapter;
import co.yapster.yapster.ProfileUserScreenLibrariesListViewAdapter;

public class PlayerActivity extends Activity {

	private ActionBar actionBar;
	private Intent intent;
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
	private ArrayList<HashMap<String, String>> libraryList;
	private ArrayList<HashMap<String, String>> stringArrayOfSongsInLibrary;
	private HashMap<String, ArrayList<HashMap<String, String>>> libraryData;
	private ProfileUserScreenLibrariesListViewAdapter profileUserScreenLibrariesListViewAdapter;
	private LibraryIndividualsWithYapsLazyAdapter libraryIndividualsWithYapsLazyAdapter;
	private View view;
	private Bundle savedInstanceState;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
		getWindow().setAllowEnterTransitionOverlap(true);
		getWindow().setAllowReturnTransitionOverlap(true);
		getWindow().setSharedElementExitTransition(new Explode());
		getWindow().setSharedElementEnterTransition(new Explode());
		// getWindow().setBackgroundDrawable(
		// new ColorDrawable(Color.parseColor("#ffffff")));
		setContentView(R.layout.activity_player);
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
		profileScreenViewHolder = new ProfileScreenViewHolder();
		this.savedInstanceState = savedInstanceState;
		view = this.findViewById(android.R.id.content);

		super.onCreate(savedInstanceState);
	}

}
