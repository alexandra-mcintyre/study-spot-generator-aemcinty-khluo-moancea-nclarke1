package edu.brown.cs.student.main.server.CSVUtils;

import edu.brown.cs.student.main.server.Exceptions.FactoryFailureException;
import java.util.List;

public interface CreatorFromRow<T> {
  /**
   * @param row - the row inputted by the parser to convert (list of strings)
   */
  T create(List<String> row) throws FactoryFailureException;
}
