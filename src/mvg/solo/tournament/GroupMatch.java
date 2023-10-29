package mvg.solo.tournament;

import mvg.solo.team.Team;
import mvg.solo.util.BackgroundColour;

import java.util.*;

final class GroupMatch extends Match {

    private static final int TRY_THRESHOLD = 4;
    private static final int LOSER_SCORE_THRESHOLD = 7;
    private static final int MATCHES_PER_GROUP = 10;
    private final Group group;

    GroupMatch(Group group) {
        super();
        this.group = group;
    }

    @Override
    void evaluateMatch(Team winner) {

        // ONE OF THE SIX BIG METHODS
        // Recall that the winner is coming from playMatch() in the Match class

        // This is just to keep track of individual points
        var teamResults = getTeamResults();
        List<String> teamNames = new ArrayList<>(teamResults.keySet());
        Team team1 = Team.getTeam(teamNames.get(0));
        Team team2 = Team.getTeam(teamNames.get(1));
        Map<String, Integer> bonusPoints = new HashMap<>(Map.of(team1.getCountryName(), 0,
                team2.getCountryName(), 0));

        // A team will receive a bonus point if they score at least 4 tries
        bonusPoints.replaceAll((teamName, oldPoints) -> bonusPointForTries(Team.getTeam(teamName)));

        // Recall that if winner == null then the match was a draw, so there will be no losing bonus points
        if (winner != null) {
            Team loser = (winner == team1) ? team2 : team1;

            // The losing team gains a bonus point if their score is within 7 (inclusive) of the winner
            int winnerScore = ScoringEvent.totalScore(teamResults.get(winner.getCountryName()));
            int loserScore = ScoringEvent.totalScore(teamResults.get(loser.getCountryName()));
            int pointsDifference = winnerScore - loserScore;
            if (pointsDifference <= LOSER_SCORE_THRESHOLD) {
                System.out.println(loser + " receives a losing bonus point!");
                bonusPoints.compute(loser.getCountryName(), (team, oldPoints) ->
                {assert oldPoints != null; return oldPoints + 1;});
            }

            // Now we can update the group table for both teams. Note that the last entry in the method
            // is so that who has beaten who can be stored in the table
            System.out.println(winner + " is the winner!!");
            group.updateTable(winner, Outcome.WIN, pointsDifference,
                    bonusPoints.get(winner.getCountryName()), loser);
            group.updateTable(loser, Outcome.LOSS, -pointsDifference,
                    bonusPoints.get(loser.getCountryName()), winner);


        } else {
            System.out.println("The match was a draw!");
            // We have enough information for a draw to update the group table
            group.updateTable(team1, Outcome.DRAW, 0,
                    bonusPoints.get(team1.getCountryName()), team2);
            group.updateTable(team2, Outcome.DRAW, 0,
                    bonusPoints.get(team2.getCountryName()), team1);
        }
        System.out.println(formattedResults());

    }

    private int bonusPointForTries(Team team) {
        int numTries = Collections.frequency(getTeamResults().get(team.getCountryName()), ScoringEvent.TRY);
        if (numTries >= TRY_THRESHOLD) {
            System.out.println(team + " receives a tries bonus point!");
            return 1;
        }
        return 0;
    }

    @Override
    public String toString() {
        int remainder = getId() % MATCHES_PER_GROUP;
        return String.format("%sGroup %c%s Match %d", group.getColour(), group.getLetter(),
                BackgroundColour.RESET,
                remainder == 0 ? MATCHES_PER_GROUP : remainder);
    }
}
