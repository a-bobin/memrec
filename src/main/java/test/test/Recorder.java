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

    private final boolean showOverlay, showTimer;

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
        showTimer = Boolean.parseBoolean(properties.get("timer.show"));
        int overlayOpacity = Integer.parseInt(properties.get("overlay.opacity"));
        int fontSize = Integer.parseInt(properties.get("overlay.font.size"));
        String[] fontColorRGB = properties.get("overlay.font.color").split(",");

        fileName = generateFileName(processName);
        createLogDir();
        logFormatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT);
        showTimeStart = LocalDateTime.now();
        if (showOverlay) {
            overlay = new Overlay(processName,
                    showTimer,
                    overlayOpacity,
                    fontSize,
                    Integer.parseInt(fontColorRGB[0]), Integer.parseInt(fontColorRGB[1]), Integer.parseInt(fontColorRGB[2]));
            overlay.setText1(processName);
        }
    }

    @Override
    public void run() {
        long tStart0 = System.currentTimeMillis();
        int counter = 0;
        while (true) {
            long correction = ++counter % 12 == 0 ? (System.currentTimeMillis() - tStart0)%1000L : 0;
            long tStart = System.currentTimeMillis();
            LocalDateTime now = LocalDateTime.now();
            String memory = String.format("%.2f", getTotalMemory(processName) / 1024.0).replaceAll("\\D", decSeparator);
            record(memory, now);
            /* skip overlay renewal if getting info was too long, for uniformity */
            if (System.currentTimeMillis() - tStart < 200 && showOverlay) {
                drawOverlay(memory, now);
            }
            long workingTime = System.currentTimeMillis() - tStart;

            try {
                Thread.sleep(recalculateSleepTime(interval - correction, workingTime, 1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (verbose) {
                System.out.printf("\t  Total time %-7d\n\tWorking time %-4d\n", System.currentTimeMillis() - tStart0, workingTime);
            }
        }
    }

    private long recalculateSleepTime(long interval, long workingTime, long step) {
        return interval < workingTime ? recalculateSleepTime(interval + step, workingTime, step) : interval - workingTime;
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
        if (showTimer) {
            overlay.setText3(String.format("%02d:%02d:%02d",
                    duration.toHoursPart(), duration.toMinutesPart(), duration.toSecondsPart()));
        }
    }
}
