package ru.job4j.grabber.storage;

import ru.job4j.grabber.model.Post;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class PsqlStore implements Store, AutoCloseable {

    private Connection cnn;

    public PsqlStore(Properties config) {
        try {
            Class.forName(config.getProperty("rabbit.driver"));
            cnn = DriverManager.getConnection(
                    config.getProperty("rabbit.url"),
                    config.getProperty("rabbit.username"),
                    config.getProperty("rabbit.password")
            );
        } catch (IllegalStateException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try (InputStream in = PsqlStore.class
                .getClassLoader().getResourceAsStream("rabbit.properties")) {
            Properties config = new Properties();
            config.load(in);
            PsqlStore psqlStore = new PsqlStore(config);

            String[] links = new String[2];
            links[0] = "https://www.sql.ru/forum/1330134/vakansiya-java-middle-senior-programmist";
            links[1] =
                 "https://www.sql.ru/forum/1329768/stazher-razrabotchik-abap-vnutrenniy-yazyk-sap";

            Post post1 = new Post("789fadfd-53f0-4f60-bdd1-bba1ef211113",
                    "Java Developer",
                    "Разработка back-end на Java/Spring",
                    links[0],
                    LocalDateTime.now());
            Post post2 = new Post("a9ff3949-9b7c-4575-9580-d739ad23922a",
                    "ABAP Developer",
                    "Разработка программ SAP ERP на ABAP",
                    links[1],
                    LocalDateTime.now());

            // mb LiquiBase and jUnit?
            psqlStore.save(post1);
            psqlStore.save(post2);

            psqlStore.getAll().forEach(System.out::println);

            Post postDB1 = psqlStore.findById("789fadfd-53f0-4f60-bdd1-bba1ef211113");
            Post postDB2 = psqlStore.findById("a9ff3949-9b7c-4575-9580-d739ad23922a");
            System.out.println(Objects.equals(post1, postDB1));
            System.out.println(Objects.equals(post2, postDB2));
        } catch (IOException e) {
//            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement ps = cnn.prepareStatement(
                "INSERT INTO post (id, name, text, link, created)"
                        + "VALUES (?, ?, ?, ?, ?)")
        ) {
            ps.setString(1, post.getId());
            ps.setString(2, post.getName());
            ps.setString(3, post.getText());
            ps.setString(4, post.getLink());
            ps.setTimestamp(5, Timestamp.valueOf(post.getCreated()));
            ps.execute();
        } catch (SQLException throwable) {
//            throwable.printStackTrace();
            System.out.println(throwable.getMessage());
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> posts = new ArrayList<>();
        try (PreparedStatement ps = cnn
                .prepareStatement("SELECT * FROM post ORDER BY created, id")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                posts.add(
                        new Post(
                                rs.getString("id"),
                                rs.getString("name"),
                                rs.getString("text"),
                                rs.getString("link"),
                                rs.getTimestamp("created").toLocalDateTime()
                        )
                );
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return posts;
    }

    @Override
    public Post findById(String id) {
        Post post = null;
        try (PreparedStatement ps = cnn
                .prepareStatement("SELECT * FROM post WHERE id = ?")) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new NoSuchElementException("По данному id нет вакансии");
            }
            post = new Post(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getString("text"),
                    rs.getString("link"),
                    rs.getTimestamp("created").toLocalDateTime()
            );
        } catch (SQLException | NoSuchElementException throwable) {
            throwable.printStackTrace();
        }
        return post;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }
}