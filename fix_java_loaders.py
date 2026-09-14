import os
import glob
import re

java_dir = 'app/src/main/java/'
files = glob.glob(java_dir + '**/*.java', recursive=True)

for file_path in files:
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    original = content
    # Replace the import
    if 'import android.widget.ProgressBar;' in content:
        content = content.replace('import android.widget.ProgressBar;', 'import fr.castorflex.android.circularprogressbar.CircularProgressBar;\nimport android.widget.ProgressBar;')
    elif 'CircularProgressBar' not in content:
        # Just in case they don't have the import but use it
        content = re.sub(r'(import android.os.Bundle;)', r'\1\nimport fr.castorflex.android.circularprogressbar.CircularProgressBar;', content)
    
    # Replace variable declarations
    content = re.sub(r'\bProgressBar progressBar\b', 'CircularProgressBar progressBar', content)
    content = re.sub(r'\bProgressBar DprogressBar\b', 'CircularProgressBar DprogressBar', content)
    content = re.sub(r'\bProgressBar PprogressBar\b', 'CircularProgressBar PprogressBar', content)
    
    # Replace casting if any
    content = re.sub(r'\(ProgressBar\)', '(CircularProgressBar)', content)
    
    if content != original:
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"Fixed {file_path}")

print("Done!")
