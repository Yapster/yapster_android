package co.yapster.yapster;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by gurkarangulati on 3/16/15.
 */
public class NotificationReturnSlot extends Activity {

    NotificationPanel notificationPanel;
    Player player;
    Bundle intentBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        intentBundle = getIntent().getExtras();
        String stringButtonPressed = (String) intentBundle.get("buttonPressed");
        User user = intentBundle.getParcelable("user");
        notificationPanel = NotificationPanel.getInstance();
        player = Player.getInstance();
        Log.i("NotificationReturnSlot",stringButtonPressed);
        if (stringButtonPressed.equals("playAndPause")) {
            //Your code
            if (player.isPlaying()){
                notificationPanel.playerPause();
                player.pause();
            }else{
                notificationPanel.playerPlay();
                player.start();
            }
        } else if (stringButtonPressed.equals("next")) {
            player.nextYap();
            notificationPanel.updateView(player.getCurrentYap());
        }else if (stringButtonPressed.equals("X")){
            player.stop();
            notificationPanel.notificationCancel();
        }
        finish();
    }
}