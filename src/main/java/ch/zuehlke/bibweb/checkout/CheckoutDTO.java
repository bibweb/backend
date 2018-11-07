package ch.zuehlke.bibweb.checkout;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class CheckoutDTO {

    private Long id;
    private Long userId;
    private Long bookId;
    private Date checkoutDate;
    private Date dueDate;
    private Boolean stillOut;
    private String bookTitle;

    public CheckoutDTO() {
        this.checkoutDate = Date.from(Instant.now());
        this.dueDate = Date.from(Instant.now().plus(30, ChronoUnit.DAYS));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Date getCheckoutDate() {
        return checkoutDate;
    }

    public void setCheckoutDate(Date checkoutDate) {
        this.checkoutDate = checkoutDate;
    }

    public Boolean getStillOut() {
        return stillOut;
    }

    public void setStillOut(Boolean stillOut) {
        this.stillOut = stillOut;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }
}
