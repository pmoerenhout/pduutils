package com.github.pmoerenhout.pduutils.ie;

public class InvalidUserDataHeaderException extends Exception {

  private static final long serialVersionUID = 323977370310065642L;

  /**
   * Construct with specified message and cause.
   *
   * @param message is the detail message.
   * @param cause   is the parent cause.
   */
  public InvalidUserDataHeaderException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Construct with specified message.
   *
   * @param message is the detail message.
   */
  public InvalidUserDataHeaderException(String message) {
    super(message);
  }
}
