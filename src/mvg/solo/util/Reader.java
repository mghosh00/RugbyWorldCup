package mvg.solo.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public interface Reader {

    default Map<String, String> textToMap(String s) {
        // This method only works in the specific scenario where the String s is in the correct format.
        // Otherwise, the method will return null.
        Scanner scanner = new Scanner(s);
        Map<String, String> map = new HashMap<>();

        // While the inputted string has more lines,
        while (scanner.hasNextLine()) {

            // Save the entry, and split it by a colon. If s is in the correct format, then this will
            // produce two elements for a String array
            String entry = scanner.nextLine();
            String[] kvPair = entry.split(":");
            if (kvPair.length != 2) {
                System.out.println("Entry needs exactly two values, exiting");
                return null;
            }

            // These two elements are a key-value pair for the map, so put this in the map
            map.putIfAbsent(kvPair[0], kvPair[1]);
        }
        return map;
    }


}
