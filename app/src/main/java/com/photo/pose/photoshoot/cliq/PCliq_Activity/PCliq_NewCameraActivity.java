package com.photo.pose.photoshoot.cliq.PCliq_Activity;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.photo.pose.photoshoot.cliq.R;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_MultiTouchListener;
import com.google.android.cameraview.AspectRatio;
import com.google.android.cameraview.CameraView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

public class PCliq_NewCameraActivity extends AppCompatActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback,
        PCliq_AspectRatioFragment.Listener {

    private static final String TAG = "MainActivity";

    private static final int REQUEST_CAMERA_PERMISSION = 1;

    private static final String FRAGMENT_DIALOG = "dialog";

    private static final int[] FLASH_OPTIONS = {
            CameraView.FLASH_AUTO,
            CameraView.FLASH_OFF,
            CameraView.FLASH_ON,
    };

    private static final int[] FLASH_ICONS = {
            R.drawable.pcliq_ic_flash_auto,
            R.drawable.pcliq_ic_flash_off,
            R.drawable.pcliq_ic_flash_on,
    };

    private static final int[] FLASH_TITLES = {
            R.string.flash_auto,
            R.string.flash_off,
            R.string.flash_on,
    };

    private int mCurrentFlash;

    private CameraView mCameraView;

    private Handler mBackgroundHandler;
    ImageView imageView, switch_camera, aspect_ratio;
    boolean isposesketch;
    SeekBar seekBar;
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.take_picture) {
                if (mCameraView != null) {
                    mCameraView.takePicture();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pcliq_activity_new_camera);

        mCameraView = findViewById(R.id.camera);
        aspect_ratio = findViewById(R.id.aspect_ratio);
        switch_camera = findViewById(R.id.switch_camera);

        if (mCameraView != null) {
            mCameraView.addCallback(mCallback);
        }
        int previewHeight = mCameraView.getHeight();
        int previewWidth = mCameraView.getWidth();
        Log.e(TAG, "Height width : " + previewHeight + " 2 " + previewWidth);


        FloatingActionButton fab = findViewById(R.id.take_picture);
        if (fab != null) {
            fab.setOnClickListener(mOnClickListener);
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }


        String image = getIntent().getStringExtra("image");
        isposesketch = getIntent().getExtras().getBoolean("isposesketch");

        imageView = findViewById(R.id.iv_sketch);

        try {
            Glide.with(this)
                    .load(image)
                    .placeholder(R.drawable.pcliq_ic_placeholder_portrait)
                    .into(imageView);

        } catch (Exception e) {
            Log.e(TAG, "onCreate: Exception"+ e.getMessage() );
        }
        imageView.setOnTouchListener(new PCliq_MultiTouchListener());


        aspect_ratio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                if (mCameraView != null
                        && fragmentManager.findFragmentByTag(FRAGMENT_DIALOG) == null) {
                    final Set<AspectRatio> ratios = mCameraView.getSupportedAspectRatios();
                    final AspectRatio currentRatio = mCameraView.getAspectRatio();
                    PCliq_AspectRatioFragment.newInstance(ratios, currentRatio)
                            .show(fragmentManager, FRAGMENT_DIALOG);
                }
            }
        });
        switch_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCameraView != null) {
                    int facing = mCameraView.getFacing();
                    mCameraView.setFacing(facing == CameraView.FACING_FRONT ?
                            CameraView.FACING_BACK : CameraView.FACING_FRONT);
                }
            }
        });

        if (!isposesketch) {
            seekBar = (SeekBar) findViewById(R.id.seekBar);
            seekBar.setVisibility(View.VISIBLE);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {
                    imageView.setAlpha(progress);
//                Toast.makeText(getApplicationContext(),"seekbar progress: "+progress, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
//                Toast.makeText(getApplicationContext(),"seekbar touch started!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
//                Toast.makeText(getApplicationContext(),"seekbar touch stopped!", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            mCameraView.start();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ConfirmationDialogFragment
                    .newInstance(R.string.camera_permission_confirmation,
                            new String[]{Manifest.permission.CAMERA},
                            REQUEST_CAMERA_PERMISSION,
                            R.string.camera_permission_not_granted)
                    .show(getSupportFragmentManager(), FRAGMENT_DIALOG);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        }
    }

    @Override
    protected void onPause() {
        mCameraView.stop();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBackgroundHandler != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                mBackgroundHandler.getLooper().quitSafely();
            } else {
                mBackgroundHandler.getLooper().quit();
            }
            mBackgroundHandler = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (permissions.length != 1 || grantResults.length != 1) {
                    throw new RuntimeException("Error on requesting camera permission.");
                }
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, R.string.camera_permission_not_granted,
                            Toast.LENGTH_SHORT).show();
                }
                // No need to start camera here; it is handled by onResume
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pcliq_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.switch_flash) {
            if (mCameraView != null) {
                mCurrentFlash = (mCurrentFlash + 1) % FLASH_OPTIONS.length;
                item.setTitle(FLASH_TITLES[mCurrentFlash]);
                item.setIcon(FLASH_ICONS[mCurrentFlash]);
                mCameraView.setFlash(FLASH_OPTIONS[mCurrentFlash]);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onAspectRatioSelected(@NonNull AspectRatio ratio) {
        if (mCameraView != null) {
            Toast.makeText(this, ratio.toString(), Toast.LENGTH_SHORT).show();
            mCameraView.setAspectRatio(ratio);
        }
    }

    private Handler getBackgroundHandler() {
        if (mBackgroundHandler == null) {
            HandlerThread thread = new HandlerThread("background");
            thread.start();
            mBackgroundHandler = new Handler(thread.getLooper());
        }
        return mBackgroundHandler;
    }

    private CameraView.Callback mCallback
            = new CameraView.Callback() {

        @Override
        public void onCameraOpened(CameraView cameraView) {
            Log.d(TAG, "onCameraOpened");
        }

        @Override
        public void onCameraClosed(CameraView cameraView) {
            Log.d(TAG, "onCameraClosed");
        }

        @Override
        public void onPictureTaken(CameraView cameraView, final byte[] data) {
            Log.d(TAG, "onPictureTaken " + data.length);
            Toast.makeText(cameraView.getContext(), R.string.picture_taken, Toast.LENGTH_SHORT)
                    .show();

            Bitmap imageBitmap = BitmapFactory.decodeByteArray(data, 0,
                    data.length);

            Log.e(TAG, "onPictureTaken: " + imageBitmap);
            if (imageBitmap == null) {
                return;
            }

            // Create a new file to store the image
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = "IMG_" + timeStamp + ".jpg";

            File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Cliq Photo Poses");

            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }

            File imageFile = new File(storageDir, imageFileName);

            try {
                OutputStream os = new FileOutputStream(imageFile);
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.close();

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(imageFile);
                mediaScanIntent.setData(contentUri);
                sendBroadcast(mediaScanIntent);

                Toast.makeText(PCliq_NewCameraActivity.this, "Image saved to gallery", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                // Handle the error appropriately
                // For example, you can display an error message
                Toast.makeText(PCliq_NewCameraActivity.this, "Failed to save image" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
           /* File imageFile;
            try {
                String state = Environment.getExternalStorageState();
                File folder = null;
                if (state.contains(Environment.MEDIA_MOUNTED)) {
                    folder = new File(Environment
                            .getExternalStorageDirectory() + "/Demo");
                } else {
                    folder = new File(Environment
                            .getExternalStorageDirectory() + "/Demo");
                }

                boolean success = true;
                if (!folder.exists()) {
                    success = folder.mkdirs();
                }
                if (success) {
                    java.util.Date date = new java.util.Date();
                    imageFile = new File(folder.getAbsolutePath()
                            + File.separator
                            + new Timestamp(date.getTime()).toString()
                            + "Image.jpg");
                    Log.e("#FILENAME", String.valueOf(imageFile));
                    imageFile.createNewFile();
                } else {
                    Toast.makeText(getBaseContext(), "Image Not saved",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                ByteArrayOutputStream ostream = new ByteArrayOutputStream();

                // save image into gallery
                loadedImage.compress(Bitmap.CompressFormat.JPEG, 100, ostream);

                FileOutputStream fout = new FileOutputStream(imageFile);
                fout.write(ostream.toByteArray());
                fout.close();
                ContentValues values = new ContentValues();

                values.put(MediaStore.Images.Media.DATE_TAKEN,
                        System.currentTimeMillis());
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                values.put(MediaStore.MediaColumns.DATA, imageFile.getAbsolutePath());

                PCliq_NewCameraActivity.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                Toast.makeText(PCliq_NewCameraActivity.this, "saved successfully", Toast.LENGTH_LONG).show();
            } catch (Exception e) {

            }*/

        }

    };

    public static class ConfirmationDialogFragment extends DialogFragment {

        private static final String ARG_MESSAGE = "message";
        private static final String ARG_PERMISSIONS = "permissions";
        private static final String ARG_REQUEST_CODE = "request_code";
        private static final String ARG_NOT_GRANTED_MESSAGE = "not_granted_message";

        public static ConfirmationDialogFragment newInstance(@StringRes int message,
                                                             String[] permissions, int requestCode, @StringRes int notGrantedMessage) {
            ConfirmationDialogFragment fragment = new ConfirmationDialogFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_MESSAGE, message);
            args.putStringArray(ARG_PERMISSIONS, permissions);
            args.putInt(ARG_REQUEST_CODE, requestCode);
            args.putInt(ARG_NOT_GRANTED_MESSAGE, notGrantedMessage);
            fragment.setArguments(args);
            return fragment;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Bundle args = getArguments();
            return new AlertDialog.Builder(getActivity())
                    .setMessage(args.getInt(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String[] permissions = args.getStringArray(ARG_PERMISSIONS);
                                    if (permissions == null) {
                                        throw new IllegalArgumentException();
                                    }
                                    ActivityCompat.requestPermissions(getActivity(),
                                            permissions, args.getInt(ARG_REQUEST_CODE));
                                }
                            })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getActivity(),
                                            args.getInt(ARG_NOT_GRANTED_MESSAGE),
                                            Toast.LENGTH_SHORT).show();
                                }
                            })
                    .create();
        }

    }

}
