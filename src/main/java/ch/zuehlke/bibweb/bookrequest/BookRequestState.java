package ch.zuehlke.bibweb.bookrequest;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
public enum BookRequestState {
    NEW,
    ACCEPTED,
    DECLINED
}
