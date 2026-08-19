# Update .gitignore for Android Project

The goal is to provide a comprehensive and robust `.gitignore` file that follows Android and IntelliJ best practices. This will prevent build artifacts, local configurations, and sensitive files (like keystores) from being accidentally committed to the repository.

## Proposed Changes

### [Root]

#### [MODIFY] [.gitignore](file:///E:/Aayush/android/Cliq-Photohoot-pose-git/.gitignore)
Update the root `.gitignore` with a standard, comprehensive list of exclusions. The proposed content is:

```gitignore
# Built artifacts
/build
/*/build
*.apk
*.aar
*.jar
*.bundle

# Gradle files
.gradle/
gradle-wrapper.jar
!gradle-wrapper.properties

# Local configuration file (sdk path, etc)
local.properties

# Log/OS Files
*.log
.DS_Store
Thumbs.db

# Android Studio generated files and folders
captures/
.externalNativeBuild/
.cxx/
output-metadata.json

# IntelliJ
*.iml
.idea/
!/.idea/codeStyles/
!/.idea/copyright/
!/.idea/inspectionProfiles/
!/.idea/iconloader.xml

# Keystore files
*.jks
*.keystore
/cliq keystore/

# Google Services (e.g. APIs or Firebase)
google-services.json

# Android Profiling
*.hprof

# Secrets
secrets.properties
```

## Verification Plan

### Manual Verification
- Verify that the `.gitignore` covers:
    - `/build` folders in all modules.
    - `.gradle/` folder.
    - `local.properties`.
    - Keystore files (`*.jks`, `*.keystore`).
    - IDE files (`.idea/`, `*.iml`).
    - OS files (`.DS_Store`, `Thumbs.db`).
