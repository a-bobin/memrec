package test.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class FileUtils {

    static final String ANSI_RESET = "\u001B[0m";
    static final String ANSI_RED = "\u001B[31m";

    static final String FILENAME_FORMAT = "yy.MM.dd_HH-mm-ss";
    static final String DATETIME_FORMAT = "M/d/yy HH:mm:ss";

    static final String DEFAULT_PROPERTIES = "" +
            "process=java.exe\n" +
            "interval=1\n" +
            "separator.csv=;\n" +
            "separator.decimal=.\n" +
            "overlay.show=true\n" +
            "overlay.opacity=188\n" +
            "overlay.font.size=21\n" +
            "overlay.font.color=177,188,85";

    public static Properties loadProperties() {
        String pathToProperties = "./memrec.properties";
        Path propertiesFile = Paths.get("./memrec.properties");

        if (Files.notExists(propertiesFile)) {
            writeToFile(DEFAULT_PROPERTIES, pathToProperties);
        }

        Properties properties = new Properties();
        try {
            properties.load(Files.newInputStream(propertiesFile));
        } catch (IOException e) {
            System.err.printf(ANSI_RED + "Can't load properties from %s" + ANSI_RESET + "\n", pathToProperties);
        }

        return properties;
    }

    public static void writeToFile(String content, String filePath) {
        Path file = Paths.get(filePath);
        try {
            Files.write(file, content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException exception) {
            System.err.printf(ANSI_RED + "Can't write to file: %s" + ANSI_RESET + "\n", filePath);
        }
    }

    public static void createLogDir() {
        Path logDir = Paths.get("log/");
        if (Files.notExists(logDir)) {
            try {
                Files.createDirectory(logDir);
            } catch (IOException e) {
                System.err.println(ANSI_RED + "Can't create log directory" + ANSI_RESET + "\n");
            }
        }
    }

    public static String generateFileName(String processName) {
        return DateTimeFormatter.ofPattern(FILENAME_FORMAT).format(LocalDateTime.now()) + "-" + processName + ".csv";
    }
}
