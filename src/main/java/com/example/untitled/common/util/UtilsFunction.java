package com.example.untitled.common.util;

import java.util.concurrent.ThreadLocalRandom;

public class UtilsFunction {

    private UtilsFunction() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * ランダムに指定された長さの文字列を返す関数
     * @param length : Length of string
     * @return generated random string
     */
    public static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder stringBuilder = new StringBuilder(length);

        for(int i = 0; i < length; ++i) {
            int index = ThreadLocalRandom.current().nextInt(characters.length());
            stringBuilder.append(characters.charAt(index));
        }

        return stringBuilder.toString();
    }
}
