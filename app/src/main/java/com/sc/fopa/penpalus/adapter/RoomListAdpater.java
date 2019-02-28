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
import com.sc.fopa.penpalus.domain.Room;
import com.sc.fopa.penpalus.domain.User;
import com.sc.fopa.penpalus.listener.OnItemClickListener;
import com.sc.fopa.penpalus.sqlite.UserHelper;
import com.sc.fopa.penpalus.utils.WsConfig;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class RoomListAdpater extends RecyclerView.Adapter<RoomListAdpater.ViewHolder>{
    private Context context;
    private List<Room> roomList;
    private OnItemClickListener listener;

    public RoomListAdpater(Context context, List<Room> rooms, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.roomList = rooms;
        this.listener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Room room = roomList.get(position);
        holder.txtRoomName.setText(room.getName());
        if (room.getUserA().equals(getUser().getId())) {
            Glide.with(context)
                    .load(WsConfig.FACEBOOK_IMG + room.getUserB() + "/picture?type=normal")
                    .thumbnail(0.1f)
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(holder.imgFriend);
        } else {
            Glide.with(context)
                    .load(WsConfig.FACEBOOK_IMG + room.getUserA() + "/picture?type=normal")
                    .thumbnail(0.1f)
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(holder.imgFriend);
        }
        holder.layoutFriendList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(view,position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.layoutFriendList)
        LinearLayout layoutFriendList;

        @BindView(R.id.imgFriend)
        ImageView imgFriend;

        @BindView(R.id.txtRoomName)
        TextView txtRoomName;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    private User getUser() {
        UserHelper userHelper = new UserHelper(context);
        return userHelper.selectUser();
    }

}
