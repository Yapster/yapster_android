package co.yapster.yapster;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by gurkarangulati on 3/20/15.
 */
public class DownloadImageAsyncTask extends
        AsyncTask<Void, Void, BitmapDrawable> {
    URL asyncURL;

    DownloadImageAsyncTask(URL asyncURL) {
        this.asyncURL = asyncURL;
    }

    @Override
    protected BitmapDrawable doInBackground(Void... params) {
        Bitmap x;

        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) asyncURL.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            connection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream input = null;
        try {
            input = connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        x = BitmapFactory.decodeStream(input);
        return new BitmapDrawable(x);
    }

    @Override
    protected void onPostExecute(BitmapDrawable result) {
        super.onPostExecute(result);


    }
}