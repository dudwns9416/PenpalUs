package com.sc.fopa.penpalus.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sc.fopa.penpalus.R;
import com.sc.fopa.penpalus.R;
import com.sc.fopa.penpalus.adapter.FriendListAdapter;
import com.sc.fopa.penpalus.domain.Room;
import com.sc.fopa.penpalus.domain.User;
import com.sc.fopa.penpalus.listener.OnItemClickListener;
import com.sc.fopa.penpalus.sqlite.UserHelper;
import com.sc.fopa.penpalus.view.CustomDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainHomeFragment extends Fragment {
    @BindView(R.id.rcvFriend)
    RecyclerView rcvFriend;

    @BindView(R.id.swipeLayout)
    SwipeRefreshLayout swipeLayout;

    private List<User> users = new ArrayList<>();
    private FriendListAdapter friendListAdapter;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference("chat");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_home, container, false);

        ButterKnife.bind(this, view);

        init();


        return view;
    }

    private void init() {
        final Context context = getContext();
        friendListAdapter = new FriendListAdapter(context, users, new OnItemClickListener() {
            @Override
            public void onClick(View view, final int position) {
                final CustomDialog customDialog = new CustomDialog(getActivity());
                final View dialogView = getLayoutInflater().inflate(R.layout.dialog_request_room, null);
                final EditText inputRoomName = (EditText) dialogView.findViewById(R.id.inputRoomName);
                Button btnRoomCreate = (Button) dialogView.findViewById(R.id.btnRoomCreate);
                customDialog.setContentView(dialogView);
                btnRoomCreate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        User user = getUser();
                        String name = inputRoomName.getText().toString();
                        String userA = user.getId();
                        String userB = users.get(position).getId();
                        String id = userA +  Calendar.getInstance().getTime().getTime();
                        Room room = new Room(id,name,userA,userB);
                        databaseReference.child("room").push().setValue(room);
                        customDialog.cancel();
                    }
                });
                customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                customDialog.show();
            }
        });
        rcvFriend.setLayoutManager(new LinearLayoutManager(context));
        rcvFriend.setAdapter(friendListAdapter);

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUserList();
            }
        });
    }

    private User getUser() {
        UserHelper userHelper = new UserHelper(getActivity());
        return userHelper.selectUser();
    }

    private void getUserList() {
        swipeLayout.setRefreshing(true);
        users.clear();
        databaseReference.child("user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    User user = postSnapshot.getValue(User.class);
                    if (user.getId().equals(getUser().getId())) continue;
                    users.add(user);
                }
                friendListAdapter.notifyDataSetChanged();
                swipeLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
