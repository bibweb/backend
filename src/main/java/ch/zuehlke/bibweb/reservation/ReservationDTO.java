package ch.zuehlke.bibweb.reservation;

import java.time.Instant;
import java.util.Date;

public class ReservationDTO {

    private Long id;
    private Long userId;
    private Long bookId;
    private Date reservedAt;
    private Boolean active;

    public ReservationDTO() {
        reservedAt = Date.from(Instant.now());
        active = true;
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
