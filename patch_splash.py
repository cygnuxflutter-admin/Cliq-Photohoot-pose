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
getData_start = content.find("private void getData() {")
if getData_start != -1:
    getData_body_start = content.find("if (PCliq_NetworkUtils.isNetworkAvailable(this)) {", getData_start)
    if getData_body_start != -1:
        # We need to find the matching closing brace of this if block.
        brace_count = 0
        i = getData_body_start + content[getData_body_start:].find("{")
        while i < len(content):
            if content[i] == '{':
                brace_count += 1
            elif content[i] == '}':
                brace_count -= 1
                if brace_count == 0:
                    break
            i += 1
        
        # After 'i', insert the else block
        else_block = """ else {
            Intent intent;
            if (!cid.equals("")) {
                intent = new Intent(PCliq_SplashActivity.this, PCliq_PoseByCatActivity.class);
                intent.putExtra("cid", cid);
                intent.putExtra("cname", cname);
                intent.putExtra("from", "noti");
            } else {
                intent = new Intent(PCliq_SplashActivity.this, PCliq_MainActivity.class);
                intent.putExtra("from", "");
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }"""
        content = content[:i+1] + else_block + content[i+1:]

with open(filepath, "w", encoding="utf-8") as f:
    f.write(content)
