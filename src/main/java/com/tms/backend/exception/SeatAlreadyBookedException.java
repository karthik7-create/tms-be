// ═══ FILE: src/main/java/com/tms/backend/exception/SeatAlreadyBookedException.java ═══
package com.tms.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class SeatAlreadyBookedException extends RuntimeException {

    public SeatAlreadyBookedException(String message) {
        super(message);
    }

    public SeatAlreadyBookedException(Long seatId) {
        super(String.format("Seat with ID %d is already booked or locked", seatId));
    }
}
