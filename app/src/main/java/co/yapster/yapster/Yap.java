package co.yapster.yapster;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.apache.commons.lang3.time.DateUtils;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by gurkarangulati on 1/21/15.
 */
public class Yap extends Object implements Parcelable {
    Integer id;
    String title;
    String description;
    String picturePath;
    URL picturePathURL;
    Integer length;
    String audioPath;
    URL url;
    URL audioPathURL;
    String dateCreated;
    Integer order;
    Integer userID;
    String userUsername;
    String userFirstName;
    String userLastName;
    String userProfilePicturePath;
    Boolean yapUserSubscribedByViewer;
    Boolean yapLibrarySubscribedByViewer;
    AsyncTask<Void,Void,Void> yapURLSAsyncTask;


    public Yap(JsonObject yap_json_object, Integer order)
    {
        this.order = order;
        JsonElement yap_user_subscribed_by_viewer_json_element = yap_json_object.get("yap_user_subscribed_by_viewer");
        yapUserSubscribedByViewer = null;
        if (yap_user_subscribed_by_viewer_json_element.isJsonNull() == false)
        {
            yapUserSubscribedByViewer = yap_json_object.get("yap_user_subscribed_by_viewer").getAsBoolean();
        }


        String date_created_json_string = yap_json_object.get("date_created").getAsString();
        SimpleDateFormat dateTimeFormatterToDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = null;
        dateCreated = null;
        try
        {
            date = dateTimeFormatterToDate.parse(date_created_json_string);
            SimpleDateFormat dateFormatterToDate = new SimpleDateFormat(
                    "MMM. dd, yyyy");
            dateCreated = (String) dateFormatterToDate
                    .format(date);
        } catch (java.text.ParseException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        title = yap_json_object.get("yap_info").getAsJsonObject().get("title").getAsString();
        JsonElement description_json_element = yap_json_object.get("yap_info").getAsJsonObject().get("description");
        description = null;
        if (description_json_element.isJsonNull() == false)
        {
            description = yap_json_object.get("yap_info").getAsJsonObject().get("description").getAsString();
        }

        id = yap_json_object.get("yap_info").getAsJsonObject().get("id").getAsInt();
        length = yap_json_object.get("yap_info").getAsJsonObject().get("length").getAsInt();
        audioPath = yap_json_object.get("yap_info").getAsJsonObject().get("audio_path").getAsString();
        JsonElement picture_path_json_element = yap_json_object.get("yap_info").getAsJsonObject().get("picture_path");
        picturePath = null;
        if (picture_path_json_element.isJsonNull() == false)
        {
            picturePath = yap_json_object.get("yap_info").getAsJsonObject().get("picture_path").getAsString();
        }

        String user_id_string = yap_json_object.get("yap_info").getAsJsonObject().get("user").getAsJsonObject().get("id").toString();
        userID = Integer.parseInt(user_id_string);
        userUsername = yap_json_object.get("yap_info").getAsJsonObject().get("user").getAsJsonObject().get("username").getAsString();
        userFirstName = yap_json_object.get("yap_info").getAsJsonObject().get("user").getAsJsonObject().get("first_name").getAsString();
        userLastName = yap_json_object.get("yap_info").getAsJsonObject().get("user").getAsJsonObject().get("last_name").getAsString();
        userProfilePicturePath = yap_json_object.get("yap_info").getAsJsonObject().get("user").getAsJsonObject().get("profile_picture_path").getAsString();

        JsonElement url_json_element = yap_json_object.get("yap_info").getAsJsonObject().get("url");
        url = null;
        if (url_json_element.isJsonNull() == false)
        {
            try {
                url = new URL(yap_json_object.get("yap_info").getAsJsonObject().get("url").getAsString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        boolean hasPictureFlag;
        if (picturePath == null) {
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
                    picturePath);
            generatePresignedURLRequest.setMethod(HttpMethod.GET);
            generatePresignedURLRequest.setExpiration(dateForSigningS3Object);
            picturePathURL = amazonS3Client
                    .generatePresignedUrl(generatePresignedURLRequest);
            GeneratePresignedUrlRequest generatePresignedURLRequest2 = new GeneratePresignedUrlRequest(
                    "yapster",
                    audioPath);
            generatePresignedURLRequest2.setMethod(HttpMethod.GET);
            generatePresignedURLRequest2.setExpiration(dateForSigningS3Object);
            audioPathURL = amazonS3Client
                    .generatePresignedUrl(generatePresignedURLRequest2);
        }
    }

    protected Yap(Parcel in) {
        id = in.readByte() == 0x00 ? null : in.readInt();
        title = in.readString();
        description = in.readString();
        picturePath = in.readString();
        picturePathURL = (URL) in.readValue(URL.class.getClassLoader());
        length = in.readByte() == 0x00 ? null : in.readInt();
        audioPath = in.readString();
        url = (URL) in.readValue(URL.class.getClassLoader());
        audioPathURL = (URL) in.readValue(URL.class.getClassLoader());
        dateCreated = in.readString();
        order = in.readByte() == 0x00 ? null : in.readInt();
        userID = in.readByte() == 0x00 ? null : in.readInt();
        userUsername = in.readString();
        userFirstName = in.readString();
        userLastName = in.readString();
        userProfilePicturePath = in.readString();
        byte yapUserSubscribedByViewerVal = in.readByte();
        yapUserSubscribedByViewer = yapUserSubscribedByViewerVal == 0x02 ? null : yapUserSubscribedByViewerVal != 0x00;
        byte yapLibrarySubscribedByViewerVal = in.readByte();
        yapLibrarySubscribedByViewer = yapLibrarySubscribedByViewerVal == 0x02 ? null : yapLibrarySubscribedByViewerVal != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(id);
        }
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(picturePath);
        dest.writeValue(picturePathURL);
        if (length == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(length);
        }
        dest.writeString(audioPath);
        dest.writeValue(url);
        dest.writeValue(audioPathURL);
        dest.writeString(dateCreated);
        if (order == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(order);
        }
        if (userID == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(userID);
        }
        dest.writeString(userUsername);
        dest.writeString(userFirstName);
        dest.writeString(userLastName);
        dest.writeString(userProfilePicturePath);
        if (yapUserSubscribedByViewer == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (yapUserSubscribedByViewer ? 0x01 : 0x00));
        }
        if (yapLibrarySubscribedByViewer == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (yapLibrarySubscribedByViewer ? 0x01 : 0x00));
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Yap> CREATOR = new Parcelable.Creator<Yap>() {
        @Override
        public Yap createFromParcel(Parcel in) {
            return new Yap(in);
        }

        @Override
        public Yap[] newArray(int size) {
            return new Yap[size];
        }
    };

    public String getYapUserFullName(){
        return userFirstName + " " + userLastName;
    }

}
