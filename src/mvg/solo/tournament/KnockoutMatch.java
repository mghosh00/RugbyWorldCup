package mvg.solo.tournament;

import mvg.solo.team.Team;

import java.util.Map;

class KnockoutMatch extends Match {

    private final int idOfNextMatch;
    private static final int THIRD_PLACE_PLAY_OFF_ID = 46;

    KnockoutMatch(int idOfCurrentMatch, int idOfNextMatch) {
        // Note - if idOfNextMatch = -1, this means that there is no nextMatch (e.g. FINAL),
        // so we will note this later
        super(idOfCurrentMatch);
        this.idOfNextMatch = idOfNextMatch;
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

        // Here we get the nextMatch to progress to
        TournamentCreator tournamentCreator = new TournamentCreator();
        Map<Integer, KnockoutMatch> knockoutMatches = tournamentCreator.instantiateKnockoutMatches();
        KnockoutMatch nextMatch = knockoutMatches.get(idOfNextMatch);

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
            Match thirdPlacePlayOff = knockoutMatches.get(THIRD_PLACE_PLAY_OFF_ID);
            thirdPlacePlayOff.addTeam(loser);
        }
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
}
