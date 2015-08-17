package com.epam.jenkins.deployment.sphere.plugin.utils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public final class DateFormatUtil {

    public static final String DATE_FORMAT = "kk:mm:ss MMMM dd, yyyy";

    private DateFormatUtil() {
    }

    public static String formatDate(final DateTime dateTime) {
        return dateTime.toString(DATE_FORMAT);
    }

    public static DateTime toDate(final String dateTimeString) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(DATE_FORMAT);
        return dateTimeFormatter.parseDateTime(dateTimeString);
    }
}
