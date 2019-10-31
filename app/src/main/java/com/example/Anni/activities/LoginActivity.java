package com.example.Anni.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Anni.R;
import com.example.Anni.util.PrefManager;
import com.example.Anni.util.PreferenceUtils;
import com.example.Anni.widget.CustomDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";

    private ConstraintLayout mConstraintLayoutLogin;
    private Button mButtonChooseDate, mButtonLogin;
    private TextView mTvDate, mTvDay;

    private AnimationDrawable mAnimationDrawable;
    private PreferenceUtils prefs;
    private DatePickerDialog mDatePickerDialog;
    private int mYear, mMonth, mDayOfMonth;
    private Calendar mCalender;
    private String mChooseDateResult;
    private int mButtonClickTimes = 0, mCountDownTime = 20;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Checking for first time launch - before calling setContentView()
        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            lunchHomeScreen();
            finish();
        }

        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();
        //init the views
        initTheViews();
        mAnimationDrawable = (AnimationDrawable) mConstraintLayoutLogin.getBackground();
        mAnimationDrawable.setEnterFadeDuration(1000);
        mAnimationDrawable.setExitFadeDuration(1000);

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 22) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        // making notification bar transparent
        changeStatusBarColor();

        //click actions
        mButtonChooseDate.setOnClickListener(this);
        mButtonLogin.setOnClickListener(this);
    }

    private void lunchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    private void initTheViews() {
        mConstraintLayoutLogin = findViewById(R.id.cl_login);
        mButtonChooseDate = findViewById(R.id.btn_choose_date);
        mButtonLogin = findViewById(R.id.btn_login);
        mTvDate = findViewById(R.id.txt_date);
        mTvDay = findViewById(R.id.txt_day);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAnimationDrawable != null && !mAnimationDrawable.isRunning()) {
            mAnimationDrawable.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAnimationDrawable != null && !mAnimationDrawable.isRunning()) {
            mAnimationDrawable.stop();
        }
    }


    //Making notification bar transparent
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= 22) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_choose_date:
                chooseDate();
                return;
            case R.id.btn_login:
                loginValid();
                return;
            default:
                break;
        }
    }

    private void loginValid() {
        if (mChooseDateResult != null) {
//            prefs.setFirstTimeLaunch(false);
            mButtonClickTimes++;
            if (mButtonClickTimes == 1) {
                if (mChooseDateResult.equalsIgnoreCase("14/6/2018")) {
                    lunchHomeScreen();
                } else {
                    Toast.makeText(this, "Wrong Date ", Toast.LENGTH_SHORT).show();
                }
            } else if (mButtonClickTimes == 2) {
                if (mChooseDateResult.equalsIgnoreCase("14/6/2018")) {
                    lunchHomeScreen();
                } else {
                    countDownDialog();
                }

            }

        }
    }

    private void countDownDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.count_down);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        final TextView mTvCount = dialog.findViewById(R.id.txt_count);

        dialog.show();

        new CountDownTimer(21000, 1000) {

            public void onTick(long millisUntilFinished) {
                mTvCount.setText(String.valueOf(mCountDownTime));
                mCountDownTime -= 1;
                if (mCountDownTime == -1) {
                    mCountDownTime = 20;
                }
            }

            public void onFinish() {
                mButtonClickTimes = 0;
                dialog.dismiss();
            }

        }.start();
        Toast.makeText(this, "Please Choose Correct Date!!!", Toast.LENGTH_LONG).show();
    }

    private void chooseDate() {
        mCalender = Calendar.getInstance();
        mYear = mCalender.get(Calendar.YEAR);
        mMonth = mCalender.get(Calendar.MONTH);
        mDayOfMonth = mCalender.get(Calendar.DAY_OF_MONTH);
        mDatePickerDialog = new DatePickerDialog(LoginActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                mChooseDateResult = day + "/" + (month + 1) + "/" + year;
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);
                SimpleDateFormat formatDayMonthYear = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
                SimpleDateFormat formatDays = new SimpleDateFormat("EEEE", Locale.getDefault());
                mTvDate.setText(formatDayMonthYear.format(calendar.getTime()));
                mTvDay.setText(formatDays.format(calendar.getTime()));
            }
        }, mYear, mMonth, mDayOfMonth);

        mDatePickerDialog.show();
    }
}
