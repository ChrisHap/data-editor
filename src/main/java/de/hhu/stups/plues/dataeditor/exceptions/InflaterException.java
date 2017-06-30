package de.hhu.stups.plues.dataeditor.exceptions;

public class InflaterException extends RuntimeException {
  public InflaterException(final Exception exception) {
    super(exception.getMessage());
  }
}
