package com.photo.pose.photoshoot.cliq.PCliq_Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.photo.pose.photoshoot.cliq.PCliq_adManager.PCliq_LoadAds;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_NetworkUtils;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_PreferenceClass;
import com.photo.pose.photoshoot.cliq.R;
import com.squareup.picasso.Picasso;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_APIClient;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_APIInterface;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_ItemUserList;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Constant;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Methods;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_SharedPref;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PCliq_ProfileEditActivity extends AppCompatActivity {

    Toolbar PC_toolbar;
    PCliq_Methods PC_methods;
    PCliq_SharedPref PC_sharedPref;
    ImageView PC_iv_profile;
    EditText PC_editText_name, PC_editText_email, PC_editText_phone, PC_editText_pass, PC_editText_cpass;
    String PC_imagePath = "";
    com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_CustomProgressDialog PC_progressDialog;
    int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pcliq_activity_profile_edit);

        PC_sharedPref = new PCliq_SharedPref(this);
        PC_methods = new PCliq_Methods(this);
        PC_methods.forceRTLIfSupported(getWindow());
        PC_methods.setStatusColor(getWindow());

        PC_progressDialog = new com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_CustomProgressDialog(this);
        PC_progressDialog.setMessage(getResources().getString(R.string.loading));
        PC_progressDialog.setCancelable(false);

        PC_toolbar = findViewById(R.id.toolbar_proedit);
        setSupportActionBar(PC_toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (PC_toolbar.getNavigationIcon() != null) {
                PC_toolbar.getNavigationIcon().setTint(ContextCompat.getColor(this, R.color.text_espresso));
            }
        }

        RelativeLayout rl_ad = this.findViewById(R.id.rl_ad);
        if (PCliq_NetworkUtils.isNetworkAvailable(this)) {
            if (new PCliq_PreferenceClass(PCliq_ProfileEditActivity.this).getInt("BannerAdStatus") == 1) {
                PCliq_LoadAds.loadAdmobBannerAd(this, rl_ad);
            } else {
                rl_ad.setVisibility(View.GONE);
            }
        }

        AppCompatButton button_update = findViewById(R.id.button_prof_update);
        PC_iv_profile = findViewById(R.id.iv_profile);
        PC_editText_name = findViewById(R.id.editText_profedit_name);
        PC_editText_email = findViewById(R.id.editText_profedit_email);
        PC_editText_phone = findViewById(R.id.editText_profedit_phone);
        PC_editText_pass = findViewById(R.id.editText_profedit_password);
        PC_editText_cpass = findViewById(R.id.editText_profedit_cpassword);

        if(PC_sharedPref.getLoginType().equals(PCliq_Constant.LOGIN_TYPE_NORMAL)) {
            PC_editText_cpass.setEnabled(true);
            PC_editText_pass.setEnabled(true);
        } else {
            if(!PC_sharedPref.getEmail().equals("")) {
                PC_editText_email.setEnabled(false);
            }
            PC_editText_cpass.setEnabled(false);
            PC_editText_pass.setEnabled(false);
        }

        setProfileVar();

        button_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    loadUpdateProfile();
                }
            }
        });

        PC_iv_profile.setOnClickListener(v -> {
            if (PC_methods.checkPer()) {
                pickImage();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private Boolean validate() {
        PC_editText_name.setError(null);
        PC_editText_email.setError(null);
        PC_editText_cpass.setError(null);
        if (PC_editText_name.getText().toString().trim().isEmpty()) {
            PC_editText_name.setError(getString(R.string.cannot_empty));
            PC_editText_name.requestFocus();
            return false;
        } else if (PC_editText_email.getText().toString().trim().isEmpty()) {
            PC_editText_email.setError(getString(R.string.email_empty));
            PC_editText_email.requestFocus();
            return false;
        } else if (PC_editText_pass.getText().toString().endsWith(" ")) {
            PC_editText_pass.setError(getString(R.string.pass_end_space));
            PC_editText_pass.requestFocus();
            return false;
        } else if (!PC_editText_pass.getText().toString().trim().equals(PC_editText_cpass.getText().toString().trim())) {
            PC_editText_cpass.setError(getString(R.string.pass_nomatch));
            PC_editText_cpass.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private void updateArray(String image) {
        PC_sharedPref.setUserName(PC_editText_name.getText().toString());
        PC_sharedPref.setEmail(PC_editText_email.getText().toString());
        PC_sharedPref.setUserMobile(PC_editText_phone.getText().toString());
        PC_sharedPref.setUserImage(image);

        if (!PC_editText_pass.getText().toString().equals("")) {
            PC_sharedPref.setRemeber(false);
        }
    }

    private void loadUpdateProfile() {
        if (PC_methods.isNetworkAvailable()) {

            PC_progressDialog.show();

            File file = null;


            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            builder.addFormDataPart("data", PC_methods.getAPIRequest(PCliq_Constant.URL_PROFILE_UPDATE,0,"","","","","","",PC_editText_name.getText().toString(),PC_editText_email.getText().toString(),PC_editText_pass.getText().toString(),PC_editText_phone.getText().toString(), PC_sharedPref.getUserId(), ""));
            if (PC_imagePath != null && !PC_imagePath.equals("")) {
                file = new File(PC_imagePath);
                builder.addFormDataPart("user_image",  file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
            }

            RequestBody requestBody = builder.build();
            Call<PCliq_ItemUserList> call = PCliq_APIClient.getClient().create(PCliq_APIInterface.class).getProfileUpdate(requestBody);
            call.enqueue(new Callback<PCliq_ItemUserList>() {
                @Override
                public void onResponse(@NonNull Call<PCliq_ItemUserList> call, @NonNull Response<PCliq_ItemUserList> response) {
                    if (response.body() != null && response.body().getArrayListUser() != null && response.body().getArrayListUser().size() > 0) {
                        if (response.body().getArrayListUser().get(0).getSuccess().equals("1")) {
                            updateArray(response.body().getArrayListUser().get(0).getImage());
                            PC_imagePath = "";
                            PCliq_Constant.isUpdate = true;
                            finish();
                            Toast.makeText(PCliq_ProfileEditActivity.this, response.body().getArrayListUser().get(0).getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            if (response.body().getArrayListUser().get(0).getMessage().contains("Email address already used")) {
                                PC_editText_email.setError(response.body().getArrayListUser().get(0).getMessage());
                                PC_editText_email.requestFocus();
                            }
                        }
                    } else {
                        Toast.makeText(PCliq_ProfileEditActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    }
                    PC_progressDialog.dismiss();
                }

                @Override
                public void onFailure(@NonNull Call<PCliq_ItemUserList> call, @NonNull Throwable t) {
                    call.cancel();
                    Toast.makeText(PCliq_ProfileEditActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    PC_progressDialog.dismiss();
                }
            });
        } else {
            Toast.makeText(PCliq_ProfileEditActivity.this, getString(R.string.internet_not_connected), Toast.LENGTH_SHORT).show();
        }
    }

    public void setProfileVar() {
        PC_editText_name.setText(PC_sharedPref.getUserName());
        PC_editText_phone.setText(PC_sharedPref.getUserMobile());
        PC_editText_email.setText(PC_sharedPref.getEmail());

        if(!PC_sharedPref.getUserImage().equals("")) {
            Picasso.get()
                    .load(PC_sharedPref.getUserImage())
                    .into(PC_iv_profile);
        }
    }

    private void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_image)), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            PC_imagePath = PC_methods.getPathImage(uri);
            PC_iv_profile.setImageURI(uri);
        }
    }
}
