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

    public String getId() {
        return id;
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Post post = (Post) o;

        if (!id.equals(post.id)) {
            return false;
        }
        if (!name.equals(post.name)) {
            return false;
        }
        if (!text.equals(post.text)) {
            return false;
        }
        if (!link.equals(post.link)) {
            return false;
        }
        return created.equals(post.created);
//        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + text.hashCode();
        result = 31 * result + link.hashCode();
//        result = 31 * result + created.hashCode();
        return result;
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
