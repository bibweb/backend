package ch.zuehlke.bibweb.book;

public class BookTestUtil {

    public static Book buildBook(Long id, String title, String isbn, int numberOfPages, int releaseYear, BookType bookType) {
        Book book = new Book();
        book.setId(id);
        book.setTitle(title);
        book.setIsbn(isbn);
        book.setNumberOfPages(numberOfPages);
        book.setReleaseYear(releaseYear);
        book.setBookType(bookType);
        return book;
    }

    public static BookDTO buildBookDTO(Long id, String title, String isbn, int numberOfPages, int releaseYear, BookType bookType) {
        BookDTO book = new BookDTO();
        book.setId(id);
        book.setTitle(title);
        book.setIsbn(isbn);
        book.setNumberOfPages(numberOfPages);
        book.setReleaseYear(releaseYear);
        book.setBookType(bookType);
        return book;
    }

    public static Boolean compareBookWithBookDTO(Book entity, BookDTO dto) {
        return (entity.getId().equals(dto.getId()) &&
                entity.getTitle().equals(dto.getTitle()) &&
                entity.getIsbn().equals(dto.getIsbn()) &&
                entity.getBookType().equals(dto.getBookType()) &&
                entity.getNumberOfPages() == dto.getNumberOfPages() &&
                entity.getReleaseYear() == dto.getReleaseYear());
    }

    public static BookDTO getDTOFromBookEntity(Book entity) {
        return buildBookDTO(entity.getId(), entity.getTitle(), entity.getIsbn(), entity.getNumberOfPages(), entity.getReleaseYear(), entity.getBookType());
    }

}
