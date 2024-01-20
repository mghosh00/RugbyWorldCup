package mvg.solo.util;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public interface TextReader {

    private Map<String, List<String>> scannerToMap(Scanner scanner, int numValues) {
        // This method only works in the specific scenario where the String s is in the correct format.
        // Otherwise, the method will return null.
        Map<String, List<String>> returnMap = new HashMap<>();

        // Split the text by lines
        scanner.useDelimiter("\n");

        // Here, create a list containing lists of strings, each corresponding to the values
        // in the text file separated by some punctuation mark as specified by the regex (normally
        // a colon)
        var listOfLines = scanner.tokens()
                .map(line -> Arrays.asList(line.split("[\\p{Punct}&&[^._-]]")))
                .toList();

        // Next, provided all the lists are of the desired length (so the text is of the correct
        // format) we add each list to the returnMap
        if (listOfLines.stream().allMatch(values -> values.size() == numValues)) {
            listOfLines.forEach(values -> returnMap
                    .putIfAbsent(values.get(0), values.subList(1, numValues)));
            return returnMap;
        }

        // Else we report an error to the console, which will be thrown later
        System.out.println("Entry needs exactly " + numValues + " values, exiting");
        return null;
    }

    default Map<String, List<String>> fileToMap(String fileName, int numValues) {

        Path path = Path.of("src/main/resources/data/" + fileName);
        try (Scanner scanner = new Scanner(path)) {
            return scannerToMap(scanner, numValues);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
