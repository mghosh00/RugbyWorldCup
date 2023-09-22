package mvg.solo.team;

import mvg.solo.data.WorldRankings;
import mvg.solo.util.Reader;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class TeamCreator implements Reader {

    Set<Team> instantiateTeams() {

        // This produces a map of (hopefully) countries to their current world ranking points
        Map<String, List<String>> teamMap = textToMap(WorldRankings.getWorldRankings(), 4);

        // If textToMap failed to produce a map, we propagate this failure
        if (teamMap == null) {
            return null;
        }

        Set<Team> teams = new HashSet<>();

        for (String countryName : teamMap.keySet()) {
            try {
                List<String> values = teamMap.get(countryName);
                double rankingPoints = Double.parseDouble(values.get(0));
                Colour homeKit = Colour.valueOf(values.get(1));
                Colour alternateKit = Colour.valueOf(values.get(2));
                teams.add(new Team(countryName, rankingPoints, homeKit, alternateKit));

            } catch (IllegalArgumentException e) {
                System.out.println("Syntax error in worldRankings at country " + countryName);
                return null;
            }
        }

        return teams;
    }
}
