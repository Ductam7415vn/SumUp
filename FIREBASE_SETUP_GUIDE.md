# 🔥 Firebase Setup Guide for SumUp

## Prerequisites

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project or select existing one
3. Add Android app with package name: `com.example.sumup`

## Setup Steps

### 1. Download google-services.json
- In Firebase Console, go to Project Settings
- Under "Your apps" section, click on Android app
- Download `google-services.json`
- Place it in `app/` directory (same level as `app/build.gradle.kts`)

### 2. Enable Services
In Firebase Console, enable:
- ✅ Analytics (auto-enabled)
- ✅ Crashlytics (Go to Crashlytics section and enable)
- ✅ Performance Monitoring (Go to Performance section and enable)
- ✅ Remote Config (Optional - for feature flags)

### 3. SHA Certificate (Required for some features)
```bash
# Debug certificate
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android

# Add SHA1 and SHA256 to Firebase Console > Project Settings > Android app
```

### 4. Verify Installation
After adding google-services.json and syncing project:
1. Build and run the app
2. Check Firebase Console > Analytics > DebugView
3. You should see events coming in

## Important Notes

⚠️ **DO NOT** commit `google-services.json` to version control
Add to `.gitignore`:
```
app/google-services.json
```

## Next Steps
Once Firebase is connected, the app will automatically:
- Track screen views
- Report crashes with stack traces
- Monitor app performance
- Track custom events