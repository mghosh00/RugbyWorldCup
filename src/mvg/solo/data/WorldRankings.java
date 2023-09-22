package mvg.solo.data;

public class WorldRankings {

    // The below shows the men's world ranking points as per https://www.world.rugby/tournaments/rankings/mru
    // on 04/09/23, the last update before the start of the world cup. Only competing teams in the world cup
    // are shown.
    private static final String worldRankings = """
            IRELAND:91.82
            SOUTH AFRICA:91.08
            FRANCE:89.22
            NEW ZEALAND:89.06
            SCOTLAND:84.01
            ARGENTINA:80.86
            FIJI:80.28
            ENGLAND:79.95
            AUSTRALIA:79.87
            WALES:78.26
            GEORGIA:76.23
            SAMOA:76.19
            ITALY:75.63
            JAPAN:73.29
            TONGA:70.29
            PORTUGAL:68.61
            URUGUAY:66.63
            ROMANIA:64.56
            NAMIBIA:61.61
            CHILE:60.49""";

    public static String getWorldRankings() {
        return worldRankings;
    }

}
