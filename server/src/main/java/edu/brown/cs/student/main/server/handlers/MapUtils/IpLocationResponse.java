package edu.brown.cs.student.main.server.handlers.MapUtils;

import com.squareup.moshi.Json;

public class IpLocationResponse {
  @Json(name = "latitude")
  private double latitude;

  @Json(name = "longitude")
  private double longitude;

  // Default constructor (required for Moshi)
  public IpLocationResponse() {}

  // Getters and setters
  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  @Override
  public String toString() {
    return "IpLocationResponse{" + "latitude=" + latitude + ", longitude=" + longitude + '}';
  }
}
