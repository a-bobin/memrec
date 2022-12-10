package test.test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import static test.test.FileUtils.*;
import static test.test.WindowsUtils.getTotalMemory;

public class Recorder implements Runnable {

    private final boolean verbose;

    private final String processName;

    private final int interval;

    private final String csvSeparator, decSeparator;

    private final boolean showOverlay;

    private final String fileName;

    private final DateTimeFormatter logFormatter;

    private final LocalDateTime showTimeStart;

    private Overlay overlay;

    public Recorder(boolean verbose) {
        this.verbose = verbose;
        Properties properties = loadProperties();
        processName = properties.getProperty("process");
        interval = Integer.parseInt(properties.getProperty("interval")) * 1000;
        csvSeparator = properties.getProperty("separator.csv");
        decSeparator = properties.getProperty("separator.decimal");
        showOverlay = Boolean.parseBoolean(properties.getProperty("overlay.show"));
        int overlayOpacity = Integer.parseInt(properties.getProperty("overlay.opacity"));
        int fontSize = Integer.parseInt(properties.getProperty("overlay.font.size"));
        String[] fontColorRGB = properties.getProperty("overlay.font.color").split(",");

        if (processName == null || interval < 1 || csvSeparator == null || decSeparator == null ||
                overlayOpacity < 0 || overlayOpacity > 255 || fontSize < 10 || fontSize > 42
                || fontColorRGB.length < 3) {
            throw new IllegalArgumentException();
        }

        fileName = generateFileName(processName);
        createLogDir();
        logFormatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT);
        showTimeStart = LocalDateTime.now();
        if (showOverlay) {
            overlay = new Overlay(processName,
                    overlayOpacity,
                    fontSize,
                    Integer.parseInt(fontColorRGB[0]), Integer.parseInt(fontColorRGB[1]), Integer.parseInt(fontColorRGB[2]));
            overlay.setText1(processName);
        }
    }

    @Override
    public void run() {

        while (true) {
            long tStart = System.currentTimeMillis();
            LocalDateTime now = LocalDateTime.now();
            String memory = String.format("%.2f", getTotalMemory(processName) / 1024.0).replaceAll("\\D", decSeparator);
            record(memory, now);
            if (showOverlay) {
                drawOverlay(memory, now);
            }

            try {
                Thread.sleep(interval - System.currentTimeMillis() + tStart);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void record(String memory, LocalDateTime time) {
        String line = String.format("%s%s%s", logFormatter.format(time), csvSeparator, memory);
        if (verbose) {
            System.out.println("Writing line '" + line + "' to log");
        }
        writeToFile(line + "\n", "log/" + fileName);
    }

    private void drawOverlay(String memory, LocalDateTime time) {
        overlay.setText2(memory + " MB");
        Duration duration = Duration.between(showTimeStart, time);
        overlay.setText3(String.format("%02d:%02d:%02d",
                duration.toHoursPart(), duration.toMinutesPart(), duration.toSecondsPart()));
    }
}
