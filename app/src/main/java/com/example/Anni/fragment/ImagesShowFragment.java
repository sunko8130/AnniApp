package com.example.Anni.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Anni.R;
import com.example.Anni.adapter.ImageGalleryAdapter;
import com.example.Anni.model.Upload;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImagesShowFragment extends DialogFragment implements ImageGalleryAdapter.OnItemClickListener {

    private static final String TAG = "ImagesShowFragment";

    private RecyclerView mGalleryRecyclerView;
    private ProgressBar mProgressCircle;
    private View view;
    private Context mContext;
    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;
    private List<Upload> uploadList;
    private ImageGalleryAdapter mImageAdapter;
    private String mGalleryType;
    private TextView mNoPhotoTextView;

     ValueEventListener mDBListener;

    public static ImagesShowFragment newInstance(String galleryType) {
        ImagesShowFragment imagesShowFragment = new ImagesShowFragment();
        Bundle bundle = new Bundle();
        bundle.putString("GALLERY_TYPE", galleryType);
        imagesShowFragment.setArguments(bundle);
        return imagesShowFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_images_show, container, false);
        //init the views
        initTheViews();
        mContext = getActivity();

        mGalleryRecyclerView.setHasFixedSize(true);
        mGalleryRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));

        mGalleryType = getArguments().getString("GALLERY_TYPE");
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getUploadDatas();
    }

    private void getUploadDatas() {

        if (mGalleryType.equalsIgnoreCase("single")) {
            mDatabaseRef = FirebaseDatabase.getInstance().getReference("single_person_uploads");
            retrieveImagesAndTexts();
        } else if (mGalleryType.equalsIgnoreCase("two")) {
            mDatabaseRef = FirebaseDatabase.getInstance().getReference("two_person_uploads");
            retrieveImagesAndTexts();
        } else {
            mDatabaseRef = FirebaseDatabase.getInstance().getReference("together_uploads");
            retrieveImagesAndTexts();
        }
    }

    private void retrieveImagesAndTexts() {
        uploadList = new ArrayList<>();

        mImageAdapter = new ImageGalleryAdapter(mContext);
        mGalleryRecyclerView.setAdapter(mImageAdapter);
        mImageAdapter.setOnItemClickListener(this);

        mStorage = FirebaseStorage.getInstance();
        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    uploadList.clear();
                    mImageAdapter.clearAll();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Upload upload = dataSnapshot1.getValue(Upload.class);
                        upload.setmKey(dataSnapshot1.getKey());

                        uploadList.add(upload);
                        mImageAdapter.setGalleryImagesList(uploadList);
                        mImageAdapter.notifyDataSetChanged();
                        mProgressCircle.setVisibility(View.INVISIBLE);
                        mNoPhotoTextView.setVisibility(View.GONE);

                    }
                }else {
                    mNoPhotoTextView.setVisibility(View.VISIBLE);
                    mProgressCircle.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(mContext, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void initTheViews() {
        mGalleryRecyclerView = view.findViewById(R.id.gallery_recyclerView);
        mProgressCircle = view.findViewById(R.id.progress_circle);
        mNoPhotoTextView = view.findViewById(R.id.txt_no_photo);
    }

    @Override
    public void onItemClick(int pos, List<Upload> upload) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("upload", (Serializable) upload);
        bundle.putInt("position", pos);
        FragmentTransaction ft = ((AppCompatActivity) mContext).getSupportFragmentManager().beginTransaction();
        SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
        newFragment.setArguments(bundle);
        newFragment.show(ft, "slideshow");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }
}
