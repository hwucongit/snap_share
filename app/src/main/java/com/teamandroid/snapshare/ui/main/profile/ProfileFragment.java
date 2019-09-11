package com.teamandroid.snapshare.ui.main.profile;

import android.content.Intent;
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
import com.teamandroid.snapshare.databinding.FragmentProfileBinding;
import com.teamandroid.snapshare.databinding.ProfileMainItemBinding;
import com.teamandroid.snapshare.generated.callback.OnClickListener;
import com.teamandroid.snapshare.utils.Constants;

import java.util.List;

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
        }
        if (mUserId != null && user != null && !mUserId.equals(user.getUid())){
            mProfileViewModel.checkFollowingState(user.getUid(),mUserId);
            listenFollow();
        }

        listenToPosts();
        mProfileViewModel.getPosts(mUserId);
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
                        mProfileViewModel.unFollowUser(mCurrentUserId,mUserId);
                    else
                        mProfileViewModel.followUser(mCurrentUserId,mUserId);
                }
            }
        });
    }

    private void handleArguments() {
        //FIXME: get args from getArguments
        // Handle passed arguments to define running mode
        Bundle args = getArguments();
        if (args != null) {
            mUserId = args.getString(Constants.ARGUMENT_USER_ID);
        }
        //TODO: got userId, fetch
        if (mProfileViewModel.displayingCurrentUser(args)) {
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
            mProfileViewModel.setProfileUser(account);
        } else {
            mProfileViewModel.setProfileUser(args);
        }
    }

    private void setToolbar() {
        FragmentActivity fragmentActivity = getActivity();
        if (fragmentActivity == null) return;
        ((AppCompatActivity) fragmentActivity).setSupportActionBar(mToolbar);
    }

    private void listenToPosts() {
        mProfileViewModel.getUserPosts()
            .observe(getViewLifecycleOwner(), new Observer<List<Post>>() {
                @Override
                public void onChanged(List<Post> posts) {
                    mProfilePostAdapter.setData(posts);
                }
            });
    }


}


