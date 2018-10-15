package ch.zuehlke.bibweb.bookrequest;

import javax.persistence.*;

@Entity
@Table(name = "bookrequest")
public class BookRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "isbn")
    private String isbn;

    @Column(name = "user")
    private String user;

    public BookRequest() {
    }

    public BookRequest(String isbn, String user) {
        this.isbn = isbn;
        this.user = user;
    }

    public BookRequest(Long id, String isbn, String user) {
        this.id = id;
        this.isbn = isbn;
        this.user = user;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
