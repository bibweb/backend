package ch.zuehlke.bibweb.book;

import javax.persistence.*;
import java.time.Year;

@Entity
public class Book {

    @Id
    @Column(name="id")
    private Long id;

    @Column(name="title")
    private String title;

    public Book() {}

    public Book(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
