package co.yapster.yapster;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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

/**
 * Created by gurkarangulati on 2/10/15.
 */

class LibraryListLazyAdapterViewHolder {
    TextView textViewLibraryName;
    TextView textViewLibraryDescription;
    ImageView imageViewLibraryPicture;
    URL libraryPictureURL;
    Library library;
}


public class LibraryListLazyAdapter extends BaseAdapter {

    private Activity activity;
    private static LayoutInflater inflater = null;
    public ImageLoader imageLoader;
    public DisplayImageOptions options;
    public Drawable fallback;
    public View finalView;
    public LibraryListLazyAdapterViewHolder libraryListLazyAdapterViewHolder;
    private AsyncTask<Void,Void,Void> libraryListLazyAdapterAsyncTask;
    private ArrayList<Library> libraries;
    private Library library;

    public LibraryListLazyAdapter(Activity activity, ArrayList<Library> libraries) {
        this.activity = activity;
        this.libraries = libraries;
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
        // END - UNIVERSAL IMAGE LOADER SETUP

    }


    @Override
    public int getCount() {
        return libraries.size();
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
                    R.layout.activity_libraries_list_view_library, parent, false);
            libraryListLazyAdapterViewHolder = new LibraryListLazyAdapterViewHolder();
            finalView.setTag(libraryListLazyAdapterViewHolder);
            alreadyLoaded = false;
        }else{
            libraryListLazyAdapterViewHolder = (LibraryListLazyAdapterViewHolder) convertView.getTag();
            finalView = convertView;
            alreadyLoaded = true;
        }
        library = libraries.get(position);
        libraryListLazyAdapterViewHolder.library = library;
        libraryListLazyAdapterViewHolder.textViewLibraryName = (TextView) finalView
                .findViewById(R.id.textViewLibraryName); // title
        libraryListLazyAdapterViewHolder.textViewLibraryDescription = (TextView) finalView
                .findViewById(R.id.textViewLibraryDescription);
        libraryListLazyAdapterViewHolder.imageViewLibraryPicture = (ImageView) finalView
                .findViewById(R.id.imageViewLibraryPicture);
        libraryListLazyAdapterViewHolder.textViewLibraryName.setText(libraryListLazyAdapterViewHolder.library.title);
        if (libraryListLazyAdapterViewHolder.library.description != null) {
            libraryListLazyAdapterViewHolder.textViewLibraryDescription.setText(libraryListLazyAdapterViewHolder.library.description);
        }
        if (alreadyLoaded == false) {
            imageLoader.displayImage(libraryListLazyAdapterViewHolder.library.picturePathURL.toString(), libraryListLazyAdapterViewHolder.imageViewLibraryPicture, options);

        }
        return finalView;

    }
}
