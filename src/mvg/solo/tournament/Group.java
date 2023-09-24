package mvg.solo.tournament;

import mvg.solo.team.Team;

import java.util.*;

public class Group {

    private final char letter;
    private final NavigableMap<Team, TableEntry> table = new TreeMap<>(
            Comparator.comparing(Team::rankingPoints));
    private final NavigableMap<Integer, GroupMatch> matches = new TreeMap<>();

    public Group(char letter) {
        this.letter = letter;
    }

    void addTeam(Team team) {

        // Here we wish to add a new Team to the table map, instantiating a new TableEntry for the team
        // at the same time

        TableEntry tableEntry = new TableEntry(team);
        table.putIfAbsent(team, tableEntry);
    }

    static NavigableMap<Character, Group> getGroups() {
        TournamentCreator tournamentCreator = new TournamentCreator();
        Set<Group> groups = tournamentCreator.instantiateGroups();

        // If the data was erroneous in any form, we throw an error here
        if (groups == null) {
            throw new RuntimeException("Initial data was erroneous in some form, please" +
                    " review mvg.solo.data.GroupData");
        }

        NavigableMap<Character, Group> groupMap = new TreeMap<>();
        for (Group group : groups) {
            groupMap.put(group.letter, group);
        }
        return groupMap;
    }

    void updateTable(Team team, Outcome outcome, int pointsDifference, int bonusPoints,
                     Team otherTeam) {
        // Here we simply pass the work on to the TableEntry for the inputted Team
        table.get(team).updateEntry(outcome, pointsDifference, bonusPoints, otherTeam);
    }

    void playMatches() {
        // First we need to set up the different Matches (round-robin for all 5 Teams)
        setUpMatches();

        // Here we simply play each Match in the Group
        for (int matchId : matches.keySet()) {
            matches.get(matchId).playMatch();
        }
    }

    private void setUpMatches() {
        // ONE OF THE SIX BIG METHODS

        // The goal here is to create 10 round-robin matches
        int currentMatchId = matches.firstKey();

        for (Team firstTeam : table.navigableKeySet()) {

            // This ensures that secondTeam is truly not equal to firstTeam
            for (Team secondTeam : table.tailMap(firstTeam, false).keySet()) {
                Match currentMatch = matches.get(currentMatchId);
                currentMatch.addTeam(firstTeam);
                currentMatch.addTeam(secondTeam);

                // If we have reached the final matchId, then we exit the method
                if (currentMatchId == matches.lastKey()) {
                    return;
                }
                currentMatchId ++;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(separatorLine('='));
        s.append(String.format("Group %c%n", letter));
        s.append(separatorLine('-'));

        // Here we will present the group table
        s.append(String.format("%-15s%2c%2c%2c%2c%5s%3s%5s%n",
                "Country", 'P', 'W', 'D', 'L', "PD", "BP", "PTS"));
        s.append(separatorLine('-'));
        for (TableEntry tableEntry : table.values()) {
            s.append(tableEntry.toString());
        }
        s.append(separatorLine('='));
        return s.toString();
    }

    private String separatorLine(char c) {
        return String.valueOf(c).repeat(36) + "\n";
    }

    private static class TableEntry implements Comparator<TableEntry> {

        private final Team team;
        private int matchesPlayed;
        private final Map<Outcome, Integer> outcomes = new HashMap<>();
        private int pointsDifference;
        private int bonusPoints;
        private final Set<Team> beatenTeams = new HashSet<>();

        private TableEntry(Team team) {
            this.team = team;
            this.matchesPlayed = 0;

            // This initialises the TableEntry with no wins, no draws and no losses
            this.outcomes.putAll(Map.of(Outcome.WIN, 0, Outcome.DRAW, 0, Outcome.LOSS, 0));
            this.pointsDifference = 0; this.bonusPoints = 0;
        }

        private int getTotalPoints() {

            // This method calculates the total points from the table
            // The rules are 4 points for a WIN, 2 for a DRAW and 0 for a LOSS, and
            // add any bonusPoints to this total
            int totalPoints = bonusPoints;
            for (Outcome outcome : outcomes.keySet()) {
                totalPoints += outcome.getPoints() * outcomes.get(outcome);
            }
            return totalPoints;
        }

        private void updateEntry(Outcome outcome, int pointsDifference, int bonusPoints, Team otherTeam) {
            // This method can only be called by Group.updateTable as it is private within Group.

            // Here we update the outcomes column by adding one new outcome to the correct key
            outcomes.compute(outcome, (o, oldValue) -> {assert oldValue != null; return oldValue + 1;});

            // Then update all other columns in the table
            this.matchesPlayed ++;
            this.pointsDifference += pointsDifference;
            this.bonusPoints += bonusPoints;

            // Finally we need to know which Teams have beaten which to determine Group rankings. This
            // will not be displayed in the toString() method
            if (outcome == Outcome.WIN) {
                beatenTeams.add(otherTeam);
            }
        }

        @Override
        public String toString() {
            // For reference, we give 15 characters for the name, 2 for matchesPlayed and all outcomes,
            // 5 for pointsDifference, 2 for bonusPoints and finally 5 for totalPoints
            return String.format("%-15s%2d%2d%2d%2d%5d%3d%5d%n", team, matchesPlayed,
                    outcomes.get(Outcome.WIN), outcomes.get(Outcome.DRAW), outcomes.get(Outcome.LOSS),
                    pointsDifference, bonusPoints, getTotalPoints());
        }

        @Override
        public int compare(TableEntry o1, TableEntry o2) {

            // Here we implement the rules for ordering the Group as determined in the following
            // link: https://www.rugbyworldcup.com/news/35290. Note that we are skipping a few
            // comparisons as we are highly unlikely to get to this stage and this will reduce
            // the amount of information we need to keep track of

            // 1. The Team with the most points is higher
            if (o1.getTotalPoints() != o2.getTotalPoints()) {
                return o1.getTotalPoints() - o2.getTotalPoints();
            }

            // 2. The winner of the Match between these two Teams is higher
            if (o1.getBeatenTeams().contains(o2.team)) {
                return 1;
            } else if (o2.getBeatenTeams().contains(o1.team)) {
                return -1;
            }

            // 3. The Team with the better pointsDifference is higher
            if (o1.pointsDifference != o2.pointsDifference) {
                return o1.pointsDifference - o2.pointsDifference;
            }

            // 4. The worldRankings will determine the higher Team as of 04/09/23
            return (int) (o1.team.rankingPoints() - o2.team.rankingPoints()) * 100;

        }

        private Set<Team> getBeatenTeams() {
            return Set.copyOf(beatenTeams);
        }
    }
}
