package mvg.solo.data;

public class GroupData {

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

    public static String getGroupData() {
        return groupData;
    }

}
