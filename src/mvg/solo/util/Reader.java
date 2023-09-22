package mvg.solo.util;

import java.util.*;

public interface Reader {

    default Map<String, List<String>> textToMap(String s, int numValues) {
        // This method only works in the specific scenario where the String s is in the correct format.
        // Otherwise, the method will return null.
        Scanner scanner = new Scanner(s);
        Map<String, List<String>> map = new HashMap<>();

        // While the inputted string has more lines,
        while (scanner.hasNextLine()) {

            // Save the entry, and split it by a colon. If s is in the correct format, then this will
            // produce n elements for a String array
            String entry = scanner.nextLine();
            List<String> values = Arrays.asList(entry.split(":"));
            if (values.size() != numValues) {
                System.out.println("Entry needs exactly " + numValues + " values, exiting");
                return null;
            }

            // These n elements form a key and set of values for the map
            map.putIfAbsent(values.get(0), values.subList(1, numValues));
        }
        return map;
    }


}
