package mvg.solo.tournament;

import mvg.solo.team.Team;
import mvg.solo.util.BackgroundColour;
import mvg.solo.util.TextReader;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

final class TournamentCreator implements TextReader {

    private static final AtomicBoolean groupsInstantiated = new AtomicBoolean(false);
    private static final AtomicBoolean knockoutMatchesInstantiated = new AtomicBoolean(false);

    Set<Group> instantiateGroups() {

        if (groupsInstantiated.compareAndSet(false, true)) {

            // This map has key equal to the Team name and value equal to their Group letter
            // Note that the List<String> has only one value
            Map<String, List<String>> groupsMap = fileToMap("groupData.txt", 2);

            // If textToMap is null, propagate this failure
            if (groupsMap == null) {
                return null;
            }

            // Instantiate all groups here
            Map<Character, Group> groups = new HashMap<>();
            Map<Character, BackgroundColour> chars = Map.of('A', BackgroundColour.BLUE,
                    'B', BackgroundColour.GREEN, 'C', BackgroundColour.RED,
                    'D', BackgroundColour.PURPLE);
            chars.forEach((k, v) -> groups.put(k, new Group(k, v)));

            // First retrieve the map of all Teams from the Team record
            Map<String, Team> allTeams = Team.getTeams();

            // Next, add all Teams to their correct Group. Note we wish to return null if either the
            // character is invalid or if the countryName is invalid. We will pass this error on to
            // the Group class
            for (String countryName : groupsMap.keySet()) {
                try {
                    Team team = allTeams.get(countryName);
                    assert team != null;

                    char groupLetter = groupsMap.get(countryName).get(0).charAt(0);
                    Group group = groups.get(groupLetter);
                    assert group != null;

                    // This addTeam method has in-built protection against adding too many
                    // Teams to a Group
                    group.addTeam(team);
                } catch (AssertionError e) {
                    System.out.println("Syntax error in groupData at country " + countryName);
                    return null;
                }
            }
            return new HashSet<>(groups.values());
        }
        System.out.println("Groups already instantiated!!");
        return null;
    }

    Map<Integer, KnockoutMatch> instantiateKnockoutMatches() {

        if (knockoutMatchesInstantiated.compareAndSet(false, true)) {

            Map<Integer, KnockoutMatch> outputMap = new HashMap<>();

            // This uses the map in the method defined below
            Map<Integer, Integer> knockoutProgressions = getKnockoutProgressions();

            assert knockoutProgressions != null;
            for (int id : knockoutProgressions.keySet()) {
                KnockoutMatch match = new KnockoutMatch(id);
                outputMap.putIfAbsent(id, match);
            }
            return outputMap;
        }
        System.out.println("Knockout matches already instantiated!!");
        return null;
    }

    Map<Integer, Integer> getKnockoutProgressions() {

        // This is a map of matchId to a singleton List containing the matchId for the
        // Match that the winner of the current matchId will progress to
        Map<String, List<String>> knockoutProgressionsMap =
                fileToMap("knockoutProgressions.txt", 2);

        // If textToMap gives null, propagate this error
        if (knockoutProgressionsMap == null) {
            return null;
        }

        Map<Integer, Integer> knockoutProgressions = new HashMap<>();

        // Now we will create the map of knockoutProgressions, catching any numerical errors

        for (String sMatchId : knockoutProgressionsMap.keySet()) {

            try {
                int idOfCurrentMatch = Integer.parseInt(sMatchId);
                int idOfNextMatch = Integer.parseInt(knockoutProgressionsMap.get(sMatchId).get(0));
                knockoutProgressions.put(idOfCurrentMatch, idOfNextMatch);
            } catch (NumberFormatException e) {
                System.out.println("Syntax error in knockoutProgressions at id " + sMatchId);
                return null;
            }
        }

        return knockoutProgressions;
    }

    Map<String, Integer> getGroupProgressions() {

        // This method reads the groupProgressions String and converts it into the correct format

        // This map should be a map from groupAndPosition (e.g. A2 for Group A runner-up) to the
        // matchId of the QUARTER_FINAL they progress to

        Map<String, List<String>> groupProgressions =
                fileToMap("groupProgressions.txt", 2);

        Map<String, Integer> outputMap = new HashMap<>();

        // We can afford to check vigorously here, so there is no need to do it later
        for (String groupAndPosition : groupProgressions.keySet()) {
            try {
                char groupLetter = groupAndPosition.charAt(0);
                assert List.of('A', 'B', 'C', 'D').contains(groupLetter);

                int groupPosition = Integer.parseInt(String.valueOf(groupAndPosition.charAt(1)));
                assert groupPosition == 1 || groupPosition == 2;

                int quarterFinalId = Integer.parseInt(
                        groupProgressions.get(groupAndPosition).get(0));
                outputMap.putIfAbsent(groupAndPosition, quarterFinalId);

            } catch (NumberFormatException | AssertionError e) {
                System.out.println("Syntax error in groupProgressions at string " + groupAndPosition);
                return null;
            }
        }
        return outputMap;
    }
}
