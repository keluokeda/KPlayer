package com.ke.kplayer;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;


import com.ke.player.media.AndroidMediaController;
import com.ke.player.media.IjkVideoView;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class VideoActivity extends AppCompatActivity {
    private static final String EXTRA_PATH = "EXTRA_PATH";
    private boolean mBackPressed;

    public static void toActivity(Context context, String videoPath) {
        Intent intent = new Intent(context, VideoActivity.class);
        intent.putExtra(EXTRA_PATH, videoPath);
        context.startActivity(intent);
    }


    private IjkVideoView mIjkVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mIjkVideoView = (IjkVideoView) findViewById(R.id.video_view);

        initIjkPlayer();

    }

    private void initIjkPlayer() {
        // init player
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        AndroidMediaController mediaController = new AndroidMediaController(this, false);
        if (getSupportActionBar()!=null){
            mediaController.setSupportActionBar(getSupportActionBar());
        }
        mIjkVideoView.setMediaController(mediaController);


        String videoPath = getIntent().getStringExtra(EXTRA_PATH);
        if (TextUtils.isEmpty(videoPath)) {
            finish();
        } else {
            mIjkVideoView.setVideoPath(videoPath);
        }


    }

    private void setFullscreen(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        if (on) {
            winParams.flags |=  bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setTitle("现在是竖屏");
            setFullscreen(false);
        } else {
            setTitle("现在是横屏");
            setFullscreen(true);
        }
    }

    @Override
    public void onBackPressed() {
        mBackPressed = true;

        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mBackPressed || !mIjkVideoView.isBackgroundPlayEnabled()) {
            mIjkVideoView.stopPlayback();
            mIjkVideoView.release(true);
            mIjkVideoView.stopBackgroundPlay();
        } else {
            mIjkVideoView.enterBackground();
        }
        IjkMediaPlayer.native_profileEnd();
    }
}
