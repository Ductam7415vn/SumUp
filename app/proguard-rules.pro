# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# PDFBox Android rules
-keep class com.tom_roush.pdfbox.** { *; }
-keep class org.apache.pdfbox.** { *; }
-keep class org.apache.fontbox.** { *; }
-keep class org.apache.commons.logging.** { *; }

# Keep PDFBox resources
-keep class com.tom_roush.pdfbox.android.PDFBoxResourceLoader { *; }
-keepclassmembers class com.tom_roush.pdfbox.android.PDFBoxResourceLoader {
    public static void init(android.content.Context);
}

# Prevent stripping of methods called via reflection
-keepclassmembers class * {
    @com.tom_roush.pdfbox.* <methods>;
}

# Keep font resources
-keep class com.tom_roush.fontbox.** { *; }
-dontwarn com.tom_roush.pdfbox.**
-dontwarn org.apache.**

# Retrofit & Gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class com.example.sumup.data.remote.dto.** { *; }
-keep class com.example.sumup.domain.model.** { *; }
-keepclassmembers class com.example.sumup.data.remote.dto.** {
    <fields>;
    <init>(...);
}

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Room Database
-keep class androidx.room.** { *; }
-keep @androidx.room.Database class * { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
-keepclassmembers class * {
    @androidx.room.* <methods>;
}

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.lifecycle.HiltViewModel { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }

# ML Kit
-keep class com.google.mlkit.** { *; }
-keep class com.google.android.gms.internal.mlkit_text_recognition.** { *; }

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# Keep generic type information for Kotlin
-keepattributes *Annotation*, InnerClasses
-keepattributes SourceFile,LineNumberTable
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Firebase
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# Mammoth library for DOCX processing
-keep class org.zwobble.mammoth.** { *; }
-keepclassmembers class org.zwobble.mammoth.** {
    <fields>;
    <methods>;
}
-dontwarn org.zwobble.mammoth.**

# Security - Obfuscate security-sensitive classes
-keep class com.example.sumup.data.remote.security.** { *; }
-keep class com.example.sumup.utils.ApiKeyManager { *; }
-keep class com.example.sumup.utils.EnhancedApiKeyManager { *; }

# Remove logs in release builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# Obfuscate API endpoints
-obfuscate class com.example.sumup.data.remote.api.** {
    *;
}

# Keep BuildConfig for app functionality
-keep class com.example.sumup.BuildConfig { *; }

# Extra obfuscation for release
-repackageclasses 'o'
-allowaccessmodification
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*