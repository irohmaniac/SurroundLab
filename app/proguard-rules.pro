# SurroundLab ProGuard / R8 Rules (Play Store Release)

# kotlinx.serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keep,includedescriptorclasses class de.surroundlab.app.**$$serializer { *; }
-keepclassmembers class de.surroundlab.app.** {
    *** Companion;
}
-keepclasseswithmembers class de.surroundlab.app.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep @kotlinx.serialization.Serializable class de.surroundlab.app.** { *; }

# Ktor Server & Client (CIO + WebSockets + ContentNegotiation)
-keep class io.ktor.** { *; }
-keep class kotlinx.io.** { *; }
-dontwarn io.ktor.**
-dontwarn kotlinx.io.**
-dontwarn org.slf4j.**
-dontwarn java.lang.management.**
-dontwarn javax.naming.**

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# ZXing QR Code Core
-keep class com.google.zxing.** { *; }
-dontwarn com.google.zxing.**
