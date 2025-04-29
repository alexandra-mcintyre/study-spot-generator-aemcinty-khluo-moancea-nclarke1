package edu.brown.cs.student.main.server.CSVUtils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class MatchesFinder {
  private final int noiseLevel;
  private final int outletAvail;
  private final int casForm;
  private final int socSolitary;
  private final int livelyCalm;
  private final int spotType;
  private final double walkDist;
  private final double userLat;
  private final double userLong;
  private double calculatedDist;
  private String img;
  private final List<StudySpot> spotData;
  public static final double RADIUS = 3963.1;

  /**
   * Constructs MatchesFinder object with user preferences.
   *
   * @param noiseLevel User's desired noise level.
   * @param outletAvail User's preference for outlet availability.
   * @param casForm User's preference for casual vs. formal atmosphere.
   * @param socSolitary User's preference for social vs. solitary atmosphere.
   * @param livelyCalm User's preference for lively vs. calm environment.
   * @param spotType User's preferred type of spot (e.g., library, coffee shop).
   * @param walkDist Maximum walking distance user is willing to cover.
   * @param spotData List of study spots to evaluate.
   * @param img Img link to pass onto other functions
   */
  public MatchesFinder(
      int noiseLevel,
      int outletAvail,
      int casForm,
      int socSolitary,
      int livelyCalm,
      int spotType,
      double walkDist,
      double userLat,
      double userLong,
      String img,
      List<StudySpot> spotData) {
    this.noiseLevel = noiseLevel;
    this.outletAvail = outletAvail;
    this.casForm = casForm;
    this.socSolitary = socSolitary;
    this.livelyCalm = livelyCalm;
    this.spotType = spotType;
    this.userLat = userLat;
    this.userLong = userLong;
    this.walkDist = walkDist;
    this.img = img;
    this.spotData = spotData; 
  }
  /**
   * Finds matches based on user preferences.
   *
   * @return A list of study spots organized by score, where index i contains spots with score i.
   */
  public void findMatches() {
    HashMap<StudySpot, Integer> spotToScore = new HashMap<>();
    for (StudySpot spot : spotData) {
      spotToScore.put(spot, -calculateScore(spot)); // since we want descending order
    }
    this.spotData.sort(Comparator.comparingInt(spotToScore::get));
    System.out.println(spotToScore);
    System.out.println(this.spotData);
    // return studySpotsByScore;
  }

  /**
   * Calculates a score for a given study spot based on user preferences.
   *
   * @param spot The study spot to evaluate.
   * @return The calculated score.
   */
  private int calculateScore(StudySpot spot) {
    double walkDist = getWalkDist(spot.getLatitude(), spot.getLongitude());
    spot.setCalculatedDist(walkDist);

    int score = 0;
    score -= 3 * Math.abs(spot.getNoiseLevel() - this.noiseLevel);
    score -= 3 * Math.abs(spot.getOutletAvail() - this.outletAvail);
    score -= Math.abs(spot.getCasualFormal() - this.casForm);
    score -= Math.abs(spot.getSocialSolitary() - this.socSolitary);
    score -= Math.abs(spot.getLivelyCalm() - this.livelyCalm);
    score -= 3 * Math.abs(spot.getSpotType() - this.outletAvail);
    if (walkDist > this.walkDist) {
      score -= 1.5 * (walkDist - this.walkDist);
    }
    return score;
  }

  public double getWalkDist(double spotLat, double spotLong) {
    double lat1Rad = Math.toRadians(spotLat);
    double lon1Rad = Math.toRadians(spotLong);
    double lat2Rad = Math.toRadians(this.userLat);
    double lon2Rad = Math.toRadians(this.userLong);
    double deltaLat = lat2Rad - lat1Rad;
    double deltaLon = lon2Rad - lon1Rad;
    double a =
        Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
            + Math.cos(lat1Rad)
                * Math.cos(lat2Rad)
                * Math.sin(deltaLon / 2)
                * Math.sin(deltaLon / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    double dist = RADIUS * c;
    System.out.println(dist);
    return dist;
  }

  public List<StudySpot> getStudySpotsByScore() {
    return this.spotData;
  }
}
