# SumUp Deployment Guide

This guide covers the complete deployment process for the SumUp Android application.

## 📋 Table of Contents
1. [Pre-Deployment Checklist](#pre-deployment-checklist)
2. [Build Configuration](#build-configuration)
3. [Release Build Process](#release-build-process)
4. [App Signing](#app-signing)
5. [Google Play Store Deployment](#google-play-store-deployment)
6. [Post-Deployment](#post-deployment)

## ✅ Pre-Deployment Checklist

Before creating a release build, ensure:

- [ ] All features are tested and working
- [ ] API keys are properly configured for production
- [ ] ProGuard rules are up to date
- [ ] Version code and version name are updated
- [ ] All debug code is removed
- [ ] Analytics is properly configured
- [ ] Crash reporting is enabled
- [ ] App permissions are justified

## 🔧 Build Configuration

### 1. Update Version Information

Edit `app/build.gradle.kts`:

```kotlin
android {
    defaultConfig {
        versionCode = 2  // Increment for each release
        versionName = "2.0.0"  // Follow semantic versioning
    }
}
```

### 2. Configure Release Build Type

```kotlin
buildTypes {
    release {
        isMinifyEnabled = true
        isShrinkResources = true
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
        signingConfig = signingConfigs.getByName("release")
    }
}
```

### 3. API Configuration

Create a `release.properties` file (DO NOT commit):

```properties
GEMINI_API_KEY=your_production_api_key
```

## 🏗️ Release Build Process

### 1. Clean Build

```bash
./gradlew clean
```

### 2. Run All Tests

```bash
./gradlew test
./gradlew connectedAndroidTest
```

### 3. Generate Release APK

```bash
./gradlew assembleRelease
```

### 4. Generate App Bundle (Recommended)

```bash
./gradlew bundleRelease
```

Output location: `app/build/outputs/bundle/release/`

## 🔐 App Signing

### 1. Generate Signing Key

```bash
keytool -genkey -v -keystore sumup-release.keystore \
    -alias sumup -keyalg RSA -keysize 2048 -validity 10000
```

### 2. Configure Signing in Gradle

Create `keystore.properties` (DO NOT commit):

```properties
storePassword=your_store_password
keyPassword=your_key_password
keyAlias=sumup
storeFile=/path/to/sumup-release.keystore
```

Update `app/build.gradle.kts`:

```kotlin
android {
    signingConfigs {
        create("release") {
            val keystorePropertiesFile = rootProject.file("keystore.properties")
            if (keystorePropertiesFile.exists()) {
                val keystoreProperties = Properties()
                keystoreProperties.load(FileInputStream(keystorePropertiesFile))
                
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
                storeFile = file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
            }
        }
    }
}
```

## 📱 Google Play Store Deployment

### 1. Prepare Store Listing

Required assets:
- App icon (512x512 PNG)
- Feature graphic (1024x500 PNG)
- Screenshots (minimum 2, recommended 8)
- App description (short and full)
- Privacy policy URL
- Category selection

### 2. Create App Release

1. Log in to [Google Play Console](https://play.google.com/console)
2. Create new application
3. Fill in store listing details
4. Upload app bundle (.aab file)
5. Complete content rating questionnaire
6. Set pricing and distribution

### 3. Testing Tracks

Recommended release process:
1. **Internal Testing** - Team members only
2. **Closed Testing** - Beta testers (100-500 users)
3. **Open Testing** - Public beta
4. **Production** - Full release

### 4. Release Checklist

- [ ] App bundle uploaded
- [ ] Release notes written
- [ ] Target API level meets requirements
- [ ] Permissions declared and justified
- [ ] Data safety form completed
- [ ] Screenshots uploaded
- [ ] Description optimized for ASO

## 🚀 Post-Deployment

### 1. Monitor Release

- Check crash reports in Play Console
- Monitor user reviews and ratings
- Track install/uninstall metrics
- Review performance metrics

### 2. Crash Monitoring

Integrate Firebase Crashlytics:

```kotlin
dependencies {
    implementation("com.google.firebase:firebase-crashlytics-ktx")
}
```

### 3. Analytics Tracking

Key metrics to monitor:
- Daily Active Users (DAU)
- User retention (1, 7, 30 days)
- Feature usage (summarization types)
- Error rates
- API usage

### 4. Update Strategy

- **Hotfixes**: For critical bugs (x.x.1)
- **Minor updates**: Monthly features (x.1.0)
- **Major updates**: Quarterly releases (2.0.0)

## 🔒 Security Considerations

1. **API Keys**: Never hardcode in source
2. **ProGuard**: Obfuscate sensitive code
3. **Certificate Pinning**: For API calls
4. **Data Encryption**: For local storage
5. **Secure Communication**: HTTPS only

## 📝 Release Notes Template

```markdown
## What's New in Version X.X.X

### ✨ New Features
- Feature 1 description
- Feature 2 description

### 🐛 Bug Fixes
- Fixed issue with...
- Resolved crash when...

### 🎨 Improvements
- Enhanced performance of...
- Updated UI for...

Thank you for using SumUp!
```

## 🆘 Troubleshooting

### Build Failures
- Clean project: `./gradlew clean`
- Invalidate caches in Android Studio
- Check ProGuard rules for missing keeps

### Signing Issues
- Verify keystore path is absolute
- Check password correctness
- Ensure keystore file permissions

### Upload Failures
- Verify version code is incremented
- Check target API level requirements
- Ensure all permissions are declared

---

*For additional help, see [Launch Checklist](../development/launch-checklist.md)*