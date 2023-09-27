package mvg.solo.data;

public final class WorldRankings {

    // The below shows the men's world ranking points as per https://www.world.rugby/tournaments/rankings/mru
    // on 04/09/23, the last update before the start of the world cup. Only competing teams in the world cup
    // are shown.
    private static final String worldRankings = """
            IRELAND:91.82:GREEN:WHITE
            SOUTH AFRICA:91.08:GREEN:CYAN
            FRANCE:89.22:BLUE:WHITE
            NEW ZEALAND:89.06:BLACK:WHITE
            SCOTLAND:84.01:BLUE:WHITE
            ARGENTINA:80.86:CYAN:BLUE
            FIJI:80.28:WHITE:RED
            ENGLAND:79.95:WHITE:BLUE
            AUSTRALIA:79.87:YELLOW:WHITE
            WALES:78.26:RED:BLACK
            GEORGIA:76.23:RED:WHITE
            SAMOA:76.19:BLUE:WHITE
            ITALY:75.63:BLUE:WHITE
            JAPAN:73.29:RED:BLUE
            TONGA:70.29:RED:WHITE
            PORTUGAL:68.61:RED:WHITE
            URUGUAY:66.63:CYAN:YELLOW
            ROMANIA:64.56:YELLOW:WHITE
            NAMIBIA:61.61:BLUE:WHITE
            CHILE:60.49:RED:WHITE""";

    private static final String upToDateWorldRankings = """
            IRELAND:93.79:GREEN:WHITE
            FRANCE:90.59:BLUE:WHITE
            SOUTH AFRICA:89.70:GREEN:CYAN
            NEW ZEALAND:87.69:BLACK:WHITE
            SCOTLAND:83.43:BLUE:WHITE
            ENGLAND:83.24:WHITE:BLUE
            WALES:83.17:RED:BLACK
            FIJI:80.66:WHITE:RED
            ARGENTINA:79.31:CYAN:BLUE
            AUSTRALIA:76.50:YELLOW:WHITE
            ITALY:75.93:BLUE:WHITE
            SAMOA:74.47:BLUE:WHITE
            JAPAN:73.27:RED:BLUE
            GEORGIA:73.18:RED:WHITE
            TONGA:70.29:RED:WHITE
            PORTUGAL:69.75:RED:WHITE
            URUGUAY:66.33:CYAN:YELLOW
            ROMANIA:64.56:YELLOW:WHITE
            NAMIBIA:61.61:BLUE:WHITE
            CHILE:60.49:RED:WHITE""";

    public static String getWorldRankings() {
        return upToDateWorldRankings;
    }

}
