package com.sc.fopa.penpalus.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.sc.fopa.penpalus.R;
import com.sc.fopa.penpalus.sqlite.UserHelper;


public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.txtPenpal)
    TextView txtPenpal;

    @BindView(R.id.txtU)
    TextView txtU;

    @BindView(R.id.txtS)
    TextView txtS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        Typeface tf = Typeface.createFromAsset(getResources().getAssets(), "BMJUA_ttf.ttf");
        txtPenpal.setTypeface(tf);
        txtU.setTypeface(tf);
        txtS.setTypeface(tf);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
        txtPenpal.setAnimation(animation);
        txtU.setAnimation(animation);
        txtS.setAnimation(animation);
        moveActivity(getDestinationActivity());
    }

    private Class<?> getDestinationActivity() {
        Class<?> destinationActivity;
        UserHelper userHelper = new UserHelper(this);

        if (userHelper.isLogin()) {
            destinationActivity = HomeActivity.class;
        } else {
            destinationActivity = LoginActivity.class;
        }
        return destinationActivity;
    }

    private void moveActivity(final Class<?> destinationActivity) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, destinationActivity);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }

}