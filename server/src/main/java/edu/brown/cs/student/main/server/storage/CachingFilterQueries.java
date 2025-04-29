package edu.brown.cs.student.main.server.storage;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A caching class that implements a least recently used cache using LinkedHashMap. The cache holds
 * a limited number of key-value pairs and evicts the least recently used entry once the capacity is
 * exceeded. This is designed to cache ACS API data.
 *
 * @param <String> the min and max lat and long coordinates (stored in the form
 *     "minLat,maxLat,minLong, maxLong", for example: "1,2,3,4" where 1 is the minLat, 2 is maxLat,
 *     etc.
 * @param <GeoJsonObject> the GeoJsonObject stored in the cache holding the response data for the
 *     inputted coordinates
 */
public class CachingFilterQueries<String, GeoJsonObject>
    extends LinkedHashMap<String, GeoJsonObject> {

  private final int capacity; // Maximum number of items the cache can hold

  /**
   * Constructs a new cache with the specified capacity. The cache will evict the least recently
   * used entries when the capacity is exceeded.
   *
   * @param capacity the maximum number of entries the cache can hold
   */
  // Constructor to initialize the cache with a fixed capacity and access-order
  public CachingFilterQueries(int capacity) {
    // The third parameter `true` indicates that we want the map to maintain access order
    super(capacity, 0.75f, true);
    this.capacity = capacity;
  }

  //
  @Override
  /**
   * Determines whether the eldest entry in the map should be removed. This method is automatically
   * called by the LinkedHashMap after each insertion. If the number of entries exceeds the
   * specified capacity, the eldest entry (the least recently used one) is removed from the cache.
   *
   * @param eldest the eldest (least recently used) entry in the map
   * @return true if the size of the cache exceeds the capacity, causing the eldest entry to be
   *     removed
   */
  protected boolean removeEldestEntry(Map.Entry<String, GeoJsonObject> eldest) {
    // Return true if the size exceeds the capacity, which will remove the eldest entry
    return size() > this.capacity;
  }

  // A method to get an item from the cache
  /**
   * Retrieves an item from the cache based on the provided key. If the key is found, the
   * corresponding value is returned; if not, null is returned.
   *
   * @param key the key for which the value is being retrieved
   * @return the value associated with the specified key
   * @throws IOException if the cache does not contain the inputted key
   */
  public GeoJsonObject getCache(String key) throws IOException {
    if (!this.containsKey(key)) {
      throw new IOException("Key could not be found");
    }
    return super.get(key); // Returns null if key is not found
  }

  // A method to put an item in the cache
  /**
   * Adds a new key-value pair to the cache. If the cache exceeds its capacity, the least recently
   * used entry will be removed. (using removeEldestEntry through the call to super)
   *
   * @param key the key with which the specified value is associated
   * @param value the value to be associated with the specified key
   */
  public void putCache(String key, GeoJsonObject value) {
    super.put(key, value);
  }

  // A method to display the cache contents (for testing purposes)
  /**
   * Displays the current contents of the cache. This method is primarily for testing purposes and
   * prints the cache's key-value pairs to the console.
   */
  public void displayCache() {
    System.out.println(super.toString());
  }
}
