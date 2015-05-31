package co.yapster.yapster;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.time.DateUtils;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

class LibraryIndividualsWithYapsViewHolder {
    TextView textViewLibraryName;
    ImageView imageViewLibraryImage;
    ListView listViewLibraryYaps;
    String stringLibraryPicturePath;
    String stringPhoneStorageLibraryPicturePath;
    LinearLayout linearLayoutForLibraryImage;
    LinearLayout linearLayoutForListOfYaps;
    RelativeLayout relativeLayoutForImageViewLibraryImage;
    Library library;
    URL libraryPictureURL;
}

public class LibraryIndividualsWithYapsLazyAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<Library> libraries;
	private static LayoutInflater inflater = null;
	public ImageLoader imageLoader;
    public DisplayImageOptions options;
    public Drawable fallback;
    public static int NUMBER_OF_YAPS = 3;
    public View finalView;
    public LibraryIndividualsWithYapsViewHolder libraryIndividualsWithYapsViewHolder;
    private Library library;
    private YapLazyAdapter adapterYap;
    private URL libraryPictureURL;
    private ArrayList<Yap> yaps;
    private String libraryName;
    private Bitmap libraryBitmap;
    private User user;
    private AsyncTask<Void,Void,Void> libraryIndividualsWithYapsAsyncTask;


	public LibraryIndividualsWithYapsLazyAdapter(Activity activity,ArrayList<Library> libraries, User user) {
		this.activity = activity;
		this.libraries = libraries;
        this.user = user;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        fallback = activity.getResources().getDrawable( R.drawable.default1);
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

	public int getCount() {
		return libraries.size();
	}

	public Object getItem(int position) {
		return libraries.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		finalView = null;
        Boolean alreadyLoaded;

        if (convertView == null) {
            finalView = inflater.inflate(
                    R.layout.activity_main_library_list_view, parent, false);
            libraryIndividualsWithYapsViewHolder = new LibraryIndividualsWithYapsViewHolder();
            alreadyLoaded = false;
        } else {

            finalView = convertView;
            alreadyLoaded = true;
        }
        library = libraries.get(position);
        libraryIndividualsWithYapsViewHolder.library = library;
        libraryIndividualsWithYapsViewHolder.textViewLibraryName = (TextView) finalView
                .findViewById(R.id.textViewLibraryName); // title
        libraryIndividualsWithYapsViewHolder.imageViewLibraryImage = (ImageView) finalView
                .findViewById(R.id.imageViewLibraryImage);
        libraryIndividualsWithYapsViewHolder.linearLayoutForListOfYaps = (LinearLayout) finalView
                .findViewById(R.id.linearLayoutForListOfYaps);
        libraryIndividualsWithYapsViewHolder.relativeLayoutForImageViewLibraryImage = (RelativeLayout) finalView.findViewById(R.id.relativeLayoutForImageViewLibraryImage);
        libraryIndividualsWithYapsViewHolder.linearLayoutForLibraryImage = (LinearLayout) finalView.findViewById(R.id.linearLayoutForLibraryImage);
        libraryName = library.title;
        libraryIndividualsWithYapsViewHolder.textViewLibraryName.setText(libraryName);
        adapterYap = new YapLazyAdapter(activity, user, library);
        if (library.yaps.size() != 0){
            for (Integer i = 0; i < NUMBER_OF_YAPS; i++){
                View libraryView = adapterYap.getView(i, null, null);
                libraryView.setId(i);
                libraryIndividualsWithYapsViewHolder.linearLayoutForListOfYaps.addView(libraryView,i);
            }
        }
        if (alreadyLoaded == false) {
            imageLoader.displayImage(libraryIndividualsWithYapsViewHolder.library.picturePathURL.toString(), libraryIndividualsWithYapsViewHolder.imageViewLibraryImage, options);
        }
        libraryIndividualsWithYapsViewHolder.imageViewLibraryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                libraryIndividualsWithYapsViewHolder.imageViewLibraryImage
                        .setTransitionName("libraryPicture");
                libraryIndividualsWithYapsViewHolder.relativeLayoutForImageViewLibraryImage
                        .setTransitionName("relativeLayoutForlibraryPicture");
                Intent intent = new Intent(activity,
                        LibraryDetailsScreenActivity.class);
                int[] screenLocation = new int[2];
                v.getLocationOnScreen(screenLocation);
                int orientation = activity.getResources().getConfiguration().orientation;

                intent.putExtra("user",user);
                intent.putExtra("library",libraries.get(position));
                intent.putExtra("orientation", orientation);
                intent.putExtra("left", screenLocation[0]);
                intent.putExtra("top", screenLocation[1]);
                intent.putExtra("width", v.getWidth());
                intent.putExtra("height", v.getHeight());

                ActivityOptions options = ActivityOptions
                        .makeSceneTransitionAnimation(
                                activity,
                                Pair.create(
                                        (View) libraryIndividualsWithYapsViewHolder.imageViewLibraryImage,
                                        "libraryPicture"),
                                Pair.create(
                                        (View) libraryIndividualsWithYapsViewHolder.textViewLibraryName,
                                        "libraryName"),
                                Pair.create(
                                        (View)  libraryIndividualsWithYapsViewHolder.linearLayoutForLibraryImage,
                                        "linearLayoutForLibraryPicture"),
                                Pair.create(
                                        (View) libraryIndividualsWithYapsViewHolder.relativeLayoutForImageViewLibraryImage,
                                        "relativeLayoutForLibraryPicture"));
                activity.startActivity(intent, options.toBundle());
                activity.overridePendingTransition(0, 0);


            }
        });

		return finalView;

	}

}