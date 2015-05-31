package co.yapster.yapster;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
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

public class User extends Object implements Parcelable {
    Integer id;
    String firstName;
    String lastName;
    String username;
    String profilePicturePath;
    URL profilePictureURL;
    String profileCroppedPicturePath;
    URL profileCroppedPictureURL;
    String webCoverPicturePath;
    String description;
    String city;
    String usState;
    String usZipCode;
    String country;
    Integer subscriberUsersCount;
    Integer subscribingUsersCount;
    Integer subscribingLibrariesCount;
    Integer librariesCount;
    Boolean viewingUserSubscribedToUser;
    Boolean facebookConnectionFlag;
    Integer facebookID;
    Boolean facebookPageConnectionFlag;
    Integer facebookPageID;
    Boolean twitterConnectionFlag;
    Integer twitterID;
    Integer lastYapUserYapID;
    Boolean isSignedInUser;
    Integer sessionID;
    String phonePathForProfilePicture;
    AsyncTask<Void,Void,Void> userURLSAsyncTask;


    User(Boolean isSignedInUser, JsonObject user_json_object){
        this.isSignedInUser = isSignedInUser;
        id = user_json_object.get("id").getAsInt();
        firstName = user_json_object.get("first_name").getAsString();
        lastName = user_json_object.get("last_name").getAsString();
        username = user_json_object.get("username").getAsString();
        JsonElement profilePicturePathJsonElement = user_json_object.get("profile_picture_path");
        if (profilePicturePathJsonElement.isJsonNull() == false){
            profilePicturePath = user_json_object.get("profile_picture_path").getAsString();
        }
        JsonElement profileCroppedPicturePathJsonElement = user_json_object.get("profile_cropped_picture_path");
        if (profileCroppedPicturePathJsonElement.isJsonNull() == false){
            profileCroppedPicturePath = profileCroppedPicturePathJsonElement.getAsString();
        }
        JsonElement webCoverPicture1PathJsonElement = user_json_object.get("web_cover_picture_1_path");
        if (webCoverPicture1PathJsonElement.isJsonNull() == false){
            webCoverPicturePath = webCoverPicture1PathJsonElement.getAsString();
        }
        JsonElement descriptionJsonElement = user_json_object.get("description");
        if (descriptionJsonElement.isJsonNull() == false){
            description = descriptionJsonElement.getAsString();
        }
        JsonElement usStateJsonElement = user_json_object.get("us_state_name");
        if (usStateJsonElement.isJsonNull() == false){
            usState = usStateJsonElement.getAsString();
        }
        JsonElement usZipCodeJsonElement = user_json_object.get("us_zip_code_name");
        if (usZipCodeJsonElement.isJsonNull() == false){
            usZipCode = usZipCodeJsonElement.getAsString();
        }
        JsonElement countryJsonElement = user_json_object.get("country_name");
        if (countryJsonElement.isJsonNull() == false){
            country = countryJsonElement.getAsString();
        }
        JsonElement subscriberUsersCountJsonElement = user_json_object.get("subscriber_users_count");
        if (subscriberUsersCountJsonElement.isJsonNull() == false){
            subscriberUsersCount = subscriberUsersCountJsonElement.getAsInt();
        }
        JsonElement subscribingUsersCountJsonElement = user_json_object.get("subscribing_users_count");
        if (subscribingUsersCountJsonElement.isJsonNull() == false){
            subscribingUsersCount = subscribingUsersCountJsonElement.getAsInt();
        }
        JsonElement subscribingLibrariesCountJsonElement = user_json_object.get("subscribing_libraries_count");
        if (subscribingLibrariesCountJsonElement.isJsonNull() == false){
            subscribingLibrariesCount = user_json_object.get("subscribing_libraries_count").getAsInt();
        }
        JsonElement subscriberUsersCountNumberJsonElement = user_json_object.get("subscriber_users_count");
        if (subscriberUsersCountNumberJsonElement.isJsonNull() == false){
            subscriberUsersCount = subscriberUsersCountNumberJsonElement.getAsInt();
        }
        JsonElement viewingUserSubscribedToUserJsonElement = user_json_object.get("viewing_user_subscribed_to_user");
        if (viewingUserSubscribedToUserJsonElement.isJsonNull() == false){
            viewingUserSubscribedToUser = viewingUserSubscribedToUserJsonElement.getAsBoolean();
        }
        JsonElement viewingUserIsUserExtraInfoJsonElement = user_json_object.get("viewing_user_is_user_extra_info");
        if (viewingUserIsUserExtraInfoJsonElement.isJsonNull() == false || this.isSignedInUser == true){
            JsonObject viewingUserIsUserExtraInfo = viewingUserIsUserExtraInfoJsonElement.getAsJsonObject();
            JsonElement facebookConnectionFlagJsonElement = viewingUserIsUserExtraInfo.get("facebook_connection_flag");
            if (facebookConnectionFlagJsonElement.isJsonNull() == false){
                facebookConnectionFlag = facebookConnectionFlagJsonElement.getAsBoolean();
            }
            JsonElement facebookIDJsonElement = viewingUserIsUserExtraInfo.get("facebook_account_id");
            if (facebookIDJsonElement.isJsonNull() == false){
                facebookID = facebookIDJsonElement.getAsInt();
            }
            JsonElement facebookPageConnectionFlagJsonElement = viewingUserIsUserExtraInfo.get("facebook_page_connection_flag");
            if (facebookPageConnectionFlagJsonElement.isJsonNull() == false){
                facebookPageConnectionFlag = facebookPageConnectionFlagJsonElement.getAsBoolean();
            }
            JsonElement facebookPageIDJsonElement = viewingUserIsUserExtraInfo.get("facebook_page_id");
            if (facebookPageIDJsonElement.isJsonNull() == false){
                facebookPageID = facebookPageIDJsonElement.getAsInt();
            }
            JsonElement twitterConnectionFlagJsonElement = viewingUserIsUserExtraInfo.get("twitter_connection_flag");
            if (twitterConnectionFlagJsonElement.isJsonNull() == false){
                twitterConnectionFlag = twitterConnectionFlagJsonElement.getAsBoolean();
            }
            JsonElement twitterIDJsonElement = viewingUserIsUserExtraInfo.get("twitter_account_id");
            if (twitterIDJsonElement.isJsonNull() == false){
                twitterID = twitterIDJsonElement.getAsInt();
            }
            JsonElement lastYapUserYapIDJsonElement = viewingUserIsUserExtraInfo.get("last_yap_user_yap_id");
            if (lastYapUserYapIDJsonElement.isJsonNull() == false){
                lastYapUserYapID = lastYapUserYapIDJsonElement.getAsInt();
            }


        }
        boolean hasPictureFlag;
        if (profilePicturePath == null) {
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
                    profilePicturePath);
            generatePresignedURLRequest.setMethod(HttpMethod.GET);
            generatePresignedURLRequest.setExpiration(dateForSigningS3Object);
            profilePictureURL = amazonS3Client
                    .generatePresignedUrl(generatePresignedURLRequest);
        }


    }

    protected User(Parcel in) {
        id = in.readByte() == 0x00 ? null : in.readInt();
        firstName = in.readString();
        lastName = in.readString();
        username = in.readString();
        profilePicturePath = in.readString();
        profilePictureURL = (URL) in.readValue(URL.class.getClassLoader());
        profileCroppedPicturePath = in.readString();
        profileCroppedPictureURL = (URL) in.readValue(URL.class.getClassLoader());
        webCoverPicturePath = in.readString();
        description = in.readString();
        city = in.readString();
        usState = in.readString();
        usZipCode = in.readString();
        country = in.readString();
        subscriberUsersCount = in.readByte() == 0x00 ? null : in.readInt();
        subscribingUsersCount = in.readByte() == 0x00 ? null : in.readInt();
        subscribingLibrariesCount = in.readByte() == 0x00 ? null : in.readInt();
        librariesCount = in.readByte() == 0x00 ? null : in.readInt();
        byte viewingUserSubscribedToUserVal = in.readByte();
        viewingUserSubscribedToUser = viewingUserSubscribedToUserVal == 0x02 ? null : viewingUserSubscribedToUserVal != 0x00;
        byte facebookConnectionFlagVal = in.readByte();
        facebookConnectionFlag = facebookConnectionFlagVal == 0x02 ? null : facebookConnectionFlagVal != 0x00;
        facebookID = in.readByte() == 0x00 ? null : in.readInt();
        byte facebookPageConnectionFlagVal = in.readByte();
        facebookPageConnectionFlag = facebookPageConnectionFlagVal == 0x02 ? null : facebookPageConnectionFlagVal != 0x00;
        facebookPageID = in.readByte() == 0x00 ? null : in.readInt();
        byte twitterConnectionFlagVal = in.readByte();
        twitterConnectionFlag = twitterConnectionFlagVal == 0x02 ? null : twitterConnectionFlagVal != 0x00;
        twitterID = in.readByte() == 0x00 ? null : in.readInt();
        lastYapUserYapID = in.readByte() == 0x00 ? null : in.readInt();
        byte isSignedInUserVal = in.readByte();
        isSignedInUser = isSignedInUserVal == 0x02 ? null : isSignedInUserVal != 0x00;
        sessionID = in.readByte() == 0x00 ? null : in.readInt();
        phonePathForProfilePicture = in.readString();
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
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(username);
        dest.writeString(profilePicturePath);
        dest.writeValue(profilePictureURL);
        dest.writeString(profileCroppedPicturePath);
        dest.writeValue(profileCroppedPictureURL);
        dest.writeString(webCoverPicturePath);
        dest.writeString(description);
        dest.writeString(city);
        dest.writeString(usState);
        dest.writeString(usZipCode);
        dest.writeString(country);
        if (subscriberUsersCount == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(subscriberUsersCount);
        }
        if (subscribingUsersCount == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(subscribingUsersCount);
        }
        if (subscribingLibrariesCount == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(subscribingLibrariesCount);
        }
        if (librariesCount == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(librariesCount);
        }
        if (viewingUserSubscribedToUser == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (viewingUserSubscribedToUser ? 0x01 : 0x00));
        }
        if (facebookConnectionFlag == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (facebookConnectionFlag ? 0x01 : 0x00));
        }
        if (facebookID == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(facebookID);
        }
        if (facebookPageConnectionFlag == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (facebookPageConnectionFlag ? 0x01 : 0x00));
        }
        if (facebookPageID == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(facebookPageID);
        }
        if (twitterConnectionFlag == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (twitterConnectionFlag ? 0x01 : 0x00));
        }
        if (twitterID == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(twitterID);
        }
        if (lastYapUserYapID == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(lastYapUserYapID);
        }
        if (isSignedInUser == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (isSignedInUser ? 0x01 : 0x00));
        }
        if (sessionID == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(sessionID);
        }
        dest.writeString(phonePathForProfilePicture);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getPhonePathForProfilePicture() {
        return phonePathForProfilePicture;
    }

    public void setPhonePathForProfilePicture(String phonePathForProfilePicture) {
        this.phonePathForProfilePicture = phonePathForProfilePicture;
    }

    public String getFullName(){
        return firstName + " " + lastName;
    }

    public URL getProfilePictureURL() {
        return profilePictureURL;
    }

    public void setProfilePictureURL(URL profilePictureURL) {
        this.profilePictureURL = profilePictureURL;
    }

    public URL getProfileCroppedPictureURL() {
        return profileCroppedPictureURL;
    }

    public void setProfileCroppedPictureURL(URL profileCroppedPictureURL) {
        this.profileCroppedPictureURL = profileCroppedPictureURL;
    }

    public Integer getSessionID() {
        return sessionID;
    }

    public void setSessionID(Integer sessionID) {
        this.sessionID = sessionID;
    }

}
