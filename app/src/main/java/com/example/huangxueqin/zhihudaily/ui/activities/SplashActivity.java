package com.example.huangxueqin.zhihudaily.ui.activities;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.huangxueqin.zhihudaily.R;
import com.example.huangxueqin.zhihudaily.models.StartImageInfo;
import com.example.huangxueqin.zhihudaily.common.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Callback;

public class SplashActivity extends BaseActivity implements Callback<StartImageInfo> {
    public static final String TAG = "SPLASH_SCREEN_TAG";

    @BindView(R.id.start_image) ImageView mStartImage;
    Handler mMainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        mStartImage.setScaleType(ImageView.ScaleType.FIT_XY);
        mMainHandler = new Handler(getMainLooper());
        loadStartImage();
    }

    @Override
    protected void onDestroy() {
        mMainHandler.removeCallbacks(mStartMainRunnable);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void loadStartImage() {
        int screenWidth = (Constants.getScreenSize(this)).x;
        Call<StartImageInfo> call = null;
        if(screenWidth <= 320) {
            call = mAPI.get320StartImage();
        } else if(screenWidth <= 480) {
            call = mAPI.get480StartImage();
        } else if(screenWidth <= 720) {
            call = mAPI.get720StartImage();
        } else {
            call = mAPI.get1080StartImage();
        }
        call.enqueue(this);
    }

    private void startMainActivity(int delayMills) {
        new Handler().postDelayed(mStartMainRunnable, delayMills);
    }

    private Runnable mStartMainRunnable = new Runnable() {
        @Override
        public void run() {
            Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mainIntent);
            overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
        }
    };

    @Override
    public void onResponse(Call<StartImageInfo> call, Response<StartImageInfo> response) {
        D(response.body().img);
        Glide.with(this).load(response.body().img).into(mStartImage);
        startMainActivity(2000);
    }

    @Override
    public void onFailure(Call<StartImageInfo> call, Throwable t) {
        Toast.makeText(this, "Load Image Failed", Toast.LENGTH_SHORT).show();
        startMainActivity(500);
    }

    public void D(String msg) {
        Log.d(TAG, msg);
    }
}
