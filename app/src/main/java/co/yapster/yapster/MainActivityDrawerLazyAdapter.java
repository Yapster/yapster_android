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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by gurkarangulati on 4/27/15.
 */

class MainActivityDrawerViewHolder {

    ImageView imageViewIcon;
    TextView textViewTitle;
    String menuOption;

}

public class MainActivityDrawerLazyAdapter extends BaseAdapter {

    private Activity activity;
    private static LayoutInflater inflater = null;
    public ImageLoader imageLoader;
    public DisplayImageOptions options;
    public Drawable fallback;
    public View finalView;
    public MainActivityDrawerViewHolder mainActivityDrawerViewHolder;
    private User user;
    private String[] menuOptionsStringArray;

   public MainActivityDrawerLazyAdapter(Activity activity, User user){

       this.activity = activity;
       this.user = user;
       menuOptionsStringArray = new String[4];
       menuOptionsStringArray[0] = "Subscribed";
       menuOptionsStringArray[1] = "Explore";
       menuOptionsStringArray[2] = "Yap";
       menuOptionsStringArray[3] = "Logout";
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
   }

    @Override
    public int getCount() {
        return menuOptionsStringArray.length;
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
                    R.layout.activity_main_drawer_list_item, parent, false);
            mainActivityDrawerViewHolder = new MainActivityDrawerViewHolder();
            alreadyLoaded = false;
        } else {

            finalView = convertView;
            alreadyLoaded = true;
        }
        mainActivityDrawerViewHolder.menuOption = menuOptionsStringArray[position];
        mainActivityDrawerViewHolder.textViewTitle = (TextView) finalView
                .findViewById(R.id.textViewTitle); // title
        mainActivityDrawerViewHolder.imageViewIcon = (ImageView) finalView
                .findViewById(R.id.imageViewIcon);
        mainActivityDrawerViewHolder.textViewTitle
                .setText(mainActivityDrawerViewHolder.menuOption);

        if (alreadyLoaded == false) {
            if (mainActivityDrawerViewHolder.menuOption.equals("Subscribed")){

                mainActivityDrawerViewHolder.imageViewIcon.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_subscribed_black));

            }else if (mainActivityDrawerViewHolder.menuOption.equals("Explore")){

                mainActivityDrawerViewHolder.imageViewIcon.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_explore_black));


            }else if (mainActivityDrawerViewHolder.menuOption.equals("Yap")){

                mainActivityDrawerViewHolder.imageViewIcon.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_yap_black));

            }else if (mainActivityDrawerViewHolder.menuOption.equals("Logout")){

                mainActivityDrawerViewHolder.imageViewIcon.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_logout_black));
            }
        }

        return finalView;

    }
}
