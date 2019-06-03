package com.example.Anni.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.Anni.R;

public class CustomDialog extends Dialog {

    private TextView mTvCount;
    private String mCountDownTime;

    public CustomDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        setContentView(R.layout.count_down);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        //init the views
        initTheViews();
        mTvCount.setText(mCountDownTime);

    }

    private void initTheViews() {
        mTvCount = findViewById(R.id.txt_count);
    }

    public void countDownTime(String countDown) {
        this.mCountDownTime = countDown;
    }
}
