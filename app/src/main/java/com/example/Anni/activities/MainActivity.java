package com.example.Anni.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Anni.R;
import com.example.Anni.fragment.ImagesShowFragment;
import com.example.Anni.util.Util;
import com.github.jinatonic.confetti.ConfettiManager;
import com.github.jinatonic.confetti.ConfettiSource;
import com.github.jinatonic.confetti.ConfettoGenerator;
import com.github.jinatonic.confetti.confetto.BitmapConfetto;
import com.github.jinatonic.confetti.confetto.Confetto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import yanzhikai.textpath.SyncTextPathView;
import yanzhikai.textpath.painter.FireworksPainter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ConfettoGenerator {

    private ConstraintLayout mMainLayout;
    private ImageButton mTogetherButton, mTwoPersonButton, mSinglePersonButton;
    private Button mButtonLogin;
    private TextView mAnniDateTextView;
    private SyncTextPathView mAnniLabel;
    private int size;
    private int velocitySlow, velocityNormal;
    private Bitmap bitmap;
    private final List<ConfettiManager> activeConfettiManagers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init the views
        initTheViews();

        //mMainLayout.setBackgroundColor(getColorWithAlpha(getResources().getColor(R.color.colorAccent), 0.5f));

        //click events
        mSinglePersonButton.setOnClickListener(this);
        mTwoPersonButton.setOnClickListener(this);
        mTogetherButton.setOnClickListener(this);
        mButtonLogin.setOnClickListener(this);

        //day counts
        String strThatDay = "2018/06/14";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Date d = null;
        try {
            d = formatter.parse(strThatDay);//catch exception
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Calendar thatDay = Calendar.getInstance();
        thatDay.setTime(d); //rest is the same....
        Calendar today = Calendar.getInstance();

        long diff = today.getTimeInMillis() - thatDay.getTimeInMillis(); //result in millis
        long days = diff / (24 * 60 * 60 * 1000);
        Typeface tf = Typeface.createFromAsset(getResources().getAssets(), "fonts/digital.ttf");
        mAnniDateTextView.setTypeface(tf);
        mAnniDateTextView.setText(Long.toString(days) + " DAYS");

        //Showing Anni Year Text
        mAnniLabel.setPathPainter(new FireworksPainter());
        mAnniLabel.drawPath(1000f);
        mAnniLabel.startAnimation(0, 1);

        final Resources res = getResources();
        size = res.getDimensionPixelSize(R.dimen.dimen_60dp);
        velocitySlow = res.getDimensionPixelOffset(R.dimen.default_velocity_slow);
        velocityNormal = res.getDimensionPixelOffset(R.dimen.default_velocity_normal);
        bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.ic_two_person),
                size, size, false);

        new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                activeConfettiManagers.add(generateStream());
            }
        }.start();

        //blink animation
//        Animation singleButtonAnimation = AnimationUtils.loadAnimation(this, R.anim.tween);
//        mSinglePersonButton.startAnimation(singleButtonAnimation);

        //blink animation
//        Animation twoButtonAnimation = AnimationUtils.loadAnimation(this, R.anim.tween);
//        mTwoPersonButton.startAnimation(twoButtonAnimation);

        //blink animation
//        Animation memoButtonAnimation = AnimationUtils.loadAnimation(this, R.anim.tween);
//        mTogetherButton.startAnimation(memoButtonAnimation);

    }

    private ConfettiManager generateStream() {
        return getConfettiManager().setNumInitialCount(0)
                .setEmissionDuration(3000)
                .setEmissionRate(20)
                .animate();
    }

    public long dayBetween(Date today, Date thatDay) {
        long diff = (today.getTime() - thatDay.getTime()) / 86400000;
        return diff;
    }

    private ConfettiManager getConfettiManager() {
        final ConfettiSource source = new ConfettiSource(0, -size, mMainLayout.getWidth(), -size);
        return new ConfettiManager(this, this, source, mMainLayout)
                .setVelocityX(0, velocitySlow)
                .setVelocityY(velocityNormal, velocitySlow)
                .setRotationalVelocity(180, 90)
                .setTouchEnabled(true);
    }

    @Override
    public Confetto generateConfetto(Random random) {
        return new BitmapConfetto(bitmap);
    }


    private void initTheViews() {
        mMainLayout = findViewById(R.id.main_layout);
        mTogetherButton = findViewById(R.id.button_together);
        mSinglePersonButton = findViewById(R.id.button_single_person);
        mTwoPersonButton = findViewById(R.id.button_two_person);
        mButtonLogin = findViewById(R.id.btn_upload);
        mAnniLabel = findViewById(R.id.txt_anni_label);
        mAnniDateTextView = findViewById(R.id.txt_anni_date);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_upload:
                Intent uploadIntent = new Intent(MainActivity.this, PhotoUploadActivity.class);
                startActivity(uploadIntent);
                return;
            case R.id.button_single_person:
                if (Util.isNetworkAvailable(MainActivity.this)) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ImagesShowFragment imagesShowFragment = ImagesShowFragment.newInstance("single");
                    imagesShowFragment.show(ft, "imageShowFragment");
                } else {
                    Toast.makeText(this, "There is no internet connection!!!", Toast.LENGTH_SHORT).show();
                }

                return;
            case R.id.button_two_person:
                if (Util.isNetworkAvailable(MainActivity.this)) {
                    FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                    ImagesShowFragment imagesShowFragment1 = ImagesShowFragment.newInstance("two");
                    imagesShowFragment1.show(ft1, "imageShowFragment");
                } else {
                    Toast.makeText(this, "There is no internet connection!!!", Toast.LENGTH_SHORT).show();
                }

                return;
            case R.id.button_together:
                if (Util.isNetworkAvailable(MainActivity.this)) {
                    FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                    ImagesShowFragment imagesShowFragment2 = ImagesShowFragment.newInstance("together");
                    imagesShowFragment2.show(ft2, "imageShowFragment");
                } else {
                    Toast.makeText(this, "There is no internet connection!!!", Toast.LENGTH_SHORT).show();
                }
                return;
            default:
                break;
        }
    }
}
