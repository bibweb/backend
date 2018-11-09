package ch.zuehlke.bibweb.book;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
public enum BookReservationState {
    NOT_RESERVED_BY_YOU,
    RESERVED_BY_YOU
}
