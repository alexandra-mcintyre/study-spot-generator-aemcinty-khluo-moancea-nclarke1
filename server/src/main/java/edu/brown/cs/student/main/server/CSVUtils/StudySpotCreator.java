package edu.brown.cs.student.main.server.CSVUtils;

import edu.brown.cs.student.main.server.Exceptions.FactoryFailureException;
import edu.brown.cs.student.main.server.Exceptions.MalformedCsvException;
import java.util.List;

public class StudySpotCreator implements CreatorFromRow<StudySpot> {

  @Override
  /**
   * @param row - the row inputted by the parser to convert (list of strings)
   * @returns objToReturn - StudySpot filled by elements in row
   * @throws FactoryFailureException - checking wrong data types or null values
   * @throws MalformedCsvException - checking wrong number of elements in a row
   */
  public StudySpot create(List<String> row) throws FactoryFailureException, MalformedCsvException {
    StudySpot objToReturn = new StudySpot();
    // distToSpot = findDistToSpot(objToReturn, LocationHandler.getUserLat(), userLong);

    // Check if the row has the correct number of fields (9 for the StudySpot object)

    if (row.size() != 10) {
      throw new MalformedCsvException(
          "There are not enough fields in this row to construct a StudySpot object: "
              + row.size()
              + row);
    }

    try {
      // Parse and set fields
      objToReturn.setName(row.get(0)); // Study Spot Name
      objToReturn.setNoiseLevel(Integer.parseInt(row.get(1))); // Noise Level
      objToReturn.setOutletAvail(Integer.parseInt(row.get(2))); // Outlet Availability
      objToReturn.setCasualFormal(Integer.parseInt(row.get(3))); // Atmosphere: Casual ↔ Formal
      objToReturn.setSocialSolitary(Integer.parseInt(row.get(4))); // Atmosphere: Social ↔ Solitary
      objToReturn.setLivelyCalm(Integer.parseInt(row.get(5))); // Atmosphere: Lively ↔ Calm
      objToReturn.setSpotType(Integer.parseInt(row.get(6))); // Spot Type
      objToReturn.setLatitude(Double.parseDouble(row.get(7)));
      objToReturn.setLongitude(Double.parseDouble(row.get(8)));
      objToReturn.setImg(row.get(9));
      // add logic for api call for walk distance here????

      // distToSpot = findDistToSpot(objToReturn, LocationHandler.getUserLat(), userLong);
      // objToReturn.setWalkDist(5);

    } catch (NumberFormatException numError) {
      throw new FactoryFailureException(
          "The arguments to construct the StudySpot may not be of correct type", row);
    } catch (NullPointerException nullError) {
      throw new FactoryFailureException("One of the arguments to create this object is null", row);
    }

    return objToReturn;
  }

  //  public double findDistToSpot(StudySpot studyObj, double userLat, double userLong) {
  //
  //  }
}
