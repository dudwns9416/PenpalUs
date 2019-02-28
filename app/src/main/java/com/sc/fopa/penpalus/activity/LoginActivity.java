package com.sc.fopa.penpalus.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.facebook.*;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.database.*;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sc.fopa.penpalus.R;
import com.sc.fopa.penpalus.domain.User;
import com.sc.fopa.penpalus.sqlite.UserHelper;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends MainActivity {
    @BindView(R.id.txtPenpal)
    TextView txtPenpal;

    @BindView(R.id.txtU)
    TextView txtU;

    @BindView(R.id.txtS)
    TextView txtS;

    @BindView(R.id.btnFbLogin)
    TextView btnFbLogin;

    private CallbackManager callbackManager;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference("chat").child("user");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(this);
        super.onCreate(savedInstanceState);

        Typeface tf = Typeface.createFromAsset(getResources().getAssets(), "BMJUA_ttf.ttf");
        txtPenpal.setTypeface(tf);
        txtU.setTypeface(tf);
        txtS.setTypeface(tf);
        btnFbLogin.setTypeface(tf);

        //LoginManager.getInstance().logOut();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.btnFbLogin)
    public void fbLoginButton() {
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this,
                Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                doGraphRequest(loginResult);
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
            }
        });

    }

    private void doGraphRequest(LoginResult loginResult) {
        GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String id = object.getString("id");
                    String email = object.getString("email");
                    String name = object.getString("name");

                    User user = setUser(id, email);

                    loginOrSignUp(user);
                    saveUserDB(user);
                    getGoNextActivity();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }

    @NonNull
    private User setUser(String id, String email) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setToken(FirebaseInstanceId.getInstance().getToken());
        user.setRoom("0");
        return user;
    }

    private void saveUserDB(User user) {
        UserHelper userHelper = new UserHelper(LoginActivity.this);
        userHelper.deleteUser();
        userHelper.insertUser(user);
        userHelper.close();
    }

    private void getGoNextActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void loginOrSignUp(final User user) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isAlreadyUser = true;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    User resultUser = postSnapshot.getValue(User.class);
                    if (resultUser.getId().equals(user.getId())) {
                        if (!resultUser.getToken().equals(user.getToken())) {
                            postSnapshot.getRef().removeValue();
                        } else {
                            isAlreadyUser = false;
                        }
                        break;
                    }
                }
                if (isAlreadyUser) {
                    databaseReference.push().setValue(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
