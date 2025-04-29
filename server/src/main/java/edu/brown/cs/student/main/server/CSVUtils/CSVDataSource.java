package edu.brown.cs.student.main.server.CSVUtils;

import edu.brown.cs.student.main.server.Exceptions.FactoryFailureException;
import edu.brown.cs.student.main.server.Parser.Parser;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVDataSource {
  private List<StudySpot> dataset;
  private boolean parsed;
  private List<String> headersList;

  public CSVDataSource() {
    this.parsed = false;
  }

  public List<StudySpot> getData() throws IOException {
    if (parsed) return dataset;
    throw new IOException("Data hasn't been loaded");
  }

  public void parseData(String fileName, Boolean headerP)
      throws FactoryFailureException, IOException {
    Parser<StudySpot> parser =
        new Parser<StudySpot>(new FileReader(fileName), new StudySpotCreator(), headerP);
    parser.parse();

    this.parsed = true;
    this.dataset = parser.parsedContent;
    this.headersList = parser.getHeaders();
  }

  public List<StudySpot> getDataset() {
    return new ArrayList<>(this.dataset);
  }

  public List<String> getHeadersList() {
    return new ArrayList<>(this.headersList);
  }
}
