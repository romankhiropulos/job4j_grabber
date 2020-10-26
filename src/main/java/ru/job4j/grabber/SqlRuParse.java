package ru.job4j.grabber;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
                        .concat(date.text()
                        .concat("\n"))
                );
            }
        }
    }
}

