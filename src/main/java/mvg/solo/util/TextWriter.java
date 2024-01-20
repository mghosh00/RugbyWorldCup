package mvg.solo.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class TextWriter {


    public static void writeToFile(String titleOfFile, String inputText,
                                    StandardOpenOption openOption) {

        Path path = Path.of("src/main/resources/data/" + titleOfFile + ".txt");

        try {
            Files.writeString(path, inputText, openOption);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clearFile(String titleOfFile) {

        Path path = Path.of("src/main/resources/data/" + titleOfFile + ".txt");

        try {
            new FileWriter(path.toFile(), false).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
