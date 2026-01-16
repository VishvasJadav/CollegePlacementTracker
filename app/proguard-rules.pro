# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# ==================== GENERAL ====================
# Preserve line number information for debugging stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep generic signatures for reflection
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes InnerClasses

# ==================== ROOM DATABASE ====================
# Keep Room classes
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *
-keep class * extends androidx.room.RoomDatabase$Callback
-dontwarn androidx.room.paging.**

# Keep Room query result classes
-keep class * extends androidx.room.RoomDatabase
-keepclassmembers class * {
    @androidx.room.* <methods>;
}

# ==================== DATA CLASSES ====================
# Keep all data classes in the app package
-keep class com.example.collegeplacementtracker.** { *; }
-keepclassmembers class com.example.collegeplacementtracker.** {
    <fields>;
    <methods>;
}

# Keep Parcelable implementations
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator CREATOR;
}

# ==================== VIEWMODELS ====================
# Keep ViewModels
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keep class * extends androidx.lifecycle.AndroidViewModel { *; }

# ==================== LIVEDATA ====================
# Keep LiveData and StateFlow
-keep class androidx.lifecycle.** { *; }
-dontwarn androidx.lifecycle.**

# ==================== COROUTINES ====================
# Keep coroutines
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

# ==================== GSON ====================
# Keep Gson classes
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Keep classes that use Gson annotations
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }

# ==================== TIMBER ====================
# Keep Timber
-keep class timber.log.** { *; }
-dontwarn timber.log.**

# ==================== COIL (IMAGE LOADING) ====================
# Keep Coil
-keep class coil.** { *; }
-dontwarn coil.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

# ==================== MPANDROIDCHART ====================
# Keep MPAndroidChart
-keep class com.github.mikephil.charting.** { *; }
-dontwarn com.github.mikephil.charting.**

# ==================== ITEXT (PDF GENERATION) ====================
# Keep iText
-keep class com.itextpdf.** { *; }
-dontwarn com.itextpdf.**
-keep class com.lowagie.** { *; }
-dontwarn com.lowagie.**

# ==================== LOTTIE ====================
# Keep Lottie
-keep class com.airbnb.lottie.** { *; }
-dontwarn com.airbnb.lottie.**

# ==================== WORK MANAGER ====================
# Keep WorkManager
-keep class androidx.work.** { *; }
-dontwarn androidx.work.**

# ==================== BIOMETRIC ====================
# Keep Biometric
-keep class androidx.biometric.** { *; }
-dontwarn androidx.biometric.**

# ==================== SECURITY CRYPTO ====================
# Keep Security Crypto
-keep class androidx.security.crypto.** { *; }
-dontwarn androidx.security.crypto.**

# ==================== RETROFIT (IF ADDING NETWORK LAYER) ====================
# Uncomment if adding Retrofit
#-keepattributes Exceptions, Signature, InnerClasses
#-keep class retrofit2.** { *; }
#-keepclasseswithmembers class * {
#    @retrofit2.http.* <methods>;
#}
#-dontwarn retrofit2.**
#-dontwarn okhttp3.**
#-dontwarn okio.**

# ==================== WEBVIEW (IF USING) ====================
# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# ==================== NATIVE METHODS ====================
# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# ==================== ENUMS ====================
# Keep enum classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ==================== R CLASS ====================
# Keep R class
-keepclassmembers class **.R$* {
    public static <fields>;
}

# ==================== REMOVE LOGGING IN RELEASE ====================
# Remove logging in release builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# Remove Timber logging in release builds
-assumenosideeffects class timber.log.Timber* {
    public static *** v(...);
    public static *** d(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}