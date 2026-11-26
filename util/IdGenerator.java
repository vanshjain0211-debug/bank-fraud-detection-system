package com.util;

public class IdGenerator {
    public static String formatAccountNumber(int seq) {
        return String.format("ACC%04d", seq);
    }

    public static String formatCustomerId(int seq) {
        return String.format("CUS%03d", seq);
    }
}
