package com.teamandroid.snapshare.ui.main.profile;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.teamandroid.snapshare.R;
import com.teamandroid.snapshare.data.model.Post;
import com.teamandroid.snapshare.databinding.ProfileMainItemBinding;

import java.util.ArrayList;
import java.util.List;

public class ProfilePostAdapter extends RecyclerView.Adapter<ProfilePostAdapter.ViewHolder> {
    private List<Post> mData = new ArrayList<>();


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ProfileMainItemBinding itemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.profile_main_item, parent, false);
        return new ViewHolder(itemBinding);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mProfileMainItemBinding.setPost(mData.get(position));
    }


    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }


    public void setData(List<Post> posts) {
        this.mData = posts;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ProfileMainItemBinding mProfileMainItemBinding;

        public ViewHolder(@NonNull ProfileMainItemBinding itemBinding) {
            super(itemBinding.getRoot());
            mProfileMainItemBinding = itemBinding;
        }
    }
}
