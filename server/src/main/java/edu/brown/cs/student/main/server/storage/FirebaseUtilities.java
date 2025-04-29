package edu.brown.cs.student.main.server.storage;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FirebaseUtilities implements StorageInterface {

  public FirebaseUtilities() throws IOException {
    // TODO: FIRESTORE PART 0:
    // Create /resources/ folder with firebase_config.json and
    // add your admin SDK from Firebase. see:
    // https://docs.google.com/document/d/10HuDtBWjkUoCaVj_A53IFm5torB_ws06fW3KYFZqKjc/edit?usp=sharing
    String workingDirectory = System.getProperty("user.dir");
    Path firebaseConfigPath =
        Paths.get(
            workingDirectory,
            "src/main/java/edu/brown/cs/student/main/server/resources/firebase_config.json");
    System.out.println(firebaseConfigPath);
    // ^-- if your /resources/firebase_config.json exists but is not found,
    // try printing workingDirectory and messing around with this path.

    FileInputStream serviceAccount = new FileInputStream(firebaseConfigPath.toString());

    FirebaseOptions options =
        new FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build();

    FirebaseApp.initializeApp(options);
  }

  @Override
  public List<Map<String, Object>> getUserCollection(String uid, String collection_id)
      throws InterruptedException, ExecutionException, IllegalArgumentException {
    if (uid == null || collection_id == null) {
      throw new IllegalArgumentException(
          "getUserCollection: uid and/or collection_id cannot be null");
    }
    // QUESTION TO TIM: should we make this an exercise too?

    // gets all documents in the collection 'collection_id' for user 'uid'

    Firestore db = FirestoreClient.getFirestore();
    // 1: Make the data payload to add to your collection
    CollectionReference dataRef = db.collection("users").document(uid).collection(collection_id);

    // 2: Get pin documents
    QuerySnapshot dataQuery = dataRef.get().get();

    // 3: Get data from document queries
    List<Map<String, Object>> data = new ArrayList<>();
    for (QueryDocumentSnapshot doc : dataQuery.getDocuments()) {
      data.add(doc.getData());
    }

    return data;
  }

  // HERE, we want to do two things when we add a document (PIN)
  // 1. each user has a collection under of pins under their name- add the pin to their collection
  // based on id
  // 2. we want to keep a general collection of pins regardless of user ids so that it is easier to
  // get all pins at once
  @Override
  public void addDocument(String uid, String collection_id, String doc_id, Map<String, Object> data)
      throws IllegalArgumentException {
    if (uid == null || collection_id == null || doc_id == null || data == null) {
      throw new IllegalArgumentException(
          "addDocument: uid, collection_id, doc_id, or data cannot be null");
    }

    // TODO: FIRESTORE PART 1:
    // use the guide below to implement this handler
    // - https://firebase.google.com/docs/firestore/quickstart#add_data

    Firestore db = FirestoreClient.getFirestore();
    // 1: Get a ref to the collection that you created
    CollectionReference collectionRef =
        db.collection("users").document(uid).collection(collection_id);

    // 2: Write data to the collection ref
    collectionRef.document(doc_id).set(data);
  }

  // this method is used to add a pin to the general list of all pins (make retrieval easier)
  @Override
  public void addFavorite(String pin_id, Map<String, Object> data) {
    Firestore db = FirestoreClient.getFirestore();
    CollectionReference allPinsRef = db.collection("allPins");
    allPinsRef
        .document(pin_id)
        .set(data); // this adds it to large collection, should get from get all pins now
  }

  // clears the collections inside of a specific user.

  private void deleteDocument(DocumentReference doc) {
    // for each subcollection, run deleteCollection()
    Iterable<CollectionReference> collections = doc.listCollections();
    for (CollectionReference collection : collections) {
      deleteCollection(collection);
    }
    // then delete the document
    doc.delete();
  }

  // recursively removes all the documents and collections inside a collection
  // https://firebase.google.com/docs/firestore/manage-data/delete-data#collections
  private void deleteCollection(CollectionReference collection) {
    try {

      // get all documents in the collection
      ApiFuture<QuerySnapshot> future = collection.get();
      List<QueryDocumentSnapshot> documents = future.get().getDocuments();

      // delete each document
      for (QueryDocumentSnapshot doc : documents) {
        doc.getReference().delete();
      }

      // NOTE: the query to documents may be arbitrarily large. A more robust
      // solution would involve batching the collection.get() call.
    } catch (Exception e) {
      System.err.println("Error deleting collection : " + e.getMessage());
    }
  }

  @Override
  public List<Map<String, Object>> getFavorites(String uid)
      throws InterruptedException, ExecutionException {
    Firestore db = FirestoreClient.getFirestore();
    List<Map<String, Object>> allPins = new ArrayList<>();

    // final Query pins = db.collectionGroup("pins");
    CollectionReference pinsRef = db.collection("users").document(uid).collection("pins");
    ApiFuture<QuerySnapshot> future = pinsRef.get();

    //   final ApiFuture<QuerySnapshot> querySnapshot = pins.get();

    // Get all the pin documents and delete them
    // List<QueryDocumentSnapshot> documents = future.get().getDocuments();

    for (QueryDocumentSnapshot document : future.get().getDocuments()) {
      allPins.add(document.getData());
      System.out.println("Pin Data" + document.getData());
      System.out.println("Pin location: " + document.getData().get("spot-name"));
      System.out.println("Pin ID: " + document.getId());
    }

    return allPins;
  }

  @Override
  public void deleteFavorite(String uid, String spotName) {
    if (uid == null || spotName == null) {
      throw new IllegalArgumentException("clearUserPins: uid cannot be null");
    }
    try {
      // Get a reference to the "pins" subcollection of the user
      Firestore db = FirestoreClient.getFirestore();
      CollectionReference pinsRef = db.collection("users").document(uid).collection("pins");

      // Get all the pin documents and delete them
      ApiFuture<QuerySnapshot> future = pinsRef.get();
      List<QueryDocumentSnapshot> documents = future.get().getDocuments();

      // Delete each document in the "pins" collection
      for (QueryDocumentSnapshot doc : documents) {
        doc.getData();
        System.out.println("Pin Data" + doc.getData());
        System.out.println("Pin Name" + doc.getData().get("uid"));
        System.out.println("in pin collection: " + doc.getData().get("spot-name"));
        System.out.println("inputted " + spotName);
        if (doc.getData().get("spot-name") != null
            & doc.getData().get("spot-name").equals(spotName)) {
          System.out.println("Removing spot " + spotName);
          ApiFuture<WriteResult> deleteFuture = doc.getReference().delete();
          deleteFuture.get();
        }
      }
    } catch (Exception e) {
      System.err.println("Error clearing pins for user: " + uid);
      System.err.println(e.getMessage());
    }
  }
}
