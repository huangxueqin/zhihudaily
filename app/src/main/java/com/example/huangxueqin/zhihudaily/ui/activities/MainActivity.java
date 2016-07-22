package com.example.huangxueqin.zhihudaily.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.example.huangxueqin.zhihudaily.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by huangxueqin on 16-7-21.
 */
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.content)
    ViewGroup mContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }
}
