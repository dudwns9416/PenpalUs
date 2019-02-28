package com.sc.fopa.penpalus.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.facebook.login.LoginManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sc.fopa.penpalus.R;
import com.sc.fopa.penpalus.R;
import com.sc.fopa.penpalus.domain.User;
import com.sc.fopa.penpalus.sqlite.UserHelper;
import com.sc.fopa.penpalus.utils.WsConfig;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class MoreFragment extends Fragment {
    @BindView(R.id.imgProfile)
    ImageView imgProfile;

    @BindView(R.id.layoutLogOut)
    LinearLayout layoutLogOut;

    @BindView(R.id.layoutRemoveMember)
    LinearLayout layoutRemoveMember;

    @BindView(R.id.txtUserId)
    TextView txtUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more, container, false);

        ButterKnife.bind(this, view);

        Glide.with(getContext())
                .load(WsConfig.FACEBOOK_IMG + getUser().getId() + "/picture?type=normal")
                .thumbnail(0.1f)
                .bitmapTransform(new CropCircleTransformation(getContext()))
                .into(imgProfile);

        txtUserId.setText(getUser().getEmail());
        return view;
    }

    @OnClick(R.id.layoutLogOut)
    public void doLogOut() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("로그아웃");
        alertDialog.setMessage("정말로 로그아웃을 하시겠습니까?");
        alertDialog.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                logOut();
            }
        });
        alertDialog.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        alertDialog.show();

    }

    private void logOut() {
        Context context = getActivity();
        UserHelper userHelper = new UserHelper(context);
        userHelper.deleteUser();
        LoginManager.getInstance().logOut();
        getActivity().finish();
    }

    @OnClick(R.id.layoutRemoveMember)
    public void doRemoveMember() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("회원탈퇴");
        alertDialog.setMessage("정말로 회원탈퇴를 하시겠습니까?");
        alertDialog.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                removeMember();
            }
        });
        alertDialog.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        alertDialog.show();
    }

    private void removeMember() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference("chat").child("user");
        final User user = getUser();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    User resultUser = postSnapshot.getValue(User.class);
                    if (resultUser.getId().equals(user.getId())) {
                        postSnapshot.getRef().removeValue();
                        logOut();
                        break;
                    }
                }
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
