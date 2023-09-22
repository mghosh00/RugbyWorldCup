package mvg.solo.util;

public class BinomialDistribution {

    private final int numTrials;
    private final double successProbability;

    public BinomialDistribution(int numTrials, double successProbability) {
        if (numTrials >= 1) {
            this.numTrials = numTrials;
        } else {
            throw new IllegalArgumentException("numTrials must be at least 1");
        }
        if (0 < successProbability && successProbability < 1) {
            this.successProbability = successProbability;
        } else {
            throw new IllegalArgumentException("successProbability must be between 0 and 1 exclusive");
        }
    }

    public double getMean() {
        return numTrials * successProbability;
    }

    public int nextInt() {

        // This simulates a binomial distribution by summing n independent
        // Bernoulli trials where n = numTrials
        int sum = 0;
        for (int i = 1; i <= numTrials; i ++) {
            sum += nextBernoulli();
        }
        return sum;
    }

    private int nextBernoulli() {

        // This uses the Bernoulli distribution to find the next either 0 or 1
        BernoulliDistribution bernoulli = new BernoulliDistribution(successProbability);
        return bernoulli.nextBernoulli();
    }
}
