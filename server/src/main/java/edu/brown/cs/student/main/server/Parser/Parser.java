package edu.brown.cs.student.main.server.Parser;

import edu.brown.cs.student.main.server.CSVUtils.CreatorFromRow;
import edu.brown.cs.student.main.server.Exceptions.FactoryFailureException;
import edu.brown.cs.student.main.server.Exceptions.MalformedCsvException;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class Parser<T> {

  public Reader reader;

  public Boolean hasHeaders;

  public CreatorFromRow<T> rowCreator;

  public List<T> parsedContent;

  public List<String> headerList;

  /**
   * @param userReader - reader type with csv file to be parsed inputted
   * @param rowCreator - the type of creator user wants to convert each row into (ex: List<String>,
   *     Stars, etc.)
   * @param hasHeaders - boolean checking if there are column headers or not
   * @throws IOException - if any of the arguments are null
   */
  public Parser(Reader userReader, CreatorFromRow<T> rowCreator, boolean hasHeaders)
      throws IOException {
    if (userReader == null)
      throw new IOException("The csv reader entered is empty- please put a functional reader ");
    if (rowCreator == null)
      throw new IOException(
          "Define what type of object you want the data to return as (ex: List<String>, StudentRecord)");
    this.reader = new BufferedReader(userReader);
    this.hasHeaders = hasHeaders;
    this.parsedContent = new ArrayList<T>();
    this.rowCreator = rowCreator;
    this.headerList = new ArrayList<>();
  }

  public Parser() throws IOException {
    this.reader = null;
    this.parsedContent = null;
    throw new IOException("there was no file path entered- please put a functional path");
    // IS THIS REALLY STUPID? DO I NEED TO BE DOING THIS?
  }
  /**
   * TODO feel free to modify this method to incorporate your design choices
   *
   * @throws IOException when buffer reader fails to read-in a line
   * @throws FactoryFailureException when data type in csv doesn't match expected format
   */
  public void parse() throws IOException, FactoryFailureException {
    String line;
    boolean countCols = true;
    int expectedCols = 0;
    int rowsParsed = 0;
    Pattern regexSplitCSVRow = Pattern.compile(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))");
    BufferedReader readInBuffer =
        new BufferedReader(reader); // wraps around readers to improve efficiency when reading

    // skips header

    while ((line = readInBuffer.readLine()) != null) {
      String[] result = regexSplitCSVRow.split(line);
      List<String> lineToArr = Arrays.stream(result).toList();
      if (this.hasHeaders) {
        this.headerList.addAll(lineToArr);
        this.hasHeaders = false;
      } else {
        if (countCols) {
          // count number of expected columns
          expectedCols = lineToArr.size();
          countCols = false;
        }
        rowsParsed += 1;
        if (lineToArr.size() != expectedCols)
          throw new MalformedCsvException(
              "There are not enough columns in this row of the csv, row " + rowsParsed);

        T obj = this.rowCreator.create(lineToArr);
        this.parsedContent.add(obj);
      }
    }
    readInBuffer.close();
  }
  /** */
  public List<T> getParsedContent() {
    // MAKE A DEFENSIVE COPY
    return new ArrayList<T>(this.parsedContent);
  }
  /** */
  public List<String> getHeaders() {
    // MAKE A DEFENSIVE COPY - don't need to be mutated
    return new ArrayList<>(this.headerList);
  }
}
