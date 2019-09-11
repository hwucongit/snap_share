package com.teamandroid.snapshare.data.repository;

import android.net.Uri;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.teamandroid.snapshare.utils.Constants;

import androidx.annotation.NonNull;

public class FirebaseStorageRepository {
    public static FirebaseStorageRepository sInstance;
    private FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
    private StorageReference mStorageRefence;

    public static synchronized FirebaseStorageRepository getInstance() {
        if (sInstance == null) {
            sInstance = new FirebaseStorageRepository();
        }
        return sInstance;
    }

    public void addPostImage(Uri uri, String userId, final Callback<Uri> callback) {
        mStorageRefence = mFirebaseStorage
            .getReference(Constants.FOLDER_POST_IMAGE)
            .child(userId + Timestamp.now().toDate().toString());
        UploadTask imageTask = mStorageRefence.putFile(uri);
        Task<Uri> urlTask = imageTask.continueWithTask(
            new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task)
                    throws Exception {
                    if (!task.isSuccessful())
                        throw task.getException();
                    return mStorageRefence.getDownloadUrl();
                }
            })
            .addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        callback.onSuccess(task.getResult());
                    }
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    callback.onFailure(e);
                }
            });
    }

    public void setAvatarImage(Uri uri, String userId, final Callback<Uri> callback) {
        mStorageRefence = mFirebaseStorage
            .getReference(Constants.FOLDER_AVATAR)
            .child(userId + Constants.KEY_AVATAR);
        UploadTask imageTask = mStorageRefence.putFile(uri);
        Task<Uri> urlTask = imageTask.continueWithTask(
            new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task)
                    throws Exception {
                    if (!task.isSuccessful())
                        throw task.getException();
                    return mStorageRefence.getDownloadUrl();
                }
            })
            .addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        callback.onSuccess(task.getResult());
                    }
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    callback.onFailure(e);
                }
            });
    }

    public interface Callback<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }
}
