package edu.brown.cs.student.main.server.CSVUtils;

import java.util.*;

/** A class representing the study spot data */
public class StudySpot {
  private String name; // Name of the study spot
  private int noiseLevel; // 1 to 5
  private int outletAvail; // 1 to 5
  private int casualFormal; // Atmosphere: Casual ↔ Formal (1 to 5)
  private int socialSolitary; // Atmosphere: Social ↔ Solitary (1 to 5)
  private int livelyCalm; // Atmosphere: Lively ↔ Calm (1 to 5)
  private int spotType; // 1: Library, 2: Entrepreneurial Space, etc., 5: cafe
  private double calculatedDist; //  distance to study spot: recalculated every time the
  // user presses submit to search
  private double latitude; // location of the study spot stored in the dataset
  private double longitude;
  private String img;

  // Constructor
  public StudySpot(
      String name,
      int noiseLevel,
      int outletAvail,
      int casualFormal,
      int socialSolitary,
      int livelyCalm,
      int spotType,
      double latitude,
      double longitude,
      String img) {
    this.name = name;
    this.noiseLevel = noiseLevel;
    this.outletAvail = outletAvail;
    this.casualFormal = casualFormal;
    this.socialSolitary = socialSolitary;
    this.livelyCalm = livelyCalm;
    this.spotType = spotType;
    this.calculatedDist = 0;
    // this.walkDist = walkDist;
    this.latitude = latitude;
    this.longitude = longitude;
    this.img = img;
  }

  public StudySpot() {
    this.name = null;
    this.noiseLevel = 0;
    this.outletAvail = 0;
    this.casualFormal = 0;
    this.socialSolitary = 0;
    this.livelyCalm = 0;
    this.spotType = 0;
    this.calculatedDist = 0;
    // this.walkDist = 0;
    this.latitude = 0;
    this.longitude = 0;
    this.img = null;
  }

  // Getters and Setters
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getNoiseLevel() {
    return noiseLevel;
  }

  public void setNoiseLevel(int noiseLevel) {
    this.noiseLevel = noiseLevel;
  }

  public int getOutletAvail() {
    return outletAvail;
  }

  public void setOutletAvail(int outletAvailability) {
    this.outletAvail = outletAvailability;
  }

  public int getCasualFormal() {
    return casualFormal;
  }

  public void setCasualFormal(int casualFormal) {
    this.casualFormal = casualFormal;
  }

  public int getSocialSolitary() {
    return socialSolitary;
  }

  public void setSocialSolitary(int socialSolitary) {
    this.socialSolitary = socialSolitary;
  }

  public int getLivelyCalm() {
    return livelyCalm;
  }

  public void setLivelyCalm(int livelyCalm) {
    this.livelyCalm = livelyCalm;
  }

  public int getSpotType() {
    return spotType;
  }

  public void setSpotType(int spotType) {
    this.spotType = spotType;
  }

  public double getLatitude() {
    return this.latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongitude() {
    return this.longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public String getImg() {
    return this.img;
  }

  public void setImg(String img) {
    this.img = img;
  }

  public void setCalculatedDist(double calculatedDist) {
    this.calculatedDist = calculatedDist;
  }
}
