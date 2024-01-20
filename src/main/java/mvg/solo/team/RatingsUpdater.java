package mvg.solo.team;

public class RatingsUpdater {

    private final Team betterTeam;
    private final int betterTeamScore;
    private final Team worseTeam;
    private final int worseTeamScore;
    private static final double CRITICAL_RATING_GAP = 10.0;
    private static final double CRITICAL_RATING_CHANGE = 1.0;

    public RatingsUpdater(Team betterTeam, int betterTeamScore, Team worseTeam, int worseTeamScore) {
        this.betterTeam = betterTeam;
        this.betterTeamScore = betterTeamScore;
        this.worseTeam = worseTeam;
        this.worseTeamScore = worseTeamScore;
    }

    private double getCurrentRatingGap(Team team) {

        Team otherTeam = (team == betterTeam) ? worseTeam : betterTeam;

        return otherTeam.getRatingPoints() - team.getRatingPoints();
    }

    private double coreRatingChange(Team team, String outcome) {

        double ratingGap = getCurrentRatingGap(team);

        // First calculate what the coreRatingChange would be for a draw, and then shift up or down
        // by 1.0 if it is a win or loss respectively

        // The below produces a piecewise-linear function for the drawRatingChange as a function of
        // ratingGap
        double drawRatingChange;
        if (ratingGap < -CRITICAL_RATING_GAP) {
            // If the ratingGap is very negative (i.e. we have a much better team) then their
            // rating change for a draw is the CRITICAL_RATING_CHANGE
            drawRatingChange = -CRITICAL_RATING_CHANGE;

        } else if (-CRITICAL_RATING_GAP <= ratingGap && ratingGap <= CRITICAL_RATING_GAP) {
            // If the magnitude of the ratingGap is smaller than the CRITICAL_RATING_GAP, then
            // we calculate the rating change for a draw by scaling the ratingGap
            drawRatingChange = ratingGap / CRITICAL_RATING_GAP;
        } else {
            drawRatingChange = CRITICAL_RATING_CHANGE;
        }

        return switch (outcome) {
            case "WIN" -> drawRatingChange + 1.0;
            case "DRAW" -> drawRatingChange;
            case "LOSS" -> drawRatingChange - 1.0;

            // Something has gone wrong here, so we can default to 0 to abort the rating change.
            default -> 0.0;
        };

    }

    public void changeRankings() {

        if (betterTeamScore == worseTeamScore) {
            double betterTeamCoreChange = coreRatingChange(betterTeam, "DRAW");
            double worseTeamCoreChange = coreRatingChange(worseTeam, "DRAW");

            // As it is a world cup, we double these
            double betterTeamRatingChange = betterTeamCoreChange * 2;
            double worseTeamRatingChange = worseTeamCoreChange * 2;
            betterTeam.updateRatingPoints(betterTeamRatingChange);
            worseTeam.updateRatingPoints(worseTeamRatingChange);
            return;
        }

        Team winner = (betterTeamScore > worseTeamScore) ? betterTeam : worseTeam;
        Team loser = (betterTeam == winner) ? worseTeam : betterTeam;

        double winnerCoreChange = coreRatingChange(winner, "WIN");
        double loserCoreChange = coreRatingChange(loser, "LOSS");

        double winnerRatingChange; double loserRatingChange;

        // If the winner wins by more than 15
        if (Math.abs(betterTeamScore - worseTeamScore) > 15) {
            winnerRatingChange = winnerCoreChange * 2 * 1.5;
            loserRatingChange = loserCoreChange * 2 * 1.5;
        } else {
            winnerRatingChange = winnerCoreChange * 2;
            loserRatingChange = loserCoreChange * 2;
        }

        winner.updateRatingPoints(winnerRatingChange);
        loser.updateRatingPoints(loserRatingChange);
    }
}
