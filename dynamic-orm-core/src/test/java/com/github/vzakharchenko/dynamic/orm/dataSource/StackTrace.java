package com.github.vzakharchenko.dynamic.orm.dataSource;

import org.apache.commons.lang3.ArrayUtils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 *
 */
public class StackTrace {
    public static final String DD_MM_YYYY_HH_MM_SS_SSSZ = "dd.MM.yyyy HH:mm:ss.SSSZ";
    public static final String STACK = " Stack: \n";
    public static final String TIME = "Time: ";

    private final String desc;
    private final long dateTime;
    private final StackTraceElement[] stackTraceElements;
    private int hashCode;

    public StackTrace(String desc, long dateTime, StackTraceElement[] traceElement) {
        this.desc = desc;
        this.dateTime = dateTime;
        stackTraceElements = ArrayUtils.clone(traceElement);
    }

    public String getDesc() {
        return desc;
    }

    public long getDateTime() {
        return dateTime;
    }

    public StackTraceElement[] getTraceElement() {
        return ArrayUtils.clone(stackTraceElements);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(TIME).append(
                new SimpleDateFormat(DD_MM_YYYY_HH_MM_SS_SSSZ)
                        .format(new Date(dateTime))).append(STACK);
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            builder.append(stackTraceElement).append('\n');
        }
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StackTrace that = (StackTrace) o;
        return Arrays.equals(stackTraceElements, that.stackTraceElements);

    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            hashCode = Arrays.hashCode(stackTraceElements);
        }
        return hashCode;
    }
}
