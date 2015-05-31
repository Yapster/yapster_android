package co.yapster.yapster;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.apache.commons.lang3.time.DateUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

/**
 * Created by gurkarangulati on 2/5/15.
 */

class UserListLazyAdapterViewHolder{
    TextView textViewUserName;
    TextView textViewUserDescription;
    ImageView imageViewUserPicture;
    URL userPictureURL;
    User profile_user;

}

public class UserListLazyAdapter extends BaseAdapter {

    private Activity activity;
    private static LayoutInflater inflater = null;
    public ImageLoader imageLoader;
    public DisplayImageOptions options;
    public Drawable fallback;
    public View finalView;
    public UserListLazyAdapterViewHolder userListLazyAdapterViewHolder;
    private AsyncTask<Void,Void,Void> userListLazyAdapterAsyncTask;
    private URL libraryPictureURL;
    private ArrayList<User> users;
    private User profile_user;
    private User user;

    public UserListLazyAdapter(Activity activity, ArrayList<User> users,User user) {
        this.activity = activity;
        this.users = users;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.user = user;
        fallback = activity.getResources().getDrawable(R.drawable.default1);
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder().resetViewBeforeLoading(true)
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .showImageForEmptyUri(fallback)
                .showImageOnFail(fallback)
                .showImageOnLoading(fallback)
                .displayer(new FadeInBitmapDisplayer(300)).build();
        // END - UNIVERSAL IMAGE LOADER SETUP

    }


    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        finalView = null;
        Boolean alreadyLoaded;
        if (convertView == null) {
            finalView = inflater.inflate(
                    R.layout.activity_users_list_view_user, parent, false);
            userListLazyAdapterViewHolder = new UserListLazyAdapterViewHolder();
            finalView.setTag(userListLazyAdapterViewHolder);
            alreadyLoaded = false;
        }else{
            userListLazyAdapterViewHolder = (UserListLazyAdapterViewHolder) convertView.getTag();
            finalView = convertView;
            alreadyLoaded = true;
        }
        profile_user = users.get(position);
        userListLazyAdapterViewHolder.profile_user = profile_user;
        userListLazyAdapterViewHolder.textViewUserName = (TextView) finalView
                .findViewById(R.id.textViewUserName); // title
        userListLazyAdapterViewHolder.textViewUserDescription = (TextView) finalView
                .findViewById(R.id.textViewUserDescription);
        userListLazyAdapterViewHolder.imageViewUserPicture = (ImageView) finalView
                .findViewById(R.id.imageViewUserPicture);
        userListLazyAdapterViewHolder.textViewUserName.setText(userListLazyAdapterViewHolder.profile_user.getFullName());
        userListLazyAdapterViewHolder.textViewUserDescription.setText(userListLazyAdapterViewHolder.profile_user.description);
        if (alreadyLoaded == false) {
            userListLazyAdapterAsyncTask = new UserListLazyAdapterAsyncTask(userListLazyAdapterViewHolder);
            userListLazyAdapterAsyncTask
                    .execute();
        }
//        View.OnClickListener finalViewOnClickListener = new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                userListLazyAdapterViewHolder.imageViewUserPicture
//                        .setTransitionName("userProfilePicture");
//                Intent intent = new Intent(activity,
//                        ProfileScreenActivity.class);
//                int[] screenLocation = new int[2];
//                userListLazyAdapterViewHolder.imageViewUserPicture.getLocationOnScreen(screenLocation);
//                int orientation = activity.getResources().getConfiguration().orientation;
//
//                intent.putExtra("user", user);
//                intent.putExtra("profile_user", profile_user);
//                intent.putExtra("orientation", orientation);
//                intent.putExtra("left", screenLocation[0]);
//                intent.putExtra("top", screenLocation[1]);
//                intent.putExtra("width", userListLazyAdapterViewHolder.imageViewUserPicture.getWidth());
//                intent.putExtra("height", userListLazyAdapterViewHolder.imageViewUserPicture.getHeight());
//
//                ActivityOptions options = ActivityOptions
//                        .makeSceneTransitionAnimation(
//                                activity,
//                                Pair.create(
//                                        (View) userListLazyAdapterViewHolder.imageViewUserPicture,
//                                        "userProfilePicture"),
//                                Pair.create(
//                                        (View) userListLazyAdapterViewHolder.textViewUserName,
//                                        "userName"));
//                activity.startActivity(intent, options.toBundle());
//                activity.overridePendingTransition(0, 0);
//            }
//        };
//        finalView.setOnClickListener(finalViewOnClickListener);
        return finalView;

    }

    class UserListLazyAdapterAsyncTask extends
            AsyncTask<Void, Void, Void> {

        UserListLazyAdapterViewHolder asyncUserListLazyAdapterViewHolder;


        UserListLazyAdapterAsyncTask(UserListLazyAdapterViewHolder viewHolder){
            asyncUserListLazyAdapterViewHolder = viewHolder;
        }

        @Override
        protected Void doInBackground(Void... params) {
            CognitoCachingCredentialsProvider cognitoProvider = new CognitoCachingCredentialsProvider(
                    activity.getBaseContext(), // get the context for the
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
                    profile_user.profilePicturePath);
            generatePresignedURLRequest.setMethod(HttpMethod.GET);
            generatePresignedURLRequest.setExpiration(dateForSigningS3Object);
            asyncUserListLazyAdapterViewHolder.userPictureURL = amazonS3Client
                    .generatePresignedUrl(generatePresignedURLRequest);
            asyncUserListLazyAdapterViewHolder.profile_user.setProfilePictureURL(asyncUserListLazyAdapterViewHolder.userPictureURL);
            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            imageLoader.displayImage(asyncUserListLazyAdapterViewHolder.userPictureURL.toString(), asyncUserListLazyAdapterViewHolder.imageViewUserPicture, options);
        }

    }
}
