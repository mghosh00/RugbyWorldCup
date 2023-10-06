package mvg.solo.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

class MainWriter {

    public static void main(String[] args) {

    }

    private static void writeToFile(File inputFile, File outputFile) {

    }

    private static void writeToFile(String titleOfFile, String inputText) {

        Path path = Path.of(titleOfFile + ".txt");

        try {
            Files.writeString(path, inputText, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
