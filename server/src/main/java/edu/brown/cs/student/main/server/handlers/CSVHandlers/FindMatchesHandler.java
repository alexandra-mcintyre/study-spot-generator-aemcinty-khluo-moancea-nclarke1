package edu.brown.cs.student.main.server.handlers.CSVHandlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.server.CSVUtils.CSVDataSource;
import edu.brown.cs.student.main.server.CSVUtils.MatchesFinder;
import edu.brown.cs.student.main.server.CSVUtils.StudySpot;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class FindMatchesHandler implements Route {
  private final CSVDataSource state;

  public FindMatchesHandler(CSVDataSource state) {
    this.state = state;
  }

  @Override
  public Object handle(Request request, Response response) throws IOException {
    HashMap<String, Object> responseMap = new HashMap<>();

    // Parse query parameters
    Integer noiseLevel =
        parseQueryParam(request.queryParams("noiseLevel"), "noiseLevel", responseMap);
    Integer outletAvail =
        parseQueryParam(request.queryParams("outletAvail"), "outletAvail", responseMap);
    Integer casForm = parseQueryParam(request.queryParams("casForm"), "casForm", responseMap);
    Integer socSolitary =
        parseQueryParam(request.queryParams("socSolitary"), "socSolitary", responseMap);
    Integer livelyCalm =
        parseQueryParam(request.queryParams("livelyCalm"), "livelyCalm", responseMap);
    Integer spotType = parseQueryParam(request.queryParams("spotType"), "spotType", responseMap);
    double userLat = parseDoubleQueryParam(request.queryParams("userLat"), "userLat", responseMap);
    double userLng = parseDoubleQueryParam(request.queryParams("userLng"), "userLng", responseMap);
    double walkDist =
        parseDoubleQueryParam(request.queryParams("walkDist"), "walkDist", responseMap);
     String img = "";
    //     request.queryParams("img");
    // Return if any required query parameter is missing or invalid
    // exits the function and adds details about the error
    if (!responseMap.isEmpty()) {
      return new SearchFailureResponse(
              "error_bad_request: Missing or invalid query parameters", responseMap)
          .serialize();
    }

    // File validation
    String filePath = LoadCSVHandler.filePath;
    if (filePath == null) {
      return new SearchFailureResponse(
              "error_bad_request: CSV file not loaded. Please load a CSV first.")
          .serialize();
    }
    try {
      // Parse CSV data
      this.state.parseData(filePath, true);
      List<StudySpot> data = this.state.getData();

      // Instantiate MatchesFinder and perform matching
      MatchesFinder finder =
          new MatchesFinder(
              noiseLevel,
              outletAvail,
              casForm,
              socSolitary,
              livelyCalm,
              spotType,
              walkDist,
              userLat,
              userLng,
              img,
              data); // Assume MatchesFinder accepts data directly
      finder.findMatches();

      responseMap.put(
          "matches", finder.getStudySpotsByScore()); // Adjust based on MatchesFinder's output
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
