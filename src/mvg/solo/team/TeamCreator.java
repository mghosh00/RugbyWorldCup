package mvg.solo.team;

import mvg.solo.data.WorldRankings;
import mvg.solo.util.Reader;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class TeamCreator implements Reader {

    Set<Team> instantiateTeams() {

        // This produces a map of (hopefully) countries to their current world ranking points
        Map<String, String> teamMap = textToMap(WorldRankings.getWorldRankings());

        // If textToMap failed to produce a map, we propagate this failure
        if (teamMap == null) {
            return null;
        }

        Set<Team> teams = new HashSet<>();

        for (String inputCountryName : teamMap.keySet()) {
            try {
                double inputRankingPoints = Double.parseDouble(teamMap.get(inputCountryName));
                teams.add(new Team(inputCountryName, inputRankingPoints));

            } catch (NumberFormatException e) {
                System.out.println("Syntax error in worldRankings at country " + inputCountryName);
                return null;
            }
        }

        return teams;
    }
}
