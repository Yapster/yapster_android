package co.yapster.yapster;

import android.app.Activity;
import android.os.AsyncTask;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;

import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;

/**
 * Created by gurkarangulati on 3/13/15.
 */
public class AWS extends Object {
    private static AWS _aws;
    private CognitoCachingCredentialsProvider cognitoProvider;
    private AmazonS3Client amazonS3Client;
    private Activity activity;
    private AsyncTask<Void,Void,Void> awsAsyncTask;

    private AWS(Activity activity) {
        this.activity = activity;
        awsAsyncTask = new AWSAsyncTask();
        awsAsyncTask.execute();
    }

    public static AWS getInstance(Activity activity) {

        if (_aws == null) {
            _aws = new AWS(activity);
        }
        return _aws;

    }

    public static AWS getInstance() {
        return _aws;
    }


    public CognitoCachingCredentialsProvider getCognitoProvider() {
        return cognitoProvider;
    }

    public AmazonS3Client getAmazonS3Client() {
        return amazonS3Client;
    }

    class AWSAsyncTask extends
            AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            cognitoProvider = new CognitoCachingCredentialsProvider(
                    activity.getBaseContext(), // get the context for the
                    "100149822649",
                    "us-east-1:c791e7de-ead0-4117-a964-94486e3ebee9",
                    "arn:aws:iam::100149822649:role/Cognito_YapsterUnauth_DefaultRole",
                    "arn:aws:iam::100149822649:role/Cognito_YapsterAuth_DefaultRole",
                    Regions.US_EAST_1);
            cognitoProvider.refresh();
            amazonS3Client = new AmazonS3Client(cognitoProvider);
            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }

    }


}
