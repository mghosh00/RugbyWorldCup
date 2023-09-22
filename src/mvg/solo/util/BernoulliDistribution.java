package mvg.solo.util;

public class BernoulliDistribution {

    private final double successProbability;

    public BernoulliDistribution(double successProbability) {
        if (0 < successProbability && successProbability < 1) {
            this.successProbability = successProbability;
        } else {
            throw new IllegalArgumentException("successProbability must be between 0 and 1 exclusive");
        }
    }

    public int nextBernoulli() {

        // This simulates a Bernoulli(p) distribution by comparing a random double to
        // the current successProbability. So if successProbability is close to 1, then
        // this function is more likely to return 1, for example
        double randDouble = Math.random();
        if (randDouble > successProbability) {
            return 0;
        }
        return 1;
    }
}
