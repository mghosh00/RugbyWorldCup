package mvg.solo.tournament;

enum Round {
    GROUP(40), QUARTER_FINAL(4), SEMI_FINAL(2), THIRD_PLACE_PLAY_OFF(1), FINAL(1);

    private final int numMatches;

    Round(int numMatches) {
        this.numMatches = numMatches;
    }

    int getNumMatches() {
        return numMatches;
    }

    static Round matchIdToRound(int id) {
        if (id < 1) {
            return null;
        }
        int maxId = 0;
        for (Round round : Round.values()) {
            maxId += round.numMatches;
            if (id <= maxId) {
                return round;
            }
        }
        return null;
    }
}
