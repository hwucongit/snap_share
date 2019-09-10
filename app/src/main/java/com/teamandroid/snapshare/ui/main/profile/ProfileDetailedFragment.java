package com.teamandroid.snapshare.ui.main.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.teamandroid.snapshare.R;

public class ProfileDetailedFragment extends Fragment {
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private ProfilePostAdapter mProfilePostAdapter;
    private ProfileViewModel mProfileViewModel;

    public ProfileDetailedFragment() {
    }


    public static ProfileDetailedFragment newInstance() {
        ProfileDetailedFragment fragment = new ProfileDetailedFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return  fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile_detailed, container, false);
        init(rootView);


        return rootView;
    }


    private void init(View rootView) {
        mToolbar = rootView.findViewById(R.id.toolbar);
        setToolbar();

    }


    private void setToolbar() {
        FragmentActivity fragmentActivity =  getActivity();
        if (fragmentActivity == null) return;
        ((AppCompatActivity) fragmentActivity).setSupportActionBar(mToolbar);
    }
}
