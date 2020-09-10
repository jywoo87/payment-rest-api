package com.jywoo.payment.util.crypto;

import org.springframework.util.StringUtils;

import java.util.stream.IntStream;

public class StringMaskingUtil {
    private static final char MASKING_CHAR = '*';

    private static String masking(String str, int start, int end) {
        if (start < 0) {
            start = 0;
        }
        if (end > str.length()) {
            end = str.length();
        }
        if (start >= end) {
            return str;
        }

        final StringBuilder builder = new StringBuilder(str);

        IntStream.range(start, end)
                .forEach(i -> builder.setCharAt(i, MASKING_CHAR));

        return builder.toString();
    }

    public static String maskCardNo(String cardNo) {
        if(StringUtils.isEmpty(cardNo)) {
            return cardNo;
        }
        cardNo = cardNo.trim();
        return masking(cardNo, 6, cardNo.length() - 3);
    }
}
