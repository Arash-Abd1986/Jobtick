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
-ignorewarnings
-keepattributes *Annotation*
#-keepclassmembers,allowobfuscation class * {
#    @com.google.gson.annotations.SerializedName <fields>;
#}

#-keepclassmembers class * implements android.os.Parcelable {
#      public static final android.os.Parcelable$Creator *;
#   }
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
-keep class com.crashlytics.*.* { *; }
-keepattributes Signature
# POJOs used with GSON
# The variable names are JSON key values and should not be obfuscated
#-keepclassmembers class com.jobtick.android.models.response.myjobs.MyJobsResponse { <fields>; }
#-keepclassmembers class com.jobtick.android.models.response.conversationinfo.GetConversationInfoResponse { <fields>; }
#-keepclassmembers class com.jobtick.android.models.* { <fields>; }
# You can apply the rule to all the affected classes also
 -keepclassmembers class com.example.apps.android.model.** { *; }

 # GSON
 -dontnote com.google.gson.**
 # GSON TypeAdapters are only referenced in annotations so ProGuard doesn't find their method usage
 -keepclassmembers,allowobfuscation,includedescriptorclasses class * extends com.google.gson.TypeAdapter {
     public <methods>;
 }
 # GSON TypeAdapterFactory is an interface, we need to keep the entire class, not just its members
 -keep,allowobfuscation,includedescriptorclasses class * implements com.google.gson.TypeAdapterFactory
 # GSON JsonDeserializer and JsonSerializer are interfaces, we need to keep the entire class, not just its members
 -keep,allowobfuscation,includedescriptorclasses class * implements com.google.gson.JsonDeserializer
 -keep,allowobfuscation,includedescriptorclasses class * implements com.google.gson.JsonSerializer
 # Ensure that all fields annotated with SerializedName will be kept
 -keepclassmembers,allowobfuscation class * {
     @com.google.gson.annotations.SerializedName <fields>;
 }




 ##---------------Begin: proguard configuration for Gson  ----------
 # Gson uses generic type information stored in a class file when working with fields. Proguard
 # removes such information by default, so configure it to keep all of it.
 -keepattributes Signature

 # Gson specific classes
 -keep class com.google.gson.stream.** { *; }

 # Application classes that will be serialized/deserialized over Gson
 # -keep class mypersonalclass.data.model.** { *; }



 ##---------------Begin: proguard configuration for Gson  ----------
 # Gson uses generic type information stored in a class file when working with fields. Proguard
 # removes such information by default, so configure it to keep all of it.
 -keepattributes Signature

 # For using GSON @Expose annotation
 -keepattributes *Annotation*

 # Gson specific classes
 -dontwarn sun.misc.**
 #-keep class com.google.gson.stream.** { *; }

 # Application classes that will be serialized/deserialized over Gson
 -keep class com.google.gson.examples.android.model.** { <fields>; }

 # Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
 # JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
 -keep class * extends com.google.gson.TypeAdapter
 -keep class * implements com.google.gson.TypeAdapterFactory
 -keep class * implements com.google.gson.JsonSerializer
 -keep class * implements com.google.gson.JsonDeserializer

 # Prevent R8 from leaving Data object members always null
 -keepclassmembers,allowobfuscation class * {
   @com.google.gson.annotations.SerializedName <fields>;
 }

 # Retain generic signatures of TypeToken and its subclasses with R8 version 3.0 and higher.
 -keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
 -keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken

 ##---------------End: proguard configuration for Gson  ----------
