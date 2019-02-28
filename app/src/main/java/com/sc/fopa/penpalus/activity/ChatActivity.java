package com.sc.fopa.penpalus.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.firebase.database.*;
import com.sc.fopa.penpalus.R;
import com.sc.fopa.penpalus.adapter.ChatAdapter;
import com.sc.fopa.penpalus.domain.ChatMessage;
import com.sc.fopa.penpalus.domain.FcmResponse;
import com.sc.fopa.penpalus.domain.Papago;
import com.sc.fopa.penpalus.domain.User;
import com.sc.fopa.penpalus.service.FcmService;
import com.sc.fopa.penpalus.service.FcmServiceClass;
import com.sc.fopa.penpalus.service.MessageService;
import com.sc.fopa.penpalus.service.MessageServiceClass;
import com.sc.fopa.penpalus.sqlite.UserHelper;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_OK;

public class ChatActivity extends MainActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.rcvChat)
    RecyclerView rcvChat;

    @BindView(R.id.inputChatMessage)
    EditText inputChateMassage;

    @BindView(R.id.btnChatMessage)
    Button btnChatMessage;

    List<ChatMessage> chatMessageList = new ArrayList<>();
    ChatAdapter chatAdapter;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference("chat");
    private String roomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setTitle("채팅방");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initRecyclerView();
        roomId = getIntent().getStringExtra("roomId");
        databaseReference.child("data").child(roomId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                getChatMessage(chatMessage);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_chat;
    }

    @OnClick(R.id.btnChatMessage)
    public void createChateMessage() {

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(inputChateMassage.getText().toString());
        chatMessage.setDate(StringSimpleFromCalendar(Calendar.getInstance()));
        chatMessage.setUser(getUser().getId());

        databaseReference.child("data").child(roomId).push().setValue(chatMessage);
        doPapago(chatMessage);
        final String userId = getIntent().getStringExtra("user");
        databaseReference.child("user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    User resultUser = postSnapshot.getValue(User.class);
                    if (resultUser.getId().equals(userId)) {
                        fcmSendMessage(resultUser.getToken());
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void fcmSendMessage(String pushToken) {
        JSONObject body = new JSONObject();
        try {
            body.put("to", "/token/" + pushToken);
            body.put("priority", "high");
            JSONObject data = new JSONObject();

            String title = URLEncoder.encode(getUser().getEmail(), "utf-8");
            String titleBody = URLEncoder.encode("메시지가 도착했습니다", "utf-8");
            data.put("title", title);
            data.put("message", titleBody);

            body.put("data", data);
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }

        FcmService fcmService = FcmServiceClass.retrofit.create(FcmService.class);
        final RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), body.toString());
        Call<FcmResponse> call = fcmService.sendFcm(requestBody);
        call.enqueue(new Callback<FcmResponse>() {
            @Override
            public void onResponse(Call<FcmResponse> call, Response<FcmResponse> response) {
                if (response.code() == HTTP_OK) {
                    FcmResponse result = response.body();
                    Log.d("responseBody", result.toString());
                } else {
                    Toast.makeText(getApplication(), "전송실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FcmResponse> call, Throwable t) {
                Toast.makeText(getApplication(), "에러", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getChatMessage(ChatMessage chatMessage) {
        chatMessageList.add(chatMessage);
        chatAdapter.notifyDataSetChanged();
        rcvChat.scrollToPosition(chatMessageList.size() - 1);
    }


    public static String StringSimpleFromCalendar(Calendar calendar) {
        SimpleDateFormat formatter = new SimpleDateFormat("a h:mm");
        return formatter.format(calendar.getTime());
    }

    private void initRecyclerView() {
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);

        chatAdapter = new ChatAdapter(this, chatMessageList);
        chatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = chatAdapter.getItemCount();
                int lastVisiblePosition =
                        mLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    rcvChat.scrollToPosition(positionStart);
                }
            }

        });
        rcvChat.setLayoutManager(mLayoutManager);
        rcvChat.setAdapter(chatAdapter);
    }

    private void doPapago(ChatMessage chatMessage) {
        String source = "ko";
        String target = "en";
        String text = chatMessage.getMessage();
        if (text.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*")) {
            papagoRetrofit(chatMessage, source, target, text);
        } else {
            source = "en";
            target = "ko";
            papagoRetrofit(chatMessage, source, target, text);
        }
    }

    private void papagoRetrofit(final ChatMessage chatMessage, String source, String target, String text) {
        MessageService messageService = MessageServiceClass.retrofit.create(MessageService.class);
        Call<Papago> call = messageService.koreanTranslateEnglish(source, target, text);
        call.enqueue(new Callback<Papago>() {
            @Override
            public void onResponse(Call<Papago> call, Response<Papago> response) {
                if (response.code() == HTTP_OK) {
                    Papago papago = response.body();
                    chatMessage.setMessage(papago.getMessage().getResult().getTranslatedText());
                    databaseReference.child("data").child(roomId).push().setValue(chatMessage);
                } else {
                    Toast.makeText(ChatActivity.this, "translate error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Papago> call, Throwable t) {
                Toast.makeText(ChatActivity.this, "network error", Toast.LENGTH_LONG).show();
            }
        });
    }

    private User getUser() {
        UserHelper userHelper = new UserHelper(ChatActivity.this);
        return userHelper.selectUser();
    }
}
