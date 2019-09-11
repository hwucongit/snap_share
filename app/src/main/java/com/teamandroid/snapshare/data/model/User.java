package com.teamandroid.snapshare.data.model;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    public static final String COLLECTION = "Users";
    public static final String FIELD_ID = "id";
    public static final String FIELD_USERNAME = "username";
    public static final String FIELD_FULL_NAME = "fullName";
    public static final String FIELD_AVATAR_URL = "avatarUrl";
    private String mId;
    private String mUsername;
    private String mFullName;
    private String mAvatarUrl;

    public User() {
    }

    public User(String id, String username, String fullName, String avatarUrl) {
        this.mId = id;
        this.mUsername = username;
        this.mFullName = fullName;
        this.mAvatarUrl = avatarUrl;
    }

    public User(String username, String fullName, String avatarUrl) {
        this.mUsername = username;
        this.mFullName = fullName;
        this.mAvatarUrl = avatarUrl;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        this.mUsername = username;
    }

    public String getFullName() {
        return mFullName;
    }

    public void setFullName(String fullName) {
        this.mFullName = fullName;
    }

    public String getAvatarUrl() {
        return mAvatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.mAvatarUrl = avatarUrl;
    }

    @BindingAdapter("avatarUrl")
    public static void loadUserAvatar(ImageView imageView, String imageUrl) {
        Glide.with(imageView).load(imageUrl).into(imageView);
    }

    public JSONObject toJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(FIELD_ID, mId);
        jsonObject.put(FIELD_USERNAME, mUsername);
        jsonObject.put(FIELD_FULL_NAME, mFullName);
        jsonObject.put(FIELD_AVATAR_URL, mAvatarUrl);
        return jsonObject;
    }
}
