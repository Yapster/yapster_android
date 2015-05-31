package co.yapster.yapster;

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
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import org.apache.commons.lang3.time.DateUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by gurkarangulati on 2/21/15.
 *
 *
 */

class YapDetailsViewHolder {
    TextView textViewLibraryYapTitle;
    TextView textViewLibraryYapDate;
    TextView textViewLibraryYapLength;
    TextView textViewLibraryYapDescription;
    RelativeLayout relativeLayoutForYapPicture;
    ImageView imageViewYapPicture;
    Yap yap;
}

public class YapDetailsLazyAdapter extends BaseAdapter {

    private Activity activity;
    private Library library;
    private User user;
    private static LayoutInflater inflater = null;
    public ImageLoader imageLoader;
    public DisplayImageOptions options;
    public Drawable fallback;
    private YapDetailsViewHolder yapDetailsViewHolder;
    private AsyncTask<Void,Void,Void> yapDetailsViewAsyncTask;
    public View finalView;


    public YapDetailsLazyAdapter(Activity activity, User user, Library library) {
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


    public Activity getActivityForThisItem() {
        return this.activity;
    }


    @Override
    public int getCount() {
        return library.yaps.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        finalView = null;
        Boolean alreadyLoaded;

        if (convertView == null) {
            finalView = inflater.inflate(
                    R.layout.activity_library_details_yap_details,
                    parent, false);
            yapDetailsViewHolder = new YapDetailsViewHolder();
            alreadyLoaded = false;
        }else{
            finalView = convertView;
            alreadyLoaded = true;
            }
        yapDetailsViewHolder.textViewLibraryYapTitle = (TextView) finalView
                .findViewById(R.id.textViewLibraryYapTitle); // title
//            yapDetailsViewHolder.textViewLibraryYapLength = (TextView) finalView
//                    .findViewById(R.id.textViewLibraryYapLength);
        yapDetailsViewHolder.relativeLayoutForYapPicture = (RelativeLayout) finalView
                .findViewById(R.id.relativeLayoutForYapPicture);
        yapDetailsViewHolder.textViewLibraryYapDescription = (TextView) finalView
                .findViewById(R.id.textViewLibraryYapDescription);
        yapDetailsViewHolder.textViewLibraryYapDate = (TextView) finalView
                .findViewById(R.id.textViewLibraryYapDate);
        yapDetailsViewHolder.yap = library.yaps.get(position);
        yapDetailsViewHolder.textViewLibraryYapTitle
                .setText(yapDetailsViewHolder.yap.title);
        yapDetailsViewHolder.textViewLibraryYapDescription
                .setText(yapDetailsViewHolder.yap.description);
        yapDetailsViewHolder.textViewLibraryYapDate.setText(yapDetailsViewHolder.yap.dateCreated);
        yapDetailsViewHolder.imageViewYapPicture = (ImageView) finalView
                .findViewById(R.id.imageViewYapPicture);
        if (yapDetailsViewHolder.yap.picturePath != null){
//            yapDetailsViewHolder.imageViewYapPicture.getViewTreeObserver().addOnPreDrawListener(
//                    new ViewTreeObserver.OnPreDrawListener() {
//                        public boolean onPreDraw() {
//                            yapDetailsViewHolder.imageViewYapPicture.getLayoutParams().height = yapDetailsViewHolder.imageViewYapPicture.getWidth() * 9 / 16;
//
//                            return true;
//                        }
//                    });
//            yapDetailsViewHolder.imageViewYapPicture.getLayoutParams().height = yapDetailsViewHolder.imageViewYapPicture.getWidth() * 9 / 16;
            yapDetailsViewHolder.imageViewYapPicture.setVisibility(View.VISIBLE);
        }else{
            yapDetailsViewHolder.imageViewYapPicture.setVisibility(View.GONE);
        }

        finalView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             Player player = Player.getInstance(user);
             player.setActivity(activity);
             player.playYap(position,library);
            }
        });
        if (yapDetailsViewHolder.yap.picturePath != null) {
            imageLoader.displayImage(yapDetailsViewHolder.yap.picturePathURL.toString(), yapDetailsViewHolder.imageViewYapPicture, options);

        }

        return finalView;

    }

}
