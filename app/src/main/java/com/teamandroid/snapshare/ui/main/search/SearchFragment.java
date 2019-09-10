package com.teamandroid.snapshare.ui.main.search;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.teamandroid.snapshare.R;
import com.teamandroid.snapshare.data.model.User;
import com.teamandroid.snapshare.ui.main.MainActivity;
import com.teamandroid.snapshare.ui.main.profile.ProfileFragment;
import com.teamandroid.snapshare.utils.Constants;
import com.teamandroid.snapshare.utils.Helper;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements UserListAdapter.OnItemClickListener {
    private Toolbar mToolbar;
    private TextView mTextViewResult;
    private SearchView mSearchView;
    private UserSearchViewModel mViewModel;
    private RecyclerView mUserListRv;
    private UserListAdapter mAdapter;
    private Context mContext;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        initView(rootView);
        setToolbar();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(UserSearchViewModel.class);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mViewModel.getUserListBySearchQuery(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        getUsers();
    }

    private void setToolbar() {
        FragmentActivity fragmentActivity = getActivity();
        if (fragmentActivity == null) return;
        ((AppCompatActivity) fragmentActivity).setSupportActionBar(mToolbar);
    }

    private void initView(View view) {
        mToolbar = view.findViewById(R.id.toolbar);
        mTextViewResult = view.findViewById(R.id.tv_search_result);
        mSearchView = view.findViewById(R.id.search_view);
        mUserListRv = view.findViewById(R.id.user_list_rv);
        mUserListRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new UserListAdapter();
        mAdapter.setOnItemClickListener(this);
        mUserListRv.setAdapter(mAdapter);
    }

    private void getUsers() {
        mViewModel.getUsers().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                if (users.size() == 0) {
                    mTextViewResult.setText(getString(R.string.search_no_resutl));
                } else
                    mTextViewResult.setText(String.format(
                        getString(R.string.search_result), users.size(),
                        users.size() > 1 ? "s" : ""));
                mAdapter.setUserList(users);
            }
        });
    }

    @Override
    public void onClick(String userId) {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null)
            mainActivity.replaceProfileFragment(userId);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
