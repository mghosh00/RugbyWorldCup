package mvg.solo.tournament;

import mvg.solo.team.Team;

public class KnockoutMatch extends Match {

    private final KnockoutMatch nextMatch;

    KnockoutMatch(int id, KnockoutMatch nextMatch) {
        super(id);
        this.nextMatch = nextMatch;
    }

    @Override
    void evaluateMatch(Team winner) {

        // ONE OF THE SIX BIG METHODS
        // Recall that winner is coming from playMatch() in the Match class

        // Perhaps a strange decision, but if we get a DRAW, then the Match is re-run
        // Recall that if winner == null then the Match was a DRAW
        if (winner == null) {
            System.out.println("The match was a draw!");
            System.out.println(formattedResults());
            System.out.println("This means we will need to re-run to determine a winner!");
            // Re-calling this method will again call evaluateMatch, so we can exit the method here
            playMatch();
            return;
        }
        System.out.println(winner + " is the winner!!");
        // If the match is a QUARTER_FINAL, then we simply add the winner to the nextMatch
        if (getRound() == Round.QUARTER_FINAL) {
            System.out.println("They will now progress to " + nextMatch);
            nextMatch.addTeam(winner);
        } else if (getRound() == Round.SEMI_FINAL) {
            System.out.println("They will now progress to the grand final!!");
            nextMatch.addTeam(winner);
            Team loser = (winner == getTeamResults().firstKey())
                    ? getTeamResults().lastKey() : getTeamResults().firstKey();
            System.out.println("And the loser, " + loser + " will join the third place play off!!");

        }
    }
}
