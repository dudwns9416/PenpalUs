package com.sc.fopa.penpalus.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sc.fopa.penpalus.R;
import com.sc.fopa.penpalus.R;
import com.sc.fopa.penpalus.activity.ChatActivity;
import com.sc.fopa.penpalus.adapter.RoomListAdpater;
import com.sc.fopa.penpalus.domain.Room;
import com.sc.fopa.penpalus.domain.User;
import com.sc.fopa.penpalus.listener.OnItemClickListener;
import com.sc.fopa.penpalus.sqlite.UserHelper;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RoomListFragment extends Fragment {
    @BindView(R.id.rcvRoom)
    RecyclerView rcvRoom;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference("chat");
    private List<Room> roomList = new ArrayList<>();
    private RoomListAdpater roomListAdpater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room_list, container, false);

        ButterKnife.bind(this, view);

        init();

        return view;
    }

    private void init() {
        final Context context = getActivity();
        roomListAdpater = new RoomListAdpater(getActivity(), roomList, new OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Room room = roomList.get(position);
                String roomId = room.getId();
                String user = new String();
                if (room.getUserA().equals(getUser().getId())) {
                    user = room.getUserB();
                } else {
                    user = room.getUserA();
                }
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("roomId", roomId);
                startActivity(intent);
            }
        });
        rcvRoom.setLayoutManager(new LinearLayoutManager(context));
        rcvRoom.setAdapter(roomListAdpater);

        databaseReference.child("room").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                roomList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Room room = postSnapshot.getValue(Room.class);
                    if (room.getUserA().equals(getUser().getId())) {
                        roomList.add(room);
                    } else if (room.getUserB().equals(getUser().getId())) {
                        roomList.add(room);
                    }
                }
                roomListAdpater.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private User getUser() {
        UserHelper userHelper = new UserHelper(getActivity());
        return userHelper.selectUser();
    }

}
