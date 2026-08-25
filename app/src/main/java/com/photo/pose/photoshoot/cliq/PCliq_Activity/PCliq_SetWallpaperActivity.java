package com.photo.pose.photoshoot.cliq.PCliq_Activity;

import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.photo.pose.photoshoot.cliq.R;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Constant;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Methods;


public class PCliq_SetWallpaperActivity extends AppCompatActivity {

    PCliq_Methods methods;
    CropImageView imageView;
    FloatingActionButton button;
    Bitmap bmImg;
    com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_CustomProgressDialog progressDialog;
    BottomSheetDialog dialog_desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pcliq_activity_setwallpaper);

        methods = new PCliq_Methods(this);
        methods.setStatusColor(getWindow());
        methods.forceRTLIfSupported(getWindow());

        progressDialog = new com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_CustomProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));

        ImageView imageView_back = findViewById(R.id.iv_back_crop);
        imageView_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imageView = findViewById(R.id.iv_crop);
        button = findViewById(R.id.button_setwallpaper);

        imageView.setImageUriAsync(PCliq_Constant.uri_set);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class SetWall extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            progressDialog.show();
            bmImg = imageView.getCroppedImage();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            WallpaperManager myWallpaperManager = WallpaperManager.getInstance(getApplicationContext());
            try {
                myWallpaperManager.setWallpaperOffsetSteps(0, 0);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    String locktype = strings[0];
                    switch (locktype) {
                        case "home":
                            myWallpaperManager.setBitmap(bmImg, null, true, WallpaperManager.FLAG_SYSTEM);
                            break;
                        case "lock":
                            myWallpaperManager.setBitmap(bmImg, null, true, WallpaperManager.FLAG_LOCK);
                            break;
                        case "all":
                            myWallpaperManager.setBitmap(bmImg);
                            break;
                    }
                } else {
                    myWallpaperManager.setBitmap(bmImg);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "0";
            }
            return "1";
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("1")) {
                Toast.makeText(PCliq_SetWallpaperActivity.this, getString(R.string.wallpaper_set), Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(PCliq_SetWallpaperActivity.this, getString(R.string.err_set_wall), Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
            super.onPostExecute(s);
        }
    }

    public void showBottomSheetDialog() {
        View view = getLayoutInflater().inflate(R.layout.pcliq_layout_set_pose_op, null);

        dialog_desc = new BottomSheetDialog(this);
        dialog_desc.setContentView(view);
        dialog_desc.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        dialog_desc.show();

        TextView textView_home = dialog_desc.findViewById(R.id.tv_set_home);
        TextView textView_lock = dialog_desc.findViewById(R.id.tv_set_lock);
        TextView textView_all = dialog_desc.findViewById(R.id.tv_set_all);

        textView_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SetWall().execute("home");
            }
        });

        textView_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SetWall().execute("lock");
            }
        });

        textView_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SetWall().execute("all");
            }
        });
    }
}