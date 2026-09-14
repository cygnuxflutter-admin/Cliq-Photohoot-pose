import re
import os

filepath = r"app/src/main/java/com/photo/pose/photoshoot/cliq/MyApplication.java"

with open(filepath, "r", encoding="utf-8") as f:
    content = f.read()

# Add imports
imports = """
import android.app.Activity;
import android.app.Dialog;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
"""

if "import android.net.ConnectivityManager;" not in content:
    content = content.replace("import android.app.Activity;", imports)

# Add variables and logic inside the class
variables = """
    private Dialog noInternetDialog;
    private Activity currentActivity;
    private boolean isNetworkConnected = true;

    private void setupGlobalNetworkListener() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {}
            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                currentActivity = activity;
                updateNetworkDialogVisibility();
            }
            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                currentActivity = activity;
                updateNetworkDialogVisibility();
            }
            @Override
            public void onActivityPaused(@NonNull Activity activity) {}
            @Override
            public void onActivityStopped(@NonNull Activity activity) {}
            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}
            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                if (currentActivity == activity) {
                    currentActivity = null;
                }
            }
        });

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkRequest networkRequest = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build();
            connectivityManager.registerNetworkCallback(networkRequest, new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull Network network) {
                    isNetworkConnected = true;
                    updateNetworkDialogVisibility();
                }

                @Override
                public void onLost(@NonNull Network network) {
                    isNetworkConnected = false;
                    updateNetworkDialogVisibility();
                }
            });
        }
    }

    private void updateNetworkDialogVisibility() {
        if (currentActivity != null) {
            currentActivity.runOnUiThread(() -> {
                if (isNetworkConnected) {
                    if (noInternetDialog != null && noInternetDialog.isShowing()) {
                        noInternetDialog.dismiss();
                    }
                } else {
                    if (noInternetDialog == null || noInternetDialog.getContext() != currentActivity) {
                        if (noInternetDialog != null && noInternetDialog.isShowing()) {
                            noInternetDialog.dismiss();
                        }
                        noInternetDialog = new Dialog(currentActivity, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                        noInternetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        noInternetDialog.setContentView(R.layout.pcliq_layout_no_internet);
                        noInternetDialog.setCancelable(false);
                        Window window = noInternetDialog.getWindow();
                        if (window != null) {
                            window.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
                            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                        }
                    }
                    if (!noInternetDialog.isShowing() && !currentActivity.isFinishing()) {
                        noInternetDialog.show();
                    }
                }
            });
        }
    }

    public static RequestQueue getLocalRequestQueue() {
"""

if "setupGlobalNetworkListener()" not in content:
    content = content.replace("    public static RequestQueue getLocalRequestQueue() {", variables)
    content = content.replace("super.onCreate();", "super.onCreate();\n        setupGlobalNetworkListener();")

with open(filepath, "w", encoding="utf-8") as f:
    f.write(content)
