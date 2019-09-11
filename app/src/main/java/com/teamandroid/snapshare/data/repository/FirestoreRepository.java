package com.teamandroid.snapshare.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.teamandroid.snapshare.data.model.Post;
import com.teamandroid.snapshare.utils.Constants;
import com.teamandroid.snapshare.data.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreRepository {
    public static FirestoreRepository sInstance;
    private final FirebaseFirestore mFirestore;

    private FirestoreRepository() {
        mFirestore = FirebaseFirestore.getInstance();
    }

    public static FirestoreRepository getInstance() {
        if (sInstance == null) {
            synchronized (FirestoreRepository.class) {
                if (sInstance == null) {
                    sInstance = new FirestoreRepository();
                }
            }
        }
        return sInstance;
    }

    public void getAllPosts(final Callback<List<Post>> callback) {

        mFirestore.collection(Post.POST_COLLECTION).orderBy(Post.FIELD_CREATED_AT, Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        final List<Post> posts = new ArrayList<>();
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            final Post post = document.toObject(Post.class);
                            post.setId(document.getId());
                            getUserById(post.getUserId(), new Callback<User>() {
                                @Override
                                public void onSuccess(User result) {
                                    if (result != null) {
                                        post.setAuthor(result.getUsername());
                                        Log.d("ahihi",result.getUsername());
                                        post.setAvatarUrl(result.getAvatarUrl());
                                    }
                                    posts.add(post);
                                }

                                @Override
                                public void onFailure(Exception e) {

                                }
                            });
                            posts.add(post);

                        }
                        callback.onSuccess(posts);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailure(e);
                    }
                });
    }


    public void getPostsOf(String userId, final Callback<List<Post>> callback) {

        mFirestore.collection(Post.POST_COLLECTION)
                .whereEqualTo(Post.FIELD_USER_ID, userId)
                .orderBy(Post.FIELD_CREATED_AT,Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Post> posts = new ArrayList<>();
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            final Post post = document.toObject(Post.class);

                            posts.add(post);
                        }
                        callback.onSuccess(posts);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailure(e);
                    }
                });

    }

    public void addPost(Post post, final Callback<Void> callback) {
        mFirestore.collection(Post.POST_COLLECTION).document().set(post)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailure(e);
                    }
                });
    }

    public void updateLike(String id, ArrayList<String> likes, final Callback<Void> callback) {
        mFirestore.collection(Post.POST_COLLECTION).document(id).update(Post.FIELD_LIKES, likes)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailure(e);
                    }
                });
        mFirestore.collection(Post.POST_COLLECTION).document(id).update(Post.FIELD_LIKE_COUNT, likes.size())
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailure(e);
                    }
                });
    }

    public void followUser(String currentUserId, String userId) {
        HashMap<Object,Object> emptyData = new HashMap<>();
        mFirestore.collection(Constants.COLLECTION_FOLLOW)
            .document(currentUserId)
            .collection(Constants.FOLLOW_FIELD_FOLLOWING)
            .document(userId)
            .set(emptyData);

        mFirestore.collection(Constants.COLLECTION_FOLLOW)
            .document(userId)
            .collection(Constants.FOLLOW_FIELD_FOLLOWER)
            .document(currentUserId)
            .set(emptyData);
    }

    public void unFollowUser(String currentUserId, String userId) {
        mFirestore.collection(Constants.COLLECTION_FOLLOW)
            .document(currentUserId)
            .collection(Constants.FOLLOW_FIELD_FOLLOWING)
            .document(userId)
            .delete();

        mFirestore.collection(Constants.COLLECTION_FOLLOW)
            .document(userId)
            .collection(Constants.FOLLOW_FIELD_FOLLOWER)
            .document(currentUserId)
            .delete();
    }
    public void getUserById(String id, final Callback<User> callback) {
        mFirestore.collection(User.COLLECTION).document(id).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        callback.onSuccess(documentSnapshot.toObject(User.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailure(e);
                    }
                });
    }

    public void setAvatar(String userId, String avatarUrl, final Callback<Void> callback) {
        mFirestore.collection(User.COLLECTION)
            .document(userId)
            .update(User.FIELD_AVATAR_URL, avatarUrl)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    callback.onSuccess(null);
                }
            });
    }
    public interface Callback<T> {

        void onSuccess(T result);

        void onFailure(Exception e);
    }
}
