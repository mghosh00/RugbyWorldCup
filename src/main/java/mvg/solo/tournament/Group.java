package mvg.solo.tournament;

import mvg.solo.team.Team;
import mvg.solo.util.BackgroundColour;

import java.util.*;

final class Group {

    private final char letter;
    private final BackgroundColour colour;
    private NavigableSet<TableEntry> table = new TreeSet<>();
    private final NavigableMap<Integer, GroupMatch> matches = new TreeMap<>();
    private final static int NUM_TEAMS_IN_GROUP = 5;
    private final static int NUM_GROUPS = 4;

    {
        // This initializer block is used to set up all the group matches. We first instantiate
        // the new GroupMatches and then add them to the matches TreeMap
        for (int i = 0; i < Round.GROUP.getNumMatches() / NUM_GROUPS; i ++) {
            GroupMatch groupMatch = new GroupMatch(this);
            matches.putIfAbsent(groupMatch.getId(), groupMatch);
        }
    }

    public Group(char letter, BackgroundColour colour) {
        this.letter = letter;
        this.colour = colour;
    }

    void addTeam(Team team) throws RuntimeException {

        // Here we wish to add a new Team to the table set, instantiating a new TableEntry for the team
        // at the same time, only if the group is not full

        if (table.size() < NUM_TEAMS_IN_GROUP) {
            TableEntry tableEntry = new TableEntry(team);
            table.add(tableEntry);
        } else {
            throw new RuntimeException("Failed to add " + team + " - must have exactly " +
                    NUM_TEAMS_IN_GROUP + " Teams in " + "Group " + letter +
                    ". Please review mvg.solo.data.GroupData");
        }
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
        // (and create a Map in the process to do this)
        Map<Team, TableEntry> tableMap = new HashMap<>();
        table.forEach(tableEntry -> tableMap.putIfAbsent(tableEntry.team, tableEntry));
        tableMap.get(team).updateEntry(outcome, pointsDifference, bonusPoints, otherTeam);
    }

    void playMatches() {

        // Here we simply play each Match in the Group
        for (int matchId : matches.keySet()) {
            matches.get(matchId).playMatch();
        }

        // This is to re-sort the elements
        List<TableEntry> tableEntries = new ArrayList<>(table);
        Collections.sort(tableEntries);
        table = new TreeSet<>(tableEntries);

        // Reveal the results
        System.out.println(this);
    }

    void setUpMatches() {
        // ONE OF THE SIX BIG METHODS

        // The goal here is to create 10 round-robin matches

        // Then find the lowest matchId
        int currentMatchId = matches.firstKey();

        for (TableEntry firstEntry : table) {

            // This ensures that secondEntry is truly not equal to firstEntry
            for (TableEntry secondEntry : table.tailSet(firstEntry, false)) {
                Match currentMatch = matches.get(currentMatchId);
                currentMatch.addTeam(firstEntry.team);
                currentMatch.addTeam(secondEntry.team);

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
        s.append(String.format("%sGroup %c%s%n", colour, letter, BackgroundColour.RESET));
        s.append(separatorLine('-'));

        // Here we will present the group table
        s.append(String.format("%-15s%2c%2c%2c%2c%5s%3s%5s%n",
                "Country", 'P', 'W', 'D', 'L', "PD", "BP", "PTS"));
        s.append(separatorLine('-'));
        for (TableEntry tableEntry : table) {
            s.append(tableEntry.toString());
        }
        s.append(separatorLine('='));
        return s.toString();
    }

    private String separatorLine(char c) {
        return String.valueOf(c).repeat(36) + "\n";
    }

    char getLetter() {
        return letter;
    }

    BackgroundColour getColour() {
        return colour;
    }

    Team getWinner() {
        return table.first().team;
    }

    Team getRunnerUp() {
        return Objects.requireNonNull(table.higher(table.first())).team;
    }

    void resetTable() {

        // Here we must reset each individual TableEntry in the table
        for (TableEntry tableEntry : table) {
            tableEntry.resetEntry();
        }
    }

    private static class TableEntry implements Comparable<TableEntry> {

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
            this.pointsDifference = 0;
            this.bonusPoints = 0;
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
            outcomes.compute(outcome, (o, oldValue) -> {
                assert oldValue != null;
                return oldValue + 1;
            });

            // Then update all other columns in the table
            this.matchesPlayed++;
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

        private Set<Team> getBeatenTeams() {
            return Set.copyOf(beatenTeams);
        }

        @Override
        public int compareTo(TableEntry o) {
            // Here we implement the rules for ordering the Group as determined in the following
            // link: https://www.rugbyworldcup.com/news/35290. Note that we are skipping a few
            // comparisons as we are highly unlikely to get to this stage and this will reduce
            // the amount of information we need to keep track of

            // 1. The Team with the most points is higher
            if (getTotalPoints() != o.getTotalPoints()) {
                return o.getTotalPoints() - getTotalPoints();
            }

            // 2. The winner of the Match between these two Teams is higher
            if (getBeatenTeams().contains(o.team)) {
                return -1;
            } else if (o.getBeatenTeams().contains(team)) {
                return 1;
            }

            // 3. The Team with the better pointsDifference is higher
            if (pointsDifference != o.pointsDifference) {
                return o.pointsDifference - pointsDifference;
            }

            // 4. The worldRankings will determine the higher Team as of 04/09/23
            // Note that this will return a unique value unless two teams have exactly
            // the same initialRatingPoints(), in which case we will compare the names (to sort
            // a bug)
            if (team.initialRatingPoints() != o.team.initialRatingPoints()) {
                return (int) ((o.team.initialRatingPoints() - team.initialRatingPoints()) * 100);
            }

            // Note that we should never reach this line, but is here just in case
            return o.team.countryName().compareTo(team.countryName());

        }

        private void resetEntry() {
            matchesPlayed = 0;
            outcomes.replaceAll(((outcome, i) -> 0));
            pointsDifference = 0;
            bonusPoints = 0;
            beatenTeams.clear();
        }
    }
}
