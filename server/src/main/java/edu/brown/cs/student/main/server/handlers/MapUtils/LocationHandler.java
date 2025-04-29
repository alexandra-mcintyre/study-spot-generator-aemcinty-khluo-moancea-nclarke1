package edu.brown.cs.student.main.server.handlers.MapUtils;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import spark.Route;

public class LocationHandler implements Route {

  //   public LocationHandler() {

  //   }

  @Override
  public Object handle(spark.Request rq, spark.Response rs) throws Exception {
    // Creates a hashmap to store the results of the request
    Map<String, Object> responseMap = new HashMap<>();
    try {
      URL ipURL = new URL("https://api.ipify.org"); // Returns plain-text IP address
      HttpURLConnection connection = (HttpURLConnection) ipURL.openConnection();
      connection.setRequestMethod("GET");
      BufferedReader reader =
          new BufferedReader(new InputStreamReader(connection.getInputStream()));
      String publicIP = reader.readLine();
      reader.close();
      if (publicIP == null) {
        throw new RuntimeException("IP not found");
      }
      Map<String, String> env = EnvLoader.loadEnv(".env");
      String url =
          "http://api.ipstack.com/" + publicIP + "?access_key=" + env.get("IPSTACK_API_KEY");
      System.out.println(url);
      OkHttpClient client = new OkHttpClient();
      Request request = new Request.Builder().url(url).build();
      Response response = client.newCall(request).execute();
      assert response.body() != null;
      String json = response.body().string();
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<IpLocationResponse> jsonAdapter = moshi.adapter(IpLocationResponse.class);
      IpLocationResponse location = jsonAdapter.fromJson(json);
      // Add resolved location and IP to the response map
      responseMap.put("ipAddress", publicIP);
      responseMap.put("latitude", location.getLatitude());
      responseMap.put("longitude", location.getLongitude());
      return new LoadSuccessResponse(responseMap).serialize();
    } catch (Exception e) {
      // Log the error and return a failure response
      e.printStackTrace();
      return new LoadFailureResponse("Failed to retrieve location: " + e.getMessage()).serialize();
    }
  }

  public record LoadSuccessResponse(String responseType, Map<String, Object> responseMap) {
    public LoadSuccessResponse(Map<String, Object> responseMap) {
      this("success", responseMap);
    }

    String serialize() {
      try {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<LoadSuccessResponse> adapter = moshi.adapter(LoadSuccessResponse.class);
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
      return moshi.adapter(LoadFailureResponse.class).toJson(this);
    }
  }
}
