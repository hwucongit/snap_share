package com.teamandroid.snapshare.ui.main.profile;


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
import com.teamandroid.snapshare.generated.callback.OnClickListener;
import com.teamandroid.snapshare.utils.Constants;

import java.util.List;


public class ProfileFragment extends Fragment {
    private final int colNumb = 3;

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private ProfilePostAdapter mProfilePostAdapter;

    private FragmentProfileBinding mBinding;
    private ProfileViewModel mProfileViewModel;

    public ProfileFragment() {
        // Required empty public constructor
    }


    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
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

        //FIXME: hard link from GoogleAuth
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String id = "";
        if (user != null) {
            id = user.getUid();
        }
        mProfileViewModel.getPosts(id);

        handleArguments();
        listenToPosts();
    }


//    @Override
//    public void onItemClick(View view, int position) {
//        // TODO: add to back stack, open detail fragment
//        Toast.makeText(getContext(), "Clicked at: " + position,  Toast.LENGTH_SHORT).show();
//        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//        fragmentTransaction.replace(R.id.content_container, ProfileDetailedFragment.newInstance());
//        fragmentTransaction.addToBackStack(this.getClass().getName());
//        fragmentTransaction.commit();
//    }



    private void init(View rootView) {
        mToolbar = rootView.findViewById(R.id.toolbar);
        setToolbar();

        mRecyclerView = rootView.findViewById(R.id.profile_posts_list);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), colNumb));

        mProfilePostAdapter = new ProfilePostAdapter();
        mRecyclerView.setAdapter(mProfilePostAdapter);

    }


    private void handleArguments() {
        //FIXME: get args from getArguments
        // Handle passed arguments to define running mode
//        Bundle args = getArguments();
        Bundle args = new Bundle();
        if (mProfileViewModel.displayingCurrentUser(args)) {
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
            mProfileViewModel.setProfileUser(account);
        } else {
            mProfileViewModel.setProfileUser(args);
        }
    }


    private void setToolbar() {
        FragmentActivity fragmentActivity =  getActivity();
        if (fragmentActivity == null) return;
        ((AppCompatActivity) fragmentActivity).setSupportActionBar(mToolbar);
    }


    private void listenToPosts() {
        mProfileViewModel.getUserPosts().observe(getViewLifecycleOwner(), new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> posts) {
                mProfilePostAdapter.setData(posts);
            }
        });
    }
}


