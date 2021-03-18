package com.github.pmoerenhout.pduutils.jsmpp;

public class InvalidMessagePayloadException extends Exception {

  private static final long serialVersionUID = -8801786720825642860L;

  /**
   * Construct with specified message and cause.
   *
   * @param message is the detail message.
   * @param cause   is the parent cause.
   */
  public InvalidMessagePayloadException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Construct with specified message.
   *
   * @param message is the detail message.
   */
  public InvalidMessagePayloadException(String message) {
    super(message);
  }
}

