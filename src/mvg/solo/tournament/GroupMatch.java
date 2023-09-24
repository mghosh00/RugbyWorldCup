package mvg.solo.tournament;

import mvg.solo.team.Team;

import java.util.*;

public class GroupMatch extends Match {

    private static final int TRY_THRESHOLD = 4;
    private static final int LOSER_SCORE_THRESHOLD = 7;
    private final Group group;

    GroupMatch(int id, Group group) {
        super(id);
        this.group = group;
    }

    @Override
    void evaluateMatch(Team winner) {

        // ONE OF THE SIX BIG METHODS
        // Recall that the winner is coming from playMatch() in the Match class

        // This is just to keep track of individual points
        var teamResults = getTeamResults();
        List<Team> teams = new ArrayList<>(teamResults.keySet());
        Team team1 = teams.get(0); Team team2 = teams.get(1);
        Map<Team, Integer> bonusPoints = new HashMap<>(Map.of(team1, 0, team2, 0));

        // A team will receive a bonus point if they score at least 4 tries
        bonusPoints.replaceAll((team, oldPoints) -> bonusPointForTries(team));

        // Recall that if winner == null then the match was a draw, so there will be no losing bonus points
        if (winner != null) {
            Team loser = (winner == team1) ? team2 : team1;

            // The losing team gains a bonus point if their score is within 7 (inclusive) of the winner
            int winnerScore = ScoringEvent.totalScore(teamResults.get(winner));
            int loserScore = ScoringEvent.totalScore(teamResults.get(loser));
            int pointsDifference = winnerScore - loserScore;
            if (pointsDifference <= LOSER_SCORE_THRESHOLD) {
                System.out.println(loser + " receives a losing bonus point!");
                bonusPoints.compute(loser, (team, oldPoints) ->
                {assert oldPoints != null; return oldPoints + 1;});
            }

            // Now we can update the group table for both teams. Note that the last entry in the method
            // is so that who has beaten who can be stored in the table
            System.out.println(winner + " is the winner!!");
            group.updateTable(winner, Outcome.WIN, pointsDifference, bonusPoints.get(winner), loser);
            group.updateTable(loser, Outcome.LOSS, -pointsDifference, bonusPoints.get(loser), winner);


        } else {
            System.out.println("The match was a draw!");
            // We have enough information for a draw to update the group table
            group.updateTable(team1, Outcome.DRAW, 0, bonusPoints.get(team1), team2);
            group.updateTable(team2, Outcome.DRAW, 0, bonusPoints.get(team2), team1);
        }
        System.out.println(formattedResults());

    }

    private int bonusPointForTries(Team team) {
        int numTries = Collections.frequency(getTeamResults().get(team), ScoringEvent.TRY);
        if (numTries >= TRY_THRESHOLD) {
            System.out.println(team + " receives a tries bonus point!");
            return 1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return String.format("%s Match %d", group, getId() % 10);
    }
}
