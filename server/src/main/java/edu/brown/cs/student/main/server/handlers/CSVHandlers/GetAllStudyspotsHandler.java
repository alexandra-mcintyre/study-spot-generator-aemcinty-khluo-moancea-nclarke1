package edu.brown.cs.student.main.server.handlers.CSVHandlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.server.CSVUtils.CSVDataSource;
import edu.brown.cs.student.main.server.CSVUtils.StudySpot;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class GetAllStudyspotsHandler implements Route {
  private final CSVDataSource state;

  public GetAllStudyspotsHandler(CSVDataSource state) {
    this.state = state;
  }

  @Override
  public Object handle(Request request, Response response) throws IOException {
    HashMap<String, Object> responseMap = new HashMap<>();

    // Parse query parameters

    // Return if any required query parameter is missing or invalid
    if (!responseMap.isEmpty()) {
      return new SearchFailureResponse(
              "error_bad_request: Missing or invalid query parameters", responseMap)
          .serialize();
    }
    System.out.println("I MADE IT TO HERE");
    // File validation
    String filePath = "src/main/java/edu/brown/cs/student/main/server/data/study_spot_data.csv";
    if (filePath == null) {
      return new SearchFailureResponse(
              "error_bad_request: CSV file not loaded. Please load a CSV first.")
          .serialize();
    }

    try {
      // Parse CSV data
      this.state.parseData(filePath, true);
      // HashMap<String, Object> pinList = new ArrayList<>();

      // I NEED TO CHANGE THE STUDY SPOTS HERE TO RETURN PIN OBJECTS

      // Create a hashmap to store the response
      HashMap<String, Object> pinsMap = new HashMap<>();
      List<Map<String, Object>> pinsList = new ArrayList<>();

      // Process the StudySpot data
      List<StudySpot> data = this.state.getData();
      for (StudySpot spot : data) {
        Map<String, Object> pin = new HashMap<>();

        pin.put("latitude", spot.getLatitude());
        pin.put("longitude", spot.getLongitude());
        pin.put("name", spot.getName());
        pin.put("noiseLevel", spot.getNoiseLevel());
        pin.put("outletAvail", spot.getOutletAvail());
        pin.put("casualFormal", spot.getCasualFormal());
        pin.put("socialSolitary", spot.getSocialSolitary());
        pin.put("livelyCalm", spot.getLivelyCalm());
        pin.put("spotType", spot.getSpotType());
        pin.put("img", spot.getImg());
        pinsList.add(pin);
      }

      // Add the list of pins to the response under the key "pins"
      pinsMap.put("pins", pinsList);

      // Add the pinsMap to the main responseMap
      responseMap.put("data", pinsMap);

      return new SearchSuccessResponse(responseMap).serialize();
    } catch (Exception e) {
      return new SearchFailureResponse(
              "error_datasource: Unable to process the request: " + e.getMessage())
          .serialize();
    }
  }

  /**
   * Helper method to parse query parameters into integers.
   *
   * @param param The parameter value as a string.
   * @param paramName The name of the parameter for error messages.
   * @param responseMap A map to collect error messages.
   * @return The parsed integer or null if invalid.
   */
  private Integer parseQueryParam(
      String param, String paramName, HashMap<String, Object> responseMap) {
    try {
      if (param == null || param.isEmpty()) {
        responseMap.put(paramName, "Missing value for " + paramName);
      }
      return Integer.parseInt(param);
    } catch (NumberFormatException e) {
      responseMap.put(paramName, "Invalid integer value for " + paramName + ": " + param);
      return null;
    }
  }

  private double parseDoubleQueryParam(
      String param, String paramName, HashMap<String, Object> responseMap) {
    try {
      if (param == null || param.isEmpty()) {
        responseMap.put(paramName, "Missing value for " + paramName);
      }
      return Double.parseDouble(param);
    } catch (NumberFormatException e) {
      responseMap.put(paramName, "Invalid double value for " + paramName + ": " + param);
      return 0;
    }
  }

  public record SearchSuccessResponse(String responseType, Map<String, Object> responseMap) {
    public SearchSuccessResponse(Map<String, Object> responseMap) {
      this("success", responseMap);
    }

    String serialize() {
      try {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<SearchSuccessResponse> adapter = moshi.adapter(SearchSuccessResponse.class);
        return adapter.toJson(this);
      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }
    }
  }

  public record SearchFailureResponse(String responseType, String errorMessage) {
    public SearchFailureResponse(String errorMessage) {
      this("error", errorMessage);
    }

    public SearchFailureResponse(String errorMessage, Map<String, Object> responseMap) {
      this("error", errorMessage + " | Details: " + responseMap);
    }

    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(SearchFailureResponse.class).toJson(this);
    }
  }
}
