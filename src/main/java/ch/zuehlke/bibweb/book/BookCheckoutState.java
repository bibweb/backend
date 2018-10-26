package ch.zuehlke.bibweb.book;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
public enum BookCheckoutState {
    AVAILABLE,
    UNAVAILABLE,
    CHECKEDOUT_BY_YOU
}
