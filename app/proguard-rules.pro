# CleanCityApp R8 / ProGuard rules
# Keep only what reflection-based libraries require. Do not use broad -keep class **.

# Preserve useful crash stack traces without exposing original filenames.
-keepattributes SourceFile,LineNumberTable,Signature,InnerClasses,EnclosingMethod,*Annotation*,Exceptions
-renamesourcefileattribute SourceFile

# Retrofit — service interfaces are invoked via reflection.
-keep,allowobfuscation,allowshrinking interface * {
    @retrofit2.http.* <methods>;
}
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**

# Gson models used by Retrofit (AuthApi DTOs etc.).
# Keep members that Gson reflects on; allow class name obfuscation where safe.
-keepclassmembers class com.example.cleancityapp.data.remote.** {
    <fields>;
}
-keep class com.example.cleancityapp.data.remote.** { *; }
-dontwarn com.google.gson.**

# kotlinx.serialization — keep generated serializers and annotated models.
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keep,includedescriptorclasses class com.example.cleancityapp.**$$serializer { *; }
-keepclassmembers class com.example.cleancityapp.** {
    *** Companion;
}
-keepclasseswithmembers class com.example.cleancityapp.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep class kotlinx.serialization.** { *; }
-dontwarn kotlinx.serialization.**

# Koin — constructors are resolved reflectively for DI.
-keepclassmembers class * {
    public <init>(...);
}
-dontwarn org.koin.**

# Firebase Messaging
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# EncryptedSharedPreferences / Tink
-dontwarn com.google.crypto.tink.**
-keep class com.google.crypto.tink.** { *; }

# Compose / Navigation: no broad keeps needed when using explicit route strings.
