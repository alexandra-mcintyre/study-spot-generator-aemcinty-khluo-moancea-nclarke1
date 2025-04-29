package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import edu.brown.cs.student.main.server.CSVUtils.CSVDataSource;
import edu.brown.cs.student.main.server.handlers.CSVHandlers.FindMatchesHandler;
import edu.brown.cs.student.main.server.handlers.CSVHandlers.GetAllStudyspotsHandler;
import edu.brown.cs.student.main.server.handlers.CSVHandlers.LoadCSVHandler;
import edu.brown.cs.student.main.server.handlers.MapUtils.AddFavoriteHandler;
import edu.brown.cs.student.main.server.handlers.MapUtils.DeleteFavoriteHandler;
import edu.brown.cs.student.main.server.handlers.MapUtils.GetFavoritesHandler;
import edu.brown.cs.student.main.server.handlers.MapUtils.LocationHandler;
import edu.brown.cs.student.main.server.storage.FirebaseUtilities;
import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.io.IOException;
import spark.Filter;
import spark.Spark;

/** Top Level class for our project, utilizes spark to create and maintain our server. */
public class Server {

  public static void setUpServer(CSVDataSource state) {
    int port = 3232;
    Spark.port(port);

    after(
        (Filter)
            (request, response) -> {
              response.header("Access-Control-Allow-Origin", "*");
              response.header("Access-Control-Allow-Methods", "*");
            });

    StorageInterface firebaseUtils;
    try {
      firebaseUtils = new FirebaseUtilities();
      ////      Spark.get("add-word", new AddWordHandler(firebaseUtils));
      ////      Spark.get("list-words", new ListWordsHandler(firebaseUtils));
      ////      Spark.get("clear-user", new ClearUserHandler(firebaseUtils));
      Spark.get("addFavorite", new AddFavoriteHandler(firebaseUtils));
      Spark.get("deleteFavorite", new DeleteFavoriteHandler(firebaseUtils));
      Spark.get("getFavorites", new GetFavoritesHandler(firebaseUtils));
      Spark.get("getStudyLocations", new GetAllStudyspotsHandler(state));
      Spark.get("loadcsv", new LoadCSVHandler(state));
      Spark.get("find-matches", new FindMatchesHandler(state));
      Spark.get("location", new LocationHandler());

      Spark.notFound(
          (request, response) -> {
            response.status(404); // Not Found
            System.out.println("ERROR");
            return "404 Not Found - The requested endpoint does not exist.";
          });
      Spark.init();
      Spark.awaitInitialization();

      System.out.println("Server started at http://localhost:" + port);
    } catch (IOException e) {
      e.printStackTrace();
      System.err.println(
          "Error: Could not initialize Firebase. Likely due to firebase_config.json not being found. Exiting.");
      System.exit(1);
    }
  }
  /**
   * Runs Server.
   *
   * @param args none
   */
  public static void main(String[] args) {
    try {
      // Server
      CSVDataSource state = new CSVDataSource();
      setUpServer(state);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
