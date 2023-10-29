package mvg.solo.team;

import mvg.solo.util.Colour;
import mvg.solo.util.TextReader;
import mvg.solo.util.TextWriter;

import java.nio.file.StandardOpenOption;
import java.util.*;

final class TeamCreator implements TextReader {

    Set<Team> instantiateTeams() {

        // This produces a map of (hopefully) countries to their initial rating points and kits
        Map<String, List<String>> teamMap = fileToMap("initialWorldRankings.txt", 4);

        // If textToMap failed to produce a map, we propagate this failure
        if (teamMap == null) {
            return null;
        }

        Set<Team> teams = new HashSet<>();

        // Refresh file
        TextWriter.clearFile("ratingPoints");

        for (String countryName : teamMap.keySet()) {
            try {
                List<String> values = teamMap.get(countryName);
                double initialRatingPoints = Double.parseDouble(values.get(0));
                Colour homeKit = Colour.valueOf(values.get(1));
                Colour alternateKit = Colour.valueOf(values.get(2));
                teams.add(new Team(countryName, initialRatingPoints, homeKit, alternateKit));

                // Here we write this initial data to a new file, which will store the
                // rating points over the course of the tournament
                TextWriter.writeToFile("ratingPoints", countryName + ":" + initialRatingPoints + "\n",
                        StandardOpenOption.APPEND);


            } catch (IllegalArgumentException e) {
                System.out.println("Syntax error in worldRankings at country " + countryName);
                return null;
            }
        }

        return teams;
    }

    double readRatingPoints(Team team) {

        // This produces a map of countries to their current rating points
        Map<String, List<String>> ratingsMap = getRatingPointsMap();

        // If this map is empty or the team does not exist in the map, an error has occurred, so throw one
        if (!ratingsMap.containsKey(team.countryName())) {
            throw new RuntimeException("File ratingPoints.txt is empty or erroneous, please review");
        }

        // Otherwise, read the points for the desired Team
        try {
            return Double.parseDouble(ratingsMap.get(team.countryName()).get(0));
        } catch (NumberFormatException e) {
            throw new RuntimeException("File ratingPoints.txt is erroneous, please review");
        }
    }

    Map<String, List<String>> getRatingPointsMap() {
        var map = fileToMap("ratingPoints.txt", 2);
        if (map == null) {
            throw new RuntimeException("File ratingPoints.txt is empty or erroneous, please review");
        }
        return map;
    }
}
