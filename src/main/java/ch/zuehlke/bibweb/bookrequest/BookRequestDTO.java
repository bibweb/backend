package ch.zuehlke.bibweb.bookrequest;

public class BookRequestDTO {

    private Long id;
    private String isbn;
    private String user;
    private BookRequestState state;

    public BookRequestDTO() {
    }

    public BookRequestDTO(String isbn) {
        this(isbn, "");
    }

    public BookRequestDTO(String isbn, String user) {
        this(isbn, user, BookRequestState.NEW);
    }

    public BookRequestDTO(Long id, String isbn, String user) {
        this(id, isbn, user, BookRequestState.NEW);
    }

    public BookRequestDTO(String isbn, String user, BookRequestState state) {
        this.isbn = isbn;
        this.user = user;
        this.state = state;
    }

    public BookRequestDTO(Long id, String isbn, String user, BookRequestState state) {
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
