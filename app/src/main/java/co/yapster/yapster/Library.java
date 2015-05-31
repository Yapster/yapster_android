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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.apache.commons.lang3.time.DateUtils;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by gurkarangulati on 1/21/15.
 */
public class Library extends Object implements Parcelable {
    User user;
    Integer order;
    Integer id;
    String title;
    String description;
    String picturePath;
    URL picturePathURL;
    Integer subscriberUsersCount;
    String color_1;
    String color_2;
    String color_3;
    URL url;
    Boolean viewingUserSubscribedToLibrary;
    Boolean isReverseChronologicalOrder;
    Date dateCreated;
    ArrayList<Yap> yaps;
    Integer page = 0;
    Boolean hasLoadedAll = false;
    AsyncTask<Void,Void,Void> libraryURLSYapsAsyncTask;


    public Library(JsonObject library_json_object,Integer order) {
        this.order = order;
        id = library_json_object.get("id").getAsInt();
        title = library_json_object.get("title").getAsString();
        JsonElement description_json_element = library_json_object.get("description");
        if (description_json_element.isJsonNull() == false) {
            description = library_json_object.get("description").getAsString();
        }
        JsonElement picture_path_json_element = library_json_object.get("picture_path");
        if (picture_path_json_element.isJsonNull() == false) {
            picturePath = library_json_object.get("picture_path").getAsString();
        }
        isReverseChronologicalOrder = library_json_object.get("is_reverse_chronological_order").getAsBoolean();
        JsonElement color_1_json_element = library_json_object.get("color_1");
        color_1 = null;
        if (color_1_json_element.isJsonNull() == false) {
            color_1 = library_json_object.get("color_1").getAsString();
        }
        JsonElement color_2_json_element = library_json_object.get("color_2");
        color_2 = null;
        if (color_2_json_element.isJsonNull() == false) {
            color_2 = library_json_object.get("color_2").getAsString();
        }
        JsonElement color_3_json_element = library_json_object.get("color_3");
        color_3 = null;
        if (color_3_json_element.isJsonNull() == false) {
            color_3 = library_json_object.get("color_3").getAsString();
        }
        viewingUserSubscribedToLibrary = library_json_object.get("viewing_user_subscribed_to_library").getAsBoolean();
        subscriberUsersCount = library_json_object.get("subscriber_users_count").getAsInt();
        JsonArray yapsJsonArray = library_json_object.get("yaps").getAsJsonArray();
        yaps = new ArrayList<Yap>();
        for (Integer i = 0; i < yapsJsonArray.size(); i++) {
            JsonObject library_yap_json_object = yapsJsonArray.get(i).getAsJsonObject();
            Yap yap = new Yap(library_yap_json_object,i);
            yaps.add(i,yap);
        }
        JsonElement url_json_element = library_json_object.get("url");
        if (url_json_element.isJsonNull() == false)
        {
            try {
                url = new URL(library_json_object.get("url").getAsString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        this.order = order;
        JsonObject user_json_object = library_json_object.get("user").getAsJsonObject();
        user = new User(false,user_json_object);
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

    }

    protected Library(Parcel in) {
        user = (User) in.readValue(User.class.getClassLoader());
        order = in.readByte() == 0x00 ? null : in.readInt();
        id = in.readByte() == 0x00 ? null : in.readInt();
        title = in.readString();
        description = in.readString();
        picturePath = in.readString();
        picturePathURL = (URL) in.readValue(URL.class.getClassLoader());
        subscriberUsersCount = in.readByte() == 0x00 ? null : in.readInt();
        color_1 = in.readString();
        color_2 = in.readString();
        color_3 = in.readString();
        url = (URL) in.readValue(URL.class.getClassLoader());
        byte viewingUserSubscribedToLibraryVal = in.readByte();
        viewingUserSubscribedToLibrary = viewingUserSubscribedToLibraryVal == 0x02 ? null : viewingUserSubscribedToLibraryVal != 0x00;
        byte isReverseChronologicalOrderVal = in.readByte();
        isReverseChronologicalOrder = isReverseChronologicalOrderVal == 0x02 ? null : isReverseChronologicalOrderVal != 0x00;
        long tmpDateCreated = in.readLong();
        dateCreated = tmpDateCreated != -1 ? new Date(tmpDateCreated) : null;
        if (in.readByte() == 0x01) {
            yaps = new ArrayList<Yap>();
            in.readList(yaps, Yap.class.getClassLoader());
        } else {
            yaps = null;
        }
        page = in.readByte() == 0x00 ? null : in.readInt();
        byte hasLoadedAllVal = in.readByte();
        hasLoadedAll = hasLoadedAllVal == 0x02 ? null : hasLoadedAllVal != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(user);
        if (order == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(order);
        }
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
        if (subscriberUsersCount == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(subscriberUsersCount);
        }
        dest.writeString(color_1);
        dest.writeString(color_2);
        dest.writeString(color_3);
        dest.writeValue(url);
        if (viewingUserSubscribedToLibrary == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (viewingUserSubscribedToLibrary ? 0x01 : 0x00));
        }
        if (isReverseChronologicalOrder == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (isReverseChronologicalOrder ? 0x01 : 0x00));
        }
        dest.writeLong(dateCreated != null ? dateCreated.getTime() : -1L);
        if (yaps == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(yaps);
        }
        if (page == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(page);
        }
        if (hasLoadedAll == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (hasLoadedAll ? 0x01 : 0x00));
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Library> CREATOR = new Parcelable.Creator<Library>() {
        @Override
        public Library createFromParcel(Parcel in) {
            return new Library(in);
        }

        @Override
        public Library[] newArray(int size) {
            return new Library[size];
        }
    };


    public URL getPicturePathURL() {
        return picturePathURL;
    }

    public void setPicturePathURL(URL picturePathURL) {
        this.picturePathURL = picturePathURL;
    }


}
