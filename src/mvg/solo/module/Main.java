package mvg.solo.module;

import mvg.solo.team.Team;
import mvg.solo.tournament.Tournament;

import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        Tournament tournament = new Tournament();
        Map<Team, Integer> teamWins = new HashMap<>();
        for (int i = 0; i < 1000; i ++) {
            Team winner = tournament.playTournament();
            if (!teamWins.containsKey(winner)) {
                teamWins.put(winner, 1);
            } else {
                teamWins.compute(winner, (k, v) -> {assert v != null; return v + 1;});
            }
        }
        System.out.println(teamWins);


    }
}
