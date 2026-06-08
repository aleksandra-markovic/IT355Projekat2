package com.eventhub.eventhub.exceptions;

public class UsernameAlreadyExistsException extends RuntimeException {
  public UsernameAlreadyExistsException(String msg) {
    super(msg);
  }
}
