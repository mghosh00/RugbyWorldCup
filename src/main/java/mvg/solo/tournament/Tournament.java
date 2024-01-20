package mvg.solo.tournament;

import mvg.solo.team.Team;
import mvg.solo.util.BackgroundColour;

import java.util.*;

public final class Tournament {

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

        // and we need to initialise all the group matches
        groups.values().forEach(Group::setUpMatches);

        // knockoutProgressions:
        knockoutProgressions.putAll(Objects.requireNonNull(tournamentCreator.getKnockoutProgressions()));

        // knockoutMatches:

        // First, instantiate all the knockoutMatches
        Map<Integer, KnockoutMatch> knockoutMatchMap = tournamentCreator.instantiateKnockoutMatches();

        // Next, use the ids to set the next Match for each knockoutMatch
        assert knockoutMatchMap != null;
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
        groupProgressions.putAll(Objects.requireNonNull(tournamentCreator.getGroupProgressions()));
    }

    public Team playTournament() {

        // ONE OF THE SIX BIG METHODS
        // Basic idea - run each Group first, and then work out the best Teams. Then send them to the
        // correct KnockoutMatch and run each of these to determine the winner
        Team winner = null;

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
                System.out.println();

                if (round == Round.FINAL) {
                    winner = match.getWinner();
                }
            }
        }

        // Here, we reset the tournament in case it is run multiple times
        resetTournament();
        return winner;
    }

    private void progressToQuarters(Group group, Team team, int position) {
        int nextMatchId = groupProgressions.get(String.valueOf(group.getLetter()) + position);
        Match nextMatch = knockoutRounds.get(Round.QUARTER_FINAL).get(nextMatchId);
        nextMatch.addTeam(team);
    }

    private void resetTournament() {

        // First we reset all the Groups so that all TableEntries have zeroes in all categories
        for (Group group : groups.values()) {
            group.resetTable();
        }

        // Next, we need to remove all Teams from all KnockoutMatches as these will need to
        // be empty when starting the new Tournament
        for (Round round : knockoutRounds.keySet()) {
            for (KnockoutMatch knockoutMatch : knockoutRounds.get(round).values()) {
                knockoutMatch.resetMatch();
            }
        }

        // This is all we need to reset to restart the tournament
    }
}
