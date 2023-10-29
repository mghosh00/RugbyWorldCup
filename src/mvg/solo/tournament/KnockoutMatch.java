package mvg.solo.tournament;

import mvg.solo.team.Team;
import mvg.solo.util.BackgroundColour;
import mvg.solo.util.Colour;

import java.util.concurrent.atomic.AtomicBoolean;

final class KnockoutMatch extends Match {

    private Match nextMatch;
    private final AtomicBoolean isNextMatchAdded = new AtomicBoolean(false);
    private Team loser = null;
    private Team winner = null;

    KnockoutMatch(int id) {
        super(id);
    }

    @Override
    void evaluateMatch(Team winner) {

        // ONE OF THE SIX BIG METHODS
        // Recall that winner is coming from playMatch() in the Match class
        if (this.winner == null) {
            this.winner = winner;
        }
        System.out.println(formattedResults());

        // Perhaps a strange decision, but if we get a DRAW, then the Match is re-run
        // Recall that if winner == null then the Match was a DRAW
        if (winner == null) {
            System.out.println("The match was a draw!");
            System.out.println("This means we will need to re-run to determine a winner!");
            // Re-calling this method will again call evaluateMatch, so we can exit the method here
            playMatch();
            return;
        }

        // Add the loser if there is not one already
        if (loser == null) {
            loser = (winner == Team.getTeam(getTeamResults().firstKey()))
                    ? Team.getTeam(getTeamResults().lastKey()) : Team.getTeam(getTeamResults().firstKey());
        }
        System.out.println(winner + " is the winner!!");

        // If the match is a QUARTER_FINAL, then we simply add the winner to the nextMatch
        if (getRound() == Round.QUARTER_FINAL) {
            System.out.println("They will now progress to " + nextMatch);
            nextMatch.addTeam(winner);
        } else if (getRound() == Round.SEMI_FINAL) {
            System.out.println("They will now progress to the grand final!!");
            nextMatch.addTeam(winner);
            System.out.println("And the loser, " + loser + " will join the third place play off!!");
        } else if (getRound() == Round.THIRD_PLACE_PLAY_OFF) {
            System.out.println(winner + " finishes 3rd Place overall, whilst " +
                    loser + " finishes 4th!");
        } else {
            System.out.printf("Congratulations to %s%s%s, the runner up!!%s%n",
                    BackgroundColour.WHITE, Colour.BLACK, loser, BackgroundColour.RESET);
            System.out.printf("But overall... %s%s%s IS THE CHAMPION!!!!%s",
                    BackgroundColour.YELLOW, Colour.BLACK, winner.toString().toUpperCase(), BackgroundColour.RESET);
        }
    }

    Team getLoser() {
        return loser;
    }

    @Override
    public String toString() {
        return switch (getRound()) {
            case QUARTER_FINAL -> "Quarter Final " + (getId() - Round.GROUP.getNumMatches());
            case SEMI_FINAL -> "Semi Final " + (getId() - Round.GROUP.getNumMatches() -
                    Round.QUARTER_FINAL.getNumMatches());
            case THIRD_PLACE_PLAY_OFF -> "Third Place Play-Off";
            case FINAL -> "Final";
            default -> "";
        };
    }

    void setNextMatch(KnockoutMatch nextMatch) {
        // This will be called exactly once by the Tournament class. We will use an AtomicBoolean
        // to ensure this
        if (isNextMatchAdded.compareAndSet(false, true)) {
            this.nextMatch = nextMatch;
        }
    }

    public Team getWinner() {
        return winner;
    }

    @Override
    public void resetMatch() {
        super.resetMatch();
        loser = null;
        winner = null;
    }
}
