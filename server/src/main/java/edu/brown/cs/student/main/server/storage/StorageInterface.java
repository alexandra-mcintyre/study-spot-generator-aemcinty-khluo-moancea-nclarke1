package edu.brown.cs.student.main.server.storage;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface StorageInterface {

  void addDocument(String uid, String collection_id, String doc_id, Map<String, Object> data);

  List<Map<String, Object>> getUserCollection(String uid, String collection_id)
      throws InterruptedException, ExecutionException;

  // void clearUser(String uid) throws InterruptedException, ExecutionException;

  List<Map<String, Object>> getFavorites(String uid)
      throws InterruptedException, ExecutionException;

  void deleteFavorite(String uid, String spotName);

  void addFavorite(String pin_id, Map<String, Object> data);
}
