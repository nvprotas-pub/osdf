package io.microconfig.osdf.utils;

import static io.microconfig.osdf.utils.ColorSymbol.RESET;
import static io.microconfig.osdf.utils.ColorSymbol.values;
import static java.lang.Integer.parseInt;
import static java.lang.Integer.valueOf;
import static java.lang.Math.max;

public class StringUtils {
    public static String pad(String s, int length) {
        return s + " ".repeat(max(length - s.length(), 0));
    }

    public static String coloredStringPad(String s, int length) {
        return s + " ".repeat(max(length - coloredStringLength(s), 0));
    }


    public static int coloredStringLength(String s) {
        String result = s;
        if (s.contains(RESET.asString())) {
            for (ColorSymbol colorSymbol : values()) {
                result = result.replace(colorSymbol.asString(), "");
            }
        }
        return result.length();
    }

    public static Integer castToInteger(String s) {
        try {
            return valueOf(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static int castToInt(String s) {
        try {
            return parseInt(s);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Can't cast to int", e);
        }
    }
}
