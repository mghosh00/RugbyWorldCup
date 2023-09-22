package mvg.solo.module;

import mvg.solo.team.Team;

import java.util.Map;

public class Main {

    public static void main(String[] args) {

        Map<String, Team> nations = Team.getTeams();
        System.out.println(nations);
        for (Team nation : nations.values()) {
            System.out.println("HOME: " + nation.homeKitString());
            System.out.println("ALTERNATE: " + nation.alternateKitString());
        }
    }
}
