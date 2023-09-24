package mvg.solo.util;

public enum BackgroundColour {
    RESET, BLACK, RED, GREEN, YELLOW, BLUE, PURPLE, CYAN, WHITE;


    @Override
    public String toString() {
        String escapeSequence = "\u001B[";
        char finalCharacter = 'm';
        if (this == RESET) {
            return escapeSequence + "0" + finalCharacter;
        }
        return escapeSequence + (ordinal() + 39) + finalCharacter;
    }
}
