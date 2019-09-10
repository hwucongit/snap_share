package com.teamandroid.snapshare.data.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;

public class Post {
    public static final String POST_COLLECTION = "Posts";
    public static final String FIELD_AUTHOR = "author";
    public static final String FIELD_USER_ID = "userId";
    public static final String FIELD_AVATAR_URL = "avatarUrl";
    public static final String FIELD_IMAGE_URL = "imageUrl";
    public static final String FIELD_CAPTION = "caption";
    public static final String FIELD_LIKE_COUNT = "likeCount";
    public static final String FIELD_CREATED_AT = "createdAt";
    public static final String FIELD_LIKES = "likes";
    private String mId;
    @PropertyName(FIELD_AUTHOR)
    private String mAuthor;
    @PropertyName(FIELD_USER_ID)
    private String mUserId;
    @PropertyName(FIELD_AVATAR_URL)
    private String mAvatarUrl;
    @PropertyName(FIELD_IMAGE_URL)
    private String mImageUrl;
    @PropertyName(FIELD_CAPTION)
    private String mCaption;
    @PropertyName(FIELD_LIKE_COUNT)
    private Integer mLikeCount;
    @PropertyName(FIELD_CREATED_AT)
    private Timestamp mCreatedAt;
    @PropertyName(FIELD_LIKES)
    private ArrayList<String> mLikes;

    public Post() {
    }

    public String getId() {
        return mId;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getUserId() {
        return mUserId;
    }

    public String getAvatarUrl() {
        return mAvatarUrl;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getCaption() {
        return mCaption;
    }

    public Integer getLikeCount() {
        return mLikeCount;
    }

    public Timestamp getCreatedAt() {
        return mCreatedAt;
    }

    public ArrayList<String> getLikes() {
        return mLikes;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public void setAuthor(String mAuthor) {
        this.mAuthor = mAuthor;
    }

    public void setUserId(String mUserId) {
        this.mUserId = mUserId;
    }

    public void setAvatarUrl(String mAvatarUrl) {
        this.mAvatarUrl = mAvatarUrl;
    }

    public void setImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public void setCaption(String mCaption) {
        this.mCaption = mCaption;
    }


    public void setLikeCount(Integer mLikeCount) {
        this.mLikeCount = mLikeCount;
    }

    public void setCreatedAt(Timestamp mCreatedAt) {
        this.mCreatedAt = mCreatedAt;
    }

    public void setLikes(ArrayList<String> likes) {
        mLikes = likes;
    }
}
