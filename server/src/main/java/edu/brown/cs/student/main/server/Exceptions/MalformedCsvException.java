package edu.brown.cs.student.main.server.Exceptions;

public class MalformedCsvException extends RuntimeException {
  public MalformedCsvException(String message) {
    super(message);
  }
}
