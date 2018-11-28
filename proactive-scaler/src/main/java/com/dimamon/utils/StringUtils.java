package com.dimamon.utils;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

public class StringUtils {

    public static List<String> showValues(List<Double> list) {
        DecimalFormat decimalFormat = new DecimalFormat("#.0#");
        return list.stream()
                .map(decimalFormat::format)
                .collect(Collectors.toList());
    }
}
