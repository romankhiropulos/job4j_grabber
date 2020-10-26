package ru.job4j.grabber;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SqlRuParse {
    public static void main(String[] args) throws Exception {
        Document doc = Jsoup.connect("https://www.sql.ru/forum/job-offers").get();
        Elements tables = doc.getElementsByClass("forumTable");
        Element forumTable = tables.get(0);
        Elements rows = forumTable.child(0).children();
        for (Element td : rows) {
            Element href = td.child(1); // Берем из shadowChildRef
            if (href.getElementsByClass("postslisttopic").size() != 0) {
                Element date = td.child(5); // Берем из shadowChildRef
                System.out.println(
                        href.childNode(1).attr("href")
                                .concat("\n")
                                .concat(href.childNode(1).childNode(0).toString())
                                .concat("\n")
                                .concat(convertDate(date.text()).toString())
                                .concat("\n")
                );
            }
        }
    }

    private static LocalDateTime convertDate(String jsoupDate) {
        LocalDateTime result = LocalDateTime.now();
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
            int year = Integer.parseInt(jsoupDate.substring(inputSize - 9, inputSize - 7)) + 2000;
            Month month = Month.of(
                    shortMonths.indexOf(jsoupDate.substring(inputSize - 13, inputSize - 10)) + 1
            );
            result = LocalDateTime.of(year, month, day, hour, minute);
        }
        return result;
    }
}

