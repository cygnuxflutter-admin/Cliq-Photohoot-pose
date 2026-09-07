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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pcliq_activity_register);

        PC_sharedPref = new PCliq_SharedPref(this);
        PC_methods = new PCliq_Methods(this);
        PC_methods.setStatusColor(getWindow());
        PC_methods.forceRTLIfSupported(getWindow());

        PC_progressDialog = new com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_CustomProgressDialog(this);
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

        PC_button_register = findViewById(R.id.button_register);
        PC_editText_name = findViewById(R.id.et_regis_name);
        PC_editText_email = findViewById(R.id.et_regis_email);
        PC_editText_pass = findViewById(R.id.et_regis_password);
        PC_editText_cpass = findViewById(R.id.et_regis_cpassword);
        PC_editText_phone = findViewById(R.id.et_regis_phone);

        PC_button_register.setOnClickListener(view -> {
            if (validate()) {
                loadRegister();
            }
        });

    }

    private Boolean validate() {
        if (PC_editText_name.getText().toString().trim().isEmpty()) {
            PC_editText_name.setError(getResources().getString(R.string.enter_name));
            PC_editText_name.requestFocus();
            return false;
        } else if (PC_editText_email.getText().toString().trim().isEmpty()) {
            PC_editText_email.setError(getResources().getString(R.string.enter_email));
            PC_editText_email.requestFocus();
            return false;
        } else if (!isEmailValid(PC_editText_email.getText().toString())) {
            PC_editText_email.setError(getString(R.string.error_invalid_email));
            PC_editText_email.requestFocus();
            return false;
        } else if (PC_editText_pass.getText().toString().isEmpty()) {
            PC_editText_pass.setError(getResources().getString(R.string.enter_password));
            PC_editText_pass.requestFocus();
            return false;
        } else if (PC_editText_pass.getText().toString().endsWith(" ")) {
            PC_editText_pass.setError(getResources().getString(R.string.pass_end_space));
            PC_editText_pass.requestFocus();
            return false;
        } else if (PC_editText_cpass.getText().toString().isEmpty()) {
            PC_editText_cpass.setError(getResources().getString(R.string.enter_cpassword));
            PC_editText_cpass.requestFocus();
            return false;
        } else if (!PC_editText_pass.getText().toString().equals(PC_editText_cpass.getText().toString())) {
            PC_editText_cpass.setError(getResources().getString(R.string.pass_nomatch));
            PC_editText_cpass.requestFocus();
            return false;
        } else if (PC_editText_phone.getText().toString().trim().isEmpty()) {
            PC_editText_phone.setError(getResources().getString(R.string.enter_phone));
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