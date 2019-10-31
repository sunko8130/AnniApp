package com.example.Anni.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.example.Anni.App;
import com.example.Anni.R;
import com.example.Anni.util.MMFontUtils;

import org.mmtextview.components.MMTextView;


public class CaptionFragment extends BottomSheetDialogFragment {

    private static final String TAG = "CaptionFragment";

    public static CaptionFragment newInstance() {
        CaptionFragment f = new CaptionFragment();
        return f;
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        final View contentView = View.inflate(getContext(), R.layout.caption_layout, null);
        dialog.setContentView(contentView);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        final CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
            contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    contentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int height = contentView.getMeasuredHeight();
                    ((BottomSheetBehavior) behavior).setPeekHeight(height);
                }
            });
        }

        String captions = (String) getArguments().getSerializable("photo_caption");
        int position = getArguments().getInt("position");
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        MMTextView textViewCaption = contentView.findViewById(R.id.txt_caption);
        if (captions != null) {
            String caption = captions.replace("\n", "<br />");
            textViewCaption.setText(caption);
        }
    }
}