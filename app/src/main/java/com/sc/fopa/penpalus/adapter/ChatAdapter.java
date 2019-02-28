package com.sc.fopa.penpalus.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sc.fopa.penpalus.R;
import com.sc.fopa.penpalus.R;
import com.sc.fopa.penpalus.activity.ChatActivity;
import com.sc.fopa.penpalus.domain.ChatMessage;
import com.sc.fopa.penpalus.domain.User;
import com.sc.fopa.penpalus.sqlite.UserHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by fopa on 2017-11-20.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{
    private Context context;
    private List<ChatMessage> chatMessagesList;

    public ChatAdapter(Context context, List<ChatMessage> chatMessagesList) {
        this.context = context;
        this.chatMessagesList = chatMessagesList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessagesList.get(position);

        if (chatMessage.getUser().equals(getUser().getId())){
            holder.layoutChatLine.setGravity(Gravity.RIGHT);
            holder.layoutToLine.setVisibility(holder.itemView.GONE);
            holder.layoutFromLine.setVisibility(holder.itemView.VISIBLE);
            holder.txtFromMessage.setText(chatMessage.getMessage());
            holder.txtFromDate.setText(chatMessage.getDate());
        }else {
            holder.layoutChatLine.setGravity(Gravity.LEFT);
            holder.layoutFromLine.setVisibility(holder.itemView.GONE);
            holder.layoutToLine.setVisibility(holder.itemView.VISIBLE);
            holder.txtToMessage.setText(chatMessage.getMessage());
            holder.txtToDate.setText(chatMessage.getDate());
        }

    }

    @Override
    public int getItemCount() {
        return chatMessagesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txtFromDate)
        TextView txtFromDate;

        @BindView(R.id.txtToDate)
        TextView txtToDate;

        @BindView(R.id.txtFromMessage)
        TextView txtFromMessage;

        @BindView(R.id.txtToMessage)
        TextView txtToMessage;

        @BindView(R.id.layoutChatLine)
        LinearLayout layoutChatLine;

        @BindView(R.id.layoutFromLine)
        LinearLayout layoutFromLine;

        @BindView(R.id.layoutToLine)
        LinearLayout layoutToLine;

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
