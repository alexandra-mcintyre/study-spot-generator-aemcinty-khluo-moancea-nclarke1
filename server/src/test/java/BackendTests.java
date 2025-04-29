import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.CSVUtils.CSVDataSource;
import edu.brown.cs.student.main.server.CSVUtils.MatchesFinder;
import edu.brown.cs.student.main.server.CSVUtils.StudySpot;
import edu.brown.cs.student.main.server.handlers.CSVHandlers.FindMatchesHandler;
import edu.brown.cs.student.main.server.handlers.CSVHandlers.LoadCSVHandler;
import edu.brown.cs.student.main.server.handlers.CSVHandlers.LoadCSVHandler.LoadSuccessResponse;
import edu.brown.cs.student.main.server.handlers.MapUtils.AddFavoriteHandler;
import edu.brown.cs.student.main.server.handlers.MapUtils.DeleteFavoriteHandler;
import edu.brown.cs.student.main.server.handlers.MapUtils.GetFavoritesHandler;
import edu.brown.cs.student.main.server.handlers.MapUtils.LocationHandler;
import edu.brown.cs.student.main.server.storage.FirebaseUtilities;
import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

public class BackendTests {

  private static StorageInterface firebaseUtils;

  @BeforeAll
  public static void setup_before_everything() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING); // Empty name = root logger

    // Initialize Firebase only once
    try {
      firebaseUtils = new FirebaseUtilities();
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to initialize Firebase utilities", e);
    }

    System.out.println("Firebase initialized successfully");
  }

  @BeforeEach
  public void setup() {
    // Re-initialize state for every test method
    CSVDataSource state = new CSVDataSource();

    // Register Spark routes
    Spark.get("addFavorite", new AddFavoriteHandler(firebaseUtils));
    Spark.get("deleteFavorite", new DeleteFavoriteHandler(firebaseUtils));
    Spark.get("getFavorites", new GetFavoritesHandler(firebaseUtils));
    Spark.get("loadcsv", new LoadCSVHandler(state));
    Spark.get("find-matches", new FindMatchesHandler(state));
    Spark.get("location", new LocationHandler());
    Spark.awaitInitialization();
    System.out.println("Spark server started on port: " + Spark.port());
  }

  @AfterEach
  public void teardown() {
    // Gracefully stop Spark after each test
    Spark.unmap("addFavorite");
    Spark.unmap("deleteFavorite");
    Spark.unmap("getFavorites");
    Spark.unmap("loadcsv");
    Spark.unmap("find-matches");
    Spark.unmap("location");
    Spark.stop();
    Spark.awaitStop(); // Wait for the server to stop
  }
  /**
   * Helper to start a connection to a specific API endpoint/params
   *
   * @param apiCall the call string, including endpoint (NOTE: this would be better if it had more
   *     structure!)
   * @return the connection for the given URL, just after connecting
   * @throws IOException if the connection fails for some reason
   */
  private static HttpURLConnection tryRequest(String apiCall) throws IOException {
    // Configure the connection (but don't actually send the request yet)
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

    // The default method is "GET", which is what we're using here.
    // If we were using "POST", we'd need to say so.
    clientConnection.setRequestMethod("GET");

    clientConnection.connect();
    return clientConnection;
  }

  public record LoadSuccessResponse(String responseType, Map<String, Object> responseMap) {
    public LoadSuccessResponse(Map<String, Object> responseMap) {
      this("success", responseMap);
    }
  }

  /**
   * test for the loadHandler
   *
   * @throws IOException
   */
  @Test
  public void testLoadCSV() throws IOException {
    HttpURLConnection clientConnection =
        tryRequest("loadcsv?filePath=src/main/data/study_spot_data.csv");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<LoadSuccessResponse> adapter = moshi.adapter(LoadSuccessResponse.class);
    try {
      LoadSuccessResponse deserializedResponse =
          adapter.fromJson(clientConnection.getResponseMessage());
      System.out.println("Response Type: " + deserializedResponse.responseType());
      System.out.println("Response Map: " + deserializedResponse.responseMap());
    } catch (Exception e) {
      e.printStackTrace();
    }
    clientConnection.disconnect();
  }

  @Test
  public void testPinHandlers() throws IOException {
    // Construct the request URL with parameters
    String uid = "user123";
    String latitude = "40.7128";
    String longitude = "-74.0060";
    String studySpotName = "x";
    String url =
        "addFavorite?uid="
            + uid
            + "&lat="
            + latitude
            + "&long="
            + longitude
            + "&spot-name="
            + studySpotName;
    // Send the request to the handler
    HttpURLConnection clientConnection = tryRequest(url);
    // Assert that the connection was successful
    assertEquals(200, clientConnection.getResponseCode());
    // Deserialize the JSON response
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));
    try {
      String responseMessage =
          new BufferedReader(new InputStreamReader(clientConnection.getInputStream()))
              .lines()
              .collect(Collectors.joining("\n"));
      Map<String, Object> deserializedResponse = adapter.fromJson(responseMessage);
      assertEquals("success", deserializedResponse.get("response_type"));
      Map<String, Object> pinData = (Map<String, Object>) deserializedResponse.get("pin");
      assertEquals(Double.parseDouble(latitude), pinData.get("latitude"));
      assertEquals(Double.parseDouble(longitude), pinData.get("longitude"));
      assertEquals(uid, pinData.get("uid"));
      assertEquals(studySpotName, pinData.get("spot-name"));
      System.out.println("Response Map: " + deserializedResponse);
    } catch (Exception e) {
      e.printStackTrace();
    }
    clientConnection.disconnect();

    HttpURLConnection clientConnection2 = tryRequest("getFavorites?uid=" + uid);
    assertEquals(200, clientConnection2.getResponseCode());
    try {
      String responseMessage =
          new BufferedReader(new InputStreamReader(clientConnection2.getInputStream()))
              .lines()
              .collect(Collectors.joining("\n"));
      Map<String, Object> deserializedResponse = adapter.fromJson(responseMessage);
      assertEquals("success", deserializedResponse.get("response_type"));
      Map<String, Object> pinData =
          ((List<Map<String, Object>>) deserializedResponse.get("pins")).get(0);
      assertEquals(Double.parseDouble(latitude), pinData.get("latitude"));
      assertEquals(Double.parseDouble(longitude), pinData.get("longitude"));
      assertEquals(uid, pinData.get("uid"));
      assertEquals(studySpotName, pinData.get("spot-name"));
      System.out.println("Response Map: " + deserializedResponse);
    } catch (Exception e) {
      e.printStackTrace();
    }

    HttpURLConnection clientConnection3 =
        tryRequest("deleteFavorite?uid=" + uid + "&spot-name=" + studySpotName);
    assertEquals(200, clientConnection3.getResponseCode());
    clientConnection3.disconnect();

    HttpURLConnection clientConnection4 = tryRequest("getFavorites?uid=" + uid);
    assertEquals(200, clientConnection4.getResponseCode());
    try {
      String responseMessage =
          new BufferedReader(new InputStreamReader(clientConnection4.getInputStream()))
              .lines()
              .collect(Collectors.joining("\n"));
      Map<String, Object> deserializedResponse = adapter.fromJson(responseMessage);
      assertEquals("success", deserializedResponse.get("response_type"));
      assertEquals(((List<Map<String, Object>>) deserializedResponse.get("pins")).size(), 0);
      System.out.println("Response Map: " + deserializedResponse);
    } catch (Exception e) {
      e.printStackTrace();
    }
    clientConnection4.disconnect();
  }

  @Test
  public void testGetLocation() throws IOException {
    HttpURLConnection clientConnection = tryRequest("location");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, String>> adapter =
        moshi.adapter(Types.newParameterizedType(Map.class, String.class, String.class));
    try {
      Map<String, String> deserializedResponse =
          adapter.fromJson(clientConnection.getResponseMessage());
      assertNotEquals(deserializedResponse.get("ipAddress"), "0.0.0.0");
      assertNotEquals(deserializedResponse.get("ipAddress"), "0");
      assertNotEquals(deserializedResponse.get("ipAddress"), "0");
    } catch (Exception e) {
      e.printStackTrace();
    }
    clientConnection.disconnect();
  }

  @Test
  public void testMatchesFinder() throws IOException {
    List<StudySpot> spotData = new ArrayList<>();
    spotData.add(new StudySpot("fifth", 7, 7, 7, 7, 7, 7, 7, 7, ''));
    spotData.add(new StudySpot("first", 3, 3, 3, 3, 3, 3, 3, 3));
    spotData.add(new StudySpot("fourth", 6, 6, 6, 6, 6, 6, 6, 6));
    spotData.add(new StudySpot("third", 5, 5, 5, 5, 5, 5, 5, 5));
    spotData.add(new StudySpot("second", 4, 4, 4, 4, 4, 4, 4, 4));
    MatchesFinder matchesFinder = new MatchesFinder(3, 3, 3, 3, 3, 3, 3, 3, 3, spotData);
    matchesFinder.findMatches();
    List<StudySpot> sorted = matchesFinder.getStudySpotsByScore();
    for (int i = 0; i < sorted.size(); i++) {
      System.out.println(sorted.get(i).getName());
      assertEquals(sorted.get(i).getSocialSolitary(), i + 3);
    }
    // testing haversine
    double dist = matchesFinder.getWalkDist(3.1, 2.9);
    assertTrue(dist < 10 && dist > 6);
    dist = matchesFinder.getWalkDist(3.05, 3);
    assertTrue(dist < 3.5 && dist > 2.5);
    dist = matchesFinder.getWalkDist(3.03, 2.99);
    assertTrue(dist < 2.5 && dist > 1.5);
  }
}
