package edu.brown.cs.student.main.server.handlers.MapUtils;

import edu.brown.cs.student.main.server.handlers.Utils;
import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class AddFavoriteHandler implements Route {

  public StorageInterface storageHandler;

  public AddFavoriteHandler(StorageInterface storageHandler) {
    this.storageHandler = storageHandler;
  }

  /**
   * Invoked when a request is made on this route's corresponding path e.g. '/addPin'
   *
   * @param request The request object providing information about the HTTP request
   * @param response The response object providing functionality for modifying the response
   * @return The content to be set in the response
   */
  @Override
  public Object handle(Request request, Response response) {
    Map<String, Object> responseMap = new HashMap<>();
    try {
      // collect parameters from the request
      String uid = request.queryParams("uid");
      String latitudeStr = request.queryParams("lat");
      String longitudeStr = request.queryParams("long");
      String studySpotName = request.queryParams("name");
      //
      System.out.println("NAME:" + studySpotName);
      Double latitude = Double.parseDouble(latitudeStr);
      Double longitude = Double.parseDouble(longitudeStr);
      // You can also add more fields if necessary (e.g., time, pin type, etc.)
      Map<String, Object> data = new HashMap<>();
      data.put("latitude", latitude);
      data.put("longitude", longitude);
      data.put("uid", uid);
      data.put("spot-name", studySpotName);
      // data.put("label", label);

      System.out.println("adding pin at [" + latitude + ", " + longitude + "] for user: " + uid);
      System.out.println("study spot" + studySpotName);
      // get the current pin count to create a unique pin ID (this part could be adjusted)
      int pinCount = this.storageHandler.getUserCollection(uid, "pins").size();
      int pinCount2 = this.storageHandler.getFavorites(uid).size();
      System.out.println(pinCount);
      System.out.println(pinCount2);
      String pinId = "pin-" + pinCount;
      // String pinId2 = "pin-" + pinCount2;

      // use the storage handler to add the pin document to Firestore
      this.storageHandler.addDocument(uid, "pins", pinId, data);
      this.storageHandler.addFavorite(pinId, data);

      responseMap.put("response_type", "success");
      responseMap.put("pin", data);
    } catch (Exception e) {
      // error likely occurred in the storage handler
      e.printStackTrace();
      responseMap.put("response_type", "failure");
      responseMap.put("error", e.getMessage());
    }

    return Utils.toMoshiJson(responseMap);
  }
}
