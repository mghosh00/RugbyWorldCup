package mvg.solo.team;

import mvg.solo.util.Colour;
import mvg.solo.util.TextWriter;

import java.nio.file.StandardOpenOption;
import java.util.*;

public record Team(String countryName, double initialRatingPoints, Colour homeKit, Colour alternateKit) {

    private static final TeamCreator creator = new TeamCreator();
    public static Map<String, Team> getTeams() {
        // We return a map here, which will be useful for instantiating the groups. This also creates
        // a deep copy.
        Set<Team> teamSet = creator.instantiateTeams();

        // Here we throw an exception if the data was erroneous in any form
        if (teamSet == null) {
            throw new RuntimeException("Initial data was erroneous in some form, please" +
                    " review mvg.solo.data.WorldRankings");
        }

        Map<String, Team> teamMap = new HashMap<>();

        for (Team team : teamSet) {
            teamMap.put(team.countryName, team);
        }

        return teamMap;
    }

    public static Team getTeam(String teamName) {
        Map<String, Team> teams = getTeams();
        return teams.get(teamName);
    }

    public double getRatingPoints() {
        // Here, we read from the file containing the current updated rating points

        // Here we use the TeamCreator to read the rating points from "ratingPoints.txt"
        return creator.readRatingPoints(this);
    }

    void updateRatingPoints(double changeInPoints) {
        // Here, we write to the file containing the updated rating points
        double newRatingPoints = getRatingPoints() + Math.round(changeInPoints * 100.00) / 100.00;
        var ratingPointsMap = creator.getRatingPointsMap();

        // Update the map
        ratingPointsMap.get(countryName).replaceAll(s -> String.valueOf(newRatingPoints));

        // Refresh the file
        TextWriter.clearFile("ratingPoints");

        // Rewrite the updated data to the file
        ratingPointsMap.forEach(
                (k, v) -> TextWriter.writeToFile("ratingPoints", k + ":" + v.get(0) + "\n",
                        StandardOpenOption.APPEND)
        );

    }

    @Override
    public String toString() {
        StringBuilder countryCamelCase = new StringBuilder();
        String[] words = countryName.split(" ");
        for (String word : words) {
            countryCamelCase.append(word.charAt(0)).append(word.substring(1).toLowerCase());
            countryCamelCase.append(" ");
        }
        countryCamelCase.deleteCharAt(countryCamelCase.length() - 1);
        return countryCamelCase.toString();
    }

    public String homeKitString() {
        return homeKit + toString() + Colour.RESET;
    }

    public String alternateKitString() {
        return alternateKit + toString() + Colour.RESET;
    }
}
