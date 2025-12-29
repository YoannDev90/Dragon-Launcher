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

# Do not mignify any Workspace-related stuff dur to boot crashes
-keep class org.elnix.dragonlauncher.ui.drawer.WorkspaceState { *; }
-keep class org.elnix.dragonlauncher.ui.drawer.Workspace { *; }
-keep class org.elnix.dragonlauncher.ui.drawer.AppOverride { *; }

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep enum * { *; }



# Was in cas of the serialization bug was still active, but this ain't the case anymore
# so I'll just keep the mignifications for a lighter app

## Keep field names for Gson serialization
#-keepclassmembers,allowobfuscation class * {
#    @com.google.gson.annotations.SerializedName <fields>;
#}
#
## Alternative: Keep ALL field names in models (more aggressive)
##-keepclassmembers class org.elnix.dragonlauncher.ui.drawer.** {
##    <fields>;
##}
#
## Keep AppModel class structure entirely
#-keep class org.elnix.dragonlauncher.ui.drawer.AppModel {
#    <fields>;
#    <init>(...);
#}


# Gson essentials
-keepattributes Signature
-keepattributes *Annotation*

# Keep CircleNest structure
-keep class org.elnix.dragonlauncher.data.CircleNest { <fields>; }


# Keep adapters
-keep class org.elnix.dragonlauncher.data.SwipeActionAdapter { *; }
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
