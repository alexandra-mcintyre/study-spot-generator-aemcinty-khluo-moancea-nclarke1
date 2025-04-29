package edu.brown.cs.student.main.server.handlers.CSVHandlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.server.CSVUtils.CSVDataSource;
import edu.brown.cs.student.main.server.Exceptions.FactoryFailureException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoadCSVHandler implements Route {
  private final CSVDataSource state;
  static String filePath;

  /**
   * Accepts a csv source state via dependency injection. The handler need not know or care what
   * kind of data source we give it.
   *
   * @param state represents if the file is loaded
   */
  public LoadCSVHandler(CSVDataSource state) {
    this.state = state;
  }

  /**
   * /** This handle method processes the filePath query and loads in the csv data showing success
   * if it was able to load
   *
   * @param request The request object providing information about the HTTP request
   * @param response The response object providing functionality for modifying the response
   */
  @Override
  public Object handle(Request request, Response response) {

    // If you are interested in how parameters are received, try commenting out and
    // printing these lines! Notice that requesting a specific parameter requires that parameter
    // to be fulfilled.
    // If you specify a queryParam, you can access it by appending ?parameterName=name to the
    // endpoint
    // ex. http://localhost:3232/activity?key=num
    filePath = request.queryParams("filePath");

    // Creates a hashmap to store the results of the request
    Map<String, Object> responseMap = new HashMap<>();
    if (filePath == null | filePath.isEmpty()) {
      return new LoadFailureResponse("error_bad_input: please give the value of a file to load.")
          .serialize();
    }
    try {
      // Sends a request to the API and receives JSON back
      // String activityJson = this.sendRequest(filePath);
      // Deserializes JSON into an Activity
      FileReader fileReader = new FileReader(filePath);
      // Adds results to the responseMap
      this.state.parseData(filePath, true);
      responseMap.put("result", "success");
    } catch (FileNotFoundException e) {
      // responseMap.put("result", "error_datasource: Inputted file does not exist");
      return new LoadFailureResponse("error_datasource: Inputted file does not exist").serialize();

      // not sure if this is necessary
    } catch (IOException e) {
      // responseMap.put("result", "error_datasource: Error reading file");
      return new LoadFailureResponse("error_datasource: Error reading file").serialize();

    } catch (FactoryFailureException e) {
      // responseMap.put("result", "error_datasource: The CSV is badly formed/has the wrong
      // types.");
      return new LoadFailureResponse(
              "error_datasource: The CSV is badly formed/has the wrong types.")
          .serialize();
    }
    return new LoadSuccessResponse(responseMap).serialize();
  }

  public record LoadSuccessResponse(String responseType, Map<String, Object> responseMap) {
    public LoadSuccessResponse(Map<String, Object> responseMap) {
      this("success", responseMap);
    }

    String serialize() {
      try {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<LoadCSVHandler.LoadSuccessResponse> adapter =
            moshi.adapter(LoadCSVHandler.LoadSuccessResponse.class);
        return adapter.toJson(this);
      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }
    }
  }

  public record LoadFailureResponse(String responseType, String errorMessage) {
    public LoadFailureResponse(String errorMessage) {
      this("error", errorMessage);
    }

    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(LoadCSVHandler.LoadFailureResponse.class).toJson(this);
    }
  }
}
