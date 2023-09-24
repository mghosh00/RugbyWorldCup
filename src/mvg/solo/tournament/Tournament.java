package mvg.solo.tournament;

import mvg.solo.team.Team;

import java.util.*;

public class Tournament {

    // The main method will only be able to see this class
    private final TournamentCreator tournamentCreator = new TournamentCreator();
    private final NavigableMap<Character, Group> groups = new TreeMap<>();
    private final Map<Round, NavigableMap<Integer, KnockoutMatch>> knockoutRounds = new HashMap<>();
    private final Map<String, Integer> groupProgressions = new HashMap<>();

    {
        // This initializer will initialize all of the above maps
        // groups:
        groups.putAll(Group.getGroups());

        // knockoutMatches:
        Map<Integer, KnockoutMatch> knockoutMatchMap = tournamentCreator.instantiateKnockoutMatches();
        Arrays.asList(Round.values()).forEach(round -> knockoutRounds.put(round, new TreeMap<>()));
        knockoutRounds.remove(Round.GROUP);
        knockoutMatchMap.forEach((i, k) -> knockoutRounds.get(k.getRound()).
                put(i, k));

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
                break;
            }
            System.out.println("Commencing " + round);
            var knockoutMatches = knockoutRounds.get(round);
            for (int matchId : knockoutMatches.navigableKeySet()) {
                KnockoutMatch match = knockoutMatches.get(matchId);
                match.playMatch();
            }
        }
    }

    private void progressToQuarters(Group group, Team team, int position) {
        int nextMatchId = groupProgressions.get(String.valueOf(group.getLetter()) + position);
        Match nextMatch = knockoutRounds.get(Round.QUARTER_FINAL).get(nextMatchId);
        nextMatch.addTeam(team);
    }
}
