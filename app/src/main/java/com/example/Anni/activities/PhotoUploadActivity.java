package com.example.Anni.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.Anni.R;
import com.example.Anni.model.Upload;
import com.example.Anni.util.Util;
import com.example.Anni.widget.ImageRotateAndCompress;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.util.List;

import static com.example.Anni.util.Util.getColorWithAlpha;
import static com.example.Anni.util.Util.getImageUri;

public class PhotoUploadActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PhotoUploadActivity";

    private EditText mEditTextContent;
    private Button mButtonUpload, mButtonPhotoChoose;
    private ImageView mImagePreview;
    private ProgressBar mUploadProgress;
    private ConstraintLayout mUploadLayout;
    private Spinner mImageTypeSpinner;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri, mUploadImageUri;
    private String mImageType;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUplaodTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_upload);

        //init the views
        initTheViews();

        // mUploadLayout.setBackgroundColor(getColorWithAlpha(getResources().getColor(R.color.colorAccent), 0.5f));

        //upDown Scrolling
        mEditTextContent.setMovementMethod(new ScrollingMovementMethod());

//        mEditTextContent.setInputType(InputType.TYPE_CLASS_TEXT);
//        mEditTextContent.requestFocus();
//        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        mgr.showSoftInput(mEditTextContent, InputMethodManager.SHOW_FORCED);

        //click events
        mButtonUpload.setOnClickListener(this);
        mButtonPhotoChoose.setOnClickListener(this);


        //init the spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.images_type)) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView) view.findViewById(android.R.id.text1)).setText("");
                    ((TextView) view.findViewById(android.R.id.text1)).setHintTextColor(getResources().getColor(R.color.colorWhite1000));
                    ((TextView) view.findViewById(android.R.id.text1)).setHint(getItem(getCount()));
                }
                return view;
            }

            @Override
            public int getCount() {
                return super.getCount() - 1;
            }
        };
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mImageTypeSpinner.setAdapter(spinnerAdapter);
        mImageTypeSpinner.setSelection(spinnerAdapter.getCount());
        mImageTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) view.findViewById(android.R.id.text1)).setTextColor(getResources().getColor(R.color.colorWhite1000));
                if (adapterView.getItemAtPosition(i).equals("Choose Image Types")) {
                    mImageType = "";
                } else {
                    mImageType = adapterView.getItemAtPosition(i).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //TODO Auto-generated method stub
            }
        });

        //progress color
        mUploadProgress.setProgressTintList(ColorStateList.valueOf(Color.RED));

//        Drawable progressDrawable = mUploadProgress.getProgressDrawable().mutate();
//        progressDrawable.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
//        mUploadProgress.setProgressDrawable(progressDrawable);
    }

    private void initTheViews() {
        mEditTextContent = findViewById(R.id.editText_content);
        mButtonPhotoChoose = findViewById(R.id.button_photo_choose);
        mButtonUpload = findViewById(R.id.button_upload);
        mImagePreview = findViewById(R.id.image_preview);
        mUploadProgress = findViewById(R.id.upload_progress);
        mUploadLayout = findViewById(R.id.upload_layout);
        mImageTypeSpinner = findViewById(R.id.img_type);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_photo_choose:
                requestMultiplePermissions();
                return;
            case R.id.button_upload:
                if (Util.isNetworkAvailable(PhotoUploadActivity.this)){
                    dataUpload();
                }else {
                    Toast.makeText(this, "There is no internet connection", Toast.LENGTH_SHORT).show();
                }
                return;
            default:
                break;
        }
    }

    private void dataUpload() {
        if (!TextUtils.isEmpty(mImageType)) {
            if (mImageType.equalsIgnoreCase("Single Person Photo")) {
                mStorageRef = FirebaseStorage.getInstance().getReference("single_person_uploads");
                mDatabaseRef = FirebaseDatabase.getInstance().getReference("single_person_uploads");

                if (mUplaodTask != null && mUplaodTask.isInProgress()) {
                    Toast.makeText(PhotoUploadActivity.this, "Upload in Progress...", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }

            } else if (mImageType.equalsIgnoreCase("Two Person Photo")) {
                mStorageRef = FirebaseStorage.getInstance().getReference("two_person_uploads");
                mDatabaseRef = FirebaseDatabase.getInstance().getReference("two_person_uploads");

                if (mUplaodTask != null && mUplaodTask.isInProgress()) {
                    Toast.makeText(PhotoUploadActivity.this, "Upload in Progress...", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
            } else {
                mStorageRef = FirebaseStorage.getInstance().getReference("together_memo_uploads");
                mDatabaseRef = FirebaseDatabase.getInstance().getReference("together_uploads");

                if (mUplaodTask != null && mUplaodTask.isInProgress()) {
                    Toast.makeText(PhotoUploadActivity.this, "Upload in Progress...", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
            }
        } else {
            Animation shakeAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
            mImageTypeSpinner.startAnimation(shakeAnimation);
            Toast.makeText(this, "You must choose image type", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadFile() {
        if (mUploadImageUri != null) {
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(mUploadImageUri));
            mUplaodTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mUploadProgress.setProgress(0);
                                }
                            }, 500);
                            Toast.makeText(PhotoUploadActivity.this, "Upload Successful", Toast.LENGTH_SHORT).show();

                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Upload upload = new Upload(mEditTextContent.getText().toString().trim(), uri.toString());
                                    String uploadId = mDatabaseRef.push().getKey();
                                    mDatabaseRef.child(uploadId).setValue(upload);
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PhotoUploadActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mUploadProgress.setProgress((int) progress);
                        }
                    });
        }
    }

    private void requestMultiplePermissions() {
        Dexter.withActivity(PhotoUploadActivity.this)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            imagePickFromGallery();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            openSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void imagePickFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void openSettingsDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(PhotoUploadActivity.this);
        builder.setTitle(getResources().getString(R.string.settingDialogTitle));
        builder.setMessage(getResources().getString(R.string.settingDialogMessage));
        builder.setPositiveButton("GO TO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Bitmap rotateImageBitmap;
            try {
                rotateImageBitmap = ImageRotateAndCompress.handleSamplingAndRotationBitmap(getApplicationContext(), mImageUri);
                mUploadImageUri = getImageUri(getApplicationContext(), rotateImageBitmap);

                Glide.with(getApplicationContext()).load(mUploadImageUri).into(mImagePreview);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

}
