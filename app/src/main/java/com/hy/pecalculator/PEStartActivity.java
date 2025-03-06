package com.hy.pecalculator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * Created by hy on 2025-03-01
 * Describe:
 */
public class PEStartActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pestart);
        findViewById(R.id.year_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PEStartActivity.this,PEMainActivity.class));
            }
        });

        findViewById(R.id.month_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PEStartActivity.this,PEMonthActivity.class));
            }
        });
    }
}
