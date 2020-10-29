package ru.job4j.grabber.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import ru.job4j.grabber.model.Post;
import ru.job4j.grabber.util.DateUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class SqlRuParse implements Parse {

    private static final String URL = "https://www.sql.ru/forum/";

    @Override
    public List<Post> list(String offersLink) {
        List<Post> result = new ArrayList<>();
        try {
            preparePageRows(offersLink).forEach(td -> {
                Element href = td.child(1); // Берем из shadowChildRef
                if (href.getElementsByClass("postslisttopic").size() != 0) {
                    result.add(detail(href.childNode(1).attr("href")));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Post detail(String vacancyLink) {
        if (vacancyLink.startsWith(URL)) {
            Document doc = null;
            try {
                doc = Jsoup.connect(vacancyLink).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Elements msgTables = Objects.requireNonNull(doc)
                    .getElementsByClass("msgTable");
            Element msgTable = msgTables.get(0);
            Elements tagTbody = msgTable.children();

            Element tdName = tagTbody.get(0).children().get(0).children().get(0);
            String name = tdName.text();
            if (name.endsWith("[new]")) {
                name = name.substring(0, name.length() - 6);
            } else if (name.endsWith("[закрыт]")) {
                return null;
            }

            Element tdTextMsg = tagTbody.get(0).children().get(1).children().get(1);
            StringJoiner stringJoiner = new StringJoiner("\n");
            for (Node node : tdTextMsg.childNodes()) {
                if (node instanceof TextNode) {
                    stringJoiner.add(((TextNode) node).text());
                }
            }
            String textMessage = stringJoiner.toString();

            Element tdDate = tagTbody.get(0).children().get(2).children().get(0);
            String date = tdDate.childNode(0).toString().split("&")[0].strip();
            LocalDateTime created = DateUtils.parseSqlRuDate(date);

            return new Post(name, textMessage, vacancyLink, created);
        }
        return null;
    }

    private Elements preparePageRows(String link) throws IOException {
        Document doc = Jsoup.connect(link).get();
        Elements tables = doc.getElementsByClass("forumTable");
        Element forumTable = tables.get(0);
        Elements rows = forumTable.child(0).children();
        return rows;
    }

    public static void main(String[] args) {
        List<Post> list = new SqlRuParse().list("https://www.sql.ru/forum/job-offers/1");
        list.forEach(System.out::println);
    }
}
