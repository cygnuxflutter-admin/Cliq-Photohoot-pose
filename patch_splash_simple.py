import re

filepath = r"app/src/main/java/com/photo/pose/photoshoot/cliq/PCliq_Activity/PCliq_SplashActivity.java"

with open(filepath, "r", encoding="utf-8") as f:
    content = f.read()

# Replace errorDialog with openLoginActivity in getAppDetails
content = content.replace("""        } else {
            errorDialog(getString(R.string.internet_not_connected), getString(R.string.error_connect_net_tryagain));
        }""", """        } else {
            openLoginActivity();
        }""")

# Find getData and add else branch
# In getData(), there is:
#     private void getData() {
#         if (PCliq_NetworkUtils.isNetworkAvailable(this)) {
# We will just replace it with:
#     private void getData() {
#         if (PCliq_NetworkUtils.isNetworkAvailable(this)) { ... } else { next(); }

target = """                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    next();
                }
            });
        }
    }"""
replacement = """                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    next();
                }
            });
        } else {
            next();
        }
    }"""
content = content.replace(target, replacement)

with open(filepath, "w", encoding="utf-8") as f:
    f.write(content)
