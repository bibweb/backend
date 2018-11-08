package ch.zuehlke.bibweb.bookrequest.data;

import javax.persistence.*;

@Entity
@Table(name = "bookrequest")
public class BookRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "isbn")
    private String isbn;

    @Column(name = "username")
    private String user;

    @Column(name = "state")
    private BookRequestState state;

    public BookRequest() {
    }

    public BookRequest(String isbn) {
        this(isbn, "");
    }

    public BookRequest(String isbn, String user) {
        this(isbn, user, BookRequestState.NEW);
    }

    public BookRequest(Long id, String isbn, String user) {
        this(id, isbn, user, BookRequestState.NEW);
    }

    public BookRequest(String isbn, String user, BookRequestState state) {
        this.isbn = isbn;
        this.user = user;
        this.state = state;
    }

    public BookRequest(Long id, String isbn, String user, BookRequestState state) {
        this.id = id;
        this.isbn = isbn;
        this.user = user;
        this.state = state;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public BookRequestState getState() {
        return state;
    }

    public void setState(BookRequestState state) {
        this.state = state;
    }
}
