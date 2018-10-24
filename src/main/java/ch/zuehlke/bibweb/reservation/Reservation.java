package ch.zuehlke.bibweb.reservation;

import ch.zuehlke.bibweb.user.User;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @Column(name="book_id")
    private Long bookId;

    @Column(name="reserved_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date reservedAt;

    @Column(name="active")
    private Boolean active;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Date getReservedAt() {
        return reservedAt;
    }

    public void setReservedAt(Date reservedAt) {
        this.reservedAt = reservedAt;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
