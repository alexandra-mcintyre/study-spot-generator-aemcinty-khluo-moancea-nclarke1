package server.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.Assertions;
import server.src.main.java.edu.brown.cs.student.main.server.handlers.MapUtils.AddPinHandler;
import server.src.main.java.edu.brown.cs.student.main.server.handlers.MapUtils.ClearPinHandler;
import server.src.main.java.edu.brown.cs.student.main.server.handlers.MapUtils.GetPinsHandler;
import server.src.main.java.edu.brown.cs.student.main.server.handlers.MapUtils.HighlightHandler;
import server.src.main.java.edu.brown.cs.student.main.server.handlers.MapUtils.RedliningHandler;
import server.src.main.java.edu.brown.cs.student.main.server.storage.FirebaseUtilities;
import server.src.main.java.edu.brown.cs.student.main.server.storage.StorageInterface;
import spark.Spark;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

public class TestServer {

  @BeforeAll
  public static void setup_before_everything() {

    Spark.port(0);

    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
  }



  final Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);

  JsonAdapter<Map<String, Object>> adapter;

  @BeforeEach
  public void setup() throws IOException {
    // Re-initialize state, etc. for _every_ test method run
    // In fact, restart the entire Spark server for every test!
    StorageInterface firebaseUtils;
    firebaseUtils = new FirebaseUtilities();

    Spark.get("add-pin", new AddPinHandler(firebaseUtils));
    Spark.get("clear-pins", new ClearPinHandler(firebaseUtils));
    Spark.get("get-pins", new GetPinsHandler(firebaseUtils));
    Spark.get("redlining", new RedliningHandler());
    //Spark.get("highlight", new HighlightHandler());
    Spark.init();
    Spark.awaitInitialization(); // don't continue until the server is listening

    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(mapStringObject);
  }

  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on both endpoints after each test
    Spark.unmap("add-pin");
    Spark.unmap("clear-pins");
    Spark.unmap("get-pins");
    Spark.unmap("redlining");
    Spark.awaitStop(); // don't proceed until the server is stopped
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
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

    // The default method is "GET", which is what we're using here.
    // If we were using "POST", we'd need to say so.
    clientConnection.setRequestMethod("GET");

    clientConnection.connect();
    return clientConnection;
  }

  //Calls the same query twice and asserts that a cache is accessed and returns the same value
  @Test
  public void testCachingSuccess() throws IOException {
    // First query - expected to fetch fresh data
    HttpURLConnection redliningConnection1 =
        tryRequest("redlining?maxLat=41.5&minLat=41.4&maxLong=70.4&minLong=70.3");
    Map<String, Object> responseBody1 =
        adapter.fromJson(new Buffer().readFrom(redliningConnection1.getInputStream()));

    // Verify status code and response type for the first query
    assertEquals(200, redliningConnection1.getResponseCode());
    assertEquals("SUCCESS: Retrieved from server and added to cache", responseBody1.get("Request status"));

    // Cache validation: Store the first response data
    Map<String, Object> responseMap1 = (Map<String, Object>) responseBody1.get("data");

    // Second query - expected to use cache
    HttpURLConnection redliningConnection2 =
        tryRequest("redlining?maxLat=41.5&minLat=41.4&maxLong=70.4&minLong=70.3");
    Map<String, Object> responseBody2 =
        adapter.fromJson(new Buffer().readFrom(redliningConnection2.getInputStream()));

    // Verify the second response uses the cached data
    assertEquals(200, redliningConnection2.getResponseCode());
    assertEquals("SUCCESS: Retrieved from cache", responseBody2.get("Request status"));

    // Cache-specific assertions (e.g., checking response time, cache hit)
    Map<String, Object> responseMap2 = (Map<String, Object>) responseBody2.get("data");

    // Ensure the second query response map is the same as the first (indicating caching)
    assertEquals(responseMap1, responseMap2);

  }


  @Test
  //cache was initialized with a capacity of 3
  //test that makes 4 different queries and checks that the first one to be called is removed from the cache
  public void testCachingRemoves() throws IOException {
    // First query - expected to fetch fresh data
    HttpURLConnection redliningConnection1 =
        tryRequest("redlining?maxLat=41.5&minLat=41.4&maxLong=70.4&minLong=70.3");
    Map<String, Object> responseBody1 =
        adapter.fromJson(new Buffer().readFrom(redliningConnection1.getInputStream()));

    // Verify status code and response type for the first query
    assertEquals(200, redliningConnection1.getResponseCode());
    assertEquals("SUCCESS: Retrieved from server and added to cache", responseBody1.get("Request status"));


    // Second query - different
    HttpURLConnection redliningConnection2 =
        tryRequest("redlining?maxLat=4&minLat=2&maxLong=7&minLong=6");
    Map<String, Object> responseBody2 =
        adapter.fromJson(new Buffer().readFrom(redliningConnection2.getInputStream()));

    assertEquals(200, redliningConnection2.getResponseCode());
    assertEquals("SUCCESS: Retrieved from server and added to cache", responseBody2.get("Request status"));



    // Third query - different
    HttpURLConnection redliningConnection3 =
        tryRequest("redlining?maxLat=10&minLat=9&maxLong=8&minLong=7");
    Map<String, Object> responseBody3 =
        adapter.fromJson(new Buffer().readFrom(redliningConnection3.getInputStream()));

    assertEquals(200, redliningConnection3.getResponseCode());
    assertEquals("SUCCESS: Retrieved from server and added to cache", responseBody3.get("Request status"));


    //4th query: should remove the first query (maxLat=41.5&minLat=41.4&maxLong=70.4&minLong=70.3)
    HttpURLConnection redliningConnection4 =
        tryRequest("redlining?maxLat=11&minLat=9&maxLong=8&minLong=7");
    Map<String, Object> responseBody4 =
        adapter.fromJson(new Buffer().readFrom(redliningConnection4.getInputStream()));

    assertEquals(200, redliningConnection4.getResponseCode());
    assertEquals("SUCCESS: Retrieved from server and added to cache", responseBody4.get("Request status"));

    //assert 2nd query is still in cache (responsemap says retrieved from cache)
    HttpURLConnection redliningConnection5 =
        tryRequest("redlining?maxLat=4&minLat=2&maxLong=7&minLong=6");
    Map<String, Object> responseBody5 =
        adapter.fromJson(new Buffer().readFrom(redliningConnection5.getInputStream()));

    assertEquals(200, redliningConnection5.getResponseCode());
    assertEquals("SUCCESS: Retrieved from cache", responseBody5.get("Request status"));


    //assert that first query (maxLat=41.5&minLat=41.4&maxLong=70.4&minLong=70.3) is no longer in
    //    cache (responsemap should say it was added to cache)
    HttpURLConnection redliningConnection6 =
        tryRequest("redlining?maxLat=41.5&minLat=41.4&maxLong=70.4&minLong=70.3");
    Map<String, Object> responseBody6 =
        adapter.fromJson(new Buffer().readFrom(redliningConnection6.getInputStream()));

    // Verify status code and response type for the first query
    assertEquals(200, redliningConnection6.getResponseCode());
    assertEquals("SUCCESS: Retrieved from server and added to cache", responseBody6.get("Request status"));
  }


  @Test
  //testing add-pins on the backend
  public void testAddingPins() throws IOException {
    HttpURLConnection addPinConnection1 =
        tryRequest("add-pin?uid=bear&lat=5&long=7");
    Map<String, Object> responseBody1 =
        adapter.fromJson(new Buffer().readFrom(addPinConnection1.getInputStream()));

    // Verify status code and response type for the first query
    assertEquals(200, addPinConnection1.getResponseCode());
    assertEquals("success", responseBody1.get("response_type"));

    //call get-pins to see this pin
    HttpURLConnection getPinConnection1 =
        tryRequest("add-pin?uid=bear&lat=5&long=7");
    Map<String, Object> responseBody2 =
        adapter.fromJson(new Buffer().readFrom(getPinConnection1.getInputStream()));
    assertEquals("success", responseBody2.get("response_type"));

    assertTrue(responseBody2.get("pins").toString().contains("bear"));

    //add a second pin from a different user

    HttpURLConnection addPinConnection2 =
        tryRequest("add-pin?uid=bee&lat=9&long=10");
    Map<String, Object> responseBody3 =
        adapter.fromJson(new Buffer().readFrom(addPinConnection2.getInputStream()));

    // Verify status code and response type for the first query
    assertEquals(200, addPinConnection2.getResponseCode());
    assertEquals("success", responseBody3.get("response_type"));

    //call get-pins to see this pin
    HttpURLConnection getPinConnection2 =
        tryRequest("get-pins");
    Map<String, Object> responseBody4 =
        adapter.fromJson(new Buffer().readFrom(getPinConnection2.getInputStream()));
    assertEquals(200, getPinConnection2.getResponseCode());
    assertEquals("success", responseBody4.get("response_type"));

    assertTrue(responseBody4.get("pins").toString().contains("bee"));

    //call clear pins with uid = bear
    HttpURLConnection clearPinsConnection1 =
        tryRequest("clear-pins?uid=bear");
    Map<String, Object> responseBody5 =
        adapter.fromJson(new Buffer().readFrom(clearPinsConnection1.getInputStream()));

    assertEquals(200, clearPinsConnection1.getResponseCode());
    assertEquals("success", responseBody5.get("response_type"));

    //assert bear is not returned when get-pins is called
    HttpURLConnection getPinConnection3 =
        tryRequest("get-pins");
    Map<String, Object> responseBody6 =
        adapter.fromJson(new Buffer().readFrom(getPinConnection3.getInputStream()));
    assertEquals(200, getPinConnection3.getResponseCode());
    assertEquals("success", responseBody6.get("response_type"));

    assertFalse(responseBody6.get("pins").toString().contains("bear"));
  }

}
