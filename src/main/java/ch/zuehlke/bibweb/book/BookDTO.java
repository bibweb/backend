package ch.zuehlke.bibweb.book;

public class BookDTO {

    private Long id;
    private String title;
    private String isbn;
    private Integer releaseYear;
    private Integer numberOfPages;
    private BookType bookType;
    private BookAvailabilityState availability;

    public BookDTO() {
        this.bookType = BookType.UNKNOWN;
        this.availability = BookAvailabilityState.UNAVAILABLE;
        this.numberOfPages = -1;
        this.releaseYear = -1;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public Integer getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(Integer numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public BookType getBookType() {
        return bookType;
    }

    public void setBookType(BookType bookType) {
        this.bookType = bookType;
    }

    public BookAvailabilityState getAvailability() {
        return availability;
    }

    public void setAvailability(BookAvailabilityState availability) {
        this.availability = availability;
    }
}
