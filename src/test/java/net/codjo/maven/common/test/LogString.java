/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.maven.common.test;
import junit.framework.Assert;
/**
 * Classe supportant le pattern de test "Log String".
 */
public class LogString {
    private static final String SEPARATOR = ", ";
    private static final String OPENING = "(";
    private static final String CLOSING = ")";
    private StringBuffer log = new StringBuffer();
    private String prefix;


    public LogString() {
    }


    public LogString(String prefix, LogString logString) {
        this.setPrefix(prefix);

        this.log = logString.log;
    }


    public void info(String message) {
        if (log.length() != 0) {
            log.append(SEPARATOR);
        }

        if (prefix != null) {
            log.append(prefix);

            log.append(".");
        }

        log.append(message);
    }


    public void call(String methodName, Object arg1) {
        info(methodName + OPENING + arg1 + CLOSING);
    }


    public void call(String methodName, Object arg1, Object arg2) {
        info(methodName + OPENING + arg1 + SEPARATOR + arg2 + CLOSING);
    }


    public void call(String methodName, Object arg1, Object arg2, Object arg3) {
        info(methodName + OPENING + arg1 + SEPARATOR + arg2 + SEPARATOR + arg3 + CLOSING);
    }


    public void call(String methodName, Object arg1, Object arg2, Object arg3, Object arg4) {
        info(methodName + OPENING + arg1 + SEPARATOR + arg2 + SEPARATOR + arg3 + SEPARATOR + arg4 + CLOSING);
    }


    public String getContent() {
        return log.toString();
    }


    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }


    public void clear() {
        log.setLength(0);
    }


    public void call(String methodName) {
        info(methodName + "()");
    }


    public void assertContent(String expectedContent) {
        Assert.assertEquals(expectedContent, getContent());
    }
}
