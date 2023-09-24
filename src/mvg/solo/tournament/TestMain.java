package mvg.solo.tournament;

import mvg.solo.util.BinomialDistribution;

import java.util.List;

public class TestMain {

    public static void main(String[] args) {

        TournamentCreator t = new TournamentCreator();
        System.out.println(t.instantiateKnockoutMatches());


    }
}
