package ai.aitia.rfid_employee.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateEntryException extends RuntimeException {

  public DuplicateEntryException() {
    super();
  }

  public DuplicateEntryException(String message) {
    super(message);
  }

  public DuplicateEntryException(String message, Throwable cause) {
    super(message, cause);
  }

}
