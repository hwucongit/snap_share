package com.teamandroid.snapshare.ui.main.home;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.teamandroid.snapshare.R;
import com.teamandroid.snapshare.data.model.Post;
import com.teamandroid.snapshare.data.model.User;
import com.teamandroid.snapshare.data.repository.FirestoreRepository;
import com.teamandroid.snapshare.utils.Helper;

import java.util.ArrayList;

public class PostItemViewModel extends ViewModel {
    public MutableLiveData<String> author = new MutableLiveData<>();
    public MutableLiveData<String> userId = new MutableLiveData<>();
    public MutableLiveData<String> avatarUrl = new MutableLiveData<>();
    public MutableLiveData<String> imageUrl = new MutableLiveData<>();
    public MutableLiveData<String> caption = new MutableLiveData<>();
    public MutableLiveData<Integer> likeCount = new MutableLiveData<>();
    public MutableLiveData<Timestamp> createdAt = new MutableLiveData<>();
    public MutableLiveData<ArrayList<String>> likes = new MutableLiveData<>();
    private Post mPost;
    private FirestoreRepository mFirestoreRepository = FirestoreRepository.getInstance();

    public PostItemViewModel(Post post) {
        mPost = post;
        userId.setValue(mPost.getUserId());
        imageUrl.setValue(mPost.getImageUrl());
        caption.setValue(mPost.getCaption());
        likeCount.setValue(mPost.getLikeCount());
        createdAt.setValue(mPost.getCreatedAt());
        likes.setValue(mPost.getLikes());
        author.setValue(mPost.getAuthor());
        avatarUrl.setValue(mPost.getAvatarUrl());
    }
    public MutableLiveData<Integer> getLikeCount() {
        return likeCount;
    }

    @BindingAdapter({"likeCount"})
    public static void loadLikeCounter(TextView textView, Integer likeCount) {
        textView.setText(String.format(textView.getContext().getString(R.string.like_counter),
                likeCount, likeCount > 1 ? "s" : ""));
    }

    @BindingAdapter({"imageUrl"})
    public static void loadImagePost(ImageView imageView, String imageUrl) {
        Glide.with(imageView).load(imageUrl).into(imageView);
    }

    @BindingAdapter({"avatarUrl"})
    public static void loadAvatar(ImageView imageView, String imageUrl) {
        Glide.with(imageView).load(imageUrl).into(imageView);
    }

    @BindingAdapter({"createdAt"})
    public static void loadTimeCreated(TextView textView, Timestamp createdAt) {
        textView.setText(Helper.getDateFromUnixTime(createdAt));
    }

    public boolean checkLike(String userId) {
        ArrayList<String> likeList = likes.getValue();
        return (likeList != null && likeList.contains(userId));
    }

    public void updateLike(int currentLikeStatus, String userId) {
        ArrayList<String> likeList = likes.getValue();
        if (likeList == null) return;
        if (currentLikeStatus == 1) {
            likeList.remove(userId);
        } else likeList.add(userId);
        if (mPost == null) return;
        mFirestoreRepository.updateLike(mPost.getId(), likeList, new FirestoreRepository.Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                //TODO
            }

            @Override
            public void onFailure(Exception e) {
                //TODO
            }
        });
    }
}
