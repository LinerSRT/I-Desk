package com.liner.i_desk.Utils;

import java.util.Random;

public class TextUtils {
    public static String generateRandomString(int lenth){
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < lenth; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }
    public static int randInt(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }

    public static float getPercent(float firstValue, float secondValue){
        return ((firstValue/secondValue) * 100f);
    }

    public static float getPercent(long firstValue, long secondValue){
        return ((Float.parseFloat(Long.toString(firstValue))/Float.parseFloat(Long.toString(secondValue))) * 100f);
    }
}
