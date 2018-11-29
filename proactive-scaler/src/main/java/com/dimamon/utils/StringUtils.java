package com.dimamon.utils;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

public class StringUtils {

    private static final DecimalFormat decimalFormat = new DecimalFormat("#.0#");

    public static List<String> showValues(List<Double> list) {
        return list.stream()
                .map(decimalFormat::format)
                .collect(Collectors.toList());
    }

    public static String showValue(Double value) {
        return decimalFormat.format(value);
    }


}
