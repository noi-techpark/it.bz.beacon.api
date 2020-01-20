package it.bz.beacon.api.exception.email;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Email not sent")
public class EmailNotSentException extends RuntimeException {
}
