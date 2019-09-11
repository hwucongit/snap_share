package com.teamandroid.snapshare.ui.main.profile;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.teamandroid.snapshare.data.model.Post;
import com.teamandroid.snapshare.data.model.User;
import com.teamandroid.snapshare.data.repository.FirebaseStorageRepository;
import com.teamandroid.snapshare.data.repository.FirestoreRepository;
import com.teamandroid.snapshare.utils.Constants;

import java.util.List;

public class ProfileViewModel extends ViewModel {
    private final String TAG = ProfileViewModel.class.getName();
    private MutableLiveData<List<Post>> mPosts = new MutableLiveData<>();
    private MutableLiveData<User> mProfileUser = new MutableLiveData<>();
    private FirestoreRepository mFirestoreRepository = FirestoreRepository.getInstance();
    private FirebaseStorageRepository mFirebaseStorageRepository =
        FirebaseStorageRepository.getInstance();
    private MutableLiveData<Boolean> mIsFollowed = new MutableLiveData<>();
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private MutableLiveData<Integer> mTotalPost = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsChangeAvatarSuccessful = new MutableLiveData<>();

    public ProfileViewModel() {
    }

    public boolean displayingCurrentUser(Bundle dataReceived) {
        return dataReceived.getBoolean(Constants.PROFILE_USER_TAG);
    }

    public MutableLiveData<List<Post>> getUserPosts() {
        if (mPosts == null) {
            mPosts = new MutableLiveData<>();
        }
        return mPosts;
    }

    public MutableLiveData<Integer> getTotalPost() {
        return mTotalPost;
    }

    public void getPosts(String userId) {
        mFirestoreRepository.getPostsOf(userId, new FirestoreRepository.Callback<List<Post>>() {
            @Override
            public void onSuccess(List<Post> result) {
                mPosts.setValue(result);
                if (result != null) {
                    mPosts.setValue(result);
                    mTotalPost.setValue(result.size());
                }
            }

            @Override
            public void onFailure(Exception e) {
            }
        });
    }

    public MutableLiveData<Boolean> getIsFollowed() {
        return mIsFollowed;
    }

    public void checkFollowingState(String currentUserId, String userId) {
        mFirestore.collection(Constants.COLLECTION_FOLLOW)
            .document(currentUserId)
            .collection(Constants.FOLLOW_FIELD_FOLLOWING)
            .document(userId)
            .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot,
                                    @Nullable FirebaseFirestoreException e) {
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        mIsFollowed.postValue(true);
                    } else
                        mIsFollowed.postValue(false);
                }
            });
    }

    public void onThumbnailClick(Integer index) {
    }

    public void changeAvatarImage(Uri uri, final String userId) {
        mFirebaseStorageRepository.setAvatarImage(uri, userId,
            new FirebaseStorageRepository.Callback<Uri>() {
                @Override
                public void onSuccess(Uri result) {
                    changeAvatarToFirestore(userId,result.toString());
                }

                @Override
                public void onFailure(Exception e) {
                }
            });
    }

    private void changeAvatarToFirestore(String userId, String imageUrl) {
        mFirestoreRepository.setAvatar(userId, imageUrl, new FirestoreRepository.Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                mIsChangeAvatarSuccessful.postValue(true);
            }

            @Override
            public void onFailure(Exception e) {
                mIsChangeAvatarSuccessful.postValue(false);
            }
        });

    }

    public void followUser(String currentUserId, String userId) {
        mFirestoreRepository.followUser(currentUserId, userId);
    }

    public void unFollowUser(String currentUserId, String userId) {
        mFirestoreRepository.unFollowUser(currentUserId, userId);
    }
}
