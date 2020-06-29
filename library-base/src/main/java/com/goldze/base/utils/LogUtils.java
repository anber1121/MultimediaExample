package com.goldze.base.utils;

import android.util.Log;

public class LogUtils {
    static String className;
    static String methodName;
    static int lineNumber;

    public LogUtils() {
    }

    public static boolean isDebuggable() {
        return true;
    }

    private static String createLog(String log) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("================");
        buffer.append(methodName);
        buffer.append("(").append(className).append(":").append(lineNumber).append(")================:");
        buffer.append(log);
        return buffer.toString();
    }

    private static void getMethodNames(StackTraceElement[] sElements) {
        className = sElements[1].getFileName();
        methodName = sElements[1].getMethodName();
        lineNumber = sElements[1].getLineNumber();
    }

    public static void e(String message) {
        if (isDebuggable()) {
            getMethodNames((new Throwable()).getStackTrace());
            Log.e(className, createLog(message));
        }
    }

    public static void i(String message) {
        if (isDebuggable()) {
            getMethodNames((new Throwable()).getStackTrace());
            Log.i(className, createLog(message));
        }
    }

    public static void d(String message) {
        if (isDebuggable()) {
            getMethodNames((new Throwable()).getStackTrace());
            Log.d(className, createLog(message));
        }
    }

    public static void v(String message) {
        if (isDebuggable()) {
            getMethodNames((new Throwable()).getStackTrace());
            Log.v(className, createLog(message));
        }
    }

    public static void w(String message) {
        if (isDebuggable()) {
            getMethodNames((new Throwable()).getStackTrace());
            Log.w(className, createLog(message));
        }
    }
}
