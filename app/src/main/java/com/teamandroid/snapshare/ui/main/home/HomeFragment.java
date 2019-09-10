package com.teamandroid.snapshare.ui.main.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.teamandroid.snapshare.R;
import com.teamandroid.snapshare.data.model.Post;
import com.teamandroid.snapshare.ui.login.LoginActivity;

import java.util.List;


public class HomeFragment extends Fragment {
    public static String TAG = HomeFragment.class.getSimpleName();
    private Toolbar mToolbar;
    private PostViewModel mPostViewModel;
    private PostListAdapter mAdapter;
    private RecyclerView postListRv;
    private SwipeRefreshLayout mRefreshLayout;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(true);
        initView(rootView);
        setToolbar();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        postListRv = view.findViewById(R.id.rv_feed);
        postListRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new PostListAdapter();
        postListRv.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPostViewModel = ViewModelProviders.of(this).get(PostViewModel.class);
        getPosts();
    }

    private void setToolbar() {
        FragmentActivity fragmentActivity = getActivity();
        if (fragmentActivity == null) return;
        ((AppCompatActivity) fragmentActivity).setSupportActionBar(mToolbar);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void initView(View view) {
        mToolbar = view.findViewById(R.id.toolbar);
        mRefreshLayout = view.findViewById(R.id.layout_refresh);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPostViewModel.loadPosts();
            }
        });
    }

    private void getPosts() {
        mPostViewModel.getPosts().observe(this, new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> posts) {
                mAdapter.setPostList(posts);
                mRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
                if (getActivity() != null) getActivity().finish();
                return true;
            case R.id.refresh:
                mRefreshLayout.setRefreshing(true);
                mPostViewModel.loadPosts();
        }
        return super.onOptionsItemSelected(item);
    }
}
