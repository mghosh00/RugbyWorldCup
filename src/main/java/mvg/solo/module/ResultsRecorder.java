package mvg.solo.module;

import mvg.solo.team.Team;
import mvg.solo.util.TextReader;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class ResultsRecorder implements TextReader {

    // A map containing the number of wins each team has
    private final Map<String, Integer> currentResults = new TreeMap<>();

    {
        // The following initializer first reads the file 'results.txt', containing data on how
        // many tournament wins each team has, and then converts that data into the desired format
        var stringResults = fileToMap("results.txt", 2);
        try {
            stringResults.forEach((teamName, value) -> currentResults.put(teamName, Integer.parseInt(value.get(0))));
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid entries in file 'results.txt', please review.");
        }
    }

    void recordWin(String teamName) {
        if (Team.getTeam(teamName) != null) {
            if (!currentResults.containsKey(teamName)) {
                currentResults.put(teamName, 1);
            } else {
                currentResults.compute(teamName, (k, v) -> {assert v != null; return v + 1;});
            }
        }
    }

    void writeToFile() {
        Path path = Path.of("src/main/resources/data/results.txt");
        try {
            var listOfLines = currentResults.entrySet().stream()
                    .map(entry -> entry.getKey() + ":" + entry.getValue())
                    .toList();
            new FileWriter(path.toFile(), false).close();
            for (String line : listOfLines) {
                Files.writeString(path, line + "\n", StandardOpenOption.APPEND);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
