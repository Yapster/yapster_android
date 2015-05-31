package co.yapster.yapster;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.time.DateUtils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

class ProfileUserScreenLibrariesListViewHolder {
	TextView textViewLibraryTitle;
	TextView textViewLibraryDescription;
	ImageView imageViewLibraryImage;
	String stringLibraryPicturePath;
	Bitmap bitmapLibraryPicture;
}

public class ProfileUserScreenLibrariesListViewAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<Library> libraries;
	private static LayoutInflater inflater = null;
	private Date startTime;
	public ImageLoader imageLoader;

	public ProfileUserScreenLibrariesListViewAdapter(Activity activity,ArrayList<Library> libraries) {
		startTime = new Date();
		this.activity = activity;
		this.libraries = libraries;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(activity));
	}

	public int getCount() {
		return libraries.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View finalView = null;

		if (convertView == null) {
			convertView = inflater
					.inflate(
							R.layout.activity_profile_library_list_table_of_contents_view,
							parent, false);
			ProfileUserScreenLibrariesListViewHolder profileUserScreenLibrariesListViewHolder = new ProfileUserScreenLibrariesListViewHolder();
			try {
				finalView = new ProfileUserScreenLibrariesListViewAysncTask(
						this.activity, convertView, position).execute(
						profileUserScreenLibrariesListViewHolder).get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			ProfileUserScreenLibrariesListViewHolder profileUserScreenLibrariesListViewHolder = (ProfileUserScreenLibrariesListViewHolder) convertView
					.getTag();
			finalView = convertView;
		}
		return finalView;

	}

	class ProfileUserScreenLibrariesListViewAysncTask extends
			AsyncTask<ProfileUserScreenLibrariesListViewHolder, Bitmap, View> {

		public Activity asyncActivity;
		private ProfileUserScreenLibrariesListViewHolder profileUserScreenLibrariesListViewHolder;
		private View asyncView;
		private Integer asyncPosition;
		private Library library;
		private URL asyncLibraryPictureURL;
		private Bitmap asyncLibraryPictureBitmapCropped;

		public ProfileUserScreenLibrariesListViewAysncTask(Activity activity,
				View view, Integer position) {

			asyncActivity = activity;
			asyncView = view;
			asyncPosition = position;

		}

		@Override
		protected View doInBackground(
				ProfileUserScreenLibrariesListViewHolder... params) {

			library = libraries.get(asyncPosition);
			asyncView.setElevation(4.0f);
			asyncView.setTranslationZ(4.0f);
			profileUserScreenLibrariesListViewHolder = params[0];

			profileUserScreenLibrariesListViewHolder.textViewLibraryTitle = (TextView) asyncView
					.findViewById(R.id.textViewLibraryTitle);
			profileUserScreenLibrariesListViewHolder.textViewLibraryDescription = (TextView) asyncView
					.findViewById(R.id.textViewLibraryDescription);
			profileUserScreenLibrariesListViewHolder.imageViewLibraryImage = (ImageView) asyncView
					.findViewById(R.id.imageViewLibraryImage);

			String libraryName = library.title;
			String libraryDescription = library.description;
			profileUserScreenLibrariesListViewHolder.stringLibraryPicturePath = library.picturePath;

			profileUserScreenLibrariesListViewHolder.textViewLibraryTitle
					.setText(libraryName);
			profileUserScreenLibrariesListViewHolder.textViewLibraryDescription
					.setText(libraryDescription);
			CognitoCachingCredentialsProvider cognitoProvider = new CognitoCachingCredentialsProvider(
					asyncActivity.getBaseContext(), // get the context for the
					"100149822649",
					"us-east-1:c791e7de-ead0-4117-a964-94486e3ebee9",
					"arn:aws:iam::100149822649:role/Cognito_YapsterUnauth_DefaultRole",
					"arn:aws:iam::100149822649:role/Cognito_YapsterAuth_DefaultRole",
					Regions.US_EAST_1);
			Date currentDate = new Date();
			Date dateForSigningS3Object = DateUtils.addHours(currentDate, 24);
			cognitoProvider.refresh();
			AmazonS3 amazonS3Client = new AmazonS3Client(cognitoProvider);
			GeneratePresignedUrlRequest generatePresignedURLRequest = new GeneratePresignedUrlRequest(
					"yapster",
					profileUserScreenLibrariesListViewHolder.stringLibraryPicturePath);
			generatePresignedURLRequest.setMethod(HttpMethod.GET);
			generatePresignedURLRequest.setExpiration(dateForSigningS3Object);
			asyncLibraryPictureURL = amazonS3Client
					.generatePresignedUrl(generatePresignedURLRequest);
			try {
				profileUserScreenLibrariesListViewHolder.bitmapLibraryPicture = BitmapFactory
						.decodeStream(asyncLibraryPictureURL.openConnection()
								.getInputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			asyncLibraryPictureBitmapCropped = ThumbnailUtils
					.extractThumbnail(
							profileUserScreenLibrariesListViewHolder.bitmapLibraryPicture,
							400, 400, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);

			asyncView.setTag(profileUserScreenLibrariesListViewHolder);

			return asyncView;

		}

		@Override
		protected void onPostExecute(View result) {
			super.onPostExecute(result);
			if (asyncLibraryPictureBitmapCropped != null) {
				profileUserScreenLibrariesListViewHolder.imageViewLibraryImage = (ImageView) asyncView
						.findViewById(R.id.imageViewLibraryImage);
				profileUserScreenLibrariesListViewHolder.imageViewLibraryImage
						.setImageBitmap(asyncLibraryPictureBitmapCropped);
				
			}
			Date endDate = new Date();
			long timeForThisAsyncTaskToRun = endDate.getTime() - startTime.getTime();
			System.out.println("It Took : " + timeForThisAsyncTaskToRun + " for Libraries List View To Load.");

		}

	}
}