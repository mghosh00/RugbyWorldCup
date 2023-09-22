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
}
