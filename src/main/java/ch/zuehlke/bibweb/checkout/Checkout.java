package ch.zuehlke.bibweb.checkout;

import javax.persistence.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Entity
@Table(name = "checkout")
public class Checkout {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "book_id")
    private Long bookId;

    @Column(name = "checkout_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date checkoutDate;

    @Column(name = "due_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dueDate;

    @Column(name = "still_out")
    private Boolean stillOut;

    public Checkout() {
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
}
