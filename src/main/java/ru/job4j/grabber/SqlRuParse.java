package ru.job4j.grabber;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.model.Post;
import ru.job4j.grabber.parser.Parse;
import ru.job4j.grabber.util.DateUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SqlRuParse implements Parse {

    private static final String URL = "https://www.sql.ru/forum/job-offers";

//    Этот компонент позволяет собрать короткое описание всех объявлений,
//    а так же загрузить детали по каждому объявлению.

//    list(link) - этот метод загружает список объявлений по ссылке типа
//    - https://www.sql.ru/forum/job-offers/1
    @Override
    public List<Post> list(String link) {
        return null;
    }

//    detail(link) - этот метод загружает детали объявления по ссылке типа
//    - https://www.sql.ru/forum/1323839/razrabotchik-java-g-kazan
    @Override
    public Post detail(String link) {
        return null;
    }

    private List<Elements> preparePageRows(int numberOfPages) throws IOException {
        List<Elements> result = new ArrayList<>();
        for (int i = 1; i <= numberOfPages; i++) {
            Document doc = Jsoup.connect(
                    URL.concat("/").concat(String.valueOf(i))
            ).get();
            Elements tables = doc.getElementsByClass("forumTable");
            Element forumTable = tables.get(0);
            Elements rows = forumTable.child(0).children();
            result.add(rows);
        }
        return result;
    }

    private void printVacancies(List<Elements> setOfElements) {
        setOfElements.forEach(rows -> rows.forEach(td -> {
            Element href = td.child(1); // Берем из shadowChildRef
            if (href.getElementsByClass("postslisttopic").size() != 0) {
                Element date = td.child(5); // Берем из shadowChildRef
                System.out.println(
                        href.childNode(1).attr("href")
                                .concat("\n")
                                .concat(href.childNode(1).childNode(0).toString()));
                LocalDateTime dateTime = DateUtils.parseSqlRuDate(date.text());
                System.out.println((dateTime == null ? "" : dateTime) + "\n");
            }
        }));
    }

    public static void runSqlParse() {
        SqlRuParse sqlRuParse = new SqlRuParse();
        try {
            List<Elements> pageRows = sqlRuParse.preparePageRows(5);
            sqlRuParse.printVacancies(pageRows);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        SqlRuParse.runSqlParse();
    }
}

