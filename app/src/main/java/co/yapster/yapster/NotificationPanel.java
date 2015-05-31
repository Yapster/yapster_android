package co.yapster.yapster;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by gurkarangulati on 3/16/15.
 */

class NotificationPanelViewHolder{
    ImageView imageViewYapImage;
    LinearLayout linearLayoutPlayerPlayingYapInfo;
    TextView textViewYapTitle;
    TextView textViewYapUser;
    ImageButton imageButtonPlayerPlayAndPause;
    ImageButton imageButtonPlayerNext;
}

public class NotificationPanel {
    private static NotificationPanel _notificationPanel;
    private Context parent;
    private NotificationManager nManager;
    private NotificationCompat.Builder nBuilder;
    private RemoteViews remoteView;
    private float mScreenHeight;
    private float mScreenWidth;
    private NotificationPanelViewHolder notificationPanelViewHolder;
    private User user;
    private Yap yap;
    private int notifyID = 0;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private Drawable fallback;

    public NotificationPanel(Context parent, User user, Yap yap) {
        // TODO Auto-generated constructor stub
        this.parent = parent;
        this.user = user;
        this.yap = yap;
        fallback = parent.getResources().getDrawable( R.drawable.default1 );
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).showImageForEmptyUri(fallback)
                .showImageOnFail(fallback)
                .showImageOnLoading(fallback).build();

        nBuilder = new NotificationCompat.Builder(parent)
                .setContentTitle("Yapster")
                .setSmallIcon(R.drawable.ic_launcher)
                .setOngoing(true);

        remoteView = new RemoteViews(parent.getPackageName(), R.layout.notification_panel);
        DisplayMetrics displayMetrics = parent.getResources().getDisplayMetrics();
        mScreenHeight = displayMetrics.heightPixels / displayMetrics.density;
        mScreenWidth = displayMetrics.widthPixels / displayMetrics.density;

        remoteView.setTextViewText(R.id.textViewYapTitle, yap.title);
        remoteView.setTextViewText(R.id.textViewYapTitle, yap.title);

        //set the button listeners
        setListeners(remoteView);
        nBuilder.setContent(remoteView);

        nManager = (NotificationManager) parent.getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(notifyID,nBuilder.build());
    }

    public static NotificationPanel getInstance(Context parent, User user, Yap yap){

        if (_notificationPanel == null){
            _notificationPanel = new NotificationPanel(parent,user, yap);
        }

        return _notificationPanel;
    }

    public static NotificationPanel getInstance(){
        return _notificationPanel;
    }

    public void setListeners(RemoteViews view){
        //listener 1
        Intent intentPlayAndPause = new Intent(parent,NotificationReturnSlot.class);
        intentPlayAndPause.putExtra("buttonPressed", "playAndPause");
        intentPlayAndPause.putExtra("user",user);
        PendingIntent imageButtonPlayerPlayAndPausePendingIntent = PendingIntent.getActivity(parent, 0, intentPlayAndPause, 0);
        view.setOnClickPendingIntent(R.id.imageButtonPlayerPlayAndPause, imageButtonPlayerPlayAndPausePendingIntent);

        //listener 2
        Intent intentNext = new Intent(parent, NotificationReturnSlot.class);
        intentNext.putExtra("buttonPressed", "next");
        intentNext.putExtra("user",user);
        PendingIntent btn2 = PendingIntent.getActivity(parent, 1, intentNext, 0);
        view.setOnClickPendingIntent(R.id.imageButtonPlayerNext, btn2);

        //listener 3
        Intent intentX = new Intent(parent, NotificationReturnSlot.class);
        intentX.putExtra("buttonPressed", "X");
        intentX.putExtra("user",user);
        PendingIntent btn3 = PendingIntent.getActivity(parent, 2, intentX, 0);
        view.setOnClickPendingIntent(R.id.imageButtonX, btn3);
    }

    public void notificationCancel() {
        nManager.cancelAll();
    }

    public void updateView(Yap yap){
        this.yap = yap;
        remoteView.setTextViewText(R.id.textViewYapTitle, yap.title);
        remoteView.setTextViewText(R.id.textViewYapTitle, yap.title);
        updateNotification();
    }

    public void playerPause(){
        Bitmap bitmapPause = BitmapFactory.decodeResource(parent.getResources(),
                R.drawable.ic_player_play_black_no_circle_no_trim);
        remoteView.setImageViewBitmap(R.id.imageButtonPlayerPlayAndPause, bitmapPause);
        updateNotification();
    }

    public void playerPlay(){
        Bitmap bitmapPause = BitmapFactory.decodeResource(parent.getResources(),
                R.drawable.ic_player_pause_black_no_circle_no_trim);
        remoteView.setImageViewBitmap(R.id.imageButtonPlayerPlayAndPause, bitmapPause);
        updateNotification();
    }

    public void updateNotification(){
        nBuilder.setContent(remoteView);
        nManager.notify(notifyID,nBuilder.build());

    }
}
