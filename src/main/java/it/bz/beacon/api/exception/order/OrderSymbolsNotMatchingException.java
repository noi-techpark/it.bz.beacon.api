package it.bz.beacon.api.exception.order;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Order symbols not matching")
public class OrderSymbolsNotMatchingException extends RuntimeException {
    private static final long serialVersionUID = 1L;
}
