package ru.job4j.grabber.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.UUID;

public class Post {

    private final String id;

    private String name;

    private String text;

    private String link;

    private LocalDateTime created;

    public Post(String name, String text, String link, LocalDateTime created) {
        this(UUID.randomUUID().toString(), name, text, link, created);
    }

    public Post(String id, String name, String text, String link, LocalDateTime created) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(text, "text must not be null");
        this.id = id;
        this.name = name;
        this.text = text;
        this.link = link;
        this.created = created;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return new StringJoiner("")
                .add("Вакансия: " + name)
                .add("\n \n")
                .add(text)
                .add("\n \n")
                .add("Ссылка: " + link)
                .add("\n")
                .add("Дата публикации: " + created.toString())
                .add("\n")
                .toString();
    }
}
