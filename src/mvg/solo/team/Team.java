package mvg.solo.team;

import mvg.solo.util.Reader;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public record Team(String countryName, double rankingPoints) implements Reader {

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

    @Override
    public String toString() {
        StringBuilder countryCamelCase = new StringBuilder();
        String[] words = countryName.split(" ");
        for (String word : words) {
            countryCamelCase.append(word.charAt(0)).append(word.substring(1).toLowerCase());
        }
        return countryCamelCase.toString();
    }
}
