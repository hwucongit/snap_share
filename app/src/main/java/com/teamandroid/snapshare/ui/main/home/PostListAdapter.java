package com.teamandroid.snapshare.ui.main.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.teamandroid.snapshare.R;
import com.teamandroid.snapshare.data.model.Post;
import com.teamandroid.snapshare.databinding.PostItemBinding;
import com.teamandroid.snapshare.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.PostViewHolder> {
    private List<Post> mPosts = new ArrayList<>();

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PostItemBinding postItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.post_item, parent, false);
        return new PostViewHolder(postItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        holder.onBind(position);
    }

    void setPostList(List<Post> posts) {
        mPosts.clear();
        mPosts.addAll(posts);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return (mPosts != null) ? mPosts.size() : 0;
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        private PostItemBinding mPostItemBinding;
        private PostItemViewModel mPostItemViewModel;
        private FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        private Post mPost;
        private int currentLikeStatus;
        private int likeCount;

        public PostViewHolder(@NonNull PostItemBinding postItemBinding) {
            super(postItemBinding.getRoot());
            this.mPostItemBinding = postItemBinding;
            mPostItemBinding.btnLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onLikeClick(view);
                }
            });
        }

        public void onBind(int position) {
            mPost = mPosts.get(position);
            mPostItemViewModel = new PostItemViewModel(mPost);
            mPostItemBinding.setViewModel(mPostItemViewModel);
            if (mPostItemViewModel.checkLike(mFirebaseUser.getUid())) {
                mPostItemBinding.btnLike.setImageResource(R.drawable.ic_heart_active);
            }
            currentLikeStatus = mPostItemViewModel.checkLike(mFirebaseUser.getUid()) ?
                    Constants.LIKE_ACTIVE : Constants.LIKE_NON_ACTIVE;
            if (mPostItemViewModel.getLikeCount().getValue() != null)
                likeCount = mPostItemViewModel.getLikeCount().getValue();
            mPostItemBinding.executePendingBindings();
        }

        private void onLikeClick(View view) {
            mPostItemBinding.btnLike.setImageResource(currentLikeStatus == 1 ?
                    R.drawable.ic_favorite_border_black : R.drawable.ic_heart_active);
            likeCount = currentLikeStatus == Constants.LIKE_ACTIVE ? likeCount - 1 : likeCount + 1;
            mPostItemViewModel.likeCount.setValue(likeCount);
            mPostItemBinding.tvLikeCounter.setText(String.format(view.getContext().getString(R.string.like_counter),
                    likeCount, likeCount > 1 ? "s" : ""));
            mPostItemViewModel.updateLike(currentLikeStatus, mFirebaseUser.getUid());
            currentLikeStatus = (currentLikeStatus == Constants.LIKE_ACTIVE) ? Constants.LIKE_NON_ACTIVE : Constants.LIKE_ACTIVE;
        }
    }
}
