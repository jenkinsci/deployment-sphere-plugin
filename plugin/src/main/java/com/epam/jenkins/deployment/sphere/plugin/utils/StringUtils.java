package com.epam.jenkins.deployment.sphere.plugin.utils;

public final class StringUtils {

    private StringUtils() {
        throw new AssertionError();
    }

    public static String upperCase(String str) {
        if (str == null) {
            return null;
        }
        return str.toUpperCase();
    }

}
