import re

with open('app/src/main/java/com/photo/pose/photoshoot/cliq/PCliq_fragments/PCliq_FragmentProfile.java', 'r', encoding='utf-8') as f:
    code = f.read()

code = code.replace('R.drawable.pcliq_user', 'R.drawable.pcliq_ic_profile_placeholder')

# Add imports
if 'import android.net.Uri;' not in code:
    code = code.replace('import android.os.Bundle;', 'import android.os.Bundle;\nimport android.net.Uri;\nimport android.widget.Toast;\nimport android.widget.LinearLayout;')

setup_methods = """
    private void setupSettings(View rootView) {
        LinearLayout ll_consent = rootView.findViewById(R.id.ll_consent);
        androidx.recyclerview.widget.RecyclerView rv_pages = rootView.findViewById(R.id.rv_pages);
        androidx.appcompat.widget.SwitchCompat switch_noti = rootView.findViewById(R.id.switch_noti);
        androidx.appcompat.widget.SwitchCompat switch_consent = rootView.findViewById(R.id.switch_consent);
        TextView tv_rateapp = rootView.findViewById(R.id.tv_rateapp);
        TextView tv_moreapp = rootView.findViewById(R.id.tv_moreapp);
        TextView tv_cachesize = rootView.findViewById(R.id.tv_cachesize);
        TextView tv_shareapp = rootView.findViewById(R.id.tv_shareapp);
        TextView tv_about = rootView.findViewById(R.id.tv_about);
        LinearLayout ll_clearcache = rootView.findViewById(R.id.ll_cache);
        View view_moreapp = rootView.findViewById(R.id.view_moreapp);
        View rl_moreapp = rootView.findViewById(R.id.rl_moreapp);

        if (getString(R.string.play_more_apps).isEmpty()) {
            if (view_moreapp != null) view_moreapp.setVisibility(View.GONE);
            if (rl_moreapp != null) rl_moreapp.setVisibility(View.GONE);
            if (tv_moreapp != null) tv_moreapp.setVisibility(View.GONE);
        }

        if (switch_noti != null) {
            switch_noti.setChecked(sharedPref.getIsNotification());
            switch_noti.setOnCheckedChangeListener((buttonView, isChecked) -> {
                com.onesignal.OneSignal.unsubscribeWhenNotificationsAreDisabled(!isChecked);
                sharedPref.setIsNotification(isChecked);
            });
        }

        if (switch_consent != null) {
            switch_consent.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    com.google.ads.consent.ConsentInformation.getInstance(getActivity()).setConsentStatus(com.google.ads.consent.ConsentStatus.PERSONALIZED);
                } else {
                    com.google.ads.consent.ConsentInformation.getInstance(getActivity()).setConsentStatus(com.google.ads.consent.ConsentStatus.NON_PERSONALIZED);
                }
            });
        }

        if (tv_rateapp != null) {
            tv_rateapp.setOnClickListener(v -> {
                final String appName = getActivity().getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
                }
            });
        }

        if (tv_moreapp != null) {
            tv_moreapp.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.play_more_apps)))));
        }

        if (tv_about != null) {
            tv_about.setOnClickListener(v -> startActivity(new Intent(getActivity(), com.photo.pose.photoshoot.cliq.PCliq_Activity.PCliq_AboutActivity.class)));
        }

        if (tv_shareapp != null) {
            tv_shareapp.setOnClickListener(v -> {
                Intent ishare = new Intent(Intent.ACTION_SEND);
                ishare.setType("text/plain");
                ishare.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.app_name) + " - http://play.google.com/store/apps/details?id=" + getActivity().getPackageName());
                startActivity(ishare);
            });
        }

        if (ll_clearcache != null && tv_cachesize != null) {
            tv_cachesize.setText(android.text.format.Formatter.formatFileSize(getActivity(), getCacheFolderSize(getActivity().getCacheDir())));
            ll_clearcache.setOnClickListener(v -> {
                com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_CustomProgressDialog progressDialog = new com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_CustomProgressDialog(getActivity());
                progressDialog.setMessage(getString(R.string.clearing_cache));
                progressDialog.show();
                new android.os.Handler().postDelayed(() -> {
                    clearCache();
                    tv_cachesize.setText("0.00 MB");
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), getString(R.string.cache_cleared), Toast.LENGTH_SHORT).show();
                }, 1000);
            });
        }

        if (rv_pages != null) {
            rv_pages.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getActivity()));
            rv_pages.setAdapter(new com.photo.pose.photoshoot.cliq.PCliq_adapter.PCliq_AdapterPages(getActivity(), com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_Constant.arrayListPages));
        }
    }

    private void clearCache() {
        try {
            java.io.File dir = getActivity().getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean deleteDir(java.io.File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new java.io.File(dir, child));
                if (!success) return false;
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    private long getCacheFolderSize(java.io.File dir) {
        long size = 0;
        if (dir != null && dir.isDirectory()) {
            for (java.io.File file : dir.listFiles()) {
                if (file.isFile()) size += file.length();
                else size += getCacheFolderSize(file);
            }
        } else if (dir != null && dir.isFile()) {
            size += dir.length();
        }
        return size;
    }
"""

last_brace = code.rfind('}')
new_code = code[:last_brace] + setup_methods + '\n}\n'

new_code = new_code.replace('return rootView;', '        setupSettings(rootView);\n        return rootView;', 1)

with open('app/src/main/java/com/photo/pose/photoshoot/cliq/PCliq_fragments/PCliq_FragmentProfile.java', 'w', encoding='utf-8') as f:
    f.write(new_code)

print('Success')
