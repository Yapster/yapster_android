package co.yapster.yapster;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import org.apache.commons.lang3.time.DateUtils;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

class YapViewHolder {
    TextView textViewLibraryYapTitle;
    TextView textViewLibraryYapDate;
    TextView textViewLibraryYapLength;
    TextView textViewLibraryYapDescription;
    RelativeLayout relativeLayoutForYapPicture;
    ImageView imageViewYapPicture;
    Bitmap bitmapYapPicture;
    Bitmap bitmapYapPictureCropped;
    Yap yap;

}

public class YapLazyAdapter extends BaseAdapter {

    private Activity activity;
    private Library library;
    private User user;
    private static LayoutInflater inflater = null;
    public ImageLoader imageLoader;
    public DisplayImageOptions options;
    public Drawable fallback;
	private YapViewHolder yapViewHolder;
    private AsyncTask<Void,Void,Void> yapViewAsyncTask;
    public View finalView;
    ArrayList<Yap> yaps;

    public YapLazyAdapter(Activity activity, User user, Library library) {
        this.activity = activity;
        this.library = library;
        this.user = user;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        fallback = activity.getResources().getDrawable(R.drawable.default1);
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder().resetViewBeforeLoading(true)
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .showImageForEmptyUri(fallback)
                .showImageOnFail(fallback)
                .showImageOnLoading(fallback)
                .displayer(new FadeInBitmapDisplayer(300)).build();
    }

    public YapLazyAdapter(Activity activity, User user, ArrayList<Yap> yaps) {
        this.activity = activity;
        this.yaps = yaps;
        this.user = user;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        fallback = activity.getResources().getDrawable(R.drawable.default1);
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder().resetViewBeforeLoading(true)
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .showImageForEmptyUri(fallback)
                .showImageOnFail(fallback)
                .showImageOnLoading(fallback)
                .displayer(new FadeInBitmapDisplayer(300)).build();
    }

	public Activity getActivityForThisItem() {
		return this.activity;
	}

    @Override
    public int getCount() {
        if (library != null){
            return library.yaps.size();
        }else{
            return yaps.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return this.getItem(this.getCount() - position - 1);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        finalView = null;
        Boolean alreadyLoaded;

        if (convertView == null) {
            finalView = inflater.inflate(
                    R.layout.activity_main_library_list_view_yap_layout,
                    parent, false);
            yapViewHolder = new YapViewHolder();
            alreadyLoaded = false;
        }else{
            finalView = convertView;
            alreadyLoaded = true;
        }
        yapViewHolder.textViewLibraryYapTitle = (TextView) finalView
                .findViewById(R.id.textViewLibraryYapTitle); // title
//            yapViewHolder.textViewLibraryYapLength = (TextView) finalView
//                    .findViewById(R.id.textViewLibraryYapLength);
        yapViewHolder.relativeLayoutForYapPicture = (RelativeLayout) finalView
                .findViewById(R.id.relativeLayoutForYapPicture);
        yapViewHolder.textViewLibraryYapDescription = (TextView) finalView
                .findViewById(R.id.textViewLibraryYapDescription);
        yapViewHolder.textViewLibraryYapDate = (TextView) finalView
                .findViewById(R.id.textViewLibraryYapDate);
        if (library != null){
            if (library.yaps.size() != 0){
                yapViewHolder.yap = library.yaps.get(position);
            }else{

            }
        }else{
            yapViewHolder.yap = yaps.get(position);
        }
        yapViewHolder.textViewLibraryYapTitle
                .setText(yapViewHolder.yap.title);
        yapViewHolder.textViewLibraryYapDescription
                .setText(yapViewHolder.yap.description);
        yapViewHolder.textViewLibraryYapDate.setText(yapViewHolder.yap.dateCreated);
        if (alreadyLoaded == false) {
            yapViewAsyncTask = new YapViewAsyncTask(yapViewHolder);
            yapViewAsyncTask.execute();
        }
        finalView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Player player = Player.getInstance(user);
                player.setActivity(activity);
                player.playYap(position,library);
            }
        });
        finalView.setTag(yapViewHolder);
        return finalView;

    }

    class YapViewAsyncTask extends
			AsyncTask<Void, Void, Void> {

        private YapViewHolder asyncViewHolder;
        private boolean hasPictureFlag;

        public YapViewAsyncTask(YapViewHolder yapViewHolder) {

            asyncViewHolder = yapViewHolder;

        }

        @Override
        protected Void doInBackground(Void... params) {
            if (asyncViewHolder.yap.picturePath == null) {
                hasPictureFlag = false;
            }else{
                hasPictureFlag = true;
            }
            if (hasPictureFlag == true) {
                Date currentDate = new Date();
                Date dateForSigningS3Object = DateUtils.addHours(currentDate, 24);
                AWS awsInstance = AWS.getInstance();
                AmazonS3 amazonS3Client = awsInstance.getAmazonS3Client();
                GeneratePresignedUrlRequest generatePresignedURLRequest = new GeneratePresignedUrlRequest(
                        "yapster",
                        asyncViewHolder.yap.picturePath);
                generatePresignedURLRequest.setMethod(HttpMethod.GET);
                generatePresignedURLRequest.setExpiration(dateForSigningS3Object);
                asyncViewHolder.yap.picturePathURL = amazonS3Client
                        .generatePresignedUrl(generatePresignedURLRequest);
                GeneratePresignedUrlRequest generatePresignedURLRequest2 = new GeneratePresignedUrlRequest(
                        "yapster",
                        asyncViewHolder.yap.audioPath);
                generatePresignedURLRequest2.setMethod(HttpMethod.GET);
                generatePresignedURLRequest2.setExpiration(dateForSigningS3Object);
                asyncViewHolder.yap.audioPathURL = amazonS3Client
                        .generatePresignedUrl(generatePresignedURLRequest2);

            }
            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
//            if (asyncViewHolder.yap.picturePath != null) {
//                imageLoader.displayImage(asyncViewHolder.yap.picturePathURL.toString(), asyncViewHolder.imageViewYapPicture, options);
//
//            }

        }
    }


}