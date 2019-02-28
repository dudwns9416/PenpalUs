package com.sc.fopa.penpalus.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sc.fopa.penpalus.R;
import com.sc.fopa.penpalus.R;
import com.sc.fopa.penpalus.domain.User;
import com.sc.fopa.penpalus.listener.OnItemClickListener;
import com.sc.fopa.penpalus.utils.WsConfig;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder>{
    private Context context;
    private List<User> userList;
    private OnItemClickListener listener;

    public FriendListAdapter(Context context, List<User> users, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.userList = users;
        this.listener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        User user = userList.get(position);
        holder.txtFriendEmail.setText(user.getEmail());
        Glide.with(context)
                .load(WsConfig.FACEBOOK_IMG + user.getId() + "/picture?type=normal")
                .thumbnail(0.1f)
                .bitmapTransform(new CropCircleTransformation(context))
                .into(holder.imgFriend);
        holder.layoutFriendList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(view,position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.layoutFriendList)
        LinearLayout layoutFriendList;

        @BindView(R.id.imgFriend)
        ImageView imgFriend;

        @BindView(R.id.txtFriendEmail)
        TextView txtFriendEmail;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }


}
