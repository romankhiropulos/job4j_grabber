package ru.job4j.grabber.util;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DateUtils {
    public static LocalDateTime parseSqlRuDate(String jsoupDate) {
        LocalDateTime result;
        if (!Objects.equals(jsoupDate, "")) {
            result = LocalDateTime.now();
            int inputSize = jsoupDate.length();
            List<String> shortMonths = new ArrayList<>(Arrays.asList(
                    "янв", "фев", "мар", "апр", "май", "июн",
                    "июл", "авг", "сен", "окт", "ноя", "дек"));
            int minute = Integer.parseInt(jsoupDate.substring(inputSize - 2));
            int hour = Integer.parseInt(jsoupDate.substring(inputSize - 5, inputSize - 3));
            if (jsoupDate.startsWith("вчера")) {
                result = LocalDateTime.of(result.getYear(), result.getMonth(),
                        result.getDayOfMonth() - 1,
                        hour,
                        minute);
            } else if (jsoupDate.startsWith("сегодня")) {
                result = LocalDateTime.of(result.getYear(), result.getMonth(),
                        result.getDayOfMonth(),
                        hour,
                        minute);
            } else {
                int day;
                if (jsoupDate.charAt(1) == ' ') {
                    day = Integer.parseInt(jsoupDate.substring(0, 1));
                } else {
                    day = Integer.parseInt(jsoupDate.substring(0, 2));
                }
                int year = 2000 + Integer.parseInt(
                        jsoupDate.substring(inputSize - 9, inputSize - 7)
                );
                Month month = Month.of(
                        shortMonths.indexOf(jsoupDate.substring(inputSize - 13, inputSize - 10)) + 1
                );
                result = LocalDateTime.of(year, month, day, hour, minute);
            }
        } else {
            return null;
        }
        return result;
    }
}
