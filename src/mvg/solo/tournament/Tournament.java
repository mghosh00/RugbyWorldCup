package mvg.solo.tournament;

import mvg.solo.team.Team;
import mvg.solo.util.BackgroundColour;

import java.util.*;

public class Tournament {

    // The main method will only be able to see this class
    private final TournamentCreator tournamentCreator = new TournamentCreator();
    private final NavigableMap<Character, Group> groups = new TreeMap<>();
    private final Map<Round, NavigableMap<Integer, KnockoutMatch>> knockoutRounds = new HashMap<>();
    private final Map<String, Integer> groupProgressions = new HashMap<>();
    private final Map<Integer, Integer> knockoutProgressions = new HashMap<>();

    {
        // This initializer will initialize all of the above maps
        // groups:
        groups.putAll(Group.getGroups());

        // knockoutProgressions:
        knockoutProgressions.putAll(tournamentCreator.getKnockoutProgressions());

        // knockoutMatches:

        // First, instantiate all the knockoutMatches
        Map<Integer, KnockoutMatch> knockoutMatchMap = tournamentCreator.instantiateKnockoutMatches();

        // Next, use the ids to set the next Match for each knockoutMatch
        for (int matchId : knockoutMatchMap.keySet()) {
            KnockoutMatch match = knockoutMatchMap.get(matchId);
            int idOfNextMatch = knockoutProgressions.get(match.getId());

            // Recall that if this id = -1, then there is no next match
            if (idOfNextMatch != -1) {
                match.setNextMatch(knockoutMatchMap.get(idOfNextMatch));
            }
        }

        // Finally, set up the knockoutRounds map by starting with Rounds as keys and then
        // inserting the correct Map of KnockoutMatches for each Round
        Arrays.asList(Round.values()).forEach(round -> knockoutRounds.put(round, new TreeMap<>()));
        knockoutRounds.remove(Round.GROUP);
        knockoutMatchMap.forEach((i, knockoutMatch) -> knockoutRounds.get(knockoutMatch.getRound()).
                put(i, knockoutMatch));

        // groupProgressions:
        groupProgressions.putAll(tournamentCreator.getGroupProgressions());
    }

    public void beginTournament() {

        // ONE OF THE SIX BIG METHODS
        // Basic idea - run each Group first, and then work out the best Teams. Then send them to the
        // correct KnockoutMatch and run each of these to determine the winner

        // GroupMatches
        for (char c : groups.navigableKeySet()) {
            System.out.println("Commencing Group " + c);
            Group group = groups.get(c);
            group.playMatches();

            Team groupWinner = group.getWinner();
            Team groupRunnerUp = group.getRunnerUp();
            progressToQuarters(group, groupWinner, 1);
            progressToQuarters(group, groupRunnerUp, 2);
        }

        // KnockoutMatches
        for (Round round : Round.values()) {
            if (round == Round.GROUP) {
                continue;
            }

            System.out.println(BackgroundColour.BLACK + "Commencing " + round + " stage" + BackgroundColour.RESET);
            var knockoutMatches = knockoutRounds.get(round);
            for (int matchId : knockoutMatches.navigableKeySet()) {
                KnockoutMatch match = knockoutMatches.get(matchId);
                match.playMatch();

                // If the Round is a SEMI_FINAL, then we need to add the losing Team to the THIRD_PLACE_PLAY_OFF
                // We have to do this here, or it would violate encapsulation
                // Note that all the winners are added to their matches via the KnockoutMatch class
                if (round == Round.SEMI_FINAL) {
                    Team loser = match.getLoser();
                    Match thirdPlacePlayOff = knockoutRounds.get(Round.THIRD_PLACE_PLAY_OFF)
                            .firstEntry().getValue();
                    thirdPlacePlayOff.addTeam(loser);
                }
            }
        }
    }

    private void progressToQuarters(Group group, Team team, int position) {
        int nextMatchId = groupProgressions.get(String.valueOf(group.getLetter()) + position);
        Match nextMatch = knockoutRounds.get(Round.QUARTER_FINAL).get(nextMatchId);
        nextMatch.addTeam(team);
    }
}
