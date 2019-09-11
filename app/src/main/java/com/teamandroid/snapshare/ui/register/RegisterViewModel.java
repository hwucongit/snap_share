package com.teamandroid.snapshare.ui.register;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.teamandroid.snapshare.BuildConfig;
import com.teamandroid.snapshare.data.local.AppPreferencesHelper;
import com.teamandroid.snapshare.data.model.User;
import com.teamandroid.snapshare.data.model.UserRegister;
import com.teamandroid.snapshare.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterViewModel extends ViewModel {
    public MutableLiveData<String> username = new MutableLiveData<>();
    public MutableLiveData<String> fullName = new MutableLiveData<>();
    public MutableLiveData<String> email = new MutableLiveData<>();
    public MutableLiveData<String> password = new MutableLiveData<>();
    public MutableLiveData<String> confirmPassword = new MutableLiveData<>();
    public MutableLiveData<UserRegister> userRegister;
    private final String TAG = RegisterViewModel.class.getSimpleName();
    private MutableLiveData<Boolean> mIsLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsRegisterSuccessful = new MutableLiveData<>();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private Client mClient = new Client(BuildConfig.ALGOLIA_APP_ID, BuildConfig.ALGOLIA_ADMIN_API_KEY);
    private Index mIndex = mClient.getIndex(Constants.AlGOLIA_INDEX);

    public MutableLiveData<Boolean> getIsLoading() {
        return mIsLoading;
    }

    public MutableLiveData<Boolean> getIsRegisterSuccessful() {
        return mIsRegisterSuccessful;
    }

    public MutableLiveData<UserRegister> getUserRegister() {
        if (userRegister == null) {
            userRegister = new MutableLiveData<>();
        }
        return userRegister;
    }

    public void onClickRegisterButton(View view) {
        UserRegister user = new UserRegister(username.getValue(),
                fullName.getValue(), email.getValue(), password.getValue(), confirmPassword.getValue());
        userRegister.setValue(user);
    }

    public void register(final UserRegister userRegister) {
        mIsLoading.postValue(true);
        mAuth.createUserWithEmailAndPassword(userRegister.getEmail(), userRegister.getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            final User user = new User(
                                    currentUser.getUid(), userRegister.getUsername(),
                                    userRegister.getFullName(), Constants.DEFAULT_AVATAR_URL);
                            mFirestore
                                    .collection(User.COLLECTION)
                                    .document(currentUser.getUid())
                                    .set(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            mIsRegisterSuccessful.postValue(true);
                                            mIsLoading.postValue(false);
                                            try {
                                                mIndex.addObjectAsync(user.toJson(), new CompletionHandler() {
                                                    @Override
                                                    public void requestCompleted(@Nullable JSONObject jsonObject, @Nullable AlgoliaException e) {
                                                        if (e != null) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    })
                            ;

                        } else {
                            mIsRegisterSuccessful.postValue(false);
                            mIsLoading.postValue(false);
                        }
                    }
                });
    }
}
