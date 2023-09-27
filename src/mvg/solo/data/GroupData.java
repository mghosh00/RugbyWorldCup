package mvg.solo.data;

public final class GroupData {

    // This simply shows which group each team belongs to. Note that there are 4 groups labelled A-D
    // with 5 teams in each group.
    private static final String groupData = """
            IRELAND:B
            SOUTH AFRICA:B
            FRANCE:A
            NEW ZEALAND:A
            SCOTLAND:B
            ARGENTINA:D
            FIJI:C
            ENGLAND:D
            AUSTRALIA:C
            WALES:C
            GEORGIA:C
            SAMOA:D
            ITALY:A
            JAPAN:D
            TONGA:B
            PORTUGAL:C
            URUGUAY:A
            ROMANIA:B
            NAMIBIA:A
            CHILE:D""";

    private static final String wackyGroupData = """
            IRELAND:A
            SOUTH AFRICA:A
            FRANCE:A
            NEW ZEALAND:A
            SCOTLAND:A
            ARGENTINA:B
            FIJI:B
            ENGLAND:B
            AUSTRALIA:B
            WALES:B
            GEORGIA:C
            SAMOA:C
            ITALY:C
            JAPAN:C
            TONGA:C
            PORTUGAL:D
            URUGUAY:D
            ROMANIA:D
            NAMIBIA:D
            CHILE:D""";

    public static String getGroupData() {
        return groupData;
    }

}
