package mvg.solo.team;

import mvg.solo.util.Colour;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Team {
    private final String countryName;
    private double ratingPoints;
    private final Colour homeKit;
    private final Colour alternateKit;

    public Team(String countryName, double ratingPoints, Colour homeKit, Colour alternateKit) {
        this.countryName = countryName;
        this.ratingPoints = ratingPoints;
        this.homeKit = homeKit;
        this.alternateKit = alternateKit;
    }

    public static Map<String, Team> getTeams() {
        // We return a map here, which will be useful for instantiating the groups. This also creates
        // a deep copy.
        TeamCreator teamCreator = new TeamCreator();
        Set<Team> teamSet = teamCreator.instantiateTeams();

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

    public String getCountryName() {
        return countryName;
    }

    public double getRatingPoints() {
        return ratingPoints;
    }

    public Colour getHomeKit() {
        return homeKit;
    }

    public Colour getAlternateKit() {
        return alternateKit;
    }

    void updateRatingPoints(double changeInPoints) {
        ratingPoints = ratingPoints + Math.round(changeInPoints * 100.00) / 100.00;
    }

    @Override
    public boolean equals(Object obj) {
        // Two teams are equal if they share the same name
        if (!(obj instanceof Team team)) {
            return false;
        }
        return countryName.equals(team.countryName);
    }

    @Override
    public int hashCode() {
        return countryName.hashCode();
    }
}
