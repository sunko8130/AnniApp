package com.example.Anni.adapter;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.icu.lang.UProperty;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.Anni.R;
import com.example.Anni.model.Upload;
import com.example.Anni.widget.AdjustableImageView;

import java.util.ArrayList;
import java.util.List;

public class ImageGalleryAdapter extends RecyclerView.Adapter<ImageGalleryAdapter.ImageGalleryViewHolder> {

    private List<Upload> galleryImagesList;
    private Context mContext;
    private int screenWidth;
    private OnItemClickListener mListener;

    public ImageGalleryAdapter(Context mContext) {
        this.mContext = mContext;
        galleryImagesList = new ArrayList<>();

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
    }

    public void setGalleryImagesList(List<Upload> imagesList) {
//        galleryImagesList.addAll(imagesList);
        this.galleryImagesList = imagesList;
        notifyDataSetChanged();
    }

    public void clearAll() {
        if (galleryImagesList != null) {
            galleryImagesList.clear();
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public ImageGalleryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.image_gallery_layout, viewGroup, false);
        return new ImageGalleryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageGalleryViewHolder imageGalleryViewHolder, int i) {
        imageGalleryViewHolder.bindImageGalleryDatas(galleryImagesList.get(i));
    }

    @Override
    public int getItemCount() {
        return galleryImagesList.size();
    }

    public class ImageGalleryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AdjustableImageView mGalleryImageView;
        ProgressBar mHolderProgress;

        public ImageGalleryViewHolder(@NonNull View itemView) {
            super(itemView);
            //init the views
            mGalleryImageView = itemView.findViewById(R.id.image_gallery);
            mHolderProgress = itemView.findViewById(R.id.holder_progress);
            //click actions
            itemView.setOnClickListener(this);
        }

        private void bindImageGalleryDatas(Upload uploadData) {
            if (uploadData != null) {
                /**
                 * Using Glide to handle image loading.
                 */
                Glide.with(mContext)
                        .load(uploadData.getImageUrl())
                        .apply(new RequestOptions()
                                .override(screenWidth / 2, 550)
                                .dontAnimate())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object
                                    model, Target<Drawable> target, boolean isFirstResource) {
                                mHolderProgress.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object
                                    model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                mHolderProgress.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(mGalleryImageView);
            }

        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(position, galleryImagesList);
                }
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int pos, List<Upload> upload);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mListener = onItemClickListener;
    }
}
