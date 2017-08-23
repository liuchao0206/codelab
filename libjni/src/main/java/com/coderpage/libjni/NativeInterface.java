package com.coderpage.libjni;

/**
 * @author lc. 2017-08-23
 * @since 0.1.0
 */

public class NativeInterface {

    static {
        System.loadLibrary("native-lib");
    }

    public native String stringFromJni();

    public native int add(int a, int b);
}
