import os
import glob

layout_dir = 'app/src/main/res/layout/'
files = glob.glob(os.path.join(layout_dir, '*.xml'))

old_str = '<ProgressBar android:indeterminateTint="@color/gold_primary"'
new_str = '<com.photo.pose.photoshoot.cliq.PCliq_utils.PCliq_PhotoshootLoaderView'

for file_path in files:
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    if old_str in content:
        content = content.replace(old_str, new_str)
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"Reverted in {file_path}")

print("Done!")
