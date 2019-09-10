package com.teamandroid.snapshare.ui.main.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.teamandroid.snapshare.R;
import com.teamandroid.snapshare.data.model.User;
import com.teamandroid.snapshare.databinding.UserItemBinding;
import com.teamandroid.snapshare.generated.callback.OnClickListener;

import java.util.ArrayList;
import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {
    private List<User> mUsers = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        UserItemBinding userItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.user_item, parent, false);
        return new ViewHolder(userItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.mUserItemBinding.setUser(mUsers.get(position));
        holder.mUserItemBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickListener.onClick(mUsers.get(position).getId());
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
    public void setUserList(List<User> users) {
        mUsers.clear();
        mUsers.addAll(users);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mUsers == null ? 0 : mUsers.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private UserItemBinding mUserItemBinding;

        public ViewHolder(@NonNull UserItemBinding itemBinding) {
            super(itemBinding.getRoot());
            mUserItemBinding = itemBinding;
        }
    }
    interface OnItemClickListener {
        void onClick(String userId);
    }
}
