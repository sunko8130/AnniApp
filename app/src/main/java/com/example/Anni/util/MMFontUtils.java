package com.example.Anni.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.widget.TextView;

import com.example.Anni.App;
import com.example.Anni.widget.CustomTypefaceSpan;


public class MMFontUtils {
    private static Typeface mmZawgyiTypeFace;
    private static Typeface mmUnicodeTypeFace;

    @SuppressLint("StaticFieldLeak")
    private static Context context = App.getContext();

    static {
        mmZawgyiTypeFace = Typeface.createFromAsset(context.getAssets(), "fonts/Zawgyi-One.ttf");
        mmUnicodeTypeFace = Typeface.createFromAsset(context.getAssets(), "fonts/mm3.ttf");
    }

    public static void setZawgyiMMFont(TextView view) {
        view.setTypeface(mmZawgyiTypeFace);
        SpannableString mText = new SpannableString(view.getText());
        mText.setSpan(new CustomTypefaceSpan("", mmZawgyiTypeFace), 0, mText.length(), 0);
        view.setText(mText);
    }

    public static void setUnicodeMMFont(TextView view) {
        view.setTypeface(mmUnicodeTypeFace);
        SpannableString mText = new SpannableString(view.getText());
        mText.setSpan(new CustomTypefaceSpan("", mmUnicodeTypeFace), 0, mText.length(), 0);
        view.setText(mText);
    }

}
