package test.test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

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
        Map<String, String> properties = getPropertiesMap();
        processName = properties.get("process");
        interval = Integer.parseInt(properties.get("interval")) * 1000;
        csvSeparator = properties.get("separator.csv");
        decSeparator = properties.get("separator.decimal");
        showOverlay = Boolean.parseBoolean(properties.get("overlay.show"));
        int overlayOpacity = Integer.parseInt(properties.get("overlay.opacity"));
        int fontSize = Integer.parseInt(properties.get("overlay.font.size"));
        String[] fontColorRGB = properties.get("overlay.font.color").split(",");

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
        writeToFile(line + "\n", LOG_DIR_PATH + fileName);
    }

    private void drawOverlay(String memory, LocalDateTime time) {
        overlay.setText2(memory + " MB");
        Duration duration = Duration.between(showTimeStart, time);
        overlay.setText3(String.format("%02d:%02d:%02d",
                duration.toHoursPart(), duration.toMinutesPart(), duration.toSecondsPart()));
    }
}
