package com.photo.pose.photoshoot.cliq.PCliq_Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.photo.pose.photoshoot.cliq.R;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_APIClient;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_APIInterface;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_ItemUserList;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Constant;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Methods;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_SharedPref;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PCliq_RegisterActivity extends AppCompatActivity {

    PCliq_Methods PC_methods;
    EditText PC_editText_name, PC_editText_email, PC_editText_pass, PC_editText_cpass, PC_editText_phone;
    Button PC_button_register;
    com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_CustomProgressDialog PC_progressDialog;
    PCliq_SharedPref PC_sharedPref;
    android.widget.TextView tv_error_name, tv_error_email, tv_error_pass, tv_error_cpass, tv_error_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pcliq_activity_register);

        PC_sharedPref = new PCliq_SharedPref(this);
        PC_methods = new PCliq_Methods(this);
        PC_methods.setStatusColor(getWindow());
        PC_methods.forceRTLIfSupported(getWindow());

        PC_progressDialog = new com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_CustomProgressDialog(this);
        PC_progressDialog = new com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_CustomProgressDialog(PCliq_RegisterActivity.this);
        PC_progressDialog.setMessage(getResources().getString(R.string.registering));
        PC_progressDialog.setCancelable(false);

        View rootLayout = findViewById(R.id.ll_register_root);
        if (rootLayout != null) {
            androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
                int navBarHeight = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.navigationBars()).bottom;
                v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), navBarHeight + (int) (16 * getResources().getDisplayMetrics().density));
                return insets;
            });
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        PC_button_register = findViewById(R.id.button_register);
        PC_editText_name = findViewById(R.id.et_regis_name);
        PC_editText_email = findViewById(R.id.et_regis_email);
        PC_editText_pass = findViewById(R.id.et_regis_password);
        PC_editText_cpass = findViewById(R.id.et_regis_cpassword);
        PC_editText_phone = findViewById(R.id.et_regis_phone);

        tv_error_name = findViewById(R.id.tv_error_name);
        tv_error_email = findViewById(R.id.tv_error_email);
        tv_error_pass = findViewById(R.id.tv_error_pass);
        tv_error_cpass = findViewById(R.id.tv_error_cpass);
        tv_error_phone = findViewById(R.id.tv_error_phone);

        PC_button_register.setOnClickListener(view -> {
            if (validate()) {
                loadRegister();
            }
        });

    }

    private Boolean validate() {
        tv_error_name.setVisibility(View.INVISIBLE);
        tv_error_email.setVisibility(View.INVISIBLE);
        tv_error_pass.setVisibility(View.INVISIBLE);
        tv_error_cpass.setVisibility(View.INVISIBLE);
        tv_error_phone.setVisibility(View.INVISIBLE);

        String name = PC_editText_name.getText().toString().trim();
        String email = PC_editText_email.getText().toString().trim();
        String pass = PC_editText_pass.getText().toString();
        String cpass = PC_editText_cpass.getText().toString();
        String phone = PC_editText_phone.getText().toString().trim();

        if (name.isEmpty()) {
            tv_error_name.setText(getResources().getString(R.string.enter_name));
            tv_error_name.setVisibility(View.VISIBLE);
            PC_editText_name.requestFocus();
            return false;
        } else if (email.isEmpty()) {
            tv_error_email.setText(getResources().getString(R.string.enter_email));
            tv_error_email.setVisibility(View.VISIBLE);
            PC_editText_email.requestFocus();
            return false;
        } else if (!isEmailValid(email)) {
            tv_error_email.setText(getString(R.string.error_invalid_email));
            tv_error_email.setVisibility(View.VISIBLE);
            PC_editText_email.requestFocus();
            return false;
        } else if (pass.isEmpty()) {
            tv_error_pass.setText(getResources().getString(R.string.enter_password));
            tv_error_pass.setVisibility(View.VISIBLE);
            PC_editText_pass.requestFocus();
            return false;
        } else if (pass.length() < 6) {
            tv_error_pass.setText("Password must be at least 6 characters long");
            tv_error_pass.setVisibility(View.VISIBLE);
            PC_editText_pass.requestFocus();
            return false;
        } else if (!pass.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            tv_error_pass.setText("Password must contain at least one symbol");
            tv_error_pass.setVisibility(View.VISIBLE);
            PC_editText_pass.requestFocus();
            return false;
        } else if (pass.endsWith(" ")) {
            tv_error_pass.setText(getResources().getString(R.string.pass_end_space));
            tv_error_pass.setVisibility(View.VISIBLE);
            PC_editText_pass.requestFocus();
            return false;
        } else if (cpass.isEmpty()) {
            tv_error_cpass.setText(getResources().getString(R.string.enter_cpassword));
            tv_error_cpass.setVisibility(View.VISIBLE);
            PC_editText_cpass.requestFocus();
            return false;
        } else if (!pass.equals(cpass)) {
            tv_error_cpass.setText(getResources().getString(R.string.pass_nomatch));
            tv_error_cpass.setVisibility(View.VISIBLE);
            PC_editText_cpass.requestFocus();
            return false;
        } else if (phone.isEmpty()) {
            tv_error_phone.setText(getResources().getString(R.string.enter_phone));
            tv_error_phone.setVisibility(View.VISIBLE);
            PC_editText_phone.requestFocus();
            return false;
        } else if (phone.length() < 10) {
            tv_error_phone.setText("Please enter a valid 10-digit mobile number");
            tv_error_phone.setVisibility(View.VISIBLE);
            PC_editText_phone.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@") && !email.contains(" ");
    }

    private void loadRegister() {
        if (PC_methods.isNetworkAvailable()) {
            PC_progressDialog.show();

            Call<PCliq_ItemUserList> call = PCliq_APIClient.getClient().create(PCliq_APIInterface.class).getRegistration(PC_methods.getAPIRequest(PCliq_Constant.URL_REGISTRATION, 0, "", "", "", "", "", "", PC_editText_name.getText().toString(), PC_editText_email.getText().toString(), PC_editText_pass.getText().toString(), PC_editText_phone.getText().toString(), "", ""));
            call.enqueue(new Callback<PCliq_ItemUserList>() {
                @Override
                public void onResponse(@NonNull Call<PCliq_ItemUserList> call, @NonNull Response<PCliq_ItemUserList> response) {
                    if (response.body() != null && response.body().getArrayListUser() != null && response.body().getArrayListUser().size() > 0) {
                        switch (response.body().getArrayListUser().get(0).getSuccess()) {
                            case "1":
                                Toast.makeText(PCliq_RegisterActivity.this, response.body().getArrayListUser().get(0).getMessage(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(PCliq_RegisterActivity.this, PCliq_LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("from", "");
                                startActivity(intent);
                                finish();
                                break;
                            case "-1":
                                PC_methods.getVerifyDialog(getString(R.string.error_unauth_access), response.body().getArrayListUser().get(0).getMessage());
                                break;
                            default:
                                if (response.body().getArrayListUser().get(0).getMessage().contains("already") || response.body().getArrayListUser().get(0).getMessage().contains("Invalid email format")) {
                                    PC_editText_email.setError(response.body().getArrayListUser().get(0).getMessage());
                                    PC_editText_email.requestFocus();
                                } else {
                                    Toast.makeText(PCliq_RegisterActivity.this, response.body().getArrayListUser().get(0).getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                break;
                        }
                    } else {
                        Toast.makeText(PCliq_RegisterActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    }
                    PC_progressDialog.dismiss();
                }

                @Override
                public void onFailure(@NonNull Call<PCliq_ItemUserList> call, @NonNull Throwable t) {
                    call.cancel();
                    Toast.makeText(PCliq_RegisterActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    PC_progressDialog.dismiss();
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.internet_not_connected), Toast.LENGTH_SHORT).show();
        }
    }
}