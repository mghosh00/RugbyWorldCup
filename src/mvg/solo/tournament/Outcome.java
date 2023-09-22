package mvg.solo.tournament;

enum Outcome {
    WIN(4), DRAW(2), LOSS(0);

    private final int points;

    Outcome(int points) {
        this.points = points;
    }

    int getPoints() {
        return points;
    }
}
