package mvg.solo.tournament;

import mvg.solo.team.Team;
import mvg.solo.util.BernoulliDistribution;
import mvg.solo.util.BinomialDistribution;

import java.util.*;

abstract class Match {

    private static int LAST_ID = 1;
    private final int id;
    private final Round round;
    private final NavigableMap<Team, List<ScoringEvent>> teamResults =
            new TreeMap<>(Comparator.comparing(Team::rankingPoints));
    private static final int MAX_SCORING_EVENTS = 8;

    // If this is changed, it must be between 0 and 60 (as the minimal ranking is around 61)
    private static final int RANKING_OFFSET = 50;

    // Two separate constructors depending on whether the Match is a GroupMatch or a KnockoutMatch
    Match() {
        this(LAST_ID ++);
    }

    Match(int id) {
        this.id = id;
        this.round = Round.matchIdToRound(this.id);
    }

    void addTeam(Team team) {

        // We only wish to add team to the Match if both Match spots have not already been filled up

        if (teamResults.keySet().size() < 2) {
            teamResults.putIfAbsent(team, new ArrayList<>());
        } else {
            System.out.println("Match already occupied by two nations, cannot add " + team);
        }
    }

    void playMatch() {

        // ONE OF THE SIX BIG METHODS
        // SUBJECT TO CHANGE DEPENDING ON CHOICE OF ALGORITHM

        // Firstly, we ensure that we have exactly two teams, else throw an error
        if (teamResults.size() != 2) {
            throw new RuntimeException(this + " must contain exactly two teams when playMatch() is called");
        }

        // Also we need to clear the Lists of ScoringEvents for each Team, so they are reset in case
        // the match needs a re-run
        for (List<ScoringEvent> scoringEvents : teamResults.values()) {
            scoringEvents.clear();
        }

        // Here, we find both teams and determine which one has a better world ranking
        Team betterTeam = teamResults.lastKey(); Team worseTeam = teamResults.firstKey();

        // Deciding on kits
        String betterTeamColoured = determineKits().get(0);
        String worseTeamColoured = determineKits().get(1);
        System.out.printf("Welcome to %s, %s vs %s!!%n", this, betterTeamColoured, worseTeamColoured);
        double betterTeamRanking = betterTeam.rankingPoints(); double worseTeamRanking = worseTeam.rankingPoints();

        // Next, we randomize a number of ScoringEvents for each team based on their rankings
        // The number of scoring events for each team is determined by a private function
        int betterTeamNumScores = numScoringEvents(betterTeamRanking, worseTeamRanking);
        int worseTeamNumScores = numScoringEvents(worseTeamRanking, betterTeamRanking);

        // Now simulate the events to work out what points to give out
        // Note that newScoringEvent updates the teamResults map
        for (int i = 0; i < betterTeamNumScores; i ++) {
            newScoringEvent(betterTeam, worseTeam);
        }

        for (int j = 0; j < worseTeamNumScores; j ++) {
            newScoringEvent(worseTeam, betterTeam);
        }

        // Find the total score for each team using the totalScore method on ScoringEvent
        int betterTeamScore = ScoringEvent.totalScore(teamResults.get(betterTeam));
        int worseTeamScore = ScoringEvent.totalScore(teamResults.get(worseTeam));

        // Finally, work out which team won (if any). If there is a winner, return that
        // team, otherwise return null for a draw
        Team winner;
        boolean isADraw = betterTeamScore == worseTeamScore;
        if (isADraw) {
            winner = null;
        } else {
            winner = (betterTeamScore > worseTeamScore) ? betterTeam : worseTeam;
        }
        evaluateMatch(winner);

        // IMPORTANT TO NOTE: both GroupMatch and KnockoutMatch will now call evaluateMatch and
        // use their different implementations
    }

    abstract void evaluateMatch(Team winner);

    private void newScoringEvent(Team currentTeam, Team otherTeam) {

        // ONE OF THE SIX BIG METHODS
        // SUBJECT TO CHANGE BASED ON CHOICE OF ALGORITHM

        // Collect the current scores from the teamResults map
        List<ScoringEvent> scoringEvents = teamResults.get(currentTeam);

        // For this ScoringEvent, we must randomise whether it is a TRY or a PENALTY
        double signedDifference = currentTeam.rankingPoints() - otherTeam.rankingPoints();
        ScoringEvent scoringEvent = tryOrPenalty(signedDifference);

        // Now, update the scoringEvents list (print statement is temporary)
        // System.out.println(currentTeam + " scores a " + scoringEvent);
        scoringEvents.add(scoringEvent);

        // Next, if it happens to be a TRY, we must randomise whether there is also a CONVERSION or not
        // This will simply be based on their ranking points
        if (scoringEvent == ScoringEvent.TRY) {
            ScoringEvent conversion = conversionAttempt(currentTeam);
            if (conversion != null) {
                // If they have scored a CONVERSION, update the scoringEvents list
                // System.out.println(currentTeam + " scores a " + conversion);
                scoringEvents.add(conversion);
            }
        }
    }

    private int numScoringEvents(double currentTeamRanking, double otherTeamRanking) {

        // create the probability parameter
        double p = (currentTeamRanking - RANKING_OFFSET) /
                (currentTeamRanking + otherTeamRanking - 2 * RANKING_OFFSET);

        // instantiate a binomial distribution
        BinomialDistribution binomial = new BinomialDistribution(MAX_SCORING_EVENTS, p);

        // return the next valid int
        return binomial.nextInt();
    }

    private ScoringEvent tryOrPenalty(double signedDifference) {
        boolean currentTeamBetter = signedDifference > 0;
        // Note that initialP is always greater than 1/3 and less than 1
        double initialP = 1 - 1 / (1.5 + Math.abs(signedDifference));
        // This linear function maps 1/3 to 1 and 1 to 1/2
        double multiplyingFactor = (5 - 3 * initialP) / 4.0;
        // This means that the currentTeam has a significant edge if they are much better. Also,
        // this takes into account that when teams are closely matched, they are less likely to
        // score tries
        double p = currentTeamBetter ? initialP : initialP * multiplyingFactor;
        BernoulliDistribution bernoulli = new BernoulliDistribution(p);
        return (bernoulli.nextBernoulli() == 1) ? ScoringEvent.TRY : ScoringEvent.PENALTY;
    }

    private ScoringEvent conversionAttempt(Team currentTeam) {
        // So this will be between 0.4 and 0.7
        double p = (currentTeam.rankingPoints() - 20) / 100;
        BernoulliDistribution bernoulli = new BernoulliDistribution(p);
        return bernoulli.nextBernoulli() == 1 ? ScoringEvent.CONVERSION : null;
    }

    List<String> determineKits() {
        Team team1 = teamResults.firstKey(); Team team2 = teamResults.lastKey();
        String team1Coloured = team1.homeKitString(); String team2Coloured = team2.homeKitString();
        if (team1.homeKit() == team2.homeKit()) {
            team2Coloured = team2.alternateKitString();
        }
        return List.of(team1Coloured, team2Coloured);
    }

    NavigableMap<Team, List<ScoringEvent>> getTeamResults() {
        // Important to make a deep copy here
        NavigableMap<Team, List<ScoringEvent>> deepCopyMap =
                new TreeMap<>(Comparator.comparing(Team::rankingPoints));
        for (Team team : teamResults.keySet()) {
            List<ScoringEvent> scoringEventsCopy = List.copyOf(teamResults.get(team));
            deepCopyMap.put(team, scoringEventsCopy);
        }
        return deepCopyMap;
    }

    public int getId() {
        return id;
    }

    public Round getRound() {
        return round;
    }

    String formattedResults() {
        Team team1 = teamResults.firstKey(); Team team2 = teamResults.lastKey();
        var team1Scores = teamResults.get(team1); var team2Scores = teamResults.get(team2);
        String team1Coloured = determineKits().get(0); String team2Coloured = determineKits().get(1);
        String firstLine = this + " Results:::\n\n";
        String secondLine = String.format("%-25s%-2d - %2d%25s%n", team1Coloured,
                ScoringEvent.totalScore(team1Scores), ScoringEvent.totalScore(team2Scores), team2Coloured);
        return firstLine + secondLine;
    }

    @Override
    public String toString() {
        return "Match " + id;
    }

    @Override
    public boolean equals(Object obj) {
        // A Match is equal to another Match if and only if they share the same id
        if (!(obj instanceof Match match)) {
            return false;
        }
        return id == match.id;
    }

    enum ScoringEvent {
        TRY(5), PENALTY(3), CONVERSION(2);

        private final int score;

        ScoringEvent(int score) {
            this.score = score;
        }

        static int totalScore(List<ScoringEvent> scores) {

            // This method calculates the totalScore from a given List of ScoringMethods
            int total = 0;
            for (ScoringEvent scoringEvent : scores) {
                total += scoringEvent.score;
            }
            return total;
        }
    }
}
