package com.photo.pose.photoshoot.cliq.PCliq_Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.onesignal.OneSignal;
import com.photo.pose.photoshoot.cliq.R;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_APIClient;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_APIInterface;
import com.photo.pose.photoshoot.cliq.PCliq_apiservices.PCliq_ItemUserList;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Constant;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_CustomProgressDialog;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Methods;
import com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_SharedPref;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import cn.refactor.library.SmoothCheckBox;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PCliq_LoginActivity extends AppCompatActivity {

    private String from = "";

    PCliq_SharedPref sharedPref;
    EditText editText_email, editText_password;
    Button button_login;
    TextView button_skip, tv_sign_up;
    TextView textView_forgotpass;
    PCliq_Methods methods;
    PCliq_CustomProgressDialog progressDialog;
    LinearLayout ll_checkbox;
    SmoothCheckBox cb_rememberme;
    private FirebaseAuth mAuth;
    android.widget.TextView tv_error_email, tv_error_pass;

    /*Facebook Login*/
    LoginButton loginButtonFB;
    CallbackManager callbackManager;

    PCliq_APIInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pcliq_activity_login);

        apiInterface = PCliq_APIClient.getClient().create(PCliq_APIInterface.class);

        mAuth = FirebaseAuth.getInstance();

        from = getIntent().getStringExtra("from");

        sharedPref = new PCliq_SharedPref(this);
        methods = new PCliq_Methods(this);
        methods.forceRTLIfSupported(getWindow());
        methods.setStatusColor(getWindow());

        progressDialog = new PCliq_CustomProgressDialog(PCliq_LoginActivity.this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);

        View topBar = findViewById(R.id.rl_login_top_bar);
        View rootLayout = findViewById(R.id.ll_login_root);
        if (rootLayout != null) {
            androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
                int statusBarHeight = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.statusBars()).top;
                int navBarHeight = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.navigationBars()).bottom;
                if (topBar != null) {
                    topBar.setPadding(topBar.getPaddingLeft(), statusBarHeight + (int) (8 * getResources().getDisplayMetrics().density), topBar.getPaddingRight(), topBar.getPaddingBottom());
                }
                v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), navBarHeight + (int) (16 * getResources().getDisplayMetrics().density));
                return insets;
            });
        }

        loginButtonFB = findViewById(R.id.login_button);
        loginButtonFB.setReadPermissions(Arrays.asList("email"));
        callbackManager = CallbackManager.Factory.create();

        ll_checkbox = findViewById(R.id.ll_checkbox);
        cb_rememberme = findViewById(R.id.cb_rememberme);
        editText_email = findViewById(R.id.et_login_email);
        editText_password = findViewById(R.id.et_login_password);
        button_login = findViewById(R.id.button_login);
        button_skip = findViewById(R.id.button_skip);
        textView_forgotpass = findViewById(R.id.tv_forgotpass);
        tv_sign_up = findViewById(R.id.tv_sign_up);
        
        tv_error_email = findViewById(R.id.tv_error_email);
        tv_error_pass = findViewById(R.id.tv_error_pass);

        if (sharedPref.getIsRemember()) {
            editText_email.setText(sharedPref.getEmail());
            editText_password.setText(sharedPref.getPassword());
        }

        ll_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cb_rememberme.setChecked(!cb_rememberme.isChecked());
            }
        });

        button_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMainActivity();
            }
        });

        textView_forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showReportDialog();
            }
        });

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        tv_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PCliq_LoginActivity.this, PCliq_RegisterActivity.class));
            }
        });


    }

    private void attemptLogin() {
        tv_error_email.setVisibility(View.INVISIBLE);
        tv_error_pass.setVisibility(View.INVISIBLE);

        // Store values at the time of the login attempt.
        String email = editText_email.getText().toString();
        String password = editText_password.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            tv_error_pass.setText(getString(R.string.enter_password));
            tv_error_pass.setVisibility(View.VISIBLE);
            focusView = editText_password;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            tv_error_pass.setText(getString(R.string.error_password_sort));
            tv_error_pass.setVisibility(View.VISIBLE);
            focusView = editText_password;
            cancel = true;
        } else if (password.endsWith(" ")) {
            tv_error_pass.setText(getString(R.string.pass_end_space));
            tv_error_pass.setVisibility(View.VISIBLE);
            focusView = editText_password;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            tv_error_email.setText(getString(R.string.cannot_empty));
            tv_error_email.setVisibility(View.VISIBLE);
            focusView = editText_email;
            cancel = true;
        } else if (!isEmailValid(email)) {
            tv_error_email.setText(getString(R.string.error_invalid_email));
            tv_error_email.setVisibility(View.VISIBLE);
            focusView = editText_email;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            loadLogin();
        }
    }

    private void loadLogin() {
        if (methods.isNetworkAvailable()) {

            progressDialog.show();

            Call<PCliq_ItemUserList> call = apiInterface.getLogin(methods.getAPIRequest(PCliq_Constant.URL_LOGIN, 0, "", "", "", "", "", "", "", editText_email.getText().toString(), editText_password.getText().toString(), "", "", ""));
            call.enqueue(new Callback<PCliq_ItemUserList>() {
                @Override
                public void onResponse(@NonNull Call<PCliq_ItemUserList> call, @NonNull Response<PCliq_ItemUserList> response) {
                    if (response.body() != null && response.body().getArrayListUser() != null && response.body().getArrayListUser().size() > 0) {
                        if (response.body().getArrayListUser().get(0).getSuccess().equals("1")) {
                            sharedPref.setLoginDetails(response.body().getArrayListUser().get(0).getId(), response.body().getArrayListUser().get(0).getName(), response.body().getArrayListUser().get(0).getMobile(), editText_email.getText().toString(), response.body().getArrayListUser().get(0).getImage(), "", cb_rememberme.isChecked(), editText_password.getText().toString(), PCliq_Constant.LOGIN_TYPE_NORMAL);
                            sharedPref.setIsLogged(true);
                            sharedPref.setIsAutoLogin(true);
                            openMainActivity();
                            Toast.makeText(PCliq_LoginActivity.this, response.body().getArrayListUser().get(0).getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            String errorMsg = response.body().getArrayListUser().get(0).getMessage();
                            tv_error_pass.setText(errorMsg);
                            tv_error_pass.setVisibility(View.VISIBLE);
                            editText_password.requestFocus();
                        }
                    } else {
                        Toast.makeText(PCliq_LoginActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(@NonNull Call<PCliq_ItemUserList> call, @NonNull Throwable t) {
                    call.cancel();
                    Toast.makeText(PCliq_LoginActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        } else {
            Toast.makeText(PCliq_LoginActivity.this, getString(R.string.internet_not_connected), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@") && !email.contains(" ");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 0;
    }

    private void openMainActivity() {
        Intent intent = new Intent(PCliq_LoginActivity.this, PCliq_MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void loadLoginSocial(final String loginType, final String name, String email, final String authId) {
        if (methods.isNetworkAvailable()) {

            progressDialog.show();

            Call<PCliq_ItemUserList> call = apiInterface.getSocialLogin(methods.getAPIRequest(PCliq_Constant.URL_SOCIAL_LOGIN, 0, authId, loginType, "", "", "", "", name, email, "", "", "", ""));
            call.enqueue(new Callback<PCliq_ItemUserList>() {
                @Override
                public void onResponse(@NonNull Call<PCliq_ItemUserList> call, @NonNull Response<PCliq_ItemUserList> response) {
                    if (response.body() != null && response.body().getArrayListUser() != null && response.body().getArrayListUser().size() > 0) {

                        switch (response.body().getArrayListUser().get(0).getSuccess()) {
                            case "1":
                                sharedPref.setLoginDetails(response.body().getArrayListUser().get(0).getId(), response.body().getArrayListUser().get(0).getName(), "", email, "", authId, cb_rememberme.isChecked(), "", loginType);
                                sharedPref.setIsLogged(true);
                                sharedPref.setIsAutoLogin(true);

                                OneSignal.sendTag("user_id", response.body().getArrayListUser().get(0).getId());

                                Toast.makeText(PCliq_LoginActivity.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();

//                                if (from.equals("app")) {
//                                    finish();
//                                } else {
                                openMainActivity();
//                                }
                                break;
                            case "-1":
                                methods.getVerifyDialog(getString(R.string.error_unauth_access), response.body().getArrayListUser().get(0).getMessage());
                                break;
                            default:
                                if (response.body().getArrayListUser().get(0).getMessage().contains("already") || response.body().getArrayListUser().get(0).getMessage().contains("Invalid email format")) {
                                    editText_email.setError(response.body().getArrayListUser().get(0).getMessage());
                                    editText_email.requestFocus();
                                } else {
                                    Toast.makeText(PCliq_LoginActivity.this, response.body().getArrayListUser().get(0).getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                try {
                                    if (loginType.equals(PCliq_Constant.LOGIN_TYPE_FB)) {
                                        LoginManager.getInstance().logOut();
                                    } else if (loginType.equals(PCliq_Constant.LOGIN_TYPE_GOOGLE)) {
                                        FirebaseAuth.getInstance().signOut();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    } else {
                        Toast.makeText(PCliq_LoginActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(@NonNull Call<PCliq_ItemUserList> call, @NonNull Throwable t) {
                    call.cancel();
                    Toast.makeText(PCliq_LoginActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        } else {
            Toast.makeText(PCliq_LoginActivity.this, getString(R.string.internet_not_connected), Toast.LENGTH_SHORT).show();
        }
    }

    private void getUserProfile(AccessToken currentAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                currentAccessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            String first_name = "", email = "", last_name = "";

                            if (object.has("first_name")) {
                                first_name = object.getString("first_name");
                            }
                            if (object.has("last_name")) {
                                last_name = object.getString("last_name");
                            }
                            if (object.has("email")) {
                                email = object.getString("email");
                            }
                            String id = object.getString("id");
                            loadLoginSocial(PCliq_Constant.LOGIN_TYPE_FB, first_name + " " + last_name, email, id);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();

    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            loadLoginSocial(PCliq_Constant.LOGIN_TYPE_GOOGLE, user.getDisplayName(), user.getEmail(), user.getUid());
                        } else {
                            Toast.makeText(PCliq_LoginActivity.this, "Failed to Sign IN", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showReportDialog() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.pcliq_layout_forgot_password, null);

        BottomSheetDialog dialog_forgot_pass = new BottomSheetDialog(PCliq_LoginActivity.this);
        dialog_forgot_pass.setContentView(view);
        dialog_forgot_pass.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        dialog_forgot_pass.show();

        final EditText et_email = dialog_forgot_pass.findViewById(R.id.et_forgot_email);
        MaterialButton button_submit = dialog_forgot_pass.findViewById(R.id.button_forgot_send);

        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (methods.isNetworkAvailable()) {
                    if (!et_email.getText().toString().trim().isEmpty()) {
                        dialog_forgot_pass.dismiss();
                        loadForgotPass(et_email.getText().toString());
                    } else {
                        Toast.makeText(PCliq_LoginActivity.this, getString(R.string.enter_email), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PCliq_LoginActivity.this, getString(R.string.internet_not_connected), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadForgotPass(String email) {
        progressDialog.show();

        Call<PCliq_ItemUserList> call = PCliq_APIClient.getClient().create(PCliq_APIInterface.class).getForgotPassword(methods.getAPIRequest(PCliq_Constant.URL_FORGOT_PASSWORD, 0, "", "", "", "", "", "", "", email, "", "", "", ""));
        call.enqueue(new Callback<PCliq_ItemUserList>() {
            @Override
            public void onResponse(@NonNull Call<PCliq_ItemUserList> call, @NonNull Response<PCliq_ItemUserList> response) {
                if (response.body() != null && response.body().getArrayListUser() != null && response.body().getArrayListUser().size() > 0) {
                    Toast.makeText(PCliq_LoginActivity.this, response.body().getArrayListUser().get(0).getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PCliq_LoginActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<PCliq_ItemUserList> call, @NonNull Throwable t) {
                call.cancel();
                Toast.makeText(PCliq_LoginActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 112) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            try {
                if (resultCode != 0) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    firebaseAuthWithGoogle(task.getResult().getIdToken());
                } else {
                    Toast.makeText(PCliq_LoginActivity.this, getString(R.string.error_login_goole), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e("TAG", "onActivityResult: " + e.getMessage());
                Toast.makeText(PCliq_LoginActivity.this, getString(R.string.error_login_goole), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}