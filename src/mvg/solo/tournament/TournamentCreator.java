package mvg.solo.tournament;

import mvg.solo.data.GroupData;
import mvg.solo.data.TournamentProgressions;
import mvg.solo.team.Team;
import mvg.solo.util.Reader;

import java.util.*;

public class TournamentCreator implements Reader {

    Set<? extends Match> instantiateMatches(Round round) {
        if (round == Round.GROUP) {

            // Use the groupProgressions String to return a map of String (as explained in the Reader
            // class) to List<String> containing only one element

            Map<String, List<String>> groupMatchesMap =
                    textToMap(TournamentProgressions.getGroupProgressions(), 2);
        }
        return null;
    }

    NavigableMap<Character, Group> instantiateGroups() {

        // This map has key equal to the Team name and value equal to their Group letter
        Map<String, List<String>> groupsMap = textToMap(GroupData.getGroupData(), 2);

        // Instantiate all groups here
        NavigableMap<Character, Group> groups = new TreeMap<>();
        List<Character> chars = List.of('A', 'B', 'C', 'D');
        chars.forEach(c -> groups.put(c, new Group(c)));

        // First retrieve the map of all Teams from the Team record
        Map<String, Team> allTeams = Team.getTeams();

        // Next, add all Teams to their correct Group
        for (String countryName : groupsMap.keySet()) {
            Team team = allTeams.get(countryName);
            char groupLetter = groupsMap.get(countryName).get(0).charAt(0);
            Group group = groups.get(groupLetter);
            group.addTeam(team);
        }
        return groups;
    }
}
