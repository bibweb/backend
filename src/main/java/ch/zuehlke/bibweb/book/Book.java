package ch.zuehlke.bibweb.book;

import javax.persistence.*;
import java.time.Year;

@Entity
public class Book {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="id")
    private Long id;

    @Column(name="title")
    private String title;

    /*@Column(name="year")
    private Year year;*/

    public Book() {}

    public Book(Long id, String title, Year year) {
        this.id = id;
        this.title = title;
        //this.year = year;
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

    /*public Year getYear() {
        return year;
    }

    public void setYear(Year year) {
        this.year = year;
    }*/
}
