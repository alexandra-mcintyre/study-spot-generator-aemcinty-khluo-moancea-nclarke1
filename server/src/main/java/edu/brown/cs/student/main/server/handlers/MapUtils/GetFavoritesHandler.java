package edu.brown.cs.student.main.server.handlers.MapUtils;

import edu.brown.cs.student.main.server.handlers.Utils;
import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class GetFavoritesHandler implements Route {

  private final StorageInterface storageHandler;

  public GetFavoritesHandler(StorageInterface storageHandler) {
    this.storageHandler = storageHandler;
  }

  /**
   * Invoked when a request is made to retrieve all pins from every user
   *
   * @param request The request object providing information about the HTTP request
   * @param response The response object providing functionality for modifying the response
   * @return The content to be set in the response
   */
  @Override
  public Object handle(Request request, Response response) {
    Map<String, Object> responseMap = new HashMap<>();

    try {
      String uid = request.queryParams("uid");

      // Fetch all pins from the database
      List<Map<String, Object>> allPins = this.storageHandler.getFavorites(uid);

      responseMap.put("response_type", "success");
      responseMap.put("pins", allPins);
    } catch (Exception e) {
      e.printStackTrace();
      responseMap.put("response_type", "failure");
      responseMap.put("error", e.getMessage());
    }

    return Utils.toMoshiJson(responseMap);
  }
}
