package com.teamandroid.snapshare.ui.main.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.teamandroid.snapshare.R;
import com.teamandroid.snapshare.data.model.Post;
import com.teamandroid.snapshare.data.model.User;
import com.teamandroid.snapshare.data.repository.FirestoreRepository;
import com.teamandroid.snapshare.databinding.FragmentProfileBinding;
import com.teamandroid.snapshare.databinding.ProfileMainItemBinding;
import com.teamandroid.snapshare.generated.callback.OnClickListener;
import com.teamandroid.snapshare.utils.Constants;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment implements ProfilePostAdapter.OnClickListener {
    private final int colNumb = 3;
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private ProfilePostAdapter mProfilePostAdapter;
    private String mUserId;
    private FragmentProfileBinding mBinding;
    private ProfileMainItemBinding mItemBinding;
    private ProfileViewModel mProfileViewModel;
    private String mCurrentUserId;
    private User mUser;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(@NonNull Bundle args) {
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        init(mBinding.getRoot());
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mProfileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        mBinding.setProfileViewModel(mProfileViewModel);
        mBinding.setLifecycleOwner(this);

        // Get arguments, pass received userId to mUserId
        handleArguments();

        // Signed user = mCurrentUSer
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            mCurrentUserId = user.getUid();
        }
        if (mUserId != null && user != null && mUserId.equals(user.getUid())) {
            mBinding.buttonFollow.setVisibility(View.GONE);
            mBinding.profileAvatar.setEnabled(true);
        }
        if (mUserId != null && user != null && !mUserId.equals(user.getUid())) {
            mProfileViewModel.checkFollowingState(user.getUid(), mUserId);
            mBinding.profileAvatar.setEnabled(false);
            listenFollow();
        }
        loadUserInfo(mUserId);
        mProfileViewModel.getPosts(mUserId);
        listenLoadPosts();
    }

    @Override
    public void onClick(String postId) {
        Intent intent = new Intent(getContext(), DetailedPostActivity.class);
        Bundle args = new Bundle();
        args.putString(Constants.ARGUMENT_POST_ID, postId);
        args.putString(Constants.ARGUMENT_USER_ID, this.mCurrentUserId);
        intent.putExtras(args);

        startActivity(intent);
        //TODO: put this fragment to backstack and open activity
    }

    private void listenFollow() {
        mProfileViewModel.getIsFollowed().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean)
                    mBinding.buttonFollow.setText(getResources().getString(R.string.un_follow));
                else
                    mBinding.buttonFollow.setText(getResources().getString(R.string.follow));
            }
        });
    }

    private void init(View rootView) {
        mToolbar = rootView.findViewById(R.id.toolbar);
        setToolbar();
        mRecyclerView = rootView.findViewById(R.id.profile_posts_list);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), colNumb));
        mProfilePostAdapter = new ProfilePostAdapter();
        mProfilePostAdapter.setOnClickListener(this);
        mRecyclerView.setAdapter(mProfilePostAdapter);

        mBinding.buttonFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mProfileViewModel.getIsFollowed().getValue() != null) {
                    if (mProfileViewModel.getIsFollowed().getValue())
                        mProfileViewModel.unFollowUser(mCurrentUserId, mUserId);
                    else
                        mProfileViewModel.followUser(mCurrentUserId, mUserId);
                }
            }
        });
        mBinding.profileAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openChooseImageScreen();
            }
        });
    }

    private void handleArguments() {
        Bundle args = getArguments();
        if (args != null) {
            mUserId = args.getString(Constants.ARGUMENT_USER_ID);
        }
    }

    private void setToolbar() {
        FragmentActivity fragmentActivity = getActivity();
        if (fragmentActivity == null) return;
        ((AppCompatActivity) fragmentActivity).setSupportActionBar(mToolbar);
    }

    private void listenLoadPosts() {
        mProfileViewModel.getUserPosts()
            .observe(getViewLifecycleOwner(), new Observer<List<Post>>() {
                @Override
                public void onChanged(List<Post> posts) {
                    mProfilePostAdapter.setData(posts);
                }
            });
        mProfileViewModel.getTotalPost()
            .observe(this, new Observer<Integer>() {
                @Override
                public void onChanged(Integer postNumber) {
                    String totalPost = String.format(getString(R.string.format_total_post),
                        postNumber, postNumber > 1 ? "s" : "");
                    mBinding.profilePostsNumber.setText(totalPost);
                }
            });
    }

    private void loadUserInfo(final String userId) {
        FirestoreRepository.getInstance().getUserById(userId,
            new FirestoreRepository.Callback<User>() {
                @Override
                public void onSuccess(User result) {
                    mBinding.setUser(result);
                }

                @Override
                public void onFailure(Exception e) {
                }
            });
    }
    private void openChooseImageScreen() {
        CropImage
            .activity()
            .start(Objects.requireNonNull(getActivity()),this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                mProfileViewModel.changeAvatarImage(resultUri,mUserId);
                mBinding.profileAvatar.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


}


