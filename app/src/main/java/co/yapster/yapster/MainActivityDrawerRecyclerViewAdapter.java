package co.yapster.yapster;

/**
 * Created by gurkarangulati on 5/27/15.
 */


import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;


public class MainActivityDrawerRecyclerViewAdapter extends RecyclerView.Adapter<MainActivityDrawerRecyclerViewAdapter.ViewHolder> {

    private static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
    // IF the view under inflation and population is header or Item
    private static final int TYPE_ITEM = 1;

    private String mNavTitles[]; // String Array to store the passed titles Value from MainActivity.java
    private int mIcons[];       // Int Array to store the passed icons resource value from MainActivity.java

    private User user;        //String Resource for header View Name
    private Activity activity;
    private static LayoutInflater inflater = null;
    public ImageLoader imageLoader;
    public DisplayImageOptions options;
    public Drawable fallback;
    public View finalView;
    private ViewHolder headerViewHolder;
    ColorMatrixColorFilter grayScaleFilter;
    ColorMatrix graymatrix;
    Context context;
    View lastSelected;


    public MainActivityDrawerRecyclerViewAdapter(Activity activity, User user, String Titles[],int Icons[], Context passedContext){

        this.activity = activity;
        this.user = user;
        mNavTitles = Titles;                //have seen earlier
        mIcons = Icons;
        this.context = passedContext;
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
        graymatrix = new ColorMatrix();
        graymatrix.setSaturation(0);
        grayScaleFilter = new ColorMatrixColorFilter(graymatrix);
    }

    // Creating a ViewHolder which extends the RecyclerView View Holder
    // ViewHolder are used to to store the inflated views in order to recycle them

    public static class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{
        int Holderid;

        TextView textViewDrawerUserFullName;
        TextView textViewDrawerUserUsername;
        ImageView imageViewUserProfilePicture;
        TextView textViewTitle;
        ImageView imageViewIcon;
        Context viewContext;


        public ViewHolder(View itemView,int ViewType, Context c) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);


            // Here we set the appropriate view in accordance with the the view type as passed when the holder object is created
            viewContext = c;
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
            itemView.setFocusable(true);
            itemView.setFocusableInTouchMode(true);
            if(ViewType == TYPE_ITEM) {
                textViewTitle = (TextView) itemView.findViewById(R.id.textViewTitle); // Creating TextView object with the id of textView from item_row.xml
                imageViewIcon = (ImageView) itemView.findViewById(R.id.imageViewIcon);// Creating ImageView object with the id of ImageView from item_row.xml
                Holderid = 1;                                               // setting holder id as 1 as the object being populated are of type item row
            }
            else{


                textViewDrawerUserFullName = (TextView) itemView.findViewById(R.id.textViewDrawerUserFullName);         // Creating Text View object from header.xml for name
                textViewDrawerUserUsername = (TextView) itemView.findViewById(R.id.textViewDrawerUserUsername);       // Creating Text View object from header.xml for email
                imageViewUserProfilePicture = (ImageView) itemView.findViewById(R.id.imageViewUserProfilePicture);// Creating Image view object from header.xml for profile pic
                Holderid = 0;                                                // Setting holder id = 0 as the object being populated are of type header view
            }
        }

        @Override
        public void onClick(View v){
            Toast.makeText(viewContext,"The Item Clicked is: "+getPosition(), Toast.LENGTH_SHORT).show();

        }


    }


    //Below first we ovverride the method onCreateViewHolder which is called when the ViewHolder is
    //Created, In this method we inflate the item_row.xml layout if the viewType is Type_ITEM or else we inflate header.xml
    // if the viewType is TYPE_HEADER
    // and pass it to the view holder

    @Override
    public MainActivityDrawerRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_main_drawer_list_item,parent,false); //Inflating the layout

            ViewHolder vhItem = new ViewHolder(v,viewType,context); //Creating ViewHolder and passing the object of type view

            return vhItem; // Returning the created object

            //inflate your layout and pass it to view holder

        } else if (viewType == TYPE_HEADER) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_main_drawer_header_view,parent,false); //Inflating the layout

            ViewHolder vhHeader = new ViewHolder(v,viewType,context); //Creating ViewHolder and passing the object of type view

            headerViewHolder = vhHeader;

            return vhHeader; //returning the object created


        }
        return null;

    }

    //Next we override a method which is called when the item in a row is needed to be displayed, here the int position
    // Tells us item at which position is being constructed to be displayed and the holder id of the holder object tell us
    // which view type is being created 1 for item row
    @Override
    public void onBindViewHolder(MainActivityDrawerRecyclerViewAdapter.ViewHolder holder, int position) {
        if(holder.Holderid ==1) {                              // as the list view is going to be called after the header view so we decrement the
            // position by 1 and pass it to the holder while setting the text and image
            holder.textViewTitle.setText(mNavTitles[position - 1]); // Setting the Text with the array of our Titles
            holder.imageViewIcon.setImageResource(mIcons[position -1]);// Settimg the image with array of our icons
        }
        else{

            imageLoader.displayImage(user.profilePictureURL.toString(), holder.imageViewUserProfilePicture, options);
            holder.textViewDrawerUserFullName.setText(user.getFullName());
            holder.textViewDrawerUserUsername.setText("@" + user.username);
        }
    }

    // This method returns the number of items present in the list
    @Override
    public int getItemCount() {
        return mNavTitles.length+1; // the number of items in the list will be +1 the titles including the header view.
    }


    // Witht the following method we check what type of view is being passed
    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    public void drawerIsOpen(){

        ObjectAnimator colorizer = ObjectAnimator.ofFloat(MainActivityDrawerRecyclerViewAdapter.this, "saturation", 0, 1);
        colorizer.setDuration(MainActivity.ANIM_DURATION);
        colorizer.start();

    }

    public void drawerIsClosed(){

        ObjectAnimator colorizer = ObjectAnimator.ofFloat(MainActivityDrawerRecyclerViewAdapter.this, "saturation", 1, 0);
        colorizer.setDuration(0);
        colorizer.start();

    }

    private void setSaturation(float value) {
        graymatrix.setSaturation(value);
        ColorMatrixColorFilter colorizerFilter = new ColorMatrixColorFilter(graymatrix);
        headerViewHolder.imageViewUserProfilePicture.setColorFilter(colorizerFilter);
    }



}
