package ch.zuehlke.bibweb.book;

import ch.zuehlke.bibweb.book.projection.BookIdAndTitle;

import javax.persistence.*;

@Entity
public class Book implements BookIdAndTitle {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="title")
    private String title;

    @Column(name="isbn")
    private String isbn;

    @Column(name="release_year", nullable = true)
    private Integer releaseYear;

    @Column(name="number_of_pages", nullable = true)
    private Integer numberOfPages;

    @Column(name="booktype")
    private BookType bookType;

    public Book() {
        this.bookType = BookType.UNKNOWN;
        this.numberOfPages = -1;
        this.releaseYear = -1;
    }

    public Book(Long id, String title){
        this();
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

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public BookType getBookType() {
        return bookType;
    }

    public void setBookType(BookType bookType) {
        this.bookType = bookType;
    }
}
