package com.teamandroid.snapshare.ui.post;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.teamandroid.snapshare.R;
import com.teamandroid.snapshare.data.local.AppPreferencesHelper;
import com.teamandroid.snapshare.data.model.Post;
import com.teamandroid.snapshare.databinding.ActivityPostBinding;
import com.teamandroid.snapshare.utils.Helper;
import com.theartofdev.edmodo.cropper.CropImage;

public class PostActivity extends AppCompatActivity {
    private ActivityPostBinding mBinding;
    private PostViewModel mPostViewModel;
    private ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        openChooseImageScreen();
        listenerAddPost();
    }

    private void init() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_post);
        mPostViewModel = ViewModelProviders.of(this).get(PostViewModel.class);
        mBinding.setPostViewModel(mPostViewModel);
        mBinding.setLifecycleOwner(this);
        mBinding.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openChooseImageScreen();
            }
        });
    }

    private void listenerAddPost() {
        mPostViewModel.getPost().observe(this, new Observer<Post>() {
            @Override
            public void onChanged(Post post) {
                mPostViewModel.sharePost(post);
            }
        });
        mPostViewModel.getIsLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean)
                    showProgressDialog();
                else hideProgressDialog();
            }
        });
        mPostViewModel.getIsAddPostSuccessful().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean)
                    finish();
                else
                    Helper.showToast(getApplicationContext(),
                            getResources().getString(R.string.fail_try_again_later));
            }
        });
    }

    private void showProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getResources().getString(R.string.please_wait));
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        mProgressDialog.dismiss();
    }

    private void openChooseImageScreen() {
        CropImage
                .activity()
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                mBinding.thumbnail.setImageURI(resultUri);
                mPostViewModel.setValueUriImage(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            } else if (resultCode == RESULT_CANCELED)
                finish();
        }
    }
}
