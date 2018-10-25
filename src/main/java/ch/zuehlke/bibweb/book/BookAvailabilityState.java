package ch.zuehlke.bibweb.book;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
public enum BookAvailabilityState {
    AVAILABLE,
    UNAVAILABLE,
    RESERVED_BY_YOU
}
