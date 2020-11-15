package com.snail.antifake.jni;

public class JniUtil {

    static {
        System.loadLibrary("extrautil");
    }
    public static native String getSecret(String id, String time);
}
