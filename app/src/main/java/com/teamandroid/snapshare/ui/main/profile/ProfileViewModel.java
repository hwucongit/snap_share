package com.teamandroid.snapshare.ui.main.profile;

import android.os.Bundle;
import android.util.Log;

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
import com.teamandroid.snapshare.data.repository.FirestoreRepository;
import com.teamandroid.snapshare.utils.Constants;

import java.util.List;

public class ProfileViewModel extends ViewModel {
    private final String TAG = ProfileViewModel.class.getName();
    private MutableLiveData<List<Post>> mPosts = new MutableLiveData<>();
    private MutableLiveData<User> mProfileUser = new MutableLiveData<>();
    private FirestoreRepository mFirestoreRepository = FirestoreRepository.getInstance();
    private MutableLiveData<Boolean> mIsFollowed = new MutableLiveData<>();
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    public ProfileViewModel() {
//        getPosts(mProfileUser.getValue().getId());
    }

    public boolean displayingCurrentUser(Bundle dataReceived) {
        return dataReceived.getBoolean(Constants.PROFILE_USER_TAG);
    }

    public void setProfileUser(GoogleSignInAccount account) {
        String id = account.getId();
        String username = account.getGivenName();
        String fullName = account.getDisplayName();
        String avatarUrl = account.getPhotoUrl().toString();
        mProfileUser.setValue(new User(id, username, fullName, avatarUrl));
    }

    public void setProfileUser(Bundle args) {
        String id = args.getString(Constants.PROFILE_USER_ID);
        String username = args.getString(Constants.PROFILE_USER_GIVEN_NAME);
        String fullName = args.getString(Constants.PROFILE_USER_DISPLAY_NAME);
        String avatarUrl = args.getString(Constants.PROFILE_USER_AVATAR);
        mProfileUser.setValue(new User(id, username, fullName, avatarUrl));
    }

    public MutableLiveData<List<Post>> getUserPosts() {
        if (mPosts == null) {
            mPosts = new MutableLiveData<>();
        }
        return mPosts;
    }

    public void getPosts(String userId) {
        mFirestoreRepository.getPostsOf(userId, new FirestoreRepository.Callback<List<Post>>() {
            @Override
            public void onSuccess(List<Post> result) {
                mPosts.setValue(result);
                Log.d(TAG, result.size() + "hiep");
            }

            @Override
            public void onFailure(Exception e) {
                //TODO handle when fetching fail
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

    public void followUser(String currentUserId, String userId) {
        mFirestoreRepository.followUser(currentUserId,userId);
    }

    public void unFollowUser(String currentUserId, String userId) {
        mFirestoreRepository.unFollowUser(currentUserId,userId);
    }
}
