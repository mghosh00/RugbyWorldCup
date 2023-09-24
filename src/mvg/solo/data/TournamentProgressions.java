package mvg.solo.data;

public class TournamentProgressions {

    // There are 40 group matches, which will have matchId = 1-40, and 8 knockout matches.
    // The quarter-finals have matchId = 41-44, the semi-finals have matchId = 45-46,
    // the third-place-play-off has matchId = 47 and the final has matchId = 48.

    // This shows which quarter-final the group winners and runners-up will progress to. For example, the
    // winner of group A (denoted A1) will progress to quarter-final 4, which has matchId = 44.
    private static final String groupProgressions = """
            A1:44
            A2:42
            B1:42
            B2:44
            C1:41
            C2:43
            D1:43
            D2:41""";

    // This shows where the winners of each knockout match will progress to. For example, the winner of
    // quarter-final 1 (matchId = 41) progresses to semi-final 1 (matchId = 45). The third-place-play-off
    // is dealt with separately.
    private static final String knockoutProgressions = """
            41:45
            42:45
            43:46
            44:46
            45:48
            46:48
            47:-1
            48:-1""";

    public static String getGroupProgressions() {
        return groupProgressions;
    }
    public static String getKnockoutProgressions() {
        return knockoutProgressions;
    }

}
