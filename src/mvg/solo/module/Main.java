package mvg.solo.module;

import mvg.solo.team.Team;
import mvg.solo.tournament.Tournament;

public class Main {

    public static void main(String[] args) {

        runTournaments(1);

    }

    private static void runTournaments(int numTournaments) {
        Tournament tournament = new Tournament();
        ResultsRecorder recorder = new ResultsRecorder();
        for (int i = 0; i < numTournaments; i ++) {
            Team winner = tournament.playTournament();
            recorder.recordWin(winner.getCountryName());
        }
        recorder.writeToFile();
    }
}
