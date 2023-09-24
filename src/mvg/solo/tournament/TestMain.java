package mvg.solo.tournament;

import mvg.solo.util.BinomialDistribution;

import java.util.List;

public class TestMain {

    public static void main(String[] args) {

        var groups = Group.getGroups();
        groups.values().forEach(System.out::println);

    }
}
