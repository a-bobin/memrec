package test.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileUtils {

    static final String ANSI_RESET = "\u001B[0m";
    static final String ANSI_RED = "\u001B[31m";

    static final String FILENAME_FORMAT = "yy.MM.dd_HH-mm-ss";
    static final String DATETIME_FORMAT = "M/d/yy HH:mm:ss";

    public static void writeToFile(String content, String filePath) {
        Path file = Paths.get(filePath);
        try {
            Files.write(file, content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException exception) {
            System.err.printf(ANSI_RED + "Can't write to file: %s" + ANSI_RESET + "\n", filePath);
        }
    }

    public static String generateFileName(String processName) {
        return DateTimeFormatter.ofPattern(FILENAME_FORMAT).format(LocalDateTime.now()) + "-" + processName + ".csv";
    }
}
